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
import com.jaspersoft.android.jaspermobile.ui.entity.job.SimpleViewRecurrence;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class IntervalUnitDialogFragment extends BaseDialogFragment {

    private final static String UNIT_ARG = "UNIT_ARG";
    private final static String UNITS_ARG = "UNITS_ARG";

    private SimpleViewRecurrence.Unit selected;
    private ArrayList<SimpleViewRecurrence.Unit> items;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String[] labels = getLabels();
        int position = getPosition(selected);
        builder.setSingleChoiceItems(labels, position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mDialogListener != null) {
                    SimpleViewRecurrence.Unit unit = items.get(which);
                    ((IntervalUnitClickListener) mDialogListener)
                            .onUnitSelected(unit);
                }
                dialog.dismiss();
            }
        });

        builder.setTitle(R.string.sr_recurrence_type);

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    protected void initDialogParams() {
        super.initDialogParams();

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(UNIT_ARG)) {
                selected = args.getParcelable(UNIT_ARG);
                items = args.getParcelableArrayList(UNITS_ARG);
            }
        }
    }

    private String[] getLabels() {
        int size = items.size();
        String[] labels = new String[size];
        for (int i = 0; i < size; i++) {
            labels[i] = items.get(i).toString();
        }
        return labels;
    }

    private int getPosition(SimpleViewRecurrence.Unit unit) {
        return items.indexOf(unit);
    }

    @Override
    protected Class<IntervalUnitClickListener> getDialogCallbackClass() {
        return IntervalUnitClickListener.class;
    }

    public static IntervalUnitFragmentBuilder createBuilder(FragmentManager fragmentManager) {
        return new IntervalUnitFragmentBuilder(fragmentManager);
    }

    //---------------------------------------------------------------------
    // Dialog Builder
    //---------------------------------------------------------------------

    public static class IntervalUnitFragmentBuilder extends BaseDialogFragmentBuilder<IntervalUnitDialogFragment> {
        public IntervalUnitFragmentBuilder(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public IntervalUnitFragmentBuilder setUnit(SimpleViewRecurrence.Unit unit) {
            args.putParcelable(UNIT_ARG, unit);
            return this;
        }

        public IntervalUnitFragmentBuilder setUnits(List<SimpleViewRecurrence.Unit> units) {
            args.putParcelableArrayList(UNITS_ARG, new ArrayList<Parcelable>(units));
            return this;
        }

        @Override
        protected IntervalUnitDialogFragment build() {
            return new IntervalUnitDialogFragment();
        }
    }

    //---------------------------------------------------------------------
    // Dialog Callback
    //---------------------------------------------------------------------

    public interface IntervalUnitClickListener extends DialogClickListener {
        void onUnitSelected(SimpleViewRecurrence.Unit unit);
    }
}
