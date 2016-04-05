package com.jaspersoft.android.jaspermobile.ui.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.jaspersoft.android.jaspermobile.GraphObject;
import com.jaspersoft.android.jaspermobile.internal.di.HasComponent;
import com.jaspersoft.android.jaspermobile.internal.di.components.AppComponent;
import com.jaspersoft.android.jaspermobile.internal.di.components.BaseActivityComponent;
import com.jaspersoft.android.jaspermobile.internal.di.components.ProfileComponent;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public enum ComponentProviderDelegate {
    INSTANCE;

    @SuppressWarnings("unchecked")
    public <C> C getComponent(Activity activity, Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) activity).getComponent());
    }

    public AppComponent getAppComponent(Context context) {
        GraphObject graphObject = GraphObject.Factory.from(context);
        return graphObject.getComponent();
    }

    public ProfileComponent getProfileComponent(Context context) {
        GraphObject graphObject = GraphObject.Factory.from(context);
        return graphObject.getProfileComponent();
    }

    public BaseActivityComponent getBaseActivityComponent(FragmentActivity activity) {
        return getProfileComponent(activity)
                .plusBase(new ActivityModule(activity));
    }
}
