package com.jaspersoft.android.jaspermobile.data.fetchers;

import com.jaspersoft.android.jaspermobile.data.JasperRestClient;
import com.jaspersoft.android.jaspermobile.data.entity.mapper.ResourcesMapper;
import com.jaspersoft.android.jaspermobile.data.entity.mapper.ResourcesSortMapper;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.entity.JasperResource;
import com.jaspersoft.android.jaspermobile.domain.model.JasperResourceModel;
import com.jaspersoft.android.jaspermobile.domain.store.SearchQueryStore;
import com.jaspersoft.android.jaspermobile.domain.store.SortStore;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.sdk.service.data.repository.Resource;
import com.jaspersoft.android.sdk.service.repository.RepositorySearchCriteria;
import com.jaspersoft.android.sdk.service.repository.SortType;
import com.jaspersoft.android.sdk.service.rx.repository.RxRepositorySearchTask;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
@PerScreen
public class ResourcesFetcher extends CatalogFetcherImpl<Resource, JasperResource>  {
    private final JasperRestClient mClient;
    private final SortStore mSortStore;
    private final SearchQueryStore mSearchQueryStore;
    private final ResourcesMapper mResourcesMapper;
    private final ResourcesSortMapper mResourcesSortMapper;
    private final JasperResourceModel mJasperResourceModel;

    private RxRepositorySearchTask mSearchTask;

    @Inject
    public ResourcesFetcher(JasperRestClient mClient,
                            SortStore mSortStore,
                            SearchQueryStore mSearchQueryStore,
                            ResourcesMapper jobsMapper,
                            ResourcesSortMapper mResourcesSortMapper,
                            JasperResourceModel mJasperResourceModel) {
        this.mClient = mClient;
        this.mSortStore = mSortStore;
        this.mSearchQueryStore = mSearchQueryStore;
        this.mResourcesMapper = jobsMapper;
        this.mResourcesSortMapper = mResourcesSortMapper;
        this.mJasperResourceModel = mJasperResourceModel;
    }

    @Override
    public void reset() {
        mSearchTask = null;
        mJasperResourceModel.clear();
        super.reset();
    }

    @Override
    protected boolean searchTaskInitialized() {
        return mSearchTask != null;
    }

    @Override
    protected void createSearchTask() {
        SortType sortType = mResourcesSortMapper.to(mSortStore.getSortType());
        RepositorySearchCriteria repositorySearchCriteria = RepositorySearchCriteria.builder()
                .withFolderUri("/")
                .withLimit(40)
                .withQuery(mSearchQueryStore.getQuery())
                .withRecursive(true)
                .withResourceMask(RepositorySearchCriteria.REPORT)
                .withSortType(sortType)
                .build();

        mSearchTask = mClient.repositoryService().toBlocking().first().search(repositorySearchCriteria);
    }

    @Override
    protected boolean hasNext() {
        return mSearchTask.hasNext();
    }

    @Override
    protected Observable<List<Resource>> getNextTask() {
        return mSearchTask.nextLookup();
    }

    @Override
    protected List<JasperResource> map(List<Resource> items) {
        return mResourcesMapper.toJasperResources(items);
    }
}
