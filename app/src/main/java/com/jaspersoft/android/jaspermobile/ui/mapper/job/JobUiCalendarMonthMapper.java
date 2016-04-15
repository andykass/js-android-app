package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jaspersoft.android.jaspermobile.ui.entity.job.CalendarViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiCollectionEntityMapper;

/**
 * @author Tom Koptel
 * @since 2.5
 */
final class JobUiCalendarMonthMapper extends UiCollectionEntityMapper<Integer, CalendarViewRecurrence.Month> {

    @NonNull
    private final EntityLocalizer<Integer> localizer;

    @VisibleForTesting
    JobUiCalendarMonthMapper(@NonNull EntityLocalizer<Integer> localizer) {
        this.localizer = localizer;
    }

    @NonNull
    public static JobUiCalendarMonthMapper create() {
        MonthLocalizer monthLocalizer = new MonthLocalizer();
        return new JobUiCalendarMonthMapper(monthLocalizer);
    }

    @NonNull
    @Override
    public CalendarViewRecurrence.Month toUiEntity(@NonNull Integer domainEntity) {
        String localize = localizer.localize(domainEntity);
        return CalendarViewRecurrence.Month.create(localize, domainEntity);
    }

    @NonNull
    @Override
    public Integer toDomainEntity(@NonNull CalendarViewRecurrence.Month uiEntity) {
        return uiEntity.rawValue();
    }
}
