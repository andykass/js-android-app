/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.jaspersoft.android.jaspermobile.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author Tom Koptel
 * @since 2.0
 */
public class JasperAuthenticatorService extends Service {
    private JasperAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new JasperAuthenticator(this);
    }

    public JasperAuthenticator getAuthenticator() {
        return mAuthenticator;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
