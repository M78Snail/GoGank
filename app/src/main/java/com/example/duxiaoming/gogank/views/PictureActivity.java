package com.example.duxiaoming.gogank.views;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.duxiaoming.gogank.GoGankApplication;
import com.example.duxiaoming.gogank.R;
import com.example.duxiaoming.gogank.core.BaseToolbarActivity;
import com.example.duxiaoming.gogank.presenter.PicturePresenter;
import com.example.duxiaoming.gogank.presenter.iview.PictureView;
import com.example.duxiaoming.gogank.utils.DeviceUtils;
import com.example.duxiaoming.gogank.utils.IntentUtils;
import com.example.duxiaoming.gogank.utils.ShareUtils;

import butterknife.Bind;
import kr.co.namee.permissiongen.PermissionGen;

/**
 * Created by duxiaoming on 2016/12/16.
 * blog:m78star.com
 * description:
 */

public class PictureActivity extends BaseToolbarActivity implements PictureView, View.OnLongClickListener {

    private static final String EXTRA_URL = "com.camnter.easygank.EXTRA_URL";
    private static final String EXTRA_TITLE = "com.camnter.easygank.EXTRA_TITLE";

    private static final String SHARED_ELEMENT_NAME = "PictureActivity";

    @Bind(R.id.picture_iv)
    ImageView pictureIV;

    private PicturePresenter presenter;

    private GlideBitmapDrawable glideBitmapDrawable;


    public static void startActivity(Context context, String url, String title) {
        context.startActivity(createIntent(context, url, title));
    }


    public static void startActivityByActivityOptionsCompat(Activity activity, String url, String title, View view) {
        Intent intent = createIntent(activity, url, title);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(
                view, view.getWidth() / 2, view.getHeight() / 2, view.getWidth(), view.getHeight());
        try {
            ActivityCompat.startActivity(activity, intent, activityOptionsCompat.toBundle());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            startActivity(activity, url, title);
        }
    }


    private static Intent createIntent(Context context, String url, String title) {
        Intent intent = new Intent(context, PictureActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_picture,menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_picture_download:
                this.download();
                return true;
            case R.id.menu_picture_copy:
                DeviceUtils.copy2Clipboard(this, this.getUrl());
                Snackbar.make(this.pictureIV, this.getString(R.string.common_copy_success),
                        Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.menu_picture_share:
                this.sharePicture();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_picture;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        this.pictureIV = this.findView(R.id.picture_iv);
        ViewCompat.setTransitionName(this.pictureIV, SHARED_ELEMENT_NAME);
        PermissionGen.with(PictureActivity.this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request();
    }



    @Override
    protected void initListeners() {
        this.pictureIV.setOnLongClickListener(this);
    }

    @Override
    protected void initData() {
        this.showBack();
        this.setTitle(this.getUrlTitle());
        Glide.with(this)
                .load(this.getUrl())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        //加载完成
                        PictureActivity.this.glideBitmapDrawable = (GlideBitmapDrawable) resource;
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(this.pictureIV)
                .getSize((width, height) -> {
                    if (!PictureActivity.this.pictureIV.isShown()) {
                        PictureActivity.this.pictureIV.setVisibility(View.VISIBLE);
                    }
                });
        this.presenter=new PicturePresenter();
        this.presenter.attachView(this);
    }

    @Override
    public void onDownloadSuccess(String path) {
        Snackbar.make(this.pictureIV,"下载成功："+path,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onShare(Uri uri) {
        ShareUtils.shareImage(this, uri, "分享妹子");
    }

    @Override
    public void onFailure(Throwable e) {
        Snackbar.make(this.pictureIV, this.getString(R.string.common_network_error),
                Snackbar.LENGTH_LONG).show();
    }



    private String getUrl() {
        return IntentUtils.getStringExtra(this.getIntent(), EXTRA_URL);
    }


    private String getUrlTitle() {
        return IntentUtils.getStringExtra(this.getIntent(), EXTRA_TITLE);
    }

    public void download(){
        if(this.glideBitmapDrawable!=null){
            this.presenter.downloadPicture(this.glideBitmapDrawable,this, GoGankApplication.getInstance());
        }else{
            Snackbar.make(this.pictureIV,this.getString(R.string.picture_loading),Snackbar.LENGTH_LONG).show();
        }
    }

    public void sharePicture(){
        if(this.glideBitmapDrawable!=null){
            this.presenter.sharePicture(glideBitmapDrawable,this,GoGankApplication.getInstance());
        }else{
            Snackbar.make(this.pictureIV,this.getString(R.string.picture_loading),Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.picture_iv:
                new AlertDialog.Builder(PictureActivity.this).setMessage(
                        getString(R.string.picture_download))
                        .setNegativeButton(
                                android.R.string.cancel,
                                (dialog, which) -> dialog.dismiss())
                        .setPositiveButton(android.R.string.ok,
                                (dialog, which) -> {
                                    this.download();
                                    dialog.dismiss();
                                })
                        .show();
                return true;
        }
        return false;
    }
}
