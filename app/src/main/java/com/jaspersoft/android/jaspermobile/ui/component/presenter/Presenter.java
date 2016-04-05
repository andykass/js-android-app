package com.jaspersoft.android.jaspermobile.ui.component.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface Presenter<T> {
    void onCreate(@Nullable PresenterBundle bundle);
    void onSaveInstanceState(@NonNull PresenterBundle bundle);
    void onDestroy();
    void onBindView(T view);
    void bindView(T view);
    void unbindView();
}
