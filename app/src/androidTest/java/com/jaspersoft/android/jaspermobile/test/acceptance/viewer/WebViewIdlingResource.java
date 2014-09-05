/*
* Copyright (C) 2012 Jaspersoft Corporation. All rights reserved.
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
* <http://www.gnu.org/licenses/lgpl>.
*/

package com.jaspersoft.android.jaspermobile.test.acceptance.viewer;

import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.jaspersoft.android.jaspermobile.test.utils.espresso.ActivityLifecycleIdlingResource;
import com.jaspersoft.android.jaspermobile.widget.JSWebView;

import static com.google.android.apps.common.testing.testrunner.util.Checks.checkNotNull;

public class WebViewIdlingResource extends WebChromeClient implements ActivityLifecycleIdlingResource<JSWebView> {

    private static final int FINISHED = 100;

    private JSWebView webView;
    private ResourceCallback callback;
    private WebChromeClient mInitialChromeClient;
    private boolean transitedToIdle;

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        Log.d("hoho", "onProgressChanged ---- " + newProgress);
        if (mInitialChromeClient != null) {
            mInitialChromeClient.onProgressChanged(view, newProgress);
        }
        if (newProgress == FINISHED) {
            finish();
        }
    }

    private void finish() {
        callback.onTransitionToIdle();
        webView.setWebChromeClient(null);
        Log.d("hoho", "finished ------------");
    }

    @Override
    public String getName() {
        return "WebView idling resource";
    }

    @Override
    public boolean isIdleNow() {
        boolean isIdle = false;
        // The webView hasn't been injected yet, so we're idling
        if (webView == null) {
            isIdle = true;
        }
        if (webView != null && webView.getProgress() == FINISHED) {
            isIdle = true;
        }
        Log.d("hohoho", "==============> " + isIdle);
        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        Log.d("hohoho", "received callback" + resourceCallback);
        this.callback = resourceCallback;
    }

    @Override
    public void inject(JSWebView activityComponent) {
        this.webView = checkNotNull(activityComponent,
                String.format("Trying to instantiate a \'%s\' with a null WebView", getName()));
        // Shall we save the original client? Atm it's not used though.
        mInitialChromeClient = webView.getWebChromeCient();
        webView.setWebChromeClient(this);
    }

    @Override
    public void clear() {
        webView = null;
    }
}