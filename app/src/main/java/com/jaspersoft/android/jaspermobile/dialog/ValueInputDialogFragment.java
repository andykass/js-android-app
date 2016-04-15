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
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.util.SimpleTextWatcher;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;

/**
 * @author Andrew Tivodar
 * @since 2.2
 */
@EFragment
public class ValueInputDialogFragment extends BaseDialogFragment implements DialogInterface.OnShowListener {

    private final static String LABEL_ARG = "label";
    private final static String VALUE_ARG = "value";
    private final static String REQUIRED_ARG = "required";

    private EditText icValue;

    private String mLabel;
    private String mValue;
    private boolean mRequired;
    AlertDialog mValueDialog;

    @SystemService
    protected InputMethodManager inputMethodManager;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View customLayout = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_ic_value, null);

        icValue = (EditText) customLayout.findViewById(R.id.icValue);

        icValue.setText("");
        icValue.append(mValue);
        icValue.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                icValue.setError(null);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(customLayout);
        builder.setTitle(mLabel);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, null);
        builder.setNegativeButton(R.string.cancel, null);

        mValueDialog = builder.create();
        mValueDialog.setOnShowListener(this);
        return mValueDialog;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        inputMethodManager.showSoftInput(icValue, 0);

        final Button positiveBnt = mValueDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveBnt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String newIcValue = ValueInputDialogFragment.this.icValue.getText().toString().trim();
                if (mRequired && newIcValue.isEmpty()) {
                    icValue.setError(getString(R.string.sr_error_field_is_empty));
                    return;
                }
                if (mDialogListener != null) {
                    ((ValueDialogCallback) mDialogListener).onTextValueEntered(requestCode, newIcValue);
                }

                mValueDialog.dismiss();
            }
        });
    }

    @Override
    protected Class<ValueDialogCallback> getDialogCallbackClass() {
        return ValueDialogCallback.class;
    }

    @Override
    protected void initDialogParams() {
        super.initDialogParams();

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(LABEL_ARG)) {
                mLabel = args.getString(LABEL_ARG);
            }
            if (args.containsKey(VALUE_ARG)) {
                mValue = args.getString(VALUE_ARG);
            }
            if (args.containsKey(REQUIRED_ARG)) {
                mRequired = args.getBoolean(REQUIRED_ARG);
            }
        }
    }

    public static ValueInputDialogFragmentBuilder createBuilder(FragmentManager fragmentManager) {
        return new ValueInputDialogFragmentBuilder(fragmentManager);
    }

    //---------------------------------------------------------------------
    // Dialog Builder
    //---------------------------------------------------------------------

    public static class ValueInputDialogFragmentBuilder extends BaseDialogFragmentBuilder<ValueInputDialogFragment> {

        public ValueInputDialogFragmentBuilder(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public ValueInputDialogFragmentBuilder setLabel(String label) {
            args.putString(LABEL_ARG, label);
            return this;
        }

        public ValueInputDialogFragmentBuilder setValue(String value) {
            args.putString(VALUE_ARG, value);
            return this;
        }

        public ValueInputDialogFragmentBuilder setRequired(boolean required) {
            args.putBoolean(REQUIRED_ARG, required);
            return this;
        }

        @Override
        protected ValueInputDialogFragment build() {
            return new ValueInputDialogFragment_();
        }
    }

    //---------------------------------------------------------------------
    // Dialog Callback
    //---------------------------------------------------------------------

    public interface ValueDialogCallback extends DialogClickListener {
        void onTextValueEntered(int requestCode, String name);
    }
}
