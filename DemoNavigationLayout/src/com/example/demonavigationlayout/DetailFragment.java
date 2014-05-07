package com.example.demonavigationlayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class DetailFragment extends Fragment
{
  ScrollView myScrollView;
  
  /* (non-Javadoc)
   * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.detail_fragment, container, false);
    myScrollView = (ScrollView) view.findViewById(R.id.detail_view);
    return view;
  }
  
  public int getScrollY()
  {
    return myScrollView.getScrollY();
  }

}
