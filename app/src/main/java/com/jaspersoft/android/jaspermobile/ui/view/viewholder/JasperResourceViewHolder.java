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

import android.view.View;

import com.jaspersoft.android.jaspermobile.domain.entity.ResourceIcon;
import com.jaspersoft.android.jaspermobile.ui.contract.JasperResourceContract;
import com.jaspersoft.android.jaspermobile.ui.contract.JobResourceContract;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class JasperResourceViewHolder extends ResourceViewHolder implements JasperResourceContract.View{

    private JasperResourceContract.EventListener mEventListener;

    public JasperResourceViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void showThumbnail(ResourceIcon resourceIcon) {

    }

    @Override
    public void setEventListener(JasperResourceContract.EventListener eventListener) {
        mEventListener = eventListener;
    }

    @Override
    public void showTitle(String title) {

    }

    @Override
    public void showActions() {

    }

    @Override
    public void showSubTitle(String subtitle) {

    }

    @Override
    public void showImage() {

    }

    public JasperResourceContract.EventListener getEventListener() {
        return mEventListener;
    }
}
