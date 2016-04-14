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

import android.app.Activity;
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
import com.jaspersoft.android.jaspermobile.dialog.IntervalUnitDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.NumberDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.OutputFormatDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.RecurrenceTypeDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.ValueInputDialogFragment;
import com.jaspersoft.android.jaspermobile.internal.di.components.screen.JobFormScreenComponent;
import com.jaspersoft.android.jaspermobile.internal.di.components.screen.activity.JobFormActivityComponent;
import com.jaspersoft.android.jaspermobile.internal.di.modules.screen.job.JobFormScreenModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.job.JobFormActivityModule;
import com.jaspersoft.android.jaspermobile.ui.component.fragment.PresenterControllerFragment;
import com.jaspersoft.android.jaspermobile.ui.contract.ScheduleFormContract;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewBundle;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewEntity;
import com.jaspersoft.android.jaspermobile.ui.entity.job.SimpleViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.presenter.ScheduleFormPresenter;
import com.jaspersoft.android.jaspermobile.ui.view.widget.ScheduleFormView;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
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
public class EditScheduleFormFragment extends PresenterControllerFragment<JobFormScreenComponent, ScheduleFormPresenter>
        implements DateDialogFragment.IcDateDialogClickListener,
        OutputFormatDialogFragment.OutputFormatClickListener,
        ValueInputDialogFragment.ValueDialogCallback,
        ScheduleFormContract.View,
        RecurrenceTypeDialogFragment.RecurrenceTypeClickListener,
        NumberDialogFragment.NumberDialogClickListener,
        IntervalUnitDialogFragment.IntervalUnitClickListener {
    public static final String FORM_DATA_ARG = "FORM_DATA_ARG";
    @FragmentArg
    int jobId;
    @OptionsMenuItem(R.id.editSchedule)
    MenuItem editAction;

    @ViewById
    ScheduleFormView scheduleFormView;

    @Inject
    ScheduleFormContract.EventListener mEventListener;

    private JobFormActivityComponent mActivityComponent;
    private boolean mShowEditAction;
    @InstanceState
    JobFormViewBundle formData;

    @Override
    protected JobFormScreenComponent onCreateNonConfigurationComponent() {
        return getProfileComponent().plus(new JobFormScreenModule(jobId));
    }

    @Override
    public ScheduleFormPresenter getPresenter() {
        if (mActivityComponent == null) {
            mActivityComponent = getComponent().plus(new JobFormActivityModule(this));
        }
        return mActivityComponent.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedule, container, false);
        scheduleFormView = (ScheduleFormView) root.findViewById(R.id.scheduleFormView);
        mActivityComponent.inject(scheduleFormView);

        getPresenter().bindView(this);
        mActivityComponent.inject(this);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (formData != null) {
            showEditAction();
            updateTitle(formData);
        }
    }

    @Override
    public void onDateSelected(Calendar date, int requestCode, Object... data) {
        scheduleFormView.onDateSelected(date, requestCode, data);
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
    public void onRecurrenceSelected(JobFormViewEntity.Recurrence recurrence) {
        scheduleFormView.onRecurrenceSelected(recurrence);
    }

    @Override
    public void onNumberSubmit(int number, int requestCode) {
        scheduleFormView.onNumberSubmit(number, requestCode);
    }

    @Override
    public void onUnitSelected(SimpleViewRecurrence.Unit unit) {
        scheduleFormView.onUnitSelected(unit);
    }

    @Override
    public void showForm(JobFormViewBundle bundle) {
        this.formData = bundle;

        showEditAction();
        updateTitle(bundle);
        scheduleFormView.showForm(bundle);
    }

    private void showEditAction() {
        mShowEditAction = true;
        getActivity().supportInvalidateOptionsMenu();
    }

    private void updateTitle(JobFormViewBundle bundle) {
        JobFormViewEntity form = bundle.form();
        setActionBarTitle(form.jobName());
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
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public JobFormViewBundle takeForm() {
        return scheduleFormView.provideForm();
    }

    @OptionsItem(R.id.editSchedule)
    protected void schedule() {
        JobFormViewBundle form = scheduleFormView.provideForm();
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
