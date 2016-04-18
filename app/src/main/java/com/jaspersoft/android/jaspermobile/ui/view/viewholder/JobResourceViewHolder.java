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

package com.jaspersoft.android.jaspermobile.ui.view.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.entity.ResourceIcon;
import com.jaspersoft.android.jaspermobile.ui.contract.JobResourceContract;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class JobResourceViewHolder extends ResourceViewHolder implements JobResourceContract.View, Toolbar.OnMenuItemClickListener {
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    private JobResourceContract.EventListener mEventListener;
    private Context mContext;

    protected TextView tvName;
    protected TextView tvDescription;
    protected ImageView ivIcon;
    protected Toolbar actionsToolbar;
    protected ProgressBar progressBar;

    public JobResourceViewHolder(View itemView) {
        super(itemView);

        this.tvName = (TextView) itemView.findViewById(android.R.id.text1);
        this.tvDescription = (TextView) itemView.findViewById(android.R.id.text2);
        this.actionsToolbar = (Toolbar) itemView.findViewById(R.id.actionsToolbar);
        this.progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        this.ivIcon = (ImageView) itemView.findViewById(android.R.id.icon);

        actionsToolbar.setOnMenuItemClickListener(this);
        mContext = itemView.getContext();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventListener.onSelect();
            }
        });
    }

    @Override
    public void showTitle(String title) {
        tvName.setText(title);
    }

    @Override
    public void showActions() {
        actionsToolbar.getMenu().clear();
    }

    @Override
    public void showSubTitle(Date nextFireDate) {
        String runDateString;

        if (nextFireDate != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT, Locale.getDefault());
            runDateString = simpleDateFormat.format(nextFireDate);
        } else {
            runDateString = "--";
        }

        tvDescription.setText(mContext.getString(R.string.sch_next_run_label, runDateString));
    }

    @Override
    public void showProgress(boolean enabled) {
        progressBar.setVisibility(enabled ? View.GONE : View.VISIBLE);
        if (enabled) return;

        actionsToolbar.getMenu().clear();
    }

    @Override
    public void showEnabled(boolean enabled) {
        itemView.setBackgroundColor(enabled ? Color.WHITE : Color.LTGRAY);
        ivIcon.setAlpha(enabled ? 255 : 160);

        if (enabled) {
            actionsToolbar.inflateMenu(R.menu.job_item_menu_enabled);
        } else {
            actionsToolbar.inflateMenu(R.menu.job_item_menu_disabled);
        }
    }

    @Override
    public void showImage() {
        ivIcon.setBackgroundResource(R.drawable.bg_resource_icon_grey);
        ivIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivIcon.setImageResource(R.drawable.ic_report);
    }

    @Override
    public void showThumbnail(ResourceIcon resourceIcon) {
        ivIcon.setImageBitmap(resourceIcon.getIcon());
        ivIcon.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void setEventListener(JobResourceContract.EventListener eventListener) {
        mEventListener = eventListener;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteAction:
                mEventListener.onDeleteAction();
                return true;
            case R.id.editAction:
                mEventListener.onEditAction();
                return true;
            case R.id.enableAction:
                mEventListener.onEnableAction();
                return true;
            case R.id.disableAction:
                mEventListener.onEnableAction();
                return true;
        }
        return false;
    }
}
