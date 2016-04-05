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
import com.jaspersoft.android.jaspermobile.domain.entity.JobResource;
import com.jaspersoft.android.jaspermobile.domain.model.JobResourceModel;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.jaspermobile.util.rx.RxTransformer;
import com.jaspersoft.android.sdk.service.exception.ServiceException;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.observables.ConnectableObservable;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerScreen
public class JobResourceModelImpl implements JobResourceModel {

    private Subscriber<Integer> mUpdateSubscriber;
    private Subscriber<Integer> mDeleteSubscriber;
    private final UniqueCompositeSubscription mThumbnailSubscriptions;
    private final JasperRestClient mRestClient;

    @Inject
    public JobResourceModelImpl(JasperRestClient mRestClient) {
        this.mRestClient = mRestClient;
        this.mThumbnailSubscriptions = new UniqueCompositeSubscription();
    }

    @Override
    public void clear() {
        mThumbnailSubscriptions.unsubscribe();
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
        return mThumbnailSubscriptions.contains(id);
    }

    @Override
    public void requestToDelete(final int jobId) {
        if (mThumbnailSubscriptions.contains(jobId)) return;

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
                        mThumbnailSubscriptions.remove(item);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUpdateSubscriber.onNext(jobId);
                        mUpdateSubscriber.onError(e);
                        mThumbnailSubscriptions.remove(jobId);
                    }
                });


        mThumbnailSubscriptions.add(jobId, deleteSubscription);
        mUpdateSubscriber.onNext(jobId);
    }

    @Override
    public void requestToEnable(JobResource jobResource, boolean enable) {

    }
}
