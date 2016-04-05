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

package com.jaspersoft.android.jaspermobile.data.entity.mapper;

import com.jaspersoft.android.jaspermobile.domain.entity.LibrarySort;
import com.jaspersoft.android.jaspermobile.domain.entity.Sort;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.sdk.service.repository.SortType;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerScreen
public class ResourcesSortMapper {

    @Inject
    public ResourcesSortMapper() {
    }

    public Collection<Sort> from(Collection<SortType> sortTypeCollection) {
        Collection<Sort> resultCollection = new ArrayList<>();
        for (SortType sortType : sortTypeCollection) {
            resultCollection.add(from(sortType));
        }
        return resultCollection;
    }

    public Sort from(SortType sortType) {
        switch (sortType) {
            case LABEL:
                return new LibrarySort(LibrarySort.SORT_BY_LABEL);
            case DESCRIPTION:
                return new LibrarySort(LibrarySort.SORT_BY_DESCRIPTION);
            case ACCESS_TIME:
                return new LibrarySort(LibrarySort.SORT_BY_ACCESS_TIME);
            case CREATION_DATE:
                return new LibrarySort(LibrarySort.SORT_BY_CREATION_DATE);
            default:
                return new LibrarySort(LibrarySort.SORT_BY_LABEL);
        }
    }

    public SortType to(Sort sort) {
        String sortType = sort.getSortType();
        if (sortType.equals(LibrarySort.SORT_BY_LABEL)) return SortType.LABEL;
        if (sortType.equals(LibrarySort.SORT_BY_DESCRIPTION)) return SortType.DESCRIPTION;
        if (sortType.equals(LibrarySort.SORT_BY_ACCESS_TIME)) return SortType.ACCESS_TIME;
        if (sortType.equals(LibrarySort.SORT_BY_CREATION_DATE)) return SortType.CREATION_DATE;
        return SortType.LABEL;
    }
}
