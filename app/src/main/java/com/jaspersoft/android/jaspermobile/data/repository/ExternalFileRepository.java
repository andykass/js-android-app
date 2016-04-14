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

package com.jaspersoft.android.jaspermobile.data.repository;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.domain.repository.FilesRepository;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
@Singleton
public class ExternalFileRepository implements FilesRepository {

    private final Context mContext;

    @Inject
    public ExternalFileRepository(@ApplicationContext Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public File cacheFile(@NonNull final String fileName, @NonNull final String folderName) throws IOException {
        File cacheDir = mContext.getCacheDir();
        if (cacheDir == null) {
            throw new FileNotFoundException("Can not get cache folder path");
        }

        File cacheFileDir = new File(cacheDir.getPath(), folderName);
        if (!cacheFileDir.exists() && !cacheFileDir.mkdir()) {
            throw new FileNotFoundException("Can not create shared cache folder");
        }

        return new File(cacheFileDir, fileName);
    }
}
