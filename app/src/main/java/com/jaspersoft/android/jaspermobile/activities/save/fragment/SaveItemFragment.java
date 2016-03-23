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

package com.jaspersoft.android.jaspermobile.activities.save.fragment;

import android.app.ActionBar;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.JasperMobileApplication;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.save.SaveReportService;
import com.jaspersoft.android.jaspermobile.data.entity.ExportBundle;
import com.jaspersoft.android.jaspermobile.dialog.NumberDialogFragment;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;
import com.jaspersoft.android.jaspermobile.util.SavedItemHelper;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.util.FileUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import javax.inject.Inject;

import timber.log.Timber;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment(R.layout.save_report_layout)
@OptionsMenu(R.menu.save_item_menu)
public class SaveItemFragment extends BaseFragment implements NumberDialogFragment.NumberDialogClickListener {

    public static final String TAG = SaveItemFragment.class.getSimpleName();

    private final static int FROM_PAGE_REQUEST_CODE = 1243;
    private final static int TO_PAGE_REQUEST_CODE = 2243;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;

    @ViewById(R.id.output_format_spinner)
    Spinner formatSpinner;
    @ViewById(R.id.report_name_input)
    EditText reportNameInput;

    @ViewById
    LinearLayout rangeControls;
    @ViewById
    TextView fromPageControl;
    @ViewById
    TextView toPageControl;

    @FragmentArg
    ResourceLookup resource;
    @FragmentArg
    int pageCount;

    @Bean
    protected SavedItemHelper savedItemHelper;

    @OptionsMenuItem
    MenuItem saveAction;

    @Inject
    protected Profile mProfile;

    private int mFromPage;
    private int mToPage;

    public enum OutputFormat {
        HTML,
        PDF,
        XLS
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseActivityComponent().inject(this);

        hasOptionsMenu();

        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.sr_ab_title);
        }
    }

    @OptionsItem
    final void saveAction() {
        if (canMakeSmores()) {
            boolean permissionDenied =
                    (ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED);
            if (permissionDenied) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return;
            }
        }
        performSaveAction();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            performSaveAction();
        } else {
            Toast.makeText(getActivity(), R.string.enable_write_permission, Toast.LENGTH_LONG).show();
        }
    }

    public boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private void performSaveAction() {
        if (!isReportNameValid()) return;

        OutputFormat outputFormat = (OutputFormat) formatSpinner.getSelectedItem();
        String reportName = reportNameInput.getText() + "." + outputFormat;
        File reportDir = getAccountReportDir(reportName);

        if (reportDir == null) {
            Toast.makeText(getActivity(), R.string.sr_failed_to_create_local_repo, Toast.LENGTH_SHORT).show();
            return;
        }

        File reportFile = new File(reportDir, reportName);
        String pageRange = calculatePages(mFromPage, mToPage);

        ExportBundle bundle = new ExportBundle.Builder()
                .setUri(resource.getUri())
                .setLabel(reportNameInput.getText().toString())
                .setDescription(resource.getDescription())
                .setFormat(outputFormat.name())
                .setFile(reportFile)
                .setPageRange(pageRange)
                .build();

        SaveReportService.start(getActivity(), bundle);
        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sdr_starting_downloading_msg), Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    @AfterViews
    final void init() {
        // show spinner with available output formats
        ArrayAdapter<OutputFormat> arrayAdapter = new ArrayAdapter<OutputFormat>(getActivity(),
                android.R.layout.simple_spinner_item, OutputFormat.values());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formatSpinner.setAdapter(arrayAdapter);

        reportNameInput.setText(resource.getLabel());

        // hide save parts views if report have only 1 page
        if (pageCount > 1) {
            rangeControls.setVisibility(View.VISIBLE);

            mFromPage = 1;
            mToPage = pageCount;

            fromPageControl.setText(String.valueOf(mFromPage));
            toPageControl.setText(String.valueOf(mToPage));
        }
    }

    @Click(R.id.fromPageControl)
    void clickOnFromPage() {
        NumberDialogFragment.createBuilder(getFragmentManager())
                .setMinValue(1)
                .setCurrentValue(mFromPage)
                .setMaxValue(pageCount)
                .setRequestCode(FROM_PAGE_REQUEST_CODE)
                .setTargetFragment(this)
                .show();
    }

    @Click(R.id.toPageControl)
    void clickOnToPage() {
        NumberDialogFragment.createBuilder(getFragmentManager())
                .setMinValue(mFromPage)
                .setCurrentValue(mToPage)
                .setMaxValue(pageCount)
                .setRequestCode(TO_PAGE_REQUEST_CODE)
                .setTargetFragment(this)
                .show();
    }

    @ItemSelect(R.id.output_format_spinner)
    public void formatItemSelected(boolean selected, OutputFormat selectedItem) {
        reportNameInput.setError(null);
    }

    @TextChange(R.id.report_name_input)
    final void reportNameChanged() {
        boolean nameValid = isReportNameValid();
        if (saveAction != null) {
            saveAction.setIcon(nameValid ? R.drawable.ic_menu_save : R.drawable.ic_menu_save_disabled);
        }
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private boolean isReportNameValid() {
        String reportName = reportNameInput.getText().toString();
        OutputFormat outputFormat = (OutputFormat) formatSpinner.getSelectedItem();

        if (reportName.trim().isEmpty()) {
            reportNameInput.setError(getString(R.string.sr_error_field_is_empty));
            return false;
        }

        // reserved characters: * \ / " ' : ? | < > + [ ]
        if (FileUtils.nameContainsReservedChars(reportName)) {
            reportNameInput.setError(getString(R.string.sr_error_characters_not_allowed));
            return false;
        }

        if (savedItemHelper.itemExist(reportName, outputFormat.name())) {
            reportNameInput.setError(getString(R.string.sr_error_report_exists));
            return false;
        }

        reportNameInput.setError(null);
        return true;
    }

    @Nullable
    private File getAccountReportDir(String reportName) {
        File appFilesDir = getActivity().getExternalFilesDir(null);
        File savedReportsDir = new File(appFilesDir, JasperMobileApplication.SAVED_REPORTS_DIR_NAME);

        File accountReportDir = new File(savedReportsDir, mProfile.getKey());
        File reportDir = new File(accountReportDir, reportName);

        if (!reportDir.exists() && !reportDir.mkdirs()) {
            Timber.e("Unable to create %s", savedReportsDir);
            return null;
        }

        return reportDir;
    }

    private String calculatePages(int fromPage, int toPage) {
        if (toPage == 0) return null;

        boolean pagesNumbersIsValid = fromPage > 0 && toPage > 0 && toPage >= fromPage;
        if (pagesNumbersIsValid) {
            boolean isRange = fromPage < toPage;
            if (isRange) {
                return fromPage + "-" + toPage;
            } else {
                return String.valueOf(fromPage);
            }
        }
        return "1";
    }

    //---------------------------------------------------------------------
    // Page Select Listeners
    //---------------------------------------------------------------------

    @Override
    public void onPageSelected(int page, int requestCode) {
        if (requestCode == FROM_PAGE_REQUEST_CODE) {
            boolean isPagePositive = (page > 1);
            boolean isRangeCorrect = (page <= mToPage);
            if (isPagePositive && isRangeCorrect) {
                boolean enableComponent = (page != pageCount);
                toPageControl.setEnabled(enableComponent);

                mFromPage = page;
                fromPageControl.setText(String.valueOf(mFromPage));
            }
        } else {
            boolean isRangeCorrect = (page >= mFromPage);
            if (isRangeCorrect) {
                mToPage = page;
                toPageControl.setText(String.valueOf(mToPage));
            }
        }
    }
}