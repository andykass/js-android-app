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

package com.jaspersoft.android.jaspermobile.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.sdk.client.ic.InputControlWrapper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class DateTimeView extends LinearLayout {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    private TextView label;
    private TextView selectedDateTime;
    private TextView errorText;
    protected ImageButton btnDate;
    protected ImageButton btnTime;
    private ImageButton btnClear;
    private View clearDivider;
    protected View dateTimeDivider;

    protected SimpleDateFormat mUserDateFormat;
    private boolean mClearVisible;
    private int mRequestCode;
    private DateType mDateType;
    private DateTimeClickListener mDateTimeClickListener;

    public DateTimeView(Context context) {
        super(context);
        init(context);
    }

    public DateTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DateTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_input_control_date, this);

        mDateType = DateType.DATE_TIME;
        mClearVisible = true;

        selectedDateTime = (TextView) itemView.findViewById(R.id.ic_datetime_text);
        errorText = (TextView) itemView.findViewById(R.id.ic_error_text);
        label = (TextView) itemView.findViewById(R.id.ic_text_label);
        btnDate = (ImageButton) itemView.findViewById(R.id.ic_date);
        btnTime = (ImageButton) itemView.findViewById(R.id.ic_time);
        btnClear = (ImageButton) itemView.findViewById(R.id.ic_clear);
        clearDivider = itemView.findViewById(R.id.ic_clear_divider);
        dateTimeDivider = itemView.findViewById(R.id.ic_datetime_divider);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDateTimeClickListener != null) {
                    mDateTimeClickListener.onDateClick(mRequestCode);
                }
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDateTimeClickListener != null) {
                    mDateTimeClickListener.onTimeClick(mRequestCode);
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDateTimeClickListener != null) {
                    mDateTimeClickListener.onClear(mRequestCode);
                }
            }
        });

        selectedDateTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setClearButtonVisibility(!s.toString().equals(InputControlWrapper.NOTHING_SUBSTITUTE_LABEL));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updateViews();
    }

    public void setRequestCode(int requestCode) {
        mRequestCode = requestCode;
    }

    public void setDateTimeClickListener(DateTimeClickListener dateTimeClickListener) {
        this.mDateTimeClickListener = dateTimeClickListener;
    }

    public void enableViews(Boolean enabled) {
        selectedDateTime.setEnabled(enabled);

        btnDate.setEnabled(enabled);
        btnDate.setAlpha(enabled ? 1f : .5f);

        btnTime.setEnabled(enabled);
        btnTime.setAlpha(enabled ? 1f : .5f);

        btnClear.setEnabled(enabled);
        btnClear.setAlpha(enabled ? 1f : .5f);
    }

    public void enableClearButton(boolean enabled) {
        mClearVisible = enabled;
        setClearButtonVisibility(selectedDateTime.getText().length() > 0);
    }

    public void setDateType(DateType dateType) {
        mDateType = dateType;

        updateViews();
    }

    public void setLabel(String title) {
        label.setText(title);
    }

    public void setPreview(String preview) {
        selectedDateTime.setText(preview);
    }

    public void setDate(Calendar date) {
        String dateText = null;
        if (date != null) {
            dateText = mUserDateFormat.format(date.getTime());
        }
        selectedDateTime.setText(dateText != null ? dateText : getContext().getString(R.string.empty_text_place_holder));
    }

    public void setClearButtonVisibility(boolean visible) {
        btnClear.setVisibility(visible && mClearVisible ? View.VISIBLE : View.GONE);
        clearDivider.setVisibility(visible && mClearVisible ? View.VISIBLE : View.GONE);
    }

    private void updateViews() {
        switch (mDateType) {
            case DATE:
                btnTime.setVisibility(View.GONE);
                dateTimeDivider.setVisibility(View.GONE);
                mUserDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
                break;
            case TIME:
                btnDate.setVisibility(View.GONE);
                dateTimeDivider.setVisibility(View.GONE);
                mUserDateFormat = new SimpleDateFormat(DEFAULT_TIME_FORMAT, Locale.getDefault());
                break;
            default:
                mUserDateFormat = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT, Locale.getDefault());
                btnTime.setVisibility(View.VISIBLE);
                dateTimeDivider.setVisibility(View.VISIBLE);
                btnDate.setVisibility(View.VISIBLE);
                dateTimeDivider.setVisibility(View.VISIBLE);
        }
    }

    public TextView getErrorView() {
        return errorText;
    }

    public enum DateType {
        DATE,
        TIME,
        DATE_TIME
    }

    public interface DateTimeClickListener {
        void onDateClick(int position);

        void onTimeClick(int position);

        void onClear(int position);
    }
}
