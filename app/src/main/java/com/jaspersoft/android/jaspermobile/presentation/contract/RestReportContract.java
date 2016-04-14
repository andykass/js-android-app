package com.jaspersoft.android.jaspermobile.presentation.contract;

import com.jaspersoft.android.jaspermobile.domain.ScreenCapture;
import com.jaspersoft.android.jaspermobile.presentation.page.ReportPageState;
import com.jaspersoft.android.jaspermobile.presentation.view.LoadDataView;

import java.io.File;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public interface RestReportContract {
    interface View extends LoadDataView {
        void setFilterActionVisibility(boolean visibilityFlag);

        void setSaveActionVisibility(boolean visibilityFlag);

        void reloadMenu();

        void showInitialFiltersPage();

        void showPage(String pageContent);

        void showPaginationControl(boolean visibility);

        void resetPaginationControl();

        int getPaginationTotalPages();

        void showTotalPages(int totalPages);

        void showCurrentPage(int page);

        void showPageOutOfRangeError();

        void showEmptyPageMessage();

        void showReloadMessage();

        void showProgress();

        void showPageLoader(boolean visibility);

        ReportPageState getState();

        void showWebView(boolean visibility);

        void navigateToAnnotationPage(File file);
    }

    interface Action {
        void loadPage(String pageRange);

        void runReport();

        void updateReport();

        void refresh();

        void shareReport(ScreenCapture screenCapture);
    }
}
