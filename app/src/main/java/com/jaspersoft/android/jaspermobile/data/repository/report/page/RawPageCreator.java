package com.jaspersoft.android.jaspermobile.data.repository.report.page;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.domain.PageRequest;
import com.jaspersoft.android.jaspermobile.domain.ReportPage;
import com.jaspersoft.android.sdk.service.data.report.PageRange;
import com.jaspersoft.android.sdk.service.data.report.ReportExportOutput;
import com.jaspersoft.android.sdk.service.report.ReportExecution;
import com.jaspersoft.android.sdk.service.report.ReportExport;
import com.jaspersoft.android.sdk.service.report.ReportExportOptions;
import com.jaspersoft.android.sdk.service.report.ReportFormat;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;

/**
 * @author Tom Koptel
 * @since 2.5
 */
class RawPageCreator extends PageCreator {
    private final PageRequest mPageRequest;
    private final ReportExecution mExecution;

    RawPageCreator(PageRequest pageRequest, ReportExecution execution) {
        mPageRequest = pageRequest;
        mExecution = execution;
    }

    @NonNull
    @Override
    public ReportPage create() throws Exception {
        String range = mPageRequest.getRange();
        PageRange pageRange = PageRange.parse(range);

        ReportExportOptions options = ReportExportOptions.builder()
                .withFormat(ReportFormat.valueOf(mPageRequest.getFormat()))
                .withPageRange(pageRange)
                .build();

        ReportExport export = mExecution.export(options);
        ReportExportOutput output = export.download();

        InputStream reportExport = output.getStream();

        try {
            byte[] content = IOUtils.toByteArray(reportExport);
            return new ReportPage(content, output.isFinal());
        } finally {
            IOUtils.closeQuietly(reportExport);
        }
    }
}
