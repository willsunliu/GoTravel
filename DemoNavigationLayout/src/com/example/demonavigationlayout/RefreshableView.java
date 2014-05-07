package com.example.demonavigationlayout;


import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.RotateAnimation;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


public class RefreshableView extends LinearLayout implements OnTouchListener
{

  private static final String TAG = "RefreshableView";

  // 下拉状态
  public static final int STATUS_PULL_TO_REFRESH = 0;
  // 释放立即刷新状态
  public static final int STATUS_RELEASE_TO_REFRESH = 1;
  // 正在刷新状态
  public static final int STATUS_REFRESHING = 2;
  // 刷新完成或未刷新状态
  public static final int STATUS_REFRESH_FINISHED = 3;
  // 下拉头部回滚的速度
  public static final int SCROLL_SPEED = -20;
  // 一分钟的毫秒值，用于判断上次的更新时间
  public static final int ONE_MINUTE = 60 * 1000;
  // 一小时的毫秒值，用于判断上次的更新时间
  public static final int ONE_HOUR = 60 * ONE_MINUTE;
  // 一天的毫秒值，用于判断上次的更新时间
  public static final int ONE_DAY = 24 * ONE_HOUR;
  // 一月的毫秒值，用于判断上次的更新时间
  public static final int ONE_MONTH = 30 * ONE_DAY;
  // 一年的毫秒值，用于判断上次的更新时间
  public static final int ONE_YEAR = 12 * ONE_MONTH;
  // 上次更新时间的字符串常量，用于作为SharedPreferences的键值
  public static final String UPDATED_AT_STRING = "updated_at";
  // 下拉刷新的回调接口
  private PullToRefreshListener mListener;
  // 用于存储上次更新时间
  private SharedPreferences preferences;
  // 下拉头的View
  private View header;
  // 需要去下拉刷新的View
  private View content;
  // 刷新时显示的进度条
  private ProgressBar myProgressBar;
  // 指示下拉和释放的箭头
  private ImageView arrow;
  // 指示下拉和释放的文字描述
  // 下拉头的布局参数
  private MarginLayoutParams headerLayoutParams;
  // 上次更新时间的毫秒值
  // 为了防止不同界面的下拉刷新在上次更新时间上互相有冲突，使用id来做区分
  private int mId = -1;
  // 下拉头的高度
  private int hideHeaderHeight;
  /*
   * 当前处理什么状态，可选值有STATUS_PULL_TO_REFRESH, STATUS_RELEASE_TO_REFRESH,
   * STATUS_REFRESHING 和 STATUS_REFRESH_FINISHED
   */
  private int currentStatus = STATUS_REFRESH_FINISHED;
  // 记录上一次的状态是什么，避免进行重复操作
  private int lastStatus = currentStatus;
  // 手指按下时的屏幕纵坐标
  private float yDown;
  // 在被判断为滚动之前用户手指可以移动的最大值
  private int touchSlop;
  // 是否已加载过以此layout，这里onLayout中的初始化只需要一次
  private boolean loadOnce;

  // 当前是否可以下拉，只有View滚动到头的时候才可以下拉
  //

  /**
   * 构造函数，在运行时动态添加一个下拉头的布局
   * 
   * @param context
   * @param attrs
   */
  public RefreshableView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    // TODO Auto-generated constructor stub
    header =
        LayoutInflater.from(context).inflate(R.layout.pull_to_refresh, null,
            true);
    myProgressBar = (ProgressBar) header.findViewById(R.id.progress_bar);
    arrow = (ImageView) header.findViewById(R.id.arrow);
    touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    setOrientation(VERTICAL);
    addView(header, 0);
  }

  /**
   * 通过向上偏移将header隐藏，给显示的内容注册touch事件
   */
  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b)
  {
    // TODO Auto-generated method stub
    super.onLayout(changed, l, t, r, b);
    if(changed && !loadOnce)
    {
      hideHeaderHeight = -header.getHeight();
      headerLayoutParams = (MarginLayoutParams) header.getLayoutParams();
      Log.i(TAG, "hideHeaderHeight=" + hideHeaderHeight);
      headerLayoutParams.topMargin = hideHeaderHeight;
      content = getChildAt(1);
      content.setOnTouchListener(this);
      loadOnce = true;
    }
  }

  @Override
  public boolean onTouch(View v, MotionEvent event)
  {
    switch(event.getAction())
    {
      case MotionEvent.ACTION_DOWN:
        Log.i(TAG, "down");
        yDown = event.getRawY();
        break;
      case MotionEvent.ACTION_MOVE:
        Log.i(TAG, "move");
        int distance = (int) (event.getRawY() - yDown);
        if(distance < touchSlop)
        {
          return false;
        }
        if(distance <= 0 && headerLayoutParams.topMargin <= hideHeaderHeight)
        {
          return false;
        }
        if(currentStatus != STATUS_REFRESHING)
        {
          if(headerLayoutParams.topMargin > 0)
          {
            currentStatus = STATUS_RELEASE_TO_REFRESH;
          }
          else
          {
            currentStatus = STATUS_PULL_TO_REFRESH;
          }
          headerLayoutParams.topMargin = (distance / 2) + hideHeaderHeight;
          header.setLayoutParams(headerLayoutParams);
        }
        break;
      case MotionEvent.ACTION_UP:
      default:
        Log.i(TAG, "up");
        if(currentStatus == STATUS_RELEASE_TO_REFRESH)
        {
          new RefreshingTask().execute();
        }
        else if(currentStatus == STATUS_PULL_TO_REFRESH)
        {
          new HideHeaderTask().execute();
        }
        // 更新下拉头中的信息
        if(currentStatus == STATUS_RELEASE_TO_REFRESH
            || currentStatus == STATUS_PULL_TO_REFRESH)
        {
          return true;
        }
        break;
    }
    if(currentStatus == STATUS_PULL_TO_REFRESH
        || currentStatus == STATUS_RELEASE_TO_REFRESH)
    {
      updateHeaderView();
      lastStatus = currentStatus;
    }
    return true;
  }

  private void updateHeaderView()
  {
    if(lastStatus != currentStatus)
    {
      Log.i(TAG, "updateHeaderView");
      if(currentStatus == STATUS_PULL_TO_REFRESH)
      {
        Log.i(TAG, "pull to refresh");
        arrow.setVisibility(View.VISIBLE);
        myProgressBar.setVisibility(View.GONE);
        rotateArrow();
      }
      else if(currentStatus == STATUS_RELEASE_TO_REFRESH)
      {
        Log.i(TAG, "release to refresh");
        arrow.setVisibility(View.VISIBLE);
        myProgressBar.setVisibility(View.GONE);
        rotateArrow();
      }
      else if(currentStatus == STATUS_REFRESHING)
      {
        Log.i(TAG, "refreshing");
        myProgressBar.setVisibility(View.VISIBLE);
        arrow.clearAnimation();
        arrow.setVisibility(View.GONE);
      }
    }
  }

  private void rotateArrow()
  {
    float pivotX = arrow.getWidth() * 0.5f;
    float pivotY = arrow.getHeight() * 0.5f;
    float fromDegrees = 0f;
    float toDegrees = 0f;
    if(currentStatus == STATUS_PULL_TO_REFRESH)
    {
      fromDegrees = 180f;
      toDegrees = 360f;
    }
    else if(currentStatus == STATUS_RELEASE_TO_REFRESH)
    {
      fromDegrees = 0f;
      toDegrees = 180f;
    }
    RotateAnimation animation =
        new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
    animation.setDuration(100);
    animation.setFillAfter(true);
    arrow.startAnimation(animation);
  }

  public void finishRefreshing()
  {
    currentStatus = STATUS_REFRESH_FINISHED;
    new HideHeaderTask().execute();
  }

  class RefreshingTask extends AsyncTask<Void, Integer, Void>
  {

    @Override
    protected Void doInBackground(Void... params)
    {
      int topMargin = headerLayoutParams.topMargin;
      while(true)
      {
        topMargin += SCROLL_SPEED;
        if(topMargin <= 0)
        {
          topMargin = 0;
          break;
        }
        publishProgress(topMargin);
        sleep(10);
      }
      currentStatus = STATUS_REFRESHING;
      publishProgress(0);
      if(mListener != null)
      {
        mListener.onRefresh();
      }
      return null;
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Integer... topMargin)
    {
      updateHeaderView();
      headerLayoutParams.topMargin = topMargin[0];
      header.setLayoutParams(headerLayoutParams);
    }

  }

  class HideHeaderTask extends AsyncTask<Void, Integer, Integer>
  {

    @Override
    protected Integer doInBackground(Void... params)
    {
      int topMargin = headerLayoutParams.topMargin;
      while(true)
      {
        topMargin += SCROLL_SPEED;
        if(topMargin <= hideHeaderHeight)
        {
          topMargin = hideHeaderHeight;
          break;
        }
        publishProgress(topMargin);
        sleep(10);
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
      headerLayoutParams.topMargin = result;
      header.setLayoutParams(headerLayoutParams);
      currentStatus = STATUS_REFRESH_FINISHED;
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Integer... topMargin)
    {
      headerLayoutParams.topMargin = topMargin[0];
      header.setLayoutParams(headerLayoutParams);
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
      e.printStackTrace();
    }
  }

  public interface PullToRefreshListener
  {
    /**
     * 刷新时回去回调此方法，在方法内编写具体的刷新逻辑。此方法在子线程运行，不必
     * 另开线程。
     */
    void onRefresh();
  }

  /**
   * 注册一个下拉刷新监听器
   * 
   * @param listener 监听器实例
   * @param id 为了防止不同界面的下拉刷新在上次更新时间上互相冲突，不同界面在注册时
   *          传入不同的id
   */
  public void setOnRefreshListener(PullToRefreshListener listener, int id)
  {
    mListener = listener;
    mId = id;
  }
}
