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
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.ui.model.ProfileViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * @author Andrew Tivodar
 * @since 2.0
 */
@EViewGroup(R.layout.view_navigation_panel)
public class NavigationPanelLayout extends RelativeLayout {

    private final String TAG = NavigationPanelLayout.class.getName();

    private NavigationListener mListener;
    private View selectedItemView;
    boolean isShowingMenu;

    @ViewById(R.id.vg_navigation_menu)
    ViewGroup navigationMenu;

    @ViewById(R.id.lv_accounts_menu)
    ListView accountsMenu;

    @ViewById(R.id.tv_profile)
    TextView tvProfile;

    @ViewById(R.id.iv_profile_arrow)
    ImageView ivProfileArrow;

    private View footerDivider;
    private Analytics analytics;
    private ProfilesAdapter mProfilesAdapter;

    //---------------------------------------------------------------------
    // Public methods
    //---------------------------------------------------------------------

    public NavigationPanelLayout(Context context) {
        super(context);
    }

    public NavigationPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationPanelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAnalytics(Analytics analytics) {
        this.analytics = analytics;
    }

    public void setListener(NavigationListener listener) {
        this.mListener = listener;
    }

    public void setItemSelected(int viewId) {
        selectItem(findViewById(viewId));
    }

    public void notifyPanelClosed() {
        isShowingMenu = true;
        showActivatedPanel(isShowingMenu);
        accountsMenu.setSelectionAfterHeaderView();
        ((ScrollView) navigationMenu).fullScroll(FOCUS_UP);
    }

    @AfterViews
    final void initNavigationLayout() {
        Timber.tag(TAG);
        isShowingMenu = true;
        View accountsFooter = LayoutInflater.from(getContext()).inflate(R.layout.view_accounts_footer, null, false);
        accountsFooter.findViewById(R.id.vg_add_account).setOnClickListener(onAddProfileClickListener);
        accountsFooter.findViewById(R.id.vg_manage_accounts).setOnClickListener(onManageProfileClickListener);
        footerDivider = accountsFooter.findViewById(R.id.footer_divider);
        accountsMenu.addFooterView(accountsFooter);
        initAccountsView();
    }

    private void initAccountsView() {
        mProfilesAdapter = new ProfilesAdapter();
        accountsMenu.setAdapter(mProfilesAdapter);
    }

    /**
     * Calculation width of Left Panel Menu. The size should be minimum of 2 values: screen width - ab size or 320dp.
     * NOTE: http://www.google.com/design/spec/patterns/navigation-drawer.html But minimum size(5 * ab size) is incorrect.
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = Math.min(dm.widthPixels, dm.heightPixels);
        int toolbarHeight = (int) getResources().getDimension(android.support.v7.appcompat.R.dimen.abc_action_bar_default_height_material);

        getLayoutParams().width = (int) Math.min(screenWidth - toolbarHeight, 320 * dm.scaledDensity);
    }

    private OnClickListener onAddProfileClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            navigateTo(v.getId());
        }
    };

    private OnClickListener onManageProfileClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            navigateTo(v.getId());
        }
    };

    @Click(R.id.vg_profile)
    final void profilePanelClicked() {
        isShowingMenu = !isShowingMenu;
        showActivatedPanel(isShowingMenu);
    }

    @Click(R.id.tv_feedback)
    final void navigateToFeedback(View view) {
        navigateTo(view.getId());
        trackNavigateEvent(Analytics.EventLabel.FEEDBACK.getValue());
    }

    @Click(R.id.tv_settings)
    final void navigateToSettings(View view) {
        navigateTo(view.getId());
        trackNavigateEvent(Analytics.EventLabel.SETTINGS.getValue());
    }

    @Click(R.id.tv_about)
    final void navigateToAbout(View view) {
        navigateTo(view.getId());
        trackNavigateEvent(Analytics.EventLabel.ABOUT.getValue());
    }

    @Click(R.id.vg_library)
    final void navigateToLibrary(View newSelectItem) {
        selectItem(newSelectItem);
        trackNavigateEvent(Analytics.EventLabel.LIBRARY.getValue());
    }

    @Click(R.id.vg_repository)
    final void navigateToRepository(View newSelectItem) {
        selectItem(newSelectItem);
        trackNavigateEvent(Analytics.EventLabel.REPOSITORY.getValue());
    }

    @Click(R.id.vg_favorites)
    final void navigateToFavorites(View newSelectItem) {
        selectItem(newSelectItem);
        trackNavigateEvent(Analytics.EventLabel.FAVORITES.getValue());
    }

    @Click(R.id.vg_saved_items)
    final void navigateToSavedItems(View newSelectItem) {
        selectItem(newSelectItem);
        trackNavigateEvent(Analytics.EventLabel.SAVED_ITEMS.getValue());
    }

    @Click(R.id.vg_recent)
    final void navigateToRecent(View newSelectItem) {
        selectItem(newSelectItem);
        trackNavigateEvent(Analytics.EventLabel.RECENTLY_VIEWED.getValue());
    }

    @Click(R.id.vg_jobs)
    final void navigateToJobs(View newSelectItem) {
        selectItem(newSelectItem);
        trackNavigateEvent(Analytics.EventLabel.JOBS.getValue());
    }

    @ItemClick(R.id.lv_accounts_menu)
    public void onAccountSelect(ProfileViewModel profile) {
        if (mListener != null) {
            mListener.onActiveProfileChange(profile);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        NavigationPanelSavedState savedState = new NavigationPanelSavedState(superState);
        savedState.isShowingMenu = this.isShowingMenu;
        savedState.selectedViewId = selectedItemView != null ? selectedItemView.getId() : -1;

        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof NavigationPanelSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        NavigationPanelSavedState savedState = (NavigationPanelSavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        this.isShowingMenu = savedState.isShowingMenu;
        showActivatedPanel(isShowingMenu);

        if (savedState.selectedViewId != -1) {
            this.selectedItemView = findViewById(savedState.selectedViewId);
            setItemSelected(selectedItemView, true);
        }
    }

    public void loadProfiles(@NonNull List<ProfileViewModel> profiles) {
        boolean hasProfiles = !profiles.isEmpty();
        footerDivider.setVisibility(hasProfiles ? VISIBLE : GONE);

        ProfileViewModel activeProfile = selectActiveProfile(profiles);
        setActiveProfile(activeProfile);

        profiles.remove(activeProfile);
        mProfilesAdapter.setProfiles(profiles);
    }

    private ProfileViewModel selectActiveProfile(List<ProfileViewModel> profiles) {
        for (ProfileViewModel profile : profiles) {
            if (profile.isActive()) {
                return profile;
            }
        }
        return ProfileViewModel.getEmpty();
    }

    private void setActiveProfile(@NonNull ProfileViewModel profile) {
        if (ProfileViewModel.getEmpty().equals(profile)) {
            tvProfile.setText(getContext().getString(R.string.nd_select_account));
        } else {
            tvProfile.setText(profile.getLabel());
        }
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void selectItem(View newSelectItem) {
        if (selectedItemView != null) {
            setItemSelected(selectedItemView, false);
        }
        setItemSelected(newSelectItem, true);
        selectedItemView = newSelectItem;

        navigateTo(newSelectItem.getId());
    }

    private void navigateTo(int viewId) {
        if (mListener != null) {
            mListener.onNavigate(viewId);
        }
    }

    private void showActivatedPanel(boolean isShowingMenu) {
        if (!isShowingMenu) {
            ivProfileArrow.setImageResource(R.drawable.ic_arrow_up);
            navigationMenu.setVisibility(GONE);
            accountsMenu.setVisibility(VISIBLE);
            ((ScrollView) navigationMenu).fullScroll(FOCUS_UP);
        } else {
            ivProfileArrow.setImageResource(R.drawable.ic_arrow_down);
            navigationMenu.setVisibility(VISIBLE);
            accountsMenu.setVisibility(GONE);
            accountsMenu.setSelectionAfterHeaderView();
        }
    }

    private void setItemSelected(View item, boolean selected) {
        item.setSelected(selected);
        try {
            ViewGroup itemGroup = ((ViewGroup) item);

            for (int i = 0; i < itemGroup.getChildCount(); i++) {
                View view = itemGroup.getChildAt(i);
                view.setSelected(selected);
            }
        } catch (ClassCastException e) {
            Timber.w(TAG, "Selected navigation item is not a layout.");
        }
    }

    private void trackNavigateEvent(String location) {
        analytics.sendEvent(
                Analytics.EventCategory.MENU.getValue(),
                Analytics.EventAction.CLICKED.getValue(),
                location
        );
    }

    //---------------------------------------------------------------------
    // Nested classes
    //---------------------------------------------------------------------

    private static class ProfilesAdapter extends BaseAdapter {
        private final List<ProfileViewModel> mProfiles = new ArrayList<>();

        public void setProfiles(List<ProfileViewModel> profiles) {
            mProfiles.clear();
            mProfiles.addAll(profiles);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mProfiles.size();
        }

        @Override
        public ProfileViewModel getItem(int position) {
            return mProfiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AccountsViewHolder mViewHolder;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.item_account, null);
                mViewHolder = new AccountsViewHolder();
                mViewHolder.tvAccountName = (TextView) convertView.findViewById(R.id.tv_account_name);
                mViewHolder.tvAccountVersion = (TextView) convertView.findViewById(R.id.tv_account_version);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (AccountsViewHolder) convertView.getTag();
            }

            ProfileViewModel item = getItem(position);
            mViewHolder.tvAccountName.setText(item.getLabel());

            //We need to show only 2 digits of version
            String serverVersion = new String(item.getVersion().substring(0, 3));
            // This possible due to migration issues from version 1.8 to 2.0
            // Some instances will have missing version names
            boolean versionNotDefined = Double.parseDouble(serverVersion) == 0d;
            mViewHolder.tvAccountVersion.setText(versionNotDefined ? "?" : serverVersion);

            return convertView;
        }

        private class AccountsViewHolder {
            TextView tvAccountName;
            TextView tvAccountVersion;
        }
    }

    static class NavigationPanelSavedState extends BaseSavedState {
        int selectedViewId;
        boolean isShowingMenu;

        NavigationPanelSavedState(Parcelable superState) {
            super(superState);
        }

        private NavigationPanelSavedState(Parcel in) {
            super(in);
            selectedViewId = in.readInt();
            isShowingMenu = in.readInt() != 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(selectedViewId);
            out.writeInt(isShowingMenu ? 1 : 0);
        }

        public static final Parcelable.Creator<NavigationPanelSavedState> CREATOR =
                new Parcelable.Creator<NavigationPanelSavedState>() {
                    public NavigationPanelSavedState createFromParcel(Parcel in) {
                        return new NavigationPanelSavedState(in);
                    }

                    public NavigationPanelSavedState[] newArray(int size) {
                        return new NavigationPanelSavedState[size];
                    }
                };
    }

    public interface NavigationListener {
        /**
         * @param viewId returns selected view Id or 0 for same view
         */
        void onNavigate(int viewId);

        void onActiveProfileChange(ProfileViewModel profile);
    }

}
