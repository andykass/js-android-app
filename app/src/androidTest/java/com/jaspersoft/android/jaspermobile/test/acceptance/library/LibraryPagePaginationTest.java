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

package com.jaspersoft.android.jaspermobile.test.acceptance.library;

import com.google.android.apps.common.testing.testrunner.ActivityLifecycleMonitorRegistry;
import com.google.android.apps.common.testing.ui.espresso.Espresso;
import com.jaspersoft.android.jaspermobile.activities.repository.LibraryActivity_;
import com.jaspersoft.android.jaspermobile.test.ProtoActivityInstrumentation;
import com.jaspersoft.android.jaspermobile.test.utils.CommonTestModule;
import com.jaspersoft.android.jaspermobile.test.utils.TestResources;
import com.jaspersoft.android.jaspermobile.util.JsSpiceManager;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.JsServerProfile;
import com.jaspersoft.android.sdk.client.async.request.cacheable.GetResourceLookupsRequest;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookupsList;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.jaspersoft.android.jaspermobile.test.utils.espresso.JasperMatcher.hasTotalCount;
import static com.jaspersoft.android.jaspermobile.test.utils.espresso.JasperMatcher.swipeUp;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 2.0
 */
public class LibraryPagePaginationTest extends ProtoActivityInstrumentation<LibraryActivity_> {
    private static final int LIMIT = 40;

    @Mock
    JsServerProfile mockServerProfile;
    @Mock
    JsRestClient mockRestClient;
    @Mock
    SpiceManager mockSpiceService;

    final MockedSpiceManager mMockedSpiceManager = new MockedSpiceManager();
    private ResourceLookupsList firstLookUp, secondLookUp;
    private ResourceFragmentInjector injector;

    public LibraryPagePaginationTest() {
        super(LibraryActivity_.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        firstLookUp = TestResources.get().fromXML(ResourceLookupsList.class, "library_0_40");
        secondLookUp = TestResources.get().fromXML(ResourceLookupsList.class, "library_40_40");

        firstLookUp.setTotalCount(
                firstLookUp.getResourceLookups().size() + secondLookUp.getResourceLookups().size()
        );

        registerTestModule(new TestModule());
        when(mockRestClient.getServerProfile()).thenReturn(mockServerProfile);
        when(mockServerProfile.getUsernameWithOrgId()).thenReturn(USERNAME);
        when(mockServerProfile.getPassword()).thenReturn(PASSWORD);

        ResourcesFragmentIdlingResource resourceFragmentInjector =
                new ResourcesFragmentIdlingResource();
        Espresso.registerIdlingResources(resourceFragmentInjector);
        injector = new ResourceFragmentInjector(resourceFragmentInjector);
        ActivityLifecycleMonitorRegistry.getInstance()
                .addLifecycleCallback(injector);
    }

    @Override
    protected void tearDown() throws Exception {
        unregisterTestModule();
        ActivityLifecycleMonitorRegistry.getInstance()
                .removeLifecycleCallback(injector);
        super.tearDown();
    }

    public void ignoreScrollTo() throws InterruptedException {
        startActivityUnderTest();
        for (int i = 0; i < 3; i++) {
            onView(withId(android.R.id.list)).perform(swipeUp());
        }
        onView(withId(android.R.id.list)).check(hasTotalCount(firstLookUp.getTotalCount()));
    }

    private class MockedSpiceManager extends JsSpiceManager {

        public <T> void execute(final SpiceRequest<T> request, final Object requestCacheKey,
                                final long cacheExpiryDuration, final RequestListener<T> requestListener) {
            if (request instanceof GetResourceLookupsRequest) {
                int offset = ((GetResourceLookupsRequest) request).getSearchCriteria().getOffset();
                if (offset == LIMIT) {
                    requestListener.onRequestSuccess((T) secondLookUp);
                } else {
                    requestListener.onRequestSuccess((T) firstLookUp);
                }
            }
        }

    }

    private class TestModule extends CommonTestModule {
        @Override
        protected void semanticConfigure() {
            bind(JsRestClient.class).toInstance(mockRestClient);
            bind(JsSpiceManager.class).toInstance(mMockedSpiceManager);
        }
    }
}

