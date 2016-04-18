package com.jaspersoft.android.jaspermobile.data.repository.report.page;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.domain.PageRequest;
import com.jaspersoft.android.jaspermobile.domain.ReportPage;
import com.jaspersoft.android.sdk.service.data.report.PageRange;
import com.jaspersoft.android.sdk.service.data.report.ReportExportOutput;
import com.jaspersoft.android.sdk.service.report.ReportExecution;
import com.jaspersoft.android.sdk.service.report.ReportExport;
import com.jaspersoft.android.sdk.service.report.ReportExportOptions;
import com.jaspersoft.android.sdk.service.report.ReportFormat;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Koptel
 * @since 2.5
 */
final class HtmlPageCreator extends PageCreator {
    private Context mContext;
    private final PageRequest mPageRequest;
    private final ReportExecution mExecution;

    HtmlPageCreator(Context context, PageRequest pageRequest, ReportExecution execution) {
        mContext = context;
        mPageRequest = pageRequest;
        mExecution = execution;
    }

    @Override
    @NonNull
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
        InputStream customScript = mContext.getAssets().open("rest-report.js");

        try {
            Reader reader = new InputStreamReader(customScript);
            Template template = Mustache.compiler().compile(reader);

            int pageIndex = Integer.valueOf(range) - 1;
            Map<String, Object> data = new HashMap<>();
            data.put("jasperPrintName", mExecution.getExecutionId());
            data.put("pageIndex", pageIndex);
            String customJs = template.execute(data);

            DataNode indexHeadScript = DataNode.createFromEncoded("<script>" + customJs + "</script>", "");

            Document document = Jsoup.parse(reportExport, Charset.defaultCharset().name(), "");
            Elements head = document.getElementsByTag("head");
            head.append(indexHeadScript.toString());

            byte[] exportContent = document.toString().getBytes(Charset.forName("UTF-8"));
            return new ReportPage(exportContent, output.isFinal());
        } finally {
            IOUtils.closeQuietly(reportExport);
            IOUtils.closeQuietly(customScript);
        }
    }
}
