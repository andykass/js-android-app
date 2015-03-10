package com.jaspersoft.android.jaspermobile.activities.navigation;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.auth.AuthenticatorActivity;
import com.jaspersoft.android.jaspermobile.activities.favorites.FavoritesPageFragment_;
import com.jaspersoft.android.jaspermobile.activities.repository.LibraryFragment_;
import com.jaspersoft.android.jaspermobile.activities.repository.RepositoryFragment_;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboToolbarActivity;
import com.jaspersoft.android.jaspermobile.activities.settings.SettingsActivity_;
import com.jaspersoft.android.jaspermobile.activities.storage.SavedReportsFragment_;
import com.jaspersoft.android.jaspermobile.widget.NavigationPanelLayout;
import com.jaspersoft.android.retrofit.sdk.account.JasperAccountManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * @author Ivan Gadzhega
 * @author Tom Koptel
 * @author Andrew Tivodar
 * @since 1.0
 */
@EActivity(R.layout.activity_navigation)
public class NavigationActivity extends RoboToolbarActivity {

    private static final int NEW_ACCOUNT = 20;

    @ViewById(R.id.dl_navigation)
    protected DrawerLayout drawerLayout;
    @ViewById(R.id.npl_navigation_menu)
    protected NavigationPanelLayout navigationPanelLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Extra
    protected int currentSelection = R.id.vg_library;

    @AfterViews
    final void setupNavigation() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_label);
        }
        setupNavDrawer();
        setupNavPanel();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();

        if (savedInstanceState == null) {
            navigateToCurrentSelection();
        }
    }

    @Override
    protected void onAccountsChanged() {
        navigationPanelLayout.notifyAccountChange();
    }

    @Override
    protected void onActiveAccountChanged() {
        navigateToCurrentSelection();
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

    @OnActivityResult(NEW_ACCOUNT)
    final void newAccountAction(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            onActiveAccountChanged();
        }
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
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void setupNavPanel() {
        navigationPanelLayout.setListener(new NavigationPanelLayout.NavigationListener() {
            @Override
            public void onNavigate(int viewId) {
                handleNavigationAction(viewId);
                drawerLayout.closeDrawer(navigationPanelLayout);
            }

            @Override
            public void onProfileChange(Account account) {
                activateAccount(account);
            }
        });
    }

    private void navigateToCurrentSelection() {
        handleNavigationAction(currentSelection);
        navigationPanelLayout.setItemSelected(currentSelection);
    }

    private void handleNavigationAction(int viewId) {
        switch (viewId) {
            case R.id.vg_library:
                currentSelection = R.id.vg_library;
                commitContent(LibraryFragment_.builder().build());
                break;
            case R.id.vg_repository:
                currentSelection = R.id.vg_repository;
                commitContent(RepositoryFragment_.builder().build());
                break;
            case R.id.vg_saved_items:
                currentSelection = R.id.vg_saved_items;
                commitContent(SavedReportsFragment_.builder().build());
                break;
            case R.id.vg_favorites:
                currentSelection = R.id.vg_favorites;
                commitContent(FavoritesPageFragment_.builder().build());
                break;
            case R.id.vg_add_account:
                startActivityForResult(new Intent(this, AuthenticatorActivity.class), NEW_ACCOUNT);
                break;
            case R.id.vg_manage_accounts:
                String[] authorities = {getString(R.string.jasper_account_authority)};
                Intent manageAccIntent = new Intent(Settings.ACTION_SYNC_SETTINGS);
                manageAccIntent.putExtra(Settings.EXTRA_AUTHORITIES, authorities);
                startActivity(manageAccIntent);
                break;
            case R.id.tv_settings:
                SettingsActivity_.intent(this).start();
                break;
            case R.id.tv_feedback:
                new FeedBackDialogFragment().show(getSupportFragmentManager(), FeedBackDialogFragment.class.getSimpleName());
                break;
            case R.id.tv_about:
                new AboutDialogFragment().show(getSupportFragmentManager(), AboutDialogFragment.class.getSimpleName());
        }
    }

    private void activateAccount(@NonNull Account account) {
        JasperAccountManager.get(this).activateAccount(account);

        onActiveAccountChanged();
        onAccountsChanged();

        drawerLayout.closeDrawer(navigationPanelLayout);
    }

    private void commitContent(@NonNull Fragment directFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    transaction.remove(fragment);
                }
            }
        }
        transaction
                .replace(R.id.main_frame, directFragment)
                .commit();
    }

    private void hideMenuItems(Menu menu, boolean visible) {
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(visible);
        }
    }

}
