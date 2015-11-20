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

package com.jaspersoft.android.jaspermobile.activities.save.fragment;

import android.accounts.Account;
import android.app.ActionBar;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.JasperMobileApplication;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboSpiceFragment;
import com.jaspersoft.android.jaspermobile.db.model.SavedItems;
import com.jaspersoft.android.jaspermobile.db.provider.JasperMobileDbProvider;
import com.jaspersoft.android.jaspermobile.dialog.NumberDialogFragment;
import com.jaspersoft.android.jaspermobile.network.SimpleRequestListener;
import com.jaspersoft.android.jaspermobile.util.ReportParamsStorage;
import com.jaspersoft.android.jaspermobile.util.account.JasperAccountManager;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.async.request.RunReportExecutionRequest;
import com.jaspersoft.android.sdk.client.async.request.SaveExportAttachmentRequest;
import com.jaspersoft.android.sdk.client.async.request.SaveExportOutputRequest;
import com.jaspersoft.android.sdk.client.oxm.report.ExportExecution;
import com.jaspersoft.android.sdk.client.oxm.report.ReportExecutionRequest;
import com.jaspersoft.android.sdk.client.oxm.report.ReportExecutionResponse;
import com.jaspersoft.android.sdk.client.oxm.report.ReportOutputResource;
import com.jaspersoft.android.sdk.client.oxm.report.ReportParameter;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.util.FileUtils;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment(R.layout.save_report_layout)
@OptionsMenu(R.menu.save_item_menu)
public class SaveItemFragment extends RoboSpiceFragment implements NumberDialogFragment.NumberDialogClickListener{

    public static final String TAG = SaveItemFragment.class.getSimpleName();

    private final static int FROM_PAGE_REQUEST_CODE = 1243;
    private final static int TO_PAGE_REQUEST_CODE = 2243;

    @ViewById(R.id.output_format_spinner)
    Spinner formatSpinner;
    @ViewById(R.id.report_name_input)
    EditText reportNameInput;

    @ViewById
    LinearLayout rangeControls;
    @ViewById
    TextView fromPageControl;
    @ViewById
    TextView toPageControl;

    @FragmentArg
    ResourceLookup resource;
    @FragmentArg
    int pageCount;

    @OptionsMenuItem
    MenuItem saveAction;

    @Inject
    JsRestClient jsRestClient;

    @Inject
    protected ReportParamsStorage paramsStorage;

    @InstanceState
    int runningRequests;

    private List<SpiceRequest<?>> requests = new ArrayList<SpiceRequest<?>>();
    private File reportFile;
    private Uri recordUri;

    private int mFromPage;
    private int mToPage;
    private JasperAccountManager accountManager;

    public static enum OutputFormat {
        HTML,
        PDF,
        XLS
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasOptionsMenu();

        accountManager = JasperAccountManager.get(getActivity());

        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.sr_ab_title);
        }
    }

    @OptionsItem
    final void saveAction() {
        if (isReportNameValid()) {
            final OutputFormat outputFormat = (OutputFormat) formatSpinner.getSelectedItem();
            String reportName = reportNameInput.getText() + "." + outputFormat;
            File reportDir = getAccountReportDir(reportName);

            if (reportDir == null) {
                Toast.makeText(getActivity(), R.string.sr_failed_to_create_local_repo, Toast.LENGTH_SHORT).show();
            } else {
                reportFile = new File(reportDir, reportName);

                if (reportFile.exists() || getSharedReportDir(reportName).exists()) {
                    // show validation message
                    reportNameInput.setError(getString(R.string.sr_error_report_exists));
                } else {
                    // save report
                    // run new report execution
                    setRefreshActionButtonState(true);
                    disableAllViewsWhileSaving();

                    final ReportExecutionRequest executionData = new ReportExecutionRequest();
                    executionData.setReportUnitUri(resource.getUri());
                    executionData.setInteractive(false);
                    executionData.setOutputFormat(outputFormat.toString());
                    executionData.setEscapedAttachmentsPrefix("./");

                    boolean hasPagination = (pageCount > 1);
                    if (hasPagination) {
                        boolean rangeIsValid = mFromPage < mToPage;
                        if (rangeIsValid) {
                            executionData.setPages(mFromPage + "-" + mToPage);
                        }
                        boolean exactPage = (mFromPage == mToPage);
                        if (exactPage) {
                            executionData.setPages(String.valueOf(mFromPage));
                        }
                    }

                    List<ReportParameter> reportParameters = paramsStorage.getInputControlHolder(resource.getUri()).getReportParams();

                    if (!reportParameters.isEmpty()) {
                        executionData.setParameters(reportParameters);
                    }

                    RunReportExecutionRequest request =
                            new RunReportExecutionRequest(jsRestClient, executionData);
                    getSpiceManager().execute(request,
                            new RunReportExecutionListener(outputFormat));
                }
            }
        }
    }

    @AfterViews
    final void init() {
        reportNameInput.setText(resource.getLabel());

        // show spinner with available output formats
        ArrayAdapter<OutputFormat> arrayAdapter = new ArrayAdapter<OutputFormat>(getActivity(),
                android.R.layout.simple_spinner_item, OutputFormat.values());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formatSpinner.setAdapter(arrayAdapter);

        // hide save parts views if report have only 1 page
        if (pageCount > 1) {
            rangeControls.setVisibility(View.VISIBLE);

            mFromPage = 1;
            mToPage = pageCount;

            fromPageControl.setText(String.valueOf(mFromPage));
            toPageControl.setText(String.valueOf(mToPage));
        }
    }

    @Click(R.id.fromPageControl)
    void clickOnFromPage() {
        NumberDialogFragment.createBuilder(getFragmentManager())
                .setMinValue(1)
                .setCurrentValue(mFromPage)
                .setMaxValue(pageCount)
                .setRequestCode(FROM_PAGE_REQUEST_CODE)
                .setTargetFragment(this)
                .show();
    }

    @Click(R.id.toPageControl)
    void clickOnToPage() {
        NumberDialogFragment.createBuilder(getFragmentManager())
                .setMinValue(mFromPage)
                .setCurrentValue(mToPage)
                .setMaxValue(pageCount)
                .setRequestCode(TO_PAGE_REQUEST_CODE)
                .setTargetFragment(this)
                .show();
    }

    @ItemSelect(R.id.output_format_spinner)
    public void formatItemSelected(boolean selected, OutputFormat selectedItem) {
        reportNameInput.setError(null);
    }

    @TextChange(R.id.report_name_input)
    final void reportNameChanged() {
        boolean nameValid = isReportNameValid();
        reportNameInput.setError(null);
        if (saveAction != null) {
            saveAction.setIcon(nameValid ? R.drawable.ic_menu_save : R.drawable.ic_menu_save_disabled);
        }
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void disableAllViewsWhileSaving(){
        formatSpinner.setEnabled(false);
        reportNameInput.setEnabled(false);
        rangeControls.setEnabled(false);
        fromPageControl.setEnabled(false);
        toPageControl.setEnabled(false);
    }

    private void enableAllViewsAfterSaving(){
        formatSpinner.setEnabled(true);
        reportNameInput.setEnabled(true);
        rangeControls.setEnabled(true);
        fromPageControl.setEnabled(true);
        toPageControl.setEnabled(true);
    }

    private boolean isReportNameValid() {
        String reportName = reportNameInput.getText().toString();

        if (reportName.trim().isEmpty()) {
            reportNameInput.setError(getString(R.string.sr_error_field_is_empty));
            return false;
        }

        // reserved characters: * \ / " ' : ? | < > + [ ]
        if (FileUtils.nameContainsReservedChars(reportName)) {
            reportNameInput.setError(getString(R.string.sr_error_characters_not_allowed));
            return false;
        }

        return true;
    }

    @Nullable
    private File getAccountReportDir(String reportName) {
        File appFilesDir = getActivity().getExternalFilesDir(null);
        File savedReportsDir = new File(appFilesDir, JasperMobileApplication.SAVED_REPORTS_DIR_NAME);
        Account account = accountManager.getActiveAccount();
        if (account != null) {
            File accountReportDir = new File(savedReportsDir, account.name);
            File reportDir = new File(accountReportDir, reportName);

            if (!reportDir.exists() && !reportDir.mkdirs()) {
                Timber.e("Unable to create %s", savedReportsDir);
                return null;
            }

            return reportDir;
        } else {
            return null;
        }
    }

    private File getSharedReportDir(String reportName) {
        File appFilesDir = getActivity().getExternalFilesDir(null);
        File savedReportsDir = new File(appFilesDir, JasperMobileApplication.SAVED_REPORTS_DIR_NAME);
        File accountReportDir = new File(savedReportsDir, JasperMobileApplication.SHARED_DIR);
        return new File(accountReportDir, reportName);
    }

    private void addSavedItemRecord(File reportFile, OutputFormat fileFormat) {
        Account currentAccount = JasperAccountManager.get(getActivity()).getActiveAccount();
        SavedItems savedItemsEntry = new SavedItems();

        savedItemsEntry.setName(reportNameInput.getText().toString());
        savedItemsEntry.setFilePath(reportFile.getPath());
        savedItemsEntry.setFileFormat(fileFormat.toString());
        savedItemsEntry.setDescription(resource.getDescription());
        savedItemsEntry.setWstype(resource.getResourceType().toString());
        savedItemsEntry.setCreationTime(new Date().getTime());
        savedItemsEntry.setAccountName(currentAccount.name);

        getActivity().getContentResolver().insert(JasperMobileDbProvider.SAVED_ITEMS_CONTENT_URI,
                savedItemsEntry.getContentValues());
    }

    private void setRefreshActionButtonState(boolean refreshing) {
        if (refreshing) {
            saveAction.setActionView(R.layout.actionbar_indeterminate_progress);
        } else {
            saveAction.setActionView(null);
        }
    }

    private void removeArtifacts() {
        removeTemplate();
        removeRecord();
    }

    private void removeTemplate() {
        if (reportFile == null) return;

        File dir = reportFile.getParentFile();
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            Timber.w(TAG, "Failed to remove template file", e);
        }
        Toast.makeText(getActivity(), R.string.sr_failed_to_execute_report, Toast.LENGTH_SHORT).show();
    }

    private void removeRecord() {
        if (recordUri == null) return;

        getActivity().getContentResolver().delete(recordUri, null, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (runningRequests > 0) {
            removeArtifacts();
        }
    }
    //---------------------------------------------------------------------
    // Page Select Listeners
    //---------------------------------------------------------------------

    @Override
    public void onPageSelected(int page, int requestCode) {
        if(requestCode == FROM_PAGE_REQUEST_CODE) {
            boolean isPagePositive = (page > 1);
            boolean isRangeCorrect = (page <= mToPage);
            if (isPagePositive && isRangeCorrect) {
                boolean enableComponent = (page != pageCount);
                toPageControl.setEnabled(enableComponent);

                mFromPage = page;
                fromPageControl.setText(String.valueOf(mFromPage));
            }
        }
        else {
            boolean isRangeCorrect = (page >= mFromPage);
            if (isRangeCorrect) {
                mToPage = page;
                toPageControl.setText(String.valueOf(mToPage));
            }
        }
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    private class RunReportExecutionListener extends SimpleRequestListener<ReportExecutionResponse> {
        private OutputFormat outputFormat;

        private RunReportExecutionListener(OutputFormat outputFormat) {
            runningRequests++;
            this.outputFormat = outputFormat;
        }

        @Override
        protected Context getContext() {
            return getActivity();
        }

        @Override
        public void onRequestFailure(SpiceException exception) {
            super.onRequestFailure(exception);
            setRefreshActionButtonState(false);
            enableAllViewsAfterSaving();
            runningRequests--;
            removeArtifacts();
        }

        @Override
        public void onRequestSuccess(ReportExecutionResponse response) {
            runningRequests--;

            ExportExecution execution = response.getExports().get(0);
            String exportOutput = execution.getId();
            String executionId = response.getRequestId();

            // save report file
            SaveExportOutputRequest outputRequest = new SaveExportOutputRequest(jsRestClient,
                    executionId, exportOutput, reportFile);
            requests.add(outputRequest);
            getSpiceManager().execute(outputRequest, new ReportFileSaveListener(outputFormat));

            // save attachments
            if (OutputFormat.HTML == outputFormat) {
                for (ReportOutputResource attachment : execution.getAttachments()) {
                    String attachmentName = attachment.getFileName();
                    File attachmentFile = new File(reportFile.getParentFile(), attachmentName);

                    SaveExportAttachmentRequest attachmentRequest = new SaveExportAttachmentRequest(jsRestClient,
                            executionId, exportOutput, attachmentName, attachmentFile);
                    requests.add(attachmentRequest);
                    getSpiceManager().execute(attachmentRequest, new AttachmentFileSaveListener());
                }
            }
        }
    }

    private class AttachmentFileSaveListener extends SimpleRequestListener<File> {

        private AttachmentFileSaveListener() {
            runningRequests++;
        }

        @Override
        protected Context getContext() {
            return getActivity();
        }

        @Override
        public void onRequestFailure(SpiceException exception) {
            super.onRequestFailure(exception);
            for (SpiceRequest<?> request : requests) {
                runningRequests--;
                request.cancel();
            }
            removeArtifacts();
            setRefreshActionButtonState(false);
            enableAllViewsAfterSaving();
        }

        @Override
        public void onRequestSuccess(File outputFile) {
            runningRequests--;

            if (runningRequests == 0) {
                // activity is done and should be closed
                setRefreshActionButtonState(false);
                enableAllViewsAfterSaving();
                Toast.makeText(getActivity(), R.string.sr_t_report_saved, Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }

    }

    private class ReportFileSaveListener extends AttachmentFileSaveListener {
        private OutputFormat outputFormat;

        private ReportFileSaveListener(OutputFormat outputFormat) {
            super();
            this.outputFormat = outputFormat;
        }

        @Override
        public void onRequestSuccess(File outputFile) {
            addSavedItemRecord(reportFile, outputFormat);
            super.onRequestSuccess(outputFile);
        }
    }
}