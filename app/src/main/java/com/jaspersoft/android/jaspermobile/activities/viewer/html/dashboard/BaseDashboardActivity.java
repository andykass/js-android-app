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

package com.jaspersoft.android.jaspermobile.activities.viewer.html.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.GraphObject;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.share.AnnotationActivity_;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.SimpleDialogFragment;
import com.jaspersoft.android.jaspermobile.domain.ErrorSubscriber;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.ScreenCapture;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.interactor.profile.AuthorizeSessionUseCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.SaveScreenCaptureCase;
import com.jaspersoft.android.jaspermobile.internal.di.components.DashboardActivityComponent;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.DashboardModule;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.presentation.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.util.FavoritesHelper;
import com.jaspersoft.android.jaspermobile.util.ScrollableTitleHelper;
import com.jaspersoft.android.jaspermobile.util.print.ResourcePrintJob;
import com.jaspersoft.android.jaspermobile.webview.DefaultUrlPolicy;
import com.jaspersoft.android.jaspermobile.webview.JasperChromeClientListenerImpl;
import com.jaspersoft.android.jaspermobile.webview.JasperWebViewClientListener;
import com.jaspersoft.android.jaspermobile.webview.SystemChromeClient;
import com.jaspersoft.android.jaspermobile.webview.SystemWebViewClient;
import com.jaspersoft.android.jaspermobile.webview.UrlPolicy;
import com.jaspersoft.android.jaspermobile.webview.WebViewEnvironment;
import com.jaspersoft.android.jaspermobile.webview.dashboard.InjectionRequestInterceptor;
import com.jaspersoft.android.jaspermobile.webview.dashboard.script.ScriptTagFactory;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;

import java.io.File;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Activity that performs dashboard viewing in HTML format through native component.
 *
 * @author Tom Koptel
 * @since 2.0
 */
@OptionsMenu(R.menu.dashboard_menu)
@EActivity
public abstract class BaseDashboardActivity extends ToolbarActivity
        implements JasperWebViewClientListener, DefaultUrlPolicy.SessionListener {

    @Extra
    protected ResourceLookup resource;

    protected WebView webView;
    protected TextView emptyView;
    protected ProgressBar progressBar;

    private JasperChromeClientListenerImpl chromeClientListener;

    @OptionsMenuItem(R.id.favoriteAction)
    protected MenuItem favoriteActionButton;

    @Bean
    protected ScrollableTitleHelper scrollableTitleHelper;

    @Inject
    Analytics analytics;
    @Inject
    ResourcePrintJob mResourcePrintJob;
    @Inject
    JasperServer mServer;
    @Inject
    ScriptTagFactory mScriptTagFactory;
    @Inject
    FavoritesHelper favoritesHelper;
    @Inject
    AuthorizeSessionUseCase mAuthorizeSessionUseCase;
    @Inject
    SaveScreenCaptureCase mSaveScreenCaptureCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_viewer);

        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        emptyView = (TextView) findViewById(android.R.id.empty);

        getComponent().inject(this);
        initWebView();
        scrollableTitleHelper.injectTitle(resource.getLabel());
    }

    public DashboardActivityComponent getComponent() {
        return GraphObject.Factory.from(this)
                .getProfileComponent()
                .plusDashboardPage(
                        new ActivityModule(this),
                        new DashboardModule(webView, String.valueOf(resource.getResourceType()))
                );
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        favoritesHelper.updateFavoriteIconState(favoriteActionButton, resource.getUri());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.removeAllViews();
            webView.destroy();
        }
    }

    @OptionsItem(R.id.favoriteAction)
    protected void favoriteAction() {
        favoritesHelper.switchFavoriteState(resource, favoriteActionButton);
    }

    @OptionsItem(R.id.aboutAction)
    protected void aboutAction() {
        SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
                .setTitle(resource.getLabel())
                .setMessage(resource.getDescription())
                .setNegativeButtonText(R.string.ok)
                .show();
    }

    @OptionsItem(R.id.printAction)
    protected void printAction() {
        Bundle args = new Bundle();
        args.putString(ResourcePrintJob.PRINT_NAME_KEY, resource.getLabel());

        mResourcePrintJob.printResource(args);
    }

    @OptionsItem(R.id.shareAction)
    protected void shareAction() {
        ScreenCapture screenCapture = ScreenCapture.Factory.capture(webView);

        mSaveScreenCaptureCase.execute(screenCapture, new SimpleSubscriber<File>() {
            @Override
            public void onStart() {
                showLoading();
            }

            @Override
            public void onError(Throwable e) {
                RequestExceptionHandler.showAuthErrorIfExists(BaseDashboardActivity.this, e);
            }

            @Override
            public void onNext(File item) {
                hideLoading();
                navigateToAnnotationPage(item);
            }
        });
    }

    //---------------------------------------------------------------------
    // Protected Util method
    //---------------------------------------------------------------------

    protected void resetZoom() {
        while (webView.zoomOut()) ;
    }

    protected void showMessage(CharSequence message) {
        if (!TextUtils.isEmpty(message) && emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(message);
        }
    }

    //---------------------------------------------------------------------
    // JasperWebViewClientListener callbacks
    //---------------------------------------------------------------------

    @Override
    public void onPageStarted(String newUrl) {
    }

    @Override
    public void onReceivedError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onPageFinishedLoading(String url) {
        onPageFinished();
    }

    //---------------------------------------------------------------------
    // DefaultUrlPolicy.SessionListener callback
    //---------------------------------------------------------------------

    @Override
    public void onSessionExpired() {
        mAuthorizeSessionUseCase.execute(new GenericSubscriber<>(new SimpleSubscriber<Void>() {
            @Override
            public void onStart() {
                Toast.makeText(BaseDashboardActivity.this, R.string.da_session_expired, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompleted() {
                onSessionRefreshed();
            }

            @Override
            public void onError(Throwable e) {
                RequestExceptionHandler.showAuthErrorIfExists(BaseDashboardActivity.this, e);
            }
        }));
    }

    protected void showLoading() {
        ProgressDialogFragment.builder(getSupportFragmentManager())
                .setLoadingMessage(R.string.da_loading)
                .show();
    }

    protected void hideLoading() {
        ProgressDialogFragment.dismiss(getSupportFragmentManager());
    }

    protected void showWebView(boolean visibility) {
        webView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    //---------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------

    public abstract void onWebViewConfigured(WebView webView);

    public abstract void onPageFinished();

    @OptionsItem(R.id.refreshAction)
    protected abstract void onRefresh();

    @OptionsItem(android.R.id.home)
    protected abstract void onHomeAsUpCalled();

    public abstract void onSessionRefreshed();

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void initWebView() {
        chromeClientListener = new JasperChromeClientListenerImpl(progressBar);

        UrlPolicy defaultPolicy = new DefaultUrlPolicy(mServer.getBaseUrl())
                .withSessionListener(this);

        SystemChromeClient systemChromeClient = new SystemChromeClient.Builder(this)
                .withDelegateListener(chromeClientListener)
                .build();
        SystemWebViewClient systemWebViewClient = new SystemWebViewClient.Builder()
                .withDelegateListener(this)
                .registerInterceptor(new InjectionRequestInterceptor())
                .registerUrlPolicy(defaultPolicy)
                .build();

        WebViewEnvironment.configure(webView)
                .withDefaultSettings()
                .withChromeClient(systemChromeClient)
                .withWebClient(systemWebViewClient);
        onWebViewConfigured(webView);
    }

    private void navigateToAnnotationPage(File file) {
        AnnotationActivity_.intent(this)
                .imageUri(Uri.fromFile(file))
                .start();
    }

    protected final class GenericSubscriber<R> extends ErrorSubscriber<R> {
        protected GenericSubscriber(SimpleSubscriber<R> delegate) {
            super(delegate);
        }

        @Override
        public void onStart() {
            showLoading();
            super.onStart();
        }

        @Override
        public void onCompleted() {
            hideLoading();
            super.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "Dashboard thrown error");
            hideLoading();
            super.onError(e);
        }
    }
}
