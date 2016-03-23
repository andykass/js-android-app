package com.jaspersoft.android.jaspermobile.ui.view.fragment;

import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.internal.di.components.BaseActivityComponent;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public abstract class BasePreferenceFragment extends PreferenceFragment {
    @SuppressWarnings("unchecked")
    @NonNull
    public <C> C getComponent(Class<C> componentType) {
        return ComponentProviderDelegate.INSTANCE.getComponent(getActivity(), componentType);
    }

    @NonNull
    public BaseActivityComponent getBaseActivityComponent() {
        return ComponentProviderDelegate.INSTANCE.getBaseActivityComponent(getActivity());
    }
}
