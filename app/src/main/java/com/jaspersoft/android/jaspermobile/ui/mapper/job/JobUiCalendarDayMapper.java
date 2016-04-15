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
final class JobUiCalendarDayMapper extends UiCollectionEntityMapper<Integer, CalendarViewRecurrence.Day> {
    @NonNull
    private final EntityLocalizer<Integer> localizer;

    @VisibleForTesting
    JobUiCalendarDayMapper(@NonNull EntityLocalizer<Integer> localizer) {
        this.localizer = localizer;
    }

    @NonNull
    public static JobUiCalendarDayMapper create() {
        DayLocalizer dayLocalizer = new DayLocalizer();
        return new JobUiCalendarDayMapper(dayLocalizer);
    }

    @NonNull
    @Override
    public CalendarViewRecurrence.Day toUiEntity(@NonNull Integer domainEntity) {
        String localize = localizer.localize(domainEntity);
        return CalendarViewRecurrence.Day.create(localize, domainEntity);
    }

    @NonNull
    @Override
    public Integer toDomainEntity(@NonNull CalendarViewRecurrence.Day uiEntity) {
        return uiEntity.rawValue();
    }
}
