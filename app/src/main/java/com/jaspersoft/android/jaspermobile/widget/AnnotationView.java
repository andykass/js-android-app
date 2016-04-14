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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class AnnotationView extends View {
    private static final int DEFAULT_COLOR = Color.RED;
    private static final int DEFAULT_SIZE = 3;

    private List<Pair<Paint, Path>> mDrawingCache;

    private int mColor;
    private int mSize;
    private float mStartX, mStartY;

    public AnnotationView(Context context) {
        super(context);
        init();
    }

    public AnnotationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnnotationView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    public void reset() {
        mDrawingCache.clear();
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (Pair<Paint, Path> paintPathPair : mDrawingCache) {
            canvas.drawPath(paintPathPair.second, paintPathPair.first);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchStart(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                onTouchEnd();
                break;
        }
        invalidate();
        return isEnabled();
    }

    private void init() {
        mDrawingCache = new ArrayList<>();
        mColor = DEFAULT_COLOR;
        mSize = DEFAULT_SIZE;
    }

    private void addPath() {
        Paint annotationPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        annotationPaint.setColor(mColor);
        annotationPaint.setStyle(Paint.Style.STROKE);
        annotationPaint.setStrokeJoin(Paint.Join.ROUND);
        annotationPaint.setStrokeCap(Paint.Cap.ROUND);

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int strokeSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mSize, metrics);
        annotationPaint.setStrokeWidth(strokeSize);

        Path annotationPath = new Path();
        annotationPath.moveTo(mStartX, mStartY);

        mDrawingCache.add(new Pair<>(annotationPaint, annotationPath));
    }

    private void onTouchStart(float xCord, float yCord) {
        addPath();
        getLastPath().moveTo(xCord, yCord);
        mStartX = xCord;
        mStartY = yCord;
    }

    private void onTouchMove(float xCord, float yCord) {
        float dx = Math.abs(xCord - mStartX);
        float dy = Math.abs(yCord - mStartY);
        if (dx >= 4 || dy >= 4) {
            getLastPath().quadTo(mStartX, mStartY, (xCord + mStartX) / 2, (yCord + mStartY) / 2);
            mStartX = xCord;
            mStartY = yCord;
        }
    }

    private void onTouchEnd() {
        getLastPath().lineTo(mStartX, mStartY);
    }

    private Path getLastPath() {
        if (mDrawingCache.isEmpty()) {
            addPath();
        }
        return mDrawingCache.get(mDrawingCache.size() - 1).second;
    }
}
