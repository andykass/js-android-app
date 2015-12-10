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

package com.jaspersoft.android.jaspermobile.data.repository;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.domain.repository.ReportRepository;
import com.jaspersoft.android.jaspermobile.domain.service.ReportExecutionService;
import com.jaspersoft.android.jaspermobile.domain.service.ReportService;
import com.jaspersoft.android.jaspermobile.presentation.mapper.ReportParamsTransformer;
import com.jaspersoft.android.jaspermobile.util.ReportParamsStorage;
import com.jaspersoft.android.sdk.client.oxm.control.InputControl;
import com.jaspersoft.android.sdk.client.oxm.report.ReportParameter;
import com.octo.android.robospice.persistence.memory.LruCache;

import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public final class InMemoryReportRepository implements ReportRepository {

    private final String mReportUri;
    private final ReportParamsStorage mReportParamsStorage;
    private final ReportParamsTransformer mReportParamsTransformer;

    private final ReportService mReportService;

    private final LruCache<String, String> mPagesCache = new LruCache<>(10);
    private ReportExecutionService mExecutionCache;

    public InMemoryReportRepository(
            String reportUri,
            ReportService reportService,
            ReportParamsStorage reportParamsStorage, ReportParamsTransformer reportParamsTransformer) {
        mReportService = reportService;
        mReportParamsStorage = reportParamsStorage;
        mReportUri = reportUri;
        mReportParamsTransformer = reportParamsTransformer;
    }

    @Override
    public Observable<String> getPage(String range) {
        Map<String, Set<String>> repoParams = getParameters();
        Observable<String> memory = createPagesMemorySource(range);
        Observable<String> network = createPagesNetworkSource(range, repoParams);
        return rx.Observable.concat(memory, network).first();
    }

    @NonNull
    private Map<String, Set<String>> getParameters() {
        List<ReportParameter> params = mReportParamsStorage.getInputControlHolder(mReportUri).getReportParams();
        return mReportParamsTransformer.transform(params);
    }

    @Override
    public Observable<List<InputControl>> getControls() {
        return mReportService.loadControls(mReportUri).doOnNext(new Action1<List<InputControl>>() {
            @Override
            public void call(List<InputControl> inputControls) {
                mReportParamsStorage.getInputControlHolder(mReportUri).setInputControls(inputControls);
            }
        });
    }

    @Override
    public Observable<Integer> getTotalPages() {
        Map<String, Set<String>> repoParams = getParameters();
        return getExecution(repoParams).flatMap(new Func1<ReportExecutionService, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(ReportExecutionService reportExecutionService) {
                return reportExecutionService.loadTotalPages();
            }
        });
    }

    private Observable<String> createPagesNetworkSource(final String range, final Map<String, Set<String>> params) {
        return getExecution(params)
                .switchMap(new Func1<ReportExecutionService, Observable<String>>() {
                    @Override
                    public Observable<String> call(ReportExecutionService reportExecutionService) {
                        return reportExecutionService.downloadExport(range);
                    }
                }).doOnNext(new Action1<String>() {
                    @Override
                    public void call(String page) {
                        mPagesCache.put(range, page);
                    }
                });
    }

    private Observable<String> createPagesMemorySource(final String pages) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                String page = mPagesCache.get(pages);
                if (page == null) {
                    return Observable.empty();
                }
                return Observable.just(page);
            }
        });
    }

    private Observable<ReportExecutionService> getExecution(Map<String, Set<String>> params) {
        Observable<ReportExecutionService> network = createNetworkExecutionSource(params);
        Observable<ReportExecutionService> memory = createMemoryExecutionSource();
        return Observable.concat(memory, network).first();
    }

    private Observable<ReportExecutionService> createMemoryExecutionSource() {
        if (mExecutionCache == null) {
            return Observable.empty();
        }
        return Observable.just(mExecutionCache);
    }

    private Observable<ReportExecutionService> createNetworkExecutionSource(Map<String, Set<String>> params) {
        return mReportService.runReport(mReportUri, params)
                .doOnNext(new Action1<ReportExecutionService>() {
                    @Override
                    public void call(ReportExecutionService reportExecutionService) {
                        mExecutionCache = reportExecutionService;
                    }
                });
    }
}
