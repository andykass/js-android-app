package com.jaspersoft.android.jaspermobile.domain.entity.job;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public final class JobNoneRecurrence implements JobScheduleForm.Recurrence {
    public static final JobNoneRecurrence INSTANCE = new JobNoneRecurrence();

    private JobNoneRecurrence() {
    }
}
