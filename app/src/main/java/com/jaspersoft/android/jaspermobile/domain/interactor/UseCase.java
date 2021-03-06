package com.jaspersoft.android.jaspermobile.domain.interactor;

import rx.Subscriber;
import rx.Subscription;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public interface UseCase<Result, Argument> {
    Subscription execute(Argument argument, Subscriber<? super Result> subscriber);
    void unsubscribe();
}
