package com.jaspersoft.android.jaspermobile.ui.mapper;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public abstract class UiCollectionEntityMapper<DomainEntity, UiEntity> implements UiEntityMapper<DomainEntity,UiEntity> {

    @NonNull
    public final List<DomainEntity> toDomainEntityList(@NonNull List<UiEntity> uiEntities) {
        List<DomainEntity> domainEntities = new ArrayList<>(uiEntities.size());
        for (UiEntity uiEntity : uiEntities) {
            domainEntities.add(toDomainEntity(uiEntity));
        }
        return domainEntities;
    }

    @NonNull
    public final List<UiEntity> toUiEntityList(@NonNull List<DomainEntity> domainEntities) {
        List<UiEntity> uiEntities = new ArrayList<>(domainEntities.size());
        for (DomainEntity uiEntity : domainEntities) {
            uiEntities.add(toUiEntity(uiEntity));
        }
        return uiEntities;
    }
}
