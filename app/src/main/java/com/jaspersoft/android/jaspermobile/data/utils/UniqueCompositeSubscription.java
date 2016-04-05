package com.jaspersoft.android.jaspermobile.data.utils;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class UniqueCompositeSubscription {
    private CompositeSubscription mSubscriptions;
    private List<Integer> mSubscriptionsIds;

    public UniqueCompositeSubscription() {
        mSubscriptions = new CompositeSubscription();
        mSubscriptionsIds = new ArrayList<>();
    }

    public void add(int id, Subscription subscription) {
        mSubscriptions.add(subscription);
        mSubscriptionsIds.add(id);
    }

    public void remove(Integer id){
        mSubscriptionsIds.remove(id);
    }

    public boolean contains(int id) {
        return mSubscriptionsIds.contains(id);
    }

    public void unsubscribe() {
        mSubscriptions.unsubscribe();
        mSubscriptions = new CompositeSubscription();
        mSubscriptionsIds = new ArrayList<>();
    }
}
