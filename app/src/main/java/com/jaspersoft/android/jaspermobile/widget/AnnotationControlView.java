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

package com.jaspersoft.android.jaspermobile.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.dialog.ColorPickerDialog;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
@EViewGroup(R.layout.view_annotation_control)
public class AnnotationControlView extends RelativeLayout {
    public static final int DRAW_MODE = 1;
    public static final int TEXT_MODE = 2;

    @ViewById(R.id.annotationType)
    ImageButton changeMode;

    @ViewById(R.id.annotationClear)
    ImageButton clear;

    @ViewById(R.id.annotationColor)
    ImageButton chooseColor;

    @ViewById(R.id.annotationSize)
    ImageButton chooseSize;

    @ViewById(R.id.currentColor)
    ImageView currentColor;

    private EventListener mEventListener;
    private int mMode;
    private int mColor;

    public AnnotationControlView(Context context) {
        super(context);
        init();
    }

    public AnnotationControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnnotationControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public int getMode() {
        return mMode;
    }

    public void setColor(int color) {
        mColor = color;
        currentColor.setBackgroundColor(color);
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    @Click(R.id.annotationType)
    void changeModeAction() {
        if (mMode == DRAW_MODE) {
            mMode = TEXT_MODE;
        } else {
            mMode = DRAW_MODE;
        }
        refreshViewState();
        mEventListener.onModeChanged(mMode);
    }

    @Click(R.id.annotationClear)
    void clearAction() {
        mEventListener.onClear();
    }

    @Click(R.id.annotationSize)
    void sizeAction() {
       if (mEventListener != null) {
           mEventListener.onSizeChangeRequested();
       }
    }

    @Click(R.id.annotationColor)
    void colorAction() {
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(getContext());
        colorPickerDialog.setSelectedColor(mColor);
        colorPickerDialog.setColorSelectListener(new ColorPickerDialog.ColorSelectListener() {
            @Override
            public void onColorSelected(int color) {
                if (mEventListener != null) {
                    mEventListener.onColorSelected(color);
                }
                setColor(color);
            }
        });
        colorPickerDialog.show();
    }

    private void init() {
        mMode = DRAW_MODE;
        mEventListener = new EmptyEventListener();
    }

    private void refreshViewState() {
        if (mMode == DRAW_MODE) {
            changeMode.setImageResource(R.drawable.ic_menu_edit);
            chooseSize.setImageResource(R.drawable.ic_menu_line_size);
        } else {
            changeMode.setImageResource(R.drawable.ic_menu_text);
            chooseSize.setImageResource(R.drawable.ic_menu_format_size);
        }
    }

    public interface EventListener {
        void onModeChanged(int mode);

        void onClear();

        void onSizeChangeRequested();

        void onColorSelected(int color);
    }

    private class EmptyEventListener implements EventListener {
        @Override
        public void onModeChanged(int mode) {

        }

        @Override
        public void onClear() {

        }

        @Override
        public void onSizeChangeRequested() {

        }

        @Override
        public void onColorSelected(int color) {

        }
    }
}
