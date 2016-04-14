package com.jaspersoft.android.jaspermobile.domain.entity.job;

import com.jaspersoft.android.jaspermobile.domain.entity.Resource;

import java.util.Date;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class JobResource extends Resource {
    public final static int NORMAL = 1;
    public final static int EXECUTING = 2;
    public final static int COMPLETE = 3;
    public final static int PAUSED = 4;
    public final static int ERROR = 5;
    public final static int UNKNOWN = 6;

    private final int mId;
    private final Date mPreviousFireDate;
    private final Date mFireDate;
    private final int mState;
    private final String mDescription;
    private final JobTarget mJobTarget;
    private final String mOwner;

    public JobResource(String label, int id, Date previousFireDate, Date fireDate, int state, String description, JobTarget jobTarget, String owner) {
        super(label);

        this.mId = id;
        mPreviousFireDate = previousFireDate;
        this.mFireDate = fireDate;
        this.mState = state;
        mDescription = description;
        mJobTarget = jobTarget;
        mOwner = owner;
    }

    @Override
    public int getId() {
        return mId;
    }

    public Date getPreviousFireDate() {
        return mPreviousFireDate;
    }

    public Date getFireDate() {
        return mFireDate;
    }

    public int getState() {
        return mState;
    }

    public String getDescription() {
        return mDescription;
    }

    public JobTarget getJobTarget() {
        return mJobTarget;
    }

    public String getOwner() {
        return mOwner;
    }
}
