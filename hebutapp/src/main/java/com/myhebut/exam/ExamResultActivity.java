package com.myhebut.exam;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.myhebut.application.MyApplication;
import com.myhebut.classes.DepthPageTransformer;
import com.myhebut.classes.ViewPagerScroller;
import com.myhebut.entity.JsonCommon;
import com.myhebut.entity.Question;
import com.myhebut.entity.User;
import com.myhebut.utils.DensityUtil;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class ExamResultActivity extends FragmentActivity {

    private int userId;

    // 0为未收藏,1为已收藏(这里默认全部未收藏)
    private List<String> questionsCollected = new ArrayList<>();

    private ExamAdapter examAdapter;

    private CardAdapter cardAdapter;
    // 答题卡弹出窗
    private PopupWindow mPopupWindow;

    private View cardView;

    private List<ExamResultPageFragment> fragmentList = new ArrayList<>();

    private ArrayList<Question> questions = new ArrayList<Question>();

    private ArrayList<String> answers = new ArrayList<String>();

    private int quesCount;

    @ViewInject(R.id.tv_exam_top_title)
    private TextView mTvTitle;

    @ViewInject(R.id.vp_exam_page)
    private ViewPager viewPager;

    @ViewInject(R.id.rg_exam_top_mode)
    private RadioGroup mRgMode;

    @ViewInject(R.id.tv_exam_bottom_count)
    private TextView mTvQuesCount;

    @ViewInject(R.id.tv_exam_bottom_true_count)
    private TextView mTvQuesTrueCount;

    @ViewInject(R.id.tv_exam_bottom_false_count)
    private TextView mTvQuesFalseCount;

    @ViewInject(R.id.iv_exam_bottom_true_count)
    private ImageView mIvQuesTrueCount;

    @ViewInject(R.id.iv_exam_bottom_false_count)
    private ImageView mIvQuesFalseCount;

    @ViewInject(R.id.cbx_exam_bottom_collect)
    private CheckBox mCbxCollect;

    private GridView mGvCard;

    private HttpUtils http;

    @OnClick(R.id.rl_exam_card)
    private void showCardByRl(View view) {
        showPopMenu();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_exam_practice_main);
        ViewUtils.inject(this);

        Intent intent = getIntent();
        questions = (ArrayList) intent.getSerializableExtra("questions");
        answers = (ArrayList) intent.getSerializableExtra("answers");

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        cardView = View.inflate(this, R.layout.activity_exam_pratice_card, null);
        mGvCard = (GridView) cardView.findViewById(R.id.gv_exam_bottom_card);

        mRgMode.setVisibility(View.GONE);
        mTvQuesTrueCount.setVisibility(View.GONE);
        mIvQuesTrueCount.setVisibility(View.GONE);
        mIvQuesFalseCount.setVisibility(View.GONE);
        mTvQuesFalseCount.setVisibility(View.GONE);
        mTvTitle.setVisibility(View.VISIBLE);

    }

    private void initData() {
        User user = ((MyApplication) this.getApplication()).getUser();
        userId = user.getUserId();
        http = HttpUtil.getHttp();

        quesCount = questions.size();
        for (int i = 0; i < quesCount; i++) {
            fragmentList.add(new ExamResultPageFragment(questions.get(i), answers.get(i)));
            questionsCollected.add("0");
        }

        initAdapter();
    }

    protected void initAdapter() {
        examAdapter = new ExamAdapter(getSupportFragmentManager());
        viewPager.setAdapter(examAdapter);
        // 切换动画
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        // 切换速度
        ViewPagerScroller scroller = new ViewPagerScroller(this);
        scroller.setScrollDuration(800);
        scroller.initViewPagerScroll(viewPager);
        // 设置答题卡适配器
        cardAdapter = new CardAdapter(this);
        mGvCard.setAdapter(cardAdapter);
        // 设置初始题号
        mTvQuesCount.setText("1/" + quesCount);

    }


    private void initEvent() {
        // 收藏复选按钮时间监听
        mCbxCollect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 选中,添加收藏;不能取消收藏,只能在收藏夹中移除(后一半条件防止setChecked导致重新添加)
                if (isChecked && questionsCollected.get(viewPager.getCurrentItem()).equals("0")) {
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("userId", userId + "");
                    params.addBodyParameter("questionId", fragmentList.get(viewPager.getCurrentItem()).getQuestionId());
                    http.send(HttpMethod.POST, UrlUtil.addCollectUrl(), params, new RequestCallBack<String>() {

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            try {
                                String jsonData = responseInfo.result;
                                Gson gson = new Gson();
                                JsonCommon jsonCommon = gson.fromJson(jsonData, JsonCommon.class);
                                if (jsonCommon.getStatus()) {
                                    Toast.makeText(ExamResultActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ExamResultActivity.this, "收藏夹中已存在该题", Toast.LENGTH_SHORT).show();
                                }
                                questionsCollected.set(viewPager.getCurrentItem(), "1");
                                mCbxCollect.setEnabled(false);
                            } catch (Exception e) {
                                Toast.makeText(ExamResultActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                                mCbxCollect.setChecked(false);
                            }

                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            Toast.makeText(ExamResultActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                            mCbxCollect.setChecked(false);
                        }
                    });
                }
            }
        });
        // 当前题号事件监听
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mTvQuesCount.setText((position + 1) + "/" + quesCount);
                // 添加过后收藏按钮禁止点击
                if (questionsCollected.get(position).equals("1")) {
                    mCbxCollect.setChecked(true);
                    mCbxCollect.setEnabled(false);
                } else {
                    mCbxCollect.setChecked(false);
                    mCbxCollect.setEnabled(true);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    class ExamAdapter extends FragmentStatePagerAdapter {

        public ExamAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }


    }

    class CardAdapter extends BaseAdapter {

        private Context context;

        public CardAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(context);
            } else {
                tv = (TextView) convertView;
            }
            tv.setText((getItemId(position) + 1) + "");
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(new LayoutParams(DensityUtil.dip2px(context, 45), DensityUtil.dip2px(context, 45)));

            tv.setBackgroundResource(R.drawable.card_item_default_bg_selector);
            // 添加事件监听
            tv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem((int) getItemId(position));
                    mPopupWindow.dismiss();
                }
            });
            return tv;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }


    // 设置默认状态为答题模式
    @Override
    public void finish() {
        super.finish();
    }


    private void showPopMenu() {
        cardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        cardView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
        LinearLayout ll_popup = (LinearLayout) cardView.findViewById(R.id.ll_popup);
        ll_popup.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_bottom_in));

        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(this);
            mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
            mPopupWindow.setHeight(LayoutParams.MATCH_PARENT);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
        }

        mPopupWindow.setContentView(cardView);
        mPopupWindow.showAtLocation(mTvQuesCount, Gravity.BOTTOM, 0, 0);
        mPopupWindow.update();
    }


    public void back(View view) {
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
