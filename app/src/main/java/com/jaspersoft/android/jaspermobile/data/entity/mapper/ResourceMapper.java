package com.jaspersoft.android.jaspermobile.data.entity.mapper;

import android.support.annotation.NonNull;

import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.service.data.repository.Resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@Singleton
public class ResourceMapper {
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Inject
    public ResourceMapper() {
    }

    @NonNull
    public List<ResourceLookup> toLegacyResources(@NonNull List<Resource> resources) {
        List<ResourceLookup> list = new ArrayList<>(resources.size());
        for (Resource resource : resources) {
            if (resource != null) {
                ResourceLookup lookup = toLegacyResource(resource);
                list.add(lookup);
            }
        }
        return list;
    }

    @NonNull
    public ResourceLookup toLegacyResource(@NonNull Resource resource) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT, Locale.getDefault());
        String creationDate = simpleDateFormat.format(resource.getCreationDate());
        String updateDate = simpleDateFormat.format(resource.getUpdateDate());

        ResourceLookup lookup = new ResourceLookup();
        lookup.setLabel(resource.getLabel());
        lookup.setDescription(resource.getDescription());
        lookup.setUri(resource.getUri());
        lookup.setResourceType(ResourceLookup.ResourceType.valueOf(resource.getResourceType().getRawValue()));
        lookup.setVersion(resource.getVersion());
        lookup.setCreationDate(creationDate);
        lookup.setUpdateDate(updateDate);
        lookup.setPermissionMask(resource.getPermissionMask().getMask());

        return lookup;
    }
}
