package com.GoTravel.Go1978.extracomponent;


import java.io.IOException;
import java.io.InputStream;

import com.GoTravel.Go1978.BaseActivity;
import com.GoTravel.Go1978.EditTextActivity;
import com.GoTravel.Go1978.PictureEditorActivity;
import com.GoTravel.Go1978.R;
import com.GoTravel.Go1978.log.MyLog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;


public class DragTextView extends View
{
  private static final String TAG = "StickerView";
  private static final int RADIUS = 24;

  private static final int WIDTH = 0;
  private static final int HEIGHT = 1;
  int[] measureSpecs;

  private Context context;
  
  private String text = null;

  // picture
  Bitmap textBg;
  Paint paint;

  // view x
  private int locationX;
  // view y
  private int locationY;
  private float scaling = 1f;
  private float lastF = 1f;

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
  private int lastCenterX, lastCenterY;

  double lastTheata = 0;

  private Rect cancelRect, rotateRect, moveRect;
  private Point cancelPoint, rotatePoint;
  private int ccX = 0, ccY = 0;
  private int rcX = 0, rcY = 0;
  boolean isCancel = false;
  boolean isRotate = false;
  boolean isMove = false;

  Bitmap delete;
  Bitmap rotate;

  long downTime = 0;
  long upTime = 0;

  public DragTextView(Context context, float scaling)
  {
    super(context);
    this.context = context;
    this.scaling = scaling;

    if(scaling != 1f)
    {
      textBg =
          Bitmap.createScaledBitmap(getSrcImage(),
              (int) (textBg.getWidth() * scaling),
              (int) (textBg.getHeight() * scaling), true);
      lastF = scaling;
    }
    else
    {
      textBg = getSrcImage();
    }

    lastBmpWidth = textBg.getWidth();
    lastBmpHeight = textBg.getHeight();
    // measureSpecs = getMeasureSpec();
    // measure(measureSpecs[WIDTH], measureSpecs[HEIGHT]);

    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setColor(Color.YELLOW);

    cancelRect = new Rect();
    cancelPoint = new Point();
    rotateRect = new Rect();
    rotatePoint = new Point();
    moveRect = new Rect();

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
    int rx = 0, ry = 0;
    rx = textBg.getWidth() / 2 + RADIUS;
    ry = textBg.getHeight() / 2 + RADIUS;
    // canvas.drawCircle(getWidth() / 2, getHeight() / 2,
    // (float) getDistance(0, 0, rx, ry), paint);
    int cx = 0, cy = 0;
    cx = textBg.getWidth() / 2 + RADIUS;
    cy = 0 - textBg.getHeight() / 2 - RADIUS;

    canvas.drawBitmap(textBg, (getWidth() - textBg.getWidth()) / 2,
        (getHeight() - textBg.getHeight()) / 2, paint);
    canvas.save();
    canvas.rotate(-rotateDegree, getWidth() / 2, getHeight() / 2);
    canvas.save();
    canvas.translate(getWidth() / 2, getHeight() / 2);
    int hitX = 0, hitY = 0;
    int picDegree =
        (int) Math.toDegrees(Math.atan2(textBg.getHeight() / 2,
            textBg.getWidth() / 2));
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
    moveRect.set(getWidth() / 2 - textBg.getWidth() / 2, getHeight() / 2
        - textBg.getHeight() / 2, getWidth() / 2 + textBg.getWidth() / 2,
        getWidth() / 2 + textBg.getHeight() / 2);
    // canvas.drawRect(rotateRect, paint);
    // canvas.drawRect(cancelRect, paint);
    canvas.restore();
    canvas.translate(getWidth() / 2, getHeight() / 2);
    canvas.drawBitmap(rotate, rx - RADIUS, ry - RADIUS, paint);
    // canvas.drawCircle(rx, ry, RADIUS, paint);
    canvas.drawBitmap(delete, cx - RADIUS, cy - RADIUS, paint);
    // canvas.drawCircle(cx, cy, RADIUS, paint);
    if (text != null) {
      paint.setTextSize(18);
      canvas.drawText(text, 0-(text.length() / 2) - 20, 0, paint);
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    int curX, curY;
    int curRawX, curRawY;
    float f;

    switch(event.getAction())
    {
      case MotionEvent.ACTION_DOWN:
        MyLog.i(TAG, "action down");
        downTime = System.currentTimeMillis();

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

        lastCenterX = getWidth() / 2;
        lastCenterY = getHeight() / 2;
        if(rotateRect.contains(downViewX, downViewY))
        {
          MyLog.i(TAG, "rotate");
          lastTheata =
              Math.atan2(event.getY() - getHeight() / 2, event.getX()
                  - getWidth() / 2);
          lastDistance =
              getDistance(getWidth() / 2, getHeight() / 2, downViewX, downViewY);
          lastBmpWidth = textBg.getWidth();
          lastBmpHeight = textBg.getHeight();
          MyLog.i(TAG, "lastDistance=" + lastDistance);
          isRotate = true;
          invalidate();
        }
        else if(cancelRect.contains(lastViewX, lastViewY))
        {
          MyLog.i(TAG, "cancel");
          isCancel = true;
        }
        else if(moveRect.contains(lastViewX, lastViewY))
        {
          isMove = true;
        }
        break;

      case MotionEvent.ACTION_UP:
        MyLog.i(TAG, "action up");
        upTime = System.currentTimeMillis();

        if(isCancel)
        {
          MyLog.i(TAG, "do cancel");
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
          if(upTime - downTime < 1000)
          {
            int drx = (int) Math.abs(event.getRawX() - downRawX);
            int dry = (int) Math.abs(event.getRawY() - downRawY);
            if(drx < 10 && dry < 10)
            {
              MyLog.i(TAG, "show activity");
              Intent intent = new Intent(context, EditTextActivity.class);
              if (text != null) {
                intent.putExtra(PictureEditorActivity.BUNDLE_TEXT, text);
              }
              ((BaseActivity) context).startActivityForResult(intent,
                  PictureEditorActivity.REQUEST_CODE_EDIT_TEXT);
            }
          }
        }
        break;

      case MotionEvent.ACTION_MOVE:
        MyLog.i(TAG, "action move");
        curX = (int) event.getX();
        curY = (int) event.getY();

        if(isRotate)
        {
          double theata =
              Math.atan2(event.getY() - getHeight() / 2, event.getX()
                  - getWidth() / 2);
          MyLog.i(TAG, "theata=" + theata);
          theata = theata < 0 ? theata + Math.PI * 2 : theata;
          MyLog.i(TAG, "theata=" + theata);
          MyLog.i(TAG, "lastTheata=" + lastTheata);
          MyLog.i(TAG, "deltaTheata=" + (theata - lastTheata));
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
          MyLog.i(TAG, "rotateDegree=" + rotateDegree);
          // MyLog.i(TAG, "rotateDegree=" + rotateDegree);
          double curDist =
              getDistance(getWidth() / 2, getHeight() / 2, event.getX(),
                  event.getY());
          MyLog.i(TAG, "curDist=" + curDist);
          f = (float) (curDist / lastDistance);
          MyLog.i(TAG, "f=" + f);
          // invalidate();

          resize(f);
          MyLog.i(TAG, "movex=" + (lastBmpWidth - textBg.getWidth()));
          MyLog.i(TAG, "movey=" + (lastBmpHeight - textBg.getHeight()));
          move((lastBmpWidth - textBg.getWidth()) / 2,
              (lastBmpHeight - textBg.getHeight()) / 2);
          lastDistance = curDist;
        }

        else if(isMove)
        {
          int dx = 0;
          int dy = 0;
          dx = (int) (event.getRawX() - lastRawX);
          dy = (int) (event.getRawY() - lastRawY);

          move(dx, dy);

          lastViewX = curX;
          lastViewY = curY;
          lastRawX = (int) event.getRawX();
          lastRawY = (int) event.getRawY();

        }

        break;
    }
    return true;
  }

  private int[] getMeasureSpec()
  {
    int[] measures = new int[2];
    int border =
        textBg.getWidth() > textBg.getHeight() ? textBg.getWidth() : textBg
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
    lastBmpWidth = textBg.getWidth();
    lastBmpHeight = textBg.getHeight();

    if(textBg != null && !textBg.isRecycled())
    {
      textBg.recycle();
    }
    textBg = getSrcImage();
    float sf = textBg.getWidth() / textBg.getHeight();
    int dstH = (int) (lastBmpHeight * f);
    int dstW = (int) (dstH * sf);
    if(dstW < 2 * RADIUS || dstH < 2 * RADIUS)
    {
      dstW = textBg.getWidth() / 4;
      dstH = textBg.getHeight() / 4;
    }
    if(dstW > 3 * textBg.getWidth() || dstH > 3 * textBg.getHeight())
    {
      dstW = textBg.getWidth() * 3;
      dstH = textBg.getHeight() * 3;
    }
    textBg = Bitmap.createScaledBitmap(textBg, dstW, dstH, true);
  }

  /**
   * 移动View
   * 
   * @param x x轴方向移动的距离
   * @param y y轴方向移动的距离
   */
  private void move(int x, int y)
  {
    MyLog.i(TAG, "move: " + x + ", " + y);
    FrameLayout.LayoutParams fp = (LayoutParams) this.getLayoutParams();
    fp.leftMargin += x;
    fp.topMargin += y;
    this.setLayoutParams(fp);
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
   * 根据文件路径获取背景的原图，并根据照片的缩放比例缩放背景
   * 
   * @return 根据照片缩放比例缩放后的背景
   */
  private Bitmap getSrcImage()
  {
    InputStream is = null;
    is = context.getResources().openRawResource(R.drawable.text_bg);
    textBg = BitmapFactory.decodeStream(is);

    try
    {
      is.close();
    }
    catch(IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return textBg;
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
   * @return the text
   */
  public String getText()
  {
    return text;
  }

  /**
   * @param text the text to set
   */
  public void setText(String text)
  {
    this.text = text;
  }

}
