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

package com.jaspersoft.android.jaspermobile.legacy;

import android.accounts.Account;
import android.content.Context;

import com.jaspersoft.android.jaspermobile.util.account.AccountServerData;
import com.jaspersoft.android.jaspermobile.util.account.JasperAccountManager;
import com.jaspersoft.android.sdk.client.JsServerProfile;

/**
 * Util class which simplifies migration from legacy code to new SDK paradigms.
 *
 * @author Tom Koptel
 * @since 2.0
 */
public class JsServerProfileCompat {

    public static void initLegacyJsRestClient(Context context, JsRestClientWrapper jsRestClient) {
        JasperAccountManager jasperAccountManager = JasperAccountManager.get(context);
        if (jasperAccountManager.isActiveAccountRegistered()) {
            Account account = jasperAccountManager.getActiveAccount();
            jsRestClient.updateServerProfile(getServerProfile(AccountServerData.get(context, account)));
        }
    }

    public static JsServerProfile getServerProfile(AccountServerData serverData) {
        JsServerProfile profile = new JsServerProfile();
        profile.setAlias(serverData.getAlias());
        profile.setServerUrl(serverData.getServerUrl());
        profile.setOrganization(serverData.getOrganization());
        profile.setUsername(serverData.getUsername());
        profile.setPassword(serverData.getPassword());
        profile.setVersionCode(serverData.getVersionName());
        profile.setServerEdition(serverData.getEdition());
        return profile;
    }

}
