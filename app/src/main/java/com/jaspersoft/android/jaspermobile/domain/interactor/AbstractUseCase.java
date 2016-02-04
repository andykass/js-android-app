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
public abstract class AbstractUseCase<Result, Argument> implements UseCase<Result, Argument> {
    private Subscription subscription = Subscriptions.empty();

    /**
     * Builds an {@link rx.Observable} which will be used when executing the current {@link AbstractSimpleUseCase}.
     */
    protected abstract Observable<Result> buildUseCaseObservable(Argument argument);

    private final PreExecutionThread mPreExecutionThread;
    private final PostExecutionThread mPostExecutionThread;

    protected AbstractUseCase(PreExecutionThread preExecutionThread, PostExecutionThread postExecutionThread) {
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
        Observable<Result> command = this.buildUseCaseObservable(argument);
        this.subscription = command
                .subscribeOn(mPreExecutionThread.getScheduler())
                .observeOn(mPostExecutionThread.getScheduler())
                .subscribe(useCaseSubscriber);
        return subscription;
    }

    /**
     * Unsubscribes from current {@link rx.Subscription}.
     */
    @Override
    public void unsubscribe() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
