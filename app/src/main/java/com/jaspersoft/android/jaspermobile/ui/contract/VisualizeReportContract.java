package com.jaspersoft.android.jaspermobile.ui.contract;

import com.jaspersoft.android.jaspermobile.domain.VisualizeTemplate;
import com.jaspersoft.android.jaspermobile.ui.model.visualize.VisualizeViewModel;
import com.jaspersoft.android.jaspermobile.ui.page.ReportPageState;
import com.jaspersoft.android.jaspermobile.ui.view.LoadDataView;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public interface VisualizeReportContract {
    interface View extends LoadDataView {
        void showFilterAction(boolean visibilityFlag);

        void showSaveAction(boolean visibilityFlag);

        void reloadMenu();

        void showInitialFiltersPage();

        void showPageOutOfRangeError();

        void showPagination(boolean visibility);

        void setPaginationEnabled(boolean enabled);

        void setPaginationCurrentPage(int page);

        void setPaginationTotalPages(int totalPages);

        int getPaginationTotalPages();

        void resetPaginationControl();


        void showWebView(boolean visibility);

        void showReloadButton(boolean visibility);


        void showEmptyPageMessage();

        void hideEmptyPageMessage();


        void loadTemplateInView(VisualizeTemplate template);

        void updateDeterminateProgress(int progress);

        void showExternalLink(String externalLink);

        void executeReport(ResourceLookup reportModel);

        void resetZoom();

        ReportPageState getState();

        VisualizeViewModel getVisualize();

        void handleSessionExpiration();
    }

    interface Action {
        void loadPage(String pageRange);

        void runReport();

        void updateReport();

        void refresh();
    }
}
