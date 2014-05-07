package com.GoTravel.Go1978.extracomponent;


import com.GoTravel.Go1978.log.MyLog;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class ZoomImageView extends View
{
  private static final String TAG = "ZoomImageView";
  // 正常状态常量
  public static final int STATUS_NORMAL = 0;
  // 初始化状态常量
  public static final int STATUS_INIT = 1;
  // // 图片放大状态常量
  // public static final int STATUS_ZOOM_OUT = 2;
  // // 图片缩小状态常量
  // public static final int STATUS_ZOOM_IN = 3;
  // 图片缩放状态常量
  public static final int STATUS_ZOOM = 2;
  // 图片拖动状态常量
  public static final int STATUS_MOVE = 4;
  // 用于对图片进行移动和缩放变换的矩阵
  private Matrix matrix = new Matrix();
  // 待展示的Bitmap对象
  private Bitmap sourceBitmap;
  // 记录当前操作的状态，可选值为STATUS_INIT, STATUS_ZOOM_OUT, STATUS_ZOOM_IN和STATUS_MOVE
  private int currentStatus;
  // ZoomImageView控件的宽度
  private int width;
  // ZoomImageView控件的高度
  private int height;
  // 记录两指同时放在屏幕上时，中心点的横坐标值
  private float centerPointX;
  // 记录两指同时放在屏幕上时，中心点的纵坐标值
  private float centerPointY;
  // 记录当前图片的宽度，图片被缩放时，这个值会一起变动
  private float currentBitmapWidth;
  // 记录当前图片的高度，图片被缩放时，这个值会一起变动
  private float currentBitmapHeight;
  // 记录上次手指移动时的横坐标
  private float lastXMove = -1;
  // 记录上次手指移动时的纵坐标
  private float lastYMove = -1;
  // 记录手指在横坐标方向上的移动距离
  private float movedDistanceX;
  // 记录手指在纵坐标方向上的移动距离
  private float movedDistanceY;
  // 记录图片在矩阵上的横向偏移值
  private float totalTranslateX = 0;
  // 记录图片在矩阵上的纵向偏移值
  private float totalTranslateY = 0;
  // 记录图片在矩阵上的上一个横向偏移值
  private float lastTotalTranslateX = 0;
  // 记录图片在矩阵上的上一个纵向偏移值
  private float lastTotalTranslateY = 0;
  // 记录图片在矩阵上的总缩放比例
  private float totalRatio;
  // 记录手指移动的距离所造成的缩放比例
  private float scaledRatio;
  // 记录图片初始化时的缩放比例
  private float initRatio;
  // 记录上次两指之间的距离
  private double lastFingerDis;

  private PhotoChangedListener photoChangedListener = null;

  /**
   * 构造函数，将当前操作状态设为STATUS_INIT
   * 
   * @param context
   */
  public ZoomImageView(Context context)
  {
    super(context);
    currentStatus = STATUS_INIT;
  }

  /**
   * 构造函数，将当前操作状态设为STATUS_INIT
   * 
   * @param context
   * @param attrs
   */
  public ZoomImageView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    currentStatus = STATUS_INIT;
  }

  /**
   * 将待展示的图片设置进来
   * 
   * @param bitmap 待展示的Bitmap
   */
  public void setImageBitmap(Bitmap bitmap)
  {
    sourceBitmap = bitmap;
    invalidate();
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right,
      int bottom)
  {
    super.onLayout(changed, left, top, right, bottom);
    if(changed)
    {
      width = getWidth();
      height = getHeight();
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    switch(event.getActionMasked())
    {
      case MotionEvent.ACTION_DOWN:
        MyLog.i(TAG, "action down");
        currentStatus = STATUS_MOVE;
        break;
      case MotionEvent.ACTION_POINTER_DOWN:
        MyLog.i(TAG, "action pointer down");
        if(event.getPointerCount() == 2)
        {
          currentStatus = STATUS_ZOOM;
          // 当两指在屏幕上时，计算两指之间的距离
          lastFingerDis = distanceBetweenFingers(event);
        }
        break;

      case MotionEvent.ACTION_MOVE:
//        MyLog.i(TAG, "action move");
        if(event.getPointerCount() == 1 && currentStatus == STATUS_MOVE)
        {
          // 只有单指在屏幕上，为拖动状态
          float xMove = event.getX();
          float yMove = event.getY();
          if(lastXMove == -1 && lastYMove == -1)
          {
            lastXMove = xMove;
            lastYMove = yMove;
          }
          // currentStatus = STATUS_MOVE;
          movedDistanceX = xMove - lastXMove;
          movedDistanceY = yMove - lastYMove;

          // 进行边界检查，不允许将图片拖出边界
          if(totalTranslateX + movedDistanceX > 0)
          {
            movedDistanceX = 0;
          }
          else if(width - (totalTranslateX + movedDistanceX) > currentBitmapWidth)
          {
            movedDistanceX = 0;
          }
          if(totalTranslateY + movedDistanceY > 0)
          {
            movedDistanceY = 0;
          }
          else if(height - (totalTranslateY + movedDistanceY) > currentBitmapHeight)
          {
            movedDistanceY = 0;
          }

//          MyLog.i(TAG, "totalTranslateX=" + totalTranslateX);
//          MyLog.i(TAG, "totalTranslateY=" + totalTranslateY);
//          MyLog.i(TAG, "movedDistanceX=" + movedDistanceX);
//          MyLog.i(TAG, "movedDistanceY=" + movedDistanceY);
          invalidate();

          lastXMove = xMove;
          lastYMove = yMove;
        }
        else if(event.getPointerCount() == 2)
        {
          // 有两个手指在屏幕上，为缩放状态
          // centerPointBetweenFingers(event);
          double fingerDis = distanceBetweenFingers(event);
          // if(fingerDis > lastFingerDis)
          // {
          // currentStatus = STATUS_ZOOM_OUT;
          // }
          // else
          // {
          // currentStatus = STATUS_ZOOM_IN;
          // }

          // 进行缩放倍数检查，最大只允许将图片放大4倍，最小可以缩小到初始化比例
          if(currentStatus == STATUS_ZOOM
              && (getTotalRatio() < 4 * initRatio || getTotalRatio() > initRatio))
          // if((currentStatus == STATUS_ZOOM_OUT && totalRatio < 4 * initRatio)
          // || (currentStatus == STATUS_ZOOM_IN && totalRatio > initRatio))
          {
            scaledRatio = (float) (fingerDis / lastFingerDis);
            setTotalRatio(getTotalRatio() * scaledRatio);
            if(getTotalRatio() > 4 * initRatio)
            {
              setTotalRatio(4 * initRatio);
            }
            else if(getTotalRatio() < initRatio)
            {
              setTotalRatio(initRatio);
            }

            invalidate();
            lastFingerDis = fingerDis;
          }
        }
        break;

      case MotionEvent.ACTION_POINTER_UP:
        MyLog.i(TAG, "action pointer up");
        if(event.getPointerCount() == 2)
        {
          currentStatus = STATUS_NORMAL;
          // 将临时值还原
          lastXMove = -1;
          lastYMove = -1;
        }
        break;

      case MotionEvent.ACTION_UP:
        MyLog.i(TAG, "action up");
        currentStatus = STATUS_NORMAL;
        // 将临时值还原
        lastXMove = -1;
        lastYMove = -1;
        break;

      default:
        break;
    }
    return true;
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    canvas.drawColor(Color.GRAY);
    switch(currentStatus)
    {
    // case STATUS_ZOOM_OUT:
    // case STATUS_ZOOM_IN:
      case STATUS_ZOOM:
        zoom(canvas);
        break;

      case STATUS_MOVE:
//        MyLog.i(TAG, "STATUS_MOVE draw");
        move(canvas);
        break;

      case STATUS_INIT:
        initBitmap(canvas);

      default:
        canvas.drawBitmap(sourceBitmap, matrix, null);
        break;
    }
  }

  /**
   * 对图片进行初始化操作，包括让图片居中，以及当图片大于屏幕宽度时对图片进行压缩
   * 
   * @param canvas
   */
  private void initBitmap(Canvas canvas)
  {
    if(sourceBitmap != null)
    {
      matrix.reset();

      float ratio = 1.0f;
      int bitmapWidth = sourceBitmap.getWidth();
      int bitmapHeight = sourceBitmap.getHeight();
      currentBitmapWidth = bitmapWidth;
      currentBitmapHeight = bitmapHeight;

      // MyLog.i(TAG, "bitmapWidth=" + bitmapWidth);
      // MyLog.i(TAG, "bitmapHeight=" + bitmapHeight);
      // MyLog.i(TAG, "width=" + width);
      // MyLog.i(TAG, "height=" + height);
      if(bitmapWidth > width || bitmapHeight > height)
      {
        if(bitmapWidth / (width * 0.1f) > bitmapHeight / (height * 0.1f))
        {
          // 当图片宽度大于屏幕宽度时，将图片等比例压缩，使它可以完全显示
          ratio = width / (bitmapWidth * 1.0f);
          // MyLog.i(TAG, "ratio=" + ratio);
          matrix.postScale(ratio, ratio);
          // float translateY = (height - (bitmapHeight * ratio)) / 2f;
          // 在纵坐标方向上进行偏移，以保证图片居中显示
          // matrix.postTranslate(0, translateY);
          // totalTranslateY = translateY;
          setTotalRatio(initRatio = ratio);
        }
        else
        {
          // 当图片高度大于屏幕高度时，将图片等比例压缩，使它可以完全显示
          ratio = height / (bitmapHeight * 1.0f);
          // MyLog.i(TAG, "ratio=" + ratio);
          matrix.postScale(ratio, ratio);
          // float translateX = (width - (bitmapWidth * ratio)) / 2f;
          // 在横坐标方向上进行偏移，以保证图片居中显示
          // matrix.postTranslate(translateX, 0);
          // totalTranslateX = translateX;
          setTotalRatio(initRatio = ratio);
        }

        currentBitmapWidth = bitmapWidth * initRatio;
        currentBitmapHeight = bitmapHeight * initRatio;
      }
      else
      {
        // 当图片的宽高都小于屏幕时，直接让图片居中显示
        // float translateX = (width - sourceBitmap.getWidth()) / 2f;
        // float translateY = (height - sourceBitmap.getHeight()) / 2f;
        // matrix.postTranslate(translateX, translateY);
        // totalTranslateX = translateX;
        // totalTranslateY = translateY;
        setTotalRatio(initRatio = 1f);
        currentBitmapWidth = bitmapWidth;
        currentBitmapHeight = bitmapHeight;
      }
      canvas.drawBitmap(sourceBitmap, matrix, null);
    }
  }

  /**
   * 对图片进行平移处理
   * 
   * @param canvas
   */
  private void move(Canvas canvas)
  {
    matrix.reset();

    // 根据手指移动的距离计算出总偏移值
    float translateX = totalTranslateX + movedDistanceX;
    float translateY = totalTranslateY + movedDistanceY;

    // 先按照已有的缩放比例对图片进行缩放
    matrix.postScale(getTotalRatio(), getTotalRatio());

    // 再根据移动距离进行偏移
    matrix.postTranslate(translateX, translateY);
    totalTranslateX = translateX;
    totalTranslateY = translateY;
    if(photoChangedListener != null)
    {
      photoChangedListener.photoTranslate(movedDistanceX, movedDistanceY);
    }
    canvas.drawBitmap(sourceBitmap, matrix, null);
  }

  /**
   * 对图片进行缩放处理
   * 
   * @param canvas
   */
  private void zoom(Canvas canvas)
  {
    matrix.reset();

    // 将图片按总缩放比例进行缩放
    matrix.postScale(getTotalRatio(), getTotalRatio());
    float scaledWidth = sourceBitmap.getWidth() * getTotalRatio();
    float scaledHeight = sourceBitmap.getHeight() * getTotalRatio();
    float translateX = 0f;
    float translateY = 0f;

    // 将缩放的比例通过PhotoChangedListener传递出去供其它view进行缩放
    if(photoChangedListener != null)
    {
      float pixelRatioW = scaledWidth / currentBitmapWidth;
      float pixelRatioH = scaledHeight / currentBitmapHeight;
      photoChangedListener.photoZoom(getTotalRatio(), pixelRatioW, pixelRatioH);
    }

    /*
     * 如果当前图片宽度小于屏幕宽度，则按屏幕中心的横坐标进行水平缩放。
     * 否则按两指的中心点的横坐标进行水平缩放
     */
    if(currentBitmapWidth < width)
    {
      // translateX = (width - scaledWidth) / 2f;
      translateX = 0;
    }
    else
    {
      translateX =
      // totalTranslateX * scaledRatio + centerPointX * (1 - scaledRatio);
          totalTranslateX * scaledRatio;

      // 进行边界检查，保证图片缩放后在水平方向上不会偏移出屏幕
      if(translateX > 0)
      {
        translateX = 0;
      }
      else if(width - translateX > scaledWidth)
      {
        translateX = width - scaledWidth;
      }
//      MyLog.i(TAG, "translateX=" + translateX);
    }

    /*
     * 如果当前图片高度小于屏幕高度，则按屏幕中心的纵坐标进行垂直缩放。
     * 否则按两指的中心点的纵坐标进行垂直缩放
     */
    if(currentBitmapHeight < height)
    {
      // translateY = (height - scaledHeight) / 2f;
      translateY = 0;
    }
    else
    {
      translateY =
      // totalTranslateY * scaledRatio + centerPointY * (1 - scaledRatio);
          totalTranslateY * scaledRatio;

//      MyLog.i(TAG, "scaledHeight=" + scaledHeight);
//      MyLog.i(TAG, "translateY=" + translateY);
      // 进行边界检查，保证图片缩放后在垂直方向上不会偏移出屏幕
      if(translateY > 0)
      {
        translateY = 0;
      }
      else if(height - translateY > scaledHeight)
      {
//        MyLog.i(TAG, "scaledHeight=" + scaledHeight);
         translateY = height - scaledHeight;
      }
//      MyLog.i(TAG, "translateY=" + translateY);
    }

    // 缩放后对图片进行偏移，以保证缩放后中心点位置不变
    matrix.postTranslate(translateX, translateY);
    lastTotalTranslateX = totalTranslateX;
    lastTotalTranslateY = totalTranslateY;
    totalTranslateX = translateX;
    totalTranslateY = translateY;
    MyLog.i(TAG, "dtx="+(totalTranslateX - lastTotalTranslateX)+", dty="+(totalTranslateY-lastTotalTranslateY));
//    photoChangedListener.photoTranslate(totalTranslateX - lastTotalTranslateX,
//        totalTranslateY - lastTotalTranslateY);
    currentBitmapWidth = scaledWidth;
    currentBitmapHeight = scaledHeight;
    canvas.drawBitmap(sourceBitmap, matrix, null);
  }

  /**
   * 计算两个手指之间中心点的坐标
   * 
   * @param event
   */
  private void centerPointBetweenFingers(MotionEvent event)
  {
    float xPoint0 = event.getX(0);
    float yPoint0 = event.getY(0);
    float xPoint1 = event.getX(1);
    float yPoint1 = event.getY(1);
    centerPointX = (xPoint0 + xPoint1) / 2;
    centerPointY = (yPoint0 + yPoint1) / 2;
  }

  /**
   * 计算两个手指之间的距离
   * 
   * @param event
   * @return 两个手指之间的距离
   */
  private double distanceBetweenFingers(MotionEvent event)
  {
    float disX = Math.abs(event.getX(0) - event.getX(1));
    float disY = Math.abs(event.getY(0) - event.getY(1));
    return Math.sqrt(disX * disX + disY * disY);
  }

  /**
   * @return the totalRatio
   */
  public float getTotalRatio()
  {
    return totalRatio;
  }

  /**
   * @param totalRatio the totalRatio to set
   */
  public void setTotalRatio(float totalRatio)
  {
    this.totalRatio = totalRatio;
  }

  public void setPhotoZoomListener(PhotoChangedListener photoChangedListener)
  {
    this.photoChangedListener = photoChangedListener;
  }

  public interface PhotoChangedListener
  {
    void photoZoom(float photoRatio, float pixelRatioW, float pixelRatioH);

    void photoTranslate(float moveX, float moveY);
  }
}
