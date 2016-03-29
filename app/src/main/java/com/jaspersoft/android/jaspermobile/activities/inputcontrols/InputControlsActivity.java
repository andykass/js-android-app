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

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.adapters.InputControlsAdapter;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.viewholders.ItemSpaceDecoration;
import com.jaspersoft.android.jaspermobile.dialog.DateDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.SaveReportOptionDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.SimpleDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.TextInputControlDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.TextInputControlDialogFragment_;
import com.jaspersoft.android.jaspermobile.domain.DeleteOptionRequest;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.SaveOptionRequest;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetInputControlsValuesCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.ValidateDashboardInputControlsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.ValidateReportInputControlsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.option.DeleteReportOptionCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.option.GetReportOptionValuesCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.option.GetReportOptionsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.option.SaveReportOptionsCase;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportModule;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.util.IcDateHelper;
import com.jaspersoft.android.jaspermobile.util.ReportOptionHolder;
import com.jaspersoft.android.jaspermobile.util.ReportParamsStorage;
import com.jaspersoft.android.sdk.client.ic.InputControlWrapper;
import com.jaspersoft.android.sdk.client.oxm.control.InputControl;
import com.jaspersoft.android.sdk.client.oxm.control.InputControlOption;
import com.jaspersoft.android.sdk.client.oxm.control.InputControlState;
import com.jaspersoft.android.sdk.client.oxm.report.ReportParameter;
import com.jaspersoft.android.sdk.service.data.report.option.ReportOption;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import rx.Subscriber;
import timber.log.Timber;

/**
 * @author Ivan Gadzhega
 * @author Tom Koptel
 * @author Andrew Tivodar
 * @since 1.6
 */
@EActivity(R.layout.view_simple_list)
@OptionsMenu(R.menu.input_control_menu)
public class InputControlsActivity extends ToolbarActivity
        implements InputControlsAdapter.InputControlInteractionListener,
        DateDialogFragment.DateDialogClickListener,
        SimpleDialogFragment.SimpleDialogClickListener,
        SaveReportOptionDialogFragment.SaveReportOptionDialogCallback,
        TextInputControlDialogFragment.InputControlValueDialogCallback {
    // Extras
    public static final int SELECT_IC_REQUEST_CODE = 521;
    public static final String RESULT_SAME_PARAMS = "ReportOptionsActivity.SAME_PARAMS";

    @Inject
    protected ReportParamsStorage paramsStorage;
    @Inject
    protected JasperServer mJasperServer;
    @Inject
    protected GetInputControlsValuesCase mGetInputControlsValuesCase;
    @Inject
    protected ValidateReportInputControlsCase mValidateReportInputControlsCase;
    @Inject
    protected ValidateDashboardInputControlsCase mValidateDashboardInputControlsCase;
    @Inject
    protected GetReportOptionsCase mGetReportOptionsCase;
    @Inject
    protected SaveReportOptionsCase mSaveReportOptionsCase;
    @Inject
    protected GetReportOptionValuesCase mGetReportOptionValuesCase;
    @Inject
    protected DeleteReportOptionCase mDeleteReportOptionCase;

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
    @OptionsMenuItem(R.id.resetReportOption)
    protected MenuItem resetAction;

    @Extra
    protected String reportUri;
    @Extra
    protected boolean dashboardInputControl;

    private List<InputControl> mInputControls;
    private List<ReportOptionHolder> mReportOptions;
    private List<String> mReportOptionsTitles;
    private InputControlsAdapter mAdapter;
    private ArrayAdapter<String> mReportOptionsAdapter;
    private boolean mIsProJrs;
    private Bundle mSavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getProfileComponent()
                .plusControlsPage(
                        new ActivityModule(this),
                        new ReportModule(reportUri)
                )
                .inject(this);

        mSavedInstanceState = savedInstanceState;
        mIsProJrs = mJasperServer.isProEdition();

        mInputControls = paramsStorage.getInputControlHolder(reportUri).getInputControls();
        if (mInputControls == null) {
            mInputControls = Collections.emptyList();
        }
        mReportOptions = paramsStorage.getInputControlHolder(reportUri).getReportOptions();
        mReportOptionsTitles = new ArrayList<>();
    }

    @AfterViews
    protected void init() {
        initToolbar();
        showInputControls();

        boolean noReportOptions = mReportOptions.isEmpty();
        boolean isReportControlsPage = !dashboardInputControl;
        if (isReportControlsPage) {
            if (noReportOptions) {
                loadReportOptions();
            } else {
                showReportOptions();
            }
        }

        if (mSavedInstanceState == null) {
            updateInputControlsFromReportParams();
        }

        updateReportOptionsTitlesList();
        notifyReportOptionsChange();
    }

    @Override
    protected void onStop() {
        mGetInputControlsValuesCase.unsubscribe();
        mValidateReportInputControlsCase.unsubscribe();
        mValidateDashboardInputControlsCase.unsubscribe();
        mGetReportOptionsCase.unsubscribe();
        mSaveReportOptionsCase.unsubscribe();
        mGetReportOptionValuesCase.unsubscribe();
        mDeleteReportOptionCase.unsubscribe();
        super.onStop();
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
        mValidateReportInputControlsCase.execute(reportUri, new GenericSubscriber<>(new ValidateReportOptionsValuesListener()));
    }

    @OptionsItem(R.id.resetReportOption)
    protected void resetReportOptionAction() {
        onReportOptionSelected(getSelectedReportOptionPosition());
    }

    @Click(R.id.btnApplyParams)
    protected void applyParamsClick() {
        GenericSubscriber<List<InputControlState>> useCaseSubscriber =
                new GenericSubscriber<>(new ValidateInputControlsValuesListener());

        setProgressDialogState(true);
        if (dashboardInputControl) {
            mValidateDashboardInputControlsCase.execute(reportUri, useCaseSubscriber);
        } else {
            mValidateReportInputControlsCase.execute(reportUri, useCaseSubscriber);
        }
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
        saveAction.setVisible(mIsProJrs && !dashboardInputControl);
        resetAction.setVisible(!dashboardInputControl);
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
        ReportOption defaultReportOption = new ReportOption.Builder()
                .withId(reportUri)
                .withUri(reportUri)
                .withLabel(getString(R.string.ro_default))
                .build();
        ReportOptionHolder reportOptionHolder = new ReportOptionHolder(defaultReportOption, mInputControls.hashCode());
        reportOptionHolder.setSelected(true);
        mReportOptions.add(reportOptionHolder);

        if (mIsProJrs) {
            setProgressDialogState(true);
            mGetReportOptionsCase.execute(reportUri, new GenericSubscriber<>(new GetReportOptionsListener()));
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
        reportOptionsList.setVisibility(mIsProJrs && !dashboardInputControl ? View.VISIBLE : View.GONE);
    }

    private void onReportOptionSelected(int position) {
        ReportOption reportOption = mReportOptions.get(position).getReportOption();
        setProgressDialogState(true);
        mGetReportOptionValuesCase.execute(reportOption.getUri(), new GenericSubscriber<>(new GetReportOptionValuesListener()));
    }

    private void deleteReportOption() {
        setProgressDialogState(true);

        ReportOption currentReportOption = mReportOptions.get(getSelectedReportOptionPosition()).getReportOption();
        DeleteOptionRequest request = new DeleteOptionRequest(reportUri, currentReportOption.getId());
        mDeleteReportOptionCase.execute(request, new GenericSubscriber<>(new DeleteReportOptionListener()));
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

        SaveOptionRequest request = new SaveOptionRequest(reportUri, reportOptionName, parameters);
        mSaveReportOptionsCase.execute(request, new GenericSubscriber<>(new SaveReportOptionListener()));
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
            mGetInputControlsValuesCase.execute(reportUri, new GenericSubscriber<>(new GetInputControlsValuesListener()));
        }
        updateReportOptionsTitlesList();
        notifyReportOptionsChange();
    }

    private void notifyReportOptionsChange() {
        if (mReportOptionsAdapter != null) {
            mReportOptionsAdapter.notifyDataSetChanged();
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
            if (savedReportOptionTitle.compareToIgnoreCase(reportOptionsNames.get(i)) < 0) {
                mReportOptions.add(i, reportOptionHolder);
                reportOptionsList.setSelection(i);
                added = true;
                break;
            } else if (savedReportOptionTitle.compareToIgnoreCase(reportOptionsNames.get(i)) == 0) {
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

    private class GetInputControlsValuesListener extends SimpleSubscriber<List<InputControlState>> {
        @Override
        public void onNext(List<InputControlState> stateList) {
            updateInputControls(stateList);
        }
    }

    private class ValidateInputControlsValuesListener extends SimpleSubscriber<List<InputControlState>> {
        @Override
        public void onNext(List<InputControlState> stateList) {
            if (stateList.isEmpty()) {
                onValidationPassed();
            } else {
                updateInputControls(stateList);
            }
        }

        protected void onValidationPassed() {
            runReport();
        }
    }

    private class ValidateReportOptionsValuesListener extends ValidateInputControlsValuesListener {
        @Override
        protected void onValidationPassed() {
            showSaveDialog();
        }
    }

    private class GetReportOptionsListener extends SimpleSubscriber<Set<ReportOption>> {
        @Override
        public void onNext(Set<ReportOption> options) {
            for (ReportOption reportOption : options) {
                mReportOptions.add(new ReportOptionHolder(reportOption, null));
            }
            paramsStorage.getInputControlHolder(reportUri).setReportOptions(mReportOptions);

            showReportOptions();
        }
    }

    private class GetReportOptionValuesListener extends GetInputControlsValuesListener {
        @Override
        public void onNext(List<InputControlState> stateList) {
            super.onNext(stateList);

            if (mIsProJrs) {
                int positionInSpinner = reportOptionsList.getSelectedItemPosition();
                int currentSelection = getSelectedReportOptionPosition();
                ReportOptionHolder currentOption = mReportOptions.get(currentSelection);
                ReportOptionHolder selectedOption = mReportOptions.get(positionInSpinner);

                currentOption.setSelected(false);
                currentOption.setHashCode(mInputControls.hashCode());

                selectedOption.setSelected(true);
            }

            invalidateOptionsMenu();
            updateReportOptionsTitlesList();
            notifyReportOptionsChange();
        }
    }

    private class DeleteReportOptionListener extends SimpleSubscriber<Void> {
        @Override
        public void onNext(Void result) {
            int removalIndex = getSelectedReportOptionPosition();
            int currentIndex = removalIndex - 1;
            mReportOptions.remove(removalIndex);
            mReportOptions.get(currentIndex).setSelected(true);

            reportOptionsList.setSelection(currentIndex);
            onReportOptionSelected(currentIndex);
        }
    }

    private class SaveReportOptionListener extends SimpleSubscriber<ReportOption> {
        @Override
        public void onNext(ReportOption reportOption) {
            mReportOptions.get(getSelectedReportOptionPosition()).setSelected(false);

            addReportOption(reportOption);
            updateReportOptionsTitlesList();
            notifyReportOptionsChange();
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

    private class GenericSubscriber<T> extends Subscriber<T> {
        private final Subscriber<T> mDelegate;

        private GenericSubscriber(Subscriber<T> delegate) {
            mDelegate = delegate;
        }

        @Override
        public void onCompleted() {
            setProgressDialogState(false);
            mDelegate.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "Subscriber crashed with error");
            RequestExceptionHandler.showCommonErrorMessage(InputControlsActivity.this, e);
            setProgressDialogState(false);
            mDelegate.onError(e);
        }

        @Override
        public void onNext(T t) {
            mDelegate.onNext(t);
        }
    }
}