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

package com.jaspersoft.android.jaspermobile.network.cookie;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.TestOnly;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.3
 */
final class AppCookieStore implements CookieStorage {
    private final WebViewCookieStore mWebViewCookieStore;
    private final java.net.CookieStore mStore;
    private final org.apache.http.client.CookieStore mLegacyStore;

    @TestOnly
    public AppCookieStore(
            WebViewCookieStore webViewCookieStore,
            java.net.CookieStore persistentStore,
            org.apache.http.client.CookieStore legacyStore) {
        mWebViewCookieStore = webViewCookieStore;
        mStore = persistentStore;
        mLegacyStore = legacyStore;
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        mStore.add(uri, cookie);
        mWebViewCookieStore.add(uri.toString(), cookie.toString());
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return mStore.get(uri);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return mStore.getCookies();
    }

    @Override
    public List<URI> getURIs() {
        return mStore.getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        boolean result = mStore.remove(uri, cookie);
        mWebViewCookieStore.removeCookie(uri.toString());
        return result;
    }

    @Override
    public boolean removeAll() {
        mLegacyStore.clear();
        boolean result = mStore.removeAll();
        mWebViewCookieStore.removeAllCookies();
        return result;
    }

    @NonNull
    @Override
    public org.apache.http.client.CookieStore getApacheCookieStore() {
        return mLegacyStore;
    }
}
