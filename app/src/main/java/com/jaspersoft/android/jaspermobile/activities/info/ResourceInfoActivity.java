package com.jaspersoft.android.jaspermobile.activities.info;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.info.fragments.FileInfoFragment_;
import com.jaspersoft.android.jaspermobile.activities.info.fragments.ReportInfoFragment_;
import com.jaspersoft.android.jaspermobile.activities.info.fragments.ResourceInfoFragment;
import com.jaspersoft.android.jaspermobile.activities.info.fragments.ResourceInfoFragment_;
import com.jaspersoft.android.jaspermobile.activities.info.fragments.SavedItemInfoFragment_;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * @author Andrew Tivodar
 * @since 2.2
 */
@EActivity
public class ResourceInfoActivity extends ToolbarActivity {

    @Extra
    protected JasperResource jasperResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment resourceInfoFragment = getSupportFragmentManager().findFragmentByTag(ResourceInfoFragment.TAG);

        if (resourceInfoFragment == null) {
            switch (jasperResource.getResourceType()) {
                case report:
                    resourceInfoFragment = ReportInfoFragment_.builder()
                            .jasperResource(jasperResource)
                            .build();
                    break;
                case file:
                    resourceInfoFragment = FileInfoFragment_.builder()
                            .jasperResource(jasperResource)
                            .build();
                    break;
                case saved_item:
                    resourceInfoFragment = SavedItemInfoFragment_.builder()
                            .jasperResource(jasperResource)
                            .build();
                    break;
                default:
                    resourceInfoFragment = ResourceInfoFragment_.builder()
                            .jasperResource(jasperResource)
                            .build();
            }
        }
        commitContent(resourceInfoFragment, ResourceInfoFragment.TAG);
    }

    @Override
    protected String getScreenName() {
        return getString(R.string.ja_ri_s);
    }

    private void commitContent(@NonNull Fragment directFragment, String fragmentTag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction
                .replace(android.R.id.content, directFragment, fragmentTag)
                .commit();
    }

}
