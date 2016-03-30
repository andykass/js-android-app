package com.jaspersoft.android.jaspermobile.ui.presenter;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.data.ComponentManager;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.domain.ProfileMetadata;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.interactor.profile.ActiveProfileRemoveUseCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.profile.GetMetadataForProfileUseCase;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.ui.contract.StartupContract;
import com.jaspersoft.android.jaspermobile.ui.navigation.Navigator;
import com.jaspersoft.android.jaspermobile.ui.navigation.Page;
import com.jaspersoft.android.jaspermobile.ui.navigation.PageFactory;
import com.jaspersoft.android.jaspermobile.ui.page.BasePageState;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
public class StartupPresenter extends LegacyPresenter<StartupContract.View> implements StartupContract.ActionListener {
    private final Analytics mAnalytics;
    private final ComponentManager mComponentManager;
    private final PageFactory mPageFactory;
    private final Navigator mNavigator;
    private final GetMetadataForProfileUseCase mGetMetadataForProfileUseCase;
    private final ActiveProfileRemoveUseCase mActiveProfileRemoveUseCase;

    @Inject
    public StartupPresenter(
            Analytics analytics,
            ComponentManager componentManager,
            PageFactory pageFactory,
            Navigator navigator,
            GetMetadataForProfileUseCase getMetadataForProfileUseCase,
            ActiveProfileRemoveUseCase activeProfileRemoveUseCase
    ) {
        mAnalytics = analytics;
        mComponentManager = componentManager;
        mPageFactory = pageFactory;
        mNavigator = navigator;
        mGetMetadataForProfileUseCase = getMetadataForProfileUseCase;
        mActiveProfileRemoveUseCase = activeProfileRemoveUseCase;
    }

    @Override
    public void resume() {
        StartupContract.View view = getView();
        BasePageState state = view.getState();
        if (state.shouldExit()) {
            navigateToMainPage();
        }
    }

    @Override
    public void destroy() {
        mActiveProfileRemoveUseCase.unsubscribe();
    }

    @Override
    public void setupNewProfile(Profile profile) {
        mComponentManager.setupActiveProfile(profile);
        navigateToMainPage();
    }

    @Override
    public void tryToSetupProfile(int signUpRequestCode) {
        Profile profile = mComponentManager.setupProfileComponent();
        if (profile.isEmpty()) {
            navigateToSignUp(signUpRequestCode);
        } else {
            logAnalyticsEvent(profile);
            listenForActiveProfileRemoved();
        }
    }

    private void navigateToSignUp(int signUpRequestCode) {
        Page authPage = mPageFactory.createSignUpPage();
        mNavigator.navigateForResult(authPage, signUpRequestCode);
    }

    private void logAnalyticsEvent(Profile profile) {
        ProfileMetadata metadata = mGetMetadataForProfileUseCase.execute(profile);
        JasperServer server = metadata.getServer();

        String serverEdition = server.getEdition();
        String version = server.getVersion();
        if (metadata.isDemo()) {
            serverEdition = "DEMO";
        }

        mAnalytics.setServerInfo(version, serverEdition);
    }

    private void listenForActiveProfileRemoved() {
        mActiveProfileRemoveUseCase.execute(new SimpleSubscriber<Boolean>() {
            @Override
            public void onError(Throwable e) {
                Timber.e(e, "ActiveProfileRemoveUseCase# failed");
            }

            @Override
            public void onNext(Boolean removed) {
                BasePageState state = getView().getState();
                state.setShouldExit(removed);
            }
        });
    }

    private void navigateToMainPage() {
        Page mainPage = mPageFactory.createMainPage();
        mNavigator.navigate(mainPage, true);
    }
}
