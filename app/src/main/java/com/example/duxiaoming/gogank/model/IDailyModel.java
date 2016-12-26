package com.example.duxiaoming.gogank.model;


import com.example.duxiaoming.gogank.bean.GankDaily;

/**
 * Created by duxiaoming on 2016/12/12.
 * blog:m78star.com
 * description:
 */

public interface IDailyModel {


    /**
     * 查询每日数据
     * @param year 年
     * @param month 月
     * @param day 日
     * @return Observable<GankDaily>
     */
    rx.Observable<GankDaily> getDaily(int year, int month, int day);
}
