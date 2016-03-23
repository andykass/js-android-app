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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.adapters.FilterableAdapter;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.adapters.SingleSelectIcAdapter;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportModule;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.util.ReportParamsStorage;
import com.jaspersoft.android.sdk.client.oxm.control.InputControl;
import com.jaspersoft.android.sdk.client.oxm.control.InputControlOption;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.2
 */
@EActivity(R.layout.view_select_ic_list)
@OptionsMenu(R.menu.search_menu)
public class SingleSelectActivity extends ToolbarActivity implements SearchView.OnQueryTextListener {

    public static final String SELECT_IC_ARG = "select_input_control_id";

    @Inject
    protected ReportParamsStorage paramsStorage;

    @Extra
    protected String reportUri;

    @Extra
    protected String inputControlId;

    @Extra
    protected int listType;

    @OptionsMenuItem(R.id.search)
    public MenuItem searchMenuItem;

    @ViewById(R.id.inputControlsList)
    protected RecyclerView inputControlsList;

    @ViewById(R.id.empty)
    protected TextView emptyText;

    private SingleSelectIcAdapter mSingleSelectIcAdapter;
    private List<InputControlOption> mInputControlOptions;
    private String mInputControlLabel;
    private int mPreviousSelected;
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
        showInputControlOptions();

        emptyText.setText(getString(R.string.r_search_nothing_to_display));
        mPreviousSelected = getSelectedPosition();
        inputControlsList.scrollToPosition(mPreviousSelected);

        getSupportActionBar().setTitle(mInputControlLabel);
    }

    @Override
    public void onBackPressed() {
        Intent dataIntent = new Intent();
        dataIntent.putExtra(SELECT_IC_ARG, inputControlId);

        int resultCode = isValueChanged ? Activity.RESULT_OK : Activity.RESULT_CANCELED;
        setResult(resultCode, dataIntent);

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.ro_search));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mSingleSelectIcAdapter.filter(newText);
        return true;
    }

    @Override
    protected String getScreenName() {
        return getString(R.string.ja_ss_ic_s);
    }

    private void showInputControlOptions() {
        mSingleSelectIcAdapter = new SingleSelectIcAdapter(mInputControlOptions);
        inputControlsList.setLayoutManager(new LinearLayoutManager(this));
        inputControlsList.setAdapter(mSingleSelectIcAdapter);
        inputControlsList.setHasFixedSize(true);
        inputControlsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int firstVisibleItem = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();

                if (totalItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount) {
                    mSingleSelectIcAdapter.loadNextItems();
                }
            }
        });
        mSingleSelectIcAdapter.setFilterListener(new FilterableAdapter.FilterListener() {
            @Override
            public void onFilterDone() {
                onFilteringList();
            }
        });
        mSingleSelectIcAdapter.setItemSelectListener(new SingleSelectIcAdapter.ItemSelectListener() {
            @Override
            public void onItemSelected(int position) {
                mInputControlOptions.get(mPreviousSelected).setSelected(false);
                mSingleSelectIcAdapter.updateItem(mPreviousSelected);

                mInputControlOptions.get(position).setSelected(true);
                mSingleSelectIcAdapter.updateItem(position);

                mPreviousSelected = position;
                isValueChanged = true;
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

    private void onFilteringList(){
        emptyText.setVisibility(mSingleSelectIcAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        inputControlsList.scrollToPosition(0);
    }

    private int getSelectedPosition() {
        for (int i = 0; i < mInputControlOptions.size(); i++) {
            if (mInputControlOptions.get(i).isSelected()) {
                return i;
            }
        }
        return -1;
    }
}
