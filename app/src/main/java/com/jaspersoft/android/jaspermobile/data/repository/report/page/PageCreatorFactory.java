package com.jaspersoft.android.jaspermobile.data.repository.report.page;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.domain.PageRequest;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.sdk.service.report.ReportExecution;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@Singleton
public class PageCreatorFactory {
    private final Context mContext;

    @Inject
    public PageCreatorFactory(@ApplicationContext Context context) {
        mContext = context;
    }

    @NonNull
    public PageCreator create(PageRequest pageRequest, ReportExecution reportExecution) {
        String format = pageRequest.getFormat();
        if ("html".equals(format.toLowerCase())) {
            return new HtmlPageCreator(mContext, pageRequest, reportExecution);
        }
        return new RawPageCreator(pageRequest, reportExecution);
    }
}
