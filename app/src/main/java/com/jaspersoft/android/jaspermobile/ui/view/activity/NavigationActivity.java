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

package com.jaspersoft.android.jaspermobile.ui.view.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.favorites.FavoritesPageFragment_;
import com.jaspersoft.android.jaspermobile.activities.library.LibraryPageFragment_;
import com.jaspersoft.android.jaspermobile.activities.recent.RecentPageFragment_;
import com.jaspersoft.android.jaspermobile.activities.repository.RepositoryPageFragment_;
import com.jaspersoft.android.jaspermobile.activities.settings.SettingsActivity_;
import com.jaspersoft.android.jaspermobile.activities.storage.SavedReportsFragment_;
import com.jaspersoft.android.jaspermobile.dialog.AboutDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.RateAppDialog_;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.internal.di.HasComponent;
import com.jaspersoft.android.jaspermobile.internal.di.components.NavigationActivityComponent;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.NavigationActivityModule;
import com.jaspersoft.android.jaspermobile.ui.contract.NavigationContract;
import com.jaspersoft.android.jaspermobile.ui.model.ProfileViewModel;
import com.jaspersoft.android.jaspermobile.ui.presenter.NavigationPresenter;
import com.jaspersoft.android.jaspermobile.ui.presenter.fragment.JobFragmentPresenter_;
import com.jaspersoft.android.jaspermobile.util.feedback.FeedbackSender;
import com.jaspersoft.android.jaspermobile.widget.NavigationPanelLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Ivan Gadzhega
 * @author Tom Koptel
 * @author Andrew Tivodar
 * @since 1.0
 */
@EActivity(R.layout.activity_navigation)
public class NavigationActivity extends CastActivity implements HasComponent<NavigationActivityComponent>, NavigationContract.View {

    private static final int NEW_ACCOUNT = 20;

    @ViewById(R.id.dl_navigation)
    protected DrawerLayout drawerLayout;
    @ViewById(R.id.npl_navigation_menu)
    protected NavigationPanelLayout navigationPanelLayout;

    @Inject
    protected Analytics mAnalytics;
    @Inject
    protected NavigationPresenter mNavigationPresenter;
    @Inject
    protected NavigationContract.ActionListener mActionListener;
    @Inject
    protected FeedbackSender mFeedbackSender;

    private ActionBarDrawerToggle mDrawerToggle;

    @Extra
    protected int currentSelection = R.id.vg_library;

    @InstanceState
    protected boolean customToolbarDisplayEnabled = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
        mNavigationPresenter.injectView(this);
        mActionListener.loadActiveProfile();
        mActionListener.loadProfiles();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();

        if (savedInstanceState == null) {
            navigateToCurrentSelection();
            RateAppDialog_.builder().build().show(this, getSupportFragmentManager());
        } else {
            setDisplayCustomToolbarEnable(customToolbarDisplayEnabled);
        }
    }

    @AfterViews
    final void setupNavigation() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_label);
        }
        setupNavDrawer();
        setupNavPanel();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        currentSelection = intent.getIntExtra(NavigationActivity_.CURRENT_SELECTION_EXTRA, R.id.vg_library);
        navigateToCurrentSelection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavigationPresenter.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNavigationPresenter.destroy();
    }

    @Override
    public NavigationActivityComponent getComponent() {
        return getProfileComponent()
                .plusNavigationPage(new NavigationActivityModule(this));
    }

    @Override
    public void toggleRecentlyViewedNavigation(boolean visibility) {
        View recentlyView = findViewById(R.id.vg_recent);
        recentlyView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showProfiles(List<ProfileViewModel> profiles) {
        navigationPanelLayout.loadProfiles(profiles);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = drawerLayout.isDrawerOpen(navigationPanelLayout);
        hideMenuItems(menu, !drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {
        super.onSupportActionModeStarted(mode);

        getToolbar().setVisibility(View.INVISIBLE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
        super.onSupportActionModeFinished(mode);

        getToolbar().setVisibility(View.VISIBLE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @OnActivityResult(NEW_ACCOUNT)
    final void newAccountAction(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String profileName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Profile profile = Profile.create(profileName);
            mActionListener.activateProfile(profile);
        }
    }

    @Override
    public void onBackPressed() {
        // Close left panel on back press
        if (drawerLayout.isDrawerOpen(navigationPanelLayout)) {
            drawerLayout.closeDrawer(navigationPanelLayout);
            return;
        }

        // Back for repository
        Fragment currentPageFragment = getSupportFragmentManager().findFragmentById(R.id.main_frame);
        if (currentPageFragment.isVisible()) {
            FragmentManager childFm = currentPageFragment.getChildFragmentManager();
            if (childFm.getBackStackEntryCount() > 0) {
                childFm.popBackStack();
                return;
            }
        }

        super.onBackPressed();
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void setupNavDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, getToolbar(),
                R.string.nd_drawer_open, R.string.nd_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                navigationPanelLayout.notifyPanelClosed();
                customToolbarDisplayEnabled = true;
                setDisplayCustomToolbarEnable(true);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                analytics.sendEvent(Analytics.EventCategory.MENU.getValue(), Analytics.EventAction.OPENED.getValue(), null);

                invalidateOptionsMenu();
                customToolbarDisplayEnabled = false;
                setDisplayCustomToolbarEnable(false);
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void setupNavPanel() {
        navigationPanelLayout.setAnalytics(mAnalytics);
        navigationPanelLayout.setListener(new NavigationPanelLayout.NavigationListener() {
            @Override
            public void onNavigate(int viewId) {
                handleNavigationAction(viewId);
                drawerLayout.closeDrawer(navigationPanelLayout);
            }

            @Override
            public void onActiveProfileChange(ProfileViewModel profile) {
                analytics.sendEvent(
                        Analytics.EventCategory.CATALOG.getValue(),
                        Analytics.EventAction.CLICKED.getValue(),
                        Analytics.EventLabel.SWITCH_ACCOUNT.getValue()
                );

                Profile domainProfile = Profile.create(profile.getLabel());
                mActionListener.activateProfile(domainProfile);
                drawerLayout.closeDrawer(navigationPanelLayout);
            }
        });
    }

    private void navigateToCurrentSelection() {
        navigationPanelLayout.setItemSelected(currentSelection);
    }

    private void handleNavigationAction(int viewId) {
        switch (viewId) {
            case R.id.vg_library:
                currentSelection = R.id.vg_library;
                commitContent(LibraryPageFragment_.builder().build());
                break;
            case R.id.vg_repository:
                currentSelection = R.id.vg_repository;
                commitContent(RepositoryPageFragment_.builder().build());
                break;
            case R.id.vg_recent:
                currentSelection = R.id.vg_recent;
                commitContent(RecentPageFragment_.builder().build());
                break;
            case R.id.vg_saved_items:
                currentSelection = R.id.vg_saved_items;
                commitContent(SavedReportsFragment_.builder().build());
                break;
            case R.id.vg_favorites:
                currentSelection = R.id.vg_favorites;
                commitContent(FavoritesPageFragment_.builder().build());
                break;
            case R.id.vg_jobs:
                currentSelection = R.id.vg_jobs;
                commitContent(JobFragmentPresenter_.builder().build());
                break;
            case R.id.vg_add_account:
                startActivityForResult(new Intent(this, AuthenticatorActivity.class), NEW_ACCOUNT);
                break;
            case R.id.vg_manage_accounts:
                String[] authorities = {getString(R.string.jasper_account_authority)};
                Intent manageAccIntent = new Intent(Settings.ACTION_SYNC_SETTINGS);
                manageAccIntent.putExtra(Settings.EXTRA_AUTHORITIES, authorities);
                try {
                    startActivity(manageAccIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, getString(R.string.wrong_action), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_settings:
                SettingsActivity_.intent(this).start();
                break;
            case R.id.tv_feedback:
                sendFeedback();
                break;
            case R.id.tv_about:
                AboutDialogFragment.createBuilder(this, getSupportFragmentManager()).show();
        }
    }

    private void commitContent(@NonNull Fragment directFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction
                .replace(R.id.main_frame, directFragment)
                .commit();
    }

    private void hideMenuItems(Menu menu, boolean visible) {
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(visible);
        }
    }

    private void sendFeedback() {
        boolean sendTaskWasInitiated = mFeedbackSender.initiate();
        if (!sendTaskWasInitiated) {
            Toast.makeText(this,
                    getString(R.string.sdr_t_no_app_available, "email"),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
