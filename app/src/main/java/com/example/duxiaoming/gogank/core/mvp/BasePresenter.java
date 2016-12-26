package com.example.duxiaoming.gogank.core.mvp;

import com.example.duxiaoming.gogank.gank.DataManager;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by duxiaoming on 2016/12/7.
 * blog:m78star.com
 * description:
 */

public class BasePresenter<T extends MvpView> implements Presenter<T> {

    private T mMvpView;
    public CompositeSubscription mCompositeSubscription;

    public DataManager mDataManager;

    @Override
    public void attachView(T mvpView) {
        this.mMvpView = mvpView;
        this.mCompositeSubscription = new CompositeSubscription();
        this.mDataManager = DataManager.getInstance();
    }

    @Override
    public void detachView() {
        this.mMvpView = null;
        this.mCompositeSubscription.unsubscribe();
        this.mCompositeSubscription = null;
        this.mDataManager = null;
    }

    public T getMvpView() {
        return mMvpView;
    }


}
