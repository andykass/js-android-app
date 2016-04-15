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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.dialog.CalendarDayDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.CalendarMonthDialogFragment;
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
import com.jaspersoft.android.jaspermobile.ui.entity.job.CalendarViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewBundle;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewEntity;
import com.jaspersoft.android.jaspermobile.ui.entity.job.SimpleViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.presenter.ScheduleFormPresenter;
import com.jaspersoft.android.jaspermobile.ui.view.widget.ScheduleFormView;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;


/**
 * @author Tom Koptel
 * @since 2.5
 */
@OptionsMenu(R.menu.report_add_schedule)
@EFragment
public class NewScheduleFormFragment extends PresenterControllerFragment<JobFormScreenComponent, ScheduleFormPresenter>
        implements DateDialogFragment.IcDateDialogClickListener,
        OutputFormatDialogFragment.OutputFormatClickListener,
        ValueInputDialogFragment.ValueDialogCallback,
        ScheduleFormContract.View,
        RecurrenceTypeDialogFragment.RecurrenceTypeClickListener,
        NumberDialogFragment.NumberDialogClickListener,
        IntervalUnitDialogFragment.IntervalUnitClickListener,
        CalendarMonthDialogFragment.MonthsSelectedListener,
        CalendarDayDialogFragment.DaysSelectedListener
{
    @FragmentArg
    JasperResource resource;

    @ViewById
    ScheduleFormView scheduleFormView;

    @Inject
    ScheduleFormContract.EventListener mEventListener;

    private JobFormActivityComponent mActivityComponent;

    @Override
    protected JobFormScreenComponent onCreateNonConfigurationComponent() {
        return getProfileComponent().plus(new JobFormScreenModule(resource));
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
    public void onDateSelected(Calendar date, int requestCode, Object... data) {
        scheduleFormView.onDateSelected(date, requestCode, data);
    }

    @Override
    public void onTextValueEntered(int requestCode, String name) {
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
    public void onDaysSelected(List<CalendarViewRecurrence.Day> selectedDays) {
        scheduleFormView.onDaysSelected(selectedDays);
    }

    @Override
    public void onMonthsSelected(List<CalendarViewRecurrence.Month> selectedMonths) {
        scheduleFormView.onMonthsSelected(selectedMonths);
    }

    @Override
    public void showForm(JobFormViewBundle form) {
        scheduleFormView.showForm(form);
    }

    @Override
    public void showFormLoadingMessage() {
    }

    @Override
    public void hideFormLoadingMessage() {
    }

    @Override
    public void showSubmitMessage() {
        ProgressDialogFragment.builder(getFragmentManager())
                .setLoadingMessage(R.string.loading_msg)
                .show();
    }

    @Override
    public void hideSubmitMessage() {
        ProgressDialogFragment.dismiss(getFragmentManager());
    }

    @Override
    public void showSubmitSuccess() {
        Toast.makeText(getActivity(), R.string.sch_created, Toast.LENGTH_SHORT).show();
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public JobFormViewBundle takeForm() {
        return scheduleFormView.provideForm();
    }

    @OptionsItem(R.id.addSchedule)
    protected void schedule() {
        JobFormViewBundle form = scheduleFormView.provideForm();
        mEventListener.onSubmitClick(form);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.sch_new);
    }
}
