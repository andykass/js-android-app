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

package com.jaspersoft.android.jaspermobile.activities.save;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;

import com.jaspersoft.android.jaspermobile.data.JasperRestClient;
import com.jaspersoft.android.jaspermobile.data.cache.report.ReportParamsCache;
import com.jaspersoft.android.jaspermobile.data.entity.ExportBundle;
import com.jaspersoft.android.jaspermobile.data.entity.mapper.ReportParamsMapper;
import com.jaspersoft.android.jaspermobile.db.model.SavedItems;
import com.jaspersoft.android.jaspermobile.db.provider.JasperMobileDbProvider;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.ComponentProviderDelegate;
import com.jaspersoft.android.jaspermobile.util.SavedItemHelper;
import com.jaspersoft.android.jaspermobile.util.SavedItemHelper_;
import com.jaspersoft.android.sdk.client.oxm.report.ReportParameter;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.service.data.report.PageRange;
import com.jaspersoft.android.sdk.service.data.report.ReportExportOutput;
import com.jaspersoft.android.sdk.service.data.report.ResourceOutput;
import com.jaspersoft.android.sdk.service.exception.ServiceException;
import com.jaspersoft.android.sdk.service.report.ReportAttachment;
import com.jaspersoft.android.sdk.service.report.ReportExecution;
import com.jaspersoft.android.sdk.service.report.ReportExecutionOptions;
import com.jaspersoft.android.sdk.service.report.ReportExport;
import com.jaspersoft.android.sdk.service.report.ReportExportOptions;
import com.jaspersoft.android.sdk.service.report.ReportFormat;
import com.jaspersoft.android.sdk.service.report.ReportService;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class ReportDownloadManager {

    @Inject
    protected Profile mProfile;
    @Inject
    protected JasperRestClient mRestClient;
    @Inject
    protected ReportParamsCache mReportParamsCache;
    @Inject
    protected ReportParamsMapper mReportParamsMapper;

    private SavedItemHelper mSavedItemHelper;

    private Context mContext;
    private HashMap<Uri, AsyncTask> mDownloadsPool;
    private ReportDownloadCallback listener;

    public ReportDownloadManager(Context context) {
        ComponentProviderDelegate.INSTANCE
                .getProfileComponent(context)
                .inject(this);

        mContext = context;
        mDownloadsPool = new HashMap<>();
        mSavedItemHelper = SavedItemHelper_.getInstance_(context);
        listener = ReportDownloadCallback.EMPTY;
    }

    public void downloadReport(ExportBundle exportBundle) {
        Uri reportUri = addSavedItemRecord(exportBundle);
        SaveReportAsyncTask asyncTask = new SaveReportAsyncTask(reportUri);
        mDownloadsPool.put(reportUri, asyncTask);

        AsyncTaskCompat.executeParallel(asyncTask, exportBundle);
    }

    public void cancelDownloading(Uri reportUri) {
        mDownloadsPool.get(reportUri).cancel(true);
    }

    public void setReportDownloadCallback(ReportDownloadCallback listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

    private Uri addSavedItemRecord(ExportBundle bundle) {
        String descriptionExtra = bundle.getDescription();
        String outputFormatExtra = bundle.getFormat();
        File reportFileExtra = bundle.getFile();
        String savedReportNameExtra = bundle.getLabel();

        SavedItems savedItemsEntry = new SavedItems();
        savedItemsEntry.setName(savedReportNameExtra);
        savedItemsEntry.setFilePath(reportFileExtra.getPath());
        savedItemsEntry.setFileFormat(outputFormatExtra);
        savedItemsEntry.setDescription(descriptionExtra);
        savedItemsEntry.setWstype(ResourceLookup.ResourceType.reportUnit.toString());
        savedItemsEntry.setCreationTime(new Date().getTime());
        savedItemsEntry.setAccountName(mProfile.getKey());
        savedItemsEntry.setDownloaded(false);

        return mContext.getContentResolver().insert(JasperMobileDbProvider.SAVED_ITEMS_CONTENT_URI,
                savedItemsEntry.getContentValues());
    }

    private boolean updateSavedItemRecord(Uri recordUri) {
        SavedItems savedItemsEntry = new SavedItems();
        savedItemsEntry.setDownloaded(true);

        return mContext.getContentResolver().update(recordUri, savedItemsEntry.getContentValues(), null, null) == 1;
    }

    private void removeUnsavedItem(Uri recordUri) {
        mSavedItemHelper.deleteSavedItem(recordUri);
    }

    private void saveReport(ExportBundle bundle) throws ServiceException, IOException {
        String reportUri = bundle.getUri();
        String format = bundle.getFormat();
        String range = bundle.getPageRange();
        File file = bundle.getFile();

        ReportFormat reportFormat = ReportFormat.valueOf(format);

        PageRange pageRange = null;
        if (range != null) {
            pageRange = PageRange.parse(range);
        }

        List<ReportParameter> parameters = mReportParamsCache.get(reportUri);
        List<com.jaspersoft.android.sdk.network.entity.report.ReportParameter> params =
                mReportParamsMapper.legacyParamsToRetrofitted(parameters);

        ReportService reportService = mRestClient.syncReportService();
        ReportExecutionOptions execOptions = ReportExecutionOptions.builder()
                .withInteractive(false)
                .withFormat(reportFormat)
                .withAttachmentPrefix("./")
                .withPageRange(pageRange)
                .withParams(params)
                .build();
        ReportExecution run = reportService.run(reportUri, execOptions);

        ReportExportOptions exportOptions = ReportExportOptions.builder()
                .withAttachmentPrefix("./")
                .withFormat(reportFormat)
                .withPageRange(pageRange)
                .build();
        ReportExport export = run.export(exportOptions);
        ReportExportOutput output = export.download();

        FileUtils.copyInputStreamToFile(output.getStream(), file);
        if ("HTML".equals(format)) {
            List<ReportAttachment> attachments = export.getAttachments();

            for (ReportAttachment attachment : attachments) {
                File attachmentFile = new File(file.getParentFile(), attachment.getFileName());
                ResourceOutput resourceOutput = attachment.download();
                FileUtils.copyInputStreamToFile(resourceOutput.getStream(), attachmentFile);
            }
        }
    }

    public interface ReportDownloadCallback {
        ReportDownloadCallback EMPTY = new ReportDownloadCallback() {
            @Override
            public void onDownloadCountChange(int count) {
            }

            @Override
            public void onDownloadComplete(String reportName) {
            }

            @Override
            public void onDownloadFailed(String reportName) {
            }

            @Override
            public void onDownloadCanceled() {

            }
        };

        void onDownloadCountChange(int count);

        void onDownloadComplete(String reportName);

        void onDownloadFailed(String reportName);

        void onDownloadCanceled();
    }

    private class SaveReportAsyncTask extends AsyncTask<ExportBundle, Void, Boolean> {
        private Uri mReportUri;
        private String mReportName;

        public SaveReportAsyncTask(Uri reportUri) {
            this.mReportUri = reportUri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            listener.onDownloadCountChange(mDownloadsPool.size());
        }

        @Override
        protected Boolean doInBackground(ExportBundle... params) {
            ExportBundle exportBundle = params[0];
            mReportName = exportBundle.getLabel();

            try {
                saveReport(exportBundle);
                return true;
            } catch (ServiceException | IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            mDownloadsPool.remove(mReportUri);
            listener.onDownloadCountChange(mDownloadsPool.size());

            if (success && updateSavedItemRecord(mReportUri)) {
                listener.onDownloadComplete(mReportName);
            } else {
                removeUnsavedItem(mReportUri);
                listener.onDownloadFailed(mReportName);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            removeUnsavedItem(mReportUri);
            mDownloadsPool.remove(mReportUri);

            listener.onDownloadCanceled();
            listener.onDownloadCountChange(mDownloadsPool.size());
        }
    }
}
