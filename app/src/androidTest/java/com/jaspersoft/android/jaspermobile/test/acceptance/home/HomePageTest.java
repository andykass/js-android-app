/*
 * Copyright (C) 2012-2014 Jaspersoft Corporation. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.test.acceptance.home;

import com.google.inject.Singleton;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.HomeActivity_;
import com.jaspersoft.android.jaspermobile.db.DatabaseProvider;
import com.jaspersoft.android.jaspermobile.test.ProtoActivityInstrumentation;
import com.jaspersoft.android.jaspermobile.test.utils.CommonTestModule;
import com.jaspersoft.android.jaspermobile.test.utils.MockedSpiceManager;
import com.jaspersoft.android.jaspermobile.util.ConnectivityUtil;
import com.jaspersoft.android.jaspermobile.util.JsXmlSpiceServiceWrapper;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.JsServerProfile;
import com.jaspersoft.android.sdk.client.async.JsXmlSpiceService;
import com.jaspersoft.android.sdk.client.oxm.server.ServerInfo;
import com.octo.android.robospice.SpiceManager;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.pressBack;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static com.jaspersoft.android.jaspermobile.test.utils.espresso.JasperMatcher.onOverflowView;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 2.0
 */
public class HomePageTest extends ProtoActivityInstrumentation<HomeActivity_> {

    private static final String PASSWORD = "SOME_PASSWORD";
    private static final String ALIAS = "Mobile Demo";
    private static final String USERNAME = "Joe";
    private static final String ORGANIZATION = "Jasper";

    @Mock
    JsRestClient mockRestClient;
    @Mock
    JsServerProfile mockServerProfile;
    @Mock
    JsXmlSpiceServiceWrapper mockJsXmlSpiceServiceWrapper;
    @Mock
    SpiceManager mockSpiceService;
    @Mock
    DatabaseProvider mockDatabaseProvider;
    @Mock
    ConnectivityUtil mockConectivityUtil;
    @Mock
    ServerInfo mockServerInfo;

    final MockedSpiceManager mMockedSpiceManager = new MockedSpiceManager(JsXmlSpiceService.class);

    public HomePageTest() {
        super(HomeActivity_.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        when(mockConectivityUtil.isConnected()).thenReturn(true);
        when(mockJsXmlSpiceServiceWrapper.getSpiceManager()).thenReturn(mMockedSpiceManager);

        registerTestModule(new TestModule());
        setDefaultCurrentProfile();

        mMockedSpiceManager.setResponseForCacheRequest(mockServerInfo);
    }

    @Override
    protected void tearDown() throws Exception {
        unregisterTestModule();
        super.tearDown();
    }

    public void testMissingNetworkConnectionCase() {
        // Given simulation of connection loss
        when(mockConectivityUtil.isConnected()).thenReturn(false);

        startActivityUnderTest();
        int[] ids = {R.id.home_item_library, R.id.home_item_repository, R.id.home_item_favorites};

        for (int id : ids) {
            // Click on dashboard item
            onView(withId(id)).perform(click());

            onOverflowView(getActivity(), withId(R.id.sdl__title)).check(matches(withText(R.string.h_ad_title_no_connection)));
            onOverflowView(getActivity(), withId(R.id.sdl__message)).check(matches(withText(R.string.h_ad_msg_no_connection)));
            pressBack();
        }
    }

    public void testOverAllNavigationForDashboard() {
        startActivityUnderTest();

        // Check ActionBar server name
        onView(withId(R.id.profile_name)).check(matches(withText(ALIAS)));

        onView(withId(R.id.home_item_library)).perform(click());
        pressBack();
        onView(withId(R.id.home_item_repository)).perform(click());
        pressBack();
        onView(withId(R.id.home_item_favorites)).perform(click());
        pressBack();
        onView(withId(R.id.home_item_saved_reports)).perform(click());
        pressBack();
        onView(withId(R.id.home_item_settings)).perform(click());
        pressBack();
        onView(withId(R.id.home_item_servers)).perform(click());
    }

    private class TestModule extends CommonTestModule {
        @Override
        protected void semanticConfigure() {
            bind(JsRestClient.class).in(Singleton.class);
            bind(JsXmlSpiceServiceWrapper.class).toInstance(mockJsXmlSpiceServiceWrapper);
            bind(DatabaseProvider.class).toInstance(mockDatabaseProvider);
            bind(ConnectivityUtil.class).toInstance(mockConectivityUtil);
        }
    }
}
