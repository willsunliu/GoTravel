package com.example.demonavigationlayout;


import java.util.ArrayList;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ScrollView;


public class MainActivity extends FragmentActivity
{
  private static final String TAG = "MainActivity";

  private static final int SCROLL_VIEW_TOP_MARGIN_DP = 500;

  private static final int SCROLL_UP = -20;
  private static final int SCROLL_DOWN = 20;

  private static final int NONE = 0;
  private static final int SLIDING_UP = 1;
  private static final int SLIDING_DOWN = 2;
  private static final int SLIDING_RIGHT = 3;
  private static final int SLIDING_LEFT = 4;

  private SlidingMenu menu;
  private FrameLayout myDragFrame;
  private DetailFragment detailFragment;
  private ScrollView detailLayout;
  private ViewPager viewPager;
  private FirstFragment firstFragment;
  private SecondFragment secondFragment;

  private boolean isViewPagerCovered;
  private int viewPagerPosition;

  private MarginLayoutParams dragFrameLayoutParams;
  private MarginLayoutParams viewPagerLayoutParams;

  private int px;

  private float xDown;
  private float yDown;
  private float xLast;
  private float yLast;
  private int speed = 0;
  private int currentStatus = NONE;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_frame);

    menu = new SlidingMenu(this);
    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
    // menu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
    menu.setShadowWidthRes(R.dimen.shadow_width);
    menu.setMode(SlidingMenu.LEFT);
    menu.setShadowDrawable(R.drawable.left_mode_shadow);
    menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
    menu.setFadeDegree(0.35f);
    menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
    menu.setMenu(R.layout.menu_frame);
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.menu_frame, new SampleListFragment()).commit();
    // if(savedInstanceState == null)
    // {
    // getSupportFragmentManager().beginTransaction()
    // .add(R.id.container, new PlaceholderFragment()).commit();
    // }

    isViewPagerCovered = false;
    viewPager = (ViewPager) findViewById(R.id.view_pager);
    viewPager.setAdapter(new ViewPaperAdapter(getSupportFragmentManager()));
    viewPager.setPageMargin(20);
    viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    viewPagerLayoutParams = (MarginLayoutParams) viewPager.getLayoutParams();
    viewPager.setOnPageChangeListener(new OnPageChangeListener()
    {

      @Override
      public void onPageSelected(int position)
      {
        Log.i(TAG, "onPageSelected");
        viewPagerPosition = position;

        switch(position)
        {
          case 0:
            menu.setMode(SlidingMenu.LEFT);
            menu.setShadowDrawable(R.drawable.left_mode_shadow);
            // menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

            break;

          default:
            menu.setMode(SlidingMenu.RIGHT);
            menu.setShadowDrawable(R.drawable.right_mode_shadow);
            // menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
            break;
        }
      }

      @Override
      public void onPageScrolled(int position, float positionOffset,
          int positionOffsetPixels)
      {
        Log.i(TAG, "onPageScrolled");
        // TODO Auto-generated method stub
        // viewPager.setPadding(20, 20, 20, 20);
      }

      @Override
      public void onPageScrollStateChanged(int state)
      {
        if(state == 0)
        {
          Log.i(TAG, "state is 0");
          // viewPagerLayoutParams.setMargins(0, 0, 0, 0);
          viewPager.setLayoutParams(viewPagerLayoutParams);
          // viewPager.setPadding(0, 0, 0, 0);
        }
        if(state == 1)
        {
          Log.i(TAG, "state is 1");
          // viewPagerLayoutParams.setMargins(20, 20, 20, 20);
          viewPager.setLayoutParams(viewPagerLayoutParams);
          // viewPager.setPadding(20, 20, 20, 20);
        }
        if(state == 2)
        {
          Log.i(TAG, "state is 2");
        }
        Log.i(TAG, "onPageScrollStateChanged");
        // TODO Auto-generated method stub

      }
    });

    firstFragment =
        (FirstFragment) ((ViewPaperAdapter) viewPager.getAdapter()).getItem(0);
    secondFragment =
        (SecondFragment) ((ViewPaperAdapter) viewPager.getAdapter()).getItem(1);

    // myDragFrame = (FrameLayout) findViewById(R.id.drag_detail_frame);
    // myDragFrame = (FrameLayout) firstFragment.getDetailFrameLayout();
    // dragFrameLayoutParams = (MarginLayoutParams)
    // myDragFrame.getLayoutParams();
    //
    // detailFragment = new DetailFragment();
    // getSupportFragmentManager().beginTransaction()
    // .replace(R.id.drag_detail_frame, detailFragment).commit();

    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);
    px = (int) Math.ceil(SCROLL_VIEW_TOP_MARGIN_DP * metrics.density);
  }

  /*
   * (non-Javadoc)
   * @see android.support.v4.app.FragmentActivity#onStart()
   */
  @Override
  protected void onStart()
  {
    // myDragFrame = (FrameLayout) firstFragment.getDetailFrameLayout();
    // dragFrameLayoutParams = (MarginLayoutParams)
    // myDragFrame.getLayoutParams();
    //
    // detailFragment = new DetailFragment();
    // getSupportFragmentManager().beginTransaction()
    // .replace(R.id.drag_detail_frame, detailFragment).commit();
    super.onStart();
  }

  class ZoomOutPageTransformer implements PageTransformer
  {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    @SuppressLint("NewApi")
    @Override
    public void transformPage(View view, float position)
    {
      int pageWidt = view.getWidth();
      int pageHeight = view.getHeight();

      if(position < -1)
      {
        view.setAlpha(0f);
      }
      else if(position <= 1)
      {
        float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
        float vertMargin = pageHeight * (1 - scaleFactor) / 2;
        float horzMargin = pageWidt * (1 - scaleFactor) / 2;
        if(position < 0)
        {
          view.setTranslationX(horzMargin - vertMargin / 2);
        }
        else
        {
          view.setTranslationX(-horzMargin + vertMargin / 2);
        }
        view.setScaleX(scaleFactor);
        view.setScaleY(scaleFactor);
        view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)
            * (1 - MIN_ALPHA));
      } else {
        view.setAlpha(0);
      }
    }

  }

  class ViewPaperAdapter extends FragmentPagerAdapter
  {
    private ArrayList<Fragment> myFragments;

    public ViewPaperAdapter(FragmentManager fm)
    {
      super(fm);
      myFragments = new ArrayList<Fragment>();
      myFragments.add(new FirstFragment());
      myFragments.add(new SecondFragment());
    }

    @Override
    public Fragment getItem(int position)
    {
      return myFragments.get(position);
    }

    @Override
    public int getCount()
    {
      return myFragments.size();
    }

    public View findViewById(int position, int id)
    {
      return null;
    }

  }

  /*
   * (non-Javadoc)
   * @see android.app.Activity#dispatchTouchEvent(android.view.MotionEvent)
   */
  @Override
  public boolean dispatchTouchEvent(MotionEvent ev)
  {
    Log.i(TAG, "scrollview at " + firstFragment.getScrollY());
    switch(ev.getAction())
    {
      case MotionEvent.ACTION_DOWN:
        // Log.i(TAG, "dispatch down");
        xDown = ev.getX();
        yDown = ev.getY();
        xLast = xDown;
        yLast = yDown;
        break;
      case MotionEvent.ACTION_MOVE:
        // Log.i(TAG, "dispatch move");
        double theater = Math.atan2(ev.getY() - yDown, ev.getX() - xDown);
        // Log.i(TAG, "theater=" + theater);
        if(!menu.isMenuShowing() && viewPagerPosition == 0)
        {
          if((isViewPagerCovered && firstFragment.getScrollY() == 0 && (theater > Math.PI / 4 && theater < Math.PI * 3 / 4))
              || (!isViewPagerCovered && (theater < -Math.PI / 4 && theater > -Math.PI * 3 / 4)))
          {
            if(theater > -Math.PI / 4 && theater < Math.PI / 4)
            {
              currentStatus = SLIDING_RIGHT;

              xLast = ev.getX();
              yLast = ev.getY();
            }
            if((theater > Math.PI * 3 / 4 && theater < Math.PI)
                || (theater > -Math.PI && theater < -Math.PI * 3 / 4))
            {
              currentStatus = SLIDING_LEFT;

              xLast = ev.getX();
              yLast = ev.getY();
            }
            if(theater < -Math.PI / 4 && theater > -Math.PI * 3 / 4)
            {
              currentStatus = SLIDING_UP;
              speed = SCROLL_UP;

              if(firstFragment.getDetailLayoutTopMargin() <= 0)
              {
                // dragFrameLayoutParams.topMargin = 0;
                // myDragFrame.setLayoutParams(dragFrameLayoutParams);
                firstFragment.setDetailLayoutTopMargin(0);
              }
              else
              {
                // dragFrameLayoutParams.topMargin += ev.getY() - yLast;
                // myDragFrame.setLayoutParams(dragFrameLayoutParams);
                firstFragment.setDetailLayoutTopMargin(firstFragment
                    .getDetailLayoutTopMargin() + (int) (ev.getY() - yLast));
              }
              xLast = ev.getX();
              yLast = ev.getY();
              return true;
            }
            if(theater > Math.PI / 4 && theater < Math.PI * 3 / 4)
            {
              currentStatus = SLIDING_DOWN;
              speed = SCROLL_DOWN;

              if(firstFragment.getDetailLayoutTopMargin() >= px)
              {
                // dragFrameLayoutParams.topMargin = px;
                // myDragFrame.setLayoutParams(dragFrameLayoutParams);
                firstFragment.setDetailLayoutTopMargin(px);
              }
              else
              {
                // dragFrameLayoutParams.topMargin += ev.getY() - yLast;
                // myDragFrame.setLayoutParams(dragFrameLayoutParams);
                firstFragment.setDetailLayoutTopMargin(firstFragment
                    .getDetailLayoutTopMargin() + (int) (ev.getY() - yLast));
              }
              xLast = ev.getX();
              yLast = ev.getY();
              return true;
            }
          }
        }
        break;
      case MotionEvent.ACTION_UP:
        // Log.i(TAG, "dispatch up");
        if(currentStatus == SLIDING_DOWN || currentStatus == SLIDING_UP)
        {
          new AnimationTask().execute(speed);
        }
        currentStatus = NONE;
        break;

      default:
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  class AnimationTask extends AsyncTask<Integer, Integer, Integer>
  {

    @Override
    protected Integer doInBackground(Integer... params)
    {
      // int topMargin = dragFrameLayoutParams.topMargin;
      int topMargin = firstFragment.getDetailLayoutTopMargin();
      while(true)
      {
        topMargin += params[0];
        if(params[0] < 0)
        {
          if(topMargin <= 0)
          {
            topMargin = 0;
            isViewPagerCovered = true;
            break;
          }
        }
        else if(params[0] > 0)
        {
          if(topMargin >= px)
          {
            topMargin = px;
            isViewPagerCovered = false;
            break;
          }
        }
        publishProgress(topMargin);
        sleep(10);
      }
      return topMargin;
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Integer... topMargin)
    {
      // dragFrameLayoutParams.topMargin = topMargin[0];
      // myDragFrame.setLayoutParams(dragFrameLayoutParams);
      firstFragment.setDetailLayoutTopMargin(topMargin[0]);
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(Integer result)
    {
      // dragFrameLayoutParams.topMargin = result;
      // myDragFrame.setLayoutParams(dragFrameLayoutParams);
      firstFragment.setDetailLayoutTopMargin(result);
    }

  }

  private void sleep(int time)
  {
    try
    {
      Thread.sleep(time);
    }
    catch(InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
