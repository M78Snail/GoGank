package com.example.duxiaoming.gogank.gank;

import com.example.duxiaoming.gogank.bean.GankDaily;
import com.example.duxiaoming.gogank.bean.GankData;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by duxiaoming on 2016/12/12.
 * blog:m78star.com
 * description:
 */

public interface GankService {

    /**
     * @param year year
     * @param month month
     * @param day day
     * @return Observable<GankDaily>
     */
    @GET("day/{year}/{month}/{day}")
    Observable<GankDaily> getDaily(@Path("year") int year, @Path("month") int month, @Path("day") int day);

    /**
     * 找妹子、Android、iOS、前端、扩展资源、休息视频
     *
     * @param type 数据类型
     * @param size 数据个数
     * @param page 第几页
     * @return Observable<GankWelfare>
     */
    @GET("data/{type}/{size}/{page}")
    Observable<GankData> getData(
            @Path("type") String type, @Path("size") int size, @Path("page") int page);
}
