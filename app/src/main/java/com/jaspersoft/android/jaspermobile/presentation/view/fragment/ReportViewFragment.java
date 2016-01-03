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

package com.jaspersoft.android.jaspermobile.presentation.view.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.InputControlsActivity;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.InputControlsActivity_;
import com.jaspersoft.android.jaspermobile.activities.save.SaveReportActivity_;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.report.widget.AbstractPaginationView;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.report.widget.PaginationBarView;
import com.jaspersoft.android.jaspermobile.data.mapper.ReportParamsTransformer;
import com.jaspersoft.android.jaspermobile.data.repository.InMemoryReportRepository;
import com.jaspersoft.android.jaspermobile.data.service.RestReportService;
import com.jaspersoft.android.jaspermobile.dialog.NumberDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.PageDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.SimpleDialogFragment;
import com.jaspersoft.android.jaspermobile.domain.interactor.GetReportControlsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.GetReportPageCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.GetReportTotalPagesCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.IsReportMultiPageCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.ReloadReportCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.RunReportExecutionCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.UpdateReportExecutionCase;
import com.jaspersoft.android.jaspermobile.domain.repository.ReportRepository;
import com.jaspersoft.android.jaspermobile.domain.service.ObservableReportService;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.presentation.action.ReportActionListener;
import com.jaspersoft.android.jaspermobile.presentation.presenter.ReportViewPresenter;
import com.jaspersoft.android.jaspermobile.presentation.view.ReportView;
import com.jaspersoft.android.jaspermobile.util.FavoritesHelper;
import com.jaspersoft.android.jaspermobile.util.ReportParamsStorage;
import com.jaspersoft.android.jaspermobile.util.print.JasperPrintJobFactory;
import com.jaspersoft.android.jaspermobile.util.print.JasperPrinter;
import com.jaspersoft.android.jaspermobile.util.print.ResourcePrintJob;
import com.jaspersoft.android.jaspermobile.webview.JasperChromeClientListenerImpl;
import com.jaspersoft.android.jaspermobile.webview.SystemChromeClient;
import com.jaspersoft.android.jaspermobile.webview.WebViewEnvironment;
import com.jaspersoft.android.jaspermobile.widget.JSWebView;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.network.AuthorizedClient;
import com.jaspersoft.android.sdk.service.report.ReportService;
import com.jaspersoft.android.sdk.util.FileUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import roboguice.fragment.RoboFragment;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@EFragment(R.layout.report_html_viewer)
@OptionsMenu({R.menu.report_filter_manager_menu, R.menu.webview_menu, R.menu.retrofit_report_menu})
public class ReportViewFragment extends RoboFragment implements ReportView, NumberDialogFragment.NumberDialogClickListener, PageDialogFragment.PageDialogClickListener {

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

    @Bean
    protected FavoritesHelper favoritesHelper;

    @Inject
    protected JsRestClient jsRestClient;
    @Inject
    protected AuthorizedClient restClient;
    @Inject
    protected ReportParamsStorage paramsStorage;

    private ReportViewPresenter mPresenter;
    private ReportActionListener mActionListener;
    private Uri favoriteEntryUri;
    private Toast mToast;

    protected boolean filtersMenuItemVisibilityFlag, saveMenuItemVisibilityFlag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_LONG);
        favoriteEntryUri = favoritesHelper.queryFavoriteUri(resource);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        favoriteAction.setIcon(favoriteEntryUri == null ? R.drawable.ic_menu_star_outline : R.drawable.ic_menu_star);
        favoriteAction.setTitle(favoriteEntryUri == null ? R.string.r_cm_add_to_favorites : R.string.r_cm_remove_from_favorites);

        saveReport.setVisible(saveMenuItemVisibilityFlag);
        showFilters.setVisible(filtersMenuItemVisibilityFlag);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        injectComponents();
        setupPaginationControl();
        runReport();
    }

    private void setupPaginationControl() {
        paginationControl.setOnPageChangeListener(new AbstractPaginationView.OnPageChangeListener() {
            @Override
            public void onPageSelected(int currentPage) {
                mActionListener.loadPage(String.valueOf(currentPage));
            }

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
        if (mPresenter != null) return;

        RequestExceptionHandler exceptionHandler = new RequestExceptionHandler(getActivity());

        String reportUri = resource.getUri();
        ReportParamsTransformer paramsTransformer = new ReportParamsTransformer();
        ReportService reportService = ReportService.newService(restClient);
        ObservableReportService observableReportService = new RestReportService(jsRestClient, reportService);
        ReportRepository reportRepository = new InMemoryReportRepository(reportUri, observableReportService, paramsStorage, paramsTransformer);

        GetReportControlsCase getReportControlsCase = new GetReportControlsCase(reportRepository);
        GetReportPageCase getReportPageCase = new GetReportPageCase(reportRepository);
        GetReportTotalPagesCase getReportTotalPagesCase = new GetReportTotalPagesCase(reportRepository);
        IsReportMultiPageCase isReportMultiPageCase = new IsReportMultiPageCase(reportRepository);
        RunReportExecutionCase runReportExecutionCase = new RunReportExecutionCase(reportRepository);
        UpdateReportExecutionCase updateReportExecutionCase = new UpdateReportExecutionCase(reportRepository);
        ReloadReportCase reloadReportCase = new ReloadReportCase(reportRepository);

        mPresenter = new ReportViewPresenter(
                exceptionHandler,
                getReportControlsCase,
                getReportPageCase,
                getReportTotalPagesCase,
                isReportMultiPageCase,
                runReportExecutionCase,
                updateReportExecutionCase,
                reloadReportCase
        );
        mPresenter.setView(this);
        mActionListener = mPresenter;
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

    @AfterViews
    final void init() {
        progressBar.setVisibility(View.VISIBLE);

        SystemChromeClient systemChromeClient = SystemChromeClient.from(getActivity())
                .withDelegateListener(new JasperChromeClientListenerImpl(progressBar));
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
//        webView.loadDataWithBaseURL(restClient.getServerUrl(), pageContent, MIME, UTF_8, null);
    }

    @Override
    public void showPaginationControl() {
        paginationControl.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePaginationControl() {
        paginationControl.setVisibility(View.GONE);
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

    @OptionsItem
    final void saveReport() {
        if (FileUtils.isExternalStorageWritable()) {
            boolean isTotalPagesDefined =
                    paginationControl.getTotalPages() != AbstractPaginationView.UNDEFINED_PAGE_NUMBER;
            int pages = isTotalPagesDefined ? paginationControl.getTotalPages() :
                    AbstractPaginationView.FIRST_PAGE;

            SaveReportActivity_.intent(this)
                    .resource(resource)
                    .pageCount(pages)
                    .start();
        } else {
            Toast.makeText(getActivity(),
                    R.string.rv_t_external_storage_not_available, Toast.LENGTH_SHORT).show();
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
        ResourcePrintJob job = JasperPrintJobFactory.createReportPrintJob(
                getActivity(),
                jsRestClient,
                resource,
                paramsStorage.getInputControlHolder(resource.getUri()).getReportParams()
        );
        JasperPrinter.print(job);
    }

    @OptionsItem
    final void favoriteAction() {
        favoriteEntryUri = favoritesHelper.
                handleFavoriteMenuAction(favoriteEntryUri, resource, favoriteAction);
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
