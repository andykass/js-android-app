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

package com.jaspersoft.android.jaspermobile;

import android.app.Application;
import android.content.Context;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;
import com.jaspersoft.android.jaspermobile.activities.SecurityProviderUpdater;
import com.jaspersoft.android.jaspermobile.data.JasperRestClient;
import com.jaspersoft.android.jaspermobile.data.entity.mapper.ReportParamsMapper;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.domain.interactor.dashboard.GetDashboardControlsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.dashboard.GetDashboardVisualizeParamsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.FlushInputControlsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetInputControlsValuesCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetReportMetadataCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetReportShowControlsPropertyCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetVisualizeExecOptionsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.ValidateInputControlsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.option.DeleteReportOptionCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.option.GetReportOptionValuesCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.option.GetReportOptionsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.option.LoadControlsForOptionCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.option.SaveReportOptionsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.GetResourceDetailsByTypeCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.GetRootFoldersCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.LoadResourceInFileCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.SearchResourcesCase;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.util.ReportParamsStorage;
import com.jaspersoft.android.jaspermobile.util.account.AccountServerData;
import com.jaspersoft.android.jaspermobile.util.print.ResourcePrintJob;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceConverter;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 1.0
 */
public class  JasperMobileModule extends AbstractModule {
    private final Context mContext;

    public JasperMobileModule(Application application) {
        super();
        mContext = application;
        GraphObject.Factory.from(mContext)
                .getComponent()
                .inject(this);
    }

    @Override
    protected void configure() {
        int animationSpeed = mContext.getResources().getInteger(
                android.R.integer.config_longAnimTime);
        animationSpeed *= 1.5;
        bindConstant().annotatedWith(Names.named("animationSpeed"))
                .to(animationSpeed);
        bindConstant().annotatedWith(Names.named("LIMIT")).to(100);
        bindConstant().annotatedWith(Names.named("MAX_PAGE_ALLOWED")).to(10);
        bindConstant().annotatedWith(Names.named("THRESHOLD")).to(5);

        String endpoint = AccountServerData.Demo.SERVER_URL;
        bindConstant().annotatedWith(Names.named("DEMO_ENDPOINT")).to(endpoint);

        bind(Context.class).annotatedWith(ApplicationContext.class).toInstance(mContext);
        bind(AppConfigurator.class).to(AppConfiguratorImpl.class);
        bind(Analytics.class).toInstance(new JasperAnalytics(mContext));
        bind(SecurityProviderUpdater.class).to(JasperSecurityProviderUpdater.class).in(Singleton.class);

        // TODO clean up mess after roboguice will be removed out
        bind(GetInputControlsValuesCase.class).toProvider(Providers.<GetInputControlsValuesCase>of(null));
        bind(ValidateInputControlsCase.class).toProvider(Providers.<ValidateInputControlsCase>of(null));
        bind(GetReportOptionsCase.class).toProvider(Providers.<GetReportOptionsCase>of(null));
        bind(SaveReportOptionsCase.class).toProvider(Providers.<SaveReportOptionsCase>of(null));
        bind(GetReportOptionValuesCase.class).toProvider(Providers.<GetReportOptionValuesCase>of(null));
        bind(DeleteReportOptionCase.class).toProvider(Providers.<DeleteReportOptionCase>of(null));
        bind(JasperServer.class).toProvider(Providers.<JasperServer>of(null));
        bind(GetDashboardControlsCase.class).toProvider(Providers.<GetDashboardControlsCase>of(null));
        bind(GetVisualizeExecOptionsCase.class).toProvider(Providers.<GetVisualizeExecOptionsCase>of(null));
        bind(RequestExceptionHandler.class).toProvider(Providers.<RequestExceptionHandler>of(null));
        bind(FlushInputControlsCase.class).toProvider(Providers.<FlushInputControlsCase>of(null));
        bind(ResourcePrintJob.class).toProvider(Providers.<ResourcePrintJob>of(null));
        bind(SearchResourcesCase.class).toProvider(Providers.<SearchResourcesCase>of(null));
        bind(GetRootFoldersCase.class).toProvider(Providers.<GetRootFoldersCase>of(null));
        bind(GetResourceDetailsByTypeCase.class).toProvider(Providers.<GetResourceDetailsByTypeCase>of(null));
        bind(LoadControlsForOptionCase.class).toProvider(Providers.<LoadControlsForOptionCase>of(null));
        bind(ReportParamsStorage.class).toProvider(Providers.<ReportParamsStorage>of(null));
        bind(JasperRestClient.class).toProvider(Providers.<JasperRestClient>of(null));
        bind(LoadResourceInFileCase.class).toProvider(Providers.<LoadResourceInFileCase>of(null));
        bind(Profile.class).toProvider(Providers.<Profile>of(null));
        bind(JasperServer.class).toProvider(Providers.<JasperServer>of(null));
        bind(JasperResourceConverter.class).toProvider(Providers.<JasperResourceConverter>of(null));
        bind(GetDashboardVisualizeParamsCase.class).toProvider(Providers.<GetDashboardVisualizeParamsCase>of(null));
        bind(GetReportShowControlsPropertyCase.class).toProvider(Providers.<GetReportShowControlsPropertyCase>of(null));
        bind(ReportParamsMapper.class).toProvider(Providers.<ReportParamsMapper>of(null));
        bind(GetReportMetadataCase.class).toProvider(Providers.<GetReportMetadataCase>of(null));
    }
}
