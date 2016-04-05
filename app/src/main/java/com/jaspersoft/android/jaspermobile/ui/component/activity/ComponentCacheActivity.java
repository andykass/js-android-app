package com.jaspersoft.android.jaspermobile.ui.component.activity;

import android.os.Bundle;

import com.jaspersoft.android.jaspermobile.ui.component.ComponentCache;
import com.jaspersoft.android.jaspermobile.ui.component.ComponentCacheDelegate;
import com.jaspersoft.android.jaspermobile.ui.view.activity.BaseActivity;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;

public class ComponentCacheActivity extends ToolbarActivity implements ComponentCache {
    private ComponentCacheDelegate delegate = new ComponentCacheDelegate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        delegate.onCreate(savedInstanceState, getLastCustomNonConfigurationInstance());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        delegate.onSaveInstanceState(outState);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return delegate.onRetainCustomNonConfigurationInstance();
    }

    @Override
    public long generateId() {
        return delegate.generateId();
    }

    @Override
    public final <C> C getComponent(long index) {
        return delegate.getComponent(index);
    }

    @Override
    public void setComponent(long index, Object component) {
        delegate.setComponent(index, component);
    }
}
