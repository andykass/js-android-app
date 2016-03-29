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

package com.jaspersoft.android.jaspermobile.activities.inputcontrols;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.adapters.FilterableAdapter;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.adapters.MultiSelectAvailableAdapter;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.adapters.MultiSelectSelectedAdapter;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportModule;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.util.ReportParamsStorage;
import com.jaspersoft.android.sdk.client.oxm.control.InputControl;
import com.jaspersoft.android.sdk.client.oxm.control.InputControlOption;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.2
 */
@EActivity(R.layout.activity_multi_select)
@OptionsMenu({R.menu.multi_select_menu, R.menu.search_menu})
public class MultiSelectActivity extends ToolbarActivity implements SearchView.OnQueryTextListener {

    public static final String SELECT_IC_ARG = "select_input_control_id";
    private final static int TAB_COUNT = 2;
    private final static int TAB_AVAILABLE = 0;
    private final static int TAB_SELECTED = 1;

    @Inject
    protected ReportParamsStorage paramsStorage;

    @Extra
    protected String reportUri;

    @Extra
    protected String inputControlId;

    @OptionsMenuItem(R.id.search)
    public MenuItem searchMenuItem;

    @ViewById(R.id.tlMultiSelect)
    protected TabLayout headerTab;

    private RecyclerView availableList;
    private RecyclerView selectedList;
    private TextView emptyTextSelected;
    private TextView emptyTextAvailable;

    private List<InputControlOption> mInputControlOptions;
    private String mInputControlLabel;
    private MultiSelectAvailableAdapter mAvailableAdapter;
    private MultiSelectSelectedAdapter mSelectedAdapter;
    private boolean isValueChanged;

    @AfterViews
    protected void init() {
       getProfileComponent()
                .plusControlsPage(
                        new ActivityModule(this),
                        new ReportModule(reportUri)
                )
                .inject(this);

        initInputControlOptions();
        initAdapters();
        initViews();

        getSupportActionBar().setTitle(mInputControlLabel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.ro_search));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isActiveSearchableTab = headerTab.getSelectedTabPosition() == TAB_AVAILABLE;
        searchMenuItem.setVisible(isActiveSearchableTab);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Intent dataIntent = new Intent();
        dataIntent.putExtra(SELECT_IC_ARG, inputControlId);

        int resultCode = isValueChanged ? Activity.RESULT_OK : Activity.RESULT_CANCELED;
        setResult(resultCode, dataIntent);

        super.onBackPressed();
    }

    @OptionsItem(R.id.selectAll)
    final void selectAll() {
        for (InputControlOption inputControlOption : mInputControlOptions) {
            inputControlOption.setSelected(true);
        }

        onItemsSelectionChange();
    }

    @OptionsItem(R.id.deselectAll)
    final void deselectAll() {
        for (InputControlOption inputControlOption : mInputControlOptions) {
            inputControlOption.setSelected(false);
        }

        onItemsSelectionChange();
    }

    @OptionsItem(R.id.inverse)
    final void selectInverse() {
        for (InputControlOption inputControlOption : mInputControlOptions) {
            inputControlOption.setSelected(!inputControlOption.isSelected());
        }

        onItemsSelectionChange();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAvailableAdapter.filter(newText);
        return true;
    }

    @Override
    protected String getScreenName() {
        return getString(R.string.ja_ms_ic_s);
    }

    private void initAdapters() {
        mAvailableAdapter = new MultiSelectAvailableAdapter(mInputControlOptions);
        mSelectedAdapter = new MultiSelectSelectedAdapter(mInputControlOptions);

        mAvailableAdapter.setFilterListener(new FilterableAdapter.FilterListener() {
            @Override
            public void onFilterDone() {
                onFilteringList();
            }
        });
        mAvailableAdapter.setItemSelectListener(new MultiSelectAvailableAdapter.ItemSelectListener() {
            @Override
            public void onItemSelected(int position) {
                InputControlOption inputControlOption = mInputControlOptions.get(position);
                inputControlOption.setSelected(!mInputControlOptions.get(position).isSelected());

                onItemSelectionChange(position);
            }
        });
        mSelectedAdapter.setItemSelectListener(new MultiSelectSelectedAdapter.ItemSelectedListener() {
            @Override
            public void onItemUnselected(int position) {
                InputControlOption inputControlOption = mInputControlOptions.get(position);
                inputControlOption.setSelected(false);

                onItemSelectionChange(position);
            }
        });
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.vpMultiSelect);
        MultiSelectViewPagerAdapter adapter = new MultiSelectViewPagerAdapter();
        viewPager.setAdapter(adapter);

        headerTab.setupWithViewPager(viewPager);
        headerTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                invalidateOptionsMenu();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initInputControlOptions() {
        List<InputControl> inputControls = paramsStorage.getInputControlHolder(reportUri).getInputControls();
        for (InputControl inputControl : inputControls) {
            if (inputControl.getId().equals(inputControlId)) {
                mInputControlLabel = inputControl.getLabel();
                mInputControlOptions = inputControl.getState().getOptions();
                break;
            }
        }
    }

    private void onItemSelectionChange(int position) {
        mAvailableAdapter.updateItem(position);
        mSelectedAdapter.notifySelectionChanged(mInputControlOptions.get(position).isSelected(), position);

        updateSelectedTabTitle();
        emptyTextSelected.setVisibility(mSelectedAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        isValueChanged = true;
    }

    private void onItemsSelectionChange() {
        mAvailableAdapter.notifyItemRangeChanged(0, mAvailableAdapter.getItemCount());
        mSelectedAdapter.notifySelectionsChanged(mInputControlOptions);

        updateSelectedTabTitle();
        searchMenuItem.collapseActionView();
        emptyTextSelected.setVisibility(mSelectedAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        isValueChanged = true;
    }

    private void onFilteringList() {
        updateAvailableTabTitle();
        emptyTextAvailable.setVisibility(mAvailableAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        availableList.scrollToPosition(0);
    }

    private void updateSelectedTabTitle() {
        TabLayout.Tab selectedTab = headerTab.getTabAt(TAB_SELECTED);
        if (selectedTab != null) {
            selectedTab.setText(getString(R.string.ro_ms_selected, mSelectedAdapter.getItemCount()));
        }
    }

    private void updateAvailableTabTitle() {
        TabLayout.Tab selectedTab = headerTab.getTabAt(TAB_AVAILABLE);
        if (selectedTab != null) {
            selectedTab.setText(getString(R.string.ro_ms_available, mInputControlOptions.size()));
        }
    }

    private class MultiSelectViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view.equals(o);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater layoutInflater = LayoutInflater.from(MultiSelectActivity.this);
            View selectView = layoutInflater.inflate(R.layout.view_select_ic_list, container, false);
            RecyclerView list = (RecyclerView) selectView.findViewById(R.id.inputControlsList);
            list.setLayoutManager(new LinearLayoutManager(MultiSelectActivity.this));
            list.setHasFixedSize(true);

            if (position == TAB_AVAILABLE) {
                availableList = list;
                availableList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                        int visibleItemCount = recyclerView.getChildCount();
                        int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                        int firstVisibleItem = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();

                        if (totalItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount) {
                            mAvailableAdapter.loadNextItems();
                        }
                    }
                });
                list.setAdapter(mAvailableAdapter);
                emptyTextAvailable = (TextView) selectView.findViewById(R.id.empty);
                emptyTextAvailable.setText(getString(R.string.r_search_nothing_to_display));
                emptyTextAvailable.setVisibility(mAvailableAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            } else {
                selectedList = list;
                list.setAdapter(mSelectedAdapter);
                emptyTextSelected = (TextView) selectView.findViewById(R.id.empty);
                emptyTextSelected.setText(getString(R.string.ro_no_items_selected));
                emptyTextSelected.setVisibility(mSelectedAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }

            container.addView(selectView, 0);
            return selectView;

        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView(((View) view));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == TAB_AVAILABLE) {
                return getString(R.string.ro_ms_available, mInputControlOptions.size());
            }
            return getString(R.string.ro_ms_selected, mSelectedAdapter.getItemCount());
        }
    }
}
