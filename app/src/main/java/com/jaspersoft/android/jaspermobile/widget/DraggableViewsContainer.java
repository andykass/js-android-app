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

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.dialog.AnnotationInputDialog;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class DraggableViewsContainer extends RelativeLayout implements View.OnTouchListener, View.OnDragListener {
    private static final int DEFAULT_COLOR = Color.BLACK;
    private static final int DEFAULT_SIZE = 2;
    private static final int BORDER_PADDING = 10;

    private int mColor;
    private int mSize;
    private boolean mNeedsBorder;

    public DraggableViewsContainer(Context context) {
        super(context);
        init();
    }

    public DraggableViewsContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DraggableViewsContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        mSize = size;
    }

    public void setNeedsBorder(boolean needsBorder) {
        mNeedsBorder = needsBorder;
    }

    public boolean needsBorder() {
        return mNeedsBorder;
    }

    @Override
    public void setEnabled(boolean enabled) {
        setOnTouchListener(enabled ? this : null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int viewId = addDraggableNote(event.getX(), event.getY());
            showTextInputDialog(viewId);
        }
        return isEnabled();
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int viewId;
        View noteView;
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                viewId = Integer.parseInt(event.getClipDescription().getLabel().toString());
                noteView = findViewById(viewId);
                noteView.setVisibility(View.GONE);
                return true;
            case DragEvent.ACTION_DROP:
                viewId = Integer.parseInt(event.getClipDescription().getLabel().toString());
                noteView = findViewById(viewId);
                noteView.setX(event.getX() - noteView.getWidth() / 2);
                noteView.setY(event.getY() - noteView.getHeight() / 2);
                noteView.setVisibility(View.VISIBLE);
                return true;
            default:
                return false;
        }
    }

    private void init() {
        setOnTouchListener(this);
        setOnDragListener(this);
        mColor = DEFAULT_COLOR;
        mSize = DEFAULT_SIZE;
    }

    private int addDraggableNote(float x, float y) {
        int viewId = getChildCount();
        TextView textView = new TextView(getContext());
        textView.setTextColor(mColor);
        textView.setTextSize(10 + mSize * 2);
        textView.setX(x);
        textView.setY(y);
        textView.setId(viewId);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showTextInputDialog(v.getId(), ((TextView) v).getText().toString());
            }
        });
        textView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText(String.valueOf(v.getId()), "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, null, 0);
                return true;
            }
        });
        addView(textView);
        return viewId;
    }

    private void showTextInputDialog(int viewId) {
        final AnnotationInputDialog annotationInputDialog = new AnnotationInputDialog(getContext());
        annotationInputDialog.setTitle(getContext().getString(R.string.annotation_edit_note));
        annotationInputDialog.setId(viewId);
        annotationInputDialog.setValue("");
        annotationInputDialog.setOnEventListener(new AnnotationInputDialog.OnAnnotationInputListener() {
            @Override
            public void onAnnotationEntered(int id, String inputText) {
                TextView note = (TextView) findViewById(id);
                note.setText(inputText);
                if (mNeedsBorder) {
                    note.setBackgroundResource(R.drawable.bg_annotation_text_border);
                    note.getBackground().setColorFilter(mColor, PorterDuff.Mode.MULTIPLY);
                    int borderPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BORDER_PADDING, getContext().getResources().getDisplayMetrics());
                    note.setPadding(borderPadding, borderPadding, borderPadding, borderPadding);
                }
            }

            @Override
            public void onAnnotationCanceled(int id) {
                TextView noteView = (TextView) findViewById(id);
                removeView(noteView);
            }
        });
        annotationInputDialog.show();
    }

    private void showTextInputDialog(int viewId, final String note) {
        final AnnotationInputDialog annotationInputDialog = new AnnotationInputDialog(getContext());
        annotationInputDialog.setTitle(getContext().getString(R.string.annotation_edit_note));
        annotationInputDialog.setId(viewId);
        annotationInputDialog.setValue(note);
        annotationInputDialog.setOnEventListener(new AnnotationInputDialog.OnAnnotationInputListener() {
            @Override
            public void onAnnotationEntered(int id, String inputText) {
                TextView note = (TextView) findViewById(id);
                note.setText(inputText);
            }

            @Override
            public void onAnnotationCanceled(int id) {
            }
        });
        annotationInputDialog.show();
    }
}
