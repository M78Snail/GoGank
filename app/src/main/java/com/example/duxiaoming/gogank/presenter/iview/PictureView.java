package com.example.duxiaoming.gogank.presenter.iview;

import android.net.Uri;

import com.example.duxiaoming.gogank.core.mvp.MvpView;

/**
 * Created by duxiaoming on 2016/12/16.
 * blog:m78star.com
 * description:
 */

public interface PictureView extends MvpView {

    void onDownloadSuccess(String path);

    void onShare(Uri uri);
}
