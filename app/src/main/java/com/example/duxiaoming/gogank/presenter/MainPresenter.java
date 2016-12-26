package com.example.duxiaoming.gogank.presenter;

import android.util.Log;

import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.example.duxiaoming.gogank.bean.BaseGankData;
import com.example.duxiaoming.gogank.bean.GankDaily;
import com.example.duxiaoming.gogank.constant.Constant;
import com.example.duxiaoming.gogank.core.mvp.BasePresenter;
import com.example.duxiaoming.gogank.gank.GankApi;
import com.example.duxiaoming.gogank.gank.GankType;
import com.example.duxiaoming.gogank.gank.GankTypeDict;
import com.example.duxiaoming.gogank.presenter.iview.MainView;
import com.example.duxiaoming.gogank.utils.DateUtils;
import com.example.duxiaoming.gogank.utils.ReservoirUtils;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Subscriber;

/**
 * Created by duxiaoming on 2016/12/12.
 * blog:m78star.com
 * description:
 */

public class MainPresenter extends BasePresenter<MainView> {

    private EasyDate currentDate;
    private int page;
    private ReservoirUtils reservoirUtils;


    /**
     * 查每日干货需要的特殊的类
     */
    public class EasyDate implements Serializable {
        private Calendar calendar;

        EasyDate(Calendar calendar) {
            this.calendar = calendar;
        }

        public int getYear() {
            return calendar.get(Calendar.YEAR);
        }

        public int getMonth() {
            return calendar.get(Calendar.MONTH) + 1;
        }

        public int getDay() {
            return calendar.get(Calendar.DAY_OF_MONTH);
        }

        public List<EasyDate> getPastTime() {
            List<EasyDate> easyDates = new ArrayList<>();
            for (int i = 0; i < GankApi.DEFAULT_DAILY_SIZE; i++) {
                /*
                 * - (page * DateUtils.ONE_DAY) 翻到哪页再找 一页有DEFAULT_DAILY_SIZE这么长
                 * - i * DateUtils.ONE_DAY 往前一天一天 找呀找
                 */
                long time = this.calendar.getTimeInMillis() - ((page - 1) * GankApi.DEFAULT_DAILY_SIZE * DateUtils.ONE_DAY) - i * DateUtils.ONE_DAY;
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(time);
                EasyDate date = new EasyDate(c);
                easyDates.add(date);
            }
            return easyDates;
        }

    }

    public MainPresenter() {
        this.reservoirUtils = new ReservoirUtils();
        long time = System.currentTimeMillis();
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        this.currentDate = new EasyDate(mCalendar);
        this.page = 1;
    }

    /**
     * 设置查询第几页
     *
     * @param page page
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * 获取当前页数量
     *
     * @return page
     */
    public int getPage() {
        return page;
    }

    /**
     * 查询每日数据
     *
     * @param refresh 是否是刷新
     * @param oldPage olaPage==GankTypeDict.DONT_SWITCH表示不是切换数据
     */
    public void getDaily(final boolean refresh, final int oldPage) {
        if (oldPage != GankTypeDict.DONT_SWITCH) {
            page = 1;
        }
        this.mCompositeSubscription.add(this.mDataManager.getDailyDataByNetwork(this.currentDate).subscribe(new Subscriber<List<GankDaily>>() {
            @Override
            public void onCompleted() {
                if (MainPresenter.this.mCompositeSubscription != null) {
                    MainPresenter.this.mCompositeSubscription.remove(this);
                }
            }

            @Override
            public void onError(Throwable e) {
                try {
                    Log.d("ERROR>>", e.getMessage());
                } catch (Throwable e1) {
                    e1.getMessage();
                } finally {
                    if (refresh) {
                        Type resultType = new TypeToken<List<GankDaily>>() {
                        }.getType();
                        MainPresenter.this.reservoirUtils.get(GankType.daily + "", resultType, new ReservoirGetCallback<List<GankDaily>>() {
                            @Override
                            public void onSuccess(List<GankDaily> object) {
                                if (oldPage != GankTypeDict.DONT_SWITCH) {
                                    if (MainPresenter.this.getMvpView() != null) {
                                        MainPresenter.this.getMvpView().onSwitchSuccess(GankType.daily);
                                    }
                                }
                                if (MainPresenter.this.getMvpView() != null)
                                    MainPresenter.this.getMvpView().onGetDailySuccess(object, refresh);
                                if (MainPresenter.this.getMvpView() != null)
                                    MainPresenter.this.getMvpView().onFailure(e);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                MainPresenter.this.switchFailure(oldPage, e);
                            }
                        });
                    } else {
                        MainPresenter.this.getMvpView().onFailure(e);
                    }
                }
            }

            @Override
            public void onNext(List<GankDaily> baseGankData) {
/*
                         * 如果是切换数据源
                         * page=1加载成功了
                         * 即刚才的loadPage
                         */
                if (oldPage != GankTypeDict.DONT_SWITCH) {
                    if (MainPresenter.this.getMvpView() != null)
                        MainPresenter.this.getMvpView().onSwitchSuccess(GankType.daily);
                }
                // 刷新缓存
                if (refresh)
                    MainPresenter.this.reservoirUtils.refresh(GankType.daily + "", baseGankData);
                if (MainPresenter.this.getMvpView() != null)
                    MainPresenter.this.getMvpView().onGetDailySuccess(baseGankData, refresh);

            }
        }));
    }

    /**
     * 获取每日消息详情
     *
     * @param results
     */
    public void getDailyDetail(final GankDaily.DailyResults results) {
        this.mCompositeSubscription.add(this.mDataManager.getDailyDetailByDailyResults(results).subscribe(new Subscriber<ArrayList<ArrayList<BaseGankData>>>() {
            @Override
            public void onCompleted() {
                if (MainPresenter.this.mCompositeSubscription != null) {
                    MainPresenter.this.mCompositeSubscription.remove(this);
                }
            }

            @Override
            public void onError(Throwable e) {
                try {
                    Log.d("ERROR>>", e.getMessage());
                } catch (Throwable e1) {
                    e1.getMessage();
                }
            }

            @Override
            public void onNext(ArrayList<ArrayList<BaseGankData>> detail) {
                if (MainPresenter.this.getMvpView() != null) {
                    MainPresenter.this.getMvpView().getDailyDetail(DateUtils.date2String(results.welfareData.get(0).publishedAt.getTime(), Constant.DAILY_DATE_FORMAT), detail);
                }
            }
        }));
    }

    public void getData(final int type, final boolean refresh, final int oldPage) {

        if (oldPage != GankTypeDict.DONT_SWITCH) {
            this.page = 1;
        }
        String gankType = GankTypeDict.type2UrlTypeDict.get(type);
        Log.d(">>>>>>>>", "gankType"+gankType);

        if (gankType == null) return;

        this.mCompositeSubscription.add(this.mDataManager.getDataByNetWork(gankType, GankApi.DEFAULT_DATA_SIZE, this.page)
                .subscribe(new Subscriber<ArrayList<BaseGankData>>() {
                    @Override
                    public void onCompleted() {
                        if (MainPresenter.this.mCompositeSubscription != null) {
                            MainPresenter.this.mCompositeSubscription.remove(this);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        try {
                            Log.d(">>>>>>>>", "失败"+e.getMessage());
                        } catch (Throwable e1) {
                            e1.getMessage();
                        } finally {
                            if (refresh) {
                                Type resultType = new TypeToken<ArrayList<BaseGankData>>() {
                                }.getType();
                                MainPresenter.this.reservoirUtils.get(type + "", resultType, new ReservoirGetCallback<ArrayList<BaseGankData>>() {
                                    @Override
                                    public void onSuccess(ArrayList<BaseGankData> object) {
                                        // 有缓存显示缓存数据
                                        if (oldPage != GankTypeDict.DONT_SWITCH) {
                                            if (MainPresenter.this.getMvpView() != null)
                                                MainPresenter.this.getMvpView().onSwitchSuccess(type);
                                        }
                                        if (MainPresenter.this.getMvpView() != null)
                                            MainPresenter.this.getMvpView().onGetDataSuccess(object, refresh);
                                        if (MainPresenter.this.getMvpView() != null)
                                            MainPresenter.this.getMvpView().onFailure(e);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        MainPresenter.this.switchFailure(oldPage, e);
                                    }
                                });
                            } else {
                                MainPresenter.this.getMvpView().onFailure(e);
                            }
                        }
                    }

                    @Override
                    public void onNext(ArrayList<BaseGankData> baseGankData) {
                        Log.d(">>>>>>>>","成功"+baseGankData.size());
                        /*
                         * 如果是切换数据源
                         * page=1加载成功了
                         * 即刚才的loadPage
                         */
                        if (oldPage != GankTypeDict.DONT_SWITCH) {
                            if (MainPresenter.this.getMvpView() != null)
                                MainPresenter.this.getMvpView().onSwitchSuccess(type);
                        }
                        // 刷新缓存
                        if (refresh)
                            MainPresenter.this.reservoirUtils.refresh(type + "", baseGankData);
                        if (MainPresenter.this.getMvpView() != null)
                            MainPresenter.this.getMvpView().onGetDataSuccess(baseGankData, refresh);
                    }
                }));
    }

    public void switchType(int type) {
        int oldPage = this.page;
        switch (type) {
            case GankType.daily:
                this.getDaily(true, oldPage);
                break;
            case GankType.android:
            case GankType.ios:
            case GankType.js:
            case GankType.resources:
            case GankType.welfare:
            case GankType.video:
            case GankType.app:
                this.getData(type, true, oldPage);
                break;
        }
    }

    /**
     * 切换分类失败   MainPresenter.this.switchFailure(oldPage, e);
     *
     * @param oldPage oldPage
     */
    private void switchFailure(int oldPage, Throwable e) {
        /*
         * 如果是切换数据源
         * 刚才尝试的page＝1失败了的请求
         * 加载失败
         * 会影响到原来页面的page
         * 在这里执行复原page操作
         */
        if (oldPage != GankTypeDict.DONT_SWITCH)
            MainPresenter.this.page = oldPage;
        if (MainPresenter.this.getMvpView() != null)
            MainPresenter.this.getMvpView().onFailure(e);
    }
}
