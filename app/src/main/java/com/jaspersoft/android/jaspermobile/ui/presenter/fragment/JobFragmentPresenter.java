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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.internal.di.components.screen.JobsScreenComponent;
import com.jaspersoft.android.jaspermobile.internal.di.components.screen.activity.JobsActivityComponent;
import com.jaspersoft.android.jaspermobile.internal.di.modules.screen.activity.ChooserReportActivityModule;
import com.jaspersoft.android.jaspermobile.ui.component.fragment.PresenterControllerFragment;
import com.jaspersoft.android.jaspermobile.ui.component.fragment.PresenterControllerFragment2;
import com.jaspersoft.android.jaspermobile.ui.component.presenter.Presenter;
import com.jaspersoft.android.jaspermobile.ui.view.activity.schedule.ChooseReportActivity;
import com.jaspersoft.android.jaspermobile.dialog.DeleteJobDialogFragment;
import com.jaspersoft.android.jaspermobile.domain.entity.JasperResource;
import com.jaspersoft.android.jaspermobile.domain.model.JobResourceModel;
import com.jaspersoft.android.jaspermobile.internal.di.components.JobsComponent;
import com.jaspersoft.android.jaspermobile.internal.di.modules.screen.activity.JobsActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.FragmentModule;
import com.jaspersoft.android.jaspermobile.ui.eventbus.JobResourcesBus;
import com.jaspersoft.android.jaspermobile.ui.navigation.Navigator;
import com.jaspersoft.android.jaspermobile.ui.navigation.Page;
import com.jaspersoft.android.jaspermobile.ui.navigation.PageFactory;
import com.jaspersoft.android.jaspermobile.ui.presenter.CatalogPresenter;
import com.jaspersoft.android.jaspermobile.ui.presenter.CatalogSearchPresenter;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.CatalogSearchFragment;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.CatalogSearchFragment_;
import com.jaspersoft.android.jaspermobile.ui.view.widget.JobCatalogView;
import com.jaspersoft.android.jaspermobile.util.resource.ReportResource;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsMenuItem;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@EFragment
public class JobFragmentPresenter extends PresenterControllerFragment2<JobsScreenComponent> implements JobResourcesBus.EventListener, DeleteJobDialogFragment.DeleteJobDialogClickListener {

    private static final String SEARCH_VIEW_TAG = "job_search_view";
    private static final int CHOOSE_REPORT_REQUEST = 2112;
    private static final int EDIT_JOB_REQUEST = 5512;

    JobCatalogView mCatalogView;
    @OptionsMenuItem(R.id.search)
    MenuItem catalogSearchItem;

    @Inject
    CatalogPresenter mCatalogPresenter;
    @Inject
    CatalogSearchPresenter mCatalogSearchPresenter;
    @Inject
    JobResourceModel mJobResourceModel;
    @Inject
    JobResourcesBus mJobResourcesBus;
    @Inject
    Navigator mNavigator;
    @Inject
    PageFactory mPageFactory;
    private JobsActivityComponent mActivityComponent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jobs, container, false);
        mCatalogView = (JobCatalogView) view.findViewById(R.id.catalogView);

        JobsActivityComponent activityComponent = activityComponent();
        activityComponent.inject(this);

        registerPresenter(mCatalogPresenter);
        registerPresenter(mCatalogSearchPresenter);

        activityComponent.inject(mCatalogView);

        return view;
    }

    private JobsActivityComponent activityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent = getComponent().plus(new JobsActivityModule(this));
        }
        return mActivityComponent;
    }

    @Override
    protected JobsScreenComponent onCreateNonConfigurationComponent() {
        return getProfileComponent().newJobsScreen();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initCatalog();
        mJobResourcesBus.subscribe(this);

        ((ToolbarActivity) getActivity()).setCustomToolbarView(null);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.sch_jobs));
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        initSearch();
    }

    @Click(R.id.newJob)
    protected void newJobAction() {
        Page jobEditPage = mPageFactory.createChooseJobPage();
        mNavigator.navigateForResult(jobEditPage, CHOOSE_REPORT_REQUEST);
    }

    @OnActivityResult(CHOOSE_REPORT_REQUEST)
    void onJobChosen(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        JasperResource resource = (JasperResource) data.getSerializableExtra(ChooseReportActivity.RESULT_JASPER_RESOURCE);
        Page jobEditPage = mPageFactory.createNewJobPage(toJasperResource(resource));
        mNavigator.navigateForResult(jobEditPage, EDIT_JOB_REQUEST);
    }

    @OnActivityResult(EDIT_JOB_REQUEST)
    void onJobEdited(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            mCatalogPresenter.refresh();
        }
    }

    @Override
    public void onDeleteConfirmed(int jobId) {
        mJobResourceModel.requestToDelete(jobId);
    }

    private void initCatalog() {
        mCatalogView.setEventListener(mCatalogPresenter);
        mCatalogPresenter.bindView(mCatalogView);
    }

    private void initSearch() {
        CatalogSearchFragment catalogSearchFragment = (CatalogSearchFragment) getChildFragmentManager().findFragmentByTag(SEARCH_VIEW_TAG);
        if (catalogSearchFragment == null) {
            catalogSearchFragment = CatalogSearchFragment_.builder().build();
            getChildFragmentManager().beginTransaction().add(catalogSearchFragment, SEARCH_VIEW_TAG).commit();
        }
        catalogSearchFragment.setEventListener(mCatalogSearchPresenter);
        mCatalogSearchPresenter.bindView(catalogSearchFragment);
    }

    @Override
    public void onSelect(int id) {
        Page jobEditPage = mPageFactory.createJobEditPage(id);
        mNavigator.navigateForResult(jobEditPage, EDIT_JOB_REQUEST);
    }

    @Override
    public void onDeleteRequest(int id) {
        String deleteMessage = getActivity().getString(R.string.sdr_delete_message);

        DeleteJobDialogFragment.createBuilder(getActivity(), getFragmentManager())
                .setJobId(id)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.sdr_delete_title)
                .setMessage(deleteMessage)
                .setPositiveButtonText(R.string.spm_delete_btn)
                .setNegativeButtonText(R.string.cancel)
                .setTargetFragment(this)
                .show();
    }

    private ReportResource toJasperResource(JasperResource resource) {
        return new ReportResource(resource.getUri().toString(), resource.getLabel(), resource.getDescription(), null);
    }
}