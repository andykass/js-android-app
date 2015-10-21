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

package com.jaspersoft.android.jaspermobile.test.acceptance.viewer;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.jaspersoft.android.jaspermobile.test.utils.espresso.ActivityLifecycleIdlingResource;
import com.jaspersoft.android.jaspermobile.widget.JSWebView;

public class WebViewIdlingResource extends WebChromeClient implements ActivityLifecycleIdlingResource<JSWebView> {

    private static final int FINISHED = 100;

    private JSWebView webView;
    private ResourceCallback callback;
    private WebChromeClient mInitialChromeClient;

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (newProgress == FINISHED && callback != null) {
            callback.onTransitionToIdle();
        }
        if (mInitialChromeClient != null) {
            mInitialChromeClient.onProgressChanged(view, newProgress);
        }
    }

    @Override
    public String getName() {
        return "WebView idling resource";
    }

    @Override
    public boolean isIdleNow() {
        // The webView hasn't been injected yet, so we're idling
        if (webView == null) return true;
        return webView.getProgress() == FINISHED && callback != null;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.callback = resourceCallback;
    }

    @Override
    public void inject(JSWebView activityComponent) {
        if (activityComponent == null) {
            throw new IllegalArgumentException(String.format("Trying to instantiate a \'%s\' with a null WebView", getName()));
        }
        this.webView = activityComponent;
        // Shall we save the original client? Atm it's not used though.
        mInitialChromeClient = webView.getWebChromeCient();
        webView.setWebChromeClient(this);
    }

    @Override
    public void clear() {
        webView = null;
    }
}