package com.example.demonavigationlayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SecondFragment extends Fragment
{
  private static final String TAG = "SecondFragment";
  
  private RefreshableView myRefreshableView;
  /* (non-Javadoc)
   * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.second_fragment, container, false);
    TextView tv = (TextView) view.findViewById(R.id.content_text);
    tv.setText("2");
    
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
  
}
