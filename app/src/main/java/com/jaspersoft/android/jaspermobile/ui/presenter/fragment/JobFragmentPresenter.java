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

package com.jaspersoft.android.jaspermobile.ui.presenter.fragment;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.schedule.ChooseReportActivity;
import com.jaspersoft.android.jaspermobile.internal.di.modules.JobsModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.LoadersModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.ui.presenter.CatalogPresenter;
import com.jaspersoft.android.jaspermobile.ui.presenter.CatalogSearchPresenter;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.CatalogSearchFragment;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.CatalogSearchFragment_;
import com.jaspersoft.android.jaspermobile.ui.view.widget.CatalogView;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@EFragment(R.layout.fragment_jobs)
public class JobFragmentPresenter extends BaseFragment implements CatalogPresenter.ItemSelectListener {

    private static final String SEARCH_VIEW_TAG = "job_search_view";

    @ViewById(R.id.catalogView)
    CatalogView mCatalogView;

    @Inject
    CatalogPresenter mCatalogPresenter;

    @Inject
    CatalogSearchPresenter mCatalogSearchPresenter;

    @AfterViews
    void init() {
        getProfileComponent()
                .plus(new LoadersModule(this), new JobsModule(), new ActivityModule(getActivity()))
                .inject(this);

        initCatalog();
        initSearch();

        ((ToolbarActivity) getActivity()).setCustomToolbarView(null);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.sch_jobs));
        }
    }

    @Click(R.id.newJob)
    protected void newJobAction() {
        startActivityForResult(new Intent(getActivity(), ChooseReportActivity.class), ChooseReportActivity.CHOOSE_REPORT_REQUEST_CODE);
    }

    @Override
    public void onPrimaryAction(JasperResource jasperResource) {

    }

    @Override
    public void onSecondaryAction(JasperResource jasperResource) {

    }

    private void initCatalog() {
        mCatalogView.setEventListener(mCatalogPresenter);
        mCatalogPresenter.bindView(mCatalogView);
        mCatalogPresenter.setListener(this);
    }

    private void initSearch() {
        CatalogSearchFragment catalogSearchFragment = CatalogSearchFragment_.builder().build();
        getFragmentManager().beginTransaction().add(catalogSearchFragment, SEARCH_VIEW_TAG).commit();
        catalogSearchFragment.setEventListener(mCatalogSearchPresenter);
        mCatalogSearchPresenter.bindView(catalogSearchFragment);
    }
}
