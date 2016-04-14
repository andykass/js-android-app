package com.jaspersoft.android.jaspermobile.data.mapper;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public abstract class DataCollectionEntityMapper<DomainEntity, DataEntity> implements DataEntityMapper<DomainEntity, DataEntity> {

    @NonNull
    public final List<DomainEntity> toDomainEntityList(@NonNull List<DataEntity> uiEntities) {
        List<DomainEntity> domainEntities = new ArrayList<>(uiEntities.size());
        for (DataEntity dataEntity : uiEntities) {
            domainEntities.add(toDomainEntity(dataEntity));
        }
        return domainEntities;
    }

    @NonNull
    public final List<DataEntity> toDataEntityList(@NonNull List<DomainEntity> domainEntities) {
        List<DataEntity> uiEntities = new ArrayList<>(domainEntities.size());
        for (DomainEntity uiEntity : domainEntities) {
            uiEntities.add(toDataEntity(uiEntity));
        }
        return uiEntities;
    }
}
