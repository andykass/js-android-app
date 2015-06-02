/*
 * Copyright © 2014 TIBCO Software, Inc. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.webview.dashboard.bridge;

import android.annotation.SuppressLint;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.jaspersoft.android.jaspermobile.webview.WebInterface;

/**
 * @author Tom Koptel
 * @since 2.0
 */
public final class DashboardWebInterface extends WebInterface implements DashboardCallback {
    private final DashboardCallback delegate;

    private DashboardWebInterface(DashboardCallback dashboardCallback) {
        this.delegate = dashboardCallback;
    }

    public static WebInterface from(DashboardCallback dashboardCallback) {
        return new DashboardWebInterface(dashboardCallback);
    }

    @JavascriptInterface
    @Override
    public void onMaximizeStart(final String title) {
        handleCallback(new Runnable() {
            @Override
            public void run() {
                delegate.onMaximizeStart(title);
            }
        });
    }

    @JavascriptInterface
    @Override
    public void onMaximizeEnd(final String title) {
        handleCallback(new Runnable() {
            @Override
            public void run() {
                delegate.onMaximizeEnd(title);
            }
        });
    }

    @JavascriptInterface
    @Override
    public void onMaximizeFailed(final String error) {
        handleCallback(new Runnable() {
            @Override
            public void run() {
                delegate.onMaximizeFailed(error);
            }
        });
    }

    @JavascriptInterface
    @Override
    public void onMinimizeStart() {
        handleCallback(new Runnable() {
            @Override
            public void run() {
                delegate.onMinimizeStart();
            }
        });
    }

    @JavascriptInterface
    @Override
    public void onMinimizeEnd() {
        handleCallback(new Runnable() {
            @Override
            public void run() {
                delegate.onMinimizeEnd();
            }
        });
    }

    @JavascriptInterface
    @Override
    public void onMinimizeFailed(final String error) {
        handleCallback(new Runnable() {
            @Override
            public void run() {
                delegate.onMinimizeFailed(error);
            }
        });
    }

    @JavascriptInterface
    @Override
    public void onScriptLoaded() {
        handleCallback(new Runnable() {
            @Override
            public void run() {
                delegate.onScriptLoaded();
            }
        });
    }

    @JavascriptInterface
    @Override
    public void onLoadStart() {
        handleCallback(new Runnable() {
            @Override
            public void run() {
                delegate.onLoadStart();
            }
        });
    }

    @JavascriptInterface
    @Override
    public void onLoadDone() {
        handleCallback(new Runnable() {
            @Override
            public void run() {
                delegate.onLoadDone();
            }
        });
    }

    @JavascriptInterface
    @Override
    public void onLoadError(final String error) {
        handleCallback(new Runnable() {
            @Override
            public void run() {
                delegate.onLoadError(error);
            }
        });
    }

    @JavascriptInterface
    @Override
    public void onReportExecution(final String data) {
        handleCallback(new Runnable() {
            @Override
            public void run() {
                delegate.onReportExecution(data);
            }
        });
    }

    @JavascriptInterface
    @Override
    public void onWindowResizeStart() {
        handleCallback(new Runnable() {
            @Override
            public void run() {
                delegate.onWindowResizeStart();
            }
        });
    }

    @JavascriptInterface
    @Override
    public void onWindowResizeEnd() {
        handleCallback(new Runnable() {
            @Override
            public void run() {
                delegate.onWindowResizeEnd();
            }
        });
    }

    @JavascriptInterface
    @Override
    public void onAuthError(final String message) {
        handleCallback(new Runnable() {
            @Override
            public void run() {
                delegate.onAuthError(message);
            }
        });
    }

    @SuppressLint("AddJavascriptInterface")
    @Override
    public void exposeJavascriptInterface(WebView webView) {
        webView.addJavascriptInterface(this, "Android");
    }
}
