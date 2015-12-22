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

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.navigation.NavigationActivity_;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.util.account.JasperAccountManager;
import com.jaspersoft.android.jaspermobile.util.server.ServerInfo;
import com.jaspersoft.android.jaspermobile.util.server.ServerInfoProvider;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

import roboguice.fragment.RoboDialogFragment;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment
public class PasswordDialogFragment extends RoboDialogFragment implements DialogInterface.OnShowListener {

    private static final String TAG = PasswordDialogFragment.class.getSimpleName();

    private EditText etPassword;

    //---------------------------------------------------------------------
    // Static methods
    //---------------------------------------------------------------------

    public static void show(FragmentManager fm) {
        PasswordDialogFragment dialogFragment = (PasswordDialogFragment)
                fm.findFragmentByTag(TAG);
        if (dialogFragment == null) {
            dialogFragment = new PasswordDialogFragment_().builder().build();
            dialogFragment.show(fm, TAG);
        }
    }

    //---------------------------------------------------------------------
    // Public methods
    //---------------------------------------------------------------------

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View dialogView = inflater.inflate(R.layout.dialog_password, null);
        ServerInfoProvider serverInfoProvider = ServerInfo.newInstance(getActivity());
        String alias = serverInfoProvider.getAlias();
        String username = serverInfoProvider.getUsername();
        String organization = serverInfoProvider.getOrganization();

        ((TextView) dialogView.findViewById(R.id.tv_alias)).setText(alias);
        ((TextView) dialogView.findViewById(R.id.tv_username)).setText(username);

        TextView organizationField = (TextView) dialogView.findViewById(R.id.tv_organization);
        organizationField.setText(organization);
        if (TextUtils.isEmpty(organization)) {
            dialogView.findViewById(R.id.tv_organization_hint).setVisibility(View.GONE);
            organizationField.setVisibility(View.GONE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.h_ad_title_server_sign_in)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setView(dialogView)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(this);

        return dialog;
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        AlertDialog dialog = ((AlertDialog) dialogInterface);
        etPassword = (EditText) dialog.findViewById(R.id.et_new_password);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new PasswordDialogOkClickListener());
    }

    @Background
    protected void tryToLogin() {
        try {
            JasperAccountManager.get(getActivity()).getActiveAuthToken();
            loginSuccess();
        } catch (JasperAccountManager.TokenException e) {
            loginFailed(e);
        }
    }

    @UiThread
    protected void loginSuccess(){
        dismiss();
        ProgressDialogFragment.dismiss(getFragmentManager());

        int flags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK;
        NavigationActivity_.intent(getActivity()).flags(flags).start();
    }

    @UiThread
    protected void loginFailed(Exception e){
        RequestExceptionHandler.handle(e, getActivity());
        ProgressDialogFragment.dismiss(getFragmentManager());
    }

    //---------------------------------------------------------------------
    // Nested classes
    //---------------------------------------------------------------------

    private class PasswordDialogOkClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String password = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                etPassword.setError(getString(R.string.sp_error_field_required));
            } else {
                JasperAccountManager.get(getActivity()).updateActiveAccountPassword(password);
                ProgressDialogFragment.builder(getFragmentManager())
                        .setLoadingMessage(R.string.loading_msg)
                        .show();
                tryToLogin();
            }
        }
    }
}
