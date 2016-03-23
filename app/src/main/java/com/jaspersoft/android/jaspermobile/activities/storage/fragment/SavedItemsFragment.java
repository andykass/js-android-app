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

package com.jaspersoft.android.jaspermobile.activities.storage.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.info.ResourceInfoActivity_;
import com.jaspersoft.android.jaspermobile.activities.save.SaveReportService;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.SavedReportHtmlViewerActivity_;
import com.jaspersoft.android.jaspermobile.db.database.table.SavedItemsTable;
import com.jaspersoft.android.jaspermobile.db.provider.JasperMobileDbProvider;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;
import com.jaspersoft.android.jaspermobile.util.JasperSettings;
import com.jaspersoft.android.jaspermobile.util.ViewType;
import com.jaspersoft.android.jaspermobile.util.filtering.StorageResourceFilter;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceAdapter;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceConverter;
import com.jaspersoft.android.jaspermobile.util.sorting.SortOrder;
import com.jaspersoft.android.jaspermobile.widget.JasperRecyclerView;
import com.jaspersoft.android.sdk.util.FileUtils;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.UiThread;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment(R.layout.fragment_resource)
public class SavedItemsFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = SavedItemsFragment.class.getSimpleName();
    private final int SAVED_ITEMS_LOADER_ID = 10;

    @FragmentArg
    protected ViewType viewType;

    @Inject
    protected Analytics analytics;
    @Inject
    protected Profile mProfile;
    @Inject
    protected JasperResourceConverter jasperResourceConverter;

    protected JasperRecyclerView listView;
    protected TextView emptyText;

    @FragmentArg
    @InstanceState
    protected String searchQuery;

    @FragmentArg
    @InstanceState
    protected SortOrder sortOrder;

    @Bean
    protected StorageResourceFilter storageResourceFilter;

    private JasperResourceAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseActivityComponent().inject(this);

        analytics.setScreenName(Analytics.ScreenName.SAVED_ITEMS.getValue());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (JasperRecyclerView) view.findViewById(android.R.id.list);
        emptyText = (TextView) view.findViewById(android.R.id.empty);

        setEmptyText(0);
        setDataAdapter();

        getActivity().getSupportLoaderManager().restartLoader(SAVED_ITEMS_LOADER_ID, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Analytics.Dimension> viewDimension = new ArrayList<>();
        viewDimension.add(new Analytics.Dimension(Analytics.Dimension.FILTER_TYPE_HIT_KEY, storageResourceFilter.getCurrent().getName()));
        viewDimension.add(new Analytics.Dimension(Analytics.Dimension.RESOURCE_VIEW_HIT_KEY, viewType.name()));
        analytics.sendScreenView(Analytics.ScreenName.SAVED_ITEMS.getValue(), viewDimension);
    }

    @UiThread
    protected void setEmptyText(int resId) {
        if (resId == 0) {
            emptyText.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText(resId);
        }
    }

    public void showSavedItemsByFilter() {
        analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.FILTERED.getValue(), storageResourceFilter.getCurrent().getName());

        getActivity().getSupportLoaderManager().restartLoader(SAVED_ITEMS_LOADER_ID, null, this);
    }

    public void showSavedItemsBySortOrder(SortOrder selectedSortOrder) {
        analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.SORTED.getValue(), sortOrder.name());

        sortOrder = selectedSortOrder;
        getActivity().getSupportLoaderManager().restartLoader(SAVED_ITEMS_LOADER_ID, null, this);
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void openReportFile(File reportOutputFile, String recordUri) {
        Locale current = getResources().getConfiguration().locale;
        String fileName = reportOutputFile.getName();
        String baseName = FileUtils.getBaseName(fileName);
        String extension = FileUtils.getExtension(fileName).toLowerCase(current);

        if ("HTML".equalsIgnoreCase(extension)) {
            // run the html report viewer
            SavedReportHtmlViewerActivity_.intent(this)
                    .reportFile(reportOutputFile)
                    .resourceLabel(baseName)
                    .recordUri(recordUri)
                    .start();
        } else {
            // run external viewer according to the file format
            String contentType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            Uri reportOutputPath = Uri.fromFile(reportOutputFile);
            Intent externalViewer = new Intent(Intent.ACTION_VIEW);
            externalViewer.setDataAndType(reportOutputPath, contentType);
            externalViewer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                startActivity(externalViewer);
            } catch (ActivityNotFoundException e) {
                // show notification if no app available to open selected format
                Toast.makeText(getActivity(),
                        getString(R.string.sdr_t_no_app_available, extension), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showFileNotReadyMessage() {
        Toast.makeText(getActivity(), getString(R.string.sdr_loading_msg), Toast.LENGTH_SHORT).show();
    }

    private void setDataAdapter() {
        mAdapter = new JasperResourceAdapter(getActivity());
        mAdapter.setOnItemInteractionListener(new JasperResourceAdapter.OnResourceInteractionListener() {
            @Override
            public void onResourceItemClicked(JasperResource jasperResource) {
                String id = jasperResource.getId();
                if (isDownloading(id)) {
                    File file = jasperResourceConverter.convertToFile(id, getActivity());
                    openReportFile(file, id);
                } else {
                    showFileNotReadyMessage();
                }
            }

            @Override
            public void onSecondaryActionClicked(JasperResource jasperResource) {
                if (isDownloading(jasperResource.getId())) {
                    ResourceInfoActivity_.intent(getActivity())
                            .jasperResource(jasperResource)
                            .start();
                } else {
                    SaveReportService.cancel(getContext(), Uri.parse(jasperResource.getId()));
                }
            }
        });

        listView.setAdapter(mAdapter);
        listView.changeViewType(viewType);
    }

    private boolean isDownloading(String id) {
        Cursor cursor = getActivity().getContentResolver().query(Uri.parse(id), null, null, null, null);
        cursor.moveToFirst();
        boolean downloaded = cursor.getInt(cursor.getColumnIndex(SavedItemsTable.DOWNLOADED)) == 1;
        cursor.close();
        return downloaded;
    }

    //---------------------------------------------------------------------
    // Implements LoaderManager.LoaderCallbacks<Cursor>
    //---------------------------------------------------------------------

    @Override
    public Loader<Cursor> onCreateLoader(int code, Bundle bundle) {
        StringBuilder selection = new StringBuilder("");
        ArrayList<String> selectionArgs = new ArrayList<String>();

        //Add general items (server id = -1)
        selection
                .append("(")
                .append(SavedItemsTable.ACCOUNT_NAME + " =?")
                .append("  OR ");

        selectionArgs.add(JasperSettings.RESERVED_ACCOUNT_NAME);

        //Add server profile id to WHERE params
        selection.append(SavedItemsTable.ACCOUNT_NAME + " =?");
        selectionArgs.add(mProfile.getKey());

        // Close select brackets
        selection.append(")");

        //Add filtration to WHERE params
        selection.append(" AND (");

        Iterator<String> iterator = storageResourceFilter.getCurrent().getValues().iterator();
        while (iterator.hasNext()) {
            selection.append(SavedItemsTable.FILE_FORMAT + " =?");
            selectionArgs.add(iterator.next());
            if (iterator.hasNext()) {
                selection.append(" OR ");
            }
        }

        selection.append(")");

        //Add sorting type to WHERE params
        String sortOrderString;
        if (sortOrder != null && sortOrder.getValue().equals(SortOrder.CREATION_DATE.getValue())) {
            sortOrderString = SavedItemsTable.CREATION_TIME + " DESC";
        } else {
            sortOrderString = SavedItemsTable.NAME + " COLLATE NOCASE ASC";
        }

        //Add search query to WHERE params
        boolean inSearchMode = searchQuery != null;
        if (inSearchMode) {
            selection.append(" AND ")
                    .append(SavedItemsTable.NAME + " LIKE ?");
            selectionArgs.add("%" + searchQuery + "%");
        }

        return new CursorLoader(getActivity(), JasperMobileDbProvider.SAVED_ITEMS_CONTENT_URI,
                SavedItemsTable.ALL_COLUMNS, selection.toString(),
                selectionArgs.toArray(new String[selectionArgs.size()]), sortOrderString);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.clear();
        mAdapter.addAll(jasperResourceConverter.convertToJasperResource(cursor));
        mAdapter.notifyDataSetChanged();

        if (cursor.getCount() > 0) {
            setEmptyText(0);
        } else {
            setEmptyText(searchQuery == null ? R.string.sdr_ab_list_msg : R.string.resources_not_found);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }
}
