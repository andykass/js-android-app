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

package com.jaspersoft.android.jaspermobile.activities.library.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.info.ResourceInfoActivity_;
import com.jaspersoft.android.jaspermobile.activities.library.LibrarySearchableActivity_;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.domain.SearchResult;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.SearchResourcesCase;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;
import com.jaspersoft.android.jaspermobile.util.DefaultPrefHelper;
import com.jaspersoft.android.jaspermobile.util.FavoritesHelper;
import com.jaspersoft.android.jaspermobile.util.ResourceOpener;
import com.jaspersoft.android.jaspermobile.util.ViewType;
import com.jaspersoft.android.jaspermobile.util.VoiceRecognitionHelper;
import com.jaspersoft.android.jaspermobile.util.filtering.LibraryResourceFilter;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceAdapter;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceConverter;
import com.jaspersoft.android.jaspermobile.util.sorting.SortOrder;
import com.jaspersoft.android.jaspermobile.widget.JasperRecyclerView;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookupSearchCriteria;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Subscriber;
import timber.log.Timber;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment(R.layout.fragment_refreshable_resource)
public class LibraryFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = LibraryFragment.class.getSimpleName();
    public static final String ROOT_URI = "/";
    // Loader actions
    private static final int LOAD_FROM_CACHE = 1;
    private static final int LOAD_FROM_NETWORK = 2;

    protected JasperRecyclerView listView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected TextView emptyText;

    @InstanceState
    @FragmentArg
    protected String resourceLabel;
    @InstanceState
    @FragmentArg
    protected SortOrder sortOrder;
    @InstanceState
    @FragmentArg
    protected String query;

    @FragmentArg
    protected ViewType viewType;

    @Inject
    @Named("LIMIT")
    protected int mLimit;
    @Inject
    @Named("THRESHOLD")
    protected int mTreshold;
    @Inject
    protected Analytics analytics;
    @Inject
    protected JasperResourceConverter jasperResourceConverter;

    @Inject
    protected SearchResourcesCase mSearchResourcesCase;
    @Inject
    protected FavoritesHelper favoritesHelper;

    @InstanceState
    protected boolean mLoading;
    @InstanceState
    protected boolean mHasNextPage = true;

    @Bean
    protected LibraryResourceFilter libraryResourceFilter;
    @Bean
    protected DefaultPrefHelper prefHelper;
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
        mSearchCriteria.setForceFullPage(true);
        mSearchCriteria.setLimit(mLimit);
        mSearchCriteria.setRecursive(true);
        mSearchCriteria.setTypes(libraryResourceFilter.getCurrent().getValues());
        mSearchCriteria.setFolderUri(ROOT_URI);
        if (!TextUtils.isEmpty(query)) {
            mSearchCriteria.setQuery(query);
        }
        if (sortOrder != null) {
            mSearchCriteria.setSortBy(sortOrder.getValue());
        }
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

        listView.addOnScrollListener(new ScrollListener());
        setDataAdapter();
        loadFirstPage();
    }

    @Override
    public void onResume() {
        super.onResume();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (TextUtils.isEmpty(resourceLabel)) {
                actionBar.setTitle(getString(R.string.h_library_label));
            } else {
                actionBar.setTitle(resourceLabel);
            }
        }

        List<Analytics.Dimension> viewDimension = new ArrayList<>();
        viewDimension.add(new Analytics.Dimension(Analytics.Dimension.FILTER_TYPE_HIT_KEY, libraryResourceFilter.getCurrent().getName()));
        viewDimension.add(new Analytics.Dimension(Analytics.Dimension.RESOURCE_VIEW_HIT_KEY, viewType.name()));
        analytics.sendScreenView(Analytics.ScreenName.LIBRARY.getValue(), viewDimension);
    }

    @Override
    public void onPause() {
        swipeRefreshLayout.clearAnimation();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        listView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        mSearchResourcesCase.unsubscribe();
        super.onDestroyView();
    }

    public void setQuery(String query) {
        this.query = query;
    }

    //---------------------------------------------------------------------
    // Implements SwipeRefreshLayout.OnRefreshListener
    //---------------------------------------------------------------------

    @Override
    public void onRefresh() {
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
        loadFirstPage();

        analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.REFRESHED.getValue(), Analytics.EventLabel.LIBRARY.getValue());
    }

    //---------------------------------------------------------------------
    // Implements ResourcesLoader
    //---------------------------------------------------------------------

    public void loadResourcesByTypes() {
        analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.FILTERED.getValue(), libraryResourceFilter.getCurrent().getName());

        mSearchCriteria.setTypes(libraryResourceFilter.getCurrent().getValues());
        loadFirstPage();
    }

    public void loadResourcesBySortOrder(SortOrder order) {
        analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.SORTED.getValue(), sortOrder.name());

        sortOrder = order;
        mSearchCriteria.setSortBy(order.getValue());
        loadFirstPage();
    }

    public void handleVoiceCommand(ArrayList<String> matches) {
        VoiceRecognitionHelper.VoiceCommand voiceCommand = VoiceRecognitionHelper.parseCommand(matches);
        String voiceCommandName;
        switch (voiceCommand.getCommand()) {
            case VoiceRecognitionHelper.VoiceCommand.FIND:
                Intent searchIntent = LibrarySearchableActivity_
                        .intent(getActivity())
                        .query(voiceCommand.getArgument())
                        .get();
                getActivity().startActivity(searchIntent);
                voiceCommandName = Analytics.EventLabel.FIND.getValue();
                break;
            case VoiceRecognitionHelper.VoiceCommand.RUN:
                requestResourceLookup(voiceCommand.getArgument());
                voiceCommandName = Analytics.EventLabel.RUN.getValue();
                break;
            default:
                Toast.makeText(getActivity(), R.string.voice_command_undefined, Toast.LENGTH_SHORT).show();
                voiceCommandName = Analytics.EventLabel.UNDEFINED.getValue();
                break;
        }
        analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.SAID_COMMANDS.getValue(), voiceCommandName);
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

    private void loadFirstPage() {
        clearData();
        mSearchCriteria.setOffset(0);
        mSearchResourcesCase.unsubscribe();
        loadResources();
    }

    private void loadNextPage() {
        if (!mLoading && mHasNextPage) {
            int currentOffset = mSearchCriteria.getOffset();
            mSearchCriteria.setOffset(currentOffset + mLimit);
            loadResources();

            analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.LOADED_NEXT.getValue(), Analytics.EventLabel.LIBRARY.getValue());
        }
    }

    private void loadResources() {
        mSearchResourcesCase.unsubscribe();
        setRefreshState(true);
        showEmptyTextIfNoItems(R.string.loading_msg);
        mSearchResourcesCase.execute(mSearchCriteria, new GetResourceLookupsListener());
    }

    private void requestResourceLookup(String label) {
        List<String> resTypes = Collections.singletonList(
                ResourceLookup.ResourceType.reportUnit.name()
        );
        ResourceLookupSearchCriteria criteria = new ResourceLookupSearchCriteria();
        criteria.setFolderUri(ROOT_URI);
        criteria.setTypes(resTypes);
        criteria.setQuery(label);
        criteria.setRecursive(true);
        criteria.setOffset(0);
        criteria.setLimit(1);
        mSearchResourcesCase.execute(criteria, new GetResourceMetadataListener(label));
    }

    private void showEmptyTextIfNoItems(int resId) {
        boolean noItems = (mAdapter.getItemCount() > 0);
        emptyText.setVisibility(noItems ? View.GONE : View.VISIBLE);
        if (resId != 0) emptyText.setText(resId);
    }

    private void setRefreshState(boolean refreshing) {
        mLoading = refreshing;
        if (!refreshing) {
            swipeRefreshLayout.setRefreshing(false);
            mAdapter.hideLoading();
        } else {
            mAdapter.showLoading();
        }
    }

    private void hideLoading() {
        ProgressDialogFragment.dismiss(getActivity().getSupportFragmentManager());
    }

    private void showLoading() {
        ProgressDialogFragment.builder(getActivity().getSupportFragmentManager())
                .setLoadingMessage(R.string.loading_msg)
                .show();
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
            RequestExceptionHandler.showAuthErrorIfExists(getContext(), e);
            showEmptyTextIfNoItems(R.string.failed_load_data);
            setRefreshState(false);
        }

        @Override
        public void onNext(SearchResult result) {
            mHasNextPage = !result.isReachedEnd();
            addData(result.getLookups());
            showEmptyTextIfNoItems(R.string.resources_not_found);
        }
    }

    private class GetResourceMetadataListener extends Subscriber<SearchResult> {
        private String mResourceQuery;

        public GetResourceMetadataListener(String mResourceQuery) {
            this.mResourceQuery = mResourceQuery;
        }

        @Override
        public void onStart() {
            showLoading();
        }

        @Override
        public void onCompleted() {
            hideLoading();
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "LibraryFragment#GetResourceMetadataListener failed");
            RequestExceptionHandler.showAuthErrorIfExists(getContext(), e);
            hideLoading();
        }

        @Override
        public void onNext(SearchResult searchResult) {
            List<ResourceLookup> resourceLookupsList = searchResult.getLookups();
            if (resourceLookupsList.isEmpty()) {
                Toast.makeText(getActivity(), "Can not find " + "\"" + mResourceQuery + "\"", Toast.LENGTH_SHORT).show();
            } else {
                resourceOpener.openResource(LibraryFragment.this, resourceLookupsList.get(0));
            }
        }
    }

    //---------------------------------------------------------------------
    // Implements AbsListView.OnScrollListener
    //---------------------------------------------------------------------

    private class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = recyclerView.getLayoutManager().getItemCount();
            int firstVisibleItem;

            if (layoutManager instanceof LinearLayoutManager) {
                firstVisibleItem = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
            } else {
                firstVisibleItem = ((GridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
            }

            if (totalItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount - mTreshold) {
                loadNextPage();
            }
        }
    }
}
