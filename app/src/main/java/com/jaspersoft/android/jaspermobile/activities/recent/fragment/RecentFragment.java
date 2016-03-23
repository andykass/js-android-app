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

package com.jaspersoft.android.jaspermobile.activities.recent.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.info.ResourceInfoActivity_;
import com.jaspersoft.android.jaspermobile.domain.SearchResult;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.SearchResourcesCase;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;
import com.jaspersoft.android.jaspermobile.util.FavoritesHelper;
import com.jaspersoft.android.jaspermobile.util.ResourceOpener;
import com.jaspersoft.android.jaspermobile.util.ViewType;
import com.jaspersoft.android.jaspermobile.util.filtering.RecentlyViewedResourceFilter;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceAdapter;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceConverter;
import com.jaspersoft.android.jaspermobile.util.sorting.SortOrder;
import com.jaspersoft.android.jaspermobile.widget.JasperRecyclerView;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookupSearchCriteria;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import timber.log.Timber;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment(R.layout.fragment_refreshable_resource)
public class RecentFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = RecentFragment.class.getSimpleName();
    public static final String ROOT_URI = "/";

    protected JasperRecyclerView listView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected TextView emptyText;

    @Inject
    protected Analytics analytics;
    @Inject
    protected JasperResourceConverter jasperResourceConverter;
    @Inject
    protected SearchResourcesCase mSearchResourcesCase;
    @Inject
    protected FavoritesHelper favoritesHelper;

    @FragmentArg
    protected ViewType viewType;

    @Bean
    protected RecentlyViewedResourceFilter recentlyViewedResourceFilter;
    @Bean
    protected ResourceOpener resourceOpener;


    private JasperResourceAdapter mAdapter;
    private HashMap<String, ResourceLookup> mResourceLookupHashMap;
    private ResourceLookupSearchCriteria mSearchCriteria;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseActivityComponent().inject(this);

        mResourceLookupHashMap = new HashMap<>();

        mSearchCriteria = new ResourceLookupSearchCriteria();
        mSearchCriteria.setLimit(10);
        mSearchCriteria.setAccessType("viewed");
        mSearchCriteria.setTypes(recentlyViewedResourceFilter.getCurrent().getValues());
        mSearchCriteria.setSortBy(SortOrder.ACCESS_TIME.getValue());
        mSearchCriteria.setFolderUri(ROOT_URI);

        analytics.setScreenName(Analytics.ScreenName.RECENTLY_VIEWED.getValue());
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (JasperRecyclerView) view.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        emptyText = (TextView) view.findViewById(android.R.id.empty);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.js_blue,
                R.color.js_dark_blue,
                R.color.js_blue,
                R.color.js_dark_blue);

        listView.setOnScrollListener(new ScrollListener());
        setDataAdapter();
        loadResources();
    }

    @Override
    public void onResume() {
        super.onResume();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.recent_card_label));
        }

        List<Analytics.Dimension> viewDimension = new ArrayList<>();
        viewDimension.add(new Analytics.Dimension(Analytics.Dimension.RESOURCE_VIEW_HIT_KEY, viewType.name()));
        analytics.sendScreenView(Analytics.ScreenName.RECENTLY_VIEWED.getValue(), viewDimension);
    }

    @Override
    public void onPause() {
        swipeRefreshLayout.clearAnimation();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mSearchResourcesCase.unsubscribe();
        super.onDestroyView();
    }

    //---------------------------------------------------------------------
    // Implements SwipeRefreshLayout.OnRefreshListener
    //---------------------------------------------------------------------

    @Override
    public void onRefresh() {
        clearData();
        loadResources();

        analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.REFRESHED.getValue(), Analytics.EventLabel.RECENTLY_VIEWED.getValue());
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void clearData() {
        mResourceLookupHashMap.clear();
        mAdapter.clear();
    }

    private void addData(List<ResourceLookup> data) {
        mResourceLookupHashMap.putAll(jasperResourceConverter.convertToDataMap(data));
        mAdapter.addAll(jasperResourceConverter.convertToJasperResource(data));
        mAdapter.notifyDataSetChanged();
    }

    private void onViewSingleClick(ResourceLookup resource) {
        resourceOpener.openResource(this, resource);
    }

    private void setDataAdapter() {
        mAdapter = new JasperResourceAdapter(getActivity());
        mAdapter.setOnItemInteractionListener(new JasperResourceAdapter.OnResourceInteractionListener() {
            @Override
            public void onResourceItemClicked(JasperResource jasperResource) {
                onViewSingleClick(mResourceLookupHashMap.get(jasperResource.getId()));
            }

            @Override
            public void onSecondaryActionClicked(JasperResource jasperResource) {
                ResourceInfoActivity_.intent(getActivity())
                        .jasperResource(jasperResource)
                        .start();
            }
        });

        listView.setAdapter(mAdapter);
        listView.changeViewType(viewType);
    }

    private void loadResources() {
        setRefreshState(true);
        showEmptyText(R.string.loading_msg);
        mSearchResourcesCase.execute(mSearchCriteria,  new GetResourceLookupsListener());
    }

    private void showEmptyText(int resId) {
        boolean noItems = (mAdapter.getItemCount() > 0);
        emptyText.setVisibility(noItems ? View.GONE : View.VISIBLE);
        if (resId != 0) emptyText.setText(resId);
    }

    private void setRefreshState(boolean refreshing) {
        if (!refreshing) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    //---------------------------------------------------------------------
    // Inner classes
    //---------------------------------------------------------------------

    private class GetResourceLookupsListener extends Subscriber<SearchResult> {
        @Override
        public void onCompleted() {
            setRefreshState(false);
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "LibraryFragment#GetResourceLookupsListener failed");
            RequestExceptionHandler.showAuthErrorIfExists(getActivity(), e);
            showEmptyText(R.string.failed_load_data);
            setRefreshState(false);
        }


        @Override
        public void onNext(SearchResult searchResult) {
            addData(searchResult.getLookups());
            setRefreshState(false);
            showEmptyText(R.string.resources_not_found);
        }
    }

    //---------------------------------------------------------------------
    // Implements AbsListView.OnScrollListener
    //---------------------------------------------------------------------
    private class ScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            enableRefreshLayout(listView);
        }

        private void enableRefreshLayout(RecyclerView listView) {
            boolean enable = !listView.canScrollVertically(-1);
            swipeRefreshLayout.setEnabled(enable);
        }
    }
}
