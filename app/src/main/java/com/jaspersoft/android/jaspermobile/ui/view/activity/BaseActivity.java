package com.jaspersoft.android.jaspermobile.ui.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.jaspersoft.android.jaspermobile.GraphObject;
import com.jaspersoft.android.jaspermobile.internal.di.components.AppComponent;
import com.jaspersoft.android.jaspermobile.internal.di.components.BaseActivityComponent;
import com.jaspersoft.android.jaspermobile.internal.di.components.ProfileComponent;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.ComponentProviderDelegate;
import com.jaspersoft.android.jaspermobile.util.DefaultPrefHelper_;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toggleScreenCapturing();
    }

    @NonNull
    protected AppComponent getAppComponent() {
        return GraphObject.Factory.from(this).getComponent();
    }

    @NonNull
    protected ProfileComponent getProfileComponent() {
        return ComponentProviderDelegate.INSTANCE.getProfileComponent(this);
    }

    @NonNull
    protected BaseActivityComponent getBaseActivityComponent() {
        return ComponentProviderDelegate.INSTANCE.getBaseActivityComponent(this);
    }

    private void toggleScreenCapturing(){
        boolean isScreenCaptureEnable = DefaultPrefHelper_.getInstance_(this).isScreenCapturingEnabled();

        if (!isScreenCaptureEnable) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }
    }
}
