package com.myhebut.exam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.myhebut.utils.HebutUtil;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.Map;

public class ExamSectionActivity extends FragmentActivity {

    @ViewInject(R.id.tv_main_module_title)
    private TextView mTvTitle;

    @ViewInject(R.id.tv_exam_section_0)
    private TextView mTvSection_0;
    @ViewInject(R.id.tv_exam_section_1)
    private TextView mTvSection_1;
    @ViewInject(R.id.tv_exam_section_2)
    private TextView mTvSection_2;
    @ViewInject(R.id.tv_exam_section_3)
    private TextView mTvSection_3;
    @ViewInject(R.id.tv_exam_section_4)
    private TextView mTvSection_4;
    @ViewInject(R.id.tv_exam_section_5)
    private TextView mTvSection_5;
    @ViewInject(R.id.tv_exam_section_6)
    private TextView mTvSection_6;
    @ViewInject(R.id.tv_exam_section_7)
    private TextView mTvSection_7;

    private String module;

    private String subject;

    private String url;

    private Intent intent;

    @OnClick(R.id.tv_exam_section)
    public void getAll(View view) {
        url = UrlUtil.getUrlWithSection(module, subject, 99);
        intent.putExtra("url", url);
        intent.putExtra("module", module);
        startActivity(intent);
    }

    @OnClick(R.id.tv_exam_section_0)
    public void get8th(View view) {
        url = UrlUtil.getUrlWithSection(module, subject, 0);
        intent.putExtra("url", url);
        intent.putExtra("module", module);
        intent.putExtra("section", mTvSection_0.getText());
        startActivity(intent);
    }

    @OnClick(R.id.tv_exam_section_1)
    public void get1st(View view) {
        url = UrlUtil.getUrlWithSection(module, subject, 1);
        intent.putExtra("url", url);
        intent.putExtra("module", module);
        intent.putExtra("section", mTvSection_1.getText());
        startActivity(intent);
    }

    @OnClick(R.id.tv_exam_section_2)
    public void get2nd(View view) {
        url = UrlUtil.getUrlWithSection(module, subject, 2);
        intent.putExtra("url", url);
        intent.putExtra("module", module);
        intent.putExtra("section", mTvSection_2.getText());
        startActivity(intent);
    }

    @OnClick(R.id.tv_exam_section_3)
    public void get3rd(View view) {
        url = UrlUtil.getUrlWithSection(module, subject, 3);
        intent.putExtra("url", url);
        intent.putExtra("module", module);
        intent.putExtra("section", mTvSection_3.getText());
        startActivity(intent);
    }

    @OnClick(R.id.tv_exam_section_4)
    public void get4th(View view) {
        url = UrlUtil.getUrlWithSection(module, subject, 4);
        intent.putExtra("url", url);
        intent.putExtra("module", module);
        intent.putExtra("section", mTvSection_4.getText());
        startActivity(intent);
    }

    @OnClick(R.id.tv_exam_section_5)
    public void get5th(View view) {
        url = UrlUtil.getUrlWithSection(module, subject, 5);
        intent.putExtra("url", url);
        intent.putExtra("module", module);
        intent.putExtra("section", mTvSection_5.getText());
        startActivity(intent);
    }

    @OnClick(R.id.tv_exam_section_6)
    public void get6th(View view) {
        url = UrlUtil.getUrlWithSection(module, subject, 6);
        intent.putExtra("url", url);
        intent.putExtra("module", module);
        intent.putExtra("section", mTvSection_6.getText());
        startActivity(intent);
    }

    @OnClick(R.id.tv_exam_section_7)
    public void get7th(View view) {
        url = UrlUtil.getUrlWithSection(module, subject, 7);
        intent.putExtra("url", url);
        intent.putExtra("module", module);
        intent.putExtra("section", mTvSection_7.getText());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_exam_section);
        ViewUtils.inject(this);

        intent = getIntent();
        subject = intent.getStringExtra("subject");
        module = intent.getStringExtra("module");
        intent = new Intent(ExamSectionActivity.this, ExamActivity.class);

        initView();
    }

    private void initView() {
        // 设置标题
        if (module.equals("random")) {
            mTvTitle.setText("随机练习");
        } else {
            mTvTitle.setText("顺序练习");
        }
        // 如果是毛概上,共6章,需要隐藏最后1个TextView和第一个TextView
        if (subject.equals("1")) {
            mTvSection_0.setVisibility(View.GONE);
            mTvSection_7.setVisibility(View.GONE);
        }

        Map<String, String> sectionMap = HebutUtil.initSections(subject);

        for (int i = 0; i < sectionMap.size() + 1; i++) {
            switch (i) {
                case 0:
                    mTvSection_0.setText(sectionMap.get(i + ""));
                    break;
                case 1:
                    mTvSection_1.setText(sectionMap.get(i + ""));
                    break;
                case 2:
                    mTvSection_2.setText(sectionMap.get(i + ""));
                    break;
                case 3:
                    mTvSection_3.setText(sectionMap.get(i + ""));
                    break;
                case 4:
                    mTvSection_4.setText(sectionMap.get(i + ""));
                    break;
                case 5:
                    mTvSection_5.setText(sectionMap.get(i + ""));
                    break;
                case 6:
                    mTvSection_6.setText(sectionMap.get(i + ""));
                    break;
                case 7:
                    mTvSection_7.setText(sectionMap.get(i + ""));
                    break;
                default:
                    break;
            }
        }
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
