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

package com.jaspersoft.android.jaspermobile.data.repository.datasource;

import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.Profile;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public interface ServerDataSource {
    JasperServer getServer(Profile profile);
    void saveServer(Profile profile, JasperServer server);

    @Singleton
    class Factory {

        @Inject
        public Factory() {
        }

        public ServerDataSource createCloudDataSource() {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        public ServerDataSource createDataSource() {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        public ServerDataSource createDiskDataSource() {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}
