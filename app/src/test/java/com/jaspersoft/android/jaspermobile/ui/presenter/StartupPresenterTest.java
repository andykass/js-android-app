package com.jaspersoft.android.jaspermobile.ui.presenter;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.data.ComponentManager;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.domain.ProfileMetadata;
import com.jaspersoft.android.jaspermobile.domain.interactor.profile.ActiveProfileRemoveUseCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.profile.GetMetadataForProfileUseCase;
import com.jaspersoft.android.jaspermobile.ui.navigation.FakePageFactory;
import com.jaspersoft.android.jaspermobile.ui.navigation.MainPage;
import com.jaspersoft.android.jaspermobile.ui.navigation.Navigator;
import com.jaspersoft.android.jaspermobile.ui.navigation.Page;
import com.jaspersoft.android.jaspermobile.ui.navigation.SignUpPage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import rx.Subscriber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class StartupPresenterTest {
    private static final Integer REQUEST_CODE = 100;

    @Mock
    Navigator mNavigator;

    @Mock
    Analytics mAnalytics;

    @Mock
    JasperServer mServer;
    @Mock
    ProfileMetadata mProfileMetadata;
    @Mock
    GetMetadataForProfileUseCase mGetMetadataForProfileUseCase;
    @Mock
    ActiveProfileRemoveUseCase mActiveProfileRemoveUseCase;
    @Mock
    ComponentManager mComponentManager;

    private FakePageFactory mFakePageFactory;
    private StartupPresenter mStartupPresenter;

    private final Profile activeProfile = Profile.create("active");
    private final Profile fakeProfile = Profile.getFake();

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        setupSpy();
        setupMocks();

        mStartupPresenter = new StartupPresenter(
                mAnalytics,
                mComponentManager,
                mFakePageFactory,
                mNavigator,
                mGetMetadataForProfileUseCase,
                mActiveProfileRemoveUseCase
        );
    }

    private void setupMocks() {
        when(mServer.getVersion()).thenReturn("6.0");
        when(mServer.getEdition()).thenReturn("PRO");
        when(mProfileMetadata.getServer()).thenReturn(mServer);
        when(mProfileMetadata.getProfile()).thenReturn(activeProfile);
    }

    private void setupSpy() {
        mFakePageFactory = spy(new FakePageFactory());
    }

    @Test
    public void should_navigate_to_main_page_if_has_active_profile() throws Exception {
        givenActiveProfile();
        givenMetadataForProfile();

        whenTriesToSetupProfile();

        thenSetupsProfileComponent();
        thenShouldLogServerToAnalytics();
        thenShouldSubscribeToProfileRemovedEvent();
    }

    @Test
    public void should_navigate_to_authentication_page_if_no_active_profile() throws Exception {
        givenNoActiveProfile();

        whenTriesToSetupProfile();

        thenSetupsProfileComponent();
        thenNavigatesToSignUpPage();
    }

    @Test
    public void should_create_use_active_profile() throws Exception {
        givenActiveProfile();

        whenSetupActiveProfile();

        thenActivatesProfile();
        thenNavigatesToMainPage();
    }

    private void givenMetadataForProfile() {
        when(mGetMetadataForProfileUseCase.execute(any(Profile.class))).thenReturn(mProfileMetadata);
    }

    private void thenShouldLogServerToAnalytics() {
        verify(mAnalytics).setServerInfo("6.0", "PRO");
    }

    private void givenActiveProfile() {
        when(mComponentManager.setupProfileComponent()).thenReturn(activeProfile);
    }

    private void givenNoActiveProfile() {
        when(mComponentManager.setupProfileComponent()).thenReturn(fakeProfile);
    }

    private void whenSetupActiveProfile() {
        mStartupPresenter.setupNewProfile(activeProfile);
    }

    private void whenTriesToSetupProfile() {
        mStartupPresenter.tryToSetupProfile(REQUEST_CODE);
    }

    private void thenActivatesProfile() {
        verify(mComponentManager).setupActiveProfile(activeProfile);
    }

    private void thenSetupsProfileComponent() {
        verify(mComponentManager).setupProfileComponent();
    }

    private void thenNavigatesToSignUpPage() {
        ArgumentCaptor<Page> argument = ArgumentCaptor.forClass(Page.class);
        verify(mNavigator).navigateForResult(argument.capture(), eq(REQUEST_CODE));
        assertThat(argument.getValue(), is(instanceOf(SignUpPage.class)));
    }

    private void thenNavigatesToMainPage() {
        ArgumentCaptor<Page> argument = ArgumentCaptor.forClass(Page.class);
        verify(mNavigator).navigate(argument.capture(), eq(true));
        assertThat(argument.getValue(), is(instanceOf(MainPage.class)));
    }

    private void thenShouldSubscribeToProfileRemovedEvent() {
        verify(mActiveProfileRemoveUseCase).execute(any(Subscriber.class));
    }
}