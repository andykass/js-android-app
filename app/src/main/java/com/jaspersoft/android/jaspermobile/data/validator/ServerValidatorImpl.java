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

package com.jaspersoft.android.jaspermobile.data.validator;

import com.jaspersoft.android.jaspermobile.data.entity.mapper.JasperServerMapper;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.validator.ServerValidator;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.sdk.service.data.server.ServerInfo;
import com.jaspersoft.android.sdk.service.rx.info.RxServerInfoService;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Implementation of server validation
 *
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
public final class ServerValidatorImpl implements ServerValidator {
    private final JasperServerMapper mServerMapper;
    private final RxServerInfoService mRxServerInfoService;

    @Inject
    public ServerValidatorImpl(JasperServerMapper serverMapper, RxServerInfoService rxServerInfoService) {
        mServerMapper = serverMapper;
        mRxServerInfoService = rxServerInfoService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<JasperServer> validate(final String serverUrl) {
        return mRxServerInfoService.requestServerInfo()
                .map(new Func1<ServerInfo, JasperServer>() {
                    @Override
                    public JasperServer call(ServerInfo serverInfo) {
                        return mServerMapper.toDomainModel(serverUrl, serverInfo);
                    }
                });
    }
}
