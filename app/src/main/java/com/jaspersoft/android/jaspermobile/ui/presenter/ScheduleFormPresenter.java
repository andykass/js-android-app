package com.jaspersoft.android.jaspermobile.ui.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.component.presenter.BasePresenter;
import com.jaspersoft.android.jaspermobile.ui.component.presenter.PresenterBundle;
import com.jaspersoft.android.jaspermobile.ui.contract.ScheduleFormContract;
import com.jaspersoft.android.jaspermobile.ui.view.entity.JobFormViewEntity;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@PerActivity
public class ScheduleFormPresenter extends BasePresenter<ScheduleFormContract.View>
        implements ScheduleFormContract.EventListener, ScheduleFormContract.Model.Callback {
    private static final String FORM_KEY = "form";

    private final ScheduleFormContract.Model mModel;
    private RequestExceptionHandler mExceptionHandler;
    private JobFormViewEntity mViewForm;

    @Inject
    public ScheduleFormPresenter(ScheduleFormContract.Model model, RequestExceptionHandler exceptionHandler) {
        mModel = model;
        mExceptionHandler = exceptionHandler;
    }

    @Override
    public void onCreate(@Nullable PresenterBundle bundle) {
        super.onCreate(bundle);

        if (bundle != null && bundle.containsKey(FORM_KEY)) {
            mViewForm = (JobFormViewEntity) bundle.getSerializable(FORM_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull PresenterBundle bundle) {
        bundle.putSerializable(FORM_KEY, mView.takeForm());
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onBindView(ScheduleFormContract.View view) {
        mModel.bind(this);

        if (mViewForm == null) {
            mView.showFormLoadingMessage();
            mModel.load();
        } else {
            mView.showForm(mViewForm);
        }
    }

    @Override
    public void onViewReady() {
    }

    @Override
    public void onSubmitClick(JobFormViewEntity form) {
        mView.showSubmitMessage();
        mModel.submit(form);
    }

    @Override
    public void onFormLoadSuccess(JobFormViewEntity form) {
        boolean formIsMissing = mViewForm == null;
        if (formIsMissing) {
            mView.hideFormLoadingMessage();
            mView.showForm(form);
        }
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

    @Override
    public void onFormSubmitError(Throwable error) {
        mView.hideSubmitMessage();
        handleError(error);
    }

    private void handleError(Throwable error) {
        mExceptionHandler.showAuthErrorIfExists(error);
    }
}
