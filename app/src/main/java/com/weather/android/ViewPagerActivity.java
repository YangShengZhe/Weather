package com.weather.android;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import com.weather.android.util.MyPagerAdapter;

public class ViewPagerActivity extends AppCompatActivity {

    private List<View> viewList = new ArrayList<>();//ViewPager数据源
    private MyPagerAdapter myPagerAdapter;//适配器
    private ViewPager viewPager;
    private int count = 0; //页面展示的数据，无实际作用


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);

        myPagerAdapter = new MyPagerAdapter(viewList);//创建适配器实例
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(myPagerAdapter);//为ViewPager设置适配器

    }
    /**
     * 添加选项菜单
     * 为了不影响ViewPager每个页面的一致性，这里使用选项菜单来操作添加和删除页面的点击事件
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,1,1,"添加页面");
        menu.add(1,2,1,"删除页面");
        return super.onCreateOptionsMenu(menu);
    }
    /**
     * 为选项菜单设置点击事件监听
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1://添加页面的点击事件
                String text = "页面" + count;
                count++;
                addPage(text);
                break;
            case 2://删除页面的点击事件
                delPage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     *该方法封装了添加页面的代码逻辑实现，参数text为要展示的数据
     */
    public void addPage(String text){
        LayoutInflater inflater = LayoutInflater.from(this);//获取LayoutInflater的实例
        View view = inflater.inflate(R.layout.activity_weather, null);//调用LayoutInflater实例的inflate()方法来加载页面的布局

        viewList.add(view);//为数据源添加一项数据
        myPagerAdapter.notifyDataSetChanged();//通知UI更新
    }
    /**
     * 删除当前页面
     */
    public void delPage(){
        int position = viewPager.getCurrentItem();//获取当前页面位置
        viewList.remove(position);//删除一项数据源中的数据
        myPagerAdapter.notifyDataSetChanged();//通知UI更新

    }
}
