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

package com.jaspersoft.android.jaspermobile.ui.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.dialog.DateDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.OutputFormatDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.ValueInputDialogFragment;
import com.jaspersoft.android.jaspermobile.internal.di.components.screen.ScheduleFormScreenComponent;
import com.jaspersoft.android.jaspermobile.internal.di.components.screen.activity.ScheduleFormActivityComponent;
import com.jaspersoft.android.jaspermobile.internal.di.modules.screen.ScheduleFormScreenModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.screen.activity.ScheduleFormActivityModule;
import com.jaspersoft.android.jaspermobile.ui.component.fragment.PresenterControllerFragment;
import com.jaspersoft.android.jaspermobile.ui.contract.ScheduleFormContract;
import com.jaspersoft.android.jaspermobile.ui.presenter.ScheduleFormPresenter;
import com.jaspersoft.android.jaspermobile.ui.view.entity.JobFormViewEntity;
import com.jaspersoft.android.jaspermobile.ui.view.widget.ScheduleFormView;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;


/**
 * @author Tom Koptel
 * @since 2.5
 */
@OptionsMenu(R.menu.report_edit_schedule)
@EFragment
public class EditScheduleFormFragment extends PresenterControllerFragment<ScheduleFormScreenComponent, ScheduleFormPresenter>
        implements DateDialogFragment.DateDialogClickListener,
        OutputFormatDialogFragment.OutputFormatClickListener,
        ValueInputDialogFragment.ValueDialogCallback,
        ScheduleFormContract.View {
    @FragmentArg
    int jobId;
    @OptionsMenuItem(R.id.editSchedule)
    MenuItem editAction;

    @ViewById
    ScheduleFormView scheduleFormView;

    @Inject
    ScheduleFormContract.EventListener mEventListener;

    private ScheduleFormActivityComponent mActivityComponent;
    private boolean mShowEditAction;

    @Override
    protected ScheduleFormScreenComponent onCreateNonConfigurationComponent() {
        return getProfileComponent().plus(new ScheduleFormScreenModule(jobId));
    }

    @Override
    public ScheduleFormPresenter getPresenter() {
        if (mActivityComponent == null) {
            mActivityComponent = getComponent().plus(new ScheduleFormActivityModule(this));
        }
        return mActivityComponent.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        scheduleFormView = (ScheduleFormView) inflater.inflate(R.layout.fragment_schedule, container, false);
        getPresenter().bindView(this);
        mActivityComponent.inject(this);
        return mActivityComponent.inject(scheduleFormView);
    }

    @Override
    public void onDateSelected(String id, Calendar date) {
        scheduleFormView.onDateSelected(id, date);
    }

    @Override
    public void onTextValueEntered(int requestCode, String name) {
        if (requestCode == ScheduleFormView.JOB_NAME_CODE) {
            setActionBarTitle(name);
        }
        scheduleFormView.onTextValueEntered(requestCode, name);
    }

    @Override
    public void onOutputFormatSelected(List<JobFormViewEntity.OutputFormat> selectedFormats) {
        scheduleFormView.onOutputFormatSelected(selectedFormats);
    }

    @Override
    public void showForm(JobFormViewEntity form) {
        mShowEditAction = true;
        getActivity().supportInvalidateOptionsMenu();

        setActionBarTitle(form.getJobName());
        scheduleFormView.showForm(form);
    }

    @Override
    public void showFormLoadingMessage() {
        showProgress();
    }

    @Override
    public void hideFormLoadingMessage() {
        hideProgress();
    }

    @Override
    public void showSubmitMessage() {
        showProgress();
    }

    @Override
    public void hideSubmitMessage() {
        hideProgress();
    }

    private void showProgress() {
        ProgressDialogFragment.builder(getFragmentManager())
                .setLoadingMessage(R.string.loading_msg)
                .show();
    }

    private void hideProgress() {
        ProgressDialogFragment.dismiss(getFragmentManager());
    }

    @Override
    public void showSubmitSuccess() {
        Toast.makeText(getActivity(), R.string.sch_updated, Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    @Override
    public JobFormViewEntity takeForm() {
        return scheduleFormView.provideForm();
    }

    @OptionsItem(R.id.editSchedule)
    protected void schedule() {
        JobFormViewEntity form = scheduleFormView.provideForm();
        mEventListener.onSubmitClick(form);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        editAction.setVisible(mShowEditAction);
    }

    private void setActionBarTitle(String title) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }
}
