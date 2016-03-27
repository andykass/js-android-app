/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.data.repository.library;

import com.jaspersoft.android.jaspermobile.domain.repository.library.LibrarySortRepository;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.sdk.service.repository.SortType;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerActivity
public class InMemoryLibrarySortRepository implements LibrarySortRepository {
    private SortType mSortType;
    private Collection<SortType> mAvailableSortTypes;

    private final PublishSubject<Void> mPublisher = PublishSubject.create();

    @Inject
    public InMemoryLibrarySortRepository() {
        mAvailableSortTypes = new ArrayList<>();
        mAvailableSortTypes.add(SortType.LABEL);
        mAvailableSortTypes.add(SortType.CREATION_DATE);
    }

    @Override
    public SortType getSortType() {
        return mSortType;
    }

    @Override
    public Observable<Void> observe() {
        return mPublisher;
    }

    @Override
    public Collection<SortType> getAvailableSortType() {
        return mAvailableSortTypes;
    }

    @Override
    public void saveSortType(SortType sortType) {
        mSortType = sortType;
        mPublisher.onNext(null);
    }
}
