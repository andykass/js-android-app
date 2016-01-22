package com.jaspersoft.android.jaspermobile.presentation.view;

import com.jaspersoft.android.jaspermobile.domain.VisualizeTemplate;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.VisualizeViewModel;
import com.jaspersoft.android.jaspermobile.presentation.page.ReportPageState;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public interface ReportVisualizeView extends LoadDataView {
    void setFilterActionVisibility(boolean visibilityFlag);

    void setSaveActionVisibility(boolean visibilityFlag);

    void reloadMenu();

    void showInitialFiltersPage();

    void showPage(String pageContent);

    void showPageOutOfRangeError();


    void setPaginationVisibility(boolean visibility);

    void setPaginationEnabled(boolean enabled);

    void setPaginationCurrentPage(int page);

    void setPaginationTotalPages(int totalPages);

    int getPaginationTotalPages();

    void resetPaginationControl();


    void setWebViewVisibility(boolean visibility);


    void showEmptyPageMessage();

    void hideEmptyPageMessage();


    void loadTemplateInView(VisualizeTemplate template);

    void updateDeterminateProgress(int progress);

    ReportPageState getState();

    VisualizeViewModel getVisualize();
}
