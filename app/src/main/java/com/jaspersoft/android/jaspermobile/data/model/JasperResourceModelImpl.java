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

import com.jaspersoft.android.jaspermobile.data.utils.UniqueCompositeSubscription;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.entity.JasperResource;
import com.jaspersoft.android.jaspermobile.domain.entity.ResourceIcon;
import com.jaspersoft.android.jaspermobile.domain.fetchers.ThumbnailFetcher;
import com.jaspersoft.android.jaspermobile.domain.model.JasperResourceModel;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.jaspermobile.util.rx.RxTransformer;

import java.io.InvalidObjectException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func0;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerScreen
public class JasperResourceModelImpl implements JasperResourceModel {

    private final ThumbnailFetcher mThumbnailFetcher;
    private UniqueCompositeSubscription mThumbnailSubscriptions;
    private Subscriber<Integer> mSubscriber;
    private Map<Integer, ResourceIcon> mThumbnails;

    @Inject
    public JasperResourceModelImpl(ThumbnailFetcher thumbnailFetcher) {
        this.mThumbnailFetcher = thumbnailFetcher;
        this.mThumbnailSubscriptions = new UniqueCompositeSubscription();
        this.mThumbnails = new HashMap<>();
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
                        mSubscriber.onNext(id);
                    }
                });
        mThumbnailSubscriptions.add(id, thumbnailSubscription);
    }

    @Override
    public void invalidateThumbnails() {
        mThumbnailFetcher.invalidate();
        mThumbnails = new HashMap<>();
    }

    @Override
    public void clear() {
        mThumbnailSubscriptions.unsubscribe();
    }

    @Override
    public void subscribe(Subscriber<Integer> subscriber) {
        mSubscriber = subscriber;
    }
}
