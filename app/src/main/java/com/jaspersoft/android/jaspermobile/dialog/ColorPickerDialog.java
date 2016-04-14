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
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.widget.RoundColorView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class ColorPickerDialog extends AlertDialog implements AdapterView.OnItemClickListener {
    private final static int COLOR_VIEW_SIZE = 48;
    private final static List<Integer> DEFAULT_COLORS = new ArrayList<Integer>() {{
        add(Color.BLACK);
        add(Color.rgb(127, 127, 127));
        add(Color.rgb(127, 0, 0));
        add(Color.RED);
        add(Color.rgb(255, 127, 0));
        add(Color.YELLOW);
        add(Color.GREEN);
        add(Color.rgb(0, 255, 255));
        add(Color.BLUE);
        add(Color.rgb(75, 0, 130));
        add(Color.rgb(143, 0, 255));
        add(Color.WHITE);
    }};

    private ColorSelectListener mColorSelectListener;
    private List<Integer> mColorList;
    private int mSelectedColor;
    private int mItemSize;

    public ColorPickerDialog(Context context) {
        super(context);
        mColorList = DEFAULT_COLORS;
        mSelectedColor = 0;

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        mItemSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, COLOR_VIEW_SIZE, metrics);
    }

    protected ColorPickerDialog(Context context, int theme) {
        super(context, theme);
    }

    protected ColorPickerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setSelectedColor(int selectedColor) {
        mSelectedColor = selectedColor;
    }

    public void setColorSelectListener(ColorSelectListener colorSelectListener) {
        mColorSelectListener = colorSelectListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GridView colorGrid = (GridView) getLayoutInflater().inflate(R.layout.dialog_color_picker, null, false);
        colorGrid.setAdapter(new ColorListAdapter());
        colorGrid.setOnItemClickListener(this);

        setTitle(getContext().getString(R.string.annotation_pick_color));
        setView(colorGrid);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mColorSelectListener != null) {
            mColorSelectListener.onColorSelected(mColorList.get(position));
        }
        dismiss();
    }

    public interface ColorSelectListener {
        void onColorSelected(int color);
    }

    private class ColorListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mColorList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int color = mColorList.get(position);

            RoundColorView roundColorView;
            if (convertView == null) {
                roundColorView = new RoundColorView(getContext());
                AbsListView.LayoutParams lp = new GridView.LayoutParams(mItemSize, mItemSize);
                roundColorView.setLayoutParams(lp);
            } else {
                roundColorView = (RoundColorView) convertView;
            }
            roundColorView.setColor(color);
            roundColorView.showSelected(mSelectedColor == color);

            return roundColorView;
        }
    }
}
