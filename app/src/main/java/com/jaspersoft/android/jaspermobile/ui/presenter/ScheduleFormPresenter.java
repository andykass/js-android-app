package com.jaspersoft.android.jaspermobile.ui.presenter;

import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.component.presenter.BasePresenter;
import com.jaspersoft.android.jaspermobile.ui.contract.ScheduleFormContract;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewBundle;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@PerActivity
public class ScheduleFormPresenter extends BasePresenter<ScheduleFormContract.View>
        implements ScheduleFormContract.EventListener, ScheduleFormContract.Model.Callback {
    private final ScheduleFormContract.Model mModel;
    private final RequestExceptionHandler mExceptionHandler;

    @Inject
    public ScheduleFormPresenter(ScheduleFormContract.Model model, RequestExceptionHandler exceptionHandler) {
        mModel = model;
        mExceptionHandler = exceptionHandler;
    }

    @Override
    public void onBindView(ScheduleFormContract.View view) {
        mModel.bind(this);
    }

    @Override
    public void onViewReady() {
        mView.showFormLoadingMessage();
        mModel.load();
    }

    @Override
    public void onSubmitClick(JobFormViewBundle form) {
        mView.showSubmitMessage();
        mModel.submit(form);
    }

    @Override
    public void onFormLoadSuccess(JobFormViewBundle form) {
        mView.hideFormLoadingMessage();
        mView.showForm(form);
    }

    @Override
    public void onFormLoadError(Throwable error) {
        mView.hideFormLoadingMessage();
        handleError(error);
    }

    @Override
    public void onFormSubmitSuccess() {
        mView.showSubmitSuccess();
        mView.hideFormLoadingMessage();
    }

    public void onFormSubmitError(Throwable error) {
        mView.hideSubmitMessage();
        handleError(error);
    }

    private void handleError(Throwable error) {
        mExceptionHandler.showAuthErrorIfExists(error);
        Timber.e(error, "ScheduleFormPresenter messaged!");
    }
}
