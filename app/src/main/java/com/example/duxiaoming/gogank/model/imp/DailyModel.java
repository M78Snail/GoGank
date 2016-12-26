package com.example.duxiaoming.gogank.model.imp;

import com.example.duxiaoming.gogank.bean.GankDaily;
import com.example.duxiaoming.gogank.gank.EasyGank;
import com.example.duxiaoming.gogank.model.IDailyModel;

import rx.Observable;

/**
 * Created by duxiaoming on 2016/12/12.
 * blog:m78star.com
 * description:
 */

public class DailyModel implements IDailyModel {

    private static final DailyModel ourInstance=new DailyModel();

    public static DailyModel getInstance(){
        return ourInstance;
    }
    private DailyModel(){

    }
    @Override
    public Observable<GankDaily> getDaily(int year, int month, int day) {
        return EasyGank.getInstance().getGankService().getDaily(year,month,day);
    }
}
