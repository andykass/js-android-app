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

import android.content.Intent;

import com.google.inject.Singleton;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.ReportHtmlViewerActivity_;
import com.jaspersoft.android.jaspermobile.db.DatabaseProvider;
import com.jaspersoft.android.jaspermobile.test.ProtoActivityInstrumentation;
import com.jaspersoft.android.jaspermobile.test.utils.CommonTestModule;
import com.jaspersoft.android.jaspermobile.test.utils.SmartMockedSpiceManager;
import com.jaspersoft.android.jaspermobile.test.utils.TestResources;
import com.jaspersoft.android.jaspermobile.util.JsXmlSpiceServiceWrapper;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.async.JsXmlSpiceService;
import com.jaspersoft.android.sdk.client.oxm.control.InputControlStatesList;
import com.jaspersoft.android.sdk.client.oxm.control.InputControlsList;
import com.jaspersoft.android.sdk.client.oxm.report.ReportExecutionResponse;
import com.octo.android.robospice.SpiceManager;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static com.jaspersoft.android.jaspermobile.test.utils.espresso.JasperMatcher.firstChildOf;
import static com.jaspersoft.android.jaspermobile.test.utils.espresso.JasperMatcher.onOverflowView;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 1.9
 */
public class ReportViewPageTest extends ProtoActivityInstrumentation<ReportHtmlViewerActivity_> {
    private static final String RESOURCE_URI = "/Reports/2_Sales_Mix_by_Demographic_Report";
    private static final String RESOURCE_LABEL = "02. Sales Mix by Demographic Report";

    @Mock
    JsXmlSpiceServiceWrapper mockJsXmlSpiceServiceWrapper;
    @Mock
    SpiceManager mockSpiceService;
    @Mock
    DatabaseProvider mockDbProvider;

    private InputControlsList inputControlList;
    private SmartMockedSpiceManager mMockedSpiceManager;
    private ReportExecutionResponse emptyReportExecution;

    public ReportViewPageTest() {
        super(ReportHtmlViewerActivity_.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mMockedSpiceManager = SmartMockedSpiceManager.createHybridManager(JsXmlSpiceService.class);
        inputControlList = TestResources.get().fromXML(InputControlsList.class, "input_contols_list");
        emptyReportExecution = TestResources.get().fromXML(ReportExecutionResponse.class, "empty_report_execution");

        MockitoAnnotations.initMocks(this);
        when(mockJsXmlSpiceServiceWrapper.getSpiceManager())
                .thenReturn(mMockedSpiceManager);
        registerTestModule(new TestModule());
        setDefaultCurrentProfile();
        WebViewInjector.registerFor(ReportHtmlViewerActivity_.class);
    }


    @Override
    protected void tearDown() throws Exception {
        unregisterTestModule();
        mMockedSpiceManager.removeLifeCyclkeListener();
        WebViewInjector.unregister();
        super.tearDown();
    }

    public void testReportWithNoInputControls() {
        mMockedSpiceManager.addNetworkResponse(new InputControlsList());
        createReportIntent();
        startActivityUnderTest();

        onView(withText(RESOURCE_LABEL)).check(matches(isDisplayed()));
        onView(not(withId(R.id.showFilters)));

        rotate();
        onView(firstChildOf(withId(R.id.webViewPlaceholder))).check(matches(isDisplayed()));
    }

    public void testReportWithInputControls() {
        mMockedSpiceManager.addNetworkResponse(inputControlList);
        createReportIntent();
        startActivityUnderTest();

        mMockedSpiceManager.behaveInRealMode();
        onView(withId(R.id.runReportButton)).perform(click());
        onView(withText(RESOURCE_LABEL)).check(matches(isDisplayed()));
        onView(withId(R.id.showFilters)).check(matches(isDisplayed()));

        rotate();
        onView(firstChildOf(withId(R.id.webViewPlaceholder))).check(matches(isDisplayed()));
    }

    public void testEmptyReport() {
        mMockedSpiceManager.behaveInMockedMode();
        mMockedSpiceManager.addNetworkResponse(inputControlList);
        mMockedSpiceManager.addNetworkResponse(new InputControlStatesList());
        mMockedSpiceManager.addNetworkResponse(emptyReportExecution);
        createReportIntent();
        startActivityUnderTest();

        onView(withId(R.id.runReportButton)).perform(click());
        onOverflowView(getActivity(), withId(R.id.sdl__title)).check(matches(withText(R.string.warning_msg)));
        onOverflowView(getActivity(), withId(R.id.sdl__message)).check(matches(withText(R.string.rv_error_empty_report)));
    }

    private void createReportIntent() {
        Intent htmlViewer = new Intent();
        htmlViewer.putExtra(ReportHtmlViewerActivity_.RESOURCE_URI_EXTRA, RESOURCE_URI);
        htmlViewer.putExtra(ReportHtmlViewerActivity_.RESOURCE_LABEL_EXTRA, RESOURCE_LABEL);
        setActivityIntent(htmlViewer);
    }

    private class TestModule extends CommonTestModule {
        @Override
        protected void semanticConfigure() {
            bind(JsRestClient.class).in(Singleton.class);
            bind(JsXmlSpiceServiceWrapper.class).toInstance(mockJsXmlSpiceServiceWrapper);
            bind(DatabaseProvider.class).toInstance(mockDbProvider);
        }
    }

}
