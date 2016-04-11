package com.myhebut.tab;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myhebut.activity.MainActivity;
import com.myhebut.activity.R;
import com.myhebut.find.BaseTagPage;
import com.myhebut.find.JwcTagPage;
import com.myhebut.find.TiebaTagPage;
import com.myhebut.find.WeiboTagPage;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

public class FindFragment extends BaseFragment {

    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;

    @ViewInject(R.id.viewpagertab)
    private SmartTabLayout viewPagerTab;

    private String[] modules = {"教务处通知", "失物招领", "贴吧交流"};

    private List<BaseTagPage> pages = new ArrayList<BaseTagPage>();

    private MyAdapter adapter;

    @Override
    public View initView() {
        View root = View.inflate(mainActivity, R.layout.fragment_find, null);
        ViewUtils.inject(this, root);
        return root;
    }

    @Override
    public void initData() {

        if (pages.size() == 0){
            // 教务处通知
            pages.add(new JwcTagPage((MainActivity) getActivity()));
            // 微博失物招领
            pages.add(new WeiboTagPage((MainActivity) getActivity()));
            // 贴吧信息
            pages.add(new TiebaTagPage((MainActivity) getActivity()));

            adapter = new MyAdapter();
        }

        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);

        super.initData();
    }


    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return modules.length;
        }

        /**
         * 页签显示数据调用该方法
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return modules[position];
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseTagPage page = pages.get(position);
            View root = page.getRoot();
            container.addView(root);

            // 加载数据
            page.initData();
            return root;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

}
