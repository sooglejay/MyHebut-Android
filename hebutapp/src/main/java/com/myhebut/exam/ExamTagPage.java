package com.myhebut.exam;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.MainActivity;
import com.myhebut.activity.R;
import com.myhebut.application.MyApplication;
import com.myhebut.entity.User;
import com.myhebut.utils.UrlUtil;

public class ExamTagPage {

    private int subject;

    protected View root;

    protected MainActivity mainActivity;

    private int userId;

    @ViewInject(R.id.tv_exam_subject_title_icon)
    private TextView mTvTitleIcon;

    @ViewInject(R.id.tv_exam_subject_title)
    private TextView mTvTitle;

    @OnClick(R.id.rl_exam)
    public void startExam(View view) {
        Intent intent = new Intent(mainActivity, ExamActivity.class);
        intent.putExtra("url", UrlUtil.getExamUrl("exam", subject));
        intent.putExtra("module", "exam");
        // 必须是String类型
        intent.putExtra("subject", subject + "");
        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.rl_note)
    public void startNote(View view) {
        Intent intent = new Intent(mainActivity, ExamActivity.class);
        intent.putExtra("url", UrlUtil.getUrlWithUserId("note", subject, userId));
        intent.putExtra("module", "note");
        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.rl_collect)
    public void startCollect(View view) {
        Intent intent = new Intent(mainActivity, ExamActivity.class);
        intent.putExtra("url", UrlUtil.getUrlWithUserId("collect", subject, userId));
        intent.putExtra("module", "collect");
        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.rl_order)
    public void startOrder(View view) {
        Intent intent = new Intent(mainActivity, ExamSectionActivity.class);
        intent.putExtra("subject", subject + "");
        intent.putExtra("module", "order");
        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.rl_random)
    public void startRandom(View view) {
        Intent intent = new Intent(mainActivity, ExamSectionActivity.class);
        intent.putExtra("subject", subject + "");
        intent.putExtra("module", "random");
        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.rl_search)
    public void startSearch(View view) {
        Intent intent = new Intent(mainActivity, ExamSearchActivity.class);
        intent.putExtra("subject", subject + "");
        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.rl_record)
    public void startRecord(View view) {
        Intent intent = new Intent(mainActivity, ExamRecordActivity.class);
        intent.putExtra("url", UrlUtil.getUrlWithUserId("record", subject, userId));
        intent.putExtra("subject", subject + "");

        mainActivity.startActivity(intent);
    }

    @OnClick(R.id.rl_rank)
    public void startRank(View view) {
        Intent intent = new Intent(mainActivity, ExamRankActivity.class);
        intent.putExtra("url", UrlUtil.getRankUrl(subject));
        mainActivity.startActivity(intent);
    }

    public ExamTagPage(MainActivity mainActivity, int subject) {
        this.mainActivity = mainActivity;
        this.subject = subject;
        // 获取用户Id
        MyApplication application = (MyApplication) mainActivity.getApplication();
        User user = application.getUser();
        userId = user.getUserId();

        initView();
        initData();
        initEvent();
    }

    public void initView() {
        root = View.inflate(mainActivity, R.layout.fragment_exam_main, null);
        ViewUtils.inject(this, root);
    }

    public void initEvent() {

    }

    public void initData() {
        Typeface iconfont = Typeface.createFromAsset(mainActivity.getAssets(), "home.ttf");
        mTvTitleIcon.setTypeface(iconfont);

        if (subject == 0) {
            mTvTitle.setText("Hebut · 马原");
        } else if (subject == 1) {
            mTvTitle.setText("Hebut · 毛概(上)");
        } else if (subject == 2) {
            mTvTitle.setText("Hebut · 毛概(下)");
        }

    }

    public View getRoot() {
        return root;
    }
}
