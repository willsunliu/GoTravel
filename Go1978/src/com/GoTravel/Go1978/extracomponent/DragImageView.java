package com.GoTravel.Go1978.extracomponent;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;


public class DragImageView extends ImageView
{
  Activity myActivity;
  MyAsyncTask myAsyncTask;
  int bitmapW, bitmapH;
  int maxW, maxH;
  int minW, minH;
  int screenW, screenH;
  int startTop = -1, startLeft = -1, startBottom = -1, startRight = -1;
  int startX, startY, currentX, currentY;
  float beforeLength, afterLength;
  float scaleTemp;
  int currentTop, currentBottom, currentLeft, currentRight;
  boolean isControlVertical, isControlHorizontal, isScaleAnim;

  private enum MODE
  {
    NONE, DRAG, ZOOM
  };

  private MODE mode = MODE.NONE;

  public DragImageView(Context context)
  {
    super(context);
    // TODO Auto-generated constructor stub
  }

  public DragImageView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    // setImageBitmap(BitmapFactory
    // .decodeFile("/storage/sdcard0/DCIM/Camera/20131227_194729.jpg"));
  }

  public DragImageView(Context context, AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void setImageBitmap(Bitmap bm)
  {
    super.setImageBitmap(bm);
    // 获取图片宽高
    bitmapH = bm.getHeight();
    bitmapW = bm.getWidth();

    maxH = bitmapH * 3;
    maxW = bitmapW * 3;
    minH = bitmapH / 3;
    minW = bitmapW / 3;
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right,
      int bottom)
  {
    super.onLayout(changed, left, top, right, bottom);
    if(startTop == -1)
    {
      startTop = top;
      startBottom = bottom;
      startLeft = left;
      startRight = right;
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    switch(event.getAction() & MotionEvent.ACTION_MASK)
    {
      case MotionEvent.ACTION_DOWN:
        onTouchDown(event);
        break;
      case MotionEvent.ACTION_POINTER_DOWN:
        onPointerDown(event);
        break;
      case MotionEvent.ACTION_POINTER_UP:
        mode = MODE.NONE;
        if(isScaleAnim)
        {
          doScaleAnim();
        }
        break;
      case MotionEvent.ACTION_UP:
        mode = MODE.NONE;
        break;
      case MotionEvent.ACTION_MOVE:
        onTouchMove(event);
        break;

      default:
        break;
    }
    return true;
  }

  private void doScaleAnim()
  {
    myAsyncTask = new MyAsyncTask(screenW, this.getWidth(), this.getHeight());
    myAsyncTask.setLTRB(this.getLeft(), this.getTop(), this.getRight(),
        this.getBottom());
    myAsyncTask.execute();
    isScaleAnim = false;
  }

  private float getDistance(MotionEvent event)
  {
    float x = event.getX(0) - event.getX(1);
    float y = event.getY(0) - event.getY(1);

    return (float) Math.sqrt(x * x + y * y);
  }

  private void setScale(float scale)
  {
    int disX = (int) (this.getWidth() * Math.abs(1 - scale)) / 4;
    int disY = (int) (this.getHeight() * Math.abs(1 - scale)) / 4;

    if(scale > 1 && this.getWidth() < maxW)
    {
      currentLeft = getLeft() - disX;
      currentRight = getRight() + disX;
      currentTop = getTop() - disY;
      currentBottom = getBottom() + disY;

      setFrame(currentLeft, currentTop, currentRight, currentBottom);
      if(currentTop <= 0 && currentBottom >= screenH)
      {
        isControlVertical = true;
      }
      else
      {
        isControlVertical = false;
      }
      if(currentLeft <= 0 && currentRight >= screenW)
      {
        isControlHorizontal = true;
      }
      else
      {
        isControlHorizontal = false;
      }
    }
    else if(scale < 1 && this.getWidth() >= minW)
    {
      currentLeft = this.getLeft() + disX;
      currentRight = this.getRight() - disX;
      currentTop = this.getTop() + disY;
      currentBottom = this.getBottom() - disY;

      // 上边越界
      if(isControlVertical && currentTop > 0)
      {
        currentTop = 0;
        currentBottom = this.getBottom() - 2 * disY;
        if(currentBottom < screenH)
        {
          currentBottom = screenH;
          isControlVertical = false;
        }
      }

      // 下边越界
      if(isControlVertical && currentBottom < screenH)
      {
        currentBottom = screenH;
        currentTop = this.getTop() + 2 * disY;
        if(currentTop > 0)
        {
          currentTop = 0;
          isControlVertical = false;
        }
      }

      // 左边越界
      if(isControlHorizontal && currentLeft >= 0)
      {
        currentLeft = 0;
        currentRight = this.getRight() - 2 * disX;
        if(currentRight <= screenW)
        {
          currentRight = screenW;
          isControlHorizontal = false;
        }
      }

      // 右边越界
      if(isControlHorizontal && currentRight <= screenW)
      {
        currentRight = screenW;
        currentLeft = this.getLeft() + 2 * disX;
        if(currentLeft >= 0)
        {
          currentLeft = 0;
          isControlHorizontal = false;
        }
      }

      if(isControlHorizontal || isControlVertical)
      {
        this.setFrame(currentLeft, currentTop, currentRight, currentBottom);
      }
      else
      {
        this.setFrame(currentLeft, currentTop, currentRight, currentBottom);
        isScaleAnim = true;
      }
    }
  }

  private void onTouchDown(MotionEvent event)
  {
    mode = MODE.DRAG;

    currentX = (int) event.getRawX();
    currentY = (int) event.getRawY();

    startX = (int) event.getX();
    startY = currentY - getTop();
  }

  private void onPointerDown(MotionEvent event)
  {
    if(event.getPointerCount() == 2)
    {
      mode = MODE.ZOOM;

      beforeLength = getDistance(event);
    }
  }

  private void onTouchMove(MotionEvent event)
  {
    int left = 0, top = 0, right = 0, bottom = 0;
    if(mode == MODE.DRAG)
    {
      left = currentX - startX;
      right = currentX + this.getWidth() - startX;
      top = currentY - startY;
      bottom = currentY - startY + this.getHeight();

      if(isControlHorizontal)
      {
        if(left >= 0)
        {
          left = 0;
          right = this.getWidth();
        }
        if(right <= screenW)
        {
          left = screenW - this.getWidth();
          right = screenW;
        }
      }
      else
      {
        left = this.getLeft();
        right = this.getRight();
      }

      if(isControlVertical)
      {
        if(top >= 0)
        {
          top = 0;
          bottom = this.getHeight();
        }

        if(bottom <= screenH)
        {
          top = screenH - this.getHeight();
          bottom = screenH;
        }
      }
      else
      {
        top = this.getTop();
        bottom = this.getBottom();
      }

      if(isControlHorizontal || isControlVertical)
      {
        this.setPosition(left, top, right, bottom);
      }

      currentX = (int) event.getRawX();
      currentY = (int) event.getRawY();
    }
    if(mode == MODE.ZOOM)
    {
      afterLength = getDistance(event);
      float deltaLength = afterLength - beforeLength;

      if(Math.abs(deltaLength) > 5f)
      {
        scaleTemp = afterLength / beforeLength;
        setScale(scaleTemp);
        beforeLength = afterLength;
      }
    }
  }

  class MyAsyncTask extends AsyncTask<Void, Integer, Void>
  {

    private int screenW, currentW, currentH;
    private int left, top, right, bottom;
    private float scale_WH;

    public void setLTRB(int left, int top, int right, int bottom)
    {
      this.left = left;
      this.top = top;
      this.right = right;
      this.bottom = bottom;
    }

    private float STEP = 5f;
    private float stepHorizontal, stepVertical;

    public MyAsyncTask(int screenW, int currentW, int currentH)
    {
      super();
      this.screenW = screenW;
      this.currentW = currentW;
      this.currentH = currentH;
      scale_WH = (float) currentH / currentW;
      stepHorizontal = STEP;
      stepVertical = scale_WH * STEP;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
      while(currentW <= screenW)
      {
        left -= stepHorizontal;
        top -= stepVertical;
        right += stepHorizontal;
        bottom += stepVertical;

        currentW += 2 * stepHorizontal;

        left = Math.max(left, startLeft);
        top = Math.max(top, startTop);
        right = Math.min(right, startRight);
        bottom = Math.min(bottom, startBottom);

        onProgressUpdate(new Integer[] {left, top, right, bottom});
        try
        {
          Thread.sleep(10);
        }
        catch(InterruptedException exception)
        {
          exception.printStackTrace();
        }
      }
      return null;
    }

    @Override
    protected void onProgressUpdate(final Integer... values)
    {
      super.onProgressUpdate(values);
      myActivity.runOnUiThread(new Runnable()
      {

        public void run()
        {
          setFrame(values[0], values[1], values[2], values[3]);
        }
      });
    }

  }

  public void setScreenW(int screenW)
  {
    this.screenW = screenW;
  }

  public void setScreenH(int screenH)
  {
    this.screenH = screenH;
  }

  private void setPosition(int left, int top, int right, int bottom)
  {
    this.layout(left, top, right, bottom);
  }

  public void setActivity(Activity activity)
  {
    this.myActivity = activity;
  }
}
