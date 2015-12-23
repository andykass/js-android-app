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

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.jaspersoft.android.jaspermobile.dialog.SaveReportOptionDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.SimpleDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.TextInputControlDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.TextInputControlDialogFragment_;
import com.jaspersoft.android.jaspermobile.network.SimpleRequestListener;
import com.jaspersoft.android.jaspermobile.util.IcDateHelper;
import com.jaspersoft.android.jaspermobile.util.ReportOptionHolder;
import com.jaspersoft.android.jaspermobile.util.ReportParamsStorage;
import com.jaspersoft.android.jaspermobile.util.account.AccountServerData;
import com.jaspersoft.android.jaspermobile.util.account.JasperAccountManager;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.async.request.CreateReportOptionsRequest;
import com.jaspersoft.android.sdk.client.async.request.DeleteReportOptionRequest;
import com.jaspersoft.android.sdk.client.async.request.GetReportOptionValuesRequest;
import com.jaspersoft.android.sdk.client.async.request.ReportOptionsRequest;
import com.jaspersoft.android.sdk.client.async.request.cacheable.GetInputControlsValuesRequest;
import com.jaspersoft.android.sdk.client.async.request.cacheable.ValidateInputControlsValuesRequest;
import com.jaspersoft.android.sdk.client.ic.InputControlWrapper;
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
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
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
public class InputControlsActivity extends RoboSpiceActivity
        implements InputControlsAdapter.InputControlInteractionListener,
        DateDialogFragment.DateDialogClickListener,
        SimpleDialogFragment.SimpleDialogClickListener,
        SaveReportOptionDialogFragment.SaveReportOptionDialogCallback,
        TextInputControlDialogFragment.InputControlValueDialogCallback
{
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
    @OptionsMenuItem(R.id.deleteReportOption)
    protected MenuItem deleteAction;
    @OptionsMenuItem(R.id.saveReportOption)
    protected MenuItem saveAction;

    @Extra
    protected String reportUri;

    private List<InputControl> mInputControls;
    private List<ReportOptionHolder> mReportOptions;
    private List<String> mReportOptionsTitles;
    private InputControlsAdapter mAdapter;
    private ArrayAdapter<String> mReportOptionsAdapter;
    private boolean mIsProJrs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Account account = JasperAccountManager.get(this).getActiveAccount();
        AccountServerData serverData = AccountServerData.get(this, account);
        mIsProJrs = serverData.getEdition().equals("PRO");

        mInputControls = paramsStorage.getInputControlHolder(reportUri).getInputControls();
        mReportOptions = paramsStorage.getInputControlHolder(reportUri).getReportOptions();
        mReportOptionsTitles = new ArrayList<>();

        if (savedInstanceState == null) {
            updateInputControlsFromReportParams();
        }

        if (mReportOptions.isEmpty()) {
            loadReportOptions();
        }
    }

    @AfterViews
    protected void init() {
        initToolbar();
        showInputControls();
        showReportOptions();
    }

    @OptionsItem(R.id.deleteReportOption)
    protected void deleteReportOptionAction() {
        ReportOption currentReportOption = mReportOptions.get(getSelectedReportOptionPosition()).getReportOption();
        SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.ro_delete_ro)
                .setMessage(getString(R.string.sdr_drd_msg, currentReportOption.getLabel()))
                .setPositiveButtonText(R.string.spm_delete_btn)
                .setNegativeButtonText(R.string.cancel)
                .show();
    }

    @OptionsItem(R.id.saveReportOption)
    protected void saveReportOptionAction() {
        setProgressDialogState(true);
        ValidateInputControlsValuesRequest request = new ValidateInputControlsValuesRequest(jsRestClient, reportUri, mInputControls);
        getSpiceManager().execute(request, new ValidateReportOptionsValuesListener());
    }

    @OptionsItem(R.id.resetReportOption)
    protected void resetReportOptionAction() {
        onReportOptionSelected(getSelectedReportOptionPosition());
    }

    @Click(R.id.btnApplyParams)
    protected void applyParamsClick() {
        setProgressDialogState(true);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        deleteAction.setVisible(reportOptionsList.getSelectedItemPosition() > 0 && mIsProJrs);
        saveAction.setVisible(mIsProJrs);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBooleanStateChanged(InputControl inputControl, boolean newState) {
        inputControl.getState().setValue(String.valueOf(newState));
        mAdapter.updateInputControl(inputControl);
        updateDependentControls(inputControl);
    }

    @Override
    public void onValueTextChanged(InputControl inputControl) {
        TextInputControlDialogFragment_.createBuilder(getSupportFragmentManager())
                .setInputControl(inputControl)
                .show();
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

    @Override
    public void onPositiveClick(int requestCode) {
        deleteReportOption();
    }

    @Override
    public void onNegativeClick(int requestCode) {
    }

    @Override
    public void onSaveConfirmed(String name) {
        saveReportOption(name);
    }

    @Override
    public void onTextValueEntered(InputControl inputControl, String text) {
        inputControl.getState().setValue(text);
        mAdapter.updateInputControl(inputControl);
        updateDependentControls(inputControl);
    }

    @Override
    protected String getScreenName() {
        return getString(R.string.ja_ics);
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

    private void loadReportOptions() {
        mReportOptions = new ArrayList<>();
        ReportOption defaultReportOption = new ReportOption(reportUri, reportUri, getString(R.string.ro_default));
        ReportOptionHolder reportOptionHolder = new ReportOptionHolder(defaultReportOption, mInputControls.hashCode());
        reportOptionHolder.setSelected(true);
        mReportOptions.add(reportOptionHolder);

        if (mIsProJrs) {
            ReportOptionsRequest runReportExecutionRequest = new ReportOptionsRequest(jsRestClient, reportUri);
            getSpiceManager().execute(runReportExecutionRequest, new GetReportOptionsListener());
            setProgressDialogState(true);
        }
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
        updateReportOptionsTitlesList();

        // It's a hack to make spinner width as a selected item width
        mReportOptionsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mReportOptionsTitles) {
            @Override
            public View getView(final int position, final View convertView,
                                final ViewGroup parent) {
                int selectedItemPosition = InputControlsActivity.this.reportOptionsList.getSelectedItemPosition();
                return super.getView(selectedItemPosition, convertView, parent);
            }
        };

        mReportOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportOptionsList.setAdapter(mReportOptionsAdapter);

        reportOptionsList.setOnItemSelectedListener(new OnReportOptionSelectListener());

        int selectedReportOptionPosition = getSelectedReportOptionPosition();
        reportOptionsList.setSelection(selectedReportOptionPosition, false);
        reportOptionsList.setVisibility(mIsProJrs ? View.VISIBLE : View.GONE);
    }

    private void onReportOptionSelected(int position) {
        ReportOption reportOption = mReportOptions.get(position).getReportOption();

        setProgressDialogState(true);

        GetReportOptionValuesRequest request = new GetReportOptionValuesRequest(jsRestClient, reportOption.getUri());
        getSpiceManager().execute(request, new GetReportOptionValuesListener());
    }

    private void deleteReportOption() {
        setProgressDialogState(true);

        ReportOption currentReportOption = mReportOptions.get(getSelectedReportOptionPosition()).getReportOption();
        DeleteReportOptionRequest request = new DeleteReportOptionRequest(jsRestClient, reportUri, currentReportOption.getId());
        getSpiceManager().execute(request, new DeleteReportOptionListener());
    }

    private void showSaveDialog() {
        List<String> reportOptionsNames = new ArrayList<>();
        for (ReportOptionHolder reportOption : mReportOptions) {
            String reportOptionTitle = reportOption.getReportOption().getLabel();
            reportOptionsNames.add(reportOptionTitle);
        }

        SaveReportOptionDialogFragment.createBuilder(getSupportFragmentManager())
                .setCurrentlySelected(getSelectedReportOptionPosition())
                .setReportOptionsTitles(reportOptionsNames)
                .show();
    }

    private void saveReportOption(String reportOptionName) {
        setProgressDialogState(true);

        ArrayList<ReportParameter> parameters = initParametersUsingSelectedValues();
        Map<String, Set<String>> hashMap = new HashMap<>(parameters.size());
        for (ReportParameter reportParameter : parameters) {
            hashMap.put(reportParameter.getName(), reportParameter.getValues());
        }

        CreateReportOptionsRequest request = new CreateReportOptionsRequest(jsRestClient, reportUri, reportOptionName, hashMap);
        getSpiceManager().execute(request, new SaveReportOptionListener());
    }

    private void setProgressDialogState(boolean loading) {
        if (loading) {
            ProgressDialogFragment.builder(getSupportFragmentManager())
                    .setLoadingMessage(R.string.loading_msg)
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
        updateReportOptionsTitlesList();
        mReportOptionsAdapter.notifyDataSetChanged();
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
                if (inputControl.getType() == InputControl.Type.bool && inputControlState.getValue().equals(InputControlWrapper.NULL_SUBSTITUTE)) {
                    inputControlState.setValue("false");
                }
                inputControl.setState(inputControlState);
            }
        }
        mAdapter.updateInputControlList(mInputControls);
    }

    private void updateInputControlsFromReportParams() {
        List<ReportParameter> reportParams = paramsStorage.getInputControlHolder(reportUri).getReportParams();

        Map<String, Set<String>> hashMap = new HashMap<>(reportParams.size());
        for (ReportParameter reportParameter : reportParams) {
            hashMap.put(reportParameter.getName(), reportParameter.getValues());
        }

        for (InputControl inputControl : mInputControls) {
            updateInputControlState(hashMap, inputControl);
            if (inputControl.getType() == InputControl.Type.bool && inputControl.getState().getValue().equals(InputControlWrapper.NULL_SUBSTITUTE)) {
                inputControl.getState().setValue("false");
            }
        }
    }

    private void updateInputControlState(Map<String, Set<String>> hashMap, InputControl inputControl) {
        InputControlState state = inputControl.getState();
        List<InputControlOption> options = state.getOptions();
        Set<String> valueSet = hashMap.get(state.getId());
        List<String> valueList = new ArrayList<>();
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

    private void updateReportOptionsTitlesList() {
        mReportOptionsTitles.clear();
        for (int i = 0; i < mReportOptions.size(); i++) {
            String reportOptionTitle = mReportOptions.get(i).getReportOption().getLabel();
            if (i == getSelectedReportOptionPosition()) {
                int currentHashCode = mInputControls.hashCode();
                Integer reportOptionHashCode = mReportOptions.get(i).getHashCode();
                if (reportOptionHashCode != null && reportOptionHashCode != currentHashCode) {
                    reportOptionTitle = "* " + reportOptionTitle;
                }
            }
            mReportOptionsTitles.add(reportOptionTitle);
        }
    }

    private int getSelectedReportOptionPosition() {
        for (int i = 0; i < mReportOptions.size(); i++) {
            if (mReportOptions.get(i).isSelected()) return i;
        }
        return -1;
    }

    private void addReportOption(ReportOption reportOption) {
        String savedReportOptionTitle = reportOption.getLabel();
        ReportOptionHolder reportOptionHolder = new ReportOptionHolder(reportOption, mInputControls.hashCode());
        reportOptionHolder.setSelected(true);

        List<String> reportOptionsNames = new ArrayList<>();
        for (ReportOptionHolder mReportOption : mReportOptions) {
            String reportOptionTitle = mReportOption.getReportOption().getLabel();
            reportOptionsNames.add(reportOptionTitle);
        }

        boolean added = false;
        for (int i = 1; i < reportOptionsNames.size(); i++) {
            if (savedReportOptionTitle.compareTo(reportOptionsNames.get(i)) < 0) {
                mReportOptions.add(i, reportOptionHolder);
                reportOptionsList.setSelection(i);
                added = true;
                break;
            } else if (savedReportOptionTitle.compareTo(reportOptionsNames.get(i)) == 0) {
                mReportOptions.set(i, reportOptionHolder);
                reportOptionsList.setSelection(i);
                added = true;
                break;
            }
        }

        if (!added) {
            mReportOptions.add(reportOptionHolder);
            reportOptionsList.setSelection(reportOptionsNames.size());
        }
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
                onValidationPassed();
            } else {
                updateInputControls(invalidStateList);
            }
            setProgressDialogState(false);
        }

        protected void onValidationPassed(){
            runReport();
        }
    }

    private class ValidateReportOptionsValuesListener extends ValidateInputControlsValuesListener {
        @Override
        protected void onValidationPassed() {
            showSaveDialog();
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
            for (ReportOption reportOption : reportOptionResponse.getOptions()) {
                mReportOptions.add(new ReportOptionHolder(reportOption, null));
            }
            paramsStorage.getInputControlHolder(reportUri).setReportOptions(mReportOptions);

            showReportOptions();
            setProgressDialogState(false);
        }
    }

    private class GetReportOptionValuesListener extends GetInputControlsValuesListener {
        @Override
        public void onRequestSuccess(InputControlStatesList stateList) {
            super.onRequestSuccess(stateList);

            mReportOptions.get(getSelectedReportOptionPosition()).setSelected(false);
            mReportOptions.get(reportOptionsList.getSelectedItemPosition()).setSelected(true);

            mReportOptions.get(getSelectedReportOptionPosition()).setHashCode(mInputControls.hashCode());

            invalidateOptionsMenu();
            updateReportOptionsTitlesList();
            mReportOptionsAdapter.notifyDataSetChanged();
        }
    }

    private class DeleteReportOptionListener extends SimpleRequestListener<Void> {
        @Override
        protected Context getContext() {
            return InputControlsActivity.this;
        }

        @Override
        public void onRequestSuccess(Void result) {
            setProgressDialogState(false);

            int removalIndex = getSelectedReportOptionPosition();
            int currentIndex = removalIndex - 1;
            mReportOptions.remove(removalIndex);
            mReportOptions.get(currentIndex).setSelected(true);

            reportOptionsList.setSelection(currentIndex);
            onReportOptionSelected(currentIndex);
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            super.onRequestFailure(spiceException);
            setProgressDialogState(false);
        }
    }

    private class SaveReportOptionListener extends SimpleRequestListener<ReportOption> {

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
        public void onRequestSuccess(ReportOption reportOption) {
            mReportOptions.get(getSelectedReportOptionPosition()).setSelected(false);

            addReportOption(reportOption);
            updateReportOptionsTitlesList();
            mReportOptionsAdapter.notifyDataSetChanged();

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