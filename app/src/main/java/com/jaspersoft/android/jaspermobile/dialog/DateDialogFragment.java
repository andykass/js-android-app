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

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.androidannotations.annotations.EFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Andrew Tivodar
 * @since 2.2
 */
@EFragment
public class DateDialogFragment extends BaseDialogFragment {

    public final static int DATE = 0;
    public final static int TIME = 1;

    private final static String IC_ID_ARG = "ic_id_arg";
    private final static String DATE_ARG = "date_arg";
    private final static String TYPE_ARG = "type_arg";

    private int type;
    protected String icId;
    protected Calendar activeDate;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (type == DATE) {
            return new DatePickerDialog(getActivity(),
                    new DateChangeListener(),
                    activeDate.get(Calendar.YEAR),
                    activeDate.get(Calendar.MONTH),
                    activeDate.get(Calendar.DAY_OF_MONTH));
        } else {
            return new TimePickerDialog(getActivity(),
                    new TimeChangeListener(),
                    activeDate.get(Calendar.HOUR_OF_DAY),
                    activeDate.get(Calendar.MINUTE),
                    true);
        }
    }

    @Override
    protected Class<IcDateDialogClickListener> getDialogCallbackClass() {
        return IcDateDialogClickListener.class;
    }

    protected void initDialogParams() {
        super.initDialogParams();

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(DATE_ARG)) {
                long dateMs = args.getLong(DATE_ARG);
                activeDate = Calendar.getInstance();
                activeDate.setTimeInMillis(dateMs);
            }
            if (args.containsKey(IC_ID_ARG)) {
                icId = args.getString(IC_ID_ARG);
            }
            if (args.containsKey(TYPE_ARG)) {
                type = args.getInt(TYPE_ARG);
            }
        }
    }

    public static DateDialogFragmentBuilder createBuilder(FragmentManager fragmentManager) {
        return new DateDialogFragmentBuilder(fragmentManager);
    }

    //---------------------------------------------------------------------
    // Dialog Builder
    //---------------------------------------------------------------------

    public static class DateDialogFragmentBuilder extends BaseDialogFragmentBuilder<DateDialogFragment> {

        public DateDialogFragmentBuilder(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public DateDialogFragmentBuilder setInputControlId(String id) {
            args.putString(IC_ID_ARG, id);
            return this;
        }

        public DateDialogFragmentBuilder setDate(Calendar date) {
            long dateLong = date != null ? date.getTimeInMillis() : new Date().getTime();
            args.putLong(DATE_ARG, dateLong);
            return this;
        }

        public DateDialogFragmentBuilder setType(int type) {
            args.putInt(TYPE_ARG, type);
            return this;
        }

        @Override
        protected DateDialogFragment build() {
            return new DateDialogFragment_();
        }
    }

    //---------------------------------------------------------------------
    // Dialog Callback
    //---------------------------------------------------------------------

    public interface IcDateDialogClickListener extends DialogClickListener {
        void onDateSelected(Calendar date, int requestCode, Object... data);
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    private class DateChangeListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            activeDate.set(year, monthOfYear, dayOfMonth);
            ((IcDateDialogClickListener) mDialogListener).onDateSelected(activeDate, requestCode, icId);
        }
    }

    private class TimeChangeListener implements TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            activeDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            activeDate.set(Calendar.MINUTE, minute);
            ((IcDateDialogClickListener) mDialogListener).onDateSelected(activeDate, requestCode, icId);
        }
    }
}
