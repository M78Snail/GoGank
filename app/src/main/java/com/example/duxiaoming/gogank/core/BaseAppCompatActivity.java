package com.example.duxiaoming.gogank.core;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.duxiaoming.gogank.utils.ToastUtils;

import butterknife.ButterKnife;

/**
 * Created by duxiaoming on 2016/12/6.
 * blog:m78star.com
 * description: 5个抽象方法
 */

public abstract class BaseAppCompatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getLayoutId());
        ButterKnife.bind(this);

        this.initToolbar(savedInstanceState);
        this.initViews(savedInstanceState);
        this.initData();
        this.initListeners();
    }

    /**
     * Find the view by id
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * Find the view by id
     * @param id
     * @param <V>
     * @return
     */
    protected <V extends View> V findView(int id){
        return (V)this.findViewById(id);
    }

    protected abstract void initViews(Bundle savedInstanceState);

    protected abstract void initToolbar(Bundle savedInstanceState);

    protected abstract void initListeners();

    protected abstract void initData();

    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        super.startActivity(intent, options);
    }

    /**
     * @param intent The intent to start.
     * @param requestCode If >= 0, this code will be returned in
     * onActivityResult() when the activity exits.
     * @param options Additional options for how the Activity should be started.
     * See {@link Context#startActivity(Intent, Bundle)
     * Context.startActivity(Intent, Bundle)} for more details.
     * @throws ActivityNotFoundException
     * @see #startActivity
     */
    @Override public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
    }


    /**
     * @param intent intent
     * @param requestCode requestCode
     */
    @Override public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }


    /**
     * Call this when your activity is done and should be closed.  The
     * ActivityResult is propagated back to whoever launched you via
     * onActivityResult().
     */
    @Override public void finish() {
        super.finish();
    }


    @Override protected void onDestroy() {
        super.onDestroy();
    }


    /*********
     * Toast *
     *********/

    public void showToast(String msg) {
        this.showToast(msg, Toast.LENGTH_SHORT);
    }


    public void showToast(String msg, int duration) {
        if (msg == null) return;
        if (duration == Toast.LENGTH_SHORT || duration == Toast.LENGTH_LONG) {
            ToastUtils.show(this, msg, duration);
        } else {
            ToastUtils.show(this, msg, ToastUtils.LENGTH_SHORT);
        }
    }


    public void showToast(int resId) {
        this.showToast(resId, Toast.LENGTH_SHORT);
    }


    public void showToast(int resId, int duration) {
        if (duration == Toast.LENGTH_SHORT || duration == Toast.LENGTH_LONG) {
            ToastUtils.show(this, resId, duration);
        } else {
            ToastUtils.show(this, resId, ToastUtils.LENGTH_SHORT);
        }
    }

}
