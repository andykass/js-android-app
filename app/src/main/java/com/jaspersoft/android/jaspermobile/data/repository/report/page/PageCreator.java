package com.jaspersoft.android.jaspermobile.data.repository.report.page;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.domain.ReportPage;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public abstract class PageCreator {
    @NonNull
    public abstract ReportPage create() throws Exception;
}
