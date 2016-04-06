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

package com.jaspersoft.android.jaspermobile.internal.di.modules.screen.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.jaspersoft.android.jaspermobile.domain.entity.JasperResource;
import com.jaspersoft.android.jaspermobile.domain.model.JasperResourceModel;
import com.jaspersoft.android.jaspermobile.internal.di.ActivityContext;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.ui.presenter.CatalogPresenter;
import com.jaspersoft.android.jaspermobile.ui.presenter.ResourceCatalogPresenter;
import com.jaspersoft.android.jaspermobile.ui.presenter.resources.JasperResourcePresenterBinder;
import com.jaspersoft.android.jaspermobile.ui.presenter.resources.ResourcePresenterBinder;
import com.jaspersoft.android.jaspermobile.ui.view.component.ResourcesAdapter;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.JasperResourceViewHolder;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.factory.GridChooseReportViewHolderFactory;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.factory.GridResourceViewHolderFactory;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.factory.ListChooseReportViewHolderFactory;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.factory.ListResourceViewHolderFactory;

import dagger.Module;
import dagger.Provides;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@Module
public class ChooserReportActivityModule extends ActivityModule {
    public ChooserReportActivityModule(FragmentActivity activity) {
        super(activity);
    }

    @Provides
    @PerActivity
    CatalogPresenter provideCatalogPresenter(ResourceCatalogPresenter presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    ListResourceViewHolderFactory<JasperResource, JasperResourceViewHolder> providesListResourceViewHolderFactory(ListChooseReportViewHolderFactory factory) {
        return factory;
    }

    @Provides
    @PerActivity
    GridResourceViewHolderFactory<JasperResource, JasperResourceViewHolder> providesGridResourceViewHolderFactory(GridChooseReportViewHolderFactory factory) {
        return factory;
    }

    @Provides
    @PerActivity
    ResourcePresenterBinder<JasperResource, JasperResourceViewHolder, JasperResourceModel> provideResourcePresenterBinder(JasperResourcePresenterBinder binder) {
        return binder;
    }

    @Provides
    @PerActivity
    ResourcesAdapter<?, ?, ?> provideResourcesAdapter(ResourcesAdapter<JasperResource, JasperResourceViewHolder, JasperResourceModel> adapter) {
        return adapter;
    }
}
