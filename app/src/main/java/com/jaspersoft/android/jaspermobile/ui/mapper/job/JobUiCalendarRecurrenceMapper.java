package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobCalendarRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.ui.entity.job.CalendarViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewEntity;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiCollectionEntityMapper;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiEntityMapper;

import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.5
 */
final class JobUiCalendarRecurrenceMapper implements UiEntityMapper<JobScheduleForm.Recurrence, JobFormViewEntity.Recurrence> {
    @NonNull
    private final EntityLocalizer<JobScheduleForm.Recurrence> entityLocalizer;
    @NonNull
    private final UiCollectionEntityMapper<Integer, CalendarViewRecurrence.Month> monthMapper;
    @NonNull
    private final UiCollectionEntityMapper<Integer, CalendarViewRecurrence.Day> dayMapper;

    @VisibleForTesting
    JobUiCalendarRecurrenceMapper(
            @NonNull EntityLocalizer<JobScheduleForm.Recurrence> entityLocalizer,
            @NonNull UiCollectionEntityMapper<Integer, CalendarViewRecurrence.Month> monthMapper,
            @NonNull UiCollectionEntityMapper<Integer, CalendarViewRecurrence.Day> dayMapper
    ) {
        this.entityLocalizer = entityLocalizer;
        this.monthMapper = monthMapper;
        this.dayMapper = dayMapper;
    }

    @NonNull
    public static JobUiCalendarRecurrenceMapper create(@NonNull Context context) {
        RecurrenceEntityLocalizer entityLocalizer = new RecurrenceEntityLocalizer(context);
        JobUiCalendarMonthMapper monthMapper = JobUiCalendarMonthMapper.create();
        JobUiCalendarDayMapper dayMapper = JobUiCalendarDayMapper.create();

        return new JobUiCalendarRecurrenceMapper(entityLocalizer, monthMapper, dayMapper);
    }

    @NonNull
    @Override
    public JobFormViewEntity.Recurrence toUiEntity(@NonNull JobScheduleForm.Recurrence domainEntity) {
        JobCalendarRecurrence domainRecurrence = (JobCalendarRecurrence) domainEntity;

        CalendarViewRecurrence.Builder builder = CalendarViewRecurrence.builder();
        String localizedLabel = entityLocalizer.localize(domainEntity);
        builder.localizedLabel(localizedLabel);

        List<CalendarViewRecurrence.Month> months = monthMapper.toUiEntityList(domainRecurrence.months());
        builder.months(months);

        List<Integer> daysInWeek = domainRecurrence.daysInWeek();
        if (daysInWeek != null) {
            List<CalendarViewRecurrence.Day> days = dayMapper.toUiEntityList(daysInWeek);
            builder.daysInWeek(days);
        }

        String daysInMonth = domainRecurrence.daysInMonth();
        if (daysInMonth != null) {
            builder.daysInMonth(daysInMonth);
        }

        builder.minutes(domainRecurrence.minutes());
        builder.hours(domainRecurrence.hours());
        builder.endDate(domainRecurrence.endDate());

        return builder.build();
    }

    @NonNull
    @Override
    public JobScheduleForm.Recurrence toDomainEntity(@NonNull JobFormViewEntity.Recurrence uiEntity) {
        CalendarViewRecurrence uiRecurrence = (CalendarViewRecurrence) uiEntity;
        JobCalendarRecurrence.Builder builder = JobCalendarRecurrence.builder();

        List<CalendarViewRecurrence.Month> uiMonths = uiRecurrence.months();
        List<Integer> months = monthMapper.toDomainEntityList(uiMonths);
        builder.months(months);

        List<CalendarViewRecurrence.Day> uiDays = uiRecurrence.daysInWeek();
        if (uiDays != null) {
            List<Integer> days = dayMapper.toDomainEntityList(uiDays);
            builder.daysInWeek(days);
        }

        String daysInMonth = uiRecurrence.daysInMonth();
        if (daysInMonth != null) {
            builder.daysInMonth(daysInMonth);
        }

        builder.minutes(uiRecurrence.minutes());
        builder.hours(uiRecurrence.hours());
        builder.endDate(uiRecurrence.endDate());

        return builder.build();
    }
}
