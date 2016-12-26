package com.example.duxiaoming.gogank.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.duxiaoming.gogank.R;
import com.example.duxiaoming.gogank.core.BaseToolbarActivity;
import com.example.duxiaoming.gogank.utils.GlideUtils;

import butterknife.Bind;


/**
 *
 */
public class AboutActivity extends BaseToolbarActivity {
    @Bind(R.id.about_avatar_iv)
    ImageView aboutAvatarIv;


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        this.showBack();
        this.setTitle(this.getString(R.string.about_title));
        GlideUtils.displayCircleHeader(this.aboutAvatarIv, R.mipmap.icon);
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }
}
