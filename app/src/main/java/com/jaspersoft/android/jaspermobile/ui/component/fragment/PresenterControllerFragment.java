package com.jaspersoft.android.jaspermobile.ui.component.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jaspersoft.android.jaspermobile.ui.component.presenter.Presenter;
import com.jaspersoft.android.jaspermobile.ui.component.presenter.PresenterControllerDelegate;


public abstract class PresenterControllerFragment<C, P extends Presenter>
        extends ComponentControllerFragment<C> {
    private PresenterControllerDelegate<P> presenterDelegate = new PresenterControllerDelegate<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenterDelegate.onCreate(getPresenter(), savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenterDelegate.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        presenterDelegate.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenterDelegate.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenterDelegate.onDestroy();
    }

    public abstract P getPresenter();
}
