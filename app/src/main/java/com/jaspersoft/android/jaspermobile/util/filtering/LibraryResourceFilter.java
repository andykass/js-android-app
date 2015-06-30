package com.jaspersoft.android.jaspermobile.util.filtering;

import android.accounts.Account;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.util.account.AccountServerData;
import com.jaspersoft.android.jaspermobile.util.account.JasperAccountManager;
import com.jaspersoft.android.retrofit.sdk.server.ServerRelease;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Tivodar
 * @since 2.0
 */
@EBean
public class LibraryResourceFilter extends ResourceFilter {

    private ServerRelease serverRelease;
    private boolean isProEdition;

    @RootContext
    protected FragmentActivity activity;

    private enum LibraryFilterCategory {
        all(R.string.s_fd_option_all),
        reports(R.string.s_fd_option_reports),
        dashboards(R.string.s_fd_option_dashboards);

        private int mTitleId = -1;

        LibraryFilterCategory(int titleId) {
            mTitleId = titleId;
        }

        public String getLocalizedTitle(Context context) {
            return context.getString(this.mTitleId);
        }
    }

    @AfterInject
    protected void initFilter() {
        Account account = JasperAccountManager.get(activity).getActiveAccount();
        AccountServerData accountServerData = AccountServerData.get(activity, account);
        this.serverRelease = ServerRelease.parseVersion(accountServerData.getVersionName());
        this.isProEdition = accountServerData.getEdition().equals("PRO");
    }

    @Override
    public String getFilterLocalizedTitle(Filter filter) {
        LibraryFilterCategory libraryFilterCategory = LibraryFilterCategory.valueOf(filter.getName());
        return libraryFilterCategory.getLocalizedTitle(activity);
    }

    @Override
    protected List<Filter> generateAvailableFilterList() {
        ArrayList<Filter> availableFilters = new ArrayList<>();

        availableFilters.add(getFilterAll());

        // Filtration is not available for CE servers
        if (isProEdition) {
            availableFilters.add(getFilterReport());
            availableFilters.add(getFilterDashboard());
        }

        return availableFilters;
    }

    @Override
    protected FilterStorage initFilterStorage() {
        return LibraryFilterStorage_.getInstance_(activity);
    }

    @Override
    protected Filter getDefaultFilter() {
        return getFilterAll();
    }

    private Filter getFilterAll() {
        ArrayList<String> filterValues = new ArrayList<>();
        filterValues.addAll(JasperResources.report());
        filterValues.addAll(JasperResources.dashboard(serverRelease));

        return new Filter(LibraryFilterCategory.all.name(), filterValues);
    }

    private Filter getFilterReport() {
        ArrayList<String> filterValues = new ArrayList<>();
        filterValues.addAll(JasperResources.report());

        return new Filter(LibraryFilterCategory.reports.name(), filterValues);
    }

    private Filter getFilterDashboard() {
        ArrayList<String> filterValues = new ArrayList<>();
        filterValues.addAll(JasperResources.dashboard(serverRelease));

        return new Filter(LibraryFilterCategory.dashboards.name(), filterValues);
    }
}
