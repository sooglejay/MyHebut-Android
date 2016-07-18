package com.myhebut.home;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myhebut.activity.R;
import com.myhebut.activity.WebViewActivity;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class HomeMapActivity extends SwipeBackActivity {

    @ViewInject(R.id.lv_home_map)
    private ListView mLvHomeMap;
    // 地图位置名称
    private String[] locations = {"北辰校区(720°全景推荐)", "红桥校区(720°全景推荐)", "北辰校区", "红桥校区(东院)",
            "红桥校区(南院)","红桥校区(北院)","廊坊校区"};
    // 名称前面的颜色图标
    private int[] points = {R.drawable.point_purple, R.drawable.point_blue, R.drawable.point_red,
            R.drawable.point_green, R.drawable.point_purple, R.drawable.point_orange, R.drawable.point_red};
    // 地图链接
    private String[] urls = {UrlUtil.getMapUrlOfbcNew(), UrlUtil.getMapUrlOfhqNew(), UrlUtil.getMapUrlOfbc(),
            UrlUtil.getMapUrlOfdy(), UrlUtil.getMapUrlOfny(), UrlUtil.getMapUrlOfby(), UrlUtil.getMapUrlOflf()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map);
        ViewUtils.inject(this);

        // 设置右滑返回
        SwipeBackLayout swipeBackLayout = this.getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        // 设置列表
        mLvHomeMap.setAdapter(new MapAdapter());
        // 设置列表监听事件
        mLvHomeMap.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomeMapActivity.this, WebViewActivity.class);
                intent.putExtra("href", urls[position]);
                intent.putExtra("title", locations[position]);
                startActivity(intent);
            }
        });
    }

    class MapAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return locations.length;
        }

        @Override
        public Object getItem(int i) {
            return locations[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view;
            ViewHolder viewHolder;

            if (convertView == null) {
                view = LayoutInflater.from(getApplicationContext()).
                        inflate(R.layout.item_home_map_list, null);
                viewHolder = new ViewHolder();
                viewHolder.mTvMap = (TextView) view.findViewById(R.id.tv_home_map_location);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.mTvMap.setText(locations[i]);
            Drawable drawable= getResources()
                    .getDrawable(points[i]);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            viewHolder.mTvMap.setCompoundDrawables(drawable, null, null, null);
            return view;
        }

        class ViewHolder {
            TextView mTvMap;
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
