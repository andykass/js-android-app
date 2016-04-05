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

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.entity.ResourceIcon;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public abstract class ListJasperResourceViewHolder extends JasperResourceViewHolder{
    protected ImageView ivIcon;
    protected TextView tvName;
    protected TextView tvDescription;
    protected Toolbar actionsToolbar;

    public ListJasperResourceViewHolder(View itemView) {
        super(itemView);

        this.ivIcon = (ImageView) itemView.findViewById(android.R.id.icon);
        this.tvName = (TextView) itemView.findViewById(android.R.id.text1);
        this.tvDescription = (TextView) itemView.findViewById(android.R.id.text2);
        this.actionsToolbar = (Toolbar) itemView.findViewById(R.id.actionsToolbar);

        actionsToolbar.inflateMenu(R.menu.info_item_menu);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEventListener().onSelect();
            }
        });
    }

    @Override
    public void showTitle(String title) {
        tvName.setText(title);
    }

    @Override
    public void showSubTitle(String subtitle) {
        tvDescription.setText(subtitle);
    }

    @Override
    public void showActions() {
        actionsToolbar.getMenu().clear();
        actionsToolbar.inflateMenu(R.menu.info_item_menu);
    }

    @Override
    public void showThumbnail(ResourceIcon resourceIcon) {
        ivIcon.setImageBitmap(resourceIcon.getIcon());
    }
}
