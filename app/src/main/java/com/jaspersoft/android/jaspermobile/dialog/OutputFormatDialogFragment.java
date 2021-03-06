/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.util.JobOutputFormatConverter;
import com.jaspersoft.android.sdk.client.ic.InputControlWrapper;
import com.jaspersoft.android.sdk.service.data.schedule.JobOutputFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class OutputFormatDialogFragment extends BaseDialogFragment implements DialogInterface.OnMultiChoiceClickListener {

    private final static String FORMATS_ARG = "formats";

    private List<JobOutputFormat> supportedFormats;
    private ArrayList<JobOutputFormat> selectedFormats;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.sr_output_format);

        createSupportedJobOutputFormatsList();

        builder.setMultiChoiceItems(JobOutputFormatConverter.toStringsArray(getContext(), supportedFormats), getSelected(), this);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mDialogListener != null) {
                    ((OutputFormatClickListener) mDialogListener).onOutputFormatSelected(selectedFormats);
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    protected void initDialogParams() {
        super.initDialogParams();

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(FORMATS_ARG)) {
                selectedFormats = (ArrayList<JobOutputFormat>) args.getSerializable(FORMATS_ARG);
            }
        }
    }

    @Override
    protected Class<OutputFormatClickListener> getDialogCallbackClass() {
        return OutputFormatClickListener.class;
    }

    public static OutputFormatFragmentBuilder createBuilder(FragmentManager fragmentManager) {
        return new OutputFormatFragmentBuilder(fragmentManager);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (which >= supportedFormats.size()) return;

        JobOutputFormat item = supportedFormats.get(which);
        if (isChecked) {
            selectedFormats.add(item);
        } else {
            selectedFormats.remove(item);
        }
    }

    private void createSupportedJobOutputFormatsList() {
        supportedFormats = new ArrayList<>();
        supportedFormats.add(JobOutputFormat.CSV);
        supportedFormats.add(JobOutputFormat.DOCX);
        supportedFormats.add(JobOutputFormat.XLS_NOPAG);
        supportedFormats.add(JobOutputFormat.XLS);
        supportedFormats.add(JobOutputFormat.HTML);
        supportedFormats.add(JobOutputFormat.ODS);
        supportedFormats.add(JobOutputFormat.ODT);
        supportedFormats.add(JobOutputFormat.PDF);
        supportedFormats.add(JobOutputFormat.RTF);
        supportedFormats.add(JobOutputFormat.XLSX_NOPAG);
        supportedFormats.add(JobOutputFormat.XLSX);
        supportedFormats.add(JobOutputFormat.PPTX);
    }

    private boolean[] getSelected() {
        boolean[] selected = new boolean[supportedFormats.size()];
        for (JobOutputFormat selectedFormat : selectedFormats) {
            int index = supportedFormats.indexOf(selectedFormat);
            selected[index] = true;
        }
        return selected;
    }

    //---------------------------------------------------------------------
    // Dialog Builder
    //---------------------------------------------------------------------

    public static class OutputFormatFragmentBuilder extends BaseDialogFragmentBuilder<OutputFormatDialogFragment> {

        public OutputFormatFragmentBuilder(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public OutputFormatFragmentBuilder setSelectedFormats(ArrayList<JobOutputFormat> formats) {
            args.putSerializable(FORMATS_ARG, formats);
            return this;
        }

        @Override
        protected OutputFormatDialogFragment build() {
            return new OutputFormatDialogFragment();
        }
    }

    //---------------------------------------------------------------------
    // Dialog Callback
    //---------------------------------------------------------------------

    public interface OutputFormatClickListener extends DialogClickListener {
        void onOutputFormatSelected(ArrayList<JobOutputFormat> outputFormatList);
    }
}
