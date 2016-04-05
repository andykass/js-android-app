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

package com.jaspersoft.android.jaspermobile.data.fetchers;

import android.graphics.Bitmap;
import android.net.Uri;

import com.jaspersoft.android.jaspermobile.data.ThumbNailGenerator;
import com.jaspersoft.android.jaspermobile.data.utils.ResourceThumbnailPreProcessor;
import com.jaspersoft.android.jaspermobile.domain.entity.ResourceIcon;
import com.jaspersoft.android.jaspermobile.domain.fetchers.ThumbnailFetcher;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.sdk.service.data.repository.Resource;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerScreen
public class ThumbnailFetcherImpl implements ThumbnailFetcher {

    private final ThumbNailGenerator mThumbNailGenerator;

    @Inject
    public ThumbnailFetcherImpl(ThumbNailGenerator thumbNailGenerator) {
        mThumbNailGenerator = thumbNailGenerator;
    }

    @Override
    public ResourceIcon fetchIcon(String resourceUri) {
        String thumbnailUri = mThumbNailGenerator.generate(resourceUri);
        Bitmap thumbnail = ImageLoader.getInstance().loadImageSync(thumbnailUri, getDisplayImageOptions());
        if (thumbnail == null) return null;
        return new ResourceIcon(thumbnail);
    }

    @Override
    public void invalidate() {
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
    }

    private DisplayImageOptions getDisplayImageOptions() {
        return new DisplayImageOptions.Builder()
                .considerExifParams(true)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .preProcessor(new ResourceThumbnailPreProcessor())
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }
}
