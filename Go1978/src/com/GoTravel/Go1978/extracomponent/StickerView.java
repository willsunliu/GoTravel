package com.GoTravel.Go1978.extracomponent;


import java.io.IOException;
import java.io.InputStream;

import com.GoTravel.Go1978.R;
import com.GoTravel.Go1978.log.MyLog;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.method.MovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;


public class StickerView extends View
{
  private static final String TAG = "StickerView";
  private static final int RADIUS = 24;

  private static final int WIDTH = 0;
  private static final int HEIGHT = 1;
  int[] measureSpecs;

  private Context context;

  // picture
  Bitmap sticker;
  Bitmap delete;
  Bitmap rotate;
  Paint paint;

  FrameLayout.LayoutParams params;

  // 正常状态常量
  public static final int STATUS_NORMAL = 0;
  // 初始化状态常量
  public static final int STATUS_INIT = 1;
  // 记录当前操作的状态，可选值为STATUS_INIT, STATUS_ZOOM_OUT, STATUS_ZOOM_IN和STATUS_MOVE
  private int currentStatus;

  // view x
  private int locationX;
  // view y
  private int locationY;
  private float scaling = 1f;
  // sticker当前的缩放比例
  private float currentRatio = 1f;
  // 被编辑图片的缩放比例
  private float zoomRatio = 1f;

  private int downViewX, downViewY;
  private int downRawX, downRawY;
  private int lastViewX, lastViewY;
  private int lastRawX, lastRawY;
  private float rotateDegree;
  private int curDegree;
  private int lastDegree;
  private float deltaDegree;
  // 旋转按钮和取消按钮之间的夹角
  private int rcDegree;
  private int distance;
  private double lastDistance;
  private int lastBmpWidth, lastBmpHeight;
  private int centerX, centerY;

  double lastTheata = 0;

  private Rect cancelRect, rotateRect;
  private Point cancelPoint, rotatePoint;
  boolean isCancel = false;
  boolean isRotate = false;
  boolean isMove = false;
  boolean isSelected = false;

  public StickerView(Context context, float scaling, float zoomRatio)
  {
    super(context);
    this.context = context;
    this.scaling = scaling;
    this.zoomRatio = zoomRatio;

    MyLog.i(TAG, "scaling=" + scaling);
    MyLog.i(TAG, "zoomRatio=" + zoomRatio);

    currentStatus = STATUS_INIT;

    sticker = getSrcSticker();
    sticker =
        Bitmap.createScaledBitmap(sticker,
            (int) (sticker.getWidth() * scaling * zoomRatio),
            (int) (sticker.getHeight() * scaling * zoomRatio), true);

    lastBmpWidth = sticker.getWidth();
    lastBmpHeight = sticker.getHeight();
    MyLog.i(TAG, "lastBmpWidth=" + lastBmpWidth);
    MyLog.i(TAG, "lastBmpHeight=" + lastBmpHeight);
    // measureSpecs = getMeasureSpec();
    // measure(measureSpecs[WIDTH], measureSpecs[HEIGHT]);

    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setColor(Color.YELLOW);

    cancelRect = new Rect();
    cancelPoint = new Point();
    rotateRect = new Rect();
    rotatePoint = new Point();

    delete =
        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
            context.getResources(), R.drawable.delete_btn_64), RADIUS * 2, RADIUS * 2,
            true);
    rotate =
        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
            context.getResources(), R.drawable.rotate_btn_64), RADIUS * 2, RADIUS * 2,
            true);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    int[] measureSpecs = null;
    measureSpecs = getMeasureSpec();
    super.onMeasure(measureSpecs[WIDTH], measureSpecs[HEIGHT]);
  }

  @Override
  public void draw(Canvas canvas)
  {
    canvas.rotate(rotateDegree, getWidth() / 2, getHeight() / 2);
    super.draw(canvas);
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    canvas.drawColor(Color.BLACK);
    if(currentStatus == STATUS_INIT)
    {
      centerX = getLeft() + getWidth() / 2;
      centerY = getTop() + getHeight() / 2;
      MyLog.i(TAG, "centerX=" + centerX + ", centerY=" + centerY);
      currentStatus = STATUS_NORMAL;
    }

    int rx = 0, ry = 0;
    rx = sticker.getWidth() / 2 + RADIUS;
    ry = sticker.getHeight() / 2 + RADIUS;
    // canvas.drawCircle(getWidth() / 2, getHeight() / 2,
    // (float) getDistance(0, 0, rx, ry), paint);
    int cx = 0, cy = 0;
    cx = sticker.getWidth() / 2 + RADIUS;
    cy = 0 - sticker.getHeight() / 2 - RADIUS;

    canvas.drawBitmap(sticker, (getWidth() - sticker.getWidth()) / 2,
        (getHeight() - sticker.getHeight()) / 2, paint);
    canvas.save();
    canvas.rotate(-rotateDegree, getWidth() / 2, getHeight() / 2);
    canvas.save();
    canvas.translate(getWidth() / 2, getHeight() / 2);
    int hitX = 0, hitY = 0;
    int picDegree =
        (int) Math.toDegrees(Math.atan2(sticker.getHeight() / 2,
            sticker.getWidth() / 2));
    hitX =
        (int) (getDistance(0, 0, rx, ry) * Math.cos(Math.toRadians(rotateDegree
            + picDegree)));
    hitY =
        (int) (getDistance(0, 0, rx, ry) * Math.sin(Math.toRadians(rotateDegree
            + picDegree)));
    canvas.restore();
    rotateRect.set(hitX + getWidth() / 2 - RADIUS, hitY + getHeight() / 2
        - RADIUS, hitX + getWidth() / 2 + RADIUS, hitY + getHeight() / 2
        + RADIUS);
    // canvas.drawLine(0, 0, hitX + getWidth() / 2, hitY + getHeight() / 2,
    // paint);
    hitX =
        (int) (getDistance(0, 0, cx, cy) * Math.cos(Math.toRadians(rotateDegree
            - picDegree)));
    hitY =
        (int) (getDistance(0, 0, cx, cy) * Math.sin(Math.toRadians(rotateDegree
            - picDegree)));
    cancelRect.set(hitX + getWidth() / 2 - RADIUS, hitY + getHeight() / 2
        - RADIUS, hitX + getWidth() / 2 + RADIUS, hitY + getHeight() / 2
        + RADIUS);
    // canvas.drawRect(rotateRect, paint);
    // canvas.drawRect(cancelRect, paint);
    canvas.restore();
    canvas.drawCircle(centerX - getLeft(), centerY - getTop(), 5, paint);
    canvas.drawLine(0, 0, getWidth(), getHeight(), paint);
    canvas.drawLine(0, getHeight(), getWidth(), 0, paint);
    canvas.translate(getWidth() / 2, getHeight() / 2);
    if(isSelected)
    {
      canvas.drawBitmap(rotate, rx - RADIUS, ry - RADIUS, paint);
      // canvas.drawCircle(rx, ry, RADIUS, paint);
      canvas.drawBitmap(delete, cx - RADIUS, cy - RADIUS, paint);
      // canvas.drawCircle(cx, cy, RADIUS, paint);
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    int curX, curY;
    int curRawX, curRawY;
    float f;

    if(!isSelected)
    {
      return false;
    }

    switch(event.getActionMasked())
    {
      case MotionEvent.ACTION_DOWN:
        // MyLog.i(TAG, "action down");

        isCancel = false;
        isRotate = false;
        isMove = false;

        // For Move
        downRawX = (int) event.getRawX();
        downRawY = (int) event.getRawY();
        lastRawX = downRawX;
        lastRawY = downRawY;

        // For rotation and resizing
        downViewX = (int) event.getX();
        downViewY = (int) event.getY();
        lastViewX = downViewX;
        lastViewY = downViewY;

        if(rotateRect.contains(downViewX, downViewY))
        {
          // MyLog.i(TAG, "rotate");
          lastTheata =
              Math.atan2(event.getY() - getHeight() / 2, event.getX()
                  - getWidth() / 2);
          lastDistance =
              getDistance(getWidth() / 2, getHeight() / 2, downViewX, downViewY);
          lastBmpWidth = sticker.getWidth();
          lastBmpHeight = sticker.getHeight();
          // MyLog.i(TAG, "lastDistance=" + lastDistance);
          isRotate = true;
          invalidate();
        }
        else if(cancelRect.contains(lastViewX, lastViewY))
        {
          // MyLog.i(TAG, "cancel");
          isCancel = true;
        }
        else
        {
          isMove = true;
        }
        return true;

      case MotionEvent.ACTION_POINTER_DOWN:
        if(event.getPointerCount() == 2)
        {
          // MyLog.i(TAG, "action pointer down");
          return false;
        }
        break;

      case MotionEvent.ACTION_UP:
        // MyLog.i(TAG, "action up");

        if(isCancel)
        {
          // MyLog.i(TAG, "do cancel");
          isCancel = false;
          cancel();
        }
        if(isRotate)
        {
          isRotate = false;
        }
        if(isMove)
        {
          isMove = false;
        }
        break;

      case MotionEvent.ACTION_MOVE:
        // MyLog.i(TAG, "action move");
        curX = (int) event.getX();
        curY = (int) event.getY();

        if(isRotate)
        {
          double theata =
              Math.atan2(event.getY() - getHeight() / 2, event.getX()
                  - getWidth() / 2);
          // MyLog.i(TAG, "theata=" + theata);
          theata = theata < 0 ? theata + Math.PI * 2 : theata;
          // MyLog.i(TAG, "theata=" + theata);
          // MyLog.i(TAG, "lastTheata=" + lastTheata);
          // MyLog.i(TAG, "deltaTheata=" + (theata - lastTheata));
          deltaDegree = (float) Math.toDegrees(theata - lastTheata);
          lastTheata = theata;
          // MyLog.i(TAG, "deltaDegree=" + deltaDegree);
          this.rotateDegree += deltaDegree;
          if(rotateDegree > 360)
          {
            rotateDegree -= 360;
          }
          else if(rotateDegree < 0)
          {
            rotateDegree += 360;
          }
          // MyLog.i(TAG, "rotateDegree=" + rotateDegree);
          // MyLog.i(TAG, "rotateDegree=" + rotateDegree);
          double curDist =
              getDistance(getWidth() / 2, getHeight() / 2, event.getX(),
                  event.getY());
          // MyLog.i(TAG, "curDist=" + curDist);
          f = (float) (curDist / lastDistance);
          MyLog.i(TAG, "f=" + f);
          // invalidate();

          resize(f);

          // 调整view的位置，保持中心点不变
          int border =
              sticker.getWidth() > sticker.getHeight() ? sticker.getWidth()
                  : sticker.getHeight();
          border = (int) (Math.sqrt(2) * (border + 4 * RADIUS));
          int left = centerX - border / 2;
          int top = centerY - border / 2;
          params = (LayoutParams) this.getLayoutParams();
          MyLog.i(TAG, "left=" + params.leftMargin + ", top="
              + params.topMargin);
          params.leftMargin = left;
          params.topMargin = top;
          this.setLayoutParams(params);
          MyLog.i(TAG, "left=" + left + ", top=" + top);

          // MyLog.i(TAG, "movex=" + (lastBmpWidth - sticker.getWidth()));
          // MyLog.i(TAG, "movey=" + (lastBmpHeight - sticker.getHeight()));
          // move((lastBmpWidth - sticker.getWidth()) / 2f,
          // (lastBmpHeight - sticker.getHeight()) / 2f);
          lastDistance = curDist;
        }

        else if(isMove)
        {
          int dx = 0;
          int dy = 0;
          dx = (int) (event.getRawX() - lastRawX);
          dy = (int) (event.getRawY() - lastRawY);

          if(Math.abs(dx) > 10 || Math.abs(dy) > 10)
          {
            move(dx, dy);

            lastViewX = curX;
            lastViewY = curY;
            lastRawX = (int) event.getRawX();
            lastRawY = (int) event.getRawY();
            centerX += dx;
            centerY += dy;
          }

          MyLog.i(TAG, "centerX=" + centerX + ", centerY=" + centerY);
        }

        break;
    }
    return super.onTouchEvent(event);
  }

  private int[] getMeasureSpec()
  {
    int[] measures = new int[2];
    int border =
        sticker.getWidth() > sticker.getHeight() ? sticker.getWidth() : sticker
            .getHeight();
    border = (int) (Math.sqrt(2) * (border + 4 * RADIUS));

    int widthMeasureSpec =
        MeasureSpec.makeMeasureSpec(border, MeasureSpec.EXACTLY);
    int heightMeasureSpec =
        MeasureSpec.makeMeasureSpec(border, MeasureSpec.EXACTLY);
    measures[WIDTH] = widthMeasureSpec;
    measures[HEIGHT] = heightMeasureSpec;
    return measures;
  }

  /**
   * 计算点的角度
   * 
   * @param x x坐标
   * @param y y坐标
   * @return 这个点相对于x轴的角度
   */
  public int getDegree(int x, int y)
  {
    double theata = Math.atan2(y, x);
    theata = theata < 0 ? theata + 2 * Math.PI : theata;

    int degree = (int) Math.toDegrees(theata);
    return degree;
  }

  /**
   * 计算两点之间的距离
   * 
   * @param x1 第一个点的x坐标
   * @param y1 第一个点的y坐标
   * @param x2 第二个点的x坐标
   * @param y2 第二个点的y坐标
   * @return 两点之间的距离
   */
  public double getDistance(float x1, float y1, float x2, float y2)
  {
    return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
  }

  /**
   * 缩放图片
   * 
   * @param f 缩放比例
   */
  private void resize(float f)
  {
    lastBmpWidth = sticker.getWidth();
    lastBmpHeight = sticker.getHeight();
    MyLog.i(TAG, "lastBmpWidth=" + lastBmpWidth);
    MyLog.i(TAG, "lastBmpHeight=" + lastBmpHeight);

    if(sticker != null && !sticker.isRecycled())
    {
      sticker.recycle();
    }
    sticker = getSrcSticker();
    float sf = sticker.getWidth() / sticker.getHeight();
    currentRatio = currentRatio * f;
    MyLog.i(TAG, "f=" + f);
    int dstH = (int) (sticker.getHeight() * currentRatio * scaling * zoomRatio);
    int dstW = (int) (sticker.getWidth() * currentRatio * scaling * zoomRatio);
    // int dstW = (int) (dstH * sf);
    if(dstW < 2 * RADIUS || dstH < 2 * RADIUS)
    {
      dstW = sticker.getWidth() / 4;
      dstH = sticker.getHeight() / 4;
    }
    if(dstW > 3 * sticker.getWidth() || dstH > 3 * sticker.getHeight())
    {
      dstW = sticker.getWidth() * 3;
      dstH = sticker.getHeight() * 3;
    }
    MyLog.i(TAG, "dstW=" + dstW);
    MyLog.i(TAG, "dstH=" + dstH);
    sticker = Bitmap.createScaledBitmap(sticker, dstW, dstH, true);
  }

  /**
   * 移动View
   * 
   * @param x x轴方向移动的距离
   * @param y y轴方向移动的距离
   */
  private void move(float x, float y)
  {
    MyLog.i(TAG, "move: " + x + ", " + y);
    params = (LayoutParams) this.getLayoutParams();
    params.leftMargin += x;
    params.topMargin += y;
    this.setLayoutParams(params);
  }

  /**
   * 从父View上移除本View
   */
  public void cancel()
  {
    MyLog.i(TAG, "remove");
    ((FrameLayout) getParent()).removeView(this);
  }

  /**
   * 根据文件路径获取sticker的原图，并根据照片的缩放比例缩放sticker
   * 
   * @return 根据照片缩放比例缩放后的sticker
   */
  private Bitmap getSrcSticker()
  {
    InputStream is = null;
//    is = context.getResources().openRawResource(R.drawable.sticker_1_1);
    sticker = BitmapFactory.decodeStream(is);

    try
    {
      is.close();
    }
    catch(IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return sticker;
  }

  /**
   * @return the locationX
   */
  public int getLocationX()
  {
    return locationX;
  }

  /**
   * @param locationX the locationX to set
   */
  public void setLocationX(int locationX)
  {
    this.locationX = locationX;
  }

  /**
   * @return the locationY
   */
  public int getLocationY()
  {
    return locationY;
  }

  /**
   * @param locationY the locationY to set
   */
  public void setLocationY(int locationY)
  {
    this.locationY = locationY;
  }

  /**
   * @return the scaling
   */
  public float getScaling()
  {
    return scaling;
  }

  /**
   * @param scaling the scaling to set
   */
  public void setScaling(float scaling)
  {
    this.scaling = scaling;
  }

  /**
   * @return the zoomRatio
   */
  public float getZoomRatio()
  {
    return zoomRatio;
  }

  /**
   * @param zoomRatio the zoomRatio to set
   */
  public void
      setZoomRatio(float zoomRatio, float pixelRatioW, float pixelRatioH)
  {
    this.zoomRatio = zoomRatio;
    // centerX = (int) (centerX * pixelRatioW);
    // centerY = (int) (centerY * pixelRatioH);
    centerX = Math.round(centerX * pixelRatioW);
    centerY = Math.round(centerY * pixelRatioH);

    MyLog.i(TAG, "zoomRatio=" + zoomRatio);
    resize(1f);

    // 调整view的位置，保持中心点不变
    int border =
        sticker.getWidth() > sticker.getHeight() ? sticker.getWidth() : sticker
            .getHeight();
    border = (int) (Math.sqrt(2) * (border + 4 * RADIUS));
    int left = centerX - border / 2;
    int top = centerY - border / 2;
    params = (LayoutParams) this.getLayoutParams();
    MyLog.i(TAG, "left=" + params.leftMargin + ", top=" + params.topMargin);
    params.leftMargin = left;
    params.topMargin = top;
    this.setLayoutParams(params);
    // move(Math.round((lastBmpWidth - sticker.getWidth()) / 2f),
    // Math.round((lastBmpHeight - sticker.getHeight()) / 2f));

    lastDistance =
        getDistance(sticker.getWidth() / 2, sticker.getHeight() / 2,
            sticker.getWidth() / 2 + RADIUS, sticker.getHeight() / 2 + RADIUS);
  }

  /**
   * @param moveX
   * @param moveY
   */
  public void setTranslation(float moveX, float moveY)
  {
    MyLog.i(TAG, "moveX="+moveX);
    MyLog.i(TAG, "moveY="+moveY);
    centerX += Math.round(moveX);
    centerY += Math.round(moveY);
    params = (LayoutParams) this.getLayoutParams();
    params.leftMargin += Math.round(moveX);
    params.topMargin += Math.round(moveY);
    this.setLayoutParams(params);
  }

  /**
   * @return the isSelected
   */
  public boolean isSelected()
  {
    return isSelected;
  }

  /**
   * @param isSelected the isSelected to set
   */
  public void setSelected(boolean isSelected)
  {
    this.isSelected = isSelected;
    invalidate();
  }

}
