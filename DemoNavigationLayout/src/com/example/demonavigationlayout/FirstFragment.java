package com.example.demonavigationlayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class FirstFragment extends Fragment
{
  private static final String TAG = "FirstFragment";
  
  RefreshableView myRefreshableView;
  ScrollView detailLayout;
  MarginLayoutParams detailLayoutParams;
  DetailFragment detailFragment;
  
  /* (non-Javadoc)
   * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    // TODO Auto-generated method stub
    View view = inflater.inflate(R.layout.first_fragment, container, false);
    TextView tv = (TextView) view.findViewById(R.id.content_text);
    tv.setText("我");
    
    detailLayout = (ScrollView) view.findViewById(R.id.drag_detail_frame);
    detailLayoutParams =
        (MarginLayoutParams) detailLayout.getLayoutParams();
    
    myRefreshableView =
        (RefreshableView) view.findViewById(R.id.refreshable_view);
    myRefreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {

      @Override
      public void onRefresh()
      {
        Log.i(TAG, "onRefresh");
        try
        {
          Thread.sleep(3000);
        }
        catch(InterruptedException e)
        {
          e.printStackTrace();
        }
        myRefreshableView.finishRefreshing();
      }
      
    }, 0);
    
    return view;
  }
  
  public void setDetailLayoutTopMargin(int topMargin)
  {
    detailLayoutParams.topMargin = topMargin;
    detailLayout.setLayoutParams(detailLayoutParams);
  }
  
  public int getDetailLayoutTopMargin()
  {
    return detailLayoutParams.topMargin;
  }
  
  public View getDetailLayout()
  {
    return detailLayout;
  }
  
  public int getScrollY()
  {
    return detailLayout.getScrollY();
  }

}
