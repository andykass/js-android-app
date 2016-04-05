package com.jaspersoft.android.jaspermobile.activities.info.fragments;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.save.SaveReportActivity_;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.ReportOptionsFragmentDialog;
import com.jaspersoft.android.jaspermobile.domain.LoadOptionParamsRequest;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.view.activity.schedule.NewScheduleActivity_;
import com.jaspersoft.android.jaspermobile.util.ResourceOpener;
import com.jaspersoft.android.sdk.service.data.report.option.ReportOption;
import com.jaspersoft.android.sdk.util.FileUtils;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;

import java.util.Set;

import rx.Subscriber;

/**
 * @author Andrew Tivodar
 * @since 2.2
 */
@OptionsMenu({R.menu.report_schedule, R.menu.save_item_menu, R.menu.report_options_menu})
@EFragment(R.layout.fragment_resource_info)
public class ReportInfoFragment extends ResourceInfoFragment
        implements ReportOptionsFragmentDialog.ReportOptionsDialogClickListener {

    @Bean
    protected ResourceOpener resourceOpener;

    @OptionsMenuItem(R.id.showReportOptions)
    protected MenuItem reportOptions;

    @OptionsMenuItem(R.id.saveAction)
    protected MenuItem saveOptions;

    private boolean mReportOptionsExist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestReportOptions();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        reportOptions.setVisible(mReportOptionsExist && mResourceLookup != null);
        saveOptions.setVisible(mResourceLookup != null);
    }

    @OptionsItem(R.id.saveAction)
    protected void saveReport() {
        if (FileUtils.isExternalStorageWritable()) {
            SaveReportActivity_.intent(this)
                    .resource(mResourceLookup)
                    .start();
        } else {
            Toast.makeText(getActivity(),
                    R.string.rv_t_external_storage_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @OptionsItem(R.id.showReportOptions)
    protected void showReportOptions() {
        ReportOptionsFragmentDialog.createBuilder(getFragmentManager())
                .setReportUri(jasperResource.getId())
                .setCancelableOnTouchOutside(true)
                .setTargetFragment(this)
                .show();
    }

    @OptionsItem(R.id.newSchedule)
    protected void schedule() {
        NewScheduleActivity_.intent(getActivity())
                .jasperResource(jasperResource)
                .start();
    }

    @Override
    public void onOptionSelected(ReportOption reportOption) {
        LoadOptionParamsRequest request = new LoadOptionParamsRequest(
                reportOption.getUri(),
                jasperResource.getId()
        );
        mLoadControlsForOptionCase.execute(request, new GetInputControlsListener());
        analytics.sendEvent(Analytics.EventCategory.RESOURCE.getValue(), Analytics.EventAction.OPENED.getValue(), Analytics.EventLabel.WITH_RO.getValue());
    }

    private void showProgressDialog() {
        ProgressDialogFragment.builder(getFragmentManager())
                .setLoadingMessage(R.string.loading_msg)
                .show();
    }

    private void hideProgressDialog() {
        if (ProgressDialogFragment.isVisible(getFragmentManager())) {
            ProgressDialogFragment.dismiss(getFragmentManager());
        }
    }

    private void requestReportOptions() {
        if (mJasperServer.isProEdition()) {
            mGetReportOptionsCase.execute(jasperResource.getId(), new GetReportOptionsListener());
        }
    }

    private class GetInputControlsListener extends Subscriber<Void> {
        @Override
        public void onStart() {
            showProgressDialog();
        }

        @Override
        public void onCompleted() {
            hideProgressDialog();
        }

        @Override
        public void onError(Throwable e) {
            RequestExceptionHandler.showAuthErrorIfExists(getActivity(), e);
            hideProgressDialog();
        }

        @Override
        public void onNext(Void aVoid) {
            resourceOpener.openResource(ReportInfoFragment.this, mResourceLookup);
        }
    }

    private class GetReportOptionsListener extends SimpleSubscriber<Set<ReportOption>> {
        @Override
        public void onNext(Set<ReportOption> options) {
            mReportOptionsExist = !options.isEmpty();
            getActivity().invalidateOptionsMenu();
        }
    }
}
