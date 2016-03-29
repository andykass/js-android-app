package com.jaspersoft.android.jaspermobile.internal.di.modules.activity;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.webkit.WebView;

import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.ui.contract.VisualizeReportContract;
import com.jaspersoft.android.jaspermobile.ui.model.visualize.VisualizeComponent;
import com.jaspersoft.android.jaspermobile.ui.model.visualize.VisualizeViewModel;
import com.jaspersoft.android.jaspermobile.ui.model.visualize.WebViewConfiguration;
import com.jaspersoft.android.jaspermobile.ui.presenter.ReportVisualizePresenter;
import com.jaspersoft.android.jaspermobile.webview.SystemChromeClient;
import com.jaspersoft.android.jaspermobile.webview.SystemWebViewClient;
import com.jaspersoft.android.jaspermobile.webview.WebViewEnvironment;
import com.jaspersoft.android.jaspermobile.webview.dashboard.InjectionRequestInterceptor;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@Module
public final class ReportVisualizeViewerModule extends ReportModule {
    private final WebView mWebView;

    public ReportVisualizeViewerModule(String reportUri, WebView webView) {
        super(reportUri);
        mWebView = webView;
    }

    @Provides
    @PerActivity
    @Named("screen_diagonal")
    Double providesScreenDiagonal(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        float widthDpi = metrics.xdpi;
        float heightDpi = metrics.ydpi;

        float widthInches = widthPixels / widthDpi;
        float heightInches = heightPixels / heightDpi;

        return Math.sqrt(
                (widthInches * widthInches)
                        + (heightInches * heightInches));
    }

    @Provides
    @PerActivity
    VisualizeViewModel provideVisualizeViewModel(JasperServer server) {
        SystemChromeClient defaultChromeClient = new SystemChromeClient.Builder(mWebView.getContext())
                .build();

        SystemWebViewClient defaultWebViewClient = new SystemWebViewClient.Builder()
                .registerInterceptor(new InjectionRequestInterceptor())
                .build();

        WebViewEnvironment.configure(mWebView)
                .withDefaultSettings()
                .withChromeClient(defaultChromeClient)
                .withWebClient(defaultWebViewClient);

        WebViewConfiguration configuration = new WebViewConfiguration(mWebView, server.getBaseUrl());
        configuration.setSystemChromeClient(defaultChromeClient);
        configuration.setSystemWebViewClient(defaultWebViewClient);
        return VisualizeViewModel.newModel(configuration);
    }

    @Provides
    @PerActivity
    VisualizeComponent provideVisualizeComponent(VisualizeViewModel component) {
        return component;
    }

    @Provides
    @PerActivity
    VisualizeReportContract.Action provideReportActionListener(ReportVisualizePresenter presenter) {
        return presenter;
    }
}
