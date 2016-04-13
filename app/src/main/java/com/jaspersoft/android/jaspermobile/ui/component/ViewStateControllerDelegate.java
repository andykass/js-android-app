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

package com.jaspersoft.android.jaspermobile.ui.component;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jaspersoft.android.jaspermobile.ui.contract.Contract;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class ViewStateControllerDelegate<VT extends Contract.View, EL extends Contract.EventListener<VT>> {
    private boolean isInitialized;
    private EL mEventListener;

    public final void setEventListener(EL eventListener) {
        mEventListener = eventListener;
    }

    public final EL getEveltListener(){
        return mEventListener;
    }

    public final void onRestoreInstanceState() {
        isInitialized = true;
    }

    public final void onAttachedToWindow(VT view) {
        if (mEventListener == null) return;
        mEventListener.onBind(view, isInitialized);
    }

    public final void onDetachedFromWindow() {
        if (mEventListener == null) return;
        mEventListener.onUnbind();
    }

    public final void setupUpNavigation(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventListener == null) return;
                mEventListener.onUpNavigate();
            }
        });
    }
}
