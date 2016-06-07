package com.myhebut.exam;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myhebut.activity.R;
import com.myhebut.entity.JsonRecord;
import com.myhebut.entity.Record;
import com.myhebut.utils.HebutUtil;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.UrlUtil;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class ExamRankActivity extends SwipeBackActivity {

    private List<Record> records = new ArrayList<>();

    private String dataUrl;

    private HttpUtils http;

    private JsonRecord jsonRecord;

    private RankAdapter rankAdapter;

    @ViewInject(R.id.lv_exam_record_list)
    private ListView mLvRecords;

    @ViewInject(R.id.tv_exam_rank_time)
    private TextView mTvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_rank);
        ViewUtils.inject(this);

        // 设置右滑返回
        SwipeBackLayout swipeBackLayout = this.getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        initData();
    }

    private void initData() {
        mTvTime.setText("时间截至到" + HebutUtil.getDate());

        Intent intent = getIntent();
        dataUrl = intent.getStringExtra("url");

        http = HttpUtil.getHttp();
        http.send(HttpRequest.HttpMethod.GET, dataUrl, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try{
                    String jsonData = responseInfo.result;
                    parseData(jsonData);
                    initAdapter();
                } catch(Exception e){
                    Toast.makeText(ExamRankActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(ExamRankActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initAdapter() {
        rankAdapter = new RankAdapter();
        mLvRecords.setAdapter(rankAdapter);
    }


    private void parseData(String jsonData) {
        Gson gson = new Gson();
        jsonRecord = gson.fromJson(jsonData, JsonRecord.class);
        records = jsonRecord.getRecords();
    }

    class RankAdapter extends BaseAdapter {

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
                        inflate(R.layout.item_exam_rank_list, null);
                viewHolder = new ViewHolder();
                viewHolder.mIvAvatar = (ImageView) view.findViewById(R.id.iv_exam_rank_avatar);
                viewHolder.mTvNickName = (TextView) view.findViewById(R.id.tv_exam_rank_nickName);
                viewHolder.mTvScore = (TextView) view.findViewById(R.id.tv_exam_rank_score);
                viewHolder.mTvDuration = (TextView) view.findViewById(R.id.tv_exam_rank_duration);
                viewHolder.mTvNum = (TextView) view.findViewById(R.id.tv_exam_rank_num);
                viewHolder.mLlRankText = (LinearLayout) view.findViewById(R.id.ll_exam_rank_text);
                viewHolder.mIvRankPic = (ImageView) view.findViewById(R.id.iv_exam_rank_pic);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.mTvNickName.setText(record.getUser().getNickName());
            viewHolder.mTvScore.setText(record.getScore() + "分");
            viewHolder.mTvDuration.setText(record.getLast_time());
            // 前三名显示奖牌
            if (i == 0) {
                viewHolder.mIvRankPic.setVisibility(View.VISIBLE);
                viewHolder.mIvRankPic.setImageResource(R.mipmap.ic_exam_rank_1);
                viewHolder.mLlRankText.setVisibility(View.GONE);
            } else if (i == 1) {
                viewHolder.mIvRankPic.setVisibility(View.VISIBLE);
                viewHolder.mIvRankPic.setImageResource(R.mipmap.ic_exam_rank_2);
                viewHolder.mLlRankText.setVisibility(View.GONE);
            } else if (i == 2) {
                viewHolder.mIvRankPic.setVisibility(View.VISIBLE);
                viewHolder.mIvRankPic.setImageResource(R.mipmap.ic_exam_rank_3);
                viewHolder.mLlRankText.setVisibility(View.GONE);
            } else {
                viewHolder.mIvRankPic.setVisibility(View.GONE);
                viewHolder.mLlRankText.setVisibility(View.VISIBLE);
                viewHolder.mTvNum.setText((i + 1) + "");
            }

            // 获取并设置头像
            String url = UrlUtil.getAvatarUrl(record.getUser().getAvatar());
            Picasso.with(ExamRankActivity.this).load(url).placeholder(R.mipmap.user_avatar_default).
                    error(R.mipmap.user_avatar_default).into(viewHolder.mIvAvatar);

            return view;
        }

        class ViewHolder {
            ImageView mIvAvatar;
            TextView mTvNickName;
            TextView mTvScore;
            TextView mTvDuration;
            TextView mTvNum;
            LinearLayout mLlRankText;
            ImageView mIvRankPic;
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