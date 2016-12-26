package com.example.duxiaoming.gogank.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by duxiaoming on 2016/12/12.
 * blog:m78star.com
 * description:
 */

public class Error {
    // 每个请求都有error数据
    @SerializedName("error") public Boolean error;

    @SerializedName("msg") public String msg;
}
