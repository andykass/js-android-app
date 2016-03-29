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

package com.jaspersoft.android.jaspermobile.ui.view.activity;

import android.os.Bundle;

import com.jaspersoft.android.jaspermobile.GraphObject;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.internal.di.HasComponent;
import com.jaspersoft.android.jaspermobile.internal.di.components.ReportRestViewerComponent;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportRestViewerModule;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.ReportViewFragment;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.ReportViewFragment_;
import com.jaspersoft.android.jaspermobile.util.ScrollableTitleHelper;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@EActivity(R.layout.report_viewer_layout)
public class ReportViewActivity extends ToolbarActivity implements HasComponent<ReportRestViewerComponent> {
    @Extra
    protected ResourceLookup resource;
    @Bean
    protected ScrollableTitleHelper scrollableTitleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scrollableTitleHelper.injectTitle(resource.getLabel());

        if (savedInstanceState == null) {
            ReportViewFragment viewFragment = ReportViewFragment_.builder()
                    .resource(resource)
                    .build();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.control, viewFragment, ReportViewFragment.TAG)
                    .commit();
        }
    }

    @Override
    public ReportRestViewerComponent getComponent() {
        return GraphObject.Factory.from(this)
                .getProfileComponent()
                .plusReportRestViewer(
                        new ActivityModule(this),
                        new ReportRestViewerModule(resource.getUri())
                );
    }
}
