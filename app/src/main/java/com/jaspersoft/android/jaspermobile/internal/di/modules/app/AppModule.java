package com.jaspersoft.android.jaspermobile.internal.di.modules.app;

import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.AppConfigurator;
import com.jaspersoft.android.jaspermobile.AppConfiguratorImpl;
import com.jaspersoft.android.jaspermobile.BackgroundThread;
import com.jaspersoft.android.jaspermobile.GraphObject;
import com.jaspersoft.android.jaspermobile.JasperAnalytics;
import com.jaspersoft.android.jaspermobile.JasperSecurityProviderUpdater;
import com.jaspersoft.android.jaspermobile.UIThread;
import com.jaspersoft.android.jaspermobile.activities.SecurityProviderUpdater;
import com.jaspersoft.android.jaspermobile.data.ComponentManager;
import com.jaspersoft.android.jaspermobile.data.cache.SecureCache;
import com.jaspersoft.android.jaspermobile.data.cache.SecureStorage;
import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.executor.PreExecutionThread;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.network.cookie.CookieHandlerFactory;
import com.jaspersoft.android.jaspermobile.ui.component.ProfileActivationListener;
import com.jaspersoft.android.jaspermobile.util.DefaultPrefHelper_;
import com.jaspersoft.android.sdk.network.Server;

import java.net.CookieHandler;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@Module
public final class AppModule {
    private final Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    @ApplicationContext
    Context provideApplicationContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    CookieHandler provideCookieHandler(CookieHandlerFactory cookieHandlerFactory, @ApplicationContext Context context) {
        return cookieHandlerFactory.newStore(context);
    }

    @Provides
    @Singleton
    PostExecutionThread providePostExecutionThread(UIThread uiThread) {
        return uiThread;
    }

    @Provides
    @Singleton
    PreExecutionThread providePreExecutionThread(BackgroundThread backgroundThread) {
        return backgroundThread;
    }

    @Provides
    @Singleton
    AccountManager providesAccountManager(@ApplicationContext Context context) {
        return AccountManager.get(context);
    }

    @Provides
    @Singleton
    Analytics providesAnalytics(@ApplicationContext Context context) {
        return new JasperAnalytics(context);
    }

    @Provides
    @Singleton
    SecurityProviderUpdater providesSecurityProviderUpdater() {
        return new JasperSecurityProviderUpdater();
    }

    @Provides
    @Singleton
    AppConfigurator providesAppConfigurator() {
        return new AppConfiguratorImpl();
    }

    @Provides
    @Singleton
    SecureCache provideSecureStorage(SecureStorage secureStorage) {
        return secureStorage;
    }

    @Provides
    Server.Builder provideServerBuilder() {
        DefaultPrefHelper_ helper = DefaultPrefHelper_.getInstance_(mApplication);
        return Server.builder()
                .withConnectionTimeOut(helper.getConnectTimeoutValue(), TimeUnit.MILLISECONDS)
                .withReadTimeout(helper.getReadTimeoutValue(), TimeUnit.MILLISECONDS);
    }

    @Provides
    GraphObject providesGraphObject() {
        return GraphObject.Factory.from(mApplication);
    }

    @Singleton
    @Provides
    ComponentManager.Callback providesComponentCallback(ProfileActivationListener activationListener) {
        return activationListener;
    }
}
