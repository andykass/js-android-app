package com.jaspersoft.android.jaspermobile.activities.info.fragments;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.info.InfoHeaderView;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.option.GetReportOptionsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.option.LoadControlsForOptionCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.GetResourceDetailsByTypeCase;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;
import com.jaspersoft.android.jaspermobile.util.FavoritesHelper;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceConverter;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.ResourceBinder;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.ResourceBinderFactory;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.2
 */
@EFragment(R.layout.fragment_resource_info)
public class SimpleInfoFragment extends BaseFragment {

    public static final String TAG = ResourceInfoFragment.class.getSimpleName();

    @FragmentArg
    protected JasperResource jasperResource;

    @Inject
    protected Analytics analytics;
    @Inject
    protected JasperServer mJasperServer;
    @Inject
    protected JasperResourceConverter mJasperResourceConverter;
    @Inject
    protected FavoritesHelper favoriteHelper;

    @Inject
    protected GetResourceDetailsByTypeCase mGetResourceDetailsByTypeCase;
    @Inject
    protected GetReportOptionsCase mGetReportOptionsCase;
    @Inject
    protected LoadControlsForOptionCase mLoadControlsForOptionCase;

    protected ImageView toolbarImage;
    protected CollapsingToolbarLayout toolbarLayout;

    private InfoHeaderView infoHeaderView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseActivityComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbarImage = (ImageView) view.findViewById(R.id.toolbarImageView);
        toolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.info_collapsing_toolbar);

        setToolbar(view);
        showHeaderView();

        if (savedInstanceState == null) {
            analytics.sendEvent(
                    Analytics.EventCategory.RESOURCE.getValue(),
                    Analytics.EventAction.INFO_VIEWED.getValue(),
                    jasperResource.getResourceType().name()
            );
        }
    }

    @Override
    public void onDestroyView() {
        mGetResourceDetailsByTypeCase.unsubscribe();
        mGetReportOptionsCase.unsubscribe();
        mLoadControlsForOptionCase.unsubscribe();
        super.onDestroyView();
    }

    final protected void updateHeaderViewLabel(String label) {
        jasperResource.setLabel(label);
        infoHeaderView.setTitle(label);
    }

    private void showHeaderView() {
        ResourceBinderFactory mResourceBinderFactory = new ResourceBinderFactory(getActivity());
        ResourceBinder resourceBinder = mResourceBinderFactory.create(jasperResource.getResourceType());

        infoHeaderView = new InfoHeaderView(toolbarImage, toolbarLayout);
        resourceBinder.bindView(infoHeaderView, jasperResource);
    }

    private void setToolbar(View infoView) {
        Toolbar toolbar = (Toolbar) infoView.findViewById(R.id.toolbar);

        AppCompatActivity actionBarActivity = (AppCompatActivity) getActivity();
        actionBarActivity.setSupportActionBar(toolbar);

        ActionBar actionBar = actionBarActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_close);
        }
    }
}
