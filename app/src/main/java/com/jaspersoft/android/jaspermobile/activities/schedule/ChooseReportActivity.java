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

package com.jaspersoft.android.jaspermobile.activities.schedule;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.internal.di.modules.ChooseReportModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.LoadersModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.ui.presenter.CatalogPresenter;
import com.jaspersoft.android.jaspermobile.ui.view.activity.NewScheduleActivity_;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.ui.view.widget.CatalogView;
import com.jaspersoft.android.jaspermobile.ui.view.widget.LibraryCatalogView_;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class ChooseReportActivity extends ToolbarActivity implements CatalogPresenter.ItemSelectListener {

    public static final int CHOOSE_REPORT_REQUEST_CODE = 5512;
    public static final String RESULT_JASPER_RESOURCE = "ChooseReportActivity.JasperResource";

    @Inject
    CatalogPresenter mCatalogPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getProfileComponent()
                .plus(new LoadersModule(this), new ChooseReportModule(), new ActivityModule(this))
                .inject(this);

        CatalogView catalogView = LibraryCatalogView_.build(this);
        setContentView(catalogView);

        catalogView.setEventListener(mCatalogPresenter);
        mCatalogPresenter.bindView(catalogView);
        mCatalogPresenter.setListener(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.sch_choose_report));
        }
    }

    @Override
    protected String getScreenName() {
        return getString(R.string.ja_choose_sch);
    }

    @Override
    public void onPrimaryAction(JasperResource jasperResource) {
        NewScheduleActivity_
                .intent(this)
                .jasperResource(jasperResource)
                .start();
        finish();
    }

    @Override
    public void onSecondaryAction(JasperResource jasperResource) {

    }
}
