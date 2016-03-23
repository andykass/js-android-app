package com.jaspersoft.android.jaspermobile.ui.navigation;

import android.support.annotation.NonNull;


/**
 * @author Tom Koptel
 * @since 2.3
 */
public class FakePageFactory extends PageFactory {

    public FakePageFactory() {
        super(null);
    }

    @NonNull
    @Override
    public Page createMainPage() {
        return new MainPage(null);
    }

    @NonNull
    @Override
    public Page createSignUpPage() {
        return new SignUpPage(null);
    }
}