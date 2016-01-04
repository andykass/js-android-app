/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from TIBCO Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of TIBCO Jaspersoft Mobile for Android.
 *
 * TIBCO Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TIBCO Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TIBCO Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.domain.interactor;

import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.data.repository.ProfileDataRepository;
import com.jaspersoft.android.jaspermobile.data.validator.CredentialsValidatorImpl;
import com.jaspersoft.android.jaspermobile.data.validator.ServerValidatorImpl;
import com.jaspersoft.android.jaspermobile.domain.AppCredentials;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.domain.ProfileForm;
import com.jaspersoft.android.jaspermobile.domain.repository.CredentialsRepository;
import com.jaspersoft.android.jaspermobile.domain.repository.JasperServerRepository;
import com.jaspersoft.android.jaspermobile.domain.repository.ProfileRepository;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.modules.CredentialsModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.JasperServerModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.ProfileModule;

import rx.Observable;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
public class SaveProfileUseCase extends AbstractUseCase<Profile, ProfileForm> {
    /**
     * Injected by {@link ProfileModule#providesProfileRepository(ProfileDataRepository)}
     */
    private final ProfileRepository mProfileRepository;
    /**
     * Injected by {@link JasperServerModule#providesServerValidator(ServerValidatorImpl)}
     */
    private final JasperServerRepository mJasperServerRepository;
    /**
     * Injected by {@link CredentialsModule#providesCredentialsValidator(CredentialsValidatorImpl)} ()}
     */
    private final CredentialsRepository mCredentialsDataRepository;

    @Inject
    public SaveProfileUseCase(ProfileRepository profileRepository,
                              JasperServerRepository jasperServerRepository,
                              CredentialsRepository credentialsDataRepository) {
        mProfileRepository = profileRepository;
        mJasperServerRepository = jasperServerRepository;
        mCredentialsDataRepository = credentialsDataRepository;
    }

    @Override
    protected Observable<Profile> buildUseCaseObservable(final ProfileForm form) {
        Profile profile = form.getProfile();
        String serverUrl = form.getServerUrl();
        AppCredentials credentials = form.getCredentials();

        Observable<Profile> saveProfileAction = mProfileRepository.saveProfile(profile);
        Observable<Profile> saveServerAction = mJasperServerRepository.saveServer(profile, serverUrl);
        Observable<Profile> saveCredentialsAction = mCredentialsDataRepository.saveCredentials(profile, credentials);
        Observable<Profile> activateProfileAction = mProfileRepository.activate(profile);

        return saveProfileAction
                .concatWith(saveServerAction)
                .concatWith(saveCredentialsAction)
                .concatWith(activateProfileAction);
    }
}
