package com.jaspersoft.android.jaspermobile.data.mapper.job;


import com.jaspersoft.android.jaspermobile.domain.entity.job.JobCalendarRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobNoneRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tom Koptel
 * @since 2.5
 */
class JobScheduleRecurrenceFactory {

    private final Map<Class<?>, RecurrenceFactory> creators;

    JobScheduleRecurrenceFactory() {
        creators = new LinkedHashMap<>();
        creators.put(JobNoneRecurrence.class, new NoneRecurrenceFactory());
        creators.put(JobSimpleRecurrence.class, new SimpleRecurrenceFactory());
        creators.put(JobCalendarRecurrence.class, new CalendarRecurrenceFactory());
    }

    List<JobScheduleForm.Recurrence> generate(JobScheduleForm.Recurrence userRecurrence) {
        List<JobScheduleForm.Recurrence> recurrences = new ArrayList<>(3);
        Class<? extends JobScheduleForm.Recurrence> userType = userRecurrence.getClass();

        for (Map.Entry<Class<?>, RecurrenceFactory> entry : creators.entrySet()) {
            Class<?> type = entry.getKey();
            RecurrenceFactory recurrenceFactory = entry.getValue();

            if (type.isAssignableFrom(userType)) {
                recurrences.add(userRecurrence);
            } else {
                recurrences.add(recurrenceFactory.createDefault());
            }
        }

        return recurrences;
    }

    private static class SimpleRecurrenceFactory implements RecurrenceFactory {
        @Override
        public JobScheduleForm.Recurrence createDefault() {
            return JobSimpleRecurrence.builder()
                    .interval(1)
                    .unit(JobSimpleRecurrence.Unit.DAY)
                    .build();
        }
    }

    private static class CalendarRecurrenceFactory implements RecurrenceFactory {
        @Override
        public JobScheduleForm.Recurrence createDefault() {
            return JobCalendarRecurrence.INSTANCE;
        }
    }

    private static class NoneRecurrenceFactory implements RecurrenceFactory {
        @Override
        public JobScheduleForm.Recurrence createDefault() {
            return JobNoneRecurrence.INSTANCE;
        }
    }

    private interface RecurrenceFactory {
        JobScheduleForm.Recurrence createDefault();
    }
}
