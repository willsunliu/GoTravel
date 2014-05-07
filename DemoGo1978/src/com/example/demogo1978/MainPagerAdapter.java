package com.example.demogo1978;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class MainPagerAdapter extends PagerAdapter
{

  ArrayList<View> list;
  
  public MainPagerAdapter(ArrayList<View> list) {
    super();
    this.list = list;
  }

  @Override
  public int getCount()
  {
    return list.size();
  }

  @Override
  public boolean isViewFromObject(View view, Object obj)
  {
    return view == obj;
  }

  /* (non-Javadoc)
   * @see android.support.v4.view.PagerAdapter#destroyItem(android.view.ViewGroup, int, java.lang.Object)
   */
  @Override
  public void destroyItem(ViewGroup container, int position, Object object)
  {
    ((ViewPager) container).removeView(list.get(position));
  }

  /* (non-Javadoc)
   * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view.ViewGroup, int)
   */
  @Override
  public Object instantiateItem(ViewGroup container, int position)
  {
    ((ViewPager) container).addView(list.get(position));
    return list.get(position);
  }

}
