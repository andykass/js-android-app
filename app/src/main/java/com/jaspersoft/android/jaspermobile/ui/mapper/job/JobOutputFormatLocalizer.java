package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;
import com.jaspersoft.android.sdk.service.data.schedule.JobOutputFormat;

/**
 * @author Tom Koptel
 * @since 2.5
 */
final class JobOutputFormatLocalizer implements EntityLocalizer<JobScheduleForm.OutputFormat> {

    private final Context context;

    public JobOutputFormatLocalizer(@ApplicationContext Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public String localize(@NonNull JobScheduleForm.OutputFormat jobOutputFormat) {
        switch (jobOutputFormat) {
            case XLS_NOPAG:
                return JobOutputFormat.XLS.name();
            case XLSX_NOPAG:
                return JobOutputFormat.XLSX.name();
            case XLS:
                return context.getString(R.string.sch_format_paginated, jobOutputFormat.name());
            case XLSX:
                return context.getString(R.string.sch_format_paginated, jobOutputFormat.name());
            default:
                return jobOutputFormat.name();
        }
    }
}
