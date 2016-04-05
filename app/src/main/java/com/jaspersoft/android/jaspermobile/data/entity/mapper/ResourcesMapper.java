package com.jaspersoft.android.jaspermobile.data.entity.mapper;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.domain.entity.JasperResource;
import com.jaspersoft.android.jaspermobile.internal.di.PerProfile;
import com.jaspersoft.android.sdk.service.data.repository.Resource;
import com.jaspersoft.android.sdk.service.data.repository.ResourceType;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
@PerProfile
public class ResourcesMapper {

    @Inject
    public ResourcesMapper() {
    }

    @NonNull
    public List<JasperResource> toJasperResources(@NonNull Collection<Resource> resources) {
        List<JasperResource> jasperResources = new ArrayList<>();
        for (Resource resource : resources) {
            JasperResource jasperResource = toJasperResource(resource);
            jasperResources.add(jasperResource);
        }
        return jasperResources;
    }

    @NonNull
    public JasperResource toJasperResource(@NonNull Resource resource) {
        URI resourceUri = URI.create(resource.getUri());
        JasperResource.Type type = parseResourceType(resource.getResourceType());
        return new JasperResource(resource.getLabel(), resourceUri, resource.getDescription(), type);
    }

    private JasperResource.Type parseResourceType(ResourceType resourceType) {
        switch (resourceType) {
            case reportUnit:
                return JasperResource.Type.report;
            case dashboard:
                return JasperResource.Type.dashboard;
            case legacyDashboard:
                return JasperResource.Type.legacyDashboard;
            case folder:
                return JasperResource.Type.folder;
            case file:
                return JasperResource.Type.file;
            default:
                return JasperResource.Type.undefined;
        }
    }
}
