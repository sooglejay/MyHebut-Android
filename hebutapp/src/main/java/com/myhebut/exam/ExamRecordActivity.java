package com.myhebut.exam;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.myhebut.application.MyApplication;
import com.myhebut.entity.JsonCommon;
import com.myhebut.entity.JsonRecord;
import com.myhebut.entity.Record;
import com.myhebut.utils.DensityUtil;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.UrlUtil;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class ExamRecordActivity extends SwipeBackActivity {

    private List<Record> records = new ArrayList<>();

    @ViewInject(R.id.tv_exam_best_score)
    private TextView mTvBestScore;

    @ViewInject(R.id.lv_exam_record_list)
    private SwipeMenuListView mLvRecords;

    @ViewInject(R.id.ll_exam_empty_record)
    private LinearLayout mLlEmptyRecord;

    @ViewInject(R.id.ll_exam_best)
    private LinearLayout mLlExamBest;

    @ViewInject(R.id.iv_no_record_user_avator)
    private ImageView mIvNoRecordAvator;

    @ViewInject(R.id.iv_have_record_user_avator)
    private ImageView mIvHaveRecordAvator;

    private String dataUrl;

    private HttpUtils http;

    private JsonRecord jsonRecord;

    private RecordAdapter recordAdapter;

    private String subject;

    private Gson gson;

    @OnClick(R.id.btn_exam_want_again)
    private void startExam(View view) {
        Intent intent = new Intent(ExamRecordActivity.this, ExamActivity.class);
        intent.putExtra("url", UrlUtil.getExamUrl("exam", Integer.parseInt(subject)));
        intent.putExtra("module", "exam");
        // 必须是String类型
        intent.putExtra("subject", subject + "");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_record);
        ViewUtils.inject(this);

        // 设置右滑返回
        SwipeBackLayout swipeBackLayout = this.getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        initData();
    }

    private void initData() {
        gson = new Gson();

        Intent intent = getIntent();
        dataUrl = intent.getStringExtra("url");
        subject = intent.getStringExtra("subject");
        // 获取考试记录
        http = HttpUtil.getHttp();
        http.send(HttpRequest.HttpMethod.GET, dataUrl, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try{
                    String jsonData = responseInfo.result;
                    // 有成绩记录才需要设置适配器
                    if (parseData(jsonData)) {
                        initAdapter();
                    }
                } catch(Exception e){
                    Toast.makeText(ExamRecordActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(ExamRecordActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });

        // 获取并设置头像
        setAvatar();
    }

    private void setAvatar() {
        MyApplication application = (MyApplication) getApplication();
        String url = UrlUtil.getAvatarUrl(application.getUser().getAvatar());
        Picasso.with(this).load(url).placeholder(R.mipmap.user_avatar_default).
                error(R.mipmap.user_avatar_default).into(mIvHaveRecordAvator);
        Picasso.with(this).load(url).placeholder(R.mipmap.user_avatar_default).
                error(R.mipmap.user_avatar_default).into(mIvNoRecordAvator);
    }

    private void initAdapter() {
        recordAdapter = new RecordAdapter();
        mLvRecords.setAdapter(recordAdapter);
        // set creator滑动菜单
        mLvRecords.setMenuCreator(creator);
        // 事件监听
        mLvRecords.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                // 删除考试记录
                deleteRecord(position);
                // 删除item更新视图
                records.remove(position);
                recordAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    private void deleteRecord(int position) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("recordId", records.get(position).getRecordId() + "");
        http.send(HttpRequest.HttpMethod.POST, UrlUtil.deleteUrl("record"), params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try{
                    String jsonData = responseInfo.result;
                    JsonCommon jsonCommon = gson.fromJson(jsonData, JsonCommon.class);
                } catch(Exception e){
                    Toast.makeText(ExamRecordActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(ExamRecordActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean parseData(String jsonData) {
        jsonRecord = gson.fromJson(jsonData, JsonRecord.class);
        records = jsonRecord.getRecords();

        if (records != null && records.size() != 0) {
            mTvBestScore.setText("您的最高历史成绩为" + jsonRecord.getBestScore() + "分");
            return true;
        } else {
            mLlEmptyRecord.setVisibility(View.VISIBLE);
            mLlExamBest.setVisibility(View.GONE);
            return false;
        }
    }


    public void back(View view) {
        finish();
    }

    class RecordAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return records.size();
        }

        @Override
        public Object getItem(int i) {
            return records.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            Record record = (Record) getItem(i);
            View view;
            ViewHolder viewHolder;

            if (convertView == null) {
                view = LayoutInflater.from(getApplicationContext()).
                        inflate(R.layout.item_exam_record_list, null);
                viewHolder = new ViewHolder();
                viewHolder.mTvScore = (TextView) view.findViewById(R.id.tv_exam_record_score);
                viewHolder.mTvTime = (TextView) view.findViewById(R.id.tv_exam_record_time);
                viewHolder.mTvDuration = (TextView) view.findViewById(R.id.tv_exam_record_duration);
                viewHolder.mTvPercent = (TextView) view.findViewById(R.id.tv_exam_record_percent);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.mTvScore.setText(record.getScore() + "");
            viewHolder.mTvTime.setText(record.getBegin_time());
            viewHolder.mTvDuration.setText(record.getLast_time());
            viewHolder.mTvPercent.setText(record.getPercent());

            return view;
        }

        class ViewHolder {
            TextView mTvScore;
            TextView mTvTime;
            TextView mTvDuration;
            TextView mTvPercent;
        }
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(255,
                    0, 0)));
            // set item width
            deleteItem.setWidth(DensityUtil.dip2px(getApplicationContext(), 65));
            // set item title
            deleteItem.setTitle("删除");
            // set item title fontsize
            deleteItem.setTitleSize(18);
            // set item title font color
            deleteItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };

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
