package com.jaspersoft.android.jaspermobile.ui.component;

public interface ComponentCache {
    long generateId();

    <C> C getComponent(long index);

    <C> void setComponent(long index, C component);
}
