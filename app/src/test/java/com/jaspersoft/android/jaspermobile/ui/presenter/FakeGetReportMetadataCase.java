package com.jaspersoft.android.jaspermobile.ui.presenter;

import com.jaspersoft.android.jaspermobile.FakePostExecutionThread;
import com.jaspersoft.android.jaspermobile.FakePreExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetReportMetadataCase;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import rx.Observable;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class FakeGetReportMetadataCase extends GetReportMetadataCase {
    private ResourceLookup mResource;

    protected FakeGetReportMetadataCase() {
        super(FakePreExecutionThread.create(), FakePostExecutionThread.create(), null, null, null, null);
    }

    public void setResource(ResourceLookup resource) {
        mResource = resource;
    }

    @Override
    protected Observable<ResourceLookup> buildUseCaseObservable(String data) {
        return Observable.just(mResource);
    }
}
