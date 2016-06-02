package com.myhebut.exam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
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
import com.myhebut.entity.JsonQuestion;
import com.myhebut.entity.Question;
import com.myhebut.entity.User;
import com.myhebut.listener.ExamListener;
import com.myhebut.manager.ExamManager;
import com.myhebut.utils.DensityUtil;
import com.myhebut.utils.HebutUtil;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.UrlUtil;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class ExamActivity extends FragmentActivity implements ExamListener {

    // 从intent收到的数据,数据url
    private String dataUrl;
    // 从intent收到的数据,模块功能
    private String module;
    // 从intent收到的数据,对应章节
    private String section;
    // 从intent收到的数据,0为手动移除错题,1为自动移除错题
    private String noteMode;
    // nextFlag为true表明错题删除后需要跳转到下一页
    private boolean nextFlag;

    private String subject;

    private int userId;

    // 0为回答错误,1为回答正确
    private List<String> answerStatus = new ArrayList<>();

    // 0为未答,1为正确,2为错误,3为考试模式下的已答状态
    private List<String> questionsStatus = new ArrayList<>();

    // 0为未收藏,1为已收藏(这里默认全部未收藏)
    private List<String> questionsCollected = new ArrayList<>();

    // 0为未删除,1为已删除
    private List<String> questionsDeleted = new ArrayList<>();

    private ExamAdapter examAdapter;

    private CardAdapter cardAdapter;
    // 答题卡弹出窗
    private PopupWindow mPopupWindow;

    private View cardView;

    // false为答题模式,true为学习模式
    public static boolean mode;

    private List<ExamPageFragment> fragmentList = new ArrayList<>();

    private ArrayList<String> wrongAnswers;

    private int quesCount;

    private int quesTrueCount;

    private int quesFalseCount;

    private JsonQuestion jsonQuestion;

    private int seconds = 1800;

    private int minute;

    private int second;

    @ViewInject(R.id.vp_exam_page)
    private ViewPager viewPager;

    @ViewInject(R.id.rg_exam_top_mode)
    private RadioGroup mRgMode;

    @ViewInject(R.id.rb_exam_top_left)
    private RadioButton mRbLeft;

    @ViewInject(R.id.rb_exam_top_right)
    private RadioButton mRbRight;

    @ViewInject(R.id.tv_exam_top_time)
    private TextView mTvExamTime;

    @ViewInject(R.id.tv_exam_top_submit)
    private TextView mTvSubmit;

    @ViewInject(R.id.tv_exam_bottom_count)
    private TextView mTvQuesCount;

    @ViewInject(R.id.iv_exam_bottom_true_count)
    private ImageView mIvQuesTrueCount;

    @ViewInject(R.id.iv_exam_bottom_false_count)
    private ImageView mIvQuesFalseCount;

    @ViewInject(R.id.tv_exam_bottom_true_count)
    private TextView mTvQuesTrueCount;

    @ViewInject(R.id.tv_exam_bottom_false_count)
    private TextView mTvQuesFalseCount;

    @ViewInject(R.id.cbx_exam_bottom_collect)
    private CheckBox mCbxCollect;

    @ViewInject(R.id.btn_exam_bottom_delete)
    private Button mBtnDelete;

    private GridView mGvCard;

    private HttpUtils http;

    private Gson gson;

    private boolean isExam;

    private User user;

    private float score;

    private Handler handler;

    private Runnable runnable;

    @OnClick(R.id.rl_exam_card)
    private void showCardByRl(View view) {
        showPopMenu();
    }

    @OnClick(R.id.tv_exam_top_submit)
    private void submit(View view) {
        dialog();
    }

    @OnClick(R.id.btn_exam_bottom_delete)
    private void delete(final View view) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", userId + "");
        params.addBodyParameter("questionId", fragmentList.get(viewPager.getCurrentItem()).getQuestionId());
        http.send(HttpMethod.POST, UrlUtil.deleteUrl(module), params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                // 非自动移除错题的模式下才显示提示
                if (!"auto".equals(noteMode)) {
                    Toast.makeText(ExamActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                }
                questionsDeleted.set(viewPager.getCurrentItem(), "1");
                ((Button) view).setEnabled(false);
                ((Button) view).setText("已删除");
                // 跳转下一页
                if (nextFlag) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    nextFlag = false;
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(ExamActivity.this, "删除错题失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_exam_practice_main);
        ViewUtils.inject(this);

        Intent intent = getIntent();
        dataUrl = intent.getStringExtra("url");
        module = intent.getStringExtra("module");
        subject = intent.getStringExtra("subject");
        section = intent.getStringExtra("section");
        noteMode = intent.getStringExtra("noteMode");
        isExam = "exam".equals(module);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        cardView = View.inflate(this, R.layout.activity_exam_pratice_card, null);
        mGvCard = (GridView) cardView.findViewById(R.id.gv_exam_bottom_card);
        TextView mTvSection = (TextView) cardView.findViewById(R.id.tv_exam_card_section);
        mTvSection.setText(section);

        // 如果是错题集或者收藏夹,显示移除功能,隐藏收藏功能
        if ("note".equals(module) || "collect".equals(module)) {
            mBtnDelete.setVisibility(View.VISIBLE);
            mCbxCollect.setVisibility(View.GONE);
        }

        // 考试模式则要显示交卷按钮和时间计时并隐藏模式切换功能和对题错题计数
        if (isExam) {
            mTvExamTime.setVisibility(View.VISIBLE);
            mTvSubmit.setVisibility(View.VISIBLE);
            mRgMode.setVisibility(View.GONE);
            mTvQuesTrueCount.setVisibility(View.GONE);
            mTvQuesFalseCount.setVisibility(View.GONE);
            mIvQuesTrueCount.setVisibility(View.GONE);
            mIvQuesFalseCount.setVisibility(View.GONE);
            // 设置计时功能
            setTime();
        }
    }

    private void setTime() {
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                seconds--;
                if (seconds > -1) {
                    minute = seconds / 60;
                    second = seconds - minute * 60;
                    if (second > 9 && minute > 9) {
                        mTvExamTime.setText("倒计时 " + minute + ":" + second);
                    } else if (second > 9) {
                        mTvExamTime.setText("倒计时 0" + minute + ":" + second);
                    } else if (minute > 9) {
                        mTvExamTime.setText("倒计时 " + minute + ":0" + second);
                    } else {
                        mTvExamTime.setText("倒计时 0" + minute + ":0" + second);
                    }
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void initData() {
        // viewpage事件监听(此处利用了接口回调)
        ExamManager manager = ExamManager.getInstance();
        manager.setPageChangedListener(this);

        http = HttpUtil.getHttp();
        gson = new Gson();
        http.send(HttpMethod.GET, dataUrl, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String jsonData = responseInfo.result;
                try {
                    parseData(jsonData);
                    initAdapter();
                    // 数据初始化完毕后才可以交卷/切换模式/收藏
                    mTvSubmit.setEnabled(true);
                    mRbLeft.setEnabled(true);
                    mRbRight.setEnabled(true);
                    mCbxCollect.setEnabled(true);
                } catch (Exception e) {
                    Toast.makeText(ExamActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(ExamActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });

        user = ((MyApplication) this.getApplication()).getUser();
        userId = user.getUserId();
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

    private void parseData(String jsonData) {
        jsonQuestion = gson.fromJson(jsonData, JsonQuestion.class);

        quesCount = jsonQuestion.getQuestions().size();
        for (int i = 0; i < quesCount; i++) {
            Question question = jsonQuestion.getQuestions().get(i);
            fragmentList.add(new ExamPageFragment(question, userId, isExam));
            questionsStatus.add("0");
            questionsCollected.add("0");
            questionsDeleted.add("0");
            answerStatus.add("0");
        }

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
                                JsonCommon jsonCommon = gson.fromJson(jsonData, JsonCommon.class);
                                if (jsonCommon.getStatus()) {
                                    Toast.makeText(ExamActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ExamActivity.this, "收藏夹中已存在该题", Toast.LENGTH_SHORT).show();
                                }
                                questionsCollected.set(viewPager.getCurrentItem(), "1");
                                mCbxCollect.setEnabled(false);
                            } catch (Exception e) {
                                Toast.makeText(ExamActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                                mCbxCollect.setChecked(false);
                            }

                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            Toast.makeText(ExamActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
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
                if (questionsDeleted.get(position).equals("1")) {
                    mBtnDelete.setText("已删除");
                    mBtnDelete.setEnabled(false);
                } else {
                    mBtnDelete.setText("移除");
                    mBtnDelete.setEnabled(true);
                }

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // 模式转换监听
        mRgMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                int currentItem = viewPager.getCurrentItem();
                if (radioButton.getText().equals("学习模式")) {
                    mode = true;
                    // 当前页与前后两页已经预先加载完成,需要在此控制模式
                    fragmentList.get(currentItem).changeToLearnMode();
                    if (currentItem != 0)
                        if (fragmentList.get(currentItem - 1).getFlag())
                            fragmentList.get(currentItem - 1).changeToLearnMode();
                    if (currentItem != fragmentList.size() - 1)
                        if (fragmentList.get(currentItem + 1).getFlag())
                            fragmentList.get(currentItem + 1).changeToLearnMode();
                } else {
                    mode = false;
                    // 当前页与前后两页已经预先加载完成,需要在此控制模式
                    fragmentList.get(currentItem).changeToAnswerMode();
                    if (currentItem != 0)
                        if (fragmentList.get(currentItem - 1).getFlag())
                            fragmentList.get(currentItem - 1).changeToAnswerMode();
                    if (currentItem != fragmentList.size() - 1)
                        if (fragmentList.get(currentItem + 1).getFlag())
                            fragmentList.get(currentItem + 1).changeToAnswerMode();
                }

            }
        });
        // 答题卡隐藏事件
        cardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
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
            tv.setLayoutParams(new GridView.LayoutParams(DensityUtil.dip2px(context, 45), DensityUtil.dip2px(context, 45)));
            if (questionsStatus.get(position).equals("1")) {
                tv.setBackgroundResource(R.drawable.card_item_true_bg);
            } else if (questionsStatus.get(position).equals("2")) {
                tv.setBackgroundResource(R.drawable.card_item_false_bg);
            } else if (questionsStatus.get(position).equals("3")) {
                tv.setBackgroundResource(R.drawable.card_item_default_bg_s);
            } else {
                tv.setBackgroundResource(R.drawable.card_item_default_bg_selector);
            }
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

    @Override
    public void nextPage() {
        // 自动移除错题模式下,跳转下一页前先删除错题
        if ("note".equals(module) && "auto".equals(noteMode)) {
            mBtnDelete.performClick();
            nextFlag = true;
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    @Override
    public void addTrueCount() {
        mTvQuesTrueCount.setText(++quesTrueCount + "");
    }

    @Override
    public void addFalseCount() {
        mTvQuesFalseCount.setText(++quesFalseCount + "");
    }

    // 设置默认状态为答题模式
    @Override
    public void finish() {
        // 停止计时
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        mode = false;
        super.finish();
    }

    @Override
    public void changeCard2Default() {
        questionsStatus.set(viewPager.getCurrentItem(), "0");
        cardAdapter.notifyDataSetChanged();
    }

    @Override
    public void changeCard2True() {
        questionsStatus.set(viewPager.getCurrentItem(), "1");
        cardAdapter.notifyDataSetChanged();
    }

    @Override
    public void changeCard2False() {
        questionsStatus.set(viewPager.getCurrentItem(), "2");
        cardAdapter.notifyDataSetChanged();
    }

    @Override
    public void changeCard2Checked() {
        questionsStatus.set(viewPager.getCurrentItem(), "3");
        cardAdapter.notifyDataSetChanged();
    }

    @Override
    public void changeAnswerStatus2T() {
        answerStatus.set(viewPager.getCurrentItem(), "1");
    }

    @Override
    public void changeAnswerStatus2F() {
        answerStatus.set(viewPager.getCurrentItem(), "0");
    }


    private void showPopMenu() {

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
        TextView textView = new TextView(this);
        textView.setText("sdfsdf");
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);
        mPopupWindow.setContentView(cardView);
        mPopupWindow.showAtLocation(mTvQuesCount, Gravity.BOTTOM, 0, 0);
        mPopupWindow.update();
    }

    private void dialog() {
        // 若为true,则有题目未答
        boolean flag = false;
        for (int i = 0; i < questionsStatus.size(); i++) {
            if (questionsStatus.get(i).equals("0"))
                flag = true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示"); //设置标题
        if (flag) {
            builder.setMessage("您还有题目未答,确定要提交试卷吗?"); //设置内容
        } else {
            builder.setMessage("您确定要提交试卷吗?"); //设置内容
        }
        //        mBuilder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                // 停止计时
                handler.removeCallbacks(runnable);
                checkAnswer();
            }
        });
        builder.setNegativeButton("继续答题", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showPopMenu();
            }
        });

        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    private void checkAnswer() {
        if ("3".equals(subject)) {
            score = 50;
        } else {
            score = 40;
        }
        // 获取错题答案
        wrongAnswers = new ArrayList<>();
        String examAnswer;
        for (int i = 0; i < quesCount; i++) {
            if (answerStatus.get(i).equals("0")) {
                examAnswer = fragmentList.get(i).getExamAnswer();
                wrongAnswers.add(examAnswer);
                // 已回答的错题自动添加到错题集
                if (examAnswer != null) {
                    addNote(fragmentList.get(i).getQuestionId());
                }

                if (fragmentList.get(i).getType().equals("0")) {
                    // 单选0.5分一道
                    score -= 0.5;
                } else {
                    // 多选分值需要判断
                    if ("0".equals(subject)) {
                        score -= 1;
                    } else {
                        score -= 0.5;
                    }
                }
            }
        }
        // 显示考试结果页面
        showResultView();
        // 添加到考试记录
        addRecord();
    }

    private void showResultView() {
        // 显示布局
        setContentView(R.layout.activity_exam_result);
        // 设置显示用户昵称
        TextView mTvName = (TextView) findViewById(R.id.tv_exam_result_name);
        mTvName.setText(user.getNickName());
        // 设置显示科目
        TextView mTvSubject = (TextView) findViewById(R.id.tv_exam_result_subject);
        // 设置显示头像
        ImageView mIvAvatar = (ImageView) findViewById(R.id.iv_exam_result_avatar);
        String url = UrlUtil.getAvatarUrl(user.getAvatar());
        Picasso.with(ExamActivity.this).load(url).placeholder(R.mipmap.user_avatar_default).
                error(R.mipmap.user_avatar_default).into(mIvAvatar);

        switch (subject) {
            case "0":
                mTvSubject.setText("马原");
                break;
            case "1":
                mTvSubject.setText("毛概(上)");
                break;
            case "2":
                mTvSubject.setText("毛概(下)");
                break;
            case "3":
                mTvSubject.setText("史纲");
                break;
            default:
                break;
        }
        // 设置成绩
        TextView mTvScore = (TextView) findViewById(R.id.tv_exam_result_score);
        mTvScore.setText(score + "");
        // 设置考试所用时间
        TextView mTvTimeMin = (TextView) findViewById(R.id.tv_exam_result_time_min);
        TextView mTvTimeSec = (TextView) findViewById(R.id.tv_exam_result_time_sec);

        mTvTimeMin.setText(29 - minute + "");
        mTvTimeSec.setText(60 - second + "");
    }

    private void addRecord() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", userId + "");
        params.addBodyParameter("subject", subject);
        params.addBodyParameter("begin_time", HebutUtil.getDate());
        params.addBodyParameter("last_time", (29 - minute) + "分" + (60 - second) + "秒");
        params.addBodyParameter("score", score + "");
        params.addBodyParameter("seconds", (1800 - seconds) + "");
        http.send(HttpMethod.POST, UrlUtil.addRecordUrl(), params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.d("exam", responseInfo.result);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Log.d("exam", msg);
            }
        });
    }

    // 添加到错题集
    private void addNote(String questionId) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", userId + "");
        params.addBodyParameter("questionId", questionId);
        http.send(HttpMethod.POST, UrlUtil.addNoteUrl(), params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                // TODO
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                // TODO
            }
        });

    }


    public void back(View view) {
        finish();
    }

    public void onWrongClick(View view) {
        // 获取错题
        ArrayList<Question> wrongQuestions = new ArrayList<Question>();
        for (int i = 0; i < quesCount; i++) {
            if (answerStatus.get(i).equals("0")) {
                wrongQuestions.add(jsonQuestion.getQuestions().get(i));
            }
        }

        Intent intent = new Intent(ExamActivity.this, ExamResultActivity.class);
        intent.putExtra("questions", wrongQuestions);
        intent.putExtra("answers", wrongAnswers);
        startActivity(intent);
        finish();
    }

    public void onAgainClick(View view) {
        Intent intent = new Intent(ExamActivity.this, ExamActivity.class);
        intent.putExtra("url", dataUrl);
        intent.putExtra("module", module);
        intent.putExtra("subject", subject);
        startActivity(intent);
        finish();
    }

    public void onRankClick(View view) {
        Intent intent = new Intent(ExamActivity.this, ExamRankActivity.class);
        intent.putExtra("url", UrlUtil.getRankUrl(Integer.parseInt(subject)));
        startActivity(intent);
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
