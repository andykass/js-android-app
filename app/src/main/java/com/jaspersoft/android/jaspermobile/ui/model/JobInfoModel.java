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

package com.jaspersoft.android.jaspermobile.ui.model;

import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.entity.JobResource;
import com.jaspersoft.android.jaspermobile.domain.repository.schedule.ScheduleRepository;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.jaspermobile.ui.contract.JobInfoContract;
import com.jaspersoft.android.jaspermobile.util.rx.RxTransformer;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerScreen
public class JobInfoModel extends SimpleModel<JobInfoContract.ResultCallback> implements JobInfoContract.Model {

    private final JobResource mJobResource;
    private final ScheduleRepository mScheduleRepository;
    private final CompositeSubscription mCompositeSubscription;

    @Inject
    public JobInfoModel(JobResource job, ScheduleRepository scheduleRepository) {
        mJobResource = job;
        mScheduleRepository = scheduleRepository;
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void clear() {
        mCompositeSubscription.unsubscribe();
    }

    @Override
    public JobResource getJobDetails() {
        return mJobResource;
    }

    @Override
    public void requestJobDeletion() {
        Subscription deleteSubscription = Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    mScheduleRepository.deleteJob(mJobResource.getId());
                    subscriber.onNext(null);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .compose(RxTransformer.<Void>applySchedulers())
                .subscribe(new SimpleSubscriber<Void>() {
                    @Override
                    public void onError(Throwable e) {
                        getCallback().onError(e);
                    }

                    @Override
                    public void onNext(Void item) {
                        getCallback().onDeletionSuccess();
                    }
                });
        mCompositeSubscription.add(deleteSubscription);
    }
}
