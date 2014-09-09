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

package com.jaspersoft.android.jaspermobile.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jaspersoft.android.jaspermobile.JasperMobileApplication;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.repository.BaseRepositoryActivity;
import com.jaspersoft.android.jaspermobile.activities.repository.FavoritesActivity;
import com.jaspersoft.android.jaspermobile.activities.repository.LibraryActivity_;
import com.jaspersoft.android.jaspermobile.activities.repository.SearchActivity;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboSpiceFragmentActivity;
import com.jaspersoft.android.jaspermobile.activities.storage.SavedReportsActivity;
import com.jaspersoft.android.jaspermobile.db.DatabaseProvider;
import com.jaspersoft.android.jaspermobile.dialog.AlertDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.PasswordDialogFragment;
import com.jaspersoft.android.jaspermobile.util.ConnectivityUtil;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.JsServerProfile;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * @author Ivan Gadzhega
 * @author Tom Koptel
 * @since 1.0
 */
@EActivity(R.layout.home_layout)
@OptionsMenu(R.menu.home_menu)
public class HomeActivity extends RoboSpiceFragmentActivity {

    // Special intent actions
    public static final String EDIT_SERVER_PROFILE_ACTION = "com.jaspersoft.android.jaspermobile.action.EDIT_SERVER_PROFILE";
    // Request Codes
    public static final int RC_UPDATE_SERVER_PROFILE = 20;
    public static final int RC_SWITCH_SERVER_PROFILE = 21;
    // Saved instance states
    private static final String FLAG_ANIMATE_STARTUP = "FLAG_ANIMATE_STARTUP";

    @Inject
    private JsRestClient mJsRestClient;
    @Inject
    private ConnectivityUtil mConnectivityUtil;
    @Inject
    private DatabaseProvider mDbProvider;
    @Inject
    private DecelerateInterpolator interpolator;
    @Inject
    @Named("animationSpeed")
    private int mAnimationSpeed;

    @ViewById
    protected ViewGroup table;
    @OptionsMenuItem
    protected MenuItem serverProfileMenuItem;
    @InstanceState
    protected boolean mAnimateStartup = true;

    private TextView mProfileNameText;
    private Bundle mSavedInstanceState;

    //---------------------------------------------------------------------
    // Static methods
    //---------------------------------------------------------------------

    public static void goHome(Context context) {
        // All of the other activities on top of it will be destroyed and this intent will be delivered
        // to the resumed instance of the activity (now on top), through onNewIntent()
        HomeActivity_.intent(context)
                .flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .start();
    }

    //---------------------------------------------------------------------
    // Public methods
    //---------------------------------------------------------------------

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        View actionView = serverProfileMenuItem.getActionView();
        mProfileNameText = (TextView) actionView.findViewById(R.id.profile_name);
        reloadProfileNameView();
        return super.onPrepareOptionsMenu(menu);
    }

    //---------------------------------------------------------------------
    // Annotated methods
    //---------------------------------------------------------------------

    @AfterViews
    final void init() {
        if (mAnimateStartup) {
            mAnimateStartup = false;
            animateLayout();
        }
    }

    @Click(R.id.home_item_repository)
    final void showRepository() {
        if (mConnectivityUtil.isConnected()) {
            LibraryActivity_.intent(this).start();
        } else {
            showNetworkAlert();
        }
    }

    @Click(R.id.home_item_library)
    final void showLibrary() {
        if (mConnectivityUtil.isConnected()) {
            Intent searchIntent = new Intent(HomeActivity.this, SearchActivity.class);
            searchIntent.putExtra(BaseRepositoryActivity.EXTRA_BC_TITLE_SMALL, getString(R.string.h_library_label));
            searchIntent.putExtra(BaseRepositoryActivity.EXTRA_RESOURCE_URI, "/");
            ArrayList<String> types = new ArrayList<String>();
            types.add(ResourceLookup.ResourceType.reportUnit.toString());
            types.add(ResourceLookup.ResourceType.dashboard.toString());
            searchIntent.putExtra(SearchActivity.EXTRA_RESOURCE_TYPES, types);
            startActivity(searchIntent);
        } else {
            showNetworkAlert();
        }
    }

    @Click(R.id.home_item_favorites)
    final void showFavorites() {
        if (mConnectivityUtil.isConnected()) {
            Intent favoritesIntent = new Intent(this, FavoritesActivity.class);
            startActivity(favoritesIntent);
        } else {
            showNetworkAlert();
        }
    }

    @Click(R.id.home_item_saved_reports)
    final void showSavedItems() {
        if (mConnectivityUtil.isConnected()) {

            Intent savedReportsIntent = new Intent(this, SavedReportsActivity.class);
            startActivity(savedReportsIntent);
        } else {
            showNetworkAlert();
        }
    }

    @Click(R.id.home_item_settings)
    final void showSettings() {
        if (mConnectivityUtil.isConnected()) {

            // Launch the settings activity
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else {
            showNetworkAlert();
        }
    }

    @Click(R.id.home_item_servers)
    final void showServerProfiles() {
        if (mConnectivityUtil.isConnected()) {
            // Launch activity to switch the server profile
            Intent intent = new Intent(this, ServerProfilesManagerActivity.class);
            startActivityForResult(intent, RC_SWITCH_SERVER_PROFILE);
        } else {
            showNetworkAlert();
        }
    }

    @OnActivityResult(RC_UPDATE_SERVER_PROFILE)
    final void updateSeverProfile(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // get updated server profile id from result data
            Bundle extras = data.getExtras();
            long rowId = extras.getLong(ServerProfileActivity.EXTRA_SERVER_PROFILE_ID);

            // update current profile
            JasperMobileApplication.setCurrentServerProfile(mJsRestClient, mDbProvider, rowId);

            // check if the password is not specified
            if (mJsRestClient.getServerProfile().getPassword().length() == 0) {
                PasswordDialogFragment.show(getSupportFragmentManager());
            }

            // the feedback about an operation
            Toast.makeText(this, R.string.spm_profile_updated_toast, Toast.LENGTH_SHORT).show();
        }
    }

    @OnActivityResult(RC_SWITCH_SERVER_PROFILE)
    final void switchServerProfile(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // get selected server profile id from result data
            Bundle extras = data.getExtras();
            long rowId = extras.getLong(ServerProfileActivity.EXTRA_SERVER_PROFILE_ID);

            // put new server profile id to shared prefs
            SharedPreferences prefs = getSharedPreferences(JasperMobileApplication.PREFS_NAME, MODE_PRIVATE);
            // we need an Editor object to make preference changes.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(JasperMobileApplication.PREFS_CURRENT_SERVER_PROFILE_ID, rowId);
            editor.apply();

            JasperMobileApplication.setCurrentServerProfile(mJsRestClient, mDbProvider, rowId);

            // check if the password is not specified
            if (mJsRestClient.getServerProfile().getPassword().length() == 0) {
                PasswordDialogFragment.show(getSupportFragmentManager());
            }

            String profileName = mJsRestClient.getServerProfile().getAlias();
            mProfileNameText.setText(profileName);

            // the feedback about an operation
            String toastMsg = getString(R.string.h_server_switched_toast, profileName);
            Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
        }
    }

    //---------------------------------------------------------------------
    // Protected methods
    //---------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (EDIT_SERVER_PROFILE_ACTION.equals(intent.getAction())) {
            // Launch activity to edit current server profile
            Intent editIntent = new Intent();
            editIntent.setClass(this, ServerProfileActivity.class);
            editIntent.setAction(ServerProfileActivity.EDIT_SERVER_PROFILE_ACTION);
            editIntent.putExtra(ServerProfileActivity.EXTRA_SERVER_PROFILE_ID, mJsRestClient.getServerProfile().getId());
            startActivityForResult(editIntent, RC_UPDATE_SERVER_PROFILE);
        }
    }

    @Override
    protected void onDestroy() {
        // close any open database object
        if (mDbProvider != null) mDbProvider.close();
        super.onDestroy();
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void showNetworkAlert() {
        AlertDialogFragment.createBuilder(this, getSupportFragmentManager())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.h_ad_title_no_connection)
                .setMessage(R.string.h_ad_msg_no_connection)
                .show();
    }

    private void animateLayout() {
        // No sense in animating if no speed set up
        // '0' case possible while black box testing
        if (mAnimationSpeed > 0) {
            int childCount = table.getChildCount();
            int defaultDelay = 200;
            for (int i = 0; i < childCount; i++) {
                View child = table.getChildAt(i);
                animateRow(child, defaultDelay);
                defaultDelay += 100;
            }
        }
    }

    private void animateRow(final View view, int delay) {
        if (view == null) return;
        view.setTranslationX(0.0F);
        view.setAlpha(0);
        view.setRotationX(45.0F);
        view.setScaleX(0.7F);
        view.setScaleY(0.55F);
        view.animate()
                .rotationX(0.0F).rotationY(0.0F)
                .translationX(0).translationY(0)
                .scaleX(1.0F).scaleY(1.0F)
                .setInterpolator(interpolator).alpha(1)
                .setDuration(mAnimationSpeed).setStartDelay(delay)
                .start();
    }

    private void reloadProfileNameView() {
        JsServerProfile serverProfile = mJsRestClient.getServerProfile();
        if (serverProfile == null) {
            // Launch activity to select the server profile
            Intent intent = new Intent(this, ServerProfilesManagerActivity.class);
            startActivityForResult(intent, RC_SWITCH_SERVER_PROFILE);
        } else {
            mProfileNameText.setText(serverProfile.getAlias());

            // savedInstanceState is null on first start, non-null on restart
            boolean isPasswordMissingOnFirstLaunch =
                    (mSavedInstanceState == null && TextUtils.isEmpty(serverProfile.getPassword()));

            if (isPasswordMissingOnFirstLaunch) {
                PasswordDialogFragment.show(getSupportFragmentManager());
            }
        }
    }

}