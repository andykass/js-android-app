package com.jaspersoft.android.jaspermobile.data.mapper;

import android.support.annotation.NonNull;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public interface DataEntityMapper<DomainEntity, DataEntity> {
    @NonNull
    DataEntity toDataEntity(@NonNull DomainEntity domainEntity);

    @NonNull
    DomainEntity toDomainEntity(@NonNull DataEntity domainEntity);
}
