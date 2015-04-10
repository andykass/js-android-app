package com.jaspersoft.android.jaspermobile.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;

/**
 * @author Tom Koptel
 * @since 1.9
 */
public class AboutDialogFragment extends SimpleDialogFragment implements DialogInterface.OnShowListener {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sa_show_about);
        builder.setMessage(R.string.sa_about_info);
        builder.setNeutralButton(android.R.string.ok, null);

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnShowListener(this);
        return dialog;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        View decorView = getDialog().getWindow().getDecorView();
        if (decorView != null) {
            TextView messageText = (TextView) decorView.findViewById(android.R.id.message);
            if (messageText != null) {
                messageText.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    public static AboutDialogFragmentBuilder createBuilder(Context context, FragmentManager fragmentManager) {
        return new AboutDialogFragmentBuilder(context, fragmentManager);
    }

    public static class AboutDialogFragmentBuilder extends SimpleDialogFragmentBuilder<AboutDialogFragment> {

        public AboutDialogFragmentBuilder(Context context, FragmentManager fragmentManager) {
            super(context, fragmentManager);
        }

        @Override
        public AboutDialogFragment build() {
            return new AboutDialogFragment();
        }
    }
}