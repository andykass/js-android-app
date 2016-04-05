package com.jaspersoft.android.jaspermobile.domain.entity;

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

    private int mId;
    private Date mFireDate;
    private int mState;

    public JobResource(String label, int id, Date fireDate, int state) {
        super(label);

        this.mId = id;
        this.mFireDate = fireDate;
        this.mState = state;
    }

    @Override
    public int getId() {
        return mId;
    }

    public Date getFireDate() {
        return mFireDate;
    }

    public int getState() {
        return mState;
    }
}
