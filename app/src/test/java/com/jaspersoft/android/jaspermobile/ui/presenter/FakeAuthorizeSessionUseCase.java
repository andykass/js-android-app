package com.jaspersoft.android.jaspermobile.ui.presenter;

import com.jaspersoft.android.jaspermobile.FakePostExecutionThread;
import com.jaspersoft.android.jaspermobile.FakePreExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.interactor.profile.AuthorizeSessionUseCase;

import rx.Observable;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class FakeAuthorizeSessionUseCase extends AuthorizeSessionUseCase {
    public FakeAuthorizeSessionUseCase() {
        super(FakePreExecutionThread.create(), FakePostExecutionThread.create(), null, null, null, null);
    }

    @Override
    protected Observable<Void> buildUseCaseObservable() {
        return Observable.just(null);
    }
}
