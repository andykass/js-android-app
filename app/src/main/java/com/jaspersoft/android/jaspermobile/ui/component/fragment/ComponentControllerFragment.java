package com.jaspersoft.android.jaspermobile.ui.component.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jaspersoft.android.jaspermobile.ui.component.ComponentCache;
import com.jaspersoft.android.jaspermobile.ui.component.ComponentControllerDelegate;
import com.jaspersoft.android.jaspermobile.ui.component.ComponentFactory;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;

public abstract class ComponentControllerFragment<C> extends BaseFragment {
    private ComponentCache componentCache;
    private ComponentControllerDelegate<C> componentDelegate = new ComponentControllerDelegate<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ComponentCache) {
            componentCache = (ComponentCache) context;
        } else {
            throw new RuntimeException(getClass().getSimpleName() + " must be attached to " +
                    "an Activity that implements " + ComponentCache.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        componentDelegate.onCreate(componentCache, savedInstanceState, componentFactory);
    }

    @Override
    public void onResume() {
        super.onResume();
        componentDelegate.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        componentDelegate.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
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