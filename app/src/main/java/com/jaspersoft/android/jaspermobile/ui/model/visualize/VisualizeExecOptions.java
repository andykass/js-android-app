package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.domain.AppCredentials;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public final class VisualizeExecOptions {
    @NonNull
    private final String mUri;
    @NonNull
    private final String mParams;
    @NonNull
    private final AppCredentials mAppCredentials;
    private final double mDiagonal;

    public VisualizeExecOptions(@NonNull String uri,
                                @NonNull String params,
                                @NonNull AppCredentials appCredentials,
                                double diagonal) {
        mUri = uri;
        mParams = params;
        mAppCredentials = appCredentials;
        mDiagonal = diagonal;
    }

    @NonNull
    public String getParams() {
        return mParams;
    }

    @NonNull
    public String getUri() {
        return mUri;
    }

    @NonNull
    public AppCredentials getAppCredentials() {
        return mAppCredentials;
    }

    public double getDiagonal() {
        return mDiagonal;
    }

    public static class Builder {
        private String mUri;
        private String mParams;
        private AppCredentials mAppCredentials;
        private double mDiagonal;

        public Builder setUri(String uri) {
            mUri = uri;
            return this;
        }

        public Builder setParams(String params) {
            mParams = params;
            return this;
        }

        public Builder setAppCredentials(AppCredentials appCredentials) {
            mAppCredentials = appCredentials;
            return this;
        }

        public Builder setDiagonal(double diagonal) {
            mDiagonal = diagonal;
            return this;
        }

        public VisualizeExecOptions build() {
            return new VisualizeExecOptions(mUri, mParams, mAppCredentials, mDiagonal);
        }
    }
}
