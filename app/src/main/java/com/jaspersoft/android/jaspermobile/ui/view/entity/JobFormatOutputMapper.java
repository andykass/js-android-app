package com.jaspersoft.android.jaspermobile.ui.view.entity;

import android.content.Context;
import android.support.annotation.StringRes;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.sdk.service.data.schedule.JobOutputFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobFormatOutputMapper {

    private final Context mContext;

    @Inject
    public JobFormatOutputMapper(@ApplicationContext Context context) {
        mContext = context;
    }

    public List<JobOutputFormat> toDataType(List<JobFormViewEntity.OutputFormat> outputFormats) {
        List<JobOutputFormat> formats = new ArrayList<>(outputFormats.size());
        for (JobFormViewEntity.OutputFormat outputFormat : outputFormats) {
            formats.add(JobOutputFormat.valueOf(outputFormat.mRawType));
        }
        return formats;
    }

    public List<JobFormViewEntity.OutputFormat> toUiType(Collection<JobOutputFormat> outputFormats) {
        List<JobFormViewEntity.OutputFormat> formats = new ArrayList<>(outputFormats.size());
        for (JobOutputFormat outputFormat : outputFormats) {
            JobFormViewEntity.OutputFormat fornmat = new JobFormViewEntity.OutputFormat(outputFormat.name(), formatToString(outputFormat));
            formats.add(fornmat);
        }
        return formats;
    }

    public String formatToString(JobOutputFormat jobOutputFormat) {
        switch (jobOutputFormat) {
            case XLS_NOPAG:
                return JobOutputFormat.XLS.name();
            case XLSX_NOPAG:
                return JobOutputFormat.XLSX.name();
            case XLS:
                return getString(R.string.sch_format_paginated, jobOutputFormat.name());
            case XLSX:
                return getString(R.string.sch_format_paginated, jobOutputFormat.name());
            default:
                return jobOutputFormat.name();
        }
    }

    private String getString(@StringRes int id, Object... args) {
        return mContext.getResources().getString(id, args);
    }
}
