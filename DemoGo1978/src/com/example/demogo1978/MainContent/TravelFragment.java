package com.example.demogo1978.MainContent;


import java.util.ArrayList;
import java.util.List;

import com.example.demogo1978.ArticleListAdapter;
import com.example.demogo1978.MainActivity;
import com.example.demogo1978.MyApplication;
import com.example.demogo1978.MyData;
import com.example.demogo1978.OnVerticalScrollActionListener;
import com.example.demogo1978.R;
import com.example.demogo1978.R.layout;
import com.example.demogo1978.view.BottomBar;
import com.example.demogo1978.view.TopBar;

import android.R.integer;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ListView;


public class TravelFragment extends Fragment implements
    OnVerticalScrollActionListener
{

  private static final String TAG = "TravelFragment";

  ArticleListAdapter adapter;
  ArticleListView listView;

  HorizontalScrollView topTab;
  MarginLayoutParams topTabLayoutParams;
  int topTabOriginalMarginTop;

  TopBar topBar;
  MarginLayoutParams topBarLayoutParams;
  int topBarOriginalMarginTop;

  FrameLayout bottomBar;
  MarginLayoutParams bottomBarLayoutParams;
  int bottomBarOriginalMarginBottom;

  private static final int HIDE = 1;
  private static final int SHOW = 2;
  private static final int SCROLLING = 3;

  private int topTabState = SHOW;
  private int topBarState = SHOW;
  private int bottomBarState = SHOW;

  /*
   * (non-Javadoc)
   * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
   */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    Log.i(TAG, "onCreate");
    super.onCreate(savedInstanceState);

    ArrayList<ArticleListItem> list = new ArrayList<ArticleListItem>();
    for(int i = 0; i < 10; i++)
    {
      ArticleListItem item =
          new ArticleListItem("罗马文化游", "23", "31", getResources().getString(
              R.string.item_content), R.drawable.icon48, R.drawable.icon48,
              R.drawable.item_pic, 200);
      list.add(item);
    }
    adapter = new ArticleListAdapter(getActivity(), list);

  }

  /*
   * (non-Javadoc)
   * @see
   * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
   * android.view.ViewGroup, android.os.Bundle)
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    Log.i(TAG, "onCreateView");
    View rootView =
        inflater.inflate(R.layout.fragment_travel, container, false);

    listView = (ArticleListView) rootView.findViewById(R.id.article_list);
    listView.setOnVerticalScrollActionListener(this);
    listView.setAdapter(adapter);

    topTab = (HorizontalScrollView) rootView.findViewById(R.id.travel_top_tab);
    topTabLayoutParams = (MarginLayoutParams) topTab.getLayoutParams();
    topTabOriginalMarginTop = topTabLayoutParams.topMargin;

    // Activity view
    topBar = (TopBar) getActivity().findViewById(R.id.top_bar);
    topBarLayoutParams = (MarginLayoutParams) topBar.getLayoutParams();
    topBarOriginalMarginTop =
        ((MainActivity) getActivity()).getTopBarOriginalTopMargin();

    bottomBar = (FrameLayout) getActivity().findViewById(R.id.bottom_bar);
    bottomBarLayoutParams = (MarginLayoutParams) bottomBar.getLayoutParams();
    bottomBarOriginalMarginBottom =
        ((MainActivity) getActivity()).getBottomBarOriginalBottomMargin();

    topTabState = SHOW;
    topBarState = SHOW;
    bottomBarState = SHOW;

    return rootView;
  }

  public void onVerticalScrolling(float deltaY)
  {
    int topTabHeight = topTab.getHeight();
    int topBarHeight = topBar.getHeight();
    int bottomBarHeight = bottomBar.getHeight();

    int topTabMarginTop = topTabLayoutParams.topMargin;
    int topBarMarginTop = topBarLayoutParams.topMargin;
    int bottomBarMarginBottom = bottomBarLayoutParams.bottomMargin;

    Log.i(TAG, "topBarState=" + topBarState + "; topTabState=" + topTabState
        + "bottomBarState=" + bottomBarState);
    if(topBarState == SHOW && bottomBarState == SHOW)
    {
      topTabMarginTop += deltaY;
      Log.i(TAG, "deltaY=" + deltaY + "; topTabMarginTop=" + topTabMarginTop);
      if(topTabMarginTop <= topTabOriginalMarginTop - topTabHeight)
      {
        topTabMarginTop = topTabOriginalMarginTop - topTabHeight;
        topTabState = HIDE;
      }
      if(topTabMarginTop >= topTabOriginalMarginTop)
      {
        topTabMarginTop = topTabOriginalMarginTop;
        topTabState = SHOW;
      }
      if(topTabMarginTop > topTabOriginalMarginTop
          && topTabMarginTop < topTabOriginalMarginTop - topTabHeight)
      {
        topTabState = SCROLLING;
      }

      topTabLayoutParams.topMargin = topTabMarginTop;
      topTab.setLayoutParams(topTabLayoutParams);
    }

    // if(topBarState == SHOW && topTabState == HIDE)
    // {
    // topTabMarginTop += deltaY;
    //
    // if(topTabMarginTop >= topTabOriginalMarginTop)
    // {
    // topTabMarginTop = topTabOriginalMarginTop;
    // topTabState = SHOW;
    // }
    //
    // topTabLayoutParams.topMargin = topTabMarginTop;
    // topTab.setLayoutParams(topTabLayoutParams);
    // }
    if(topTabState == HIDE)
    {
      topBarMarginTop += deltaY;
      bottomBarMarginBottom += deltaY;

      if(topBarMarginTop <= topBarOriginalMarginTop - topBarHeight)
      {
        topBarMarginTop = topBarOriginalMarginTop - topBarHeight;
        topBarState = HIDE;
      }

      if(bottomBarMarginBottom <= bottomBarOriginalMarginBottom
          - bottomBarHeight)
      {
        bottomBarMarginBottom = bottomBarOriginalMarginBottom - bottomBarHeight;
        bottomBarState = HIDE;
      }

      if(topBarMarginTop >= topBarOriginalMarginTop)
      {
        topBarMarginTop = topBarOriginalMarginTop;
        topBarState = SHOW;
      }

      if(bottomBarMarginBottom >= bottomBarOriginalMarginBottom)
      {
        bottomBarMarginBottom = bottomBarOriginalMarginBottom;
        bottomBarState = SHOW;
      }

      if(topBarMarginTop > topBarOriginalMarginTop - topBarHeight
          && topBarMarginTop < topBarOriginalMarginTop)
      {
        topBarState = SCROLLING;
      }
      
      if(bottomBarMarginBottom > bottomBarOriginalMarginBottom - bottomBarHeight
          && bottomBarMarginBottom < bottomBarOriginalMarginBottom)
      {
        bottomBarState = SCROLLING;
      }

      topBarLayoutParams.topMargin = topBarMarginTop;
      topBar.setLayoutParams(topBarLayoutParams);

      bottomBarLayoutParams.bottomMargin = bottomBarMarginBottom;
      bottomBar.setLayoutParams(bottomBarLayoutParams);
    }

    // if(topTabState == HIDE && topBarState == SHOW && deltaY < 0)
    // {
    // topBarMarginTop += deltaY;
    // bottomBarMarginBottom += deltaY;
    //
    // if(topBarMarginTop <= topBarOriginalMarginTop - topBarHeight)
    // {
    // topBarMarginTop = topBarOriginalMarginTop - topBarHeight;
    // topBarState = HIDE;
    // }
    //
    // if(bottomBarMarginBottom <= bottomBarOriginalMarginBottom
    // - bottomBarHeight)
    // {
    // bottomBarMarginBottom = bottomBarOriginalMarginBottom - bottomBarHeight;
    // bottomBarState = HIDE;
    // }
    //
    // topBarLayoutParams.topMargin = topBarMarginTop;
    // topBar.setLayoutParams(topBarLayoutParams);
    //
    // bottomBarLayoutParams.bottomMargin = bottomBarMarginBottom;
    // bottomBar.setLayoutParams(bottomBarLayoutParams);
    // }
    //
    // if(topTabState == HIDE && topBarState == HIDE && deltaY > 0)
    // {
    // topBarMarginTop += deltaY;
    // bottomBarMarginBottom += deltaY;
    //
    // if(topBarMarginTop >= topBarOriginalMarginTop)
    // {
    // topBarMarginTop = topBarOriginalMarginTop;
    // topBarState = SHOW;
    // }
    //
    // if(bottomBarMarginBottom >= bottomBarOriginalMarginBottom)
    // {
    // bottomBarMarginBottom = bottomBarOriginalMarginBottom;
    // bottomBarState = SHOW;
    // }
    //
    // topBarLayoutParams.topMargin = topBarMarginTop;
    // topBar.setLayoutParams(topBarLayoutParams);
    //
    // bottomBarLayoutParams.bottomMargin = bottomBarMarginBottom;
    // bottomBar.setLayoutParams(bottomBarLayoutParams);
    // }
  }

  public void onVerticalScrollFinished()
  {
    if(topBarState == SHOW)
    {
      new TopTabAnimationTask().execute();
    }

    if(topTabState == HIDE)
    {
      new TopAndBottomBarAnimationTask().execute();
    }
  }

  /**
   * @author IT01
   */
  private class TopAndBottomBarAnimationTask extends
      AsyncTask<Void, Integer, ArrayList<Integer>>
  {

    @Override
    protected ArrayList<Integer> doInBackground(Void... params)
    {
      int speed = 0;
      int topBarHeight = topBar.getHeight();
      int bottomBarHeight = bottomBar.getHeight();
      int topBarMarginTop = topBarLayoutParams.topMargin;
      int bottomBarMarginBottom = bottomBarLayoutParams.bottomMargin;

      ArrayList<Integer> list = new ArrayList<Integer>();

      if(topBarMarginTop < (topBarOriginalMarginTop - topBarHeight) / 2
          || bottomBarMarginBottom < (bottomBarOriginalMarginBottom - bottomBarHeight) / 2)
      {
        speed = -20;
      }
      else
      {
        speed = 20;
      }

      while(true)
      {
        topBarMarginTop += speed;
        bottomBarMarginBottom += speed;

        // TopBar
        if(topBarMarginTop >= topBarOriginalMarginTop)
        {
          topBarMarginTop = topBarOriginalMarginTop;
          topBarState = SHOW;
          break;
        }

        if(topBarMarginTop <= topBarOriginalMarginTop - topBarHeight)
        {
          topBarMarginTop = topBarOriginalMarginTop - topBarHeight;
          topBarState = HIDE;
          break;
        }

        // BottomBar
        if(bottomBarMarginBottom >= bottomBarOriginalMarginBottom)
        {
          bottomBarMarginBottom = bottomBarOriginalMarginBottom;
          bottomBarState = SHOW;
          break;
        }

        if(bottomBarMarginBottom <= bottomBarOriginalMarginBottom
            - bottomBarHeight)
        {
          bottomBarMarginBottom =
              bottomBarOriginalMarginBottom - bottomBarHeight;
          bottomBarState = HIDE;
          break;
        }

        publishProgress(topBarMarginTop, bottomBarMarginBottom);

        try
        {
          Thread.sleep(10);
        }
        catch(InterruptedException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

      list.add(topBarMarginTop);
      list.add(bottomBarMarginBottom);

      return list;
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(ArrayList<Integer> result)
    {
      topBarLayoutParams.topMargin = result.get(0);
      bottomBarLayoutParams.bottomMargin = result.get(1);
      topBar.setLayoutParams(topBarLayoutParams);
      bottomBar.setLayoutParams(bottomBarLayoutParams);
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Integer... values)
    {
      topBarLayoutParams.topMargin = values[0];
      bottomBarLayoutParams.bottomMargin = values[1];
      topBar.setLayoutParams(topBarLayoutParams);
      bottomBar.setLayoutParams(bottomBarLayoutParams);
    }

  }

  private class TopTabAnimationTask extends AsyncTask<Void, Integer, Integer>
  {

    @Override
    protected Integer doInBackground(Void... params)
    {
      int speed = 0;
      int height = topTab.getHeight();
      int top = topTabLayoutParams.topMargin;
      if(top < -height / 2)
      {
        speed = -20;
      }
      else
      {
        speed = 20;
      }

      while(true)
      {
        top += speed;
        if(top > topBarOriginalMarginTop)
        {
          top = topBarOriginalMarginTop;
          topTabState = SHOW;
          break;
        }
        if(top < topBarOriginalMarginTop - height)
        {
          top = topBarOriginalMarginTop - height;
          topTabState = HIDE;
          break;
        }

        publishProgress(top);
        try
        {
          Thread.sleep(10);
        }
        catch(InterruptedException e)
        {
          e.printStackTrace();
        }
      }

      return top;
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(Integer result)
    {
      topTabLayoutParams.topMargin = result;
      topTab.setLayoutParams(topTabLayoutParams);
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Integer... values)
    {
      topTabLayoutParams.topMargin = values[0];
      topTab.setLayoutParams(topTabLayoutParams);
    }

  }
}
