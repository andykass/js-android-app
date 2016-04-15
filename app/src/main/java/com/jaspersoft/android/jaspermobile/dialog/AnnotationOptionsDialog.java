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
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.jaspersoft.android.jaspermobile.R;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class AnnotationOptionsDialog extends AlertDialog implements DialogInterface.OnClickListener {
    private int mSize;
    private Boolean mBorder;

    private SeekBar sizeBar;
    private CheckBox borderSize;
    private OnAnnotationSizeListener mOnEventListener;

    public AnnotationOptionsDialog(Context context) {
        super(context);
    }

    protected AnnotationOptionsDialog(Context context, int theme) {
        super(context, theme);
    }

    protected AnnotationOptionsDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewGroup dialog = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_annotation_options, null, false);
        sizeBar = (SeekBar) dialog.findViewById(R.id.annotationSize);
        sizeBar.setProgress(mSize);
        borderSize = (CheckBox) dialog.findViewById(R.id.annotationBorder);
        if (mBorder == null) {
            borderSize.setVisibility(View.GONE);
        } else {
            borderSize.setVisibility(View.VISIBLE);
            borderSize.setChecked(mBorder);
        }

        setView(dialog);
        setButton(BUTTON_POSITIVE, getContext().getString(R.string.ok), this);
        setButton(BUTTON_NEGATIVE, getContext().getString(R.string.cancel), this);

        super.onCreate(savedInstanceState);
    }

    public void setSize(int size) {
        this.mSize = size;
    }

    public void setBorder(Boolean border) {
        mBorder = border;
    }

    public void setOnEventListener(OnAnnotationSizeListener onEventListener) {
        mOnEventListener = onEventListener;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mOnEventListener == null) return;

        if (which == BUTTON_POSITIVE) {
            if (mBorder == null) {
                mOnEventListener.onAnnotationOptionsSelected(sizeBar.getProgress());
            } else {
                mOnEventListener.onAnnotationOptionsSelected(sizeBar.getProgress(), borderSize.isChecked());
            }
        }
    }

    public interface OnAnnotationSizeListener {
        void onAnnotationOptionsSelected(int size);
        void onAnnotationOptionsSelected(int size, boolean needsBorder);
    }
}
