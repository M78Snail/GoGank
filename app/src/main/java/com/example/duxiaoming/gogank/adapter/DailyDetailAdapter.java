package com.example.duxiaoming.gogank.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.camnter.easyrecyclerview.adapter.EasyRecyclerViewAdapter;
import com.camnter.easyrecyclerview.holder.EasyRecyclerViewHolder;
import com.example.duxiaoming.gogank.R;
import com.example.duxiaoming.gogank.bean.BaseGankData;
import com.example.duxiaoming.gogank.gank.GankType;
import com.example.duxiaoming.gogank.gank.GankTypeDict;
import com.example.duxiaoming.gogank.utils.GlideUtils;
import com.example.duxiaoming.gogank.utils.ResourcesUtils;
import com.example.duxiaoming.gogank.widget.RatioImageView;

import java.util.List;

/**
 * Created by duxiaoming on 2016/12/14.
 * blog:m78star.com
 * description:
 */

public class DailyDetailAdapter extends EasyRecyclerViewAdapter {
    private Context context;

    private int cardItemPadding;
    private int cardCategoryPaddingTopBottom;
    private int cardItemDivider;

    private int viaTextSize;
    private String viaModel;
    private String viaModelKey;
    private ColorStateList viaColorStateList;

    private static final int dividerColor = 0xffCCCCCC;

    private DailyDetailAdapter.onCardItemClickListener onCardItemClickListener;

    public DailyDetailAdapter(Context context) {
        this.context = context;
        Resources res = this.context.getResources();
        this.initCardItemStyle(res);
        this.initViaTextStyle(res);
    }

    private void initCardItemStyle(Resources res) {
        this.cardItemPadding = res.getDimensionPixelOffset(R.dimen.card_item_content_padding);
        this.cardCategoryPaddingTopBottom = res.getDimensionPixelOffset(
                R.dimen.card_category_padding_top_bottom);
        this.cardItemDivider = res.getDimensionPixelOffset(R.dimen.card_item_divider);
    }

    private void initViaTextStyle(Resources res) {
        int viaTextColor = ResourcesUtils.getColor(this.context, R.color.common_item_via);
        this.viaTextSize = res.getDimensionPixelSize(R.dimen.item_via_tv);
        this.viaModel = res.getString(R.string.common_via);
        this.viaModelKey = res.getString(R.string.common_via_key);
        this.viaColorStateList = ResourcesUtils.createColorStateList(viaTextColor, viaTextColor,
                viaTextColor, viaTextColor);
    }

        @Override
    public int[] getItemLayouts() {
        return new int[]{R.layout.item_daily_detail};
    }

    @Override
    public void onBindRecycleViewHolder(EasyRecyclerViewHolder easyRecyclerViewHolder, int position) {
        List<BaseGankData> categoryData = this.getItem(position);
        if (categoryData == null || categoryData.size() <= 0) return;
        LinearLayout detailLL = easyRecyclerViewHolder.findViewById(R.id.daily_detail_ll);
        detailLL.removeAllViews();

        for (int i = 0; i < categoryData.size(); i++) {
            final BaseGankData baseGankData = categoryData.get(i);
            if (i == 0) {
                TextView categoryTv = createCardCategory(baseGankData.type);
                detailLL.addView(categoryTv);
                detailLL.addView(this.createDivider());
            }
            if (GankTypeDict.urlType2TypeDict.get(baseGankData.type) == GankType.welfare) {
                Log.d("GankTypeDict>>>",baseGankData.type+"+"+baseGankData.url);
                RatioImageView welfareIV = createRatioImageView();
                GlideUtils.display(welfareIV, baseGankData.url);
                welfareIV.setOnClickListener(v -> {
                    if (DailyDetailAdapter.this.onCardItemClickListener != null) {
                        DailyDetailAdapter.this.onCardItemClickListener.onWelfareOnClick(
                                baseGankData.url, baseGankData.desc, v);
                    }
                });
                Log.d("GankTypeDict>>>",baseGankData.type+">>>>>>>");
                detailLL.addView(welfareIV);
            } else {
                TextView itemText = this.createCardItemText(baseGankData);
                detailLL.addView(itemText);
            }
        }


    }

    @Override
    public int getRecycleViewItemType(int position) {
        return 0;
    }

    private TextView createCardCategory(String urlType) {
        TextView categoryTV = new TextView(this.context);
        categoryTV.setPadding(this.cardItemPadding, this.cardCategoryPaddingTopBottom,
                this.cardItemPadding, this.cardCategoryPaddingTopBottom);
        categoryTV.setText(urlType);
        categoryTV.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        categoryTV.setTextSize(20);
        categoryTV.setTextColor(GankTypeDict.urlType2ColorDict.get(urlType));
        categoryTV.setBackgroundResource(R.drawable.shape_card_background_default);
        return categoryTV;
    }

    private View createDivider() {
        View divider = new View(this.context);
        divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, this.cardItemDivider));
        divider.setBackgroundColor(dividerColor);
        return divider;
    }

    private RatioImageView createRatioImageView() {
        return (RatioImageView) LayoutInflater.from(this.context).inflate(R.layout.view_card_radio_view, null);
    }

    private TextView createCardItemText(BaseGankData baseGankData) {
        TextView itemText = (TextView) LayoutInflater.from(this.context)
                .inflate(R.layout.view_card_item, null);
        itemText.setPadding(this.cardItemPadding, this.cardItemPadding, this.cardItemPadding,
                this.cardItemPadding);
        String content = baseGankData.desc.trim() +
                "   " +
                String.format(this.viaModel, baseGankData.who);
        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        ssb.setSpan(new TextAppearanceSpan("serif", Typeface.ITALIC, this.viaTextSize,
                        this.viaColorStateList, this.viaColorStateList), content.indexOf(this.viaModelKey),
                content.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        itemText.setText(ssb);
        itemText.setTag(R.id.tag_card_item_url, baseGankData.url);
        itemText.setTag(R.id.tag_card_item_desc, baseGankData.desc.trim());
        itemText.setTag(R.id.tag_card_item_type, baseGankData.type);
        itemText.setOnClickListener(v -> {
            if (DailyDetailAdapter.this.onCardItemClickListener != null) {
                DailyDetailAdapter.this.onCardItemClickListener.onCardItemOnClick(
                        (String) v.getTag(R.id.tag_card_item_type),
                        (String) v.getTag(R.id.tag_card_item_desc),
                        (String) v.getTag(R.id.tag_card_item_url));
            }
        });
        return itemText;
    }

    public void setOnCardItemClickListener(DailyDetailAdapter.onCardItemClickListener onCardItemClickListener) {
        this.onCardItemClickListener = onCardItemClickListener;
    }


    public interface onCardItemClickListener {

        void onCardItemOnClick(String urlType, String title, String url);

        void onWelfareOnClick(String url, String title, View v);
    }
}
