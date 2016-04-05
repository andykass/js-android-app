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

package com.jaspersoft.android.jaspermobile.ui.view.activity.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.entity.JasperResource;
import com.jaspersoft.android.jaspermobile.internal.di.components.screen.ChooseReportScreenComponent;
import com.jaspersoft.android.jaspermobile.internal.di.components.screen.activity.ChooseReportActivityComponent;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.screen.activity.ChooserReportActivityModule;
import com.jaspersoft.android.jaspermobile.ui.component.activity.PresenterControllerActivity2;
import com.jaspersoft.android.jaspermobile.ui.eventbus.JasperResourceBus;
import com.jaspersoft.android.jaspermobile.ui.presenter.CatalogPresenter;
import com.jaspersoft.android.jaspermobile.ui.view.widget.LibraryCatalogView;
import com.jaspersoft.android.jaspermobile.ui.view.widget.LibraryCatalogView_;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class ChooseReportActivity extends PresenterControllerActivity2<ChooseReportScreenComponent> implements JasperResourceBus.EventListener {

    public static final String RESULT_JASPER_RESOURCE = "ChooseReportActivity.JasperResource";

    @Inject
    CatalogPresenter mCatalogPresenter;
    @Inject
    JasperResourceBus mJasperResourceBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChooseReportActivityComponent activityComponent = activityComponent();
        activityComponent.inject(this);
        registerPresenter(mCatalogPresenter);

        LibraryCatalogView catalogView = LibraryCatalogView_.build(this);
        activityComponent.inject(catalogView);

        setContentView(catalogView);
        catalogView.setEventListener(mCatalogPresenter);
        mCatalogPresenter.bindView(catalogView);

        mJasperResourceBus.subscribe(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.sch_choose_report));
        }
    }

    private ChooseReportActivityComponent activityComponent() {
        return getComponent().plus(new ChooserReportActivityModule(this));
    }

    @Override
    protected ChooseReportScreenComponent onCreateNonConfigurationComponent() {
        return getProfileComponent().newChooseReportScreen();
    }

    @Override
    protected String getScreenName() {
        return getString(R.string.ja_choose_sch);
    }

    @Override
    public void onSelect(JasperResource resource) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(RESULT_JASPER_RESOURCE, resource);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
