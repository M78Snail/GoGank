package com.example.duxiaoming.gogank;

import android.app.Application;

import com.anupcowkur.reservoir.Reservoir;
import com.example.duxiaoming.gogank.gank.GankApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by duxiaoming on 2016/12/6.
 * blog:m78star.com
 * description:
 */

public class GoGankApplication extends Application {
    private static GoGankApplication ourInstance = new GoGankApplication();
    public Gson gson;

    public static final long ONE_KB = 1024L;
    public static final long ONE_MB = ONE_KB * 1024L;
    public static final long CACHE_DATA_MAX_SIZE = ONE_MB * 3L;

    public static GoGankApplication getInstance() {
        return ourInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ourInstance=this;
        initGson();
        this.initReservoir();

    }

    private void initGson(){
        this.gson = new GsonBuilder().setDateFormat(GankApi.GANK_DATA_FORMAT).create();
    }
    private void initReservoir() {
        try {
            Reservoir.init(this, CACHE_DATA_MAX_SIZE, this.gson);
        } catch (Exception e) {
            //failure
            e.printStackTrace();
        }
    }
}
