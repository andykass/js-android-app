package com.jaspersoft.android.jaspermobile.data.entity.mapper;

import android.support.annotation.NonNull;

import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookupSearchCriteria;
import com.jaspersoft.android.sdk.service.repository.AccessType;
import com.jaspersoft.android.sdk.service.repository.RepositorySearchCriteria;
import com.jaspersoft.android.sdk.service.repository.SortType;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@Singleton
public class CriteriaMapper {
    @Inject
    public CriteriaMapper() {
    }

    @NonNull
    public RepositorySearchCriteria toRetrofittedCriteria(@NonNull ResourceLookupSearchCriteria legacyCriteria) {
        RepositorySearchCriteria.Builder criteriaBuilder = RepositorySearchCriteria.builder();
        criteriaBuilder.withFolderUri(legacyCriteria.getFolderUri());
        criteriaBuilder.withLimit(legacyCriteria.getLimit());
        criteriaBuilder.withOffset(legacyCriteria.getOffset());
        criteriaBuilder.withQuery(legacyCriteria.getQuery());
        criteriaBuilder.withRecursive(legacyCriteria.isRecursive());

        String accessType = legacyCriteria.getAccessType();
        if (accessType != null) {
            criteriaBuilder.withAccessType(AccessType.fromRawValue(accessType));
        }

        String sortBy = legacyCriteria.getSortBy();
        if (sortBy != null) {
            criteriaBuilder.withSortType(SortType.fromRawValue(sortBy));
        }
        criteriaBuilder.withResourceMask(adaptMask(legacyCriteria));

        return criteriaBuilder.build();
    }

    int adaptMask(ResourceLookupSearchCriteria criteria) {
        int mask = 0;
        for (String filterValue : criteria.getTypes()) {
            if ("folder".equals(filterValue)) {
                mask |= RepositorySearchCriteria.FOLDER;
            }
            if ("reportUnit".equals(filterValue)) {
                mask |= RepositorySearchCriteria.REPORT;
            }
            if ("dashboard".equals(filterValue)) {
                mask |= RepositorySearchCriteria.DASHBOARD;
            }
            if ("legacyDashboard".equals(filterValue)) {
                mask |= RepositorySearchCriteria.LEGACY_DASHBOARD;
            }
            if ("reportOptions".equals(filterValue)) {
                mask |= RepositorySearchCriteria.REPORT_OPTION;
            }
            if ("file".equals(filterValue)) {
                mask |= RepositorySearchCriteria.FILE;
            }
        }
        return mask;
    }
}
