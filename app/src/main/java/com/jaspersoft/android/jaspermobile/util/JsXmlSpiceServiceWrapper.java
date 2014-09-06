/*
 * Copyright (C) 2012-2014 Jaspersoft Corporation. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Mobile for Android.
 *
 * Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.util;

import android.content.Context;

import com.google.inject.Inject;
import com.jaspersoft.android.sdk.client.async.JsXmlSpiceService;
import com.octo.android.robospice.SpiceManager;

/**
 * @author Tom Koptel
 * @since 1.9
 */
public class JsXmlSpiceServiceWrapper {
    private final SpiceManager spiceManager;

    //---------------------------------------------------------------------
    // Constructors
    //---------------------------------------------------------------------

    @Inject
    public JsXmlSpiceServiceWrapper() {
        spiceManager = new SpiceManager(JsXmlSpiceService.class);
    }

    //---------------------------------------------------------------------
    // Public methods
    //---------------------------------------------------------------------

    public void onStart(Context context) {
        spiceManager.start(context);
    }

    public void onStop() {
        if (spiceManager.isStarted()) spiceManager.shouldStop();
    }

    public SpiceManager getSpiceManager() {
        return spiceManager;
    }
}