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

package com.jaspersoft.android.jaspermobile.util;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;

/**
 * Hides details of visualize url creation.
 *
 * @author Tom Koptel
 * @since 2.1
 */
public class VisualizeEndpoint {
    private final String mBaseUrl;
    private final boolean mOptimized;
    private final boolean mShowControls;

    private VisualizeEndpoint(EndpointBuilder endpointBuilder) {
        mBaseUrl = endpointBuilder.baseUrl;
        mOptimized = endpointBuilder.optimized;
        mShowControls = endpointBuilder.showControls;
    }

    /**
     * Creates visualize url.
     *
     * @return absolute url on visualize.js resources
     */
    public String createUri() {
        String serverUrl = BaseUrlNormalizer.denormalize(mBaseUrl);
        Uri.Builder visualizeUriBuilder = Uri.parse(mBaseUrl + "client/visualize.js")
                .buildUpon()
                .appendQueryParameter("_opt", String.valueOf(mOptimized))
                .appendQueryParameter("baseUrl", serverUrl);
        if (mShowControls) {
            visualizeUriBuilder.appendQueryParameter("_showInputControls", String.valueOf(true));
        }
        Uri visualizeUri = visualizeUriBuilder.build();
        return visualizeUri.toString();
    }

    @Override
    public String toString() {
        return "VisualizeEndpoint{" +
                "mBaseUrl='" + mBaseUrl + '\'' +
                ", mOptimized=" + mOptimized +
                '}';
    }

    /**
     * Creates builder or throws exception if baseUrl malformed
     *
     * @param baseUrl should be base url of target JRS server
     * @return build with default values
     */
    public static EndpointBuilder forBaseUrl(String baseUrl) {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new IllegalArgumentException("Base url should not be null");
        }
        boolean isValid = URLUtil.isNetworkUrl(baseUrl);
        if (!isValid) {
            throw new IllegalArgumentException("Url does not considered to be network url: " + baseUrl);
        }

        return new EndpointBuilder(baseUrl);
    }

    public static class EndpointBuilder {
        private final String baseUrl;
        private boolean optimized;
        private boolean showControls;

        private EndpointBuilder(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public EndpointBuilder setOptimized(boolean optimized) {
            this.optimized = optimized;
            return this;
        }

        public EndpointBuilder optimized() {
            this.optimized = true;
            return this;
        }

        public EndpointBuilder showControls() {
            this.showControls = true;
            return this;
        }

        public VisualizeEndpoint build() {
            return new VisualizeEndpoint(this);
        }
    }
}
