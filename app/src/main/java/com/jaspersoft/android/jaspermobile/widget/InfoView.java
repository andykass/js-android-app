/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@EViewGroup(R.layout.view_info)
public class InfoView extends CardView {

    protected static final String EMPTY_TEXT = "---";

    private LayoutInflater mLayoutInflater;

    @ViewById(R.id.infoDetailsContainer)
    protected LinearLayout infoDataContainer;

    @ViewById(R.id.infoProgress)
    protected ProgressBar infoProgress;

    public InfoView(Context context) {
        super(context);
    }

    public InfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    protected void init() {
        mLayoutInflater = LayoutInflater.from(getContext());
        updateVisibility();
    }

    public void fillWithBaseData(String type, String label, String description, String uri, String creationDate, String modifiedDate) {
        infoDataContainer.removeAllViews();

        addInfoItem(getContext().getString(R.string.ri_type_title), type);
        addInfoItem(getContext().getString(R.string.ri_label_title), label);
        addInfoItem(getContext().getString(R.string.ri_description_title), description);
        addInfoItem(getContext().getString(R.string.ri_uri_title), uri);
        addInfoItem(getContext().getString(R.string.ri_creation_title), creationDate);
        addInfoItem(getContext().getString(R.string.ri_modified_title), modifiedDate);
    }

    public void addInfoItem(String title, String value) {
        if (title == null || value == null) return;

        LinearLayout itemContainer = (LinearLayout) mLayoutInflater.inflate(R.layout.item_info, infoDataContainer, false);
        if (infoDataContainer.getChildCount() > 0) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int itemMargin = (int) getResources().getDimension(R.dimen.info_content_items_distance);
            layoutParams.setMargins(layoutParams.leftMargin, itemMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
            itemContainer.setLayoutParams(layoutParams);
        }

        TextView infoTitle = (TextView) itemContainer.findViewById(R.id.infoTitle);
        TextView infoValue = (TextView) itemContainer.findViewById(R.id.infoValue);

        infoTitle.setText(title);
        infoValue.setText(value.isEmpty() ? EMPTY_TEXT : value);

        infoDataContainer.addView(itemContainer);
        updateVisibility();
    }

    private void updateVisibility() {
        infoProgress.setVisibility(infoDataContainer.getChildCount() > 0 ? INVISIBLE : VISIBLE);
    }
}
