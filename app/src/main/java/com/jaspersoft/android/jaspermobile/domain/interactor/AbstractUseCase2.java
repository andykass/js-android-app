package com.jaspersoft.android.jaspermobile.domain.interactor;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.executor.PreExecutionThread;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public abstract class AbstractUseCase2<Result, Argument> implements UseCase2<Result, Argument> {
    private Subscription mSubscription = Subscriptions.empty();
    private Observable<Result> mCommand;

    /**
     * Builds an {@link rx.Observable} which will be used when executing the current {@link AbstractSimpleUseCase}.
     */
    protected abstract Observable<Result> buildUseCaseObservable(Argument argument);

    private final PreExecutionThread mPreExecutionThread;
    private final PostExecutionThread mPostExecutionThread;

    protected AbstractUseCase2(PreExecutionThread preExecutionThread, PostExecutionThread postExecutionThread) {
        mPreExecutionThread = preExecutionThread;
        mPostExecutionThread = postExecutionThread;
    }

    /**
     * Executes the current use case.
     *
     * @param useCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservable(Argument)}.
     */
    @Override
    public Subscription execute(@NonNull Argument argument, @NonNull Subscriber<? super Result> useCaseSubscriber) {
        if (mCommand == null) {
            mCommand = this.buildUseCaseObservable(argument);
        }
        return createSubscription(useCaseSubscriber);
    }

    /**
     * Rebinds subscriber to already executed action
     *
     * @param useCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservable(Argument)}.
     */
    @Override
    public Subscription subscribe(Subscriber<? super Result> useCaseSubscriber) {
        if (mCommand == null) {
            return Subscriptions.empty();
        }
        return createSubscription(useCaseSubscriber);
    }

    /**
     * Unsubscribes from current {@link rx.Subscription}.
     */
    @Override
    public void unsubscribe() {
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    private Subscription createSubscription(@NonNull Subscriber<? super Result> useCaseSubscriber) {
        mSubscription = mCommand
                .subscribeOn(mPreExecutionThread.getScheduler())
                .observeOn(mPostExecutionThread.getScheduler())
                .subscribe(useCaseSubscriber);

        return mSubscription;
    }
}
