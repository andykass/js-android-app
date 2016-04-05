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
import com.jaspersoft.android.jaspermobile.domain.model.JasperResourceModel;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.ui.eventbus.JasperResourceBus;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.JasperResourceViewHolder;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerActivity
public class JasperResourcePresenterBinder extends ResourcePresenterBinder<JasperResource, JasperResourceViewHolder, JasperResourceModel>{

    private final JasperResourceBus mJasperResourceBus;

    @Inject
    public JasperResourcePresenterBinder(JasperResourceBus mJasperResourceBus) {
        this.mJasperResourceBus = mJasperResourceBus;
    }

    @Override
    public void bind(JasperResourceViewHolder holder, JasperResource entity, JasperResourceModel model) {
        JasperResourcePresenter resourcePresenter = new JasperResourcePresenter(model, entity, mJasperResourceBus);
        resourcePresenter.bindView(holder);
        holder.setEventListener(resourcePresenter);
    }
}
