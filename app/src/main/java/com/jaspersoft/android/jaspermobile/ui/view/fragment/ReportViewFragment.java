/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from TIBCO Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of TIBCO Jaspersoft Mobile for Android.
 *
 * TIBCO Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TIBCO Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TIBCO Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.ui.view.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.InputControlsActivity;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.InputControlsActivity_;
import com.jaspersoft.android.jaspermobile.activities.save.SaveReportActivity_;
import com.jaspersoft.android.jaspermobile.dialog.NumberDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.PageDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.SimpleDialogFragment;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.internal.di.components.ReportRestViewerComponent;
import com.jaspersoft.android.jaspermobile.ui.contract.RestReportContract;
import com.jaspersoft.android.jaspermobile.ui.page.ReportPageState;
import com.jaspersoft.android.jaspermobile.ui.presenter.ReportViewPresenter;
import com.jaspersoft.android.jaspermobile.ui.view.activity.schedule.NewScheduleActivity_;
import com.jaspersoft.android.jaspermobile.util.FavoritesHelper;
import com.jaspersoft.android.jaspermobile.util.print.ReportPrintJob;
import com.jaspersoft.android.jaspermobile.util.print.ResourcePrintJob;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceConverter;
import com.jaspersoft.android.jaspermobile.webview.JasperChromeClientListener;
import com.jaspersoft.android.jaspermobile.webview.SystemChromeClient;
import com.jaspersoft.android.jaspermobile.webview.WebViewEnvironment;
import com.jaspersoft.android.jaspermobile.widget.AbstractPaginationView;
import com.jaspersoft.android.jaspermobile.widget.JSWebView;
import com.jaspersoft.android.jaspermobile.widget.PaginationBarView;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.util.FileUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@EFragment(R.layout.report_html_viewer)
@OptionsMenu({
        R.menu.report_filter_manager_menu,
        R.menu.webview_menu,
        R.menu.retrofit_report_menu,
        R.menu.print_menu,
        R.menu.report_schedule
})
public class ReportViewFragment extends BaseFragment
        implements RestReportContract.View,
        NumberDialogFragment.NumberDialogClickListener,
        PageDialogFragment.PageDialogClickListener {

    public static final String TAG = "report-view";
    private static final String MIME = "text/html";
    private static final String UTF_8 = "utf-8";

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
    protected ReportViewPresenter mPresenter;
    @Inject
    protected RestReportContract.Action mActionListener;
    @Inject
    protected PostExecutionThread mPostExecutionThread;
    @Inject
    protected ResourcePrintJob mResourcePrintJob;
    @Inject
    protected Analytics mAnalytics;
    @Inject
    protected JasperResourceConverter mJasperResourceConverter;

    @InstanceState
    protected ReportPageState mReportPageState;

    private Toast mToast;

    protected boolean filtersMenuItemVisibilityFlag, saveMenuItemVisibilityFlag;
    private Subscription onPageChangeSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (mReportPageState == null) {
            mReportPageState = new ReportPageState();
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
        setupPaginationControl();
        runReport();
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
                            .setTargetFragment(ReportViewFragment.this)
                            .show();
                } else {
                    PageDialogFragment.createBuilder(getFragmentManager())
                            .setMaxValue(Integer.MAX_VALUE)
                            .setTargetFragment(ReportViewFragment.this)
                            .show();
                }
            }
        });
    }

    private void runReport() {
        mPresenter.init();
    }

    private void injectComponents() {
        getComponent(ReportRestViewerComponent.class).inject(this);
        mPresenter.injectView(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onPageChangeSubscription.unsubscribe();
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

    @AfterViews
    final void init() {
        progressBar.setVisibility(View.VISIBLE);

        SystemChromeClient systemChromeClient = new SystemChromeClient.Builder(getActivity())
                .withDelegateListener(new JasperChromeClientListener() {
                    @Override
                    public void onProgressChanged(WebView webView, int progress) {
                        int maxProgress = progressBar.getMax();
                        if (progress == maxProgress) {
                            progressBar.setVisibility(View.GONE);
                            webView.setVisibility(View.VISIBLE);
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                            webView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onConsoleMessage(ConsoleMessage consoleMessage) {
                    }
                })
                .build();
        WebViewEnvironment.configure(webView)
                .withDefaultSettings()
                .withChromeClient(systemChromeClient);
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

    @Override
    public void setFilterActionVisibility(boolean visibilityFlag) {
        filtersMenuItemVisibilityFlag = visibilityFlag;
    }

    @Override
    public void setSaveActionVisibility(boolean visibilityFlag) {
        saveMenuItemVisibilityFlag = visibilityFlag;
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

    @Override
    public void showPage(String pageContent) {
        webView.loadDataWithBaseURL(mServer.getBaseUrl(), pageContent, MIME, UTF_8, null);
    }

    @Override
    public void showPaginationControl(boolean visibility) {
        paginationControl.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void resetPaginationControl() {
        paginationControl.updateTotalCount(AbstractPaginationView.UNDEFINED_PAGE_NUMBER);
    }

    @Override
    public void showTotalPages(int totalPages) {
        paginationControl.updateTotalCount(totalPages);
    }

    @Override
    public void showCurrentPage(int page) {
        paginationControl.updateCurrentPage(page);
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
    public void showReloadMessage() {
        showNotification("Restoring report");
    }

    @Override
    public void showPageLoader(boolean visibility) {
        progressBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }
    @Override
    public ReportPageState getState() {
        return mReportPageState;
    }

    @Override
    public void showWebView(boolean visibility) {
        webView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @OptionsItem
    final void saveReport() {
        if (FileUtils.isExternalStorageWritable()) {
            int pages = getPaginationTotalPages();

            SaveReportActivity_.intent(this)
                    .resource(resource)
                    .pageCount(pages)
                    .start();
        } else {
            Toast.makeText(getActivity(),
                    R.string.rv_t_external_storage_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getPaginationTotalPages() {
        boolean isTotalPagesDefined =
                paginationControl.getTotalPages() != AbstractPaginationView.UNDEFINED_PAGE_NUMBER;
        return isTotalPagesDefined ? paginationControl.getTotalPages() :
                AbstractPaginationView.FIRST_PAGE;
    }

    @OptionsItem
    public void showFilters() {
        InputControlsActivity_.intent(this)
                .reportUri(resource.getUri())
                .startForResult(REQUEST_NEW_REPORT_PARAMETERS);
    }

    @OptionsItem(R.id.newSchedule)
    final void scheduleAction() {
        JasperResource reportResource = mJasperResourceConverter.convertToJasperResource(resource);

        NewScheduleActivity_.intent(getActivity())
                .jasperResource(reportResource)
                .start();
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

    @OptionsItem
    final void refreshAction() {
        mActionListener.refresh();
    }

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
