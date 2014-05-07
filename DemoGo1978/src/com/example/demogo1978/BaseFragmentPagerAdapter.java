package com.example.demogo1978;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class BaseFragmentPagerAdapter extends FragmentPagerAdapter
{

  ArrayList<Fragment> mList; 
  
  public BaseFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list)
  {
    super(fm);
    mList = list;
  }

  @Override
  public Fragment getItem(int position)
  {
    return mList.get(position);
  }

  @Override
  public int getCount()
  {
    return mList.size();
  }

}
