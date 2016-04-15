package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import com.ibm.icu.util.Calendar;
import com.jaspersoft.android.jaspermobile.ui.entity.job.CalendarViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobUiCalendarMonthMapperTest {

    public static final String LOCALIZED_MONTH = "Localized month";
    public static final int AUGUST = Calendar.AUGUST;
    @Mock
    EntityLocalizer<Integer> localizer;
    private JobUiCalendarMonthMapper monthMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        monthMapper = new JobUiCalendarMonthMapper(localizer);
        when(localizer.localize(anyInt())).thenReturn(LOCALIZED_MONTH);
    }

    @Test
    public void testToUiEntity() throws Exception {
        CalendarViewRecurrence.Month uiEntity = CalendarViewRecurrence.Month.create(LOCALIZED_MONTH, AUGUST);
        Integer domainEntity = monthMapper.toDomainEntity(uiEntity);
        assertThat(domainEntity, is(AUGUST));
    }

    @Test
    public void testToDomainEntity() throws Exception {
        CalendarViewRecurrence.Month month = monthMapper.toUiEntity(AUGUST);

        verify(localizer).localize(AUGUST);
        assertThat(month.localizedLabel(), is(LOCALIZED_MONTH));
        assertThat(month.rawValue(), is(Calendar.AUGUST));
    }
}