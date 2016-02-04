package com.jaspersoft.android.jaspermobile.domain.repository.resource;

import android.support.annotation.NonNull;

import com.jaspersoft.android.sdk.client.oxm.report.FolderDataResponse;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookupSearchCriteria;
import com.jaspersoft.android.sdk.service.data.report.ResourceOutput;
import com.jaspersoft.android.sdk.service.data.repository.Resource;

import java.util.List;

import rx.Observable;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public interface ResourceRepository {
    @NonNull
    Observable<Resource> getResourceByType(@NonNull String reportUri, @NonNull String type);

    @NonNull
    Observable<ResourceOutput> getResourceContent(@NonNull String resourceUri);

    @NonNull
    Observable<List<ResourceLookup>> searchResources(@NonNull ResourceLookupSearchCriteria criteria);

    @NonNull
    Observable<List<FolderDataResponse>> getRootRepositories();
}
