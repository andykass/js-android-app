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

package com.jaspersoft.android.jaspermobile.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;

import javax.inject.Inject;

import static com.jaspersoft.android.jaspermobile.util.ViewType.GRID;
import static com.jaspersoft.android.jaspermobile.util.ViewType.LIST;

/**
 * @author Tom Koptel
 * @since 1.9
 */
public abstract class ControllerFragment extends BaseFragment {
    protected final static String PREF_TAG_KEY = "prefTag";

    private MenuItem switchLayoutMenuItem;
    protected ControllerPref controllerPref;
    private int switchMenuIcon;

    @Inject
    protected Analytics analytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getBaseActivityComponent().inject(this);
        restoreSavedInstanceState(savedInstanceState);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.switch_menu, menu);
        switchLayoutMenuItem = menu.findItem(R.id.switchLayout);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        toggleSwitcher();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("switchMenuIcon", switchMenuIcon);
    }

    private void restoreSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        switchMenuIcon = savedInstanceState.getInt("switchMenuIcon");
    }

    @Override
    public void onStart() {
        super.onStart();
        initControllerPref();
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean switchButtonInitialized = switchMenuIcon != 0;
        boolean switchViewWasAltered = switchMenuIcon != getToggleIcon();

        if (switchButtonInitialized && switchViewWasAltered) {
            switchLayout();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = super.onOptionsItemSelected(item);
        if (handled) {
            return true;
        }
        int itemId_ = item.getItemId();
        if (itemId_ == R.id.switchLayout) {
            togglePref();
            switchLayout();

            analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.CHANGED_VIEW_TYPE.getValue(),  ViewType.valueOf(controllerPref).name());
            return true;
        }
        return false;
    }

    private void togglePref() {
        controllerPref.viewType()
                .put(getViewType() == LIST ? GRID.toString() : LIST.toString());
    }

    private void switchLayout() {
        toggleSwitcher();
        commitContentFragment();
        getActivity().supportInvalidateOptionsMenu();
    }

    private void toggleSwitcher() {
        switchMenuIcon = getToggleIcon();
        if (switchLayoutMenuItem != null) {
            switchLayoutMenuItem.setIcon(switchMenuIcon);
        }
    }

    private int getToggleIcon() {
        return getViewType() == LIST ? R.drawable.ic_menu_module : R.drawable.ic_menu_list;
    }

    protected void commitContentFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.content_frame, getContentFragment(), getContentFragmentTag())
                .commit();
    }

    protected ViewType getViewType() {
        if (controllerPref == null) {
            initControllerPref();
        }
        return ViewType.valueOf(controllerPref);
    }


    private void initControllerPref() {
        String prefTag = getArguments().getString(PREF_TAG_KEY);
        if (TextUtils.isEmpty(prefTag)) {
            throw new IllegalStateException("Missing preference key for switch component");
        }
        controllerPref = new ControllerPref(getActivity(), prefTag);
    }

    protected abstract Fragment getContentFragment();

    protected abstract String getContentFragmentTag();

}
