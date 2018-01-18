package com.weather.android.util;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
/**
 * Created by Padge on 2018/1/18.
 */

public class MyPagerAdapter extends PagerAdapter {
    private List<View> viewList;

    public MyPagerAdapter(List<View> viewList){
        this.viewList = viewList;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
