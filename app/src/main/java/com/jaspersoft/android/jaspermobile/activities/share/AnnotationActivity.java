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

package com.jaspersoft.android.jaspermobile.activities.share;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.dialog.AnnotationOptionsDialog;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.domain.ScreenCapture;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.SaveScreenCaptureCase;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.presentation.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.widget.AnnotationControlView;
import com.jaspersoft.android.jaspermobile.widget.AnnotationView;
import com.jaspersoft.android.jaspermobile.widget.DraggableViewsContainer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
@EActivity(R.layout.activity_annotation)
@OptionsMenu(R.menu.annotation)
public class AnnotationActivity extends ToolbarActivity implements AnnotationControlView.EventListener {

    @ViewById(R.id.container)
    RelativeLayout container;
    @ViewById(R.id.reportImage)
    ImageView reportImage;
    @ViewById(R.id.annotationDrawingContainer)
    AnnotationView annotationDrawing;
    @ViewById(R.id.annotationControl)
    AnnotationControlView annotationControlView;
    @ViewById(R.id.annotationNotesContainer)
    DraggableViewsContainer annotationNotes;

    @Extra
    Uri imageUri;

    @Inject
    SaveScreenCaptureCase mSaveScreenCaptureCase;
    @Inject
    RequestExceptionHandler mRequestExceptionHandler;

    @AfterViews
    void init() {
        getBaseActivityComponent().inject(this);

        reportImage.setImageURI(imageUri);
        getSupportActionBar().setTitle(getString(R.string.annotation_title));
        annotationControlView.setEventListener(this);
        annotationControlView.setColor(annotationDrawing.getColor());
        annotationNotes.setEnabled(false);
    }

    @OptionsItem(R.id.annotationDoneAction)
    void annotationDoneAction() {
        ScreenCapture reportScreenCapture = ScreenCapture.Factory.capture(container);
        mSaveScreenCaptureCase.execute(reportScreenCapture, new SimpleSubscriber<File>() {
            @Override
            public void onStart() {
                ProgressDialogFragment.builder(getSupportFragmentManager())
                        .setLoadingMessage(R.string.loading_msg)
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        })
                        .show();
            }

            @Override
            public void onError(Throwable e) {
                mRequestExceptionHandler.showCommonErrorMessage(e);
            }

            @Override
            public void onNext(File item) {
                Intent data = new Intent();
                data.setData(imageUri);
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void onCompleted() {
                ProgressDialogFragment.dismiss(getSupportFragmentManager());
            }
        });
    }

    @Override
    public void onModeChanged(int mode) {
        annotationDrawing.setEnabled(mode == AnnotationControlView.DRAW_MODE);
        annotationNotes.setEnabled(mode == AnnotationControlView.TEXT_MODE);

        annotationControlView.setColor(annotationControlView.getMode() == AnnotationControlView.DRAW_MODE
                ? annotationDrawing.getColor() : annotationNotes.getColor());

    }

    @Override
    public void onClear() {
        annotationDrawing.reset();
        annotationNotes.removeAllViews();
    }

    @Override
    public void onSizeChangeRequested() {
        AnnotationOptionsDialog annotationOptionsDialog = new AnnotationOptionsDialog(this);
        annotationOptionsDialog.setSize(annotationControlView.getMode() == AnnotationControlView.DRAW_MODE
                ? annotationDrawing.getSize() : annotationNotes.getSize());
        annotationOptionsDialog.setTitle(annotationControlView.getMode() == AnnotationControlView.DRAW_MODE
                ? getString(R.string.annotation_pick_line_size) : getString(R.string.annotation_pick_font_size));
        annotationOptionsDialog.setBorder(annotationControlView.getMode() == AnnotationControlView.DRAW_MODE
                ? null : annotationNotes.needsBorder());
        annotationOptionsDialog.setOnEventListener(new AnnotationOptionsDialog.OnAnnotationSizeListener() {
            @Override
            public void onAnnotationOptionsSelected(int size) {
                if (annotationControlView.getMode() == AnnotationControlView.DRAW_MODE) {
                    annotationDrawing.setSize(size);
                } else {
                    annotationNotes.setSize(size);
                }
            }

            @Override
            public void onAnnotationOptionsSelected(int size, boolean needsBorder) {
                onAnnotationOptionsSelected(size);
                annotationNotes.setNeedsBorder(needsBorder);
            }
        });
        annotationOptionsDialog.show();
    }

    @Override
    public void onColorSelected(int color) {
        if (annotationControlView.getMode() == AnnotationControlView.DRAW_MODE) {
            annotationDrawing.setColor(color);
        } else {
            annotationNotes.setColor(color);
        }
    }
}
