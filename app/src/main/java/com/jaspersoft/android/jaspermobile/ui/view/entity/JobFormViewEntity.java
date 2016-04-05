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

package com.jaspersoft.android.jaspermobile.ui.view.entity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.jaspersoft.android.sdk.client.ic.InputControlWrapper;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class JobFormViewEntity implements Serializable {

    final int mVersion;
    @NonNull
    final String mSource;
    @NonNull
    private final String mJobName;
    @NonNull
    private final String mFileName;
    @NonNull
    private final String mOutputPath;
    @Nullable
    private final Date mStartDate;
    @NonNull
    private final List<OutputFormat> mOutputFormats;

    private JobFormViewEntity(
            int version,
            @NonNull String jobName,
            @NonNull String fileName,
            @NonNull String outputPath,
            @NonNull String source,
            @Nullable Date startDate,
            @NonNull List<OutputFormat> outputFormats) {
        mVersion = version;
        mJobName = jobName;
        mFileName = fileName;
        mOutputPath = outputPath;
        mStartDate = startDate;
        mSource = source;
        mOutputFormats = outputFormats;
    }

    public Calendar getStartDate() {
        Calendar instance = Calendar.getInstance();
        if (hasStartDate()) {
            instance.setTime(mStartDate);
        }
        return instance;
    }

    public boolean hasStartDate() {
        return mStartDate != null;
    }

    @NonNull
    public String getFileName() {
        return mFileName;
    }

    @NonNull
    public String getJobName() {
        return mJobName;
    }

    @NonNull
    public String getOutputPath() {
        return mOutputPath;
    }

    @NonNull
    public List<OutputFormat> getOutputFormats() {
        return mOutputFormats;
    }

    @NonNull
    public Builder newBuilder() {
        return new Builder()
                .withInternalVersion(mVersion)
                .withInternalSource(mSource)
                .withName(mJobName)
                .withFileName(mFileName)
                .withOutputPath(mOutputPath)
                .withStartDate(mStartDate)
                .withOutputFormats(mOutputFormats);
    }

    public String getSupportedFormatsTitles() {
        if (mOutputFormats.isEmpty()) {
            return InputControlWrapper.NOTHING_SUBSTITUTE_LABEL;
        } else {
            return TextUtils.join(", ", mOutputFormats);
        }
    }

    public static class OutputFormat {
        final String mRawType;
        private final String mLabel;

        OutputFormat(String rawType, String label) {
            mRawType = rawType;
            mLabel = label;
        }

        public String getLabel() {
            return mLabel;
        }

        @Override
        public String toString() {
            return mLabel;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OutputFormat format = (OutputFormat) o;

            if (mRawType != null ? !mRawType.equals(format.mRawType) : format.mRawType != null)
                return false;
            return mLabel != null ? mLabel.equals(format.mLabel) : format.mLabel == null;

        }

        @Override
        public int hashCode() {
            int result = mRawType != null ? mRawType.hashCode() : 0;
            result = 31 * result + (mLabel != null ? mLabel.hashCode() : 0);
            return result;
        }
    }

    public static class Builder {
        private int mVersion;
        private String mJobName;
        private String mFileName;
        private String mOutputPath;
        private String mSource;
        private Date mStartDate;
        private List<OutputFormat> mOutputFormats = Collections.emptyList();

        Builder withInternalVersion(@NonNull int version) {
            mVersion = version;
            return this;
        }

        Builder withInternalSource(@NonNull String uri) {
            mSource = uri;
            return this;
        }

        public Builder withName(@NonNull String jobName) {
            mJobName = jobName;
            return this;
        }

        public Builder withFileName(@NonNull String fileName) {
            mFileName = fileName;
            return this;
        }

        public Builder withOutputPath(@NonNull String outputPath) {
            mOutputPath = outputPath;
            return this;
        }

        public Builder withStartDate(@Nullable Date startDate) {
            mStartDate = startDate;
            return this;
        }

        public Builder withOutputFormats(@Nullable List<OutputFormat> outputFormats) {
            if (outputFormats == null) {
                mOutputFormats = Collections.emptyList();
            }
            mOutputFormats = outputFormats;
            return this;
        }

        public JobFormViewEntity build() {
            return new JobFormViewEntity(
                    mVersion,
                    mJobName,
                    mFileName,
                    mOutputPath,
                    mSource,
                    mStartDate,
                    Collections.unmodifiableList(mOutputFormats)
            );
        }
    }
}
