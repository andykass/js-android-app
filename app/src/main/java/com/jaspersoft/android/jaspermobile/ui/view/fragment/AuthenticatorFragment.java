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

package com.jaspersoft.android.jaspermobile.ui.view.fragment;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.domain.AppCredentials;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.domain.ProfileForm;
import com.jaspersoft.android.jaspermobile.internal.di.components.AuthenticatorActivityComponent;
import com.jaspersoft.android.jaspermobile.ui.contract.AuthenticationContract;
import com.jaspersoft.android.jaspermobile.ui.page.AuthPageState;
import com.jaspersoft.android.jaspermobile.ui.presenter.AuthenticationPresenter;
import com.jaspersoft.android.jaspermobile.ui.view.activity.AuthenticatorActivity;
import com.jaspersoft.android.jaspermobile.util.BaseUrlNormalizer;
import com.jaspersoft.android.jaspermobile.util.SimpleTextWatcher;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Tom Koptel
 * @since 2.0
 */
@EFragment(R.layout.add_account_layout)
public class AuthenticatorFragment extends BaseFragment implements AuthenticationContract.View {
    private static final String ALIAS = "Mobile Demo";
    private static final String SERVER_URL = "http://mobiledemo2.jaspersoft.com/jasperserver-pro";
    private static final String ORGANIZATION = "organization_1";
    private static final String USERNAME = "phoneuser";
    private static final String PASSWORD = "phoneuser";

    @ViewById
    protected EditText aliasEdit;
    @ViewById
    protected EditText usernameEdit;
    @ViewById
    protected EditText organizationEdit;
    @ViewById
    protected EditText serverUrlEdit;
    @ViewById
    protected EditText passwordEdit;
    @ViewById(R.id.tryDemoContainer)
    protected ViewGroup tryDemoLayout;

    @Inject
    AuthenticationPresenter mPresenter;
    @Inject
    AuthenticationContract.ActionListener mActionListener;

    @InstanceState
    AuthPageState mState = new AuthPageState();

    @Inject
    @Named("accountType")
    String accountType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (mPresenter == null) {
            injectComponents();
        }
    }

    private void injectComponents() {
        getComponent(AuthenticatorActivityComponent.class).inject(this);
        mPresenter.injectView(this);
    }

    @AfterViews
    void init() {
        showTryDemo(false);
        mActionListener.checkDemoAccountAvailability();

        setOnTypeErrorClearListeners();
    }

    @Click
    void addAccount() {
        String alias = aliasEdit.getText().toString();
        String serverUrl = serverUrlEdit.getText().toString();
        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String organization = organizationEdit.getText().toString();
        saveProfile(alias, serverUrl, username, password, organization);
    }

    @Click
    void tryDemo() {
        saveProfile(ALIAS, SERVER_URL, USERNAME, PASSWORD, ORGANIZATION);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.resume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.pause();
        }
    }

    @Override
    public void showLoading() {
        ProgressDialogFragment.builder(getFragmentManager())
                .setLoadingMessage(R.string.account_add)
                .show();
    }

    @Override
    public void hideLoading() {
        ProgressDialogFragment.dismiss(getFragmentManager());
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showNotification(String message) {
    }

    @Override
    public void hideError() {
    }

    @Override
    public void showAliasDuplicateError() {
        aliasEdit.setError(getString(R.string.sp_error_duplicate_alias));
        aliasEdit.requestFocus();
    }

    @Override
    public void showAliasReservedError() {
        aliasEdit.setError(getString(R.string.sp_error_reserved_alias));
        aliasEdit.requestFocus();
    }

    @Override
    public void showAliasRequiredError() {
        aliasEdit.setError(getString(R.string.sp_error_field_required));
        aliasEdit.requestFocus();
    }

    @Override
    public void showServerUrlFormatError() {
        serverUrlEdit.setError(getString(R.string.sp_error_url_not_valid));
        serverUrlEdit.requestFocus();
    }

    @Override
    public void showServerUrlRequiredError() {
        serverUrlEdit.setError(getString(R.string.sp_error_field_required));
        serverUrlEdit.requestFocus();
    }

    @Override
    public void showUsernameRequiredError() {
        usernameEdit.setError(getString(R.string.sp_error_field_required));
        usernameEdit.requestFocus();
    }

    @Override
    public void showPasswordRequiredError() {
        passwordEdit.setError(getString(R.string.sp_error_field_required));
        passwordEdit.requestFocus();
    }

    @Override
    public void showServerVersionNotSupported() {
        showError(getString(R.string.r_error_server_not_supported));
    }

    @Override
    public void showFailedToAddProfile(String message) {
        showError(getString(R.string.failure_add_account, message));
    }

    @Override
    public void navigateToApp(Profile profile) {
        Bundle data = new Bundle();
        data.putString(AccountManager.KEY_ACCOUNT_NAME, profile.getKey());
        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);

        Intent resultIntent = new Intent();
        resultIntent.putExtras(data);

        getAccountAuthenticatorActivity().setAccountAuthenticatorResult(data);
        getActivity().setResult(Activity.RESULT_OK, resultIntent);
        getActivity().finish();
    }

    @Override
    public void showTryDemo(boolean visible) {
        tryDemoLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public AuthPageState getState() {
        return mState;
    }

    private AuthenticatorActivity getAccountAuthenticatorActivity() {
        if (getActivity() instanceof AuthenticatorActivity) {
            return (AuthenticatorActivity) getActivity();
        } else {
            throw new IllegalStateException("Fragment can only be consumed " +
                    "within com.jaspersoft.android.jaspermobile.activities.auth.AuthenticatorActivity");
        }
    }

    /**
     * Hide error on text change.
     * Paste text on Android 4.2 will not cause clearing error on editText.
     */
    private void setOnTypeErrorClearListeners(){
        aliasEdit.addTextChangedListener(new ErrorTextWatcher(aliasEdit));
        serverUrlEdit.addTextChangedListener(new ErrorTextWatcher(serverUrlEdit));
        usernameEdit.addTextChangedListener(new ErrorTextWatcher(usernameEdit));
        passwordEdit.addTextChangedListener(new ErrorTextWatcher(passwordEdit));
    }

    private void saveProfile(
            String alias,
            String serverUrl,
            String username,
            String password,
            String organization
    ) {
        AppCredentials credentials = AppCredentials.builder()
                .setUsername(username)
                .setPassword(password)
                .setOrganization(organization)
                .create();
        ProfileForm profileForm = new ProfileForm.Builder()
                .setAlias(alias)
                .setBaseUrl(BaseUrlNormalizer.normalize(serverUrl))
                .setCredentials(credentials)
                .build();
        mPresenter.saveProfile(profileForm);
    }

    private class ErrorTextWatcher extends SimpleTextWatcher {

        private EditText editText;

        public ErrorTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            editText.setError(null);
        }
    }
}
