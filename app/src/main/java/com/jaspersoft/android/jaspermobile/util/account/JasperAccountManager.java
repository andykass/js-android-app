/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from TIBCO Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of TIBCO Jaspersoft Mobile for Android.
 *
 * TIBCO Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TIBCO Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TIBCO Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.util.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.OnAccountsUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.jaspersoft.android.jaspermobile.JasperMobileApplication;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.navigation.NavigationActivity_;
import com.jaspersoft.android.jaspermobile.util.security.PasswordManager;
import com.jaspersoft.android.jaspermobile.util.JasperSettings;

import org.roboguice.shaded.goole.common.collect.Lists;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * TODO provide unit tests
 *
 * @author Tom Koptel
 * @since 2.0
 */
public class JasperAccountManager {
    private static final String PREF_NAME = JasperAccountManager.class.getSimpleName();
    private static final String ACCOUNT_NAME_KEY = "ACCOUNT_NAME_KEY";

    private final Context mContext;
    private final SharedPreferences mPreference;
    private final AccountManager mDelegateManager;

    public static JasperAccountManager get(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context should not be null");
        }
        return new JasperAccountManager(context);
    }

    private JasperAccountManager(Context context) {
        mContext = context;
        mDelegateManager = AccountManager.get(context);
        mPreference = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        Timber.tag(PREF_NAME);
    }

    public void setOnAccountsUpdatedListener(OnAccountsUpdateListener listener) {
        mDelegateManager.addOnAccountsUpdatedListener(listener, null, false);
    }

    public void removeOnAccountsUpdatedListener(OnAccountsUpdateListener listener) {
        mDelegateManager.removeOnAccountsUpdatedListener(listener);
    }

    public AccountServerData getActiveServerData() throws TokenException {
        Account activeAccount = getActiveAccount();
        return getServerData(activeAccount);
    }

    public Observable<AccountServerData> getAsyncActiveServerData() {
        return Observable.create(new Observable.OnSubscribe<AccountServerData>() {
            @Override
            public void call(Subscriber<? super AccountServerData> subscriber) {
                AccountServerData serverData;
                try {
                    Account activeAccount = getActiveAccount();
                    serverData = getServerData(activeAccount);

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(serverData);
                        subscriber.onCompleted();
                    }
                } catch (TokenException e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }

    public Account getActiveAccount() {
        String accountName = mPreference.getString(ACCOUNT_NAME_KEY, "");
        if (TextUtils.isEmpty(accountName)) {
            return null;
        }
        return new Account(accountName, JasperSettings.JASPER_ACCOUNT_TYPE);
    }

    public boolean isActiveAccountRegistered(){
        Account account = getActiveAccount();
        Account[] accounts = getAccounts();
        boolean activeAccountExists = Lists.newArrayList(accounts).contains(account);
        return activeAccountExists;
    }

    public void activateAccount(Account account) {
        String tokenToInvalidate = mDelegateManager.peekAuthToken(account, JasperSettings.JASPER_AUTH_TOKEN_TYPE);
        invalidateToken(tokenToInvalidate);
        mPreference.edit().putString(ACCOUNT_NAME_KEY, account.name).apply();

        syncJsRestClient();
    }

    public void activateFirstAccount() {
        Account account = getAccounts()[0];
        String tokenToInvalidate = mDelegateManager.peekAuthToken(account, JasperSettings.JASPER_AUTH_TOKEN_TYPE);
        invalidateToken(tokenToInvalidate);
        mPreference.edit().putString(ACCOUNT_NAME_KEY, account.name).apply();

        syncJsRestClient();
    }

    public void deactivateAccount() {
        mPreference.edit().putString(ACCOUNT_NAME_KEY, "").apply();
    }

    public void updateActiveAccountPassword(String newPassword) {
        invalidateActiveToken();
        updateAccountPassword(getActiveAccount(), newPassword);
    }

    public void updateAccountPassword(Account account , String newPassword) {
        String encrypted = encryptPassword(newPassword);
        mDelegateManager.setPassword(account, encrypted);
    }

    public Account[] getAccounts() {
        Account[] accounts = mDelegateManager.getAccountsByType(JasperSettings.JASPER_ACCOUNT_TYPE);
        Timber.d(Arrays.toString(accounts));
        return accounts;
    }

    public List<AccountServerData> getInactiveAccountsData() {
        Account activeAccount = getActiveAccount();
        final String activeName = (activeAccount == null) ? "" : activeAccount.name;

        return Observable.from(getAccounts())
                .filter(new Func1<Account, Boolean>() {
                    @Override
                    public Boolean call(Account account) {
                        return !activeName.equals(account.name);
                    }
                }).map(new Func1<Account, AccountServerData>() {
                    @Override
                    public AccountServerData call(Account account) {
                        return AccountServerData.get(mContext, account);
                    }
                }).toList().toBlocking().first();
    }

    public Observable<Account> addAccountExplicitly(final AccountServerData serverData) {
        return Observable.create(new Observable.OnSubscribe<Account>() {
            @Override
            public void call(Subscriber<? super Account> subscriber) {
                try {
                    Account account = new Account(serverData.getAlias(),
                            JasperSettings.JASPER_ACCOUNT_TYPE);

                    String encrypted = encryptPassword(serverData.getPassword());
                    mDelegateManager.addAccountExplicitly(account,
                            encrypted, null);
                    setUserData(account, serverData);
                    
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(account);
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }

    public String getActiveAuthToken() throws TokenException {
        Account activeAccount = getActiveAccount();
        return getAuthToken(activeAccount);
    }

    public void invalidateActiveToken() {
        String tokenToInvalidate = mDelegateManager.peekAuthToken(getActiveAccount(), JasperSettings.JASPER_AUTH_TOKEN_TYPE);
        invalidateToken(tokenToInvalidate);
    }

    public void invalidateToken(String token) {
        mDelegateManager.invalidateAuthToken(JasperSettings.JASPER_ACCOUNT_TYPE, token);
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    /**
     * Retrieves token from {@link android.accounts.AccountManager} for specified {@link android.accounts.Account}.
     *
     * @param account which represents both JRS and user data configuration for more details refer to {@link AccountServerData}
     * @return token which in our case is cookie string for specified account. Can be <b>null</b> or empty if token is missing
     */
    private String getAuthToken(final Account account) throws TokenException {
        if (account == null)
            throw new TokenException("No accounts", TokenException.NO_ACCOUNTS_ERROR);

        Bundle tokenOutput;
        try {
            AccountManagerFuture<Bundle> future = mDelegateManager.getAuthToken(account,
                    JasperSettings.JASPER_AUTH_TOKEN_TYPE, null, false, null, null);
            tokenOutput = future.getResult();
        } catch (Exception ex) {
            Timber.e(ex, "Failed to getAuthToken()");
            Bundle output = new Bundle();
            output.putString(AccountManager.KEY_ERROR_MESSAGE, ex.getLocalizedMessage());
            output.putInt(AccountManager.KEY_ERROR_CODE, TokenException.OBTAIN_TOKEN_ERROR);
            throw new TokenException(output);
        }

        if (tokenOutput.containsKey(AccountManager.KEY_ERROR_MESSAGE)) {
            int errorCode = tokenOutput.getInt(AccountManager.KEY_ERROR_CODE);
            if (errorCode == TokenException.SERVER_UPDATED_ERROR) {
                int flags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK;
                NavigationActivity_.intent(mContext).flags(flags).start();
            }

            throw new TokenException(tokenOutput);
        }

        return tokenOutput.getString(AccountManager.KEY_AUTHTOKEN);
    }

    private AccountServerData getServerData(Account account) throws TokenException {
        String token = getAuthToken(account);
        AccountServerData accountServerData = AccountServerData.get(mContext, account);
        accountServerData.setServerCookie(token);
        return accountServerData;
    }

    private void syncJsRestClient() {
        if (mContext.getApplicationContext() instanceof JasperMobileApplication) {
            JasperMobileApplication app = ((JasperMobileApplication) mContext.getApplicationContext());
            app.initLegacyJsRestClient();
        }
    }

    /**
     * Due to bug in AccountManager this is the only way to set account user data
     * @param account for adding data
     * @param serverData data
     */
    private void setUserData(Account account, AccountServerData serverData){
        AccountManager accountManager = AccountManager.get(mContext);
        accountManager.setUserData(account, AccountServerData.ALIAS_KEY, serverData.getAlias());
        accountManager.setUserData(account, AccountServerData.SERVER_URL_KEY, serverData.getServerUrl());
        accountManager.setUserData(account, AccountServerData.ORGANIZATION_KEY, serverData.getOrganization());
        accountManager.setUserData(account, AccountServerData.USERNAME_KEY, serverData.getUsername());
        accountManager.setUserData(account, AccountServerData.EDITION_KEY, serverData.getEdition());
        accountManager.setUserData(account, AccountServerData.VERSION_NAME_KEY, serverData.getVersionName());
    }

    private String encryptPassword(String newPassword) {
        String salt = mContext.getResources().getString(R.string.password_salt_key);
        PasswordManager passwordManager = PasswordManager.init(mContext, salt);
        try {
            return passwordManager.encrypt(newPassword);
        } catch (PasswordManager.EncryptionException encryptionException) {
            throw new RuntimeException(encryptionException);
        }
    }

    public String getPassword(Account account) {
        return mDelegateManager.getPassword(account);
    }

    //---------------------------------------------------------------------
    // Nested classes
    //---------------------------------------------------------------------

    public static class TokenException extends IOException {
        public static final int OBTAIN_TOKEN_ERROR = 15;
        public static final int SERVER_NOT_FOUND = 16;
        public static final int SERVER_UPDATED_ERROR = 17;
        public static final int INCORRECT_SERVER_VERSION_ERROR = 18;
        public static final int NO_ACCOUNTS_ERROR = 19;
        public static final int NO_PASSWORD_ERROR = 20;

        private final int mErrorCode;

        public TokenException(Bundle output) {
            super(output.getString(AccountManager.KEY_ERROR_MESSAGE));
            mErrorCode = output.getInt(AccountManager.KEY_ERROR_CODE);
        }

        public TokenException(String message, int errorCode) {
            super(message);
            mErrorCode = errorCode;
        }

        public int getErrorCode() {
            return mErrorCode;
        }
    }
}
