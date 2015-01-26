/*
 * Copyright © 2014 TIBCO Software, Inc. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.activities.repository.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.legacy.JsServerProfileCompat;
import com.jaspersoft.android.jaspermobile.widget.TopCropImageView;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.JsServerProfile;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.client.oxm.server.ServerInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import roboguice.RoboGuice;

import static com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup.ResourceType;

/**
 * @author Tom Koptel
 * @since 1.9
 */
public class ResourceViewHelper {
    private static final String TAG = ResourceViewHelper.class.getSimpleName();

    private static final String FIRST_INITIAL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String SECOND_INITIAL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private final SimpleDateFormat[] serverDateFormats;
    private final double mServerVersion;
    private final Context mContext;

    private DisplayImageOptions displayImageOptions;

    @Inject
    JsRestClient jsRestClient;

    public ResourceViewHelper(Context context, double serverVersion) {
        RoboGuice.getInjector(context).injectMembersWithoutViews(this);

        mContext = context;
        mServerVersion = serverVersion;

        Locale current = mContext.getResources().getConfiguration().locale;
        serverDateFormats = new SimpleDateFormat[]{new SimpleDateFormat(FIRST_INITIAL_DATE_FORMAT, current),
                new SimpleDateFormat(SECOND_INITIAL_DATE_FORMAT, current)};
    }

    public void populateView(IResourceView resourceView, ResourceLookup item) {
        setIcon(resourceView, item);

        resourceView.setTitle(item.getLabel());

        resourceView.setSubTitle(item.getDescription());

        if (item.getResourceType() == ResourceType.folder) {
            resourceView.setTimeStamp(formatDateString(item.getCreationDate()));
        }
    }

    public static int getResourceIcon(ResourceType resourceType) {
        switch (resourceType) {
            case folder:
                return R.drawable.sample_repo_blue;
            case legacyDashboard:
            case dashboard:
                return R.drawable.sample_dashboard_blue;
            case reportUnit:
                return R.drawable.sample_report_grey;
            default:
                return R.drawable.js_blue_gradient;
        }
    }

    public static int getResourceBackground(ResourceType resourceType) {
        switch (resourceType) {
            case folder:
                return R.color.dashboard_item_bg;
            case legacyDashboard:
            case dashboard:
                return R.drawable.js_blue_gradient;
            case reportUnit:
                return R.drawable.js_grey_gradient;
            default:
                return R.drawable.js_blue_gradient;
        }
    }

    private void setIcon(IResourceView resourceView, ResourceLookup item) {
        ImageView imageView = resourceView.getImageView();
        int resource = getResourceIcon(item.getResourceType());
        int background = getResourceBackground(item.getResourceType());

        ResourceType currentType = item.getResourceType();
        boolean isAmberOrHigher = mServerVersion >= ServerInfo.VERSION_CODES.AMBER;
        boolean isReport = currentType.equals(ResourceType.reportUnit);
        boolean isDashboard = currentType.equals(ResourceType.dashboard) ||
                currentType.equals(ResourceType.legacyDashboard);

        if (isReport || isDashboard) {
            imageView.setBackgroundResource(background);
            setScaleType(imageView, TopCropImageView.ScaleType.FIT_CENTER);
        } else {
            imageView.setBackgroundResource(background);
            setScaleType(imageView, TopCropImageView.ScaleType.FIT_XY);
        }

        if (isAmberOrHigher && isReport) {
            String path = jsRestClient.generateThumbNailUri(item.getUri());
            ImageLoader.getInstance().displayImage(
                    path, imageView, getDisplayImageOptions(),
                    new ImageLoadingListener()
            );
        } else {
            imageView.setImageResource(resource);
        }
    }

    public DisplayImageOptions getDisplayImageOptions() {
        if (displayImageOptions == null) {
            Map<String, String> headers = Maps.newHashMap();

            JsServerProfileCompat.initLegacyJsRestClient(mContext, jsRestClient);
            JsServerProfile profile = jsRestClient.getServerProfile();
            String authorisation = profile.getUsernameWithOrgId() + ":" + profile.getPassword();
            String encodedAuthorisation = "Basic " + Base64.encodeToString(
                    authorisation.getBytes(), Base64.NO_WRAP);
            headers.put("Authorization", encodedAuthorisation);
            headers.put("Accept", "image/jpeg");

            int animationSpeed = mContext.getResources().getInteger(
                    android.R.integer.config_mediumAnimTime);

            displayImageOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.sample_report_grey)
                    .showImageForEmptyUri(R.drawable.sample_report_grey)
                    .showImageOnFail(R.drawable.sample_report_grey)
                    .considerExifParams(true)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .extraForDownloader(headers)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new FadeInBitmapDisplayer(animationSpeed))
                    .build();
        }

        return displayImageOptions;
    }

    private String formatDateString(String updateDate) {
        if (updateDate == null) return "";

        try {
            Date dateValue = serverDateFormats[0].parse(updateDate);
            DateFormat dateFormat = DateFormat.getDateInstance();
            return dateFormat.format(dateValue);
        } catch (ParseException ex) {
            // ignoring
        }

        try {
            Date dateValue = serverDateFormats[1].parse(updateDate);
            DateFormat dateFormat = DateFormat.getDateInstance();
            return dateFormat.format(dateValue);
        } catch (ParseException ex) {
            Log.w(TAG, "Wrong date format");
        }

        return updateDate;
    }

    private static void setScaleType(View view, TopCropImageView.ScaleType type) {
        TopCropImageView imageView = (TopCropImageView) view;
        if (imageView != null) {
            imageView.setScaleType(type);
        }
    }

    private static class ImageLoadingListener extends SimpleImageLoadingListener {
        @Override
        public void onLoadingStarted(String imageUri, View view) {
            setScaleType(view, TopCropImageView.ScaleType.FIT_CENTER);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            setScaleType(view, TopCropImageView.ScaleType.TOP_CROP);
        }
    }
}
