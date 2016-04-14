/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.activities.viewer.html.report;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.InputControlsActivity;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.InputControlsActivity_;
import com.jaspersoft.android.jaspermobile.data.entity.mapper.ReportParamsMapper;
import com.jaspersoft.android.jaspermobile.dialog.NumberPickerDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.NumberDialogFragment;
import com.jaspersoft.android.jaspermobile.domain.ReportControlFlags;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.interactor.profile.AuthorizeSessionUseCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetReportShowControlsPropertyCase;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.view.activity.CastActivity;
import com.jaspersoft.android.jaspermobile.util.ReportParamsStorage;
import com.jaspersoft.android.jaspermobile.util.cast.ResourcePresentationService;
import com.jaspersoft.android.jaspermobile.widget.AbstractPaginationView;
import com.jaspersoft.android.jaspermobile.widget.PaginationBarView;
import com.jaspersoft.android.sdk.client.oxm.control.InputControl;
import com.jaspersoft.android.sdk.client.oxm.report.ReportParameter;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import timber.log.Timber;


/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@OptionsMenu({R.menu.webview_menu, R.menu.report_filter_manager_menu})
@EActivity(R.layout.activity_cast_report)
public class ReportCastActivity extends CastActivity
        implements
        ResourcePresentationService.ResourcePresentationCallback,
        NumberPickerDialogFragment.NumberDialogClickListener,
        NumberDialogFragment.NumberDialogClickListener,
        AbstractPaginationView.OnPageChangeListener,
        AbstractPaginationView.OnPickerSelectedListener {

    private static final int REQUEST_INITIAL_REPORT_PARAMETERS = 100;
    private static final int REQUEST_NEW_REPORT_PARAMETERS = 200;

    @Extra
    protected ResourceLookup resource;

    @ViewById(R.id.progressLoading)
    protected ProgressBar reportProgress;

    @ViewById(R.id.reportMessage)
    protected TextView reportMessage;

    @ViewById(R.id.scrollContainer)
    protected LinearLayout scrollContainer;

    @ViewById(R.id.paginationControl)
    protected PaginationBarView paginationBar;

    @ViewById(R.id.reload)
    protected View reloadControl;

    @OptionsMenuItem(R.id.refreshAction)
    protected MenuItem refreshAction;

    @OptionsMenuItem(R.id.showFilters)
    protected MenuItem showFilters;

    @InstanceState
    protected Boolean mHasControls;

    @Inject
    protected ReportParamsStorage paramsStorage;
    @Inject
    protected GetReportShowControlsPropertyCase mGetReportShowControlsPropertyCase;
    @Inject
    protected AuthorizeSessionUseCase mAuthorizeSessionUseCase;

    @Inject
    protected ReportParamsMapper mReportParamsMapper;

    private ResourcePresentationService mResourcePresentationService;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseActivityComponent().inject(this);

        mResourcePresentationService = (ResourcePresentationService) ResourcePresentationService.getInstance();
        mTimer = new Timer();
    }

    @AfterViews
    protected void run() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(resource.getLabel());
        }
        paginationBar.setOnPageChangeListener(this);
        paginationBar.setOnPickerSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ResourcePresentationService.isStarted()) {
            mResourcePresentationService.addResourcePresentationCallback(this);
            mResourcePresentationService.synchronizeState(resource, this);
        } else {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mResourcePresentationService.removeResourcePresentationCallback(this);
        cancelScrolling();
    }

    @Override
    protected void onDestroy() {
        mGetReportShowControlsPropertyCase.unsubscribe();
        mAuthorizeSessionUseCase.unsubscribe();
        super.onDestroy();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isPresenting = mResourcePresentationService.isPresenting();

        refreshAction.setVisible(isPresenting);
        showFilters.setVisible(isPresenting && !getInputControls().isEmpty());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected String getScreenName() {
        return getString(R.string.ja_rc_s);
    }

    @OptionsItem
    public boolean showFilters() {
        InputControlsActivity_.intent(ReportCastActivity.this)
                .reportUri(resource.getUri())
                .startForResult(REQUEST_NEW_REPORT_PARAMETERS);
        return true;
    }

    @OptionsItem
    public void refreshAction() {
        mResourcePresentationService.refresh();
    }

    @OnActivityResult(REQUEST_INITIAL_REPORT_PARAMETERS)
    final void onInitialsParametersResult(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            requestReportCasting();
        } else {
            finish();
        }
    }

    @OnActivityResult(REQUEST_NEW_REPORT_PARAMETERS)
    final void onNewParametersResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            boolean isNewParamsEqualOld = data.getBooleanExtra(
                    InputControlsActivity.RESULT_SAME_PARAMS, false);
            if (!isNewParamsEqualOld) {
                requestApplyParams();
            }
        }
    }

    @Touch(R.id.btnScrollUp)
    protected boolean scrollUpAction(MotionEvent event) {
        scrollTo(event, new TimerTask() {
            @Override
            public void run() {
                mResourcePresentationService.scrollUp();
            }
        });
        return false;
    }

    @Touch(R.id.btnScrollDown)
    protected boolean scrollDownAction(MotionEvent event) {
        scrollTo(event, new TimerTask() {
            @Override
            public void run() {
                mResourcePresentationService.scrollDown();
            }
        });
        return false;
    }

    @Touch(R.id.btnScrollLeft)
    protected boolean scrollLeftAction(MotionEvent event) {
        scrollTo(event, new TimerTask() {
            @Override
            public void run() {
                mResourcePresentationService.scrollLeft();
            }
        });
        return false;
    }

    @Touch(R.id.btnScrollRight)
    protected boolean scrollRightAction(MotionEvent event) {
        scrollTo(event, new TimerTask() {
            @Override
            public void run() {
                mResourcePresentationService.scrollRight();
            }
        });
        return false;
    }

    @Click(R.id.btnZoomIn)
    void zoomInAction() {
        mResourcePresentationService.zoomIn();
    }

    @Click(R.id.btnZoomOut)
    void zoomOutAction() {
        mResourcePresentationService.zoomOut();
    }

    @Click(R.id.reload)
    void reloadSession() {
        mAuthorizeSessionUseCase.execute(new ErrorSubscriber<>(new SimpleSubscriber<Void>() {
            @Override
            public void onStart() {
                showReloadButton(false);
                showMessageView(false);
                showPaginationBar(false);
                resetProgressBar();

                showProgressBar(true);
                showProgressMessage(getString(R.string.r_pd_running_report_msg));
            }

            @Override
            public void onCompleted() {
                mResourcePresentationService.reload();
            }
        }));
    }

    //---------------------------------------------------------------------
    // Callbacks
    //---------------------------------------------------------------------

    @Override
    public void onPagePickerRequested() {
        if (paginationBar.isTotalPagesLoaded()) {
            NumberPickerDialogFragment.createBuilder(getSupportFragmentManager())
                    .setMinValue(1)
                    .setCurrentValue(paginationBar.getCurrentPage())
                    .setMaxValue(paginationBar.getTotalPages())
                    .show();
        } else {
            NumberDialogFragment.createBuilder(getSupportFragmentManager())
                    .setMaxValue(Integer.MAX_VALUE)
                    .show();
        }
    }

    @Override
    public void onNumberPicked(int page, int requestCode) {
        paginationBar.updateCurrentPage(page);
        onNumberSubmit(page, requestCode);
    }

    @Override
    public void onNumberSubmit(int currentPage, int requestCode) {
        onPageSelected(currentPage);
    }

    @Override
    public void onPageSelected(int currentPage) {
        paginationBar.setEnabled(false);
        mResourcePresentationService.selectPage(currentPage);
    }

    @Override
    public void onCastStarted() {
        super.onCastStarted();
        showProgressMessage(getString(R.string.r_pd_initializing_msg));
    }

    @Override
    public void onInitializationDone() {
        showMessageView(false);
        showProgressBar(false);
        loadInputControls();
    }

    private void loadInputControls() {
        if (controlsLoaded()) {
            requestReportCasting();
        } else {
            loadControls();
        }
    }

    private void loadControls() {
        mGetReportShowControlsPropertyCase.execute(resource.getUri(), new ErrorSubscriber<>(new SimpleSubscriber<ReportControlFlags>() {
            @Override
            public void onNext(ReportControlFlags property) {
                mHasControls = property.hasControls();

                if (property.needPrompt()) {
                    InputControlsActivity_.intent(ReportCastActivity.this)
                            .reportUri(resource.getUri())
                            .startForResult(REQUEST_INITIAL_REPORT_PARAMETERS);
                } else {
                    requestReportCasting();
                }
            }
        }));
    }

    @Override
    public void onLoadingStarted() {
        invalidateOptionsMenu();

        showProgressMessage(getString(R.string.r_pd_running_report_msg));

        showPaginationBar(false);
        resetProgressBar();
    }

    private void resetProgressBar() {
        resetPaginationView();
    }

    @Override
    public void onPresentationBegun() {
        invalidateOptionsMenu();
        showMessageView(false);
        showProgressBar(false);
        showScrollControls(true);
        setPaginationBarEnable(true);
    }

    @Override
    public void onMultiPage() {
        if (paginationBar.getTotalPages() == 0) {
            showEmptyView();
            showPaginationBar(false);
        } else {
            showPaginationBar(true);
        }

    }

    @Override
    public void onPageCountObtain(int pageCount) {
        if (pageCount == 0) {
            showEmptyView();
        }

        setPaginationBarTotal(pageCount);
        boolean isMultiPage = pageCount > 1;
        showPaginationBar(isMultiPage);
    }

    @Override
    public void onPageChanged(int pageNumb, String errorMessage) {
        setPaginationBarPage(pageNumb);
        setPaginationBarEnable(true);

        if (errorMessage != null) {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorOccurred(String error) {
        invalidateOptionsMenu();
        showErrorMessage(error);

        showProgressBar(false);
        showPaginationBar(false);
        resetPaginationView();
    }

    @Override
    public void onAuthErrorOccurred() {
        showPaginationBar(false);
        showProgressBar(false);
        showErrorMessage(getString(R.string.da_session_expired));
        showReloadButton(true);
    }

    @Override
    public void onCastStopped() {
        super.onCastStopped();
        finish();
    }

    //---------------------------------------------------------------------
    // UI methods
    //---------------------------------------------------------------------

    public void showProgressMessage(CharSequence message) {
        showProgressBar(true);
        showMessageView(true);
        showMessage(message);
        showScrollControls(false);
    }

    public void showEmptyView() {
        showMessageView(true);
        showMessage(getString(R.string.rv_error_empty_report));
        showScrollControls(false);
    }

    public void showErrorMessage(CharSequence error) {
        showMessageView(true);
        showMessage(error);
        showScrollControls(false);
    }

    public void showMessage(CharSequence message) {
        reportMessage.setText(message);
    }

    private void resetPaginationView() {
        paginationBar.reset();
    }

    public void setPaginationBarTotal(int total) {
        paginationBar.updateTotalCount(total);
    }

    public void setPaginationBarEnable(boolean enabled) {
        paginationBar.setEnabled(enabled);
    }

    public void setPaginationBarPage(int page) {
        paginationBar.updateCurrentPage(page);
    }

    public void showPaginationBar(boolean visibility) {
        paginationBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void showReloadButton(boolean visibility) {
        reloadControl.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void showMessageView(boolean visibility) {
        reportMessage.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    private void showProgressBar(boolean visibility) {
        reportProgress.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void showScrollControls(boolean visibility) {
        scrollContainer.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    private void scrollTo(MotionEvent event, TimerTask task) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            cancelScrolling();
            mTimer.scheduleAtFixedRate(task, 0, 10);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            cancelScrolling();
        }
    }

    private void cancelScrolling() {
        mTimer.cancel();
        mTimer.purge();
        mTimer = new Timer();
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private boolean controlsLoaded() {
        return mHasControls != null;
    }

    private void requestReportCasting() {
        mResourcePresentationService.startPresentation(resource);
    }

    private void requestApplyParams() {
        String params = mReportParamsMapper.legacyParamsToJson(getReportParameters());
        mResourcePresentationService.applyParams(params);
    }

    private List<InputControl> getInputControls() {
        List<InputControl> inputControls = paramsStorage.getInputControlHolder(resource.getUri()).getInputControls();
        if (inputControls == null) {
            return Collections.emptyList();
        }
        return inputControls;
    }

    private List<ReportParameter> getReportParameters() {
        List<ReportParameter> reportParams = paramsStorage.getInputControlHolder(resource.getUri()).getReportParams();
        if (reportParams == null) {
            return Collections.emptyList();
        }
        return reportParams;
    }

    private class ErrorSubscriber<T> extends SimpleSubscriber<T> {
        private final SimpleSubscriber<T> mDelegate;

        protected ErrorSubscriber(SimpleSubscriber<T> delegate) {
            mDelegate = delegate;
        }

        @Override
        public void onNext(T item) {
            mDelegate.onNext(item);
        }

        @Override
        public void onCompleted() {
            showProgressBar(false);
            showMessageView(false);
            mDelegate.onCompleted();
        }

        @Override
        public void onStart() {
            showProgressMessage(getString(R.string.loading_msg));
            mDelegate.onStart();
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "GetReportShowControlsPropertyCase failed");
            String error = RequestExceptionHandler.extractMessage(ReportCastActivity.this, e);
            showErrorMessage(error);
            showProgressBar(false);
            mDelegate.onError(e);
        }
    }
}
