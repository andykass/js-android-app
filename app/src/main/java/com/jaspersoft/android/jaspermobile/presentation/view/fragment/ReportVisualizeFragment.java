package com.jaspersoft.android.jaspermobile.presentation.view.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.InputControlsActivity;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.InputControlsActivity_;
import com.jaspersoft.android.jaspermobile.activities.save.SaveReportActivity_;
import com.jaspersoft.android.jaspermobile.activities.schedule.NewScheduleActivity_;
import com.jaspersoft.android.jaspermobile.activities.share.AnnotationActivity_;
import com.jaspersoft.android.jaspermobile.dialog.NumberDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.PageDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.SimpleDialogFragment;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.ScreenCapture;
import com.jaspersoft.android.jaspermobile.domain.VisualizeTemplate;
import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportVisualizeViewerModule;
import com.jaspersoft.android.jaspermobile.presentation.contract.VisualizeReportContract;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.VisualizeViewModel;
import com.jaspersoft.android.jaspermobile.presentation.page.ReportPageState;
import com.jaspersoft.android.jaspermobile.presentation.presenter.ReportVisualizePresenter;
import com.jaspersoft.android.jaspermobile.presentation.view.activity.ReportVisualizeActivity_;
import com.jaspersoft.android.jaspermobile.util.FavoritesHelper;
import com.jaspersoft.android.jaspermobile.util.print.ReportPrintJob;
import com.jaspersoft.android.jaspermobile.util.print.ResourcePrintJob;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceConverter;
import com.jaspersoft.android.jaspermobile.widget.AbstractPaginationView;
import com.jaspersoft.android.jaspermobile.widget.JSWebView;
import com.jaspersoft.android.jaspermobile.widget.PaginationBarView;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.util.FileUtils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@EFragment(R.layout.activity_report_viewer)
@OptionsMenu({
        R.menu.report_filter_manager_menu,
        R.menu.webview_menu,
        R.menu.retrofit_report_menu,
        R.menu.print_menu,
        R.menu.report_schedule
})
public class ReportVisualizeFragment extends BaseFragment
        implements VisualizeReportContract.View,
        NumberDialogFragment.NumberDialogClickListener,
        PageDialogFragment.PageDialogClickListener {

    public static final String TAG = "report-visualize-view";

    private static final int REQUEST_INITIAL_REPORT_PARAMETERS = 100;
    private static final int REQUEST_NEW_REPORT_PARAMETERS = 200;

    @FragmentArg
    protected ResourceLookup resource;

    @ViewById
    protected JSWebView webView;
    @ViewById(android.R.id.empty)
    protected TextView errorView;
    @ViewById
    protected ProgressBar progressBar;
    @ViewById
    protected PaginationBarView paginationControl;
    @ViewById(R.id.reload)
    protected View reloadControl;

    @OptionsMenuItem
    protected MenuItem saveReport;
    @OptionsMenuItem(R.id.printAction)
    protected MenuItem printReport;
    @OptionsMenuItem
    protected MenuItem showFilters;
    @OptionsMenuItem
    protected MenuItem favoriteAction;
    @OptionsMenuItem
    protected MenuItem aboutAction;

    @Inject
    protected FavoritesHelper favoritesHelper;
    @Inject
    protected JasperServer mServer;
    @Inject
    protected ReportVisualizePresenter mPresenter;
    @Inject
    protected VisualizeReportContract.Action mActionListener;
    @Inject
    protected PostExecutionThread mPostExecutionThread;
    @Inject
    protected VisualizeViewModel mVisualizeViewModel;
    @Inject
    protected ResourcePrintJob mResourcePrintJob;
    @Inject
    protected Analytics mAnalytics;
    @Inject
    protected JasperResourceConverter mJasperResourceConverter;

    @InstanceState
    protected ReportPageState mState;

    private Toast mToast;

    protected boolean filtersMenuItemVisibilityFlag, saveMenuItemVisibilityFlag;
    private Subscription onPageChangeSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mState == null) {
            mState = new ReportPageState();
        }
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_LONG);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        favoritesHelper.updateFavoriteIconState(favoriteAction, resource.getUri());
        saveReport.setVisible(saveMenuItemVisibilityFlag);
        showFilters.setVisible(filtersMenuItemVisibilityFlag);

        if (printReport != null) {
            printReport.setVisible(saveMenuItemVisibilityFlag);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        injectComponents();
        runReport();
    }

    private void injectComponents() {
        getProfileComponent()
                .plusReportVisualizeViewer(
                        new ActivityModule(getActivity()),
                        new ReportVisualizeViewerModule(resource.getUri(), webView)
                )
                .inject(this);
        mPresenter.injectView(this);
    }

    private void setupPaginationControl() {
        onPageChangeSubscription = paginationControl.toRx()
                .pagesChangeEvents()
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(mPostExecutionThread.getScheduler())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer currentPage) {
                        mActionListener.loadPage(String.valueOf(currentPage));
                    }
                });
        paginationControl.setOnPickerSelectedListener(new AbstractPaginationView.OnPickerSelectedListener() {
            @Override
            public void onPagePickerRequested() {
                if (paginationControl.isTotalPagesLoaded()) {
                    NumberDialogFragment.createBuilder(getFragmentManager())
                            .setMinValue(1)
                            .setCurrentValue(paginationControl.getCurrentPage())
                            .setMaxValue(paginationControl.getTotalPages())
                            .setTargetFragment(ReportVisualizeFragment.this)
                            .show();
                } else {
                    PageDialogFragment.createBuilder(getFragmentManager())
                            .setMaxValue(Integer.MAX_VALUE)
                            .setTargetFragment(ReportVisualizeFragment.this)
                            .show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setupPaginationControl();
        mPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        onPageChangeSubscription.unsubscribe();
        mPresenter.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.destroy();
        mToast.cancel();
        favoritesHelper.getToast().cancel();
    }

    @OnActivityResult(REQUEST_INITIAL_REPORT_PARAMETERS)
    final void onInitialsParametersResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mActionListener.runReport();
        } else {
            getActivity().finish();
        }
    }

    @OnActivityResult(REQUEST_NEW_REPORT_PARAMETERS)
    final void onNewParametersResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            boolean isNewParamsEqualOld = data.getBooleanExtra(
                    InputControlsActivity.RESULT_SAME_PARAMS, false);
            if (!isNewParamsEqualOld) {
                mActionListener.updateReport();
            }
        }
    }

    @OptionsItem
    public void showFilters() {
        InputControlsActivity_.intent(this)
                .reportUri(resource.getUri())
                .startForResult(REQUEST_NEW_REPORT_PARAMETERS);
    }

    @OptionsItem
    final void printAction() {
        mAnalytics.sendEvent(
                Analytics.EventCategory.RESOURCE.getValue(),
                Analytics.EventAction.PRINTED.getValue(),
                Analytics.EventLabel.REPORT.getValue()
        );

        Bundle args = new Bundle();
        args.putString(ReportPrintJob.REPORT_URI_KEY, resource.getUri());
        args.putInt(ReportPrintJob.TOTAL_PAGES_KEY, getPaginationTotalPages());
        args.putString(ResourcePrintJob.PRINT_NAME_KEY, resource.getLabel());

        mResourcePrintJob.printResource(args);
    }

    @OptionsItem
    final void favoriteAction() {
        favoritesHelper.switchFavoriteState(resource, favoriteAction);
    }

    @OptionsItem
    final void aboutAction() {
        SimpleDialogFragment.createBuilder(getActivity(), getFragmentManager())
                .setTitle(resource.getLabel())
                .setMessage(resource.getDescription())
                .setNegativeButtonText(R.string.ok)
                .setTargetFragment(this)
                .show();
    }

    @OptionsItem(R.id.newSchedule)
    final void scheduleAction() {
        JasperResource reportResource = mJasperResourceConverter.convertToJasperResource(resource);

        NewScheduleActivity_.intent(getActivity())
                .jasperResource(reportResource)
                .start();
    }

    @OptionsItem
    final void saveReport() {
        if (FileUtils.isExternalStorageWritable()) {
            SaveReportActivity_.intent(this)
                    .resource(resource)
                    .pageCount(getPaginationTotalPages())
                    .start();
        } else {
            Toast.makeText(getActivity(),
                    R.string.rv_t_external_storage_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @OptionsItem
    final void shareAction() {
        ScreenCapture reportScreenCapture = ScreenCapture.Factory.capture(webView);
        mActionListener.shareReport(reportScreenCapture);
    }

    private void runReport() {
        mPresenter.init();
    }

    @Click
    void reload() {
        hideError();
        showReloadButton(false);
        mActionListener.refresh();
    }

    //---------------------------------------------------------------------
    // ReportVisualizeView callbacks
    //---------------------------------------------------------------------

    @Override
    public void showFilterAction(boolean visibilityFlag) {
        filtersMenuItemVisibilityFlag = visibilityFlag;
    }

    @Override
    public void showSaveAction(boolean visibilityFlag) {
        saveMenuItemVisibilityFlag = visibilityFlag;
    }

    @Override
    public void showProgress() {
        ProgressDialogFragment.builder(getFragmentManager())
                .setLoadingMessage(R.string.loading_msg)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        getActivity().finish();
                    }
                })
                .show();
    }

    @Override
    public void reloadMenu() {
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public void showInitialFiltersPage() {
        InputControlsActivity_.intent(this)
                .reportUri(resource.getUri())
                .startForResult(REQUEST_INITIAL_REPORT_PARAMETERS);
    }

    @OptionsItem
    final void refreshAction() {
        mActionListener.refresh();
    }

    @Override
    public void showPagination(boolean visibility) {
        paginationControl.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setPaginationEnabled(boolean enabled) {
        paginationControl.setEnabled(enabled);
    }

    @Override
    public void setPaginationTotalPages(int totalPages) {
        paginationControl.updateTotalCount(totalPages);
    }

    @Override
    public int getPaginationTotalPages() {
        boolean isTotalPagesDefined =
                paginationControl.getTotalPages() != AbstractPaginationView.UNDEFINED_PAGE_NUMBER;
        return isTotalPagesDefined ? paginationControl.getTotalPages() :
                AbstractPaginationView.FIRST_PAGE;
    }

    @Override
    public void setPaginationCurrentPage(int page) {
        paginationControl.updateCurrentPage(page);
    }

    @Override
    public void resetPaginationControl() {
        paginationControl.updateTotalCount(AbstractPaginationView.UNDEFINED_PAGE_NUMBER);
    }

    @Override
    public void showWebView(boolean visibility) {
        webView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showReloadButton(boolean visibility) {
        reloadControl.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showPageOutOfRangeError() {
        showNotification(getString(R.string.rv_out_of_range));
    }

    @Override
    public void showEmptyPageMessage() {
        showError(getString(R.string.rv_error_empty_report));
    }

    @Override
    public void hideEmptyPageMessage() {
        hideError();
    }

    @Override
    public void loadTemplateInView(VisualizeTemplate template) {
        webView.loadDataWithBaseURL(mServer.getBaseUrl(), template.getContent(), "text/html", "utf-8", null);
    }

    @Override
    public void updateDeterminateProgress(int progress) {
        int maxProgress = progressBar.getMax();
        progressBar.setProgress((maxProgress / 100) * progress);
        if (progress == maxProgress) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showExternalLink(String externalLink) {
        String title = getString(R.string.rv_open_link_chooser);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(externalLink));
        Intent chooser = Intent.createChooser(browserIntent, title);
        if (browserIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    @Override
    public void executeReport(ResourceLookup lookup) {
        ReportVisualizeActivity_.intent(getActivity())
                .resource(lookup)
                .start();
    }

    @Override
    public void resetZoom() {
        while (webView.zoomOut()) ;
    }

    @Override
    public ReportPageState getState() {
        return mState;
    }

    @Override
    public VisualizeViewModel getVisualize() {
        return mVisualizeViewModel;
    }

    @Override
    public void handleSessionExpiration() {
        showWebView(false);
        showPagination(false);
        showReloadButton(true);
        showError(getString(R.string.da_session_expired));
    }

    @Override
    public void navigateToAnnotationPage(File file) {
        Intent intent = AnnotationActivity_.intent(getContext())
                .imageUri(Uri.fromFile(file))
                .get();
        startActivity(intent);
    }

    @Override
    public void showLoading() {
        ProgressDialogFragment.builder(getFragmentManager())
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        getActivity().finish();
                    }
                })
                .show();
    }

    @Override
    public void hideLoading() {
        ProgressDialogFragment.dismiss(getFragmentManager());
    }

    @Override
    public void showError(String message) {
        errorView.setVisibility(View.VISIBLE);
        errorView.setText(message);
    }

    @Override
    public void showNotification(String message) {
        mToast.setText(message);
        mToast.show();
    }

    @Override
    public void hideError() {
        errorView.setVisibility(View.INVISIBLE);
    }

    //---------------------------------------------------------------------
    // Pagination callbacks
    //---------------------------------------------------------------------

    @Override
    public void onPageSelected(int page, int requestCode) {
        updatePage(page);
    }

    @Override
    public void onPageSelected(int page) {
        updatePage(page);
    }

    private void updatePage(int page) {
        mActionListener.loadPage(String.valueOf(page));
    }
}
