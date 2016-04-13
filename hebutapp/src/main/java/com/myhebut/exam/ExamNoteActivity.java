package com.myhebut.exam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.umeng.analytics.MobclickAgent;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class ExamNoteActivity extends SwipeBackActivity {

    private String module;

    private String url;

    private Intent intent;

    @OnClick(R.id.tv_exam_note_manual)
    private void manual(View view){
        intent.putExtra("noteMode","manual");
        startActivity(intent);
    }

    @OnClick(R.id.tv_exam_note_auto)
    private void auto(View view){
        intent.putExtra("noteMode","auto");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_note_mode);
        ViewUtils.inject(this);
        // 设置右滑返回
        SwipeBackLayout swipeBackLayout = this.getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);


        intent = getIntent();
        url = intent.getStringExtra("url");
        intent = new Intent(ExamNoteActivity.this, ExamActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("module", "note");
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
