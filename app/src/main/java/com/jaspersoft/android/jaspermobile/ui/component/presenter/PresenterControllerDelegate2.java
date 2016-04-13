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

package com.jaspersoft.android.jaspermobile.ui.component.presenter;

public class PresenterControllerDelegate2 {
    private boolean isDestroyedBySystem;
    private final Presenter presenter;

    public PresenterControllerDelegate2(Presenter presenter) {
        this.presenter = presenter;
    }

    public void onResume() {
        presenter.resumeView();
        isDestroyedBySystem = false;
    }

    public void onSaveInstanceState() {
        isDestroyedBySystem = true;
    }

    public void onPause() {
        presenter.pauseView();
    }

    public void onDestroy() {
        if (!isDestroyedBySystem) {
            presenter.unbindView();
        }
    }
}
