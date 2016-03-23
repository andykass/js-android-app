package com.jaspersoft.android.jaspermobile.ui.view.activity;

import android.os.Bundle;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.report.ReportCastActivity_;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.ReportVisualizeFragment;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.ReportVisualizeFragment_;
import com.jaspersoft.android.jaspermobile.util.ScrollableTitleHelper;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@EActivity(R.layout.report_viewer_layout)
public class ReportVisualizeActivity extends CastActivity {
    @Extra
    protected ResourceLookup resource;
    @Bean
    protected ScrollableTitleHelper scrollableTitleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scrollableTitleHelper.injectTitle(resource.getLabel());

        if (savedInstanceState == null) {
            ReportVisualizeFragment viewFragment = ReportVisualizeFragment_.builder()
                    .resource(resource)
                    .build();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.control, viewFragment, ReportVisualizeFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected void onCastStarted() {
        ReportCastActivity_.intent(this)
                .resource(resource)
                .start();

        finish();
    }
}
