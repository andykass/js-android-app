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

package com.jaspersoft.android.jaspermobile.data.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;

import com.nostra13.universalimageloader.core.process.BitmapProcessor;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class ResourceThumbnailPreProcessor implements BitmapProcessor {

    @Override
    public Bitmap process(Bitmap bitmap) {
        if (atLeast12x12(bitmap)) {
            return removeArtifacts(bitmap);
        }
        return null;
    }

    private Bitmap removeArtifacts(Bitmap origin) {
        int originalWidth = origin.getWidth();
        int originalHeight = origin.getHeight();

        int ratioHeight = calculateRatioHeight(originalWidth, originalHeight);
        int newHeight = ratioHeight - 6;
        int newWidth = originalWidth - 6;

        if (newWidth > 0 && newHeight > 0) {
            return Bitmap.createBitmap(origin, 3, 3, newWidth, newHeight);
        }
        return origin;
    }

    private int calculateRatioHeight(int originalWidth, int originalHeight) {
        int scaledWidth = (int) (originalWidth * 0.66);
        return scaledWidth < originalHeight ? scaledWidth : originalHeight;
    }

    private boolean atLeast12x12(Bitmap bitmap) {
        return bitmap.getWidth() >= 12 && bitmap.getHeight() >= 12;
    }
}
