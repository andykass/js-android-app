package com.jaspersoft.android.jaspermobile.domain.request;

import android.support.annotation.NonNull;

import com.jaspersoft.android.sdk.service.data.schedule.JobForm;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class UpdateJobRequest {
    private final int mJobId;
    @NonNull
    private final JobForm mJobForm;

    public UpdateJobRequest(int id, @NonNull JobForm jobForm) {
        mJobId = id;
        mJobForm = jobForm;
    }

    @NonNull
    public JobForm getJobForm() {
        return mJobForm;
    }

    public int getJobId() {
        return mJobId;
    }
}
