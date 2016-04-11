package com.myhebut.find;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myhebut.activity.MainActivity;
import com.myhebut.activity.R;
import com.myhebut.activity.WebViewActivity;
import com.myhebut.entity.JsonTieba;
import com.myhebut.entity.Tieba;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;
import com.myhebut.utils.UrlUtil;

import java.util.ArrayList;
import java.util.List;

public class TiebaTagPage extends BaseTagPage {

    // 是否是第一次访问页面
    private boolean isFirst = true;

    @ViewInject(R.id.listView)
    private PullToRefreshListView mPullRefreshListView;

    private HttpUtils http;

    private Gson gson;

    private List<Tieba> tiebas = new ArrayList<>();

    private MyAdapter adapter;

    private int pn = 1;

    public TiebaTagPage(MainActivity mainActivity) {
        super(mainActivity);
    }

    @Override
    public void initView() {
        root = View.inflate(mainActivity, R.layout.fragment_find_list, null);
        ViewUtils.inject(this, root);
    }

    @Override
    public void initEvent() {
        // 点击事件
        mPullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Tieba tieba = tiebas.get(position - 1);
                Intent intent = new Intent(mainActivity, WebViewActivity.class);
                intent.putExtra("href", UrlUtil.getTbItemUrl(tieba.getHref()));
                intent.putExtra("title", "帖子详情");
                mainActivity.startActivity(intent);
            }
        });
        // 上拉刷新,下拉加载
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String date = DateUtils.formatDateTime(mainActivity, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                //判断上拉 还是下拉
                if (PullToRefreshBase.Mode.PULL_FROM_START == mPullRefreshListView.getCurrentMode()) {
                    //设置刷新标签
                    refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
                    //设置下拉标签
                    refreshView.getLoadingLayoutProxy().setPullLabel("下拉刷新");
                    //设置释放标签
                    refreshView.getLoadingLayoutProxy().setReleaseLabel("释放开始刷新");
                    //设置上一次刷新的提示标签
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新时间:" + date);
                    //加载数据操作
                    new RefreshTask().execute();
                } else if (PullToRefreshBase.Mode.PULL_FROM_END == mPullRefreshListView.getCurrentMode()) {
                    refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
                    refreshView.getLoadingLayoutProxy().setPullLabel("下拉刷新");
                    refreshView.getLoadingLayoutProxy().setReleaseLabel("释放开始加载");
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("最后加载时间:" + date);
                    new LoadTask().execute();
                }
            }
        });
    }

    private class RefreshTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            updateDataByHttp();
            Log.d("tieba", "http:update");
            super.onPostExecute(result);
        }

    }

    private class LoadTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            loadDataByHttp();
            super.onPostExecute(result);
        }

    }

    @Override
    public void initData() {
        http = HttpUtil.getHttp();
        gson = new Gson();

        if (isFirst) {
            // 先读取本地数据
            String jsonData = SpUtil.getString(mainActivity, MyConstants.TBDATA, null);
            if (jsonData != null) {
                parseData(jsonData);
                // TODO 因为listview指向堆内存,直接对数据进行new之后刷新无效,必须重新设置适配器,建立新的绑定
                initAdapter();
            }
            // 网络请求
            // 自动刷新
            mPullRefreshListView.setRefreshing();
            // TODO 上一行的刷新没有执行网络请求？
            updateDataByHttp();
        } else {
            // 直接读取本地数据
            String jsonData = SpUtil.getString(mainActivity, MyConstants.TBDATA, null);
            Log.d("tieba", "local:" + jsonData);
            parseData(jsonData);
            initAdapter();
        }
    }

    private void updateDataByHttp() {
        http.send(HttpRequest.HttpMethod.GET, UrlUtil.getTbListUrl(0), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    String jsonData = responseInfo.result;
                    parseData(jsonData);
                    initAdapter();
                    // 数据存储
                    SpUtil.setString(mainActivity, MyConstants.TBDATA, jsonData);
                    // 设置页码
                    pn = 1;
                    //数据加载到适配器完成后，刷新完成
                    mPullRefreshListView.onRefreshComplete();
                    // 只有成功获取数据后才能认为第一次获取成功
                    isFirst = false;
                } catch (Exception e) {
                    mPullRefreshListView.onRefreshComplete();
                    Toast.makeText(mainActivity, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                mPullRefreshListView.onRefreshComplete();
                Toast.makeText(mainActivity, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataByHttp() {
        http.send(HttpRequest.HttpMethod.GET, UrlUtil.getTbListUrl(pn++), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    String jsonData = responseInfo.result;

                    JsonTieba jsonTieba = gson.fromJson(jsonData, JsonTieba.class);
                    tiebas.addAll(jsonTieba.getTiebas());

                    adapter.notifyDataSetChanged();
                    //数据加载到适配器完成后，刷新完成，
                    mPullRefreshListView.onRefreshComplete();
                } catch (Exception e) {
                    mPullRefreshListView.onRefreshComplete();
                    Toast.makeText(mainActivity, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(HttpException error, String msg) {
                mPullRefreshListView.onRefreshComplete();
                Toast.makeText(mainActivity, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initAdapter() {
        adapter = new MyAdapter(mainActivity, R.layout.item_find_list_tieba, tiebas);
        mPullRefreshListView.setAdapter(adapter);
    }

    private void parseData(String jsonData) {
        JsonTieba jsonTieba = gson.fromJson(jsonData, JsonTieba.class);
        tiebas = jsonTieba.getTiebas();
    }


    private class MyAdapter extends ArrayAdapter<Tieba> {

        private int resourceId;

        public MyAdapter(Context context, int resource, List<Tieba> objects) {
            super(context, resource, objects);
            this.resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Tieba tieba = getItem(position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) view.findViewById(R.id.tv_find_item_title);
                viewHolder.time = (TextView) view.findViewById(R.id.tv_find_item_time);
                viewHolder.icon = (TextView) view.findViewById(R.id.tv_find_item_icon);
                viewHolder.reply = (TextView) view.findViewById(R.id.tv_find_item_reply);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            Typeface iconfont = Typeface.createFromAsset(mainActivity.getAssets(), "reply.ttf");
            viewHolder.icon.setTypeface(iconfont);
            viewHolder.title.setText(tieba.getTitle());
            viewHolder.time.setText(tieba.getTime());
            viewHolder.reply.setText(tieba.getReply());
            return view;
        }

        class ViewHolder {
            private TextView title;
            private TextView time;
            private TextView icon;
            private TextView reply;
        }
    }


    public View getRoot() {
        return root;
    }
}
