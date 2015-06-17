/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Mobile for Android.
 *
 * Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.util.report;

import com.jaspersoft.android.sdk.client.oxm.report.ExportExecution;
import com.jaspersoft.android.sdk.client.oxm.report.ExportsRequest;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Tom Koptel
 * @since 2.1
 */
@RunWith(JUnitParamsRunner.class)
public class ExportIdFormatImplTest {

    @Test
    public void shouldAdaptExportIdFor5_5() {
        ExportsRequest exportRequest = new ExportsRequest();
        exportRequest.setPages("1-5");
        exportRequest.setOutputFormat("PDF");

        ExportExecution exportExecution = new ExportExecution();
        exportExecution.setId("PDF");

        ExportIdFormat adapter = ExportIdFormatFactory.builder()
                .setExportExecution(exportExecution)
                .setExportsRequest(exportRequest)
                .build()
                .createAdapter("5.5");
        assertThat(adapter.format(), is("PDF;pages=1-5"));
    }

    @Test
    @Parameters({"5.6", "5.6.1", "6.0", "6.0.1", "6.1"})
    public void shouldAdaptExportIdByDefault(String serverVersion) {
        ExportsRequest exportRequest = new ExportsRequest();
        exportRequest.setPages("1-5");
        exportRequest.setOutputFormat("PDF");

        ExportExecution exportExecution = new ExportExecution();
        exportExecution.setId("1234");

        ExportIdFormat adapter = ExportIdFormatFactory.builder()
                .setExportExecution(exportExecution)
                .setExportsRequest(exportRequest)
                .build()
                .createAdapter(serverVersion);
        assertThat(adapter.format(), is("1234"));
    }

}
