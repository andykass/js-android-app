package com.jaspersoft.android.jaspermobile.domain.entity;

import java.io.Serializable;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public abstract class Resource implements Serializable{
    private String mLabel;

    public Resource(String mLabel) {
        this.mLabel = mLabel;
    }

    public abstract int getId();

    public String getLabel() {
        return mLabel;
    }
}
