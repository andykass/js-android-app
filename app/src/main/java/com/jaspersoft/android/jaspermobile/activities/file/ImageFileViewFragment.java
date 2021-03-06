package com.jaspersoft.android.jaspermobile.activities.file;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.EFragment;

import java.io.File;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@EFragment(R.layout.fragment_image_open)
public class ImageFileViewFragment extends FileLoadFragment {

    protected ImageView resourceImage;
    protected TextView errorText;

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resourceImage = (ImageView) view.findViewById(R.id.resourceImage);
        errorText = (TextView) view.findViewById(R.id.error_text);

        loadFile();
    }

    @Override
    protected void onFileReady(File file) {
        showImage(file);
    }

    @Override
    protected void showErrorMessage() {
        errorText.setVisibility(View.VISIBLE);
    }

    private void showImage(File file) {
        if (file == null) {
            showErrorMessage();
            return;
        }

        String decodedImgUri = Uri.fromFile(file).toString();
        ImageLoader.getInstance().displayImage(decodedImgUri, resourceImage);
    }
}
