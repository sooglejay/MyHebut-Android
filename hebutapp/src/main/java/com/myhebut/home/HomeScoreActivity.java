package com.myhebut.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.myhebut.activity.WebViewActivity;
import com.myhebut.classes.StickyListViewForScrollView;
import com.myhebut.entity.JsonScore;
import com.myhebut.entity.Score;
import com.myhebut.entity.Term;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class HomeScoreActivity extends SwipeBackActivity {

    @ViewInject(R.id.lv_home_score)
    private StickyListViewForScrollView mLvScore;

    @ViewInject(R.id.tv_home_score_refresh)
    private TextView mTvRefresh;

    @ViewInject(R.id.sv_home_score)
    private ScrollView mSvScore;

    @ViewInject(R.id.tv_home_score_gpa)
    private TextView mTvGPA;

    @OnClick(R.id.tv_home_score_refresh)
    private void refresh(View view) {
        Intent intent = new Intent(HomeScoreActivity.this, HomeJwcLoginActivity.class);
        intent.putExtra("module", "score");
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.tv_home_score_help)
    private void help(View view) {
        Intent intent = new Intent(HomeScoreActivity.this, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getScoreHelpUrl());
        intent.putExtra("title", "绩点,加权平均分计算规则");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_jwc_score);
        ViewUtils.inject(this);

        // 设置右滑返回
        SwipeBackLayout swipeBackLayout = this.getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        // 设置回弹
        OverScrollDecoratorHelper.setUpOverScroll(mSvScore);
        // scrollview滚动到最顶端
        mSvScore.smoothScrollTo(0, 0);
        // 设置iconfont
        Typeface iconfont = Typeface.createFromAsset(this.getAssets(), "refresh.ttf");
        mTvRefresh.setTypeface(iconfont);

        initData();
    }

    private void initData() {
        Gson gson = new Gson();
        // 读取保存的课程数据并转换
        String jsonData = SpUtil.getString(this, MyConstants.SCOREDATA, null);
        JsonScore jsonScore = gson.fromJson(jsonData, JsonScore.class);

        mTvGPA.setText("绩点:" + jsonScore.getGpa() + "(仅供参考)");
        List<Term> terms = jsonScore.getTerms();
        // 提取出terms中的所有成绩并设置每个成绩对应的学期
        List<Score> scores = new ArrayList<>();
        Map<Integer, String> timeMap = new HashMap<>();
        Map<Integer, Double> avgMap = new HashMap<>();
        // 倒序添加,显示最新的成绩
        for (int i = terms.size() - 1; i >= 0 ; i--){
            timeMap.put(i, terms.get(i).getTime());
            avgMap.put(i, terms.get(i).getAvg());
            List<Score> tempScores = terms.get(i).getScores();
            for (int j = 0; j < tempScores.size(); j++){
                tempScores.get(j).setTerm(i);
            }
            scores.addAll(tempScores);
        }
        // 设置listview适配器
        mLvScore.setAdapter(new MyAdapter(this, scores, timeMap, avgMap));
    }

    public class MyAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private List<Score> scores;

        private Map<Integer, String> timeMap;

        private Map<Integer, Double> avgMap;

        private LayoutInflater inflater;

        public MyAdapter(Context context, List<Score> scores, Map<Integer, String> timeMap, Map<Integer, Double> avgMap) {
            inflater = LayoutInflater.from(context);
            this.scores = scores;
            this.timeMap = timeMap;
            this.avgMap = avgMap;
        }

        @Override
        public int getCount() {
            return scores.size();
        }

        @Override
        public Object getItem(int position) {
            return scores.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_home_score_view, parent, false);
                holder.mTvCourse = (TextView) convertView.findViewById(R.id.tv_home_score_course);
                holder.mTvCredit = (TextView) convertView.findViewById(R.id.tv_home_score_credit);
                holder.mTvScore = (TextView) convertView.findViewById(R.id.tv_home_score);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.mTvCourse.setText(scores.get(position).getCourse());
            String credit = scores.get(position).getCredit();
            Log.d("credit", credit);
            if (credit.equals(" ")){
                holder.mTvCredit.setText("");
            } else {
                holder.mTvCredit.setText(credit + "学分");
            }
            holder.mTvScore.setText(scores.get(position).getScore());

            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = inflater.inflate(R.layout.item_home_score_header, parent, false);
                holder.mTvHeader = (TextView) convertView.findViewById(R.id.tv_home_score_header);
                holder.mTvAvg = (TextView) convertView.findViewById(R.id.tv_home_score_avg);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }
            //set header mTvHeader as first char in name
            String headerText = timeMap.get(scores.get(position).getTerm());
            Double avg = avgMap.get(scores.get(position).getTerm());
            holder.mTvHeader.setText(headerText);
            if (avg != 0){
                holder.mTvAvg.setText("加权平均:" + avg);
            } else {
                holder.mTvAvg.setText("");
            }
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            //return the first character of the country as ID because this is what headers are based upon
            return scores.get(position).getTerm();
        }

        class HeaderViewHolder {
            TextView mTvHeader;
            TextView mTvAvg;
        }

        class ViewHolder {
            TextView mTvCourse;
            TextView mTvCredit;
            TextView mTvScore;
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
