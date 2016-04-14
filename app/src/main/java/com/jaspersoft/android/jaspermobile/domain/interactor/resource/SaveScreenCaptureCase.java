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

package com.jaspersoft.android.jaspermobile.domain.interactor.resource;

import com.jaspersoft.android.jaspermobile.domain.ScreenCapture;
import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.executor.PreExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.interactor.AbstractUseCase;
import com.jaspersoft.android.jaspermobile.domain.repository.FilesRepository;
import com.jaspersoft.android.jaspermobile.internal.di.PerProfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerProfile
public class SaveScreenCaptureCase extends AbstractUseCase<File, ScreenCapture> {
    private static final String SCREEN_CAPTURE_NAME = "screen_capture_file.jpg";
    private static final String SCREEN_CAPTURE_SHARED_DIR = "shared_cache";

    private final FilesRepository mFilesRepository;

    @Inject
    protected SaveScreenCaptureCase(PreExecutionThread preExecutionThread, PostExecutionThread postExecutionThread, FilesRepository filesRepository) {
        super(preExecutionThread, postExecutionThread);
        mFilesRepository = filesRepository;
    }

    @Override
    protected Observable<File> buildUseCaseObservable(final ScreenCapture screenCapture) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                try {
                    File file = mFilesRepository.cacheFile(SCREEN_CAPTURE_NAME, SCREEN_CAPTURE_SHARED_DIR);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    boolean saved = screenCapture.saveInto(fileOutputStream);
                    if (saved) {
                        subscriber.onNext(file);
                    } else {
                        subscriber.onError(new IOException("Can not create file."));
                    }
                } catch (IOException exc) {
                    subscriber.onError(new IOException("Can not cache file."));
                }
            }
        });
    }
}
