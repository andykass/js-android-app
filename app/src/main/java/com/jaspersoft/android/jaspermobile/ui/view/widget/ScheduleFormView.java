package com.jaspersoft.android.jaspermobile.ui.view.widget;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.dialog.CalendarDayDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.CalendarMonthDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.DateDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.IntervalUnitDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.NumberDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.OutputFormatDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.RecurrenceTypeDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.ValueInputDialogFragment;
import com.jaspersoft.android.jaspermobile.ui.contract.ScheduleFormContract;
import com.jaspersoft.android.jaspermobile.ui.entity.job.CalendarViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewBundle;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewEntity;
import com.jaspersoft.android.jaspermobile.ui.entity.job.NoneViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.SimpleViewRecurrence;
import com.jaspersoft.android.jaspermobile.widget.DateTimeView;

import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@EViewGroup(R.layout.view_schedule_form)
public class ScheduleFormView extends LinearLayout implements
        DateDialogFragment.IcDateDialogClickListener,
        OutputFormatDialogFragment.OutputFormatClickListener,
        ValueInputDialogFragment.ValueDialogCallback,
        NumberDialogFragment.NumberDialogClickListener,
        RecurrenceTypeDialogFragment.RecurrenceTypeClickListener,
        IntervalUnitDialogFragment.IntervalUnitClickListener,
        CalendarMonthDialogFragment.MonthsSelectedListener,
        CalendarDayDialogFragment.DaysSelectedListener
{

    public final static int JOB_NAME_CODE = 563;
    public final static int FILE_NAME_CODE = 251;
    public final static int OUTPUT_PATH_CODE = 515;
    public final static int REPEAT_INTERVAL_COUNT_CODE = 517;
    public final static int REPEAT_OCCURRENCE_COUNT_CODE = 518;
    public final static int START_DATE_CHANGED_CODE = 519;
    public final static int END_DATE_CHANGED_CODE = 520;
    public final static int DAYS_IN_MONTH_CODE = 521;
    public final static int MINUTES_CODE = 522;
    public final static int HOURS_CODE = 523;

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

    @ViewById(R.id.recurrenceType)
    TextView recurrenceType;
    @ViewById(R.id.repeatInterval)
    TextView repeatInterval;
    @ViewById(R.id.repeatUnit)
    TextView repeatUnit;
    @ViewById(R.id.scheduleEndGroup)
    RadioGroup scheduleEndGroup;
    @ViewById(R.id.numberOfRunsControl)
    View numberOfRunsControl;
    @ViewById(R.id.numberOfRuns)
    TextView numberOfRuns;
    @ViewById(R.id.runUntilDateControl)
    DateTimeView runUntilDateControl;
    @ViewById(R.id.simpleRecurrenceForm)
    View simpleRecurrenceForm;

    @ViewById(R.id.calendarRecurrenceForm)
    View calendarRecurrenceForm;
    @ViewById(R.id.selectedMonths)
    TextView selectedMonths;
    @ViewById(R.id.selectedDaysInWeek)
    TextView selectedDaysInWeek;
    @ViewById(R.id.selectedDaysInMonth)
    TextView selectedDaysInMonth;
    @ViewById(R.id.selectedHours)
    TextView selectedHours;
    @ViewById(R.id.selectedMinutes)
    TextView selectedMinutes;
    @ViewById(R.id.selectDaysControl)
    RadioGroup selectDaysControl;
    @ViewById(R.id.calendarEndDate)
    DateTimeView calendarEndDate;

    private JobFormViewBundle formBundle;
    private JobFormViewEntity form;

    private final WidgetStateDelegate<JobFormViewBundle> stateDelegate =
            new WidgetStateDelegate<JobFormViewBundle>(JobFormViewBundle.class) {
                @Override
                public JobFormViewBundle provideState() {
                    return formBundle.newBuilder()
                            .form(form)
                            .build();
                }
            };

    public ScheduleFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void showForm(JobFormViewBundle bundle) {
        this.form = bundle.form();
        this.formBundle = bundle;
        update(form);
    }

    @Nullable
    public JobFormViewBundle provideForm() {
        if (formBundle == null) {
            return null;
        }
        return formBundle.newBuilder()
                .form(form)
                .build();
    }

    private void update(JobFormViewEntity form) {
        jobName.setText(form.jobName());
        fileName.setText(form.fileName());

        runImmediatelyTitle.setText(getString(R.string.sch_run_immediately));
        checkBoxCheckedChange(!form.hasStartDate());

        scheduleDate.setDate(form.startDateAsCalendar());
        scheduleDate.setLabel(getString(R.string.sch_start_date));
        scheduleDate.setClearButtonVisibility(false);
        scheduleDate.setDateTimeClickListener(new ScheduleDateClickListener());

        outputFormat.setText(form.outputFormatsAsString());
        outputPath.setText(form.folderUri());

        updateRecurrenceForm(form.recurrence());
    }

    private void updateRecurrenceForm(JobFormViewEntity.Recurrence recurrence) {
        recurrenceType.setText(recurrence.localizedLabel());

        if (recurrence instanceof NoneViewRecurrence) {
            handleNoneRecurrence();
        }
        if (recurrence instanceof SimpleViewRecurrence) {
            SimpleViewRecurrence simpleRecurrence = (SimpleViewRecurrence) recurrence;
            handleSimpleRecurrence(simpleRecurrence);
        }
        if (recurrence instanceof CalendarViewRecurrence) {
            CalendarViewRecurrence calendarRecurrence = (CalendarViewRecurrence) recurrence;
            handleCalendarRecurrence(calendarRecurrence);
        }
    }

    private void handleCalendarRecurrence(CalendarViewRecurrence calendarRecurrence) {
        simpleRecurrenceForm.setVisibility(GONE);
        calendarRecurrenceForm.setVisibility(VISIBLE);

        calendarEndDate.setDateTimeClickListener(new CalendarEndDateListener());
        calendarEndDate.setLabel(getString(R.string.sch_end_date));
        calendarEndDate.setPreview(getString(R.string.empty_text_place_holder));

        selectedMonths.setText(calendarRecurrence.monthsAsString());
        if (calendarRecurrence.daysInWeek() != null) {
            selectDaysControl.check(R.id.selectDays);
            selectedDaysInWeek.setText(calendarRecurrence.daysInWeekAsString());
        }
        if (calendarRecurrence.daysInMonth() != null) {
            selectDaysControl.check(R.id.selectDaysInMonth);
            selectedDaysInMonth.setText(calendarRecurrence.daysInMonth());
        }
        selectedHours.setText(calendarRecurrence.hours());
        selectedMinutes.setText(calendarRecurrence.minutes());
    }

    private void handleSimpleRecurrence(SimpleViewRecurrence recurrence) {
        calendarRecurrenceForm.setVisibility(GONE);
        simpleRecurrenceForm.setVisibility(VISIBLE);

        repeatInterval.setText(recurrence.intervalAsString());
        repeatUnit.setText(recurrence.unit().toString());

        runUntilDateControl.setDateTimeClickListener(new TriggerEndDateListener());
        runUntilDateControl.setLabel(getString(R.string.sch_end_date));
        runUntilDateControl.setPreview(getString(R.string.empty_text_place_holder));

        if (recurrence.runsIndefinitely()) {
            scheduleEndGroup.check(R.id.runsIndefinitelyControl);
        } else if (recurrence.runsByOccurrences()) {
            scheduleEndGroup.check(R.id.runsOccurrenceControl);
            numberOfRuns.setText(recurrence.occurrenceAsString());
        } else if (recurrence.runsTillDate()) {
            runUntilDateControl.setDate(recurrence.untilDateAsCalendar());
            scheduleEndGroup.check(R.id.runsUntilControl);
        }
    }

    @CheckedChange(R.id.runsIndefinitelyControl)
    void onIndefinitelyChecked(CompoundButton control, boolean checked) {
        if (!checked) return;
        control.requestFocus();
        numberOfRunsControl.setVisibility(GONE);
        runUntilDateControl.setVisibility(GONE);
    }

    @CheckedChange(R.id.runsOccurrenceControl)
    void onOccurrenceChecked(CompoundButton control, boolean checked) {
        if (!checked) return;
        control.requestFocus();
        numberOfRunsControl.setVisibility(VISIBLE);
        runUntilDateControl.setVisibility(GONE);
    }

    @CheckedChange(R.id.runsUntilControl)
    void onUntilChecked(CompoundButton control, boolean checked) {
        if (!checked) return;
        control.requestFocus();
        numberOfRunsControl.setVisibility(GONE);
        runUntilDateControl.setVisibility(VISIBLE);
    }

    @CheckedChange(R.id.selectDays)
    void selectDays(CompoundButton control, boolean checked) {
        if (!checked) return;
        control.requestFocus();
        selectedDaysInWeek.setVisibility(VISIBLE);
        selectedDaysInMonth.setVisibility(GONE);
    }

    @CheckedChange(R.id.selectDaysInMonth)
    void selectDaysInMonth(CompoundButton control, boolean checked) {
        if (!checked) return;
        control.requestFocus();
        selectedDaysInWeek.setVisibility(GONE);
        selectedDaysInMonth.setVisibility(VISIBLE);
    }

    private void handleNoneRecurrence() {
        simpleRecurrenceForm.setVisibility(GONE);
    }

    private String getString(@StringRes int id) {
        return getContext().getResources().getString(id);
    }

    @Click(R.id.runImmediately)
    protected void runImmediatelyClicked() {
        runImmediately.performClick();
    }

    @Click(R.id.selectedDaysInMonth)
    protected void selectedDaysInMonthClick() {
        CalendarViewRecurrence recurrence = getCalendarRecurrence();

        String value = recurrence.daysInMonth();
        value = value == null ? "" : value;

        ValueInputDialogFragment.createBuilder(mFragmentManager)
                .setLabel(getString(R.string.sr_dates_in_month))
                .setValue(value)
                .setRequired(true)
                .setCancelableOnTouchOutside(true)
                .setRequestCode(DAYS_IN_MONTH_CODE)
                .setTargetFragment(mParentFragment)
                .show();
    }

    @Click(R.id.selectHours)
    protected void selectHoursInMonthClick() {
        CalendarViewRecurrence recurrence = getCalendarRecurrence();

        ValueInputDialogFragment.createBuilder(mFragmentManager)
                .setLabel(getString(R.string.sr_hours))
                .setValue(recurrence.hours())
                .setRequired(true)
                .setCancelableOnTouchOutside(true)
                .setRequestCode(HOURS_CODE)
                .setTargetFragment(mParentFragment)
                .show();
    }

    @Click(R.id.selectMinutes)
    protected void selectMinutesInMonthClick() {
        CalendarViewRecurrence recurrence = getCalendarRecurrence();

        ValueInputDialogFragment.createBuilder(mFragmentManager)
                .setLabel(getString(R.string.sr_minutes))
                .setValue(recurrence.minutes())
                .setRequired(true)
                .setCancelableOnTouchOutside(true)
                .setRequestCode(MINUTES_CODE)
                .setTargetFragment(mParentFragment)
                .show();
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
                .setSelected(form.outputFormats())
                .setFormats(formBundle.allFormats())
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

    @Click(R.id.recurrence)
    protected void selectRecurrence() {
        RecurrenceTypeDialogFragment.createBuilder(mFragmentManager)
                .setRecurrence(form.recurrence())
                .setRecurrences(formBundle.allRecurrences())
                .setTargetFragment(mParentFragment)
                .show();
    }

    @Click(R.id.repeatInterval)
    protected void repeatIntervalNumberClicked() {
        SimpleViewRecurrence recurrence = (SimpleViewRecurrence) form.recurrence();

        NumberDialogFragment.createBuilder(mFragmentManager)
                .setValue(recurrence.interval())
                .setRequestCode(REPEAT_INTERVAL_COUNT_CODE)
                .setTargetFragment(mParentFragment)
                .show();
    }

    @Click(R.id.numberOfRunsControl)
    protected void repeatOccurrenceNumberClicked() {
        SimpleViewRecurrence recurrence = (SimpleViewRecurrence) form.recurrence();

        Integer occurrence = recurrence.occurrence();

        NumberDialogFragment.PageDialogFragmentBuilder builder = NumberDialogFragment.createBuilder(mFragmentManager);
        if (occurrence != null) {
            builder.setValue(occurrence);
        }
        builder.setRequestCode(REPEAT_OCCURRENCE_COUNT_CODE);
        builder.setTargetFragment(mParentFragment);
        builder.show();
    }

    @Click(R.id.repeatUnit)
    protected void intervalUnitClicked() {
        SimpleViewRecurrence recurrence = (SimpleViewRecurrence) form.recurrence();

        IntervalUnitDialogFragment.createBuilder(mFragmentManager)
                .setUnit(recurrence.unit())
                .setUnits(formBundle.allIntervalUnits())
                .setTargetFragment(mParentFragment)
                .show();
    }

    @Click(R.id.selectMonths)
    protected void selectMonthsClicked() {
        CalendarViewRecurrence recurrence = getCalendarRecurrence();

        CalendarMonthDialogFragment.createBuilder(mFragmentManager)
                .setMonths(formBundle.allMonths())
                .setSelected(recurrence.months())
                .setTargetFragment(mParentFragment)
                .show();
    }

    @Click(R.id.selectedDaysInWeek)
    protected void selectDaysInWeekClicked() {
        CalendarViewRecurrence recurrence = getCalendarRecurrence();

        List<CalendarViewRecurrence.Day> days = recurrence.daysInWeek();
        days = days == null ? Collections.<CalendarViewRecurrence.Day>emptyList() : days;

        CalendarDayDialogFragment.createBuilder(mFragmentManager)
                .setDays(formBundle.allDays())
                .setSelected(days)
                .setTargetFragment(mParentFragment)
                .show();
    }

    @CheckedChange(R.id.ic_boolean)
    protected void checkBoxCheckedChange(boolean checked) {
        runImmediately.setChecked(checked);
        scheduleDate.setVisibility(checked ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDateSelected(Calendar date, int requestCode, Object... metadata) {
        switch (requestCode) {
            case START_DATE_CHANGED_CODE:
                updateStartDate(date);
                break;
            case END_DATE_CHANGED_CODE:
                updateEndDate(date);
                break;
        }
    }

    private void updateStartDate(Calendar startDate) {
        if (startDate == null) {
            setStartDate(null);
            return;
        }

        if (startDate.getTimeInMillis() < new Date().getTime()) {
            Toast.makeText(getContext(), getString(R.string.error_schedule_start_date_in_the_past), Toast.LENGTH_SHORT).show();
        } else {
            setStartDate(startDate);
        }
    }

    private void setStartDate(Calendar startDate) {
        form = form.newBuilder()
                .startDate(startDate.getTime())
                .build();
        scheduleDate.setDate(startDate);
    }

    private void updateEndDate(Calendar endDate) {
        if (endDate == null) {
            setEndDate(null);
            return;
        }

        if (endDate.getTimeInMillis() < new Date().getTime()) {
            Toast.makeText(getContext(), getString(R.string.error_schedule_end_date_in_the_past), Toast.LENGTH_SHORT).show();
        } else {
            setEndDate(endDate);
        }
    }

    private void setEndDate(Calendar endDate) {
        if (form.recurrence() instanceof SimpleViewRecurrence) {
            setSimpleRecurrenceEndDate(endDate);
        }
        if (form.recurrence() instanceof CalendarViewRecurrence) {
            setCalendarRecurrenceEndDate(endDate);
        }
    }

    private void setCalendarRecurrenceEndDate(@Nullable Calendar date) {
        CalendarViewRecurrence recurrence = getCalendarRecurrence();

        Date endDate = date == null ? null : date.getTime();
        CalendarViewRecurrence modifiedRecurrence = recurrence.newBuilder()
                .endDate(endDate)
                .build();
        updateRecurrence(modifiedRecurrence);
        calendarEndDate.setDate(date);
    }

    private void setSimpleRecurrenceEndDate(@Nullable Calendar date) {
        SimpleViewRecurrence recurrence = (SimpleViewRecurrence) form.recurrence();

        Date endDate = date == null ? null : date.getTime();
        SimpleViewRecurrence modifiedRecurrence = recurrence.newBuilder()
                .untilDate(endDate)
                .build();
        updateRecurrence(modifiedRecurrence);
        runUntilDateControl.setDate(date);
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
            case DAYS_IN_MONTH_CODE:
                updateDaysInMonth(name);
                break;
            case MINUTES_CODE:
                updateMinutes(name);
                break;
            case HOURS_CODE:
                updateHours(name);
                break;
        }
    }

    private void updateOutputPath(String path) {
        form = form.newBuilder()
                .folderUri(path)
                .build();
        outputPath.setText(path);
    }

    private void updateFileName(String fileName) {
        form = form.newBuilder()
                .fileName(fileName)
                .build();
        this.fileName.setText(fileName);
    }

    private void updateJobName(String name) {
        form = form.newBuilder()
                .jobName(name)
                .build();
        jobName.setText(name);
    }

    private void updateDaysInMonth(String days) {
        CalendarViewRecurrence recurrence = getCalendarRecurrence();
        CalendarViewRecurrence updatedRecurrence = recurrence.newBuilder()
                .daysInMonth(days)
                .build();
        updateRecurrence(updatedRecurrence);
        selectedDaysInMonth.setText(days);
    }

    private void updateMinutes(String minutes) {
        CalendarViewRecurrence recurrence = getCalendarRecurrence();
        CalendarViewRecurrence updatedRecurrence = recurrence.newBuilder()
                .minutes(minutes)
                .build();
        updateRecurrence(updatedRecurrence);
        selectedMinutes.setText(minutes);
    }

    private void updateHours(String hours) {
        CalendarViewRecurrence recurrence = getCalendarRecurrence();
        CalendarViewRecurrence updatedRecurrence = recurrence.newBuilder()
                .hours(hours)
                .build();
        updateRecurrence(updatedRecurrence);
        selectedHours.setText(hours);
    }

    private void updateRecurrence(JobFormViewEntity.Recurrence recurrence) {
        form = form.newBuilder()
                .recurrence(recurrence)
                .build();
    }

    @NonNull
    private CalendarViewRecurrence getCalendarRecurrence() {
        return (CalendarViewRecurrence) form.recurrence();
    }

    @Override
    public void onOutputFormatSelected(List<JobFormViewEntity.OutputFormat> newFormats) {
        form = form.newBuilder()
                .outputFormats(newFormats)
                .build();
        outputFormat.setText(form.outputFormatsAsString());
    }

    @Override
    public void onRecurrenceSelected(JobFormViewEntity.Recurrence recurrence) {
        updateRecurrence(recurrence);
        updateRecurrenceForm(recurrence);
    }

    @Override
    public void onNumberSubmit(int number, int requestCode) {
        switch (requestCode) {
            case REPEAT_INTERVAL_COUNT_CODE:
                handleRepeatDayNumberChange(number);
                break;
            case REPEAT_OCCURRENCE_COUNT_CODE:
                handleOccurrenceCountNumberChange(number);
                break;
        }
    }

    private void handleRepeatDayNumberChange(int intervalCount) {
        SimpleViewRecurrence recurrence = (SimpleViewRecurrence) form.recurrence();
        SimpleViewRecurrence modifiedRecurrence = recurrence.newBuilder()
                .interval(intervalCount)
                .build();
        form = form.newBuilder()
                .recurrence(modifiedRecurrence)
                .build();
        repeatInterval.setText(modifiedRecurrence.intervalAsString());
    }

    private void handleOccurrenceCountNumberChange(int occurrenceCount) {
        SimpleViewRecurrence recurrence = (SimpleViewRecurrence) form.recurrence();
        SimpleViewRecurrence modifiedRecurrence = recurrence.newBuilder()
                .occurrence(occurrenceCount)
                .build();
        form = form.newBuilder()
                .recurrence(modifiedRecurrence)
                .build();
        numberOfRuns.setText(modifiedRecurrence.occurrenceAsString());
    }

    @Override
    public void onUnitSelected(SimpleViewRecurrence.Unit unit) {
        SimpleViewRecurrence recurrence = (SimpleViewRecurrence) form.recurrence();
        SimpleViewRecurrence modifiedRecurrence = recurrence.newBuilder()
                .unit(unit)
                .build();
        form = form.newBuilder()
                .recurrence(modifiedRecurrence)
                .build();

        repeatUnit.setText(unit.toString());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        JobFormViewBundle state = stateDelegate.retrieveState();
        if (state == null) {
            mEventListener.onViewReady();
        } else {
            showForm(state);
        }
    }

    @Override
    public void onMonthsSelected(List<CalendarViewRecurrence.Month> months) {
        CalendarViewRecurrence recurrence = getCalendarRecurrence();
        CalendarViewRecurrence updatedRecurrence = recurrence.newBuilder()
                .months(months)
                .build();
        updateRecurrence(updatedRecurrence);
        selectedMonths.setText(updatedRecurrence.monthsAsString());
    }

    @Override
    public void onDaysSelected(List<CalendarViewRecurrence.Day> selectedDays) {
        CalendarViewRecurrence recurrence = getCalendarRecurrence();
        CalendarViewRecurrence updatedRecurrence = recurrence.newBuilder()
                .daysInWeek(selectedDays)
                .build();
        updateRecurrence(updatedRecurrence);
        selectedDaysInWeek.setText(updatedRecurrence.daysInWeekAsString());
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        return stateDelegate.onSaveInstanceState(parcelable);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Parcelable delegateState = stateDelegate.onRestoreInstanceState(state);
        super.onRestoreInstanceState(delegateState);
    }

    private class ScheduleDateClickListener implements DateTimeView.DateTimeClickListener {
        @Override
        public void onDateClick(int position) {
            DateDialogFragment.createBuilder(mFragmentManager)
                    .setInputControlId(null)
                    .setDate(form.startDateAsCalendar())
                    .setType(DateDialogFragment.DATE)
                    .setTargetFragment(mParentFragment)
                    .setRequestCode(START_DATE_CHANGED_CODE)
                    .show();
        }

        @Override
        public void onTimeClick(int position) {
            DateDialogFragment.createBuilder(mFragmentManager)
                    .setInputControlId(null)
                    .setDate(form.startDateAsCalendar())
                    .setType(DateDialogFragment.TIME)
                    .setTargetFragment(mParentFragment)
                    .setRequestCode(START_DATE_CHANGED_CODE)
                    .show();
        }

        @Override
        public void onClear(int position) {
            form = form.newBuilder()
                    .startDate(null)
                    .build();
            scheduleDate.setDate(null);
        }
    }

    private class TriggerEndDateListener implements DateTimeView.DateTimeClickListener {
        @Override
        public void onDateClick(int position) {
            SimpleViewRecurrence recurrence = (SimpleViewRecurrence) form.recurrence();
            DateDialogFragment.createBuilder(mFragmentManager)
                    .setInputControlId(null)
                    .setDate(recurrence.untilDateAsCalendar())
                    .setType(DateDialogFragment.DATE)
                    .setTargetFragment(mParentFragment)
                    .setRequestCode(END_DATE_CHANGED_CODE)
                    .show();
        }

        @Override
        public void onTimeClick(int position) {
            SimpleViewRecurrence recurrence = (SimpleViewRecurrence) form.recurrence();
            DateDialogFragment.createBuilder(mFragmentManager)
                    .setInputControlId(null)
                    .setDate(recurrence.untilDateAsCalendar())
                    .setType(DateDialogFragment.TIME)
                    .setTargetFragment(mParentFragment)
                    .setRequestCode(END_DATE_CHANGED_CODE)
                    .show();
        }

        @Override
        public void onClear(int position) {
            updateEndDate(null);
        }
    }

    private class CalendarEndDateListener implements DateTimeView.DateTimeClickListener {
        @Override
        public void onDateClick(int position) {
            CalendarViewRecurrence recurrence = getCalendarRecurrence();
            DateDialogFragment.createBuilder(mFragmentManager)
                    .setInputControlId(null)
                    .setDate(recurrence.endDateAsCalendar())
                    .setType(DateDialogFragment.DATE)
                    .setTargetFragment(mParentFragment)
                    .setRequestCode(END_DATE_CHANGED_CODE)
                    .show();
        }

        @Override
        public void onTimeClick(int position) {
            CalendarViewRecurrence recurrence = getCalendarRecurrence();
            DateDialogFragment.createBuilder(mFragmentManager)
                    .setInputControlId(null)
                    .setDate(recurrence.endDateAsCalendar())
                    .setType(DateDialogFragment.TIME)
                    .setTargetFragment(mParentFragment)
                    .setRequestCode(END_DATE_CHANGED_CODE)
                    .show();
        }

        @Override
        public void onClear(int position) {
            updateEndDate(null);
        }
    }
}
