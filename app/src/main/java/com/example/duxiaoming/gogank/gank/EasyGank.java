package com.example.duxiaoming.gogank.gank;

import com.example.duxiaoming.gogank.GoGankApplication;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by duxiaoming on 2016/12/12.
 * blog:m78star.com
 * description:
 */

public class EasyGank {
    private static EasyGank outInstance;
    private GankService gankService;

    public static EasyGank getInstance(){
        if(outInstance==null) outInstance=new EasyGank();
        return outInstance;
    }

    private EasyGank(){
        OkHttpClient okHttpClient=new OkHttpClient();
        okHttpClient.setReadTimeout(7676, TimeUnit.MILLISECONDS);

        Retrofit retrofit=new Retrofit.Builder().baseUrl(GankApi.BASE_URL)
                                                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                                .addConverterFactory(GsonConverterFactory.create(GoGankApplication.getInstance().gson))
                                                .client(okHttpClient)
                                                .build();
        this.gankService=retrofit.create(GankService.class);
    }

    public GankService getGankService(){
        return gankService;
    }
}
