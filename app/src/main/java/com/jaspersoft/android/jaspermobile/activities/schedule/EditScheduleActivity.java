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

package com.jaspersoft.android.jaspermobile.activities.schedule;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.data.JasperRestClient;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.util.rx.RxTransformers;
import com.jaspersoft.android.jaspermobile.util.schedule.JobConverter;
import com.jaspersoft.android.jaspermobile.util.schedule.ScheduleViewModel;
import com.jaspersoft.android.sdk.service.data.schedule.JobData;
import com.jaspersoft.android.sdk.service.data.schedule.JobForm;
import com.jaspersoft.android.sdk.service.rx.report.schedule.RxReportScheduleService;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@OptionsMenu(R.menu.report_edit_schedule)
@EActivity
public class EditScheduleActivity extends ToolbarActivity {

    @Extra
    protected int jobId;

    @Inject
    protected Analytics analytics;
    @Inject
    protected JasperRestClient mRestClient;

    @OptionsMenuItem(R.id.editSchedule)
    protected MenuItem editAction;

    private CompositeSubscription mCompositeSubscription;
    private ScheduleFragment mScheduleFragment;
    private JobForm currentJobForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseActivityComponent().inject(this);

        mCompositeSubscription = new CompositeSubscription();

        if (savedInstanceState != null) {
            mScheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentByTag(ScheduleFragment.TAG);
        }

        requestJobInfo();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean currentJobEmpty = currentJobForm == null;
        editAction.setVisible(!currentJobEmpty);

        setActionBarTitle(currentJobEmpty ? getString(R.string.sch_edit) : currentJobForm.getLabel());

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mCompositeSubscription.unsubscribe();
    }

    @Override
    protected String getScreenName() {
        return getString(R.string.ja_edit_sch);
    }

    @OptionsItem(R.id.editSchedule)
    protected void editSchedule() {
        ProgressDialogFragment.builder(getSupportFragmentManager())
                .setLoadingMessage(R.string.loading_msg)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mCompositeSubscription.unsubscribe();
                        mCompositeSubscription = new CompositeSubscription();
                    }
                })
                .show();
        subscribe(
                mRestClient.scheduleService()
                        .flatMap(new Func1<RxReportScheduleService, Observable<JobData>>() {
                            @Override
                            public Observable<JobData> call(RxReportScheduleService rxReportScheduleService) {
                                ScheduleViewModel scheduleViewModel = mScheduleFragment.provideJob();
                                return rxReportScheduleService.updateJob(jobId, JobConverter.toJobForm(currentJobForm, scheduleViewModel));
                            }
                        })
                        .compose(RxTransformers.<JobData>applySchedulers())
                        .subscribe(new Subscriber<JobData>() {
                            @Override
                            public void onCompleted() {
                                hideLoading();
                                analytics.sendEvent(Analytics.EventCategory.JOB.getValue(), Analytics.EventAction.CHANGED.getValue(), null);
                            }

                            @Override
                            public void onError(Throwable e) {
                                hideLoading();
                                RequestExceptionHandler.showAuthErrorIfExists(EditScheduleActivity.this, e);
                            }

                            @Override
                            public void onNext(JobData data) {
                                Toast.makeText(EditScheduleActivity.this, R.string.sch_updated, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
        );
    }

    private void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    private void hideLoading() {
        ProgressDialogFragment.dismiss(getSupportFragmentManager());
    }

    private void subscribe(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    private void requestJobInfo() {
        ProgressDialogFragment.builder(getSupportFragmentManager())
                .setLoadingMessage(R.string.loading_msg)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mCompositeSubscription.unsubscribe();
                        mCompositeSubscription = new CompositeSubscription();
                        finish();
                    }
                })
                .show();

        subscribe(
                mRestClient.scheduleService()
                        .flatMap(new Func1<RxReportScheduleService, Observable<JobForm>>() {
                            @Override
                            public Observable<JobForm> call(RxReportScheduleService rxReportScheduleService) {
                                return rxReportScheduleService.readJob(jobId);
                            }
                        })
                        .compose(RxTransformers.<JobForm>applySchedulers())
                        .subscribe(new Subscriber<JobForm>() {
                            @Override
                            public void onCompleted() {
                                hideLoading();
                                analytics.sendEvent(Analytics.EventCategory.JOB.getValue(), Analytics.EventAction.VIEWED.getValue(), null);
                                invalidateOptionsMenu();
                            }

                            @Override
                            public void onError(Throwable e) {
                                hideLoading();
                                finish();
                                RequestExceptionHandler.showAuthErrorIfExists(EditScheduleActivity.this, e);
                            }

                            @Override
                            public void onNext(JobForm data) {
                                currentJobForm = data;

                                if (mScheduleFragment != null) return;

                                mScheduleFragment = ScheduleFragment_
                                        .builder()
                                        .scheduleViewModel(JobConverter.toJobViewModel(data))
                                        .build();

                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction
                                        .replace(R.id.content, mScheduleFragment, ScheduleFragment.TAG)
                                        .commit();
                            }
                        })
        );
    }
}
