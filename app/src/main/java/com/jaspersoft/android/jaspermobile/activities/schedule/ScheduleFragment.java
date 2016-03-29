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

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.dialog.DateDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.OutputFormatDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.ValueInputDialogFragment;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;
import com.jaspersoft.android.jaspermobile.util.JobOutputFormatConverter;
import com.jaspersoft.android.jaspermobile.util.schedule.ScheduleViewModel;
import com.jaspersoft.android.jaspermobile.widget.DateTimeView;
import com.jaspersoft.android.sdk.client.ic.InputControlWrapper;
import com.jaspersoft.android.sdk.service.data.schedule.JobOutputFormat;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@EFragment(R.layout.fragment_schedule)
public class ScheduleFragment extends BaseFragment implements DateDialogFragment.DateDialogClickListener,
        OutputFormatDialogFragment.OutputFormatClickListener, ValueInputDialogFragment.ValueDialogCallback {

    public static final String TAG = ScheduleFragment.class.getSimpleName();

    private final static int JOB_NAME_CODE = 563;
    private final static int FILE_NAME_CODE = 251;
    private final static int OUTPUT_PATH_CODE = 515;

    @InstanceState
    @FragmentArg
    protected ScheduleViewModel scheduleViewModel;

    @ViewById(R.id.scheduleName)
    TextView jobName;
    @ViewById(R.id.fileName)
    TextView fileName;
    @ViewById(R.id.ic_boolean_title)
    TextView runImmediatelyTitle;
    @ViewById(R.id.ic_boolean)
    CheckBox runImmediately;
    @ViewById(R.id.scheduleDate)
    DateTimeView scheduleDate;
    @ViewById(R.id.outputFormat)
    TextView outputFormat;
    @ViewById(R.id.outputPath)
    TextView outputPath;

    @AfterViews
    protected void init() {
        jobName.setText(scheduleViewModel.getJobName());
        fileName.setText(scheduleViewModel.getFileName());

        runImmediatelyTitle.setText(getString(R.string.sch_run_immediately));
        checkBoxCheckedChange(scheduleViewModel.getDate() == null);

        Calendar selectedDate = scheduleViewModel.getDate() != null ? scheduleViewModel.getDate() : Calendar.getInstance();
        scheduleDate.setDate(selectedDate);
        scheduleDate.setLabel(getString(R.string.sch_start_date));
        scheduleDate.setClearButtonVisibility(false);
        scheduleDate.setDateTimeClickListener(new ScheduleDateClickListener());

        outputFormat.setText(getSupportedFormatsTitles());
        outputPath.setText(scheduleViewModel.getOutputPath());
    }

    public ScheduleViewModel provideJob() {
        ScheduleViewModel job = scheduleViewModel.clone();
        if (runImmediately.isChecked()) {
            job.setDate(null);
        }
        return job;
    }

    @Click(R.id.runImmediately)
    protected void runImmediatelyClicked() {
        runImmediately.performClick();
    }

    @Click(R.id.scheduleNameContainer)
    protected void scheduleNameClick() {
        ValueInputDialogFragment.createBuilder(getFragmentManager())
                .setLabel(getString(R.string.sch_job_name))
                .setValue(jobName.getText().toString())
                .setRequired(true)
                .setCancelableOnTouchOutside(true)
                .setRequestCode(JOB_NAME_CODE)
                .setTargetFragment(this)
                .show();
    }

    @Click(R.id.fileNameContainer)
    protected void fileNameClick() {
        ValueInputDialogFragment.createBuilder(getFragmentManager())
                .setLabel(getString(R.string.sch_file_name))
                .setValue(fileName.getText().toString())
                .setRequired(true)
                .setCancelableOnTouchOutside(true)
                .setRequestCode(FILE_NAME_CODE)
                .setTargetFragment(this)
                .show();
    }

    @Click(R.id.outputFormatContainer)
    protected void selectOutputFormat() {
        OutputFormatDialogFragment.createBuilder(getFragmentManager())
                .setSelectedFormats(scheduleViewModel.getJobOutputFormats())
                .setTargetFragment(this)
                .show();
    }

    @Click(R.id.outputPathContainer)
    protected void outputPathClick() {
        ValueInputDialogFragment.createBuilder(getFragmentManager())
                .setLabel(getString(R.string.sch_destination))
                .setValue(outputPath.getText().toString())
                .setRequired(true)
                .setCancelableOnTouchOutside(true)
                .setRequestCode(OUTPUT_PATH_CODE)
                .setTargetFragment(this)
                .show();
    }

    @CheckedChange(R.id.ic_boolean)
    protected void checkBoxCheckedChange(boolean checked) {
        runImmediately.setChecked(checked);
        scheduleDate.setVisibility(checked ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDateSelected(String id, Calendar date) {
        if (date.getTimeInMillis() < new Date().getTime()) {
            Toast.makeText(getActivity(), getString(R.string.error_schedule_in_the_past), Toast.LENGTH_SHORT).show();
            return;
        }

        scheduleViewModel.setDate(date);
        scheduleDate.setDate(date);
    }

    @Override
    public void onTextValueEntered(int requestCode, String name) {
        switch (requestCode) {
            case JOB_NAME_CODE:
                scheduleViewModel.setJobName(name);
                jobName.setText(name);
                break;
            case FILE_NAME_CODE:
                scheduleViewModel.setFileName(name);
                fileName.setText(name);
                break;
            case OUTPUT_PATH_CODE:
                scheduleViewModel.setOutputPath(name);
                outputPath.setText(name);
                break;
        }
    }

    @Override
    public void onOutputFormatSelected(ArrayList<JobOutputFormat> outputFormats) {
        scheduleViewModel.setJobOutputFormats(outputFormats);
        outputFormat.setText(getSupportedFormatsTitles());
    }

    private String getSupportedFormatsTitles() {
        return scheduleViewModel.getJobOutputFormats().isEmpty() ? InputControlWrapper.NOTHING_SUBSTITUTE_LABEL :
                TextUtils.join(", ", JobOutputFormatConverter.toStringsArray(getActivity(), scheduleViewModel.getJobOutputFormats()));
    }

    private class ScheduleDateClickListener implements DateTimeView.DateTimeClickListener {
        @Override
        public void onDateClick(int position) {
            DateDialogFragment.createBuilder(getFragmentManager())
                    .setInputControlId(null)
                    .setDate(scheduleViewModel.getDate())
                    .setType(DateDialogFragment.DATE)
                    .setTargetFragment(ScheduleFragment.this)
                    .show();
        }

        @Override
        public void onTimeClick(int position) {
            DateDialogFragment.createBuilder(getFragmentManager())
                    .setInputControlId(null)
                    .setDate(scheduleViewModel.getDate())
                    .setType(DateDialogFragment.TIME)
                    .setTargetFragment(ScheduleFragment.this)
                    .show();
        }

        @Override
        public void onClear(int position) {
            scheduleViewModel.setDate(null);
            scheduleDate.setDate(null);
        }
    }
}
