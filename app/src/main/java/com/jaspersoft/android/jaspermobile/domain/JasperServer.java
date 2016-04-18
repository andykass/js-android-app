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

package com.jaspersoft.android.jaspermobile.domain;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.util.BaseUrlNormalizer;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class JasperServer {
    @NonNull
    private final String baseUrl;
    @NonNull
    private final String edition;
    @NonNull
    private final String version;

    private final boolean fake;

    private JasperServer(@NonNull String baseUrl,
                         @NonNull String edition,
                         @NonNull String version,
                         boolean fake) {
        this.baseUrl = baseUrl;
        this.edition = edition;
        this.version = version;
        this.fake = fake;
    }

    @NonNull
    public String getBaseUrl() {
        return BaseUrlNormalizer.normalize(baseUrl);
    }

    public boolean isProEdition() {
        return "PRO".equals(edition);
    }

    @NonNull
    public String getEdition() {
        return edition;
    }

    @NonNull
    public String getVersion() {
        return version;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JasperServer)) return false;

        JasperServer server = (JasperServer) o;

        if (!baseUrl.equals(server.baseUrl)) return false;
        if (!edition.equals(server.edition)) return false;
        return version.equals(server.version);

    }

    @Override
    public final int hashCode() {
        int result = baseUrl.hashCode();
        result = 31 * result + edition.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "JasperServer{" +
                "baseUrl='" + baseUrl + '\'' +
                ", edition='" + edition + '\'' +
                ", fake=" + fake +
                ", version=" + version +
                '}';
    }

    public boolean isFake() {
        return fake;
    }

    public static JasperServer createFake() {
        return new JasperServer("", "CE", "5.5", true);
    }

    public static class Builder {
        private String mBaseUrl;
        private String mEdition;
        private String mVersion;

        public Builder setBaseUrl(String baseUrl) {
            mBaseUrl = baseUrl;
            return this;
        }

        public Builder setEdition(String edition) {
            mEdition = edition;
            return this;
        }

        public Builder setVersion(String version) {
            mVersion = version;
            return this;
        }

        public JasperServer create() {
            if (mEdition == null) {
                throw new NullPointerException("Edition should not be null");
            }
            if (mVersion == null) {
                throw new NullPointerException("Edition should not be null");
            }
            if (mBaseUrl == null) {
                throw new NullPointerException("Edition should not be null");
            }
            return new JasperServer(mBaseUrl, mEdition, mVersion, false);
        }
    }
}
