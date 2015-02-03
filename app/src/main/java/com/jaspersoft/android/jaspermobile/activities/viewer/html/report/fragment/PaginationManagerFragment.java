/*
 * Copyright © 2014 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Mobile for Android.
 *
 * Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.activities.viewer.html.report.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboSpiceFragment;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.report.FragmentCreator;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.report.NodePagerAdapter;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.report.support.ReportSession;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.report.widget.PaginationBarView;
import com.jaspersoft.android.jaspermobile.network.UniversalRequestListener;
import com.jaspersoft.android.jaspermobile.widget.JSViewPager;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.async.request.ReportDetailsRequest;
import com.jaspersoft.android.sdk.client.oxm.report.ReportExecutionResponse;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment(R.layout.fragment_pagination_manager)
public class PaginationManagerFragment extends RoboSpiceFragment {

    public static final String TAG = PaginationManagerFragment.class.getSimpleName();

    @Inject
    private JsRestClient jsRestClient;

    @ViewById
    protected View rootContainer;
    @ViewById
    protected PaginationBarView paginationLayout;

    @InstanceState
    protected int mTotalPage;

    @Bean
    protected ReportSession reportSession;

    private JSViewPager viewPager;
    private NodePagerAdapter mAdapter;

    @AfterViews
    final void init() {
        reportSession.registerObserver(sessionObserver);

        viewPager = (JSViewPager) getActivity().findViewById(R.id.viewPager);
        viewPager.setSwipeable(false);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                int currentPage = position + 1;
                paginationLayout.setPage(currentPage);

                boolean showNext = (currentPage == mAdapter.getCount());
                if (paginationLayout.hasTotalCount()) {
                    int totalPages = paginationLayout.getTotalPage();
                    showNext &= (currentPage + 1 <= totalPages);
                }

                if (showNext) {
                    viewPager.setOnPageChangeListener(null);
                    mAdapter.addPage();
                    mAdapter.notifyDataSetChanged();
                    viewPager.setOnPageChangeListener(this);
                }
            }
        });

        mAdapter = new NodePagerAdapter(getFragmentManager(), new FragmentCreator<Fragment, Integer>() {
            @Override
            public Fragment createFragment(Integer page) {
                NodeWebViewFragment nodeWebViewFragment =
                        NodeWebViewFragment_.builder()
                                .page(page)
                                .build();
                nodeWebViewFragment.setOnPageLoadListener(nodeListener);
                return nodeWebViewFragment;
            }
        });
        viewPager.setAdapter(mAdapter);

        paginationLayout.setOnPageChangeListener(new PaginationBarView.OnPageChangeListener() {
            @Override
            public void onPageSelected(final int page) {
                int count = mAdapter.getCount();
                int item = page - 1;
                if (count < page) {
                    mAdapter.setCount(page);
                    mAdapter.notifyDataSetChanged();
                }
                viewPager.setCurrentItem(item);
            }
        });

        if (mTotalPage != 0) {
            paginationLayout.showTotalCount(mTotalPage);
            showPaginationControl();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        reportSession.removeObserver(sessionObserver);
    }

    public void paginateToCurrentSelection() {
        viewPager.setCurrentItem(paginationLayout.getPage() - 1);
    }

    public void paginateTo(int page) {
        paginationLayout.navigateTo(page);
    }

    public void showTotalPageCount(int totalPageCount) {
        paginationLayout.showTotalCount(totalPageCount);
    }

    public boolean isPaginationLoaded() {
        return paginationLayout.hasTotalCount();
    }

    public void loadNextPageInBackground() {
        mAdapter.addPage();
        mAdapter.notifyDataSetChanged();
    }

    public void update() {
        if (!isPaginationLoaded()) {
            requestDetails(reportSession.getRequestId());
        }
    }

    private void requestDetails(String requestId) {
        ReportDetailsRequest reportDetailsRequest = new ReportDetailsRequest(jsRestClient, requestId);
        UniversalRequestListener<ReportExecutionResponse> universalRequestListener =
                UniversalRequestListener.builder(getActivity())
                        .semanticListener(new ReportDetailsRequestListener())
                        .create();
        getSpiceManager().execute(reportDetailsRequest, universalRequestListener);
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void showPaginationControl() {
        rootContainer.setVisibility(View.VISIBLE );

        RelativeLayout htmlViewer = (RelativeLayout)
                getActivity().findViewById(R.id.htmlViewer_layout);
        if (htmlViewer != null) {
            htmlViewer.setPadding(0, 0, 0, paginationLayout.getHeight());
        }
    }

    //---------------------------------------------------------------------
    // Inner classes
    //---------------------------------------------------------------------

    private final ReportSession.SessionObserver sessionObserver =
            new ReportSession.SessionObserver() {
                @Override
                public void onSessionChanged(String requestId) {
                    mAdapter.clear();
                    mAdapter.addPage();
                    mAdapter.notifyDataSetChanged();
                    paginationLayout.setPage(1);
                    viewPager.setCurrentItem(0);
                }
            };

    private final NodeWebViewFragment.OnPageLoadListener nodeListener =
            new NodeWebViewFragment.OnPageLoadListener() {
                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess(int page) {
                    // This means that we have 2 page loaded
                    // and that is enough to show pagination control
                    if (page == 2) {
                        viewPager.setSwipeable(true);
                        showPaginationControl();
                    }
                }
            };

    private class ReportDetailsRequestListener extends UniversalRequestListener.SimpleSemanticListener<ReportExecutionResponse> {
        @Override
        public final void onSemanticSuccess(ReportExecutionResponse response) {
            int totalPageCount = response.getTotalPages();
            boolean needToShow = (totalPageCount > 1);

            if (needToShow) {
                showTotalPageCount(response.getTotalPages());
            }

            if (totalPageCount == 0) {
                ReportExecutionFragment reportExecutionFragment = (ReportExecutionFragment)
                        getFragmentManager().findFragmentByTag(ReportExecutionFragment.TAG);
                reportExecutionFragment.showEmptyReportOptionsDialog();
            } else {
                FilterManagerFragment filterManagerFragment = (FilterManagerFragment)
                        getFragmentManager().findFragmentByTag(FilterManagerFragment.TAG);
                filterManagerFragment.makeSnapshot();
            }
        }
    }

}
