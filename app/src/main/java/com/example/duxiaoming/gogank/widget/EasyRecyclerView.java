package com.example.duxiaoming.gogank.widget;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by duxiaoming on 2016/12/6.
 * blog:m78star.com
 * description:
 */

public class EasyRecyclerView extends RecyclerView {

    private LinearLayoutManager mLinearLayoutManager;


    public EasyRecyclerView(Context context) {
        super(context);
        this.initRecyclerView(context);
    }


    public EasyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initRecyclerView(context);
    }


    public EasyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initRecyclerView(context);
    }


    /**
     * Init the recycler view
     *
     * @param context context
     */
    private void initRecyclerView(Context context) {
        // init LinearLayoutManager
        this.mLinearLayoutManager = new LinearLayoutManager(context);
        // set the VERTICAL layout
        this.mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // set layout manager
        this.setLayoutManager(this.mLinearLayoutManager);
        // set item animator
        this.setItemAnimator(new DefaultItemAnimator());
        // keep recyclerview fixed size
        this.setHasFixedSize(true);
    }


    public LinearLayoutManager getLinearLayoutManager() {
        return mLinearLayoutManager;
    }


    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager) {
        mLinearLayoutManager = linearLayoutManager;
    }
}
