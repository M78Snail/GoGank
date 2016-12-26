package com.example.duxiaoming.gogank.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.duxiaoming.gogank.R;
import com.example.duxiaoming.gogank.core.BaseToolbarActivity;
import com.example.duxiaoming.gogank.gank.GankType;
import com.example.duxiaoming.gogank.gank.GankTypeDict;
import com.example.duxiaoming.gogank.utils.DeviceUtils;
import com.example.duxiaoming.gogank.utils.IntentUtils;
import com.example.duxiaoming.gogank.utils.ShareUtils;
import com.example.duxiaoming.gogank.utils.WebViewUtils;

import butterknife.Bind;

/**
 * Created by duxiaoming on 2016/12/14.
 * blog:m78star.com
 * description:
 */

public class EasyWebViewActivity extends BaseToolbarActivity {
    private static final String EXTRA_URL = "com.camnter.easygank.EXTRA_URL";
    private static final String EXTRA_TITLE = "com.camnter.easygank.EXTRA_TITLE";

    // For gank api
    private static final String EXTRA_GANK_TYPE = "com.camnter.easygank.EXTRA_GANK_TYPE";

    private static final int PROGRESS_RATIO = 1000;

    @Bind(R.id.webview_pb)
    ProgressBar webviewPb;
    @Bind(R.id.webview)
    WebView webview;

    private boolean goBack = false;
    private static final int RESET_GO_BACK_INTERVAL = 2666;
    private static final int MSG_WHAT_RESET_GO_BACK = 206;
    private final Handler mHandler = new Handler(msg -> {
        switch (msg.what) {
            case EasyWebViewActivity.MSG_WHAT_RESET_GO_BACK:
                EasyWebViewActivity.this.goBack = false;
                return true;
        }
        return false;
    });


    /**
     * @param context Any context
     * @param url A valid url to navigate to
     * @param titleResId A String resource to display as the title
     */
    public static void toUrl(Context context, String url, int titleResId) {
        toUrl(context, url, context.getString(titleResId));
    }


    /**
     * @param context Any context
     * @param url A valid url to navigate to
     * @param title A title to display
     */
    public static void toUrl(Context context, String url, String title) {
        Intent intent = new Intent(context, EasyWebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_TITLE, title);
        context.startActivity(intent);
    }


    /**
     * For gank api
     *
     * @param context context
     * @param url url
     * @param title title
     * @param type type
     */
    public static void toUrl(Context context, String url, String title, String type) {
        Intent intent = new Intent(context, EasyWebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_GANK_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {

    }
    private String getUrl() {
        return IntentUtils.getStringExtra(this.getIntent(), EXTRA_URL);
    }


    private String getUrlTitle() {
        return IntentUtils.getStringExtra(this.getIntent(), EXTRA_TITLE);
    }

    /**
     * For gank api
     *
     * @return tyep
     */
    private String getGankType() {
        return IntentUtils.getStringExtra(this.getIntent(), EXTRA_GANK_TYPE);
    }



    @Override
    protected void initData() {
        this.enableJavascript();
        this.enableCaching();
        this.enableCustomClients();
        this.enableAdjust();
        this.zoomedOut();
        this.webview.loadUrl(this.getUrl());
        this.showBack();
        this.setTitle(this.getUrlTitle());

        if (this.getGankType() == null) return;
        if (GankTypeDict.urlType2TypeDict.get(this.getGankType()) == GankType.video) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void enableJavascript() {
        this.webview.getSettings().setJavaScriptEnabled(true);
        this.webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    }

    private void enableCaching() {
        this.webview.getSettings().setAppCachePath(getFilesDir() + getPackageName() + "/cache");
        this.webview.getSettings().setAppCacheEnabled(true);
        this.webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    private void enableCustomClients(){
        this.webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("www.vmovier.com")) {
                    WebViewUtils.injectCSS(EasyWebViewActivity.this,
                            EasyWebViewActivity.this.webview, "vmovier.css");
                } else if (url.contains("video.weibo.com")) {
                    WebViewUtils.injectCSS(EasyWebViewActivity.this,
                            EasyWebViewActivity.this.webview, "weibo.css");
                } else if (url.contains("m.miaopai.com")) {
                    WebViewUtils.injectCSS(EasyWebViewActivity.this,
                            EasyWebViewActivity.this.webview, "miaopai.css");
                }
            }
        });
        this.webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                EasyWebViewActivity.this.webviewPb.setProgress(progress);
                setProgress(progress * PROGRESS_RATIO);
                if (progress >= 80) {
                    EasyWebViewActivity.this.webviewPb.setVisibility(View.GONE);
                } else {
                    EasyWebViewActivity.this.webviewPb.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void enableAdjust() {
        this.webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        this.webview.getSettings().setLoadWithOverviewMode(true);
    }

    private void zoomedOut() {
        this.webview.getSettings().setLoadWithOverviewMode(true);
        this.webview.getSettings().setUseWideViewPort(true);
        this.webview.getSettings().setSupportZoom(true);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_web_view, menu);
        return true;
    }


    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.web_refresh:
                this.refreshWebView();
                return true;
            case R.id.web_copy:
                DeviceUtils.copy2Clipboard(this, this.webview.getUrl());
                Snackbar.make(this.webview, this.getString(R.string.common_copy_success),
                        Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.menu_web_share:
                ShareUtils.share(this, this.getUrl());
                return true;
            case R.id.web_switch_screen_mode:
                this.switchScreenConfiguration(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    protected void refreshWebView() {
        this.webview.reload();
    }

    public void switchScreenConfiguration(MenuItem item) {
        if (this.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            if (item != null) item.setTitle(this.getString(R.string.menu_web_vertical));
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            if (item != null) item.setTitle(this.getString(R.string.menu_web_horizontal));
        }
    }
}
