package com.jaspersoft.android.jaspermobile.domain.repository.schedule;

import com.jaspersoft.android.jaspermobile.domain.repository.Specification;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public final class ScheduleSpecification implements Specification {
    private final int mId;

    public ScheduleSpecification(int id) {
        mId = id;
    }

    public int toId() {
        return mId;
    }
}
