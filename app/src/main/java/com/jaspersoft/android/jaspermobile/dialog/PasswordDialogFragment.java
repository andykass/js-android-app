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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.AppCredentials;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.domain.ProfileForm;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.interactor.profile.CheckPasswordUseCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.profile.GetCurrentProfileFormUseCase;
import com.jaspersoft.android.jaspermobile.ui.view.activity.NavigationActivity_;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.ComponentProviderDelegate;
import com.jaspersoft.android.sdk.service.exception.ServiceException;
import com.jaspersoft.android.sdk.service.exception.StatusCodes;

import org.androidannotations.annotations.EFragment;

import javax.inject.Inject;

import rx.Subscriber;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment
public class PasswordDialogFragment extends DialogFragment implements DialogInterface.OnShowListener {

    private static final String TAG = PasswordDialogFragment.class.getSimpleName();

    private EditText passwordField;
    private View dialogView;
    private Toast mToast;

    @Inject
    GetCurrentProfileFormUseCase mGetCurrentProfileFormUseCase;
    @Inject
    CheckPasswordUseCase mCheckPasswordUseCase;

    //---------------------------------------------------------------------
    // Static methods
    //---------------------------------------------------------------------

    public static void show(FragmentManager fm) {
        PasswordDialogFragment dialogFragment = (PasswordDialogFragment)
                fm.findFragmentByTag(TAG);
        if (dialogFragment == null) {
            dialogFragment = PasswordDialogFragment_.builder().build();
            dialogFragment.show(fm, TAG);
        }
    }

    //---------------------------------------------------------------------
    // Public methods
    //---------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ComponentProviderDelegate.INSTANCE
                .getBaseActivityComponent(getActivity())
                .inject(this);

        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_LONG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        dialogView = inflater.inflate(R.layout.dialog_password, null);
        mGetCurrentProfileFormUseCase.execute(new SimpleSubscriber<ProfileForm>() {
            @Override
            public void onError(Throwable e) {
                showError(e.getLocalizedMessage());
            }

            @Override
            public void onNext(ProfileForm form) {
                populateForm(form);
            }
        });

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

    private void populateForm(ProfileForm formData) {
        Profile profile = formData.getProfile();
        AppCredentials credentials = formData.getCredentials();

        String alias = profile.getKey();
        String username = credentials.getUsername();
        String organization = credentials.getOrganization();

        ((TextView) dialogView.findViewById(R.id.tv_alias)).setText(alias);
        ((TextView) dialogView.findViewById(R.id.tv_username)).setText(username);

        TextView organizationField = (TextView) dialogView.findViewById(R.id.tv_organization);
        organizationField.setText(organization);

        if (TextUtils.isEmpty(organization)) {
            dialogView.findViewById(R.id.tv_organization_hint).setVisibility(View.GONE);
            organizationField.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mToast != null) {
            mToast.cancel();
        }
        mGetCurrentProfileFormUseCase.unsubscribe();
        mCheckPasswordUseCase.unsubscribe();
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        AlertDialog dialog = ((AlertDialog) dialogInterface);
        passwordField = (EditText) dialog.findViewById(R.id.et_new_password);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new PasswordDialogOkClickListener());
    }


    private void tryToLogin(String password) {
        mCheckPasswordUseCase.execute(password, new Subscriber<Void>() {
            @Override
            public void onStart() {
                showLoader();
            }

            @Override
            public void onCompleted() {
                hideLoader();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof ServiceException) {
                    ServiceException serviceException = (ServiceException) e;
                    int code = serviceException.code();
                    if (code == StatusCodes.AUTHORIZATION_ERROR) {
                        showError(getString(R.string.r_error_incorrect_credentials));
                    }
                } else {
                    showError(e.getLocalizedMessage());
                }
                hideLoader();
            }

            @Override
            public void onNext(Void item) {
                dismiss();

                Intent restartIntent = NavigationActivity_.intent(getActivity()).get();
                restartIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                getActivity().startActivity(restartIntent);
            }
        });
    }

    private void showLoader() {
        ProgressDialogFragment.builder(getFragmentManager())
                .setLoadingMessage(R.string.loading_msg)
                .show();
    }

    private void hideLoader() {
        ProgressDialogFragment.dismiss(getFragmentManager());
    }

    private void showError(String message) {
        mToast.setText(message);
        mToast.show();
    }

    //---------------------------------------------------------------------
    // Nested classes
    //---------------------------------------------------------------------

    private class PasswordDialogOkClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String password = passwordField.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                passwordField.setError(getString(R.string.sp_error_field_required));
            } else {
                tryToLogin(password);
            }
        }
    }
}