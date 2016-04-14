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

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jaspersoft.android.jaspermobile.R;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class AnnotationInputDialog extends AlertDialog implements DialogInterface.OnClickListener {
    private int mId;
    private String mValue;

    private EditText input;
    private OnAnnotationInputListener mOnEventListener;

    public AnnotationInputDialog(Context context) {
        super(context);
        init();
    }

    protected AnnotationInputDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    protected AnnotationInputDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewGroup dialog = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_annotation_input, null, false);
        input = (EditText) dialog.findViewById(R.id.annotationValue);
        input.setText(mValue);
        input.setSelection(mValue.length());

        setView(dialog);
        setButton(BUTTON_POSITIVE, getContext().getString(R.string.ok), this);
        setButton(BUTTON_NEGATIVE, getContext().getString(R.string.cancel), this);

        super.onCreate(savedInstanceState);
    }

    public void setId(int id) {
        mId = id;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public void setOnEventListener(OnAnnotationInputListener onEventListener) {
        mOnEventListener = onEventListener;
    }

    private void init() {
        mValue = "";
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mOnEventListener == null) return;

        if (which == BUTTON_POSITIVE) {
            mOnEventListener.onAnnotationEntered(mId, input.getText().toString());
        } else {
            mOnEventListener.onAnnotationCanceled(mId);
        }
    }

    public interface OnAnnotationInputListener {
        void onAnnotationEntered(int id, String text);

        void onAnnotationCanceled(int id);
    }
}
