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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.jaspersoft.android.jaspermobile.R;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class RoundColorView extends ImageView {
    private Paint mPaint;

    public RoundColorView(Context context) {
        super(context);
        init();
    }

    public RoundColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void showSelected(boolean selected) {
        if (!selected) return;

        setImageResource(R.drawable.ic_menu_done);
        getDrawable().setColorFilter(inverseColor(mPaint.getColor()), PorterDuff.Mode.MULTIPLY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getHeight() / 2, mPaint);
        super.onDraw(canvas);
    }

    private void init() {
        setScaleType(ScaleType.CENTER);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private int inverseColor(int color) {
        if (0.2126 * Color.red(color) + 0.7152 * Color.green(color) + 0.0722 * Color.blue(color) > 127)
            return Color.BLACK;
        return Color.WHITE;
    }
}
