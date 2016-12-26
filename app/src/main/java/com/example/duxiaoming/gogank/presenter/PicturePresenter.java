package com.example.duxiaoming.gogank.presenter;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.example.duxiaoming.gogank.R;
import com.example.duxiaoming.gogank.core.mvp.BasePresenter;
import com.example.duxiaoming.gogank.presenter.iview.PictureView;
import com.example.duxiaoming.gogank.utils.DeviceUtils;
import com.example.duxiaoming.gogank.utils.RxUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import kr.co.namee.permissiongen.PermissionGen;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by duxiaoming on 2016/12/16.
 * blog:m78star.com
 * description:
 */

public class PicturePresenter extends BasePresenter<PictureView> {


    private Observable<String> getSavePictureObservable(final GlideBitmapDrawable glideBitmapDrawable,
                                                        final Context context, final Application application) {

        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

                String dirPath = DeviceUtils.createAPPFolder(
                        context.getString(R.string.app_name), application);
                File downloadFile = new File(new File(dirPath),
                        UUID.randomUUID().toString().replace("-", "") + ".jpg");
                if (!downloadFile.exists()) {
                    File parent = downloadFile.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }

                }

                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(downloadFile);
                } catch (FileNotFoundException e) {
                    PermissionGen.with((Activity) context)
                            .addRequestCode(100)
                            .permissions(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .request();
                    e.printStackTrace();
                }
                glideBitmapDrawable.getBitmap()
                        .compress(Bitmap.CompressFormat.JPEG, 100, output);
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 更新相册
                Uri uri = Uri.fromFile(downloadFile);
                Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                context.sendBroadcast(scannerIntent);
                subscriber.onNext(downloadFile.getPath());

            }
        }).compose(RxUtils.applyIOToMainThreadSchedulers());

    }

    public void downloadPicture(@NonNull final GlideBitmapDrawable glideBitmapDrawable,
                                @NonNull final Context context, @NonNull final Application application) {
        this.mCompositeSubscription.add(this.getSavePictureObservable(glideBitmapDrawable, context, application)
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        PicturePresenter.this.mCompositeSubscription.remove(this);
                    }


                    @Override
                    public void onError(Throwable e) {
                        if (PicturePresenter.this.getMvpView() != null) {
                            PicturePresenter.this.getMvpView().onFailure(e);
                        }
                    }


                    @Override
                    public void onNext(String s) {
                        if (PicturePresenter.this.getMvpView() != null) {
                            PicturePresenter.this.getMvpView().onDownloadSuccess(s);
                        }
                    }
                }));
    }

    public void sharePicture(
            @NonNull final GlideBitmapDrawable glideBitmapDrawable,
            @NonNull final Context context, @NonNull final Application application) {
        this.mCompositeSubscription.add(
                this.getSavePictureObservable(glideBitmapDrawable, context, application)
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                                PicturePresenter.this.mCompositeSubscription.remove(this);
                            }


                            @Override
                            public void onError(Throwable e) {
                                if (PicturePresenter.this.getMvpView() != null) {
                                    PicturePresenter.this.getMvpView().onFailure(e);
                                }
                            }


                            @Override
                            public void onNext(String s) {
                                if (PicturePresenter.this.getMvpView() != null) {
                                    Uri uri = Uri.parse("file://" + s);
                                    PicturePresenter.this.getMvpView().onShare(uri);
                                }
                            }
                        }));
    }
}
