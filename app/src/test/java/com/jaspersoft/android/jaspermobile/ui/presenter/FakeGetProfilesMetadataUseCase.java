package com.jaspersoft.android.jaspermobile.ui.presenter;

import com.jaspersoft.android.jaspermobile.FakePostExecutionThread;
import com.jaspersoft.android.jaspermobile.FakePreExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.ProfileMetadataCollection;
import com.jaspersoft.android.jaspermobile.domain.interactor.profile.GetProfilesMetadataUseCase;

import rx.Observable;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class FakeGetProfilesMetadataUseCase extends GetProfilesMetadataUseCase {
    private ProfileMetadataCollection mProfileMetadataCollection;

    public FakeGetProfilesMetadataUseCase() {
        super(FakePreExecutionThread.create(), FakePostExecutionThread.create(), null, null);
    }

    public void setProfileMetadataCollection(ProfileMetadataCollection profileMetadataCollection) {
        mProfileMetadataCollection = profileMetadataCollection;
    }

    @Override
    protected Observable<ProfileMetadataCollection> buildUseCaseObservable() {
        return Observable.just(mProfileMetadataCollection);
    }
}
