package com.jaspersoft.android.jaspermobile.util.resource.viewbinder;

import android.content.Context;
import android.widget.ImageView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.ResourceDetailsRequest;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.GetResourceDetailsByTypeCase;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.ComponentProviderDelegate;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.sdk.client.oxm.resource.FileLookup;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import javax.inject.Inject;

import rx.Subscription;


/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class FileResourceBinder extends ResourceBinder {

    @Inject
    protected GetResourceDetailsByTypeCase mGetResourceDetailsByTypeCase;

    public FileResourceBinder(Context context) {
        super(context);
        ComponentProviderDelegate.INSTANCE
                .getProfileComponent(context)
                .inject(this);
    }

    @Override
    public void setIcon(ImageView imageView, JasperResource jasperResource) {
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBackgroundResource(R.drawable.bg_resource_icon_grey);
        imageView.setImageResource(R.drawable.ic_file);

        if (jasperResource instanceof FileResource) {
            loadFileType(imageView, ((FileResource) jasperResource).getFileUri());
        }
    }

    @Override
    public void setThumbnail(ImageView imageView, JasperResource jasperResource) {
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setBackgroundResource(R.drawable.bg_gradient_grey);
        imageView.setImageResource(R.drawable.ic_file);

        if (jasperResource instanceof FileResource) {
            loadFileType(imageView, ((FileResource) jasperResource).getFileUri());
        }
    }

    private void loadFileType(final ImageView imageView, String uri) {
        ResourceDetailsRequest request = new ResourceDetailsRequest(uri, "file");
        Subscription subscription = mGetResourceDetailsByTypeCase.execute(request, new SimpleSubscriber<ResourceLookup>() {
            @Override
            public void onNext(ResourceLookup resourceLookup) {
                FileLookup fileLookup = (FileLookup) resourceLookup;
                FileLookup.FileType fileType = fileLookup.getFileType();
                int resId;
                switch (fileType) {
                    case csv:
                        resId = R.drawable.ic_file_csv;
                        break;
                    case docx:
                        resId = R.drawable.ic_file_doc;
                        break;
                    case html:
                        resId = R.drawable.ic_file_html;
                        break;
                    case img:
                        resId = R.drawable.ic_file_img;
                        break;
                    case json:
                        resId = R.drawable.ic_file_json;
                        break;
                    case ods:
                        resId = R.drawable.ic_file_ods;
                        break;
                    case odt:
                        resId = R.drawable.ic_file_odt;
                        break;
                    case pdf:
                        resId = R.drawable.ic_file_pdf;
                        break;
                    case rtf:
                        resId = R.drawable.ic_file_rtf;
                        break;
                    case pptx:
                        resId = R.drawable.ic_file_pptx;
                        break;
                    case xls:
                    case xlsx:
                        resId = R.drawable.ic_file_xls;
                        break;
                    case txt:
                        resId = R.drawable.ic_file_txt;
                        break;
                    default:
                        resId = R.drawable.ic_file;
                        break;
                }
                imageView.setImageResource(resId);
            }
        });
        imageView.setTag(subscription);
    }
}
