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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import static android.content.DialogInterface.OnCancelListener;
import static android.content.DialogInterface.OnShowListener;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment
public class ProgressDialogFragment extends DialogFragment {
    private static final String TAG = ProgressDialogFragment.class.getSimpleName();
    private DialogInterface.OnCancelListener onCancelListener;
    private DialogInterface.OnShowListener onShowListener;

    @FragmentArg
    int loadingMessage;

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public void setOnShowListener(OnShowListener onShowListener) {
        this.onShowListener = onShowListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View customLayout = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_progress, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        TextView message = (TextView) customLayout.findViewById(R.id.progressMessage);
        message.setText(getString(loadingMessage));
        builder.setView(customLayout);

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (onShowListener != null) {
                    onShowListener.onShow(dialog);
                }
            }
        });
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (onCancelListener != null) {
            onCancelListener.onCancel(dialog);
        }
    }

    public static void dismiss(FragmentManager fm) {
        ProgressDialogFragment dialogFragment = getInstance(fm);
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }

    public static boolean isVisible(FragmentManager fm) {
        return (getInstance(fm) != null);
    }

    public static ProgressDialogFragment getInstance(FragmentManager fm) {
        if (fm == null) return null;
        return (ProgressDialogFragment) fm.findFragmentByTag(TAG);
    }

    public static Builder builder(FragmentManager fm) {
        return new Builder(fm);
    }

    public static class Builder {
        private OnCancelListener onCancelListener;
        private OnShowListener onShowListener;
        private int loadingMessage;
        private final FragmentManager fm;

        public Builder(FragmentManager fm) {
            this.fm = fm;
            this.loadingMessage = R.string.r_pd_running_report_msg;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            this.onCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnShowListener(OnShowListener onShowListener) {
            this.onShowListener = onShowListener;
            return this;
        }

        public Builder setLoadingMessage(int loadingMessage) {
            this.loadingMessage = loadingMessage;
            return this;
        }

        public void show() {
            ProgressDialogFragment dialogFragment = getInstance(fm);

            if (dialogFragment == null) {
                dialogFragment = ProgressDialogFragment_.builder().loadingMessage(loadingMessage).build();
                dialogFragment.setOnCancelListener(onCancelListener);
                dialogFragment.setOnShowListener(onShowListener);
                dialogFragment.show(fm, TAG);
            }
        }

        public void display() {
            ProgressDialogFragment dialogFragment = ProgressDialogFragment_.builder().loadingMessage(loadingMessage).build();
            dialogFragment.setOnCancelListener(onCancelListener);
            dialogFragment.setOnShowListener(onShowListener);
            dialogFragment.show(fm, TAG);
        }
    }
}
