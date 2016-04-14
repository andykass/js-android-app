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
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class RecurrenceTypeDialogFragment extends BaseDialogFragment {

    private final static String RECURRENCE_ARG = "RECURRENCE_ARG";
    private final static String RECURRENCES_ARG = "RECURRENCES_ARG";

    private JobFormViewEntity.Recurrence selected;
    private List<JobFormViewEntity.Recurrence> items;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sr_recurrence_type);

        String[] labels = getLabels();
        int position = Arrays.asList(labels).indexOf(selected.toString());

        builder.setSingleChoiceItems(labels, position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mDialogListener != null) {
                    JobFormViewEntity.Recurrence recurrence = items.get(which);
                    ((RecurrenceTypeDialogFragment.RecurrenceTypeClickListener) mDialogListener)
                            .onRecurrenceSelected(recurrence);
                }
                dialog.dismiss();
            }
        });

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    private String[] getLabels() {
        int size = items.size();
        String[] labels = new String[size];
        for (int i = 0; i < size; i++) {
            labels[i] = items.get(i).toString();
        }
        return labels;
    }

    @Override
    protected void initDialogParams() {
        super.initDialogParams();

        Bundle args = getArguments();
        if (args != null) {
            selected = args.getParcelable(RECURRENCE_ARG);
            items = args.getParcelableArrayList(RECURRENCES_ARG);
        }
    }

    @Override
    protected Class<RecurrenceTypeClickListener> getDialogCallbackClass() {
        return RecurrenceTypeClickListener.class;
    }

    public static RecurrenceFragmentBuilder createBuilder(FragmentManager fragmentManager) {
        return new RecurrenceFragmentBuilder(fragmentManager);
    }

    //---------------------------------------------------------------------
    // Dialog Builder
    //---------------------------------------------------------------------

    public static class RecurrenceFragmentBuilder extends BaseDialogFragmentBuilder<RecurrenceTypeDialogFragment> {
        public RecurrenceFragmentBuilder(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public RecurrenceFragmentBuilder setRecurrence(JobFormViewEntity.Recurrence recurrence) {
            args.putParcelable(RECURRENCE_ARG, recurrence);
            return this;
        }

        public RecurrenceFragmentBuilder setRecurrences(List<JobFormViewEntity.Recurrence> recurrences) {
            args.putParcelableArrayList(RECURRENCES_ARG, new ArrayList<Parcelable>(recurrences));
            return this;
        }

        @Override
        protected RecurrenceTypeDialogFragment build() {
            return new RecurrenceTypeDialogFragment();
        }
    }

    //---------------------------------------------------------------------
    // Dialog Callback
    //---------------------------------------------------------------------

    public interface RecurrenceTypeClickListener extends DialogClickListener {
        void onRecurrenceSelected(JobFormViewEntity.Recurrence recurrence);
    }
}
