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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.favorites.adapter.SingleChoiceAdapterHelper;
import com.jaspersoft.android.jaspermobile.activities.repository.support.ViewType;
import com.jaspersoft.android.jaspermobile.util.FavoritesHelper_;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import java.util.Collection;

import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

import static com.google.common.base.Preconditions.checkNotNull;

public class ResourceAdapter extends SingleChoiceArrayAdapter<ResourceLookup> {
    private final FavoritesHelper_ favoriteHelper;
    private final ResourceViewHelper viewHelper;

    private final ViewType mViewType;
    private MenuItem favoriteActionItem;

    public static Builder builder(Context context, Bundle savedInstanceState) {
        checkNotNull(context);
        return new Builder(context, savedInstanceState);
    }

    private ResourceAdapter(Context context, double serverVersion,
                            Bundle savedInstanceState, ViewType viewType) {
        super(savedInstanceState, context, 0);
        favoriteHelper = FavoritesHelper_.getInstance_(context);
        mViewType = checkNotNull(viewType, "ViewType can`t be null");
        viewHelper = new ResourceViewHelper(context, serverVersion);
    }

    @Override
    protected View getViewImpl(int position, View convertView, ViewGroup parent) {
        IResourceView itemView = (IResourceView) convertView;

        if (itemView == null) {
            if (mViewType == ViewType.LIST) {
                itemView = ListItemView_.build(getContext());
            } else {
                itemView = GridItemView_.build(getContext());
            }
        }

        viewHelper.populateView(itemView, getItem(position));
        return (View) itemView;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.am_resource_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        favoriteActionItem = menu.findItem(R.id.favoriteAction);
        if (getCount() > 0) {
            alterFavoriteIcon();
            return true;
        }
        return false;
    }

    @Override
    public void addAll(Collection<? extends ResourceLookup> collection) {
        super.addAll(collection);
        // Because of rotation we are loosing content of adapter. For that
        // reason we are altering ActionMode icon if it visible state to
        // the required value.
        if (favoriteActionItem != null && collection.size() > 0
                && getCurrentPosition() != SingleChoiceAdapterHelper.NO_POSITION) {
            alterFavoriteIcon();
        }
    }

    @Override
    public void clear() {
        super.clear();
        resetCurrentPosition();
    }

    private void alterFavoriteIcon() {
        ResourceLookup resource = getItem(getCurrentPosition());
        Cursor cursor = favoriteHelper.queryFavoriteByResource(resource);

        try {
            boolean alreadyFavorite = (cursor.getCount() > 0);
            favoriteActionItem.setIcon(alreadyFavorite ? R.drawable.ic_rating_favorite : R.drawable.ic_rating_not_favorite);
            favoriteActionItem.setTitle(alreadyFavorite ? R.string.r_cm_remove_from_favorites : R.string.r_cm_add_to_favorites);
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        ResourceLookup resource = getItem(getCurrentPosition());
        switch (item.getItemId()) {
            case R.id.favoriteAction:
                Uri uri = favoriteHelper.queryFavoriteUri(resource);
                favoriteHelper.handleFavoriteMenuAction(uri, resource, null);
                break;
            case R.id.showAction:
                FragmentManager fm = ((FragmentActivity) getContext()).getSupportFragmentManager();
                SimpleDialogFragment.createBuilder(getContext(), fm)
                        .setTitle(resource.getLabel())
                        .setMessage(resource.getDescription())
                        .setNegativeButtonText(android.R.string.ok)
                        .show();
                break;
        }
        mode.invalidate();
        return true;
    }

    public void sortByType() {
        super.sort(new OrderingByType());
    }

    private static class OrderingByType extends Ordering<ResourceLookup> {
        @Override
        public int compare(ResourceLookup res1, ResourceLookup res2) {
            ResourceLookup.ResourceType resType1 = res1.getResourceType();
            ResourceLookup.ResourceType resType2 = res2.getResourceType();
            return Ints.compare(resType1.ordinal(), resType2.ordinal());
        }
    }

    public static class Builder {
        private final Context context;
        private final Bundle savedInstanceState;

        private ViewType viewType;
        private double mServerVersion;

        public Builder(Context context, Bundle savedInstanceState) {
            this.context = context;
            this.savedInstanceState = savedInstanceState;
        }

        public Builder viewType(ViewType viewType) {
            this.viewType = viewType;
            return this;
        }

        public Builder serverVersion(double version) {
            mServerVersion = version;
            return this;
        }

        public ResourceAdapter create() {
            return new ResourceAdapter(context, mServerVersion, savedInstanceState, viewType);
        }
    }
}
