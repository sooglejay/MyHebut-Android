package com.myhebut.exam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class ExamSearchActivity extends SwipeBackActivity {

    private String subject;

    @ViewInject(R.id.et_exam_keyword)
    private EditText mEtKeyWord;

    @OnClick(R.id.btn_exam_keyword_submit)
    private void keyWordSubmit(View view){
        Intent intent = new Intent(ExamSearchActivity.this, ExamActivity.class);
        String url = UrlUtil.getUrlWithKeyword(subject, String.valueOf(mEtKeyWord.getText()));
        intent.putExtra("url", url);
        startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_search);
        ViewUtils.inject(this);
        // 设置右滑返回
        SwipeBackLayout swipeBackLayout = this.getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        initData();

    }

    private void initData() {
        Intent intent = getIntent();
        subject = intent.getStringExtra("subject");
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
