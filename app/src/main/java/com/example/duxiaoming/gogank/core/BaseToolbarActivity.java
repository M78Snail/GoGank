package com.example.duxiaoming.gogank.core;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.duxiaoming.gogank.R;

import butterknife.Bind;

/**
 * Created by duxiaoming on 2016/12/6.
 * blog:m78star.com
 * description:
 */

public abstract class BaseToolbarActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    @Bind(R.id.app_bar_layout)
    protected AppBarLayout mAppBarLayout;

    protected ActionBarHelper mActionBarHelper;

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        initToolBarHelper();
    }

    protected void initToolBarHelper() {
        if (this.mToolbar == null || this.mAppBarLayout == null) return;
        this.setSupportActionBar(this.mToolbar);
        this.mActionBarHelper=this.createActionBarHelper();
        this.mActionBarHelper.init();

        if (Build.VERSION.SDK_INT >= 21) {
            this.mAppBarLayout.setElevation(6.6f);
        }

    }

    /**
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    protected void showBack() {
        if (this.mActionBarHelper != null) this.mActionBarHelper.setDisplayHomeAsUpEnabled(true);
    }


    /**
     * set the AppBarLayout visibility
     *
     * @param visibility visibility
     */
    protected void setAppBarLayoutVisibility(boolean visibility) {
        if (visibility) {
            this.mAppBarLayout.setVisibility(View.VISIBLE);
        } else {
            this.mAppBarLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Create a compatible helper that will manipulate the action bar if available.
     */
    public ActionBarHelper createActionBarHelper() {
        return new ActionBarHelper();
    }

    public class ActionBarHelper {
        private final ActionBar mActionBar;
        CharSequence mDrawerTitle;
        CharSequence mTitle;

        public ActionBarHelper() {
            this.mActionBar = getSupportActionBar();
        }

        void init() {
            if (this.mActionBar == null) return;
            this.mActionBar.setDisplayHomeAsUpEnabled(true);
            this.mActionBar.setDisplayShowHomeEnabled(false);
            this.mTitle = mDrawerTitle = getTitle();

        }

        void onDrawerClosed() {
            if (this.mActionBar == null) return;
            this.mActionBar.setTitle(this.mTitle);
        }

        void onDrawerOpened() {
            if (this.mActionBar == null) return;
            this.mActionBar.setTitle(this.mDrawerTitle);
        }

        public void setTitle(CharSequence title) {
            this.mTitle = title;
        }


        public void setDrawerTitle(CharSequence drawerTitle) {
            this.mDrawerTitle = drawerTitle;
        }


        public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
            if (this.mActionBar == null) return;
            this.mActionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
        }
    }
}
