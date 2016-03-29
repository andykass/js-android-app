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

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.data.JasperRestClient;
import com.jaspersoft.android.jaspermobile.dialog.DeleteDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;
import com.jaspersoft.android.jaspermobile.util.ViewType;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceAdapter;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceConverter;
import com.jaspersoft.android.jaspermobile.util.rx.RxTransformer;
import com.jaspersoft.android.jaspermobile.util.rx.RxTransformers;
import com.jaspersoft.android.jaspermobile.widget.JasperRecyclerView;
import com.jaspersoft.android.sdk.service.data.schedule.JobUnit;
import com.jaspersoft.android.sdk.service.report.schedule.JobSearchCriteria;
import com.jaspersoft.android.sdk.service.report.schedule.JobSortType;
import com.jaspersoft.android.sdk.service.rx.report.schedule.RxReportScheduleService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

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
@EFragment(R.layout.fragment_jobs)
public class JobsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, DeleteDialogFragment.DeleteDialogClickListener {

    @ViewById(android.R.id.list)
    protected JasperRecyclerView listView;
    @ViewById(R.id.refreshLayout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @ViewById(android.R.id.empty)
    protected TextView message;

    @Inject
    protected Analytics analytics;
    @Inject
    protected JasperRestClient mRestClient;
    @Inject
    protected JasperResourceConverter jasperResourceConverter;

    private JasperResourceAdapter mAdapter;
    private CompositeSubscription mCompositeSubscription;
    private LinkedHashMap<Integer, JobUnit> mJobs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseActivityComponent().inject(this);

        mCompositeSubscription = new CompositeSubscription();

        if (savedInstanceState == null) {
            analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.VIEWED.getValue(), Analytics.EventLabel.JOBS.getValue());
        }

        ((ToolbarActivity) getActivity()).setCustomToolbarView(null);
    }

    @AfterViews
    protected void init(){
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.js_blue,
                R.color.js_dark_blue,
                R.color.js_blue,
                R.color.js_dark_blue);

        setDataAdapter();
    }

    @Override
    public void onRefresh() {
        loadJobs();
        analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.REFRESHED.getValue(), Analytics.EventLabel.JOBS.getValue());
    }

    @Override
    public void onResume() {
        super.onResume();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.sch_jobs));
        }

        loadJobs();

        List<Analytics.Dimension> viewDimension = new ArrayList<>();
        analytics.sendScreenView(Analytics.ScreenName.JOBS.getValue(), viewDimension);
    }

    @Override
    public void onPause() {
        swipeRefreshLayout.clearAnimation();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        listView.setVisibility(View.GONE);
        mCompositeSubscription.unsubscribe();
    }

    @OnActivityResult(ChooseReportActivity.CHOOSE_REPORT_REQUEST_CODE)
    protected void onReportSelected(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        JasperResource resource = (JasperResource) data.getSerializableExtra(ChooseReportActivity.RESULT_JASPER_RESOURCE);
        NewScheduleActivity_
                .intent(getActivity())
                .jasperResource(resource)
                .start();
    }

    @Click(R.id.newJob)
    protected void newScheduleAction() {
        startActivityForResult(new Intent(getActivity(), ChooseReportActivity.class), ChooseReportActivity.CHOOSE_REPORT_REQUEST_CODE);
    }

    @Override
    public void onDeleteConfirmed(final JasperResource resource) {
        try {
            final int jobId = Integer.parseInt(resource.getId());
            final Set<Integer> idToDel = new HashSet<>();
            idToDel.add(jobId);

            ProgressDialogFragment.builder(getFragmentManager())
                    .setLoadingMessage(R.string.loading_msg)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mCompositeSubscription.unsubscribe();
                            mCompositeSubscription = new CompositeSubscription();
                        }
                    })
                    .show();

            mCompositeSubscription.add(mRestClient.scheduleService()
                    .flatMap(new Func1<RxReportScheduleService, Observable<Set<Integer>>>() {
                        @Override
                        public Observable<Set<Integer>> call(RxReportScheduleService scheduleService) {
                            return scheduleService.deleteJobs(idToDel);
                        }
                    })
                    .compose(RxTransformers.<Set<Integer>>applySchedulers())
                    .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<Set<Integer>>() {
                        @Override
                        public void onCompleted() {
                            Toast.makeText(getActivity(), R.string.sch_deleted, Toast.LENGTH_SHORT).show();

                            mJobs.remove(jobId);
                            mAdapter.remove(resource);

                            if (mJobs.isEmpty()) {
                                showMessage(getString(R.string.sch_not_found));
                            }
                            analytics.sendEvent(
                                    Analytics.EventCategory.RESOURCE.getValue(),
                                    Analytics.EventAction.REMOVED.getValue(),
                                    Analytics.EventLabel.JOB.getValue()
                            );
                        }
                    })));
        } catch (NumberFormatException ex) {
            Toast.makeText(getActivity(), R.string.wrong_action, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadJobs() {
        Subscription subscription = mRestClient.scheduleService()
                .flatMap(new Func1<RxReportScheduleService, Observable<List<JobUnit>>>() {
                    @Override
                    public Observable<List<JobUnit>> call(RxReportScheduleService service) {
                        JobSearchCriteria jobSearchCriteria = JobSearchCriteria.builder()
                                .withSortType(JobSortType.SORTBY_JOBNAME)
                                .build();
                        return service.search(jobSearchCriteria).nextLookup();
                    }
                })
                .compose(RxTransformer.<List<JobUnit>>applySchedulers())
                .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<List<JobUnit>>() {
                    @Override
                    public void onStart() {
                        mJobs = new LinkedHashMap<>();
                        showMessage(getString(R.string.loading_msg));
                    }

                    @Override
                    public void onCompleted() {
                        showMessage(mJobs.isEmpty() ? getString(R.string.sch_not_found) : null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showMessage(getString(R.string.failed_load_data));
                    }

                    @Override
                    public void onNext(List<JobUnit> jobUnits) {
                        mAdapter.addAll(jasperResourceConverter.convertToJasperResources(getActivity(), jobUnits));

                        for (JobUnit job : jobUnits) {
                            mJobs.put(job.getId(), job);
                        }
                    }
                }));
        mCompositeSubscription.add(subscription);
    }

    private void setDataAdapter() {
        mAdapter = new JasperResourceAdapter(getActivity());
        mAdapter.setOnItemInteractionListener(new JasperResourceAdapter.OnResourceInteractionListener() {
            @Override
            public void onResourceItemClicked(JasperResource jasperResource) {
                try {
                    int jobId = Integer.parseInt(jasperResource.getId());

                    EditScheduleActivity_.intent(getContext())
                            .jobId(jobId)
                            .start();
                } catch (NumberFormatException ex) {
                    Toast.makeText(getActivity(), R.string.wrong_action, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSecondaryActionClicked(JasperResource jasperResource) {
                String deleteMessage = getActivity().getString(R.string.sdr_delete_message);

                DeleteDialogFragment.createBuilder(getActivity(), getFragmentManager())
                        .setResource(jasperResource)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.sdr_delete_title)
                        .setMessage(deleteMessage)
                        .setPositiveButtonText(R.string.spm_delete_btn)
                        .setNegativeButtonText(R.string.cancel)
                        .setTargetFragment(JobsFragment.this)
                        .show();
            }
        });

        listView.setAdapter(mAdapter);
        listView.changeViewType(ViewType.LIST);
    }

    private void showMessage(String textMessage) {
        message.setText(textMessage);
        if (textMessage != null) {
            mAdapter.clear();
        }
    }

    private void setRefreshState(boolean refreshing) {
        if (!refreshing) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void hideLoading() {
        ProgressDialogFragment.dismiss(getFragmentManager());
    }

    private class ErrorSubscriber<R> extends Subscriber<R> {
        private final Subscriber<R> mDelegate;

        private ErrorSubscriber(Subscriber<R> delegate) {
            mDelegate = delegate;
        }

        @Override
        public void onStart() {
            mDelegate.onStart();
        }

        @Override
        public void onCompleted() {
            setRefreshState(false);
            hideLoading();
            mDelegate.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            RequestExceptionHandler.showAuthErrorIfExists(getActivity(), e);
            hideLoading();
            setRefreshState(false);
            mDelegate.onError(e);
        }

        @Override
        public void onNext(R r) {
            mDelegate.onNext(r);
        }
    }
}
