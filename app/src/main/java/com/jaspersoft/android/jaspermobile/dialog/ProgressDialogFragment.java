/*
 * Copyright © 2014 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Mobile for Android.
 *
 * Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

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
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(loadingMessage));
        progressDialog.setOnShowListener(onShowListener);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (onCancelListener != null) {
            onCancelListener.onCancel(dialog);
        }
    }

    @Deprecated
    public static void show(FragmentManager fm,
                            OnCancelListener onCancelListener,
                            OnShowListener onShowListener) {
        ProgressDialogFragment dialogFragment = getInstance(fm);

        if (dialogFragment == null) {
            dialogFragment = ProgressDialogFragment_.builder()
                    .loadingMessage(R.string.r_pd_running_report_msg).build();
            dialogFragment.setOnCancelListener(onCancelListener);
            dialogFragment.setOnShowListener(onShowListener);
            dialogFragment.show(fm, TAG);
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
    }
}
