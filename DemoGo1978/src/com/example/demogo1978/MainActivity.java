package com.example.demogo1978;


import java.util.ArrayList;

import com.example.demogo1978.MainContent.MainContentFragment;
import com.example.demogo1978.QA.QaFragment;
import com.example.demogo1978.UserCenter.UserCenterFragment;
import com.example.demogo1978.animation.ZoomOutPageTransformer;
import com.example.demogo1978.view.TopBar;
import com.example.demogo1978.view.ViewPagerScrollBar;

import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends BaseActivity implements OnPageChangeListener
{
  private static final String TAG = "MainActivity";

  // FrameLayout mainContentFrameLayout;
  // FrameLayout userCenterFrameLayout;
  // FrameLayout qaFrameLayout;

  MainContentFragment mainContentFragment;
  UserCenterFragment userCenterFragment;
  QaFragment qaFragment;

  ViewPager mViewPager;
  ViewPagerScrollBar mViewPagerScrollBar;

  TopBar mTopBar;
  private int topOriginalMargin;
  FrameLayout mBottomBar;
  private int bottomOriginalMargin;

  private int touchSlop;
  private float pointX;
  private float pointY;
  private float lastX;
  private float lastY;

  // 旅游
  Button mBottomTravelButton;
  Button mBottomLifeButton;
  Button mBottomKnoledgeButton;
  Button mBottomEquitmentButton;
  Button mBottomNotificationButton;

  // 个人
  Button mBottomInteractionButton;
  Button mBottomCollectionButton;
  Button mBottomFriendsButton;
  Button mBottomShoppingTrolleyButton;
  Button mBottomSettingsButton;

  // 问答
  Button mBottomDestinationButton;
  Button mBottomPreparationButton;
  Button mBottomNecessaryButton;

  private static final int[] titleIds = {R.id.user_center_title,
      R.id.main_content_title, R.id.qa_title,};

  private static final int SHOW = 1;
  private static final int HIDE = 2;

  private final int SLIDING_NONE = 0;
  private final int SLIDING_FROM_LEFT_TO_RIGHT = 1;
  private final int SLIDING_FROM_RIGHT_TO_LEFT = 2;
  private int slidingState = SLIDING_NONE;

  private static final int INVISIBLE_VIEW_PAGER_SCROLL_BAR = 0x1;
  Runnable runnable;
  Handler myHandler = new Handler()
  {

    /*
     * (non-Javadoc)
     * @see android.os.Handler#handleMessage(android.os.Message)
     */
    @Override
    public void handleMessage(Message msg)
    {
      switch(msg.what)
      {
        case INVISIBLE_VIEW_PAGER_SCROLL_BAR:
          if(!(slidingState == SLIDING_FROM_LEFT_TO_RIGHT || slidingState == SLIDING_FROM_RIGHT_TO_LEFT))
          {
            mViewPagerScrollBar.setVisibility(View.INVISIBLE);
          }
          break;

        default:
          break;
      }
    }

  };

  /**
   * 初始化个人，旅游，出行百科三个主界面
   */
  private void initPagerView()
  {
    // LayoutInflater inflater = getLayoutInflater();
    // userCenterFrameLayout =
    // (FrameLayout) inflater.inflate(R.layout.user_center, null);
    // mainContentFrameLayout =
    // (FrameLayout) inflater.inflate(R.layout.main_content, null);
    // qaFrameLayout = (FrameLayout) inflater.inflate(R.layout.qa, null);

    // ArrayList<View> list = new ArrayList<View>();
    // list.add(userCenterFrameLayout);
    // list.add(mainContentFrameLayout);
    // list.add(qaFrameLayout);

    mainContentFragment = new MainContentFragment();
    userCenterFragment = new UserCenterFragment();
    qaFragment = new QaFragment();

    ArrayList<Fragment> list = new ArrayList<Fragment>();
    list.add(userCenterFragment);
    list.add(mainContentFragment);
    list.add(qaFragment);

    BaseFragmentPagerAdapter mMainPagerAdapter =
        new BaseFragmentPagerAdapter(getSupportFragmentManager(), list);

    // MainPagerAdapter mMainPagerAdapter = new MainPagerAdapter(list);

    mViewPager = (ViewPager) findViewById(R.id.main_pager);
    mViewPager.setAdapter(mMainPagerAdapter);
    mViewPager.setCurrentItem(1);
    mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    mViewPager.setOnPageChangeListener(this);

    mViewPagerScrollBar =
        (ViewPagerScrollBar) findViewById(R.id.view_pager_scroll_bar);
    ((RelativeLayout) findViewById(R.id.container))
        .bringChildToFront(mViewPagerScrollBar);
    mViewPagerScrollBar.setVisibility(View.INVISIBLE);
  }

  /**
   * 初始化顶部条
   */
  private void initTopBar()
  {
    mTopBar = (TopBar) findViewById(R.id.top_bar);
    topOriginalMargin =
        ((MarginLayoutParams) mTopBar.getLayoutParams()).topMargin;

    ImageView logo = new ImageView(getApplicationContext());
    logo.setImageDrawable(getResources().getDrawable(R.drawable.logo48));
    mTopBar.addCenterLogo(logo, 50, 50);

    ImageView logo1 = new ImageView(getApplicationContext());
    logo1.setImageDrawable(getResources().getDrawable(R.drawable.logo48));
    ImageView logo2 = new ImageView(getApplicationContext());
    logo2.setImageDrawable(getResources().getDrawable(R.drawable.logo48));
    ArrayList<View> list = new ArrayList<View>();
    list.add(logo1);
    list.add(logo2);
    mTopBar.addMenuView(RelativeLayout.ALIGN_PARENT_RIGHT, list, 50, 50, 5, 5,
        5, 5);
  }

  /**
   * 初始化底部条的按钮
   * 
   * @param resId
   * @param isSelected
   * @param clickListener
   * @return
   */
  private Button initBottomButton(int resId, boolean isSelected,
      OnClickListener clickListener)
  {
    Button button = (Button) findViewById(resId);
    button.setSelected(isSelected);
    button.setOnClickListener(clickListener);
    return button;
  }

  /**
   * 初始化底部条
   */
  private void initBottomBar()
  {
    mBottomBar = (FrameLayout) findViewById(R.id.bottom_bar);
    bottomOriginalMargin =
        ((MarginLayoutParams) mBottomBar.getLayoutParams()).bottomMargin;
    mBottomBar.bringChildToFront(findViewById(R.id.main_content_bottom_bar));

    MyBottomButtonClickListener listener = new MyBottomButtonClickListener();
    mBottomTravelButton =
        initBottomButton(R.id.bottom_bar_travel, true, listener);
    mBottomLifeButton = initBottomButton(R.id.bottom_bar_life, false, listener);
    mBottomKnoledgeButton =
        initBottomButton(R.id.bottom_bar_knowledge, false, listener);
    mBottomEquitmentButton =
        initBottomButton(R.id.bottom_bar_equitment, false, listener);
    mBottomNotificationButton =
        initBottomButton(R.id.bottom_bar_notification, false, listener);

    mBottomInteractionButton =
        initBottomButton(R.id.bottom_bar_interaction, true, listener);
    mBottomCollectionButton =
        initBottomButton(R.id.bottom_bar_collection, false, listener);
    mBottomFriendsButton =
        initBottomButton(R.id.bottom_bar_friends, false, listener);
    mBottomShoppingTrolleyButton =
        initBottomButton(R.id.bottom_bar_shopping_trolley, false, listener);
    mBottomSettingsButton =
        initBottomButton(R.id.bottom_bar_settings, false, listener);

    mBottomDestinationButton =
        initBottomButton(R.id.bottom_bar_destination, true, listener);
    mBottomPreparationButton =
        initBottomButton(R.id.bottom_bar_preparation, false, listener);
    mBottomNecessaryButton =
        initBottomButton(R.id.bottom_bar_necessary, false, listener);
  }

  /**
   * 底部条上按钮的点击事件处理类
   * 
   * @author IT01
   */
  private class MyBottomButtonClickListener implements OnClickListener
  {

    @Override
    public void onClick(View v)
    {
      int[] array = null;
      int length = 0;

      switch(v.getId())
      {
        case R.id.bottom_bar_travel:
          array = MyData.mainContentBottomButtons;
          length = array.length;
          mainContentFragment
              .changeChildFragment(MainContentFragment.TRAVEL_FRAGMENT);
          break;
        case R.id.bottom_bar_life:
          array = MyData.mainContentBottomButtons;
          length = array.length;
          mainContentFragment
              .changeChildFragment(MainContentFragment.LIFE_FRAGMENT);
          break;
        case R.id.bottom_bar_knowledge:
          array = MyData.mainContentBottomButtons;
          length = array.length;
          mainContentFragment
              .changeChildFragment(MainContentFragment.KNOWLEDGE_FRAGMENT);
          break;
        case R.id.bottom_bar_equitment:
          array = MyData.mainContentBottomButtons;
          length = array.length;
          mainContentFragment
              .changeChildFragment(MainContentFragment.EQUITMENT_FRAGMENT);
          break;
        case R.id.bottom_bar_notification:
          array = MyData.mainContentBottomButtons;
          length = array.length;
          mainContentFragment
              .changeChildFragment(MainContentFragment.NOTIFICATION_FRAGMENT);
          break;
        case R.id.bottom_bar_interaction:
        case R.id.bottom_bar_collection:
        case R.id.bottom_bar_friends:
        case R.id.bottom_bar_shopping_trolley:
        case R.id.bottom_bar_settings:
          array = MyData.userCenterBottomButtons;
          length = array.length;
          break;
        case R.id.bottom_bar_destination:
        case R.id.bottom_bar_preparation:
        case R.id.bottom_bar_necessary:
          array = MyData.qaBottomButtons;
          length = array.length;
          break;
      }

      for(int i = 0; i < length; i++)
      {
        if(v.getId() == array[i])
        {
          findViewById(array[i]).setSelected(true);
        }
        else
        {
          findViewById(array[i]).setSelected(false);
        }
      }
    }

  }

  /*
   * (non-Javadoc)
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    Log.i(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
    slidingState = SLIDING_NONE;

    initPagerView();

    initTopBar();

    initBottomBar();
  }

  /*
   * (non-Javadoc)
   * @see android.support.v4.app.FragmentActivity#onStart()
   */
  @Override
  protected void onStart()
  {
    super.onStart();
  }

  /*
   * (non-Javadoc)
   * @see android.support.v4.app.FragmentActivity#onResume()
   */
  @Override
  protected void onResume()
  {
    super.onResume();
    // mainContentFragment.getChildFragmentManager().beginTransaction()
    // .add(R.id.main_content_container, new TravelFragment()).commit();
  }

  @Override
  public void onPageScrollStateChanged(int state)
  {
    switch(state)
    {
      case ViewPager.SCROLL_STATE_IDLE:
//        runnable = new Runnable()
//        {
//          
//          @Override
//          public void run()
//          {
//            Message msg = new Message();
//            msg.what = INVISIBLE_VIEW_PAGER_SCROLL_BAR;
//            myHandler.sendMessage(msg);
//          }
//        };
//        myHandler.postDelayed(runnable, 1000);
        new Thread(new Runnable()
        {

          @Override
          public void run()
          {
            Message msg = new Message();
            msg.what = INVISIBLE_VIEW_PAGER_SCROLL_BAR;
            myHandler.sendMessageDelayed(msg, 1000);
          }
        }).start();
        
        slidingState = SLIDING_NONE;
        break;
      case ViewPager.SCROLL_STATE_DRAGGING:
        // 在再次拖动前，先将之前等待发送的handler message消息清除
        myHandler.removeMessages(INVISIBLE_VIEW_PAGER_SCROLL_BAR);
        findViewById(R.id.view_pager_scroll_bar).setVisibility(View.VISIBLE);
        break;
      case ViewPager.SCROLL_STATE_SETTLING:
        break;
    }
  }

  /**
   * 在滑动过程中更新滑块的rect
   * 
   * @param position
   * @param offset
   * @param offsetPixels
   */
  @Override
  public void onPageScrolled(int position, float offset, int offsetPixels)
  {
    int currentPosition = position;
    int nextPosition = position;

    /*
     * 从左往右和从右往左两种情况，ViewPager对position和offset的处理不同：
     * 从手指左往右滑动，即切换到左边的view。ViewPager会先将position做减1处理，即
     * onPageScrolled的过程中，使用的都是下一个view的position, 而offset则会从1递减
     * 到0。
     * 从手指右往左滑动，即切换到右边的view。此时position会一直保持为当前view的
     * position，offset从0到1递增。
     */
    if(slidingState == SLIDING_FROM_LEFT_TO_RIGHT)
    {
      currentPosition = position + 1;
      nextPosition = position;
      offset = 1 - offset;
      if(nextPosition < 0)
      {
        currentPosition = position;
        nextPosition = position;
        offset = 1 - offset;
      }
      if(currentPosition >= titleIds.length)
      {
        currentPosition = position;
        nextPosition = position;
        offset = 1 - offset;
      }
    }
    else if(slidingState == SLIDING_FROM_RIGHT_TO_LEFT)
    {
      currentPosition = position;
      nextPosition = position + 1;
      if(nextPosition >= titleIds.length)
      {
        currentPosition = position;
        nextPosition = position;
      }
    }

    TextView currentView = (TextView) findViewById(titleIds[currentPosition]);
    int currentLeft = currentView.getLeft();
    int currentTop = currentView.getTop();
    int currentWidth = currentView.getWidth();
    int currentHeight = currentView.getHeight();

    TextView nextView = (TextView) findViewById(titleIds[nextPosition]);
    int nextLeft = nextView.getLeft();
    int nextWidth = nextView.getWidth();

    int rectLeft = (int) (currentLeft + (nextLeft - currentLeft) * offset);
    int rectTop = currentTop;
    int rectWidth = (int) (currentWidth + (nextWidth - currentWidth) * offset);
    int rectHeight = currentHeight;
    Rect rect =
        new Rect(rectLeft, rectTop, rectLeft + rectWidth, rectTop + rectHeight);
    mViewPagerScrollBar.setRect(rect);
  }

  @Override
  public void onPageSelected(int position)
  {
    /*
     * 根据不同的Pager调整TopBar和BottomBar显示的内容
     */

    if(position == 0)
    {
      ImageView logo1 = new ImageView(getApplicationContext());
      logo1.setImageDrawable(getResources().getDrawable(R.drawable.logo48));
      ArrayList<View> list = new ArrayList<View>();
      list.add(logo1);
      mTopBar.addMenuView(RelativeLayout.ALIGN_PARENT_RIGHT, list, 50, 50, 5,
          5, 5, 5);
      mBottomBar.bringChildToFront(findViewById(R.id.user_center_bottom_bar));
    }
    else
    {
      ImageView logo1 = new ImageView(getApplicationContext());
      logo1.setImageDrawable(getResources().getDrawable(R.drawable.logo48));
      ImageView logo2 = new ImageView(getApplicationContext());
      logo2.setImageDrawable(getResources().getDrawable(R.drawable.logo48));
      ArrayList<View> list = new ArrayList<View>();
      list.add(logo1);
      list.add(logo2);
      mTopBar.addMenuView(RelativeLayout.ALIGN_PARENT_RIGHT, list, 50, 50, 5,
          5, 5, 5);
    }

    if(position == 1)
    {
      ImageView logo = new ImageView(getApplicationContext());
      logo.setImageDrawable(getResources().getDrawable(R.drawable.logo48));
      mTopBar.addCenterLogo(logo, 50, 50);
      findViewById(R.id.main_content_bottom_bar).setVisibility(View.VISIBLE);
      mBottomBar.bringChildToFront(findViewById(R.id.main_content_bottom_bar));

    }
    else
    {
      ImageView logo = new ImageView(getApplicationContext());
      logo.setImageDrawable(getResources().getDrawable(R.drawable.icon48));
      mTopBar.addCenterLogo(logo, 50, 50);
      findViewById(R.id.main_content_bottom_bar).setVisibility(View.GONE);
    }

    if(position == 2)
    {
      mBottomBar.bringChildToFront(findViewById(R.id.qa_bottom_bar));
    }
  }

  public void showArticle()
  {
    mainContentFragment.showArticle();

    // 显示文章的时候显示TopBar和隐藏BottomBar
    new MyTopBarAnimationTask().execute(SHOW);
    new MyBottomBarAnimationTask().execute(HIDE);
  }

  /**
   * TopBar显示和隐藏的动画效果
   * 
   * @author IT01
   */
  private class MyTopBarAnimationTask extends
      AsyncTask<Integer, Integer, Integer>
  {

    MarginLayoutParams layoutParams;

    @Override
    protected Integer doInBackground(Integer... params)
    {
      int speed = (params[0] == SHOW ? 20 : -20);
      layoutParams = (MarginLayoutParams) mTopBar.getLayoutParams();
      int topMargin = layoutParams.topMargin;
      int height = mTopBar.getHeight();
      while(true)
      {
        topMargin += speed;
        if(params[0] == SHOW)
        {
          if(topMargin >= topOriginalMargin)
          {
            topMargin = topOriginalMargin;
            break;
          }
        }
        else
        {
          if(topMargin <= -height)
          {
            topMargin = -height;
            break;
          }
        }

        publishProgress(topMargin);

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
      return topMargin;
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(Integer result)
    {
      layoutParams.topMargin = result;
      mTopBar.setLayoutParams(layoutParams);
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Integer... values)
    {
      layoutParams.topMargin = values[0];
      mTopBar.setLayoutParams(layoutParams);
    }

  }

  /**
   * BottomBar显示和隐藏的动画效果
   * 
   * @author IT01
   */
  private class MyBottomBarAnimationTask extends
      AsyncTask<Integer, Integer, Integer>
  {

    MarginLayoutParams layoutParams;

    @Override
    protected Integer doInBackground(Integer... params)
    {
      int speed = (params[0] == SHOW ? 20 : -20);
      layoutParams = (MarginLayoutParams) mBottomBar.getLayoutParams();
      int bottomMargin = layoutParams.bottomMargin;
      int height = mBottomBar.getHeight();
      while(true)
      {
        bottomMargin += speed;
        if(params[0] == SHOW)
        {
          if(bottomMargin >= bottomOriginalMargin)
          {
            bottomMargin = bottomOriginalMargin;
            break;
          }
        }
        else
        {
          if(bottomMargin <= -height)
          {
            bottomMargin = -height;
            break;
          }
        }

        publishProgress(bottomMargin);

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
      return bottomMargin;
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(Integer result)
    {
      layoutParams.bottomMargin = result;
      mBottomBar.setLayoutParams(layoutParams);
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Integer... values)
    {
      layoutParams.bottomMargin = values[0];
      mBottomBar.setLayoutParams(layoutParams);
    }

  }

  /*
   * (non-Javadoc)
   * @see android.support.v4.app.FragmentActivity#onBackPressed()
   */
  @Override
  public void onBackPressed()
  {
    // 从文章返回时，显示TopBar和BottomBar
    new MyTopBarAnimationTask().execute(SHOW);
    new MyBottomBarAnimationTask().execute(SHOW);

    // 如果mainContentFragment没有子Fragment可以出栈，执行父类的返回按钮事件
    boolean canHandle = mainContentFragment.popBack();
    if(!canHandle)
    {
      super.onBackPressed();
    }
  }

  /*
   * (non-Javadoc)
   * @see android.app.Activity#dispatchTouchEvent(android.view.MotionEvent)
   */
  @Override
  public boolean dispatchTouchEvent(MotionEvent event)
  {
    switch(event.getAction())
    {
      case MotionEvent.ACTION_DOWN:
        pointX = event.getRawX();
        pointY = event.getRawY();
        lastX = pointX;
        lastY = pointY;
        break;
      case MotionEvent.ACTION_UP:
        break;
      case MotionEvent.ACTION_MOVE:

        float vector = event.getRawX() - lastX;
        if(Math.abs(vector) < touchSlop)
        {
          return super.dispatchTouchEvent(event);
        }

        switch(slidingState)
        {
          case SLIDING_FROM_LEFT_TO_RIGHT:
            if(vector < 0)
            {
              pointX = lastX;
              pointY = lastY;
              slidingState = SLIDING_FROM_RIGHT_TO_LEFT;
            }
            break;
          case SLIDING_FROM_RIGHT_TO_LEFT:
            if(vector > 0)
            {
              pointX = lastX;
              pointY = lastY;
              slidingState = SLIDING_FROM_LEFT_TO_RIGHT;
            }
            break;
          case SLIDING_NONE:
            if(vector > 0)
            {
              slidingState = SLIDING_FROM_LEFT_TO_RIGHT;
            }
            else
            {
              slidingState = SLIDING_FROM_RIGHT_TO_LEFT;
            }
            break;
        }

        lastX = event.getRawX();
        lastY = event.getRawY();

        break;
    }

    return super.dispatchTouchEvent(event);
  }

  public int getTopBarOriginalTopMargin()
  {
    return topOriginalMargin;
  }

  public int getBottomBarOriginalBottomMargin()
  {
    return bottomOriginalMargin;
  }
}
