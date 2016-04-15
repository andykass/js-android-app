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

package com.jaspersoft.android.jaspermobile.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.dialog.PasswordDialogFragment;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.sdk.service.exception.ServiceException;
import com.jaspersoft.android.sdk.service.exception.StatusCodes;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * @author Ivan Gadzhega
 * @since 1.6
 */
@Singleton
public class RequestExceptionHandler {
    private final Context mContext;

    @Inject
    public RequestExceptionHandler(@ApplicationContext Context context) {
        mContext = context;
    }

    public static void showAuthErrorIfExists(Context context, Throwable exception) {
        RequestExceptionHandler requestExceptionHandler = new RequestExceptionHandler(context);
        requestExceptionHandler.showAuthErrorIfExists(exception);
    }

    public static void showCommonErrorMessage(Context context, Throwable exception) {
        RequestExceptionHandler requestExceptionHandler = new RequestExceptionHandler(context);
        requestExceptionHandler.showCommonErrorMessage(exception);
    }

    @Nullable
    public String extractMessage(@Nullable Throwable exception) {
        if (exception == null) {
            throw new IllegalArgumentException("Exception should not be null");
        }

        if (exception instanceof ServiceException) {
            ServiceException serviceException = ((ServiceException) exception);
            return adaptServiceMessage(serviceException);
        }
        if (exception.getCause() instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) exception.getCause();
            return adaptServiceMessage(serviceException);
        }

        return exception.getLocalizedMessage();
    }

    public void showAuthErrorIfExists(Throwable exception) {
        if (isAuthError(exception)) {
            showAuthErrorDialog();
        } else {
            showCommonErrorMessage(exception);
        }
    }

    public void showCommonErrorMessage(@NonNull Throwable exception) {
        String message = extractMessage(exception);
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }


    @Nullable
    public static String extractMessage(@NonNull Context context, @Nullable Throwable exception) {
        RequestExceptionHandler handler = new RequestExceptionHandler(context);
        return handler.extractMessage(exception);
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------


    private boolean isAuthError(Throwable throwable) {
        if (throwable instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) throwable;
            int code = serviceException.code();
            return code == StatusCodes.AUTHORIZATION_ERROR;
        }
        return false;
    }

    private void showAuthErrorDialog() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (supportFragmentManager != null) {
            PasswordDialogFragment.show(supportFragmentManager);
        }
    }


    private String adaptServiceMessage(ServiceException exception) {
        int code = exception.code();
        switch (code) {
            case StatusCodes.NETWORK_ERROR:
                return mContext.getString(R.string.no_network);
            case StatusCodes.AUTHORIZATION_ERROR:
                return mContext.getString(R.string.error_http_401);
            case StatusCodes.PERMISSION_DENIED_ERROR:
                return mContext.getString(R.string.error_http_403);
            case StatusCodes.CLIENT_ERROR:
                return mContext.getString(R.string.error_http_404);
            case StatusCodes.INTERNAL_ERROR:
                return mContext.getString(R.string.error_http_500);
            case StatusCodes.EXPORT_PAGE_OUT_OF_RANGE:
                return mContext.getString(R.string.rv_out_of_range);
            case StatusCodes.REPORT_EXECUTION_CANCELLED:
                return mContext.getString(R.string.error_report_cancelled);
            case StatusCodes.REPORT_EXECUTION_FAILED:
                return mContext.getString(R.string.error_report_failed);
            case StatusCodes.EXPORT_EXECUTION_FAILED:
                return mContext.getString(R.string.error_export_failed);
            case StatusCodes.EXPORT_EXECUTION_CANCELLED:
                return mContext.getString(R.string.error_export_cancelled);
            case StatusCodes.JOB_DUPLICATE_OUTPUT_FILE_NAME:
                return mContext.getString(R.string.error_schedule_duplicate_file_name);
            case StatusCodes.JOB_START_DATE_IN_THE_PAST:
                return mContext.getString(R.string.error_schedule_start_date_in_the_past);
            case StatusCodes.JOB_OUTPUT_FILENAME_INVALID_CHARS:
                return mContext.getString(R.string.error_schedule_special_characters);
            case StatusCodes.JOB_OUTPUT_FOLDER_DOES_NOT_EXIST:
                return mContext.getString(R.string.error_schedule_folder_not_exist);
            case StatusCodes.JOB_OUTPUT_FOLDER_IS_NOT_WRITABLE:
                return mContext.getString(R.string.error_schedule_folder_not_writable);
            case StatusCodes.JOB_OUTPUT_FILENAME_TOO_LONG:
                String fileLength = exception.getArguments().get(0);
                return mContext.getString(R.string.error_schedule_output_filename_too_long, fileLength);
            case StatusCodes.JOB_TRIGGER_MONTHS_EMPTY:
                return mContext.getString(R.string.error_trigger_empty_months);
            case StatusCodes.JOB_TRIGGER_WEEK_DAYS_EMPTY:
                return mContext.getString(R.string.error_trigger_empty_days);
            case StatusCodes.JOB_LABEL_TOO_LONG:
                String labelLength = exception.getArguments().get(0);
                return mContext.getString(R.string.error_schedule_label_too_long, labelLength);
            case StatusCodes.SAVED_VALUES_EXIST_IN_FOLDER:
                return mContext.getString(R.string.error_saved_values_duplicate);
            case StatusCodes.SAVED_VALUES_LABEL_TOO_LONG:
                String savedValesLabelLength = exception.getArguments().get(0);
                return mContext.getString(R.string.error_saved_values_label_too_long, savedValesLabelLength);
            default:
                return mContext.getString(R.string.error_undefined);
        }
    }

    /**
     * It is dirty method will leave here until
     * we move to android.app.FragmentManager
     *
     * @return FragmentManager or null
     */
    @Nullable
    private FragmentManager getSupportFragmentManager() {
        if (mContext instanceof AppCompatActivity) {
            return ((AppCompatActivity) mContext).getSupportFragmentManager();
        } else {
            return null;
        }
    }

}