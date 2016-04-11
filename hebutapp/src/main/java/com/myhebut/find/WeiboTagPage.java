package com.myhebut.find;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.MainActivity;
import com.myhebut.activity.R;
import com.myhebut.entity.JsonWeibo;
import com.myhebut.entity.Weibo;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;
import com.myhebut.utils.UrlUtil;

import java.util.ArrayList;
import java.util.List;

public class WeiboTagPage extends BaseTagPage {

    // 是否是第一次访问页面
    private boolean isFirst = true;

    @ViewInject(R.id.listView)
    private PullToRefreshListView mPullRefreshListView;

    @ViewInject(R.id.tv_find_wb_search)
    private EditText mEtSearch;

    private HttpUtils http;

    private Gson gson;

    private List<Weibo> weibos = new ArrayList<>();

    private MyAdapter adapter;

    @OnClick(R.id.ibtn_find_wb_search)
    private void search(View view) {
        Intent intent = new Intent(mainActivity, FindWbContentActivity.class);
        String keyword = mEtSearch.getText().toString();
        String href = UrlUtil.getsearchWbUrl(weibos.get(0).getHref(), weibos.get(1).getHref(), weibos.get(2).getHref(), weibos.get(4).getHref(), weibos.get(5).getHref(), keyword);
        intent.putExtra("href", href);
        intent.putExtra("mode", "1");
        mainActivity.startActivity(intent);
    }

    public WeiboTagPage(MainActivity mainActivity) {
        super(mainActivity);
    }

    @Override
    public void initView() {
        root = View.inflate(mainActivity, R.layout.fragment_find_wb_list, null);
        ViewUtils.inject(this, root);
    }

    @Override
    public void initEvent() {
        // 点击事件
        mPullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Weibo weibo = weibos.get(position - 1);
                Intent intent = new Intent(mainActivity, FindWbContentActivity.class);
                intent.putExtra("href", weibo.getHref());
                intent.putExtra("time", weibo.getTime());
                intent.putExtra("mode", "0");
                mainActivity.startActivity(intent);
            }
        });
        // 上拉刷新
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String date = DateUtils.formatDateTime(mainActivity, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
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
            super.onPostExecute(result);
        }

    }

    @Override
    public void initData() {
        http = HttpUtil.getHttp();
        gson = new Gson();

        if (isFirst) {
            // 先读取本地数据
            String jsonData = SpUtil.getString(mainActivity, MyConstants.WBDATA, null);
            if (jsonData != null) {
                parseData(jsonData);
                initAdapter();
            }
            // 网络请求
            // 自动刷新
            updateDataByHttp();
            mPullRefreshListView.setRefreshing();
        } else {
            // 直接读取本地数据
            String jsonData = SpUtil.getString(mainActivity, MyConstants.WBDATA, null);
            parseData(jsonData);
            initAdapter();
        }
    }

    private void updateDataByHttp() {
        http.send(HttpRequest.HttpMethod.GET, UrlUtil.getWbListUrl(), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    String jsonData = responseInfo.result;
                    parseData(jsonData);
                    initAdapter();
                    // 数据存储
                    SpUtil.setString(mainActivity, MyConstants.WBDATA, jsonData);
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

    private void initAdapter() {
        adapter = new MyAdapter(mainActivity, R.layout.item_find_list, weibos);
        mPullRefreshListView.setAdapter(adapter);
    }

    private void parseData(String jsonData) {
        JsonWeibo jsonWeibo = gson.fromJson(jsonData, JsonWeibo.class);
        weibos = jsonWeibo.getWeibos();
    }


    private class MyAdapter extends ArrayAdapter<Weibo> {

        private int resourceId;

        public MyAdapter(Context context, int resource, List<Weibo> objects) {
            super(context, resource, objects);
            this.resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Weibo weibo = getItem(position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) view.findViewById(R.id.tv_find_item_title);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.title.setText(weibo.getTime() + " 失物招领信息总结");
            return view;
        }

        class ViewHolder {
            private TextView title;
        }
    }
}
