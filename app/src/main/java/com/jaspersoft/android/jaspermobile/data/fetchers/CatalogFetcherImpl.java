package com.jaspersoft.android.jaspermobile.data.fetchers;

import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.entity.Resource;
import com.jaspersoft.android.jaspermobile.domain.fetchers.CatalogFetcher;
import com.jaspersoft.android.jaspermobile.util.rx.RxTransformer;
import com.jaspersoft.android.sdk.service.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public abstract class CatalogFetcherImpl<SearchType, ResourceType extends Resource> implements CatalogFetcher {

    private List<Resource> mResourceList;
    private LoaderCallback mLoaderCallback;
    private Subscription mSearchSubscription;
    private CompositeSubscription mSubscriptionsList;
    private boolean mPreviousWasEmpty;

    public CatalogFetcherImpl() {
        this.mSubscriptionsList = new CompositeSubscription();
        this.mResourceList = new ArrayList<>();
        this.mLoaderCallback = EMPTY;
        this.mPreviousWasEmpty = true;
    }

    public LoaderCallback getLoaderCallback() {
        return mLoaderCallback;
    }

    public List<Resource> getResourceList() {
        return mResourceList;
    }

    @Override
    public void subscribe(LoaderCallback loaderCallback) {
        mLoaderCallback = loaderCallback;

        if (!mResourceList.isEmpty()) {
            mLoaderCallback.onLoaded(mResourceList);
        }
    }

    @Override
    public void unsubscribe() {
        mLoaderCallback = EMPTY;
    }

    @Override
    public void reset() {
        if (mSearchSubscription != null) {
            mSearchSubscription.unsubscribe();
            mSearchSubscription = null;
        }
        mResourceList = new ArrayList<>();
        search();
    }

    @Override
    public void search() {
        if (!searchTaskInitialized()) {
            createSearchTask();
            searchNext();
            return;
        }
        if (hasNext()) {
            searchNext();
        }
    }

    private void searchNext() {
        if (mSearchSubscription != null) return;

        int delay = mPreviousWasEmpty ? 0 : 750;

        mSearchSubscription = getNextTask()
                .delay(delay, TimeUnit.MILLISECONDS)
                .compose(RxTransformer.<List<SearchType>>applySchedulers())
                .subscribe(new SimpleSubscriber<List<SearchType>>() {
                    @Override
                    public void onStart() {
                        mLoaderCallback.onLoadStarted(mResourceList.isEmpty());
                    }

                    @Override
                    public void onNext(List<SearchType> items) {
                        mPreviousWasEmpty = items.isEmpty();
                        mResourceList.addAll(map(items));
                        mLoaderCallback.onLoaded(mResourceList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPreviousWasEmpty = true;
                        mLoaderCallback.onError((ServiceException) e, mResourceList.isEmpty());
                    }

                    @Override
                    public void onCompleted() {
                        mSearchSubscription = null;
                    }
                });
    }

    protected void observe(Observable<Void> observable) {
        mSubscriptionsList.add(observable.subscribe(new ResourcesObserver()));
    }

    protected abstract boolean searchTaskInitialized();

    protected abstract void createSearchTask();

    protected abstract boolean hasNext();

    protected abstract Observable<List<SearchType>> getNextTask();

    protected abstract List<ResourceType> map(List<SearchType> items);

    protected class ResourcesObserver extends SimpleSubscriber<Void> {
        @Override
        public void onNext(Void item) {
            reset();
        }
    }
}
