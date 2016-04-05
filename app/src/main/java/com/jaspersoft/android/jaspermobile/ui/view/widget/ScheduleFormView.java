package com.jaspersoft.android.jaspermobile.ui.view.widget;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.dialog.DateDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.OutputFormatDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.ValueInputDialogFragment;
import com.jaspersoft.android.jaspermobile.ui.contract.ScheduleFormContract;
import com.jaspersoft.android.jaspermobile.ui.view.entity.JobFormViewEntity;
import com.jaspersoft.android.jaspermobile.widget.DateTimeView;

import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@EViewGroup(R.layout.view_schedule_form)
public class ScheduleFormView extends LinearLayout implements
        DateDialogFragment.DateDialogClickListener,
        OutputFormatDialogFragment.OutputFormatClickListener,
        ValueInputDialogFragment.ValueDialogCallback {

    public final static int JOB_NAME_CODE = 563;
    public final static int FILE_NAME_CODE = 251;
    public final static int OUTPUT_PATH_CODE = 515;

    @Inject
    FragmentManager mFragmentManager;
    @Inject
    Fragment mParentFragment;
    @Inject
    ScheduleFormContract.EventListener mEventListener;

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

    private JobFormViewEntity form;

    public ScheduleFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void showForm(JobFormViewEntity form) {
        this.form = form;
        update(form);
    }

    public JobFormViewEntity provideForm() {
        return form;
    }

    private void update(JobFormViewEntity form) {
        jobName.setText(form.getJobName());
        fileName.setText(form.getFileName());

        runImmediatelyTitle.setText(getString(R.string.sch_run_immediately));
        checkBoxCheckedChange(form.hasStartDate());

        scheduleDate.setDate(form.getStartDate());
        scheduleDate.setLabel(getString(R.string.sch_start_date));
        scheduleDate.setClearButtonVisibility(false);
        scheduleDate.setDateTimeClickListener(new ScheduleDateClickListener());

        outputFormat.setText(form.getSupportedFormatsTitles());
        outputPath.setText(form.getOutputPath());
    }

    private String getString(@StringRes int id) {
        return getContext().getResources().getString(id);
    }

    @Click(R.id.runImmediately)
    protected void runImmediatelyClicked() {
        runImmediately.performClick();
    }

    @Click(R.id.scheduleNameContainer)
    protected void scheduleNameClick() {
        ValueInputDialogFragment.createBuilder(mFragmentManager)
                .setLabel(getString(R.string.sch_job_name))
                .setValue(jobName.getText().toString())
                .setRequired(true)
                .setCancelableOnTouchOutside(true)
                .setRequestCode(JOB_NAME_CODE)
                .setTargetFragment(mParentFragment)
                .show();
    }

    @Click(R.id.fileNameContainer)
    protected void fileNameClick() {
        ValueInputDialogFragment.createBuilder(mFragmentManager)
                .setLabel(getString(R.string.sch_file_name))
                .setValue(fileName.getText().toString())
                .setRequired(true)
                .setCancelableOnTouchOutside(true)
                .setRequestCode(FILE_NAME_CODE)
                .setTargetFragment(mParentFragment)
                .show();
    }

    @Click(R.id.outputFormatContainer)
    protected void selectOutputFormat() {
        OutputFormatDialogFragment.createBuilder(mFragmentManager)
                .setSelectedFormats(form.getOutputFormats())
                .setTargetFragment(mParentFragment)
                .show();
    }

    @Click(R.id.outputPathContainer)
    protected void outputPathClick() {
        ValueInputDialogFragment.createBuilder(mFragmentManager)
                .setLabel(getString(R.string.sch_destination))
                .setValue(outputPath.getText().toString())
                .setRequired(true)
                .setCancelableOnTouchOutside(true)
                .setRequestCode(OUTPUT_PATH_CODE)
                .setTargetFragment(mParentFragment)
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
            Toast.makeText(getContext(), getString(R.string.error_schedule_in_the_past), Toast.LENGTH_SHORT).show();
            return;
        }

        updateStartDate(date);
    }

    private void updateStartDate(Calendar startDate) {
        form = form.newBuilder()
                .withStartDate(startDate.getTime())
                .build();
        scheduleDate.setDate(startDate);
    }

    @Override
    public void onTextValueEntered(int requestCode, String name) {
        switch (requestCode) {
            case JOB_NAME_CODE:
                updateJobName(name);
                break;
            case FILE_NAME_CODE:
                updateFileName(name);
                break;
            case OUTPUT_PATH_CODE:
                updateOutputPath(name);
                break;
        }
    }

    private void updateOutputPath(String path) {
        form = form.newBuilder()
                .withOutputPath(path)
                .build();
        outputPath.setText(path);
    }

    private void updateFileName(String fileName) {
        form = form.newBuilder()
                .withFileName(fileName)
                .build();
        this.fileName.setText(fileName);
    }

    private void updateJobName(String name) {
        form = form.newBuilder()
                .withName(name)
                .build();
        jobName.setText(name);
    }

    @Override
    public void onOutputFormatSelected(List<JobFormViewEntity.OutputFormat> newFormats) {
        form = form.newBuilder()
                .withOutputFormats(newFormats)
                .build();
        outputFormat.setText(form.getSupportedFormatsTitles());
    }

    private class ScheduleDateClickListener implements DateTimeView.DateTimeClickListener {
        @Override
        public void onDateClick(int position) {
            DateDialogFragment.createBuilder(mFragmentManager)
                    .setInputControlId(null)
                    .setDate(form.getStartDate())
                    .setType(DateDialogFragment.DATE)
                    .setTargetFragment(mParentFragment)
                    .show();
        }

        @Override
        public void onTimeClick(int position) {
            DateDialogFragment.createBuilder(mFragmentManager)
                    .setInputControlId(null)
                    .setDate(form.getStartDate())
                    .setType(DateDialogFragment.TIME)
                    .setTargetFragment(mParentFragment)
                    .show();
        }

        @Override
        public void onClear(int position) {
            form = form.newBuilder()
                    .withStartDate(null)
                    .build();
            scheduleDate.setDate(null);
        }
    }
}
