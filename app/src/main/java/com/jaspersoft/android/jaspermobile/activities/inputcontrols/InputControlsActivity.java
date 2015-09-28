/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.activities.inputcontrols;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.adapters.InputControlsAdapter;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.viewholders.ItemSpaceDecoration;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboSpiceActivity;
import com.jaspersoft.android.jaspermobile.dialog.DateDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.network.SimpleRequestListener;
import com.jaspersoft.android.jaspermobile.util.IcDateHelper;
import com.jaspersoft.android.jaspermobile.util.ReportParamsStorage;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.async.request.GetReportOptionValuesRequest;
import com.jaspersoft.android.sdk.client.async.request.ReportOptionsRequest;
import com.jaspersoft.android.sdk.client.async.request.cacheable.GetInputControlsValuesRequest;
import com.jaspersoft.android.sdk.client.async.request.cacheable.ValidateInputControlsValuesRequest;
import com.jaspersoft.android.sdk.client.oxm.control.InputControl;
import com.jaspersoft.android.sdk.client.oxm.control.InputControlOption;
import com.jaspersoft.android.sdk.client.oxm.control.InputControlState;
import com.jaspersoft.android.sdk.client.oxm.control.InputControlStatesList;
import com.jaspersoft.android.sdk.client.oxm.report.ReportParameter;
import com.jaspersoft.android.sdk.client.oxm.report.option.ReportOption;
import com.jaspersoft.android.sdk.client.oxm.report.option.ReportOptionResponse;
import com.octo.android.robospice.persistence.exception.SpiceException;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Gadzhega
 * @author Tom Koptel
 * @author Andrew Tivodar
 * @since 1.6
 */
@EActivity(R.layout.view_simple_list)
@OptionsMenu(R.menu.input_control_menu)
public class InputControlsActivity extends RoboSpiceActivity implements InputControlsAdapter.InputControlInteractionListener, DateDialogFragment.DateDialogClickListener {
    // Extras
    public static final int SELECT_IC_REQUEST_CODE = 521;
    public static final String RESULT_SAME_PARAMS = "ReportOptionsActivity.SAME_PARAMS";

    @Inject
    protected JsRestClient jsRestClient;
    @Inject
    protected ReportParamsStorage paramsStorage;

    @ViewById(R.id.btnApplyParams)
    protected FloatingActionButton applyParams;
    @ViewById(R.id.inputControlsList)
    protected RecyclerView inputControlsList;
    @ViewById(R.id.reportOptions)
    protected Spinner reportOptionsList;

    @Extra
    protected String reportUri;

    private List<InputControl> mInputControls;
    private List<ReportOption> mReportOptions;
    private InputControlsAdapter mAdapter;

    @AfterViews
    protected void init() {
        initToolbar();
        initReportOptions();
        initInputControls();

        showInputControls();
    }

    @Click(R.id.btnApplyParams)
    protected void applyParamsClick() {
        setApplyButtonState(true);
        ValidateInputControlsValuesRequest request = new ValidateInputControlsValuesRequest(jsRestClient, reportUri, mInputControls);
        getSpiceManager().execute(request, new ValidateInputControlsValuesListener());
    }

    @OnActivityResult(SELECT_IC_REQUEST_CODE)
    final void selectIcAction(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) return;

        if (data.hasExtra(SingleSelectActivity.SELECT_IC_ARG)) {
            String inputControlId = data.getStringExtra(SingleSelectActivity.SELECT_IC_ARG);
            InputControl selectInputControl = getInputControl(inputControlId);

            mAdapter.updateInputControl(selectInputControl);
            updateDependentControls(selectInputControl);
        }
    }

    @Override
    public void onBooleanStateChanged(InputControl inputControl, boolean newState) {
        inputControl.getState().setValue(String.valueOf(newState));
        mAdapter.updateInputControl(inputControl);
        updateDependentControls(inputControl);
    }

    @Override
    public void onValueTextChanged(InputControl inputControl, String newValue) {
        inputControl.getState().setValue(newValue);
        mAdapter.updateInputControl(inputControl);
        updateDependentControls(inputControl);
    }

    @Override
    public void onSingleSelectIcClicked(InputControl inputControl) {
        SingleSelectActivity_.intent(this)
                .reportUri(reportUri)
                .inputControlId(inputControl.getId())
                .startForResult(SELECT_IC_REQUEST_CODE);
    }

    @Override
    public void onMultiSelectIcClicked(InputControl inputControl) {
        MultiSelectActivity_.intent(this)
                .reportUri(reportUri)
                .inputControlId(inputControl.getId())
                .startForResult(SELECT_IC_REQUEST_CODE);
    }

    @Override
    public void onDateIcClicked(InputControl inputControl) {
        DateDialogFragment.createBuilder(getSupportFragmentManager())
                .setInputControlId(inputControl.getId())
                .setDate(IcDateHelper.convertToDate(inputControl))
                .setType(DateDialogFragment.DATE)
                .show();
    }

    @Override
    public void onTimeIcClicked(InputControl inputControl) {
        DateDialogFragment.createBuilder(getSupportFragmentManager())
                .setInputControlId(inputControl.getId())
                .setDate(IcDateHelper.convertToDate(inputControl))
                .setType(DateDialogFragment.TIME)
                .show();
    }

    @Override
    public void onDateClear(InputControl inputControl) {
        inputControl.getState().setValue("");
        mAdapter.updateInputControl(inputControl);
        updateDependentControls(inputControl);
    }

    @Override
    public void onDateSelected(String icId, Calendar date) {
        InputControl inputControl = getInputControl(icId);

        updateDateValue(inputControl, date);
        mAdapter.updateInputControl(inputControl);
        updateDependentControls(inputControl);
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void initToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.icToolbar));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_close);
        }
    }

    private void initReportOptions() {
        mReportOptions = paramsStorage.getInputControlHolder(reportUri).getReportOptions();
        if (mReportOptions.isEmpty()) {
            mReportOptions = new ArrayList<>();
            mReportOptions.add(0, new ReportOption(reportUri, reportUri, getString(R.string.ro_default)));

            ReportOptionsRequest runReportExecutionRequest = new ReportOptionsRequest(jsRestClient, reportUri);
            getSpiceManager().execute(runReportExecutionRequest, new GetReportOptionsListener());
            setProgressDialogState(true);
        } else {
            showReportOptions();
        }
    }

    private void initInputControls() {
        mInputControls = paramsStorage.getInputControlHolder(reportUri).getInputControls();
        updateInputControlsFromReportParams();
    }

    private void showInputControls() {
        mAdapter = new InputControlsAdapter(mInputControls);
        mAdapter.setInteractionListener(this);
        int dividerHeight = (int) getResources().getDimension(R.dimen.ic_divider_height);
        int topPadding = (int) getResources().getDimension(R.dimen.ic_top_padding);

        inputControlsList.addItemDecoration(new ItemSpaceDecoration(dividerHeight, topPadding));
        inputControlsList.setItemAnimator(null);
        inputControlsList.setLayoutManager(new LinearLayoutManager(this));
        inputControlsList.setAdapter(mAdapter);
    }

    private void showReportOptions() {
        // It's a hack to make spinner width as a selected item width
        ArrayAdapter<String> reportOptionsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getReportOptionsTitles()) {
            @Override
            public View getView(final int position, final View convertView,
                                final ViewGroup parent) {
                int selectedItemPosition = InputControlsActivity.this.reportOptionsList.getSelectedItemPosition();
                return super.getView(selectedItemPosition, convertView, parent);
            }
        };
        reportOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportOptionsList.setAdapter(reportOptionsAdapter);

        int selectedReportOptionPosition = paramsStorage.getInputControlHolder(reportUri).getCurrentReportOption();
        reportOptionsList.setSelection(selectedReportOptionPosition, false);
        reportOptionsList.setOnItemSelectedListener(new OnReportOptionSelectListener());
        reportOptionsList.setVisibility(View.VISIBLE);
    }

    private void onReportOptionSelected(int position) {
        ReportOption reportOption = mReportOptions.get(position);
        paramsStorage.getInputControlHolder(reportUri).setCurrentReportOption(position);

        setProgressDialogState(true);

        GetReportOptionValuesRequest request = new GetReportOptionValuesRequest(jsRestClient, reportOption.getUri());
        getSpiceManager().execute(request, new GetInputControlsValuesListener());
    }

    private void setProgressDialogState(boolean loading) {
        if (loading) {
            ProgressDialogFragment.builder(getSupportFragmentManager())
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .show();
        } else {
            if (ProgressDialogFragment.isVisible(getSupportFragmentManager())) {
                ProgressDialogFragment.dismiss(getSupportFragmentManager());
            }
        }
    }

    private void setApplyButtonState(boolean refreshing) {
//        if (refreshing) {
//            runReportAction.setActionView(R.layout.actionbar_indeterminate_progress);
//        } else {
//            runReportAction.setActionView(null);
//        }

        mAdapter.setListEnabled(!refreshing);
    }

    private InputControl getInputControl(String id) {
        for (InputControl inputControl : mInputControls) {
            if (inputControl.getId().equals(id)) {
                return inputControl;
            }
        }
        return null;
    }

    private boolean isNewParamsEqualOld(ArrayList<ReportParameter> newParams) {
        List<ReportParameter> oldParams = paramsStorage.getInputControlHolder(reportUri).getReportParams();

        if (oldParams.size() != newParams.size()) {
            return false;
        }

        for (int i = 0; i < oldParams.size(); i++) {
            if (!oldParams.get(i).getValues().equals(newParams.get(i).getValues())) return false;
        }

        return true;
    }

    private void updateDateValue(InputControl inputControl, Calendar newDate) {
        String newDateString = IcDateHelper.convertToString(inputControl, newDate);
        inputControl.getState().setValue(newDateString);
    }

    private void updateDependentControls(InputControl inputControl) {
        if (!inputControl.getSlaveDependencies().isEmpty()) {
            setProgressDialogState(true);
            GetInputControlsValuesRequest request = new GetInputControlsValuesRequest(jsRestClient, reportUri, mInputControls);
            getSpiceManager().execute(request, new GetInputControlsValuesListener());
        }
    }

    private void runReport() {
        Intent htmlViewer = new Intent();
        ArrayList<ReportParameter> parameters = initParametersUsingSelectedValues();
        if (isNewParamsEqualOld(parameters)) {
            htmlViewer.putExtra(RESULT_SAME_PARAMS, true);
        }
        paramsStorage.getInputControlHolder(reportUri).setReportParams(parameters);
        setResult(Activity.RESULT_OK, htmlViewer);
        finish();
    }

    private ArrayList<ReportParameter> initParametersUsingSelectedValues() {
        ArrayList<ReportParameter> parameters = new ArrayList<>();
        for (InputControl inputControl : mInputControls) {
            parameters.add(new ReportParameter(inputControl.getId(), inputControl.getSelectedValues()));
        }
        return parameters;
    }

    private void updateInputControls(List<InputControlState> stateList) {
        for (InputControlState inputControlState : stateList) {
            InputControl inputControl = getInputControl(inputControlState.getId());
            if (inputControl != null) {
                inputControl.setState(inputControlState);
            }
        }
        mAdapter.updateInputControlList(mInputControls);
    }

    private void updateInputControlsFromReportParams() {
        List<ReportParameter> reportParams = paramsStorage.getInputControlHolder(reportUri).getReportParams();

        Map<String, Set<String>> hashMap = new HashMap<String, Set<String>>(reportParams.size());
        for (ReportParameter reportParameter : reportParams) {
            hashMap.put(reportParameter.getName(), reportParameter.getValues());
        }

        for (InputControl inputControl : mInputControls) {
            updateInputControlState(hashMap, inputControl);
        }
    }

    private void updateInputControlState(Map<String, Set<String>> hashMap, InputControl inputControl) {
        InputControlState state = inputControl.getState();
        List<InputControlOption> options = state.getOptions();
        Set<String> valueSet = hashMap.get(state.getId());
        List<String> valueList = new ArrayList<String>();
        if (valueSet != null) {
            valueList.addAll(valueSet);
        }

        if (!valueList.isEmpty()) {
            switch (inputControl.getType()) {
                case bool:
                case singleValueText:
                case singleValueNumber:
                case singleValueTime:
                case singleValueDate:
                case singleValueDatetime:
                    state.setValue(valueList.get(0));
                    break;
                case multiSelect:
                case multiSelectCheckbox:
                case singleSelect:
                case singleSelectRadio:
                    for (InputControlOption option : options) {
                        option.setSelected(valueList.contains(option.getValue()));
                    }
                    break;
            }
        }
    }

    private String[] getReportOptionsTitles() {
        String[] reportOptionsTitles = new String[mReportOptions.size()];
        for (int i = 0; i < mReportOptions.size(); i++) {
            reportOptionsTitles[i] = mReportOptions.get(i).getLabel();
        }
        return reportOptionsTitles;
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    private class GetInputControlsValuesListener extends SimpleRequestListener<InputControlStatesList> {

        @Override
        protected Context getContext() {
            return InputControlsActivity.this;
        }

        @Override
        public void onRequestFailure(SpiceException exception) {
            super.onRequestFailure(exception);
            setProgressDialogState(false);
        }

        @Override
        public void onRequestSuccess(InputControlStatesList stateList) {
            updateInputControls(stateList.getInputControlStates());
            setProgressDialogState(false);
        }
    }

    private class ValidateInputControlsValuesListener extends SimpleRequestListener<InputControlStatesList> {

        @Override
        protected Context getContext() {
            return InputControlsActivity.this;
        }

        @Override
        public void onRequestFailure(SpiceException exception) {
            super.onRequestFailure(exception);
            setProgressDialogState(false);
        }

        @Override
        public void onRequestSuccess(InputControlStatesList stateList) {
            List<InputControlState> invalidStateList = stateList.getInputControlStates();
            if (invalidStateList.isEmpty()) {
                runReport();
            } else {
                updateInputControls(invalidStateList);
            }
            setProgressDialogState(false);
        }
    }

    private class GetReportOptionsListener extends SimpleRequestListener<ReportOptionResponse> {
        @Override
        protected Context getContext() {
            return InputControlsActivity.this;
        }

        @Override
        public void onRequestFailure(SpiceException exception) {
            super.onRequestFailure(exception);
            setProgressDialogState(false);
        }

        @Override
        public void onRequestSuccess(ReportOptionResponse reportOptionResponse) {
            mReportOptions.addAll(reportOptionResponse.getOptions());
            paramsStorage.getInputControlHolder(reportUri).setReportOptions(mReportOptions);

            showReportOptions();
            setProgressDialogState(false);
        }
    }

    private class OnReportOptionSelectListener implements AdapterView.OnItemSelectedListener {

        boolean initialSelectPassed;

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (initialSelectPassed) {
                onReportOptionSelected(position);
            }
            initialSelectPassed = true;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}