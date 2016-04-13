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

package com.jaspersoft.android.jaspermobile.ui.view.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.entity.Resource;
import com.jaspersoft.android.jaspermobile.ui.contract.CatalogContract;
import com.jaspersoft.android.jaspermobile.ui.view.component.ResourcesAdapter;
import com.jaspersoft.android.jaspermobile.widget.JasperRecyclerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@EViewGroup
public abstract class CatalogView extends FrameLayout implements CatalogContract.View, SwipeRefreshLayout.OnRefreshListener {

    private final static CatalogContract.EventListener EMPTY = new EmptyEventListener();

    @ViewById(android.R.id.list)
    JasperRecyclerView resourcesList;
    @ViewById(R.id.refreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @ViewById(android.R.id.empty)
    TextView message;

    @Inject
    @Named("THRESHOLD")
    protected int mTreshold;

    @Inject
    ResourcesAdapter<?, ?, ?> mAdapter;

    private CatalogContract.EventListener mEventListener = EMPTY;
    private boolean mInited;

    public CatalogView(Context context) {
        super(context);
    }

    public CatalogView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CatalogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEventListener(CatalogContract.EventListener eventListener) {
        mEventListener = eventListener;
    }

    @AfterViews
    void initViews() {
        ((SimpleItemAnimator) resourcesList.getItemAnimator()).setSupportsChangeAnimations(false);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.js_blue,
                R.color.js_dark_blue,
                R.color.js_blue,
                R.color.js_dark_blue);

        resourcesList.addOnScrollListener(new ScrollListener());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        resourcesList.setAdapter(mAdapter);
        if (mInited) return;

        mEventListener.onInit();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.message = message.getText().toString();
        ss.refreshing = swipeRefreshLayout.isRefreshing();
        ss.loading = mAdapter.isLoading();
        ss.resources = new ArrayList<>(mAdapter.getResources());
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        mAdapter.setResource(ss.resources);
        if (ss.loading) {
            mAdapter.showLoading();
        }
        swipeRefreshLayout.setRefreshing(ss.refreshing);
        message.setText(ss.message);

        mInited = true;
    }

    @Override
    public void showResources(List<Resource> resources) {
        mAdapter.setResource(resources);
    }

    @Override
    public void showError() {
        mAdapter.setResource(new ArrayList<Resource>());
    }

    @Override
    public void updateResource(int id) {
        mAdapter.notifyItemChangedById(id);
    }

    @Override
    public void showFirstLoading() {
        if (mAdapter.getItemCount() > 0) {
            swipeRefreshLayout.setRefreshing(true);
        } else {
            message.setText(R.string.loading_msg);
        }
    }

    @Override
    public void showNextLoading() {
        mAdapter.showLoading();
    }

    @Override
    public void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
        message.setText("");
        mAdapter.hideLoading();
    }

    @Override
    public void onRefresh() {
        mEventListener.onRefresh();
    }

    private static final class EmptyEventListener implements CatalogContract.EventListener {

        @Override
        public void onInit() {

        }

        @Override
        public void onRefresh() {

        }

        @Override
        public void onScrollToEnd() {

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
                mEventListener.onScrollToEnd();
            }
        }
    }

    //---------------------------------------------------------------------
    // Saved state
    //---------------------------------------------------------------------

    static class SavedState extends BaseSavedState {
        private final static String MESSAGE_KEY = "message";
        private final static String REFRESHING_KEY = "refreshing";
        private final static String LOADING_KEY = "loading";
        private final static String RESOURCES_KEY = "resources";

        String message;
        boolean refreshing;
        boolean loading;
        ArrayList<Resource> resources;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);

            Bundle bundle = in.readBundle();
            message = bundle.getString(MESSAGE_KEY);
            refreshing = bundle.getBoolean(REFRESHING_KEY);
            loading = bundle.getBoolean(LOADING_KEY);
            resources = (ArrayList<Resource>) bundle.getSerializable(RESOURCES_KEY);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            Bundle state = new Bundle();
            state.putString(MESSAGE_KEY, message);
            state.putBoolean(REFRESHING_KEY, refreshing);
            state.putBoolean(LOADING_KEY, loading);
            state.putSerializable(RESOURCES_KEY, resources);

            out.writeBundle(state);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
