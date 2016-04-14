package com.jaspersoft.android.jaspermobile.ui.mapper;

import android.support.annotation.NonNull;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public interface UiEntityMapper<DomainEntity, UiEntity> {
    @NonNull
    UiEntity toUiEntity(@NonNull DomainEntity domainEntity);

    @NonNull
    DomainEntity toDomainEntity(@NonNull UiEntity uiEntity);
}
