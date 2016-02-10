package com.jaspersoft.android.jaspermobile.activities.info.fragments;

import android.view.Menu;
import android.view.MenuItem;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.domain.ResourceDetailsRequest;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.widget.InfoView;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import rx.Subscriber;
import timber.log.Timber;

/**
 * @author Andrew Tivodar
 * @since 2.2
 */
@OptionsMenu(R.menu.favorite_menu)
@EFragment(R.layout.fragment_resource_info)
public class ResourceInfoFragment extends SimpleInfoFragment {

    @ViewById(R.id.infoDetailsView)
    protected InfoView infoView;

    @OptionsMenuItem(R.id.favoriteAction)
    protected MenuItem favoriteAction;

    protected ResourceLookup mResourceLookup;

    @AfterViews
    protected void requestInfo() {
        ResourceLookup.ResourceType resourceType = mJasperResourceConverter.convertToResourceType(jasperResource);
        ResourceDetailsRequest resource = new ResourceDetailsRequest(jasperResource.getId(), resourceType.name());
        mGetResourceDetailsByTypeCase.execute(resource, new GetResourceDescriptorListener());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        favoriteAction.setVisible(mResourceLookup != null);
        favoriteHelper.updateFavoriteIconState(favoriteAction, jasperResource.getId());
    }

    @OptionsItem(R.id.favoriteAction)
    final void favoriteAction() {
        favoriteHelper.switchFavoriteState(mResourceLookup, favoriteAction);
    }

    protected void onDataObtain() {
        jasperResource.setLabel(mResourceLookup.getLabel());
        fillWithData();
        updateHeaderViewLabel(mResourceLookup.getLabel());
        getActivity().invalidateOptionsMenu();
    }

    private void fillWithData() {
        infoView.fillWithBaseData(
                mResourceLookup.getResourceType().name(),
                mResourceLookup.getLabel(),
                mResourceLookup.getDescription(),
                mResourceLookup.getUri(),
                mResourceLookup.getCreationDate(),
                mResourceLookup.getUpdateDate(),
                String.valueOf(mResourceLookup.getVersion()),
                mResourceLookup.getPermissionMask()
        );
    }

    private class GetResourceDescriptorListener extends Subscriber<ResourceLookup> {
        @Override
        public void onCompleted() {
            ProgressDialogFragment.dismiss(getFragmentManager());
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "ResourceInfoFragment#GetResourceDescriptorListener failed");
            RequestExceptionHandler.handle(e, getContext());
            getActivity().finish();
        }

        @Override
        public void onNext(ResourceLookup lookup) {
            mResourceLookup = lookup;
            onDataObtain();
        }
    }
}
