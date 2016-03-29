package com.jaspersoft.android.jaspermobile.activities.file;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jaspersoft.android.jaspermobile.JasperMobileApplication;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.LoadFileRequest;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.LoadResourceInFileCase;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;
import com.jaspersoft.android.jaspermobile.util.DefaultPrefHelper;
import com.jaspersoft.android.sdk.client.oxm.resource.FileLookup;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;

import java.io.File;
import java.util.Date;

import javax.inject.Inject;

import rx.Subscriber;
import timber.log.Timber;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@EFragment
public abstract class FileLoadFragment extends BaseFragment {

    private static final String TEMP_FILE_NAME = "tempFile";

    @Inject
    protected LoadResourceInFileCase mLoadResourceInFileCase;
    @Inject
    protected JasperServer mServer;
    @Inject
    protected Profile mProfile;

    @Bean
    DefaultPrefHelper prefHelper;

    @InstanceState
    @FragmentArg
    protected FileLookup.FileType fileType;

    @InstanceState
    @FragmentArg
    protected String fileUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseActivityComponent().inject(this);
    }

    @Override
    public void onDestroyView() {
        mLoadResourceInFileCase.unsubscribe();
        super.onDestroyView();
    }

    protected abstract void onFileReady(File file);

    protected abstract void showErrorMessage();

    protected void loadFile() {
        File resourceFile = getResourceFile(fileUri);

        if (resourceFile == null) {
            showErrorMessage();
        } else {
            loadFile(resourceFile);
        }
    }

    protected File getResourceFile(String resourceUri) {
        boolean cacheEnabled = isCachingEnabled();
        if (cacheEnabled) {
            return getCacheFile(resourceUri);
        } else {
            return getTempFile();
        }
    }

    protected boolean isFileValid(File cacheFile) {
        boolean cacheEnabled = isCachingEnabled();
        if (cacheEnabled) {
            long currentDate = new Date().getTime();
            long cacheExpiration = prefHelper.getRepoCacheExpirationValue();
            long lastModifiedDate = cacheFile.lastModified();
            return lastModifiedDate + cacheExpiration >= currentDate;
        }
        return false;
    }

    private void loadFile(File resourceFile) {
        if (!resourceFile.exists() || !isFileValid(resourceFile)) {
            requestFile(resourceFile);
        } else {
            onFileReady(resourceFile);
        }
    }

    private void requestFile(File resourceFile) {
        LoadFileRequest request = new LoadFileRequest(fileUri, resourceFile);
        mLoadResourceInFileCase.execute(request, new FileContentListener());
    }

    @Nullable
    private File getCacheFile(String resourceUri) {
        File cacheDir = getActivity().getExternalCacheDir();
        File resourceCacheDir = new File(cacheDir, JasperMobileApplication.RESOURCES_CACHE_DIR_NAME);

        File accountCacheDir = new File(resourceCacheDir, mProfile.getKey());
        if (!accountCacheDir.exists() && !accountCacheDir.mkdirs()) {
            Timber.e("Unable to create %s", accountCacheDir);
            return null;
        }
        return new File(accountCacheDir, resourceUri);
    }

    private File getTempFile() {
        File cacheDir = getActivity().getExternalCacheDir();
        File resourceCacheDir = new File(cacheDir, JasperMobileApplication.RESOURCES_CACHE_DIR_NAME);
        return new File(resourceCacheDir, TEMP_FILE_NAME);
    }

    private boolean isCachingEnabled() {
        return prefHelper.getRepoCacheExpirationValue() != -1;
    }

    private void showProgressDialog() {
        ProgressDialogFragment.builder(getActivity().getSupportFragmentManager())
                .setLoadingMessage(R.string.loading_msg)
                .setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                getActivity().finish();
                            }
                        }
                )
                .show();
    }

    private void hideProgressDialog() {
        ProgressDialogFragment.dismiss(getActivity().getSupportFragmentManager());
    }

    private class FileContentListener extends Subscriber<File> {
        @Override
        public void onStart() {
            showProgressDialog();
        }

        @Override
        public void onCompleted() {
            hideProgressDialog();
        }

        @Override
        public void onError(Throwable e) {
            RequestExceptionHandler.showAuthErrorIfExists(getContext(), e);
            showErrorMessage();
            hideProgressDialog();
        }

        @Override
        public void onNext(File file) {
            onFileReady(file);
        }
    }
}
