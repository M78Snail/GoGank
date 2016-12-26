package com.example.duxiaoming.gogank.views;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.camnter.easyrecyclerview.widget.EasyRecyclerView;
import com.camnter.easyrecyclerview.widget.decorator.EasyBorderDividerItemDecoration;
import com.example.duxiaoming.gogank.R;
import com.example.duxiaoming.gogank.adapter.MainAdapter;
import com.example.duxiaoming.gogank.bean.BaseGankData;
import com.example.duxiaoming.gogank.bean.GankDaily;
import com.example.duxiaoming.gogank.constant.Constant;
import com.example.duxiaoming.gogank.core.BaseDrawerLayoutActivity;
import com.example.duxiaoming.gogank.gank.GankApi;
import com.example.duxiaoming.gogank.gank.GankType;
import com.example.duxiaoming.gogank.gank.GankTypeDict;
import com.example.duxiaoming.gogank.presenter.MainPresenter;
import com.example.duxiaoming.gogank.presenter.iview.MainView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class MainActivity extends BaseDrawerLayoutActivity implements MainView, MainAdapter.OnClickListener {

    @Bind(R.id.main_rv)
    EasyRecyclerView mainRv;

    private EasyBorderDividerItemDecoration dataDecoration;
    private EasyBorderDividerItemDecoration welfareDecoration;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    private MainAdapter mainAdapter;
    private MainPresenter presenter;

    private int emptyCount = 0;
    private static final int EMPTY_LIMIT = 5;

    private int gankType;

    @Override
    protected NavigationView.OnNavigationItemSelectedListener getNavigationItemSelectedListener() {
        return item -> MainActivity.this.menuItemChecked(item.getItemId());
    }

    @Override
    protected int[] getMenuItemIds() {
        return GankTypeDict.menuIds;
    }

    @Override
    protected void onMenuItemOnClick(MenuItem now) {
        if (GankTypeDict.menuId2TypeDict.indexOfKey(now.getItemId()) >= 0) {
            this.changeGankType(GankTypeDict.menuId2TypeDict.get(now.getItemId()));
        }

    }

    private void changeGankType(int gankType) {
        this.refresh(true);
        this.presenter.switchType(gankType);
    }

    @Override
    public void onSwipeRefresh() {
        this.refreshData(this.gankType);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    /**
     * Initialize the view in the layout
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void initViews(Bundle savedInstanceState) {
        this.dataDecoration = new EasyBorderDividerItemDecoration(this.getResources().getDimensionPixelOffset(R.dimen.data_border_divider_height),
                this.getResources().getDimensionPixelOffset(R.dimen.data_border_padding_infra_spans));
        this.welfareDecoration = new EasyBorderDividerItemDecoration(
                this.getResources().getDimensionPixelOffset(R.dimen.welfare_border_divider_height),
                this.getResources()
                        .getDimensionPixelOffset(R.dimen.welfare_border_padding_infra_spans));
        this.mainRv.addItemDecoration(dataDecoration);
        this.mLinearLayoutManager = this.mainRv.getLinearLayoutManager();
        this.mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        this.mActionBarHelper.setDrawerTitle(this.getResources().getString(R.string.app_menu));

    }

    /**
     * Initialize the View of the listener
     */
    @Override
    protected void initListeners() {
        this.mainRv.addOnScrollListener(getRecyclerViewOnScrollListener());
        this.mainAdapter.setOnItemClickListener((view, position) -> {
            Object o = MainActivity.this.mainAdapter.getItem(position);
            if (o instanceof BaseGankData) {
                BaseGankData baseGankData = (BaseGankData) o;
                if (GankTypeDict.urlType2TypeDict.get(baseGankData.type) == GankType.welfare) {
                    PictureActivity.startActivityByActivityOptionsCompat(MainActivity.this,
                            baseGankData.url, baseGankData.desc, view);
                } else {
                    EasyWebViewActivity.toUrl(MainActivity.this, baseGankData.url,
                            baseGankData.desc, baseGankData.type);
                }
            } else if (o instanceof GankDaily) {
                GankDaily gankDaily = (GankDaily) o;
                MainActivity.this.presenter.getDailyDetail(gankDaily.results);
            }
        });

    }

    /**
     * Initialize the Activity data
     */
    @Override
    protected void initData() {
        this.presenter = new MainPresenter();
        this.presenter.attachView(this);
        this.gankType = GankType.daily;

        //默认是每日干活
        this.mainAdapter = new MainAdapter(this, this.gankType);
        this.mainAdapter.setListener(this);
        this.mainRv.setAdapter(this.mainAdapter);

        this.refreshData(this.gankType);

    }


    /**
     * LinearLayoutManager 时的滚动监听
     *
     * @return RecyclerView.OnScrollListener
     */
    public RecyclerView.OnScrollListener getRecyclerViewOnScrollListener() {
        return new RecyclerView.OnScrollListener() {
            private boolean toLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
                    //不滚动
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (toLast && manager.findLastCompletelyVisibleItemPosition() == (manager.getItemCount() - 1)) {
                            MainActivity.this.loadMoreRequest();
                        }
                    }
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) layoutManager;
                    // 不滚动
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        /*
                         * 由于是StaggeredGridLayoutManager
                         * 取最底部数据可能有两个item，所以判断这之中有一个正好是 最后一条数据的index
                         * 就OK
                         */
                        int[] bottom = manager.findLastCompletelyVisibleItemPositions(new int[2]);
                        int lastItemCount = manager.getItemCount() - 1;
                        if (toLast && (bottom[0] == lastItemCount || bottom[1] == lastItemCount)) {
                            MainActivity.this.loadMoreRequest();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                /*
                 * dy 表示y轴滑动方向
                 * dx 表示x轴滑动方向
                 */
                // 正在向下滑动
                // 停止滑动或者向上滑动
                this.toLast = dy > 0;
            }
        };
    }

    /**
     * 请求加载更多
     */
    private void loadMoreRequest() {
        // 没数据了
        if (this.emptyCount >= EMPTY_LIMIT) {
            this.showToast(MainActivity.this.getString(R.string.main_empty_data),
                    Toast.LENGTH_LONG);
            return;
        }
        // 如果没在刷新中
        if (!MainActivity.this.isRefreshStatus()) {
            this.presenter.setPage(MainActivity.this.presenter.getPage() + 1);
            this.setRefreshStatus(false);
            this.loadMore(gankType);
            this.refresh(true);
        }

    }

    /**
     * 加载更多
     *
     * @param gankType gankType
     */
    private void loadMore(int gankType) {
        switch (gankType) {
            case GankType.daily:
                this.presenter.getDaily(false, GankTypeDict.DONT_SWITCH);
                break;
            case GankType.android:
            case GankType.ios:
            case GankType.js:
            case GankType.resources:
            case GankType.welfare:
            case GankType.video:
            case GankType.app:
                this.presenter.getData(this.gankType, false, GankTypeDict.DONT_SWITCH);
                break;
        }
    }

    private void refreshData(int gankType) {
        this.presenter.setPage(1);
        new Handler().post(() -> MainActivity.this.refresh(true));
        switch (gankType) {
            case GankType.daily:
                this.presenter.getDaily(true, GankTypeDict.DONT_SWITCH);
                break;
            case GankType.android:
            case GankType.ios:
            case GankType.js:
            case GankType.resources:
            case GankType.welfare:
            case GankType.video:
            case GankType.app:
                this.presenter.getData(this.gankType, true, GankTypeDict.DONT_SWITCH);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_about:
                AboutActivity.startActivity(MainActivity.this);
                return true;
            case R.id.menu_main_blog:
                EasyWebViewActivity.toUrl(this, Constant.BLOG_URL, Constant.BLOG_URL_TITLE);
                return true;
            case R.id.menu_main_home_page:
                EasyWebViewActivity.toUrl(this, GankApi.GANK_HOME_PAGE_URL, GankApi.GANK_HOME_PAGE_NAME);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    //    <-MainView->

    /**
     * 查询每日干货成功
     *
     * @param dailyData dailyData
     * @param refresh   是否刷新
     */
    @Override
    public void onGetDailySuccess(List<GankDaily> dailyData, boolean refresh) {
        if (refresh) {
            this.emptyCount = 0;
            this.mainAdapter.clear();
            this.mainAdapter.setList(dailyData);
        } else {
            this.mainAdapter.addAll(dailyData);
        }
        this.mainAdapter.notifyDataSetChanged();
        this.refresh(false);
        if (dailyData.size() == 0) this.emptyCount++;

    }

    @Override
    public void onGetDataSuccess(List<BaseGankData> data, boolean refresh) {

        if (refresh) {
            this.emptyCount = 0;
            this.mainAdapter.clear();
            this.mainAdapter.setList(data);
        } else {
            this.mainAdapter.addAll(data);
        }
        this.mainAdapter.notifyDataSetChanged();
        this.refresh(false);
        if (data.size() == 0) this.emptyCount++;

    }

    @Override
    public void onSwitchSuccess(int type) {
        this.emptyCount = 0;
        this.mainAdapter.setType(type);
        this.mainAdapter.clear();
        this.gankType = type;
        // 重置LayoutManager 和 分割线
        switch (gankType) {
            case GankType.daily:
            case GankType.android:
            case GankType.ios:
            case GankType.js:
            case GankType.resources:
            case GankType.video:
            case GankType.app:
                // 防止重复添加一样的
                this.clearDecoration();
                this.mainRv.setLayoutManager(this.mLinearLayoutManager);
                this.mainRv.addItemDecoration(this.dataDecoration);
                break;
            case GankType.welfare:
                this.clearDecoration();
                this.mainRv.setLayoutManager(this.mStaggeredGridLayoutManager);
                this.mainRv.addItemDecoration(this.welfareDecoration);
                break;
        }
    }

    private void clearDecoration() {
        this.mainRv.removeItemDecoration(this.dataDecoration);
        this.mainRv.removeItemDecoration(this.welfareDecoration);
    }

    @Override
    public void getDailyDetail(String title, ArrayList<ArrayList<BaseGankData>> detail) {
        DailyDetailActivity.startActivity(this, title, detail);
    }

    @Override
    public void onFailure(Throwable e) {
        this.refresh(false);
        this.setRefreshStatus(true);
        Snackbar.make(this.mainRv, R.string.main_load_error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        this.presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onClickPicture(String url, String title, View view) {
        PictureActivity.startActivityByActivityOptionsCompat(this, url, title, view);
    }
}
