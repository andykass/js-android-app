package com.jaspersoft.android.jaspermobile.ui.model.mapper;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.domain.ProfileMetadata;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.ui.model.ProfileViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
public class ProfileViewModelMapper {

    @Inject
    public ProfileViewModelMapper() {
    }

    @NonNull
    public List<ProfileViewModel> transform(@NonNull List<ProfileMetadata> profiles) {
        List<ProfileViewModel> models = new ArrayList<>(profiles.size());
        for (ProfileMetadata profileMetadata : profiles) {
            if (profileMetadata != null) {
                ProfileViewModel model = transform(profileMetadata);
                models.add(model);
            }
        }
        return models;
    }

    @NonNull
    public ProfileViewModel transform(@NonNull ProfileMetadata profileMetadata) {
        Profile profile = profileMetadata.getProfile();
        JasperServer server = profileMetadata.getServer();

        String version = server.getVersion();
        boolean isActive = profileMetadata.isActive();

        return new ProfileViewModel(profile.getKey(), version, isActive);
    }
}
