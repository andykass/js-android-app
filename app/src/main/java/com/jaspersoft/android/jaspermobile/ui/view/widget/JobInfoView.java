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

package com.jaspersoft.android.jaspermobile.ui.view.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.entity.JobResource;
import com.jaspersoft.android.jaspermobile.ui.component.ViewStateControllerDelegate;
import com.jaspersoft.android.jaspermobile.ui.contract.JobInfoContract;
import com.jaspersoft.android.jaspermobile.ui.presenter.JobInfoPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@EViewGroup(R.layout.view_job_info)
public class JobInfoView extends LinearLayout implements JobInfoContract.View, Toolbar.OnMenuItemClickListener {

    private final static String EMPTY = "--";
    private final static String RUN_DATE_PATTERN = "yyyy-MM-dd HH:mm";
    private SimpleDateFormat mRunDateFormat;

    @ViewById(R.id.jobInfoToolbar)
    Toolbar toolbar;

    @ViewById(R.id.progressBar)
    ProgressBar progressBar;

    @ViewById(R.id.reportLabelValue)
    TextView reportLabel;

    @ViewById(R.id.reportUriValue)
    TextView reportUri;

    @ViewById(R.id.jobNameValue)
    TextView jobName;

    @ViewById(R.id.jobDescriptionValue)
    TextView jobDescription;

    @ViewById(R.id.jobIdValue)
    TextView jobId;

    @ViewById(R.id.jobOwnerValue)
    TextView jobOwner;

    @ViewById(R.id.jobStateValue)
    TextView jobState;

    @ViewById(R.id.jobLastRunValue)
    TextView jobLastRun;

    @ViewById(R.id.jobNextRunValue)
    TextView jobNextRun;

    private MenuItem enableAction;
    private AlertDialog deleteDialog;

    private ViewStateControllerDelegate<JobInfoContract.View, JobInfoContract.EventListener> mControllerDelegate;

    public JobInfoView(Context context) {
        super(context);
    }

    public JobInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JobInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    void initViews() {
        mControllerDelegate = new ViewStateControllerDelegate<>();

        mRunDateFormat = new SimpleDateFormat(RUN_DATE_PATTERN, Locale.getDefault());
        setOrientation(VERTICAL);
        mControllerDelegate.setupUpNavigation(toolbar);

        toolbar.inflateMenu(R.menu.job_info_menu);
        enableAction = toolbar.getMenu().findItem(R.id.enableAction);
        toolbar.setOnMenuItemClickListener(this);

        deleteDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.sdr_delete_title)
                .setMessage(R.string.sdr_delete_message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.spm_delete_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mControllerDelegate.getEveltListener().onDelete();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    public void setEventListener(JobInfoPresenter eventListener) {
        mControllerDelegate.setEventListener(eventListener);
    }

    @Override
    public void showInfo(JobResource resource) {
        reportLabel.setText(resource.getJobTarget().getReportLabel());
        reportUri.setText(resource.getJobTarget().getReportUri());
        jobName.setText(resource.getLabel());
        jobDescription.setText(resource.getDescription().isEmpty() ? EMPTY : resource.getDescription());
        jobId.setText(String.valueOf(resource.getId()));
        jobOwner.setText(resource.getOwner());
        jobState.setText(parseJobState(resource.getState()));

        jobLastRun.setText(resource.getPreviousFireDate() == null ? EMPTY : mRunDateFormat.format(resource.getPreviousFireDate()));
        jobNextRun.setText(resource.getFireDate() == null ? EMPTY : mRunDateFormat.format(resource.getFireDate()));

        toolbar.setTitle(resource.getLabel());
    }

    @Override
    public void showEnableAction(boolean enabled) {
        enableAction.setChecked(enabled);
        enableAction.setIcon(enabled ? R.drawable.ic_menu_enabled : R.drawable.ic_menu_disabled);
        enableAction.setTitle(enabled ? R.string.r_cm_enable : R.string.r_cm_disable);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(VISIBLE);
        toolbar.getMenu().setGroupVisible(R.id.job_info_group, false);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(GONE);
        toolbar.getMenu().setGroupVisible(R.id.job_info_group, true);
    }

    @Override
    public void showDeleted() {
        Toast.makeText(getContext(), R.string.sch_deleted , Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editAction:
                mControllerDelegate.getEveltListener().onEdit();
                return true;
            case R.id.deleteAction:
                deleteDialog.show();
                return true;
            case R.id.enableAction:
                mControllerDelegate.getEveltListener().onEnable();
                return true;
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mControllerDelegate.onAttachedToWindow(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mControllerDelegate.onDetachedFromWindow();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.reportLabel = reportLabel.getText().toString();
        ss.reportUri = reportUri.getText().toString();
        ss.name = jobName.getText().toString();
        ss.description = jobDescription.getText().toString();
        ss.id = jobId.getText().toString();
        ss.owner = jobOwner.getText().toString();
        ss.state = jobState.getText().toString();
        ss.lastRun = jobLastRun.getText().toString();
        ss.nextRun = jobNextRun.getText().toString();
        ss.isInProgress = progressBar.isShown();
        ss.isEnabled = enableAction.isChecked();
        ss.isDeleteDialogShowing = deleteDialog.isShowing();
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        mControllerDelegate.onRestoreInstanceState();

        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        toolbar.setTitle(ss.name);
        reportLabel.setText(ss.reportLabel);
        reportUri.setText(ss.reportUri);
        jobName.setText(ss.name);
        jobDescription.setText(ss.description);
        jobId.setText(ss.id);
        jobOwner.setText(ss.owner);
        jobState.setText(ss.state);
        jobLastRun.setText(ss.lastRun);
        jobNextRun.setText(ss.nextRun);
        showEnableAction(ss.isEnabled);
        progressBar.setVisibility(ss.isInProgress ? VISIBLE : GONE);
        if (ss.isInProgress) {
            showLoading();
        }
        if (ss.isDeleteDialogShowing) {
            deleteDialog.show();
        }
    }

    private String parseJobState(int jobState) {
        switch (jobState) {
            case JobResource.NORMAL:
                return getContext().getString(R.string.sch_state_normal);
            case JobResource.COMPLETE:
                return getContext().getString(R.string.sch_state_complete);
            case JobResource.EXECUTING:
                return getContext().getString(R.string.sch_state_executing);
            case JobResource.ERROR:
                return getContext().getString(R.string.sch_state_error);
            case JobResource.PAUSED:
                return getContext().getString(R.string.sch_state_paused);
            default:
                return getContext().getString(R.string.sch_state_unknown);
        }
    }

    //---------------------------------------------------------------------
    // Saved state
    //---------------------------------------------------------------------

    static class SavedState extends BaseSavedState {
        String reportLabel;
        String reportUri;
        String name;
        String description;
        String id;
        String owner;
        String state;
        String lastRun;
        String nextRun;

        boolean isEnabled;
        boolean isInProgress;
        boolean isDeleteDialogShowing;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);

            reportLabel = in.readString();
            reportUri = in.readString();
            name = in.readString();
            description = in.readString();
            id = in.readString();
            owner = in.readString();
            state = in.readString();
            lastRun = in.readString();
            nextRun = in.readString();
            isEnabled = in.readInt() == 1;
            isInProgress = in.readInt() == 1;
            isDeleteDialogShowing = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeString(reportLabel);
            out.writeString(reportUri);
            out.writeString(name);
            out.writeString(description);
            out.writeString(id);
            out.writeString(owner);
            out.writeString(state);
            out.writeString(lastRun);
            out.writeString(nextRun);
            out.writeInt(isEnabled ? 1 : 0);
            out.writeInt(isInProgress ? 1 : 0);
            out.writeInt(isDeleteDialogShowing ? 1 : 0);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
