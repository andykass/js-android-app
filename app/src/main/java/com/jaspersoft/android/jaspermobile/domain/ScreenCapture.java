/*
 * Copyright ? 2015 TIBCO Software, Inc. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.jaspersoft.android.jaspermobile.internal.di.PerProfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import dagger.Provides;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class ScreenCapture {
    private final Bitmap mSource;

    private ScreenCapture(Bitmap source) {
        mSource = source;
    }

    public boolean saveInto(FileOutputStream fas) {
        return mSource.compress(Bitmap.CompressFormat.PNG, 100, fas);
    }

    public static class Factory {
        public static ScreenCapture capture(View view) {
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap resource = Bitmap.createBitmap(view.getDrawingCache());
            view.destroyDrawingCache();
            return new ScreenCapture(resource);
        }
    }
}
