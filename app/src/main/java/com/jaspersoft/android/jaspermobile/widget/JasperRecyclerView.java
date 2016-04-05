package com.jaspersoft.android.jaspermobile.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.util.ViewType;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.DividerGridItemDecoration;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceAdapter;

/**
 * @author Andrew Tivodar
 * @since 2.0
 */
public class JasperRecyclerView extends RecyclerView {
    private final static int MIN_COLUMN_COUNT = 2;

    private int columnWidth;
    private DividerGridItemDecoration decoration;

    public JasperRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public JasperRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public JasperRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int[] attrsArray = {android.R.attr.columnWidth};
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
            columnWidth = array.getDimensionPixelSize(0, -1);
            array.recycle();
        }
        setHasFixedSize(true);

        decoration = new DividerGridItemDecoration((int) context.getResources().getDimension(R.dimen.resource_item_padding));
        addItemDecoration(decoration);
        setLayoutManager(new GridLayoutManagerWithLoading(getContext(), 1));
    }

    public void changeViewType(ViewType viewType) {
        getAdapter().setViewType(viewType);

        stopScroll();
        setHasFixedSize(false);
        getItemAnimator().endAnimations();
        getAdapter().notifyItemRangeChanged(0, getAdapter().getItemCount());
        setHasFixedSize(true);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter instanceof Adapter) {
            super.setAdapter(adapter);
            return;
        }
        throw new UnsupportedOperationException("Only JasperRecyclerView.Adapter is supported");
    }

    @Override
    public JasperRecyclerView.Adapter getAdapter() {
        return (Adapter) super.getAdapter();
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (layout instanceof GridLayoutManagerWithLoading) {
            super.setLayoutManager(layout);
            return;
        }
        throw new UnsupportedOperationException("Only GridLayoutManagerWithLoading is supported!");
    }

    @Override
    public GridLayoutManagerWithLoading getLayoutManager() {
        return (GridLayoutManagerWithLoading) super.getLayoutManager();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);

        GridLayoutManagerWithLoading layoutManager = getLayoutManager();
        if (columnWidth > 0) {
            int spanCount = getAdapter().getViewType() == Adapter.LIST_TYPE ? 1 : Math.max(MIN_COLUMN_COUNT, getMeasuredWidth() / columnWidth);
            layoutManager.setSpanCount(spanCount);
            decoration.setSpanCount(spanCount);
        }
    }

    public static abstract class Adapter<VH extends ViewHolder> extends RecyclerView.Adapter<VH> {
        public final static int LOADING_TYPE = -1;
        public final static int LIST_TYPE = 1;
        public final static int GRID_TYPE = 2;

        private int mViewType = 1;

        public int getViewType() {
            return mViewType;
        }

        void setViewType(ViewType viewType) {
            this.mViewType = viewType == ViewType.LIST ? LIST_TYPE : GRID_TYPE;
        }

        protected void onViewTypeChanged(ViewType viewType) {

        }
    }

    public class GridLayoutManagerWithLoading extends GridLayoutManager {

        public GridLayoutManagerWithLoading(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            init();
        }

        public GridLayoutManagerWithLoading(Context context, int spanCount) {
            super(context, spanCount);
            init();
        }

        public GridLayoutManagerWithLoading(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
            init();
        }

        private void init() {
            setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    Adapter adapter = getAdapter();
                    if (adapter != null) {
                        return adapter.getItemViewType(position) == Adapter.LOADING_TYPE ? getSpanCount() : 1 ;
                    }
                    return 1;
                }
            });
        }
    }
}
