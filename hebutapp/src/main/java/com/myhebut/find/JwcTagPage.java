package com.myhebut.find;

import android.content.Context;
import android.content.Intent;
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
import com.myhebut.entity.JsonOffice;
import com.myhebut.entity.Office;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;
import com.myhebut.utils.UrlUtil;

import java.util.ArrayList;
import java.util.List;

public class JwcTagPage extends BaseTagPage {

    // 是否是第一次访问页面
    private boolean isFirst = true;

    @ViewInject(R.id.listView)
    private PullToRefreshListView mPullRefreshListView;

    private HttpUtils http;

    private Gson gson;

    private List<Office> offices = new ArrayList<>();

    private MyAdapter adapter;


    public JwcTagPage(MainActivity mainActivity) {
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
                // TODO 第一个item的position值为1??
                Office office = offices.get(position - 1);
                Intent intent = new Intent(mainActivity, FindJwcContentActivity.class);
                Log.d("find", "position:" + position + ",newsId:" + office.getNewsId());
                intent.putExtra("newsId", office.getNewsId());
                mainActivity.startActivity(intent);
            }
        });
        // 上拉刷新,下拉加载
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
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {

            updateDataByHttp();
            mPullRefreshListView.onRefreshComplete();//数据加载到适配器完成后，刷新完成，
            super.onPostExecute(result);
        }

    }

    @Override
    public void initData() {
        http = HttpUtil.getHttp();
        gson = new Gson();
        // 第一次请求不一定有本地数据,所有还要网络请求;如果网络请求成功后,则一定有本地数据
        if (isFirst) {
            // 先读取本地数据
            String jsonData = SpUtil.getString(mainActivity, MyConstants.JWCDATA, null);
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
            String jsonData = SpUtil.getString(mainActivity, MyConstants.JWCDATA, null);
            parseData(jsonData);
            initAdapter();
        }

    }

    private void updateDataByHttp() {
        http.send(HttpRequest.HttpMethod.GET, UrlUtil.getJwcListUrl(), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try{
                    String jsonData = responseInfo.result;
                    parseData(jsonData);
                    initAdapter();
                    // 数据存储
                    SpUtil.setString(mainActivity, MyConstants.JWCDATA, jsonData);

                    //数据加载到适配器完成后，刷新完成
                    mPullRefreshListView.onRefreshComplete();
                    // 只有成功获取数据后才能认为第一次获取成功
                    isFirst = false;
                } catch(Exception e){
                    Toast.makeText(mainActivity, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(mainActivity, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                mPullRefreshListView.onRefreshComplete();
            }
        });
    }

    private void initAdapter() {
        adapter = new MyAdapter(mainActivity, R.layout.item_find_list, offices);
        mPullRefreshListView.setAdapter(adapter);
    }

    private void parseData(String jsonData) {
        JsonOffice jsonOffice = gson.fromJson(jsonData, JsonOffice.class);
        offices = jsonOffice.getOffices();
    }


    private class MyAdapter extends ArrayAdapter<Office> {

        private int resourceId;

        public MyAdapter(Context context, int resource, List<Office> objects) {
            super(context, resource, objects);
            this.resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Office office = getItem(position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) view.findViewById(R.id.tv_find_item_title);
                viewHolder.time = (TextView) view.findViewById(R.id.tv_find_item_time);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.title.setText(office.getTitle());
            viewHolder.time.setText(office.getTime());
            return view;
        }

        class ViewHolder {
            private TextView title;
            private TextView time;
        }
    }
}
