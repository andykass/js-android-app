package com.jaspersoft.android.jaspermobile.ui.component.presenter;

import android.os.Bundle;

public class PresenterControllerDelegate<P extends Presenter> {
    private boolean isDestroyedBySystem;
    private P presenter;

    public void onCreate(P presenter, Bundle savedInstanceState) {
        this.presenter = presenter;
        PresenterBundle bundle = PresenterBundleUtils.getPresenterBundle(savedInstanceState);
        presenter.onCreate(bundle);
    }

    public void onResume() {
        isDestroyedBySystem = false;
    }

    public void onSaveInstanceState(Bundle outState) {
        isDestroyedBySystem = true;
        PresenterBundle bundle = new PresenterBundle();
        presenter.onSaveInstanceState(bundle);
        PresenterBundleUtils.setPresenterBundle(outState, bundle);
    }

    public void onDestroyView() {
        presenter.pauseView();
    }

    public void onDestroy() {
        if (!isDestroyedBySystem) {
            presenter.unbindView();
        }
    }
}
