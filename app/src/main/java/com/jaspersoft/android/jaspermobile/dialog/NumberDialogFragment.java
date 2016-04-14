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
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jaspersoft.android.jaspermobile.R;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment
public class NumberDialogFragment extends BaseDialogFragment implements DialogInterface.OnShowListener {

    private final static String MAX_VALUE_ARG = "max_value";
    private final static String INIT_VALUE_ARG = "init_value";

    private int mMaxValue;
    private int mInitValue = Integer.MIN_VALUE;

    private EditText numberEditText;

    @SystemService
    protected InputMethodManager inputMethodManager;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        ViewGroup customView = (ViewGroup) layoutInflater
                .inflate(R.layout.page_dialog_layout,
                        (ViewGroup) getActivity().getWindow().getDecorView(), false);
        numberEditText = (EditText) customView.findViewById(R.id.customNumber);
        numberEditText.setFilters(new InputFilter[]{new InputFilterMinMax(1, mMaxValue)});

        if (mInitValue != Integer.MIN_VALUE) {
            numberEditText.setText(String.valueOf(mInitValue));
        }

        builder.setTitle(R.string.rv_select_page);
        builder.setView(customView);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.ok, null);

        Dialog pageDialog = builder.create();
        pageDialog.setOnShowListener(this);
        return pageDialog;
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        numberEditText.requestFocus();
        inputMethodManager.showSoftInput(numberEditText, 0);

        AlertDialog dialog = ((AlertDialog) getDialog());
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnPositiveCLickListener());
    }

    @Override
    protected Class<NumberDialogClickListener> getDialogCallbackClass() {
        return NumberDialogClickListener.class;
    }

    protected void initDialogParams() {
        super.initDialogParams();

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(MAX_VALUE_ARG)) {
                mMaxValue = args.getInt(MAX_VALUE_ARG);
            }
            if (args.containsKey(INIT_VALUE_ARG)) {
                mInitValue = args.getInt(INIT_VALUE_ARG);
            }
        }
    }

    public static PageDialogFragmentBuilder createBuilder(FragmentManager fragmentManager) {
        return new PageDialogFragmentBuilder(fragmentManager);
    }

    //---------------------------------------------------------------------
    // Dialog Builder
    //---------------------------------------------------------------------

    public static class PageDialogFragmentBuilder extends BaseDialogFragmentBuilder<NumberDialogFragment> {

        public PageDialogFragmentBuilder(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public PageDialogFragmentBuilder setValue(int value) {
            args.putInt(INIT_VALUE_ARG, value);
            return this;
        }


        public PageDialogFragmentBuilder setMaxValue(int maxValue) {
            args.putInt(MAX_VALUE_ARG, maxValue);
            return this;
        }

        @Override
        protected NumberDialogFragment build() {
            if (!args.containsKey(MAX_VALUE_ARG)) {
                args.putInt(MAX_VALUE_ARG, Integer.MAX_VALUE);
            }
            return new NumberDialogFragment_();
        }
    }

    //---------------------------------------------------------------------
    // Dialog Callback
    //---------------------------------------------------------------------

    public interface NumberDialogClickListener extends DialogClickListener {
        void onNumberSubmit(int number, int requestCode);
    }

    //---------------------------------------------------------------------
    // Nested classes
    //---------------------------------------------------------------------

    private static class InputFilterMinMax implements InputFilter {
        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) {
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

    private class OnPositiveCLickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (numberEditText.length() == 0) {
                numberEditText.setError(
                        getString(R.string.sp_error_field_required));
            } else {
                int page;
                try {
                    page = Integer.valueOf(numberEditText.getText().toString());
                } catch (NumberFormatException ex) {
                    page = Integer.MAX_VALUE;
                    numberEditText.setText(String.valueOf(page));
                }

                if (mDialogListener != null) {
                    ((NumberDialogClickListener) mDialogListener).onNumberSubmit(page, requestCode);
                }
                dismiss();
            }
        }
    }
}
