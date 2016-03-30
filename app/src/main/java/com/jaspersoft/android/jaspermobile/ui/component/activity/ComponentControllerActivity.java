package com.jaspersoft.android.jaspermobile.ui.component.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jaspersoft.android.jaspermobile.ui.component.ComponentControllerDelegate;
import com.jaspersoft.android.jaspermobile.ui.component.ComponentFactory;

public abstract class ComponentControllerActivity<C> extends ComponentCacheActivity {
    private ComponentControllerDelegate<C> componentDelegate = new ComponentControllerDelegate<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        componentDelegate.onCreate(this, savedInstanceState, componentFactory);
    }

    @Override
    protected void onResume() {
        super.onResume();
        componentDelegate.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        componentDelegate.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        componentDelegate.onDestroy();
    }

    public C getComponent() {
        return componentDelegate.getComponent();
    }

    protected abstract C onCreateNonConfigurationComponent();

    private ComponentFactory<C> componentFactory = new ComponentFactory<C>() {
        @NonNull
        @Override
        public C createComponent() {
            return onCreateNonConfigurationComponent();
        }
    };
}