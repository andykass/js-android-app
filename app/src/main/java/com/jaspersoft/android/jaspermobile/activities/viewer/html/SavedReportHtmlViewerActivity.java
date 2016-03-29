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

package com.jaspersoft.android.jaspermobile.activities.viewer.html;

import android.net.Uri;
import android.os.Bundle;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.dialog.DeleteDialogFragment;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.util.SavedItemHelper;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import java.io.File;

/**
 * Activity that performs report viewing in HTML format.
 *
 * @author Ivan Gadzhega
 * @since 1.4
 */

@EActivity
@OptionsMenu(R.menu.saved_report)
public class SavedReportHtmlViewerActivity extends ToolbarActivity
        implements WebViewFragment.OnWebViewCreated, DeleteDialogFragment.DeleteDialogClickListener {

    @Extra
    protected File reportFile;

    @Extra
    protected String recordUri;

    @Extra
    protected String resourceLabel;

    @Bean
    protected SavedItemHelper savedItemHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            WebViewFragment webViewFragment = WebViewFragment_.builder()
                    .resourceLabel(resourceLabel).build();
            webViewFragment.setOnWebViewCreated(this);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, webViewFragment, WebViewFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void onWebViewCreated(WebViewFragment webViewFragment) {
        Uri reportOutputPath = Uri.fromFile(reportFile);
        webViewFragment.loadUrl(reportOutputPath.toString());
    }

    @OptionsItem
    final void deleteItem() {
        DeleteDialogFragment.createBuilder(this, getSupportFragmentManager())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.sdr_drd_title)
                .setMessage(getString(R.string.sdr_drd_msg, resourceLabel))
                .setPositiveButtonText(R.string.spm_delete_btn)
                .setNegativeButtonText(R.string.cancel)
                .show();
    }

    @Override
    protected String getScreenName() {
        return getString(R.string.ja_s_hf_s);
    }

    //---------------------------------------------------------------------
    // Implements Implements DeleteDialogFragment.DeleteDialogClickListener
    //---------------------------------------------------------------------

    @Override
    public void onDeleteConfirmed(JasperResource resource) {
        savedItemHelper.deleteSavedItem(Uri.parse(recordUri));
        finish();
    }
}
