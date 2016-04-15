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
import com.jaspersoft.android.jaspermobile.ui.entity.job.CalendarViewRecurrence;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class CalendarMonthDialogFragment extends BaseDialogFragment implements DialogInterface.OnMultiChoiceClickListener {

    private final static String SELECTED_MONTHS_ARG = "SELECTED_MONTHS_ARG";
    private final static String MONTHS_ARG = "MONTHS_ARG";

    private List<CalendarViewRecurrence.Month> selectedDays;
    private List<CalendarViewRecurrence.Month> days;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sr_months);
        builder.setMultiChoiceItems(getLabels(), getSelected(), this);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mDialogListener != null) {
                    ((MonthsSelectedListener) mDialogListener).onMonthsSelected(selectedDays);
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
            selectedDays = args.getParcelableArrayList(SELECTED_MONTHS_ARG);
            days = args.getParcelableArrayList(MONTHS_ARG);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (which >= days.size()) return;

        CalendarViewRecurrence.Month item = days.get(which);
        if (isChecked) {
            selectedDays.add(item);
        } else {
            selectedDays.remove(item);
        }
    }

    private String[] getLabels() {
        int size = days.size();
        String[] labels = new String[size];
        for (int i = 0; i < size; i++) {
            labels[i] = days.get(i).toString();
        }
        return labels;
    }

    private boolean[] getSelected() {
        boolean[] selected = new boolean[days.size()];
        for (CalendarViewRecurrence.Month selectedFormat : selectedDays) {
            int index = days.indexOf(selectedFormat);
            selected[index] = true;
        }
        return selected;
    }

    @Override
    protected Class<MonthsSelectedListener> getDialogCallbackClass() {
        return MonthsSelectedListener.class;
    }

    public static CalendarMonthFragmentBuilder createBuilder(FragmentManager fragmentManager) {
        return new CalendarMonthFragmentBuilder(fragmentManager);
    }

    //---------------------------------------------------------------------
    // Dialog Builder
    //---------------------------------------------------------------------

    public static class CalendarMonthFragmentBuilder extends BaseDialogFragmentBuilder<CalendarMonthDialogFragment> {

        public CalendarMonthFragmentBuilder(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public CalendarMonthFragmentBuilder setSelected(List<CalendarViewRecurrence.Month> formats) {
            args.putParcelableArrayList(SELECTED_MONTHS_ARG, new ArrayList<>(formats));
            return this;
        }

        public CalendarMonthFragmentBuilder setMonths(List<CalendarViewRecurrence.Month> formats) {
            args.putParcelableArrayList(MONTHS_ARG, new ArrayList<>(formats));
            return this;
        }

        @Override
        protected CalendarMonthDialogFragment build() {
            return new CalendarMonthDialogFragment();
        }
    }

    //---------------------------------------------------------------------
    // Dialog Callback
    //---------------------------------------------------------------------

    public interface MonthsSelectedListener extends DialogClickListener {
        void onMonthsSelected(List<CalendarViewRecurrence.Month> selectedMonths);
    }
}
