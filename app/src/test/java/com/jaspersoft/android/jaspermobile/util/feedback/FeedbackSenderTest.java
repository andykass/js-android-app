/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from TIBCO Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of TIBCO Jaspersoft Mobile for Android.
 *
 * TIBCO Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TIBCO Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TIBCO Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.util.feedback;

import android.content.Intent;
import android.net.Uri;

import com.jaspersoft.android.jaspermobile.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 2.1
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(manifest = Config.NONE, sdk = 21, constants = BuildConfig.class)
public class FeedbackSenderTest {

    @Mock
    Message feedbackMessage;
    FeedbackSender sender;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sender = new FeedbackSender(RuntimeEnvironment.application, feedbackMessage);
    }

    @Test
    public void shouldCreateIntentWithActionSendTo() {
        Intent intent = sender.buildIntent();
        assertThat(intent.getAction(), is(Intent.ACTION_SENDTO));
    }

    @Test
    public void shouldCreateIntentWithDataMailTo() {
        Intent intent = sender.buildIntent();
        assertThat(intent.getData(), is(Uri.parse("mailto:")));
    }

    @Test
    public void shouldCreateIntentWithSubjectMails() {
        Intent intent = sender.buildIntent();
        String[] emails = {"js-dev-mobile@tibco.com", "js.testdevice@gmail.com"};
        assertThat(intent.getStringArrayExtra(Intent.EXTRA_EMAIL), is(emails));
    }

    @Test
    public void shouldCreateIntentWithSubject() {
        Intent intent = sender.buildIntent();
        assertThat(intent.getStringExtra(Intent.EXTRA_SUBJECT), is("Feedback"));
    }

    @Test
    public void shouldCreateIntentWithSubjectExtraText() {
        when(feedbackMessage.create()).thenReturn("message");

        Intent intent = sender.buildIntent();
        assertThat(intent.getStringExtra(Intent.EXTRA_TEXT), is("message"));

        verify(feedbackMessage, times(1)).create();
    }

    // Just assert method doesn't cause NPE
    @Test
    public void shouldSendIntent() {
        sender.initiate();
    }

}
