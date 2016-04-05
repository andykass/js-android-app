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

package com.jaspersoft.android.jaspermobile.ui.presenter.resources;

import com.jaspersoft.android.jaspermobile.domain.entity.JasperResource;
import com.jaspersoft.android.jaspermobile.domain.entity.ResourceIcon;
import com.jaspersoft.android.jaspermobile.domain.model.JasperResourceModel;
import com.jaspersoft.android.jaspermobile.ui.contract.JasperResourceContract;
import com.jaspersoft.android.jaspermobile.ui.eventbus.JasperResourceBus;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class JasperResourcePresenter extends ResourcePresenter<JasperResourceContract.View, JasperResourceModel, JasperResource> implements JasperResourceContract.EventListener {

    private final JasperResourceBus mJasperResourceBus;

    public JasperResourcePresenter(JasperResourceModel resourceModel, JasperResource jobResource, JasperResourceBus mJasperResourceBus) {
        super(resourceModel, jobResource);
        this.mJasperResourceBus = mJasperResourceBus;
    }

    @Override
    protected void onBindView(JasperResourceContract.View view) {
        super.onBindView(view);

        getView().showImage();
        getView().showSubTitle(getEntity().getDescription());

        ResourceIcon resourceIcon = getModel().getResourceIcon(getEntity().getId());
        if (resourceIcon == null) {
            getModel().requestThumbnail(getEntity().getId(), getEntity().getUri());
        } else {
            getView().showThumbnail(resourceIcon);
        }
    }

    @Override
    public void onSelect() {
        mJasperResourceBus.sendSelectEvent(getEntity());
    }

    @Override
    public void onInfoAction() {

    }
}
