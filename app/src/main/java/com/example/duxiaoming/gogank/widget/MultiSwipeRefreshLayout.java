package com.example.duxiaoming.gogank.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.example.duxiaoming.gogank.R;

/**
 * Created by duxiaoming on 2016/12/6.
 * blog:m78star.com
 * description:
 */

public class MultiSwipeRefreshLayout extends SwipeRefreshLayout {
    private CanChildScrollUpCallback mCanChildScrollUpCallback;
    private Drawable mForegroundDrawable;


    public MultiSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public MultiSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MultiSwipeRefreshLayout, 0, 0);
        this.mForegroundDrawable = a.getDrawable(R.styleable.MultiSwipeRefreshLayout_foreground);
        if (this.mForegroundDrawable != null) {
            this.mForegroundDrawable.setCallback(this);
            setWillNotDraw(false);
        }
        a.recycle();

    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.mForegroundDrawable != null) {
            this.mForegroundDrawable.setBounds(0, 0, w, h);
        }
    }


    @Override public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        if (this.mForegroundDrawable != null) {
            this.mForegroundDrawable.draw(canvas);
        }
    }
    public void setCanChildScrollUpCallback(CanChildScrollUpCallback canChildScrollUpCallback) {
        this.mCanChildScrollUpCallback = canChildScrollUpCallback;
    }

    @Override
    public boolean canChildScrollUp() {
        if (mCanChildScrollUpCallback != null) {
            return mCanChildScrollUpCallback.canSwipeRefreshChildScrollUp();
        }
        return super.canChildScrollUp();
    }

    public interface CanChildScrollUpCallback {
        boolean canSwipeRefreshChildScrollUp();
    }
}
