/*
 * Copyright (C) 2012-2014 Jaspersoft Corporation. All rights reserved.
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.JsServerProfile;

import roboguice.fragment.RoboDialogFragment;

/**
 * @author Tom Koptel
 * @since 1.9
 */
public class PasswordDialogFragment extends RoboDialogFragment {

    private static final String TAG = PasswordDialogFragment.class.getSimpleName();

    @Inject
    private JsRestClient mJsRestClient;

    private EditText mPasswordEdit;
    private TextView mOrganizationText;
    private TextView mProfileNameText;
    private TextView mUserNameText;
    private View mOrganizationTableRow;

    //---------------------------------------------------------------------
    // Static methods
    //---------------------------------------------------------------------

    public static void show(FragmentManager fm) {
        PasswordDialogFragment dialogFragment = (PasswordDialogFragment)
                fm.findFragmentByTag(TAG);
        if (dialogFragment == null) {
            dialogFragment = new PasswordDialogFragment();
            dialogFragment.show(fm, TAG);
        }
    }

    //---------------------------------------------------------------------
    // Public methods
    //---------------------------------------------------------------------

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View view = inflater.inflate(R.layout.ask_pwd_dialog_layout, null);
        mPasswordEdit = (EditText) view.findViewById(R.id.dialogPasswordEdit);
        mOrganizationText = (TextView) view.findViewById(R.id.dialogOrganizationText);
        mProfileNameText = (TextView) view.findViewById(R.id.dialogProfileNameText);
        mUserNameText = (TextView) view.findViewById(R.id.dialogUsernameText);
        mOrganizationTableRow = view.findViewById(R.id.dialogOrganizationTableRow);
        builder.setView(view);

        builder.setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JsServerProfile currentProfile = mJsRestClient.getServerProfile();

                        String password = mPasswordEdit.getText().toString();
                        currentProfile.setPassword(password);

                        mJsRestClient.setServerProfile(currentProfile);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String alias = mJsRestClient.getServerProfile().getAlias();
        String org = mJsRestClient.getServerProfile().getOrganization();
        String usr = mJsRestClient.getServerProfile().getUsername();

        // Update username
        mProfileNameText.setText(alias);

        // Update organization
        if (TextUtils.isEmpty(org)) {
            mOrganizationTableRow.setVisibility(View.GONE);
        } else {
            mOrganizationText.setText(org);
            mOrganizationTableRow.setVisibility(View.VISIBLE);
        }

        // Update username
        mUserNameText.setText(usr);

        // Clear password
        mPasswordEdit.setText("");
    }

}
