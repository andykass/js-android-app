/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.data.validator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.domain.validator.ProfileValidator;
import com.jaspersoft.android.jaspermobile.domain.validator.exception.DuplicateProfileException;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
public final class ProfileValidatorImpl implements ProfileValidator {
    private final Context mContext;
    private final String mAccountType;
    private final Profile mProfile;

    @Inject
    public ProfileValidatorImpl(Context context,
                                @Named("accountType") String accountType,
                                Profile profile) {
        mContext = context;
        mAccountType = accountType;
        mProfile = profile;
    }

    @Override
    public Profile validate() throws DuplicateProfileException {
        AccountManager accountManager = AccountManager.get(mContext);
        Account[] accounts = accountManager.getAccountsByType(mAccountType);
        Set<Account> accountsSet = new HashSet<>(Arrays.asList(accounts));

        String profileName = mProfile.getKey();
        Account profileAccount = new Account(profileName, mAccountType);

        if (accountsSet.contains(profileAccount)) {
            String[] availableNames = getAvailableNames(accounts);
            throw new DuplicateProfileException(profileName, availableNames);
        }

        return mProfile;
    }

    private String[] getAvailableNames(Account[] accounts) {
        String[] names = new String[accounts.length];
        int count = names.length;
        for (int i = 0; i < count; i++) {
            names[i] = accounts[i].name;
        }
        return names;
    }
}
