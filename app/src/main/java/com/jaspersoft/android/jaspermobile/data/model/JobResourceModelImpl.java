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

package com.jaspersoft.android.jaspermobile.data.model;

import com.jaspersoft.android.jaspermobile.data.JasperRestClient;
import com.jaspersoft.android.jaspermobile.data.utils.UniqueCompositeSubscription;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.entity.ResourceIcon;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobResource;
import com.jaspersoft.android.jaspermobile.domain.fetchers.ThumbnailFetcher;
import com.jaspersoft.android.jaspermobile.domain.model.JasperResourceModel;
import com.jaspersoft.android.jaspermobile.domain.model.JobResourceModel;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.jaspermobile.util.rx.RxTransformer;
import com.jaspersoft.android.sdk.service.exception.ServiceException;

import java.io.InvalidObjectException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func0;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerScreen
public class JobResourceModelImpl implements JobResourceModel {

    private Subscriber<Integer> mUpdateSubscriber;
    private Subscriber<Integer> mDeleteSubscriber;
    private Map<Integer, ResourceIcon> mThumbnails;
    private final UniqueCompositeSubscription mThumbnailSubscriptions;
    private final ThumbnailFetcher mThumbnailFetcher;
    private final UniqueCompositeSubscription mActionSubscriptions;
    private final JasperRestClient mRestClient;

    @Inject
    public JobResourceModelImpl(ThumbnailFetcher thumbnailFetcher, JasperRestClient mRestClient) {
        this.mThumbnailFetcher = thumbnailFetcher;
        this.mRestClient = mRestClient;
        mActionSubscriptions = new UniqueCompositeSubscription();
        mThumbnailSubscriptions = new UniqueCompositeSubscription();
        mThumbnails = new HashMap<>();
    }

    @Override
    public void clear() {
        mActionSubscriptions.unsubscribe();
    }

    @Override
    public void subscribe(Subscriber<Integer> subscriber) {
        mUpdateSubscriber = subscriber;
    }

    @Override
    public void subscribeOnDeletion(Subscriber<Integer> subscriber) {
        mDeleteSubscriber = subscriber;
    }

    @Override
    public boolean isInAction(int id) {
        return mActionSubscriptions.contains(id);
    }

    @Override
    public void requestToDelete(final int jobId) {
        if (mActionSubscriptions.contains(jobId)) return;

        Subscription deleteSubscription = Observable.defer(new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                final Set<Integer> idToDel = new HashSet<>();
                idToDel.add(jobId);

                try {
                    mRestClient.syncScheduleService().deleteJobs(idToDel);
                } catch (ServiceException e) {
                    return Observable.error(e);
                }
                return Observable.just(jobId);
            }
        })
                .delay(750, TimeUnit.MILLISECONDS)
                .compose(RxTransformer.<Integer>applySchedulers())
                .subscribe(new SimpleSubscriber<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        mDeleteSubscriber.onNext(item);
                        mActionSubscriptions.remove(item);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUpdateSubscriber.onNext(jobId);
                        mUpdateSubscriber.onError(e);
                        mActionSubscriptions.remove(jobId);
                    }
                });

        mActionSubscriptions.add(jobId, deleteSubscription);
        mUpdateSubscriber.onNext(jobId);
    }

    @Override
    public void requestToEnable(JobResource jobResource, boolean enable) {

    }

    @Override
    public ResourceIcon getResourceIcon(int id) {
        return mThumbnails.get(id);
    }

    @Override
    public void requestThumbnail(final int id, final URI resourceUri) {
        if (mThumbnailSubscriptions.contains(id)) return;

        Subscription thumbnailSubscription = Observable.defer(new Func0<Observable<ResourceIcon>>() {
            @Override
            public Observable<ResourceIcon> call() {
                ResourceIcon resourceIcon = mThumbnailFetcher.fetchIcon(resourceUri.toString());
                if (resourceIcon == null)
                    return Observable.error(new InvalidObjectException("There is no thumbnail for this resource"));

                return Observable.just(resourceIcon);
            }
        })
                .compose(RxTransformer.<ResourceIcon>applySchedulers())
                .subscribe(new SimpleSubscriber<ResourceIcon>() {
                    @Override
                    public void onNext(ResourceIcon item) {
                        mThumbnails.put(id, item);
                        mUpdateSubscriber.onNext(id);
                    }
                });
        mThumbnailSubscriptions.add(id, thumbnailSubscription);
    }

    @Override
    public void invalidateThumbnails() {
        mThumbnailFetcher.invalidate();
        mThumbnails = new HashMap<>();
    }
}
