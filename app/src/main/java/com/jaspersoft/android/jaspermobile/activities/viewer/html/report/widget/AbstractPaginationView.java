/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
 *  http://community.jaspersoft.com/project/jaspermobile-android
 *
 *  Unless you have purchased a commercial license agreement from Jaspersoft,
 *  the following license terms apply:
 *
 *  This program is part of Jaspersoft Mobile for Android.
 *
 *  Jaspersoft Mobile is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Jaspersoft Mobile is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Jaspersoft Mobile for Android. If not, see
 *  <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.activities.viewer.html.report.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author Tom Koptel
 * @since 2.0
 */
public abstract class AbstractPaginationView extends RelativeLayout {
    public static final int FIRST_PAGE = 1;

    private static final String INSTANCE_STATE = "INSTANCE_STATE";
    private static final String CURRENT_PAGE = "CURRENT_PAGE";
    private static final String TOTAL_PAGE = "TOTAL_PAGE";

    protected OnPageChangeListener onPageChangeListener;

    private int currentPage = FIRST_PAGE;
    private int mTotalPages = -1;

    public AbstractPaginationView(Context context) {
        super(context);
    }

    public AbstractPaginationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractPaginationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbstractPaginationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    public void updateCurrentPage(int page) {
        currentPage = page;
        alterControlStates();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void updateTotalCount(int totalPage) {
        mTotalPages = totalPage;
        alterTotalCount();
    }

    public boolean isTotalPagesLoaded() {
        return mTotalPages != -1;
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    public void reset() {
        updateTotalCount(-1);
        updateCurrentPage(AbstractPaginationView.FIRST_PAGE);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        currentPage = bundle.getInt(CURRENT_PAGE);
        mTotalPages = bundle.getInt(TOTAL_PAGE);
        Parcelable instanceState = bundle.getParcelable(INSTANCE_STATE);
        super.onRestoreInstanceState(instanceState);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(CURRENT_PAGE, currentPage);
        bundle.putInt(TOTAL_PAGE, mTotalPages);
        return bundle;
    }

    protected FragmentManager getFragmentManager() {
        FragmentActivity activity = (FragmentActivity) getContext();
        return activity.getSupportFragmentManager();
    }

    /**
     * Method responsible for refreshing data inside associated view.
     * Total page view represents maximum page count for multi page report.
     */
    protected abstract void alterTotalCount();

    /**
     * Method responsible for refreshing data inside control views.
     * Though can be next page, previous page, last and first.
     * In general depends on implementation of corresponding swipe component.
     */
    protected abstract void alterControlStates();

    public interface OnPageChangeListener {
        void onPageSelected(int currentPage);
        void onPagePickerRequested();
    }
}
