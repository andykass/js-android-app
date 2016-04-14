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

package com.jaspersoft.android.jaspermobile.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class OutputFormatDialogFragment extends BaseDialogFragment implements DialogInterface.OnMultiChoiceClickListener {

    private final static String SELECTED_FORMATS_ARG = "SELECTED_FORMATS_ARG";
    private final static String FORMATS_ARG = "FORMATS_ARG";

    private List<JobFormViewEntity.OutputFormat> selectedFormats;
    private List<JobFormViewEntity.OutputFormat> formats;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sr_output_format);
        builder.setMultiChoiceItems(getLabels(), getSelected(), this);
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
            selectedFormats = args.getParcelableArrayList(SELECTED_FORMATS_ARG);
            formats = args.getParcelableArrayList(FORMATS_ARG);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (which >= formats.size()) return;

        JobFormViewEntity.OutputFormat item = formats.get(which);
        if (isChecked) {
            selectedFormats.add(item);
        } else {
            selectedFormats.remove(item);
        }
    }

    private String[] getLabels() {
        int size = formats.size();
        String[] labels = new String[size];
        for (int i = 0; i < size; i++) {
            labels[i] = formats.get(i).toString();
        }
        return labels;
    }

    private boolean[] getSelected() {
        boolean[] selected = new boolean[formats.size()];
        for (JobFormViewEntity.OutputFormat selectedFormat : selectedFormats) {
            int index = formats.indexOf(selectedFormat);
            selected[index] = true;
        }
        return selected;
    }

    @Override
    protected Class<OutputFormatClickListener> getDialogCallbackClass() {
        return OutputFormatClickListener.class;
    }

    public static OutputFormatFragmentBuilder createBuilder(FragmentManager fragmentManager) {
        return new OutputFormatFragmentBuilder(fragmentManager);
    }

    //---------------------------------------------------------------------
    // Dialog Builder
    //---------------------------------------------------------------------

    public static class OutputFormatFragmentBuilder extends BaseDialogFragmentBuilder<OutputFormatDialogFragment> {

        public OutputFormatFragmentBuilder(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public OutputFormatFragmentBuilder setSelected(List<JobFormViewEntity.OutputFormat> formats) {
            args.putParcelableArrayList(SELECTED_FORMATS_ARG, new ArrayList<>(formats));
            return this;
        }

        public OutputFormatFragmentBuilder setFormats(List<JobFormViewEntity.OutputFormat> formats) {
            args.putParcelableArrayList(FORMATS_ARG, new ArrayList<>(formats));
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
        void onOutputFormatSelected(List<JobFormViewEntity.OutputFormat> selectedFormats);
    }
}
