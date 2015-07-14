/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Mobile for Android.
 *
 * Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>./
 */

package com.jaspersoft.android.jaspermobile.db.seed;

import android.accounts.Account;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaspersoft.android.jaspermobile.BuildConfig;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.util.account.AccountServerData;
import com.jaspersoft.android.jaspermobile.util.account.JasperAccountManager;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author Tom Koptel
 * @since 2.0
 */
public class AccountSeed implements Seed {
    private final JasperAccountManager jasperAccountManager;
    private final Context mContext;
    private final Action1<Throwable> emptyError = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
        }
    };

    private AccountSeed(Context context) {
        mContext = context;
        jasperAccountManager = JasperAccountManager.get(context);
        Timber.tag(AccountSeed.class.getSimpleName());
    }

    public static void seed(Context context) {
        boolean noAccounts = JasperAccountManager.get(context).getAccounts().length == 0;
        boolean buildForQa = (BuildConfig.DEBUG || BuildConfig.FLAVOR.equalsIgnoreCase("dev"));
        if (noAccounts && buildForQa) {
            AccountSeed accountSeed = new AccountSeed(context);
            accountSeed.seed();
        }
    }

    @Override
    public void seed() {
        populateDefaultServer();
        populateTestServers();
    }

    private void populateDefaultServer() {
        Timber.d("Populating default server");
        AccountServerData serverData = new AccountServerData()
                .setAlias(AccountServerData.Demo.ALIAS)
                .setServerUrl(AccountServerData.Demo.SERVER_URL)
                .setOrganization(AccountServerData.Demo.ORGANIZATION)
                .setUsername(AccountServerData.Demo.USERNAME)
                .setPassword(AccountServerData.Demo.PASSWORD)
                .setEdition("PRO")
                .setVersionName("6.0.1");
        jasperAccountManager
                .addAccountExplicitly(serverData)
                .subscribeOn(Schedulers.io())
                .subscribe(Actions.empty(), emptyError);
    }

    private void populateTestServers() {
        InputStream is = mContext.getResources().openRawResource(R.raw.profiles);

        // This is possible during unit testing
        // As soon as we don`t care about test data at that stage
        // we are simply ignoring step
        if (is == null) return;

        try {
            String json = IOUtils.toString(is);
            Gson gson = new Gson();

            Type listType = new TypeToken<List<AccountServerData>>() {
            }.getType();
            List<AccountServerData> datum = gson.fromJson(json, listType);
            Observable.from(datum).flatMap(new Func1<AccountServerData, Observable<Account>>() {
                @Override
                public Observable<Account> call(AccountServerData serverData) {
                    return jasperAccountManager.addAccountExplicitly(serverData);
                }
            }).subscribe(new Action1<Account>() {
                @Override
                public void call(Account account) {
                    Timber.d("Account was added " + account);
                }
            }, emptyError);
        } catch (IOException e) {
            Timber.w("Ignoring population of data");
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
