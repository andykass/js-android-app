/*
 * Copyright (C) 2012-2013 Jaspersoft Corporation. All rights reserved.
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

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.octo.android.robospice.persistence.DurationInMillis;

import roboguice.activity.RoboPreferenceActivity;

import static android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/**
 * @author Ivan Gadzhega
 * @since 1.5
 */
public class SettingsActivity extends RoboPreferenceActivity implements OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_REPO_CACHE_ENABLED = "pref_repo_cache_enabled";
    public static final String KEY_PREF_REPO_CACHE_EXPIRATION = "pref_repo_cache_expiration";
    public static final String KEY_PREF_CONNECT_TIMEOUT = "pref_connect_timeout";
    public static final String KEY_PREF_READ_TIMEOUT = "pref_read_timeout";

    public static final boolean DEFAULT_REPO_CACHE_ENABLED = true;
    public static final String DEFAULT_REPO_CACHE_EXPIRATION = "48";
    public static final String DEFAULT_CONNECT_TIMEOUT = "15";
    public static final String DEFAULT_READ_TIMEOUT = "120";

    private SharedPreferences sharedPreferences;
    private EditTextPreference repoCacheExpirationPref;
    private EditTextPreference connectTimeoutPref;
    private EditTextPreference readTimeoutPref;

    @Inject
    private JsRestClient jsRestClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        // update title
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.st_title);
        // use the App Icon for Navigation
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // init shared preferences
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        // repository cache
        repoCacheExpirationPref = (EditTextPreference) getPreferenceScreen().findPreference(KEY_PREF_REPO_CACHE_EXPIRATION);
        // timeouts
        connectTimeoutPref = (EditTextPreference) getPreferenceScreen().findPreference(KEY_PREF_CONNECT_TIMEOUT);
        readTimeoutPref = (EditTextPreference) getPreferenceScreen().findPreference(KEY_PREF_READ_TIMEOUT);
        // init summaries for all preferences
        updatePreferenceSummary(KEY_PREF_REPO_CACHE_EXPIRATION);
        updatePreferenceSummary(KEY_PREF_CONNECT_TIMEOUT);
        updatePreferenceSummary(KEY_PREF_READ_TIMEOUT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                HomeActivity.goHome(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        validatePreferenceValue(key);
        updatePreferenceSummary(key);
        updateDependentObjects(key);
    }

    public static long getRepoCacheExpirationValue(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean repoCacheEnabled = preferences.getBoolean(KEY_PREF_REPO_CACHE_ENABLED, DEFAULT_REPO_CACHE_ENABLED);
        if (repoCacheEnabled) {
            String value = preferences.getString(KEY_PREF_REPO_CACHE_EXPIRATION, DEFAULT_REPO_CACHE_EXPIRATION);
            return Integer.parseInt(value) * DurationInMillis.ONE_HOUR;
        } else {
            return -1;
        }
    }

    public static int getConnectTimeoutValue(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = preferences.getString(KEY_PREF_CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
        return Integer.parseInt(value);
    }

    public static int getReadTimeoutValue(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = preferences.getString(KEY_PREF_READ_TIMEOUT, DEFAULT_READ_TIMEOUT);
        return Integer.parseInt(value);
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void validatePreferenceValue(String key) {
        if (key.equals(KEY_PREF_REPO_CACHE_EXPIRATION)) {
            validatePreferenceValue(key, DEFAULT_REPO_CACHE_EXPIRATION);
        } else if (key.equals(KEY_PREF_CONNECT_TIMEOUT)) {
            validatePreferenceValue(key, DEFAULT_CONNECT_TIMEOUT);
        } else if (key.equals(KEY_PREF_READ_TIMEOUT)) {
            validatePreferenceValue(key, DEFAULT_READ_TIMEOUT);
        }
    }

    private void validatePreferenceValue(String key, String defValue) {
        if (sharedPreferences.getString(key, defValue).length() == 0){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, defValue);
            editor.commit();
        }
    }

    private void updatePreferenceSummary(String key) {
        if (key.equals(KEY_PREF_REPO_CACHE_EXPIRATION)) {
            String value = sharedPreferences.getString(KEY_PREF_REPO_CACHE_EXPIRATION, DEFAULT_REPO_CACHE_EXPIRATION);
            String summary = getString(R.string.st_summary_h, value);
            repoCacheExpirationPref.setSummary(summary);
        } else if (key.equals(KEY_PREF_CONNECT_TIMEOUT)) {
            String value = sharedPreferences.getString(KEY_PREF_CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
            String summary = getString(R.string.st_summary_sec, value);
            connectTimeoutPref.setSummary(summary);
        } else if (key.equals(KEY_PREF_READ_TIMEOUT)) {
            String value = sharedPreferences.getString(KEY_PREF_READ_TIMEOUT, DEFAULT_READ_TIMEOUT);
            String summary = getString(R.string.st_summary_sec, value);
            readTimeoutPref.setSummary(summary);
        }
    }

    private void updateDependentObjects(String key) {
        if (key.equals(KEY_PREF_CONNECT_TIMEOUT)) {
            int readTimeoutValue = getReadTimeoutValue(this);
            jsRestClient.setConnectTimeout(readTimeoutValue * 1000);
        } else if (key.equals(KEY_PREF_READ_TIMEOUT)) {
            int readTimeoutValue = getReadTimeoutValue(this);
            jsRestClient.setReadTimeout(readTimeoutValue * 1000);
        }
    }

}
