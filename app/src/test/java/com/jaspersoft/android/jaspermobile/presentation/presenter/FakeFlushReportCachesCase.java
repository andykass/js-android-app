package com.jaspersoft.android.jaspermobile.presentation.presenter;

import com.jaspersoft.android.jaspermobile.domain.interactor.report.FlushReportCachesCase;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class FakeFlushReportCachesCase extends FlushReportCachesCase {
    public FakeFlushReportCachesCase() {
        super(null, null, null, null);
    }

    @Override
    public void execute(String reportUri) {

    }
}
