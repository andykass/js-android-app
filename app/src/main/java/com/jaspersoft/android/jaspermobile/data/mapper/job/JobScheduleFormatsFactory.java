package com.jaspersoft.android.jaspermobile.data.mapper.job;


import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;

import java.util.Arrays;
import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.5
 */
class JobScheduleFormatsFactory {
    List<JobScheduleForm.OutputFormat> generate() {
        return Arrays.asList(JobScheduleForm.OutputFormat.values());
    }
}
