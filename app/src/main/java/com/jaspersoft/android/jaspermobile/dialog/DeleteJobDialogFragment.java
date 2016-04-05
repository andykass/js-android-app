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

package com.jaspersoft.android.jaspermobile.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;

/**
 * @author Andrew Tivodar
 * @since 2.0
 */
public class DeleteJobDialogFragment extends SimpleDialogFragment {

    private final static String JOB_ID_ARG = "job_id_arg";
    private int mJobId;

    @Override
    protected Class<DeleteJobDialogClickListener> getDialogCallbackClass() {
        return DeleteJobDialogClickListener.class;
    }

    @Override
    protected void initDialogParams() {
        super.initDialogParams();

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(JOB_ID_ARG)) {
                mJobId = args.getInt(JOB_ID_ARG);
            }
        }
    }

    @Override
    protected void onNegativeClick() {
    }

    @Override
    protected void onPositiveClick() {
        ((DeleteJobDialogClickListener) mDialogListener).onDeleteConfirmed(mJobId);
    }

    public static DeleteJobDialogFragmentBuilder createBuilder(Context context, FragmentManager fragmentManager) {
        return new DeleteJobDialogFragmentBuilder(context, fragmentManager);
    }

    //---------------------------------------------------------------------
    // Dialog Builder
    //---------------------------------------------------------------------

    public static class DeleteJobDialogFragmentBuilder extends SimpleDialogFragmentBuilder<DeleteJobDialogFragment> {

        public DeleteJobDialogFragmentBuilder(Context context, FragmentManager fragmentManager) {
            super(context, fragmentManager);
        }

        public DeleteJobDialogFragmentBuilder setJobId(int jobId) {
            args.putInt(JOB_ID_ARG, jobId);
            return this;
        }

        @Override
        public DeleteJobDialogFragment build() {
            return new DeleteJobDialogFragment();
        }
    }

    //---------------------------------------------------------------------
    // Dialog Callback
    //---------------------------------------------------------------------

    public interface DeleteJobDialogClickListener extends DialogClickListener {
        void onDeleteConfirmed(int jobId);
    }

}
