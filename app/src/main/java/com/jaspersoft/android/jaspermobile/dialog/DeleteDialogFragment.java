/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
 *  http://community.jaspersoft.com/project/jaspermobile-android
 *
 *  Unless you have purchased a commercial license agreement from Jaspersoft,
 *  the following license terms apply:
 *
 *  This program is part of Jaspersoft Mobile for Android.
 *
 *  Jaspersoft Mobile is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Jaspersoft Mobile is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Jaspersoft Mobile for Android. If not, see
 *  <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.dialog;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import java.io.File;

/**
 * @author Andrew Tivodar
 * @since 2.0
 */
public class DeleteDialogFragment extends SimpleDialogFragment {

    private final static String RECORD_URI_ARG = "record_uri";
    private final static String FILE_URI_ARG = "file_uri";
    private Uri recordUri;
    private File itemFile;

    @Override
    protected Class<DeleteDialogClickListener> getDialogCallbackClass() {
        return DeleteDialogClickListener.class;
    }

    @Override
    protected void initDialogParams() {
        super.initDialogParams();

        Bundle args = getArguments();
        if (args!= null) {
            if (args.containsKey(RECORD_URI_ARG)) {
                recordUri = args.getParcelable(RECORD_URI_ARG);
            }
            if (args.containsKey(FILE_URI_ARG)) {
                Uri fileUri = args.getParcelable(FILE_URI_ARG);
                itemFile = new File(fileUri.getPath());
            }
        }
    }

    @Override
    protected void onNegativeClick() {
        ((DeleteDialogClickListener) mDialogListener).onDeleteCanceled();
    }

    @Override
    protected void onPositiveClick() {
        ((DeleteDialogClickListener) mDialogListener).onDeleteConfirmed(recordUri, itemFile);
    }

    public static DeleteDialogFragmentBuilder createBuilder(Context context, FragmentManager fragmentManager) {
        return new DeleteDialogFragmentBuilder(context, fragmentManager);
    }

    //---------------------------------------------------------------------
    // Dialog Builder
    //---------------------------------------------------------------------

    public static class DeleteDialogFragmentBuilder extends SimpleDialogFragmentBuilder<DeleteDialogFragment> {

        public DeleteDialogFragmentBuilder(Context context, FragmentManager fragmentManager) {
            super(context, fragmentManager);
        }

        public DeleteDialogFragmentBuilder setRecordUri(Uri recordUri) {
            args.putParcelable(RECORD_URI_ARG, recordUri);
            return this;
        }

        public DeleteDialogFragmentBuilder setFile(File file) {
            args.putParcelable(FILE_URI_ARG, Uri.fromFile(file));
            return this;
        }

        @Override
        public DeleteDialogFragment build() {
            return new DeleteDialogFragment();
        }
    }

    //---------------------------------------------------------------------
    // Dialog Callback
    //---------------------------------------------------------------------

    public interface DeleteDialogClickListener extends DialogClickListener{
        public void onDeleteConfirmed(Uri itemToDelete, File fileToDelete);
        public void onDeleteCanceled();
    }

}
