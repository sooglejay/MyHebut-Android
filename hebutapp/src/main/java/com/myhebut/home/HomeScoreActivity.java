package com.myhebut.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.myhebut.entity.Course;
import com.myhebut.entity.JsonCourse;
import com.myhebut.utils.HebutUtil;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class HomeScoreActivity extends SwipeBackActivity {

    @ViewInject(R.id.sv_home_score)
    private ScrollView mSvScore;


    @ViewInject(R.id.lv_home_course_all)
    private ListView mLvCourseAll;


    @ViewInject(R.id.tv_home_score_refresh)
    private TextView mTvRefresh;

    @OnClick(R.id.tv_home_course_refresh)
    private void refresh(View view){
        Intent intent = new Intent(HomeScoreActivity.this, HomeJwcLoginActivity.class);
        intent.putExtra("module","score");
        startActivity(intent);
        finish();
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
        String jsonData = SpUtil.getString(this, MyConstants.COURSEDATA, null);
        JsonCourse jsonCourse = gson.fromJson(jsonData, JsonCourse.class);
        List<Course> courses = jsonCourse.getCourses();
        // 设置listview适配器
        mLvCourseAll.setAdapter(new MyAdapter(this, R.layout.item_home_course_view, courses));
        List<Course> todayCourses = HebutUtil.selectToday(courses);

    }

    private class MyAdapter extends ArrayAdapter<Course> {

        private int resourceId;

        public MyAdapter(Context context, int resource, List<Course> objects) {
            super(context, resource, objects);
            this.resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Course course = getItem(position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.time = (TextView) view.findViewById(R.id.tv_home_course_time);
                viewHolder.name = (TextView) view.findViewById(R.id.tv_home_course_name);
                viewHolder.credit = (TextView) view.findViewById(R.id.tv_home_course_credit);
                viewHolder.week = (TextView) view.findViewById(R.id.tv_home_course_week);
                viewHolder.type = (TextView) view.findViewById(R.id.tv_home_course_type);
                viewHolder.teacher = (TextView) view.findViewById(R.id.tv_home_course_teacher);
                viewHolder.room = (TextView) view.findViewById(R.id.tv_home_course_room);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            if (course.getDay() == 0){
                viewHolder.time.setText("时间暂未确定");
            } else {
                // 周一 1-2节
                viewHolder.time.setText("周" + HebutUtil.parseDay(course.getDay()) + " " + course.getJieci() + "-" + (course.getJieci() + course.getJieshu() - 1) + "节");
            }
            viewHolder.name.setText(course.getCourseName());
            viewHolder.credit.setText("(" + course.getCredit() + "学分)");
            viewHolder.week.setText(course.getWeek());
            viewHolder.type.setText(course.getType() + "课");
            viewHolder.teacher.setText("教师:" + course.getTeacher());
            viewHolder.room.setText(course.getLocation() + course.getClassroom());
            return view;
        }

        class ViewHolder {
            private TextView time;
            private TextView name;
            private TextView credit;
            private TextView week;
            private TextView type;
            private TextView teacher;
            private TextView room;
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
