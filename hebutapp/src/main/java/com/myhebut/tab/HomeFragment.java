package com.myhebut.tab;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.myhebut.activity.WebViewActivity;
import com.myhebut.application.MyApplication;
import com.myhebut.classes.NetworkImageHolderView;
import com.myhebut.entity.Banner;
import com.myhebut.home.HomeBusActivity;
import com.myhebut.home.HomeCourseActivity;
import com.myhebut.home.HomeHistoryActivity;
import com.myhebut.home.HomeJwcLoginActivity;
import com.myhebut.home.HomeKuaidiActivity;
import com.myhebut.home.HomeMapActivity;
import com.myhebut.home.HomeMusicErrActivity;
import com.myhebut.home.HomeScoreActivity;
import com.myhebut.setting.SettingFeedbackActivity;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;
import com.myhebut.utils.UrlUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class HomeFragment extends BaseFragment {

    @ViewInject(R.id.convenientBanner)
    private ConvenientBanner convenientBanner;

    @ViewInject(R.id.sv_main)
    private ScrollView mScrollView;

    @ViewInject(R.id.tv_home_score)
    private TextView mTvScore;
    @ViewInject(R.id.tv_home_course)
    private TextView mTvCourse;
    @ViewInject(R.id.tv_home_daka)
    private TextView mTvDaka;
    @ViewInject(R.id.tv_home_bus)
    private TextView mTvBus;
    @ViewInject(R.id.tv_home_map)
    private TextView mTvMap;
    @ViewInject(R.id.tv_home_logout)
    private TextView mTvLogout;
    @ViewInject(R.id.tv_home_music)
    private TextView mTvMusic;
    @ViewInject(R.id.tv_home_feedback_icon)
    private TextView mTvFeedback;
    @ViewInject(R.id.tv_home_card)
    private TextView mTvCard;
    @ViewInject(R.id.tv_home_xiala)
    private TextView mTvXiala;
    @ViewInject(R.id.tv_home_kuaidi)
    private TextView mTvKuaidi;
//    @ViewInject(R.id.tv_home_more)
//    private TextView mTvMore;
    @ViewInject(R.id.tv_home_cet)
    private TextView mTvCet;
    @ViewInject(R.id.tv_home_history)
    private TextView mTvHistory;
    @ViewInject(R.id.tv_home_guide)
    private TextView mTvGuide;
    @ViewInject(R.id.tv_home_motto)
    private TextView mTvMotto;

    @OnClick(R.id.rl_home_more)
    private void scroll2bottom(View view) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @OnClick(R.id.ll_score)
    private void grade(View view) {
        String jsonData = SpUtil.getString(mainActivity, MyConstants.SCOREDATA, null);
        Intent intent;
        if (jsonData != null && jsonData.indexOf("gpa") != -1) {
            intent = new Intent(mainActivity, HomeScoreActivity.class);
        } else {
            intent = new Intent(mainActivity, HomeJwcLoginActivity.class);
            intent.putExtra("module", "score");
        }
        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.ll_course)
    private void course(View view) {
        String jsonData = SpUtil.getString(mainActivity, MyConstants.COURSEDATA, null);
        Intent intent;
        if (jsonData != null) {
            intent = new Intent(mainActivity, HomeCourseActivity.class);
        } else {
            intent = new Intent(mainActivity, HomeJwcLoginActivity.class);
            intent.putExtra("module", "course");
        }
        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.ll_daka)
    private void daka(View view) {
        Intent intent = new Intent(mainActivity, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getDakaUrl());
        intent.putExtra("title", "查打卡");
        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.ll_bus)
    private void bus(View view) {
        Intent intent = new Intent(mainActivity, HomeBusActivity.class);
        mainActivity.startActivity(intent);
    }


    @OnClick(R.id.ll_map)
    private void map(View view) {
        Intent intent = new Intent(mainActivity, HomeMapActivity.class);
        mainActivity.startActivity(intent);
    }


    @OnClick(R.id.ll_logout)
    private void logout(View view) {
        Intent intent = new Intent(mainActivity, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getLogoutUrl());
        intent.putExtra("title", "校园网注销");
        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.ll_music)
    private void music(View view) {
        HttpUtils http = HttpUtil.getHttp();
        http.send(HttpRequest.HttpMethod.GET, UrlUtil.getMusisTimesUrl(), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    int times = Integer.parseInt(responseInfo.result);
                    if (times > 0) {
                        Intent intent = new Intent(mainActivity, WebViewActivity.class);
                        intent.putExtra("href", UrlUtil.getMusicUrl());
                        intent.putExtra("title", "蜜思点歌台 - 点歌");
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(mainActivity, HomeMusicErrActivity.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Toast.makeText(mainActivity, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(mainActivity, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.ll_card)
    private void card(View view) {
        Intent intent = new Intent(mainActivity, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getCardUrl());
        intent.putExtra("title", "玩转一卡通");
        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.ll_kuaidi)
    private void kuaidi(View view) {
        Intent intent = new Intent(mainActivity, HomeKuaidiActivity.class);
        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.ll_cet)
    private void cet(View view) {
        Intent intent = new Intent(mainActivity, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getCetUrl());
        intent.putExtra("title", "查四六级");
        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.ll_history)
    private void history(View view) {
        Intent intent = new Intent(mainActivity, HomeHistoryActivity.class);
        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.ll_guide)
    private void guide(View view) {
        Intent intent = new Intent(mainActivity, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getGuideUrl());
        intent.putExtra("title", "新生指南");
        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.tv_home_feedback)
    private void feedback1(View view) {
        Intent intent = new Intent(mainActivity, SettingFeedbackActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.tv_home_feedback_icon)
    private void feedback2(View view) {
        Intent intent = new Intent(mainActivity, SettingFeedbackActivity.class);
        startActivity(intent);
    }


    @Override
    public View initView() {
        View root = View.inflate(mainActivity, R.layout.fragment_home, null);
        ViewUtils.inject(this, root);

        // 设置fonticon
        setFonticon();
        // 设置scrollview回弹
        OverScrollDecoratorHelper.setUpOverScroll(mScrollView);

        return root;
    }

    private void setFonticon() {
        Typeface iconfont = Typeface.createFromAsset(mainActivity.getAssets(), "function.ttf");
        mTvScore.setTypeface(iconfont);
        mTvCourse.setTypeface(iconfont);
        mTvDaka.setTypeface(iconfont);
        mTvBus.setTypeface(iconfont);
        mTvMap.setTypeface(iconfont);
        mTvLogout.setTypeface(iconfont);
        mTvMusic.setTypeface(iconfont);
        iconfont = Typeface.createFromAsset(mainActivity.getAssets(), "home.ttf");
        mTvFeedback.setTypeface(iconfont);
        mTvXiala.setTypeface(iconfont);
        iconfont = Typeface.createFromAsset(mainActivity.getAssets(), "more.ttf");
        mTvCard.setTypeface(iconfont);
//        mTvMore.setTypeface(iconfont);
        iconfont = Typeface.createFromAsset(mainActivity.getAssets(), "function_add.ttf");
        mTvKuaidi.setTypeface(iconfont);
        mTvCet.setTypeface(iconfont);
        mTvGuide.setTypeface(iconfont);
        iconfont = Typeface.createFromAsset(mainActivity.getAssets(), "history.ttf");
        mTvHistory.setTypeface(iconfont);
    }

    @Override
    public void initData() {
        // 获取banner
        MyApplication application = (MyApplication) mainActivity.getApplication();
        List<Banner> banners = application.getBanners();
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < banners.size(); i++) {
            urls.add(banners.get(i).getUrl());
        }
        convenientBanner.setPages(
                new CBViewHolderCreator<NetworkImageHolderView>() {
                    @Override
                    public NetworkImageHolderView createHolder() {
                        return new NetworkImageHolderView();
                    }
                }, urls)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused})
                        //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        //　轮播起始页
        convenientBanner.setcurrentitem(new Random().nextInt(3));
        // 设置一句话
        try {
            InputStream in = getResources().openRawResource(R.raw.motto);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf8"));// 注意编码
            int num = new Random().nextInt(27);
            String temp;
            int i = 0;
            while ((temp = br.readLine()) != null) {
                if (i == num) {
                    mTvMotto.setText(temp);
                    break;
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.initData();
    }


    // 开始自动翻页
    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
        convenientBanner.startTurning(4000);
    }


    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
    }


}
