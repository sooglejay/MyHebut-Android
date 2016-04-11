package com.myhebut.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.myhebut.activity.WebViewActivity;
import com.myhebut.entity.JsonMusic;
import com.myhebut.entity.Music;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class HomeMusicListActivity extends SwipeBackActivity {

    @ViewInject(R.id.lv_home_music_list)
    private ListView mLvMusicList;

    private List<Music> musics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_music_list);
        ViewUtils.inject(this);

        // 设置右滑返回
        SwipeBackLayout swipeBackLayout = this.getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        initData();
        initEvent();
    }

    private void initEvent() {
        // 点击事件
        mLvMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Music music = musics.get(position);
                Intent intent = new Intent(HomeMusicListActivity.this, WebViewActivity.class);
                intent.putExtra("href", music.getUrl());
                intent.putExtra("title", "蜜思点歌台 - 歌单查询");
                startActivity(intent);
            }
        });
    }

    private void initData() {
        HttpUtils http = HttpUtil.getHttp();
        http.send(HttpRequest.HttpMethod.GET, UrlUtil.getMusicListUrl(), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    String jsonData = responseInfo.result;
                    parseData(jsonData);
                } catch (Exception e) {
                    Toast.makeText(HomeMusicListActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(HomeMusicListActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void parseData(String jsonData) {
        Gson gson = new Gson();
        JsonMusic jsonMusic = gson.fromJson(jsonData, JsonMusic.class);
        musics = jsonMusic.getMusics();
        // 设置listview适配器
        mLvMusicList.setAdapter(new MyAdapter(this, R.layout.item_home_music_view, musics));
    }

    private class MyAdapter extends ArrayAdapter<Music> {

        private int resourceId;

        public MyAdapter(Context context, int resource, List<Music> objects) {
            super(context, resource, objects);
            this.resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Music music = getItem(position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.time = (TextView) view.findViewById(R.id.tv_home_music_item);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.time.setText("    " + music.getTime() + "歌单");
            return view;
        }

        class ViewHolder {
            private TextView time;
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
