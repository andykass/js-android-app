package com.jaspersoft.android.jaspermobile.domain.repository.schedule;

import com.jaspersoft.android.sdk.service.data.schedule.JobData;
import com.jaspersoft.android.sdk.service.data.schedule.JobForm;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public interface ScheduleRepository {
    JobForm readForm(int id) throws Exception;
    JobData createForm(JobForm form) throws Exception;
    JobForm updateForm(int jobId, JobForm form) throws Exception;
}
