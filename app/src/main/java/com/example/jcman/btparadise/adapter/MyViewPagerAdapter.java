package com.example.jcman.btparadise.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2015/7/21.
 */
public class MyViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<View> mList;
    private List<String> mTitles;

    public MyViewPagerAdapter(Context context,List<View> list,List<String> titles){
        mContext = context;
        mList = list;
        mTitles = titles;
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager)container).addView(mList.get(position));
        return mList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView(mList.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
