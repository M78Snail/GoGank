package com.example.duxiaoming.gogank.core.mvp;

/**
 * Created by duxiaoming on 2016/12/7.
 * blog:m78star.com
 * description:
 */

public interface Presenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();
}
