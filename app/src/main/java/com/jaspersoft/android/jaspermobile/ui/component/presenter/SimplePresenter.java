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

package com.jaspersoft.android.jaspermobile.ui.component.presenter;

import com.jaspersoft.android.jaspermobile.ui.contract.Contract;
import com.jaspersoft.android.jaspermobile.ui.navigation.Navigator;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public abstract class SimplePresenter<V extends Contract.View, M extends Contract.Model<C>, C extends Contract.ResultCallback> implements Contract.EventListener<V> {

    @Inject
    Navigator mNavigator;

    @Inject
    M model;
    private V view;

    @Override
    public final void onBind(V view, boolean isInitialized) {
        this.view = view;
        model.subscribe((C) this);

        if (isInitialized) return;

        onInit();
    }

    @Override
    public void onUnbind() {
        view = null;
        model.unsubscribe();
    }

    @Override
    public void onUpNavigate() {
        mNavigator.navigateUp();
    }

    @Override
    public void onDestroy() {
        model.clear();
    }

    public V getView() {
        return view;
    }

    public M getModel() {
        return model;
    }

    public final Navigator getNavigator() {
        return mNavigator;
    }

    protected void onInit() {

    }
}
