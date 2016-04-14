package com.jaspersoft.android.jaspermobile.data.mapper.job;


import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;

import java.util.Arrays;
import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.5
 */
class JobScheduleIntervalUnitFactory {
    List<JobSimpleRecurrence.Unit> generate() {
        return Arrays.asList(JobSimpleRecurrence.Unit.values());
    }
}
