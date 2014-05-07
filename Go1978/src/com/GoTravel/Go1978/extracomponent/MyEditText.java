package com.GoTravel.Go1978.extracomponent;



import com.GoTravel.Go1978.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;


public class MyEditText extends EditText
{
  private static final String TAG = "MyEditText";

  private static final int RADIUS = 24;
  private static final int DISTANCE = 48;

  // 判断是否处于debug模式：true, debug模式；false, 非debug模式
  boolean isDebug = false;

  Context context;
  Paint paint;

  // 记录按下时相对于view的x, y坐标
  int downViewX, downViewY;
  // 记录按下时相对于屏幕的x, y坐标
  int downRawX, downRawY;
  // 记录在touch过程中上一次事件相对于view的x, y坐标
  int lastViewX, lastViewY;
  // 记录在touch过程中上一次事件相对于屏幕的x, y坐标
  int lastRawX, lastRawY;

  // 记录上一次的夹角
  double lastTheata = 0;
  // 记录view旋转的角度
  float rotateDegree;
  // 记录在显示hint的情况下控制按钮相对于中心的角度
  float hintDegree;
  // 记录文本最大的行宽
  int maxLineWidth;
  // 记录文本最大的高度
  int maxHeight;
  // 记录文本改变前最大的行宽
  int lastMaxLineWidth;
  // 记录文本改变前最大的高度
  int lastMaxHeight;
  // hint的文本宽度
  int hintWidth;

  // 是否处于旋转或者缩放状态
  boolean isRotationOrTransition = false;
  // 是否绘制控制按钮
  boolean isCtrlBallVisible = true;

  // 记录控制按钮点击有效区
  Rect myHitRect;

  // 上一次手指坐标到原点的距离
  double lastDist;

  // 记录view的layout属性
  LayoutParams params;

  // 文字的尺寸
  float textSize = 0;

  // 背景图片
  Bitmap textBg;
  // 用于记录被缩放的背景图片
  Bitmap bmp;

  public MyEditText(Context context)
  {
    super(context);
    init(context);
  }

  /**
   * Debug模式的构造器
   * 
   * @param context
   * @param isDebug
   */
  public MyEditText(Context context, boolean isDebug)
  {
    super(context);
    this.isDebug = isDebug;
    init(context);
  }

  public MyEditText(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init(context);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    // 获取新的宽高
    int[] result = setSize();

    super.onMeasure(result[0], result[1]);

  }

  @Override
  public void draw(Canvas canvas)
  {
    // 旋转画布
    canvas.rotate(rotateDegree, getWidth() / 2, getHeight() / 2);

    super.draw(canvas);
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    // 计算控制按钮的坐标
    int circleX = 0, circleY = 0;
    if(getPaint().measureText(getText().toString()) == 0) // 没录入文字
    {
      circleX = (int) (getPaint().measureText(getHint().toString()) / 2);
      circleY = getLineHeight() * (getLineCount() + 1) / 2;
    }
    else
    // 录入了文字
    {
      circleX =
          (int) ((maxLineWidth - getPaddingLeft() - getPaddingRight()) / 2);
      circleY = getLineHeight() * getLineCount() / 2;
    }

    if(!isDebug)
    {
      // 绘制背景
      if(textBg != null)
      {
        canvas.drawBitmap(bmp, (getWidth() - maxLineWidth) / 2,
            (getHeight() - maxHeight) / 2, paint);
      }
    }
    else
    {
      paint.setColor(Color.GRAY);
      canvas.drawCircle(getWidth() / 2, getHeight() / 2,
          (float) calDistance(0, 0, circleX, circleY), paint);
      paint.setColor(Color.RED);
    }

    super.onDraw(canvas);

    // 绘制控制按钮
    if(isCtrlBallVisible)
    {
      Matrix matrix = new Matrix();
      canvas.save();
      // 反旋转回去以view的坐标系计算点击区域和绘制控制按钮
      canvas.rotate(-rotateDegree, getWidth() / 2, getHeight() / 2);

      canvas.save();
      // 平移到view的中点处，以中点为原点计算点击区域的坐标
      canvas.translate(getWidth() / 2, getHeight() / 2);
      int hitX = 0, hitY = 0;
      if(getText().toString().length() == 0)
      {
        hitX =
            (int) (calDistance(0, 0, circleX, circleY) * Math.cos(Math
                .toRadians(rotateDegree + hintDegree)));
        hitY =
            (int) (calDistance(0, 0, circleX, circleY) * Math.sin(Math
                .toRadians(rotateDegree + hintDegree)));
      }
      else
      {
        int textDegree =
            (int) Math.toDegrees(Math.atan2(
                (getLineCount() * getLineHeight()) / 2, (maxLineWidth
                    - getPaddingLeft() - getPaddingRight()) / 2));
        hitX =
            (int) (calDistance(0, 0, circleX, circleY) * Math.cos(Math
                .toRadians(rotateDegree + textDegree)));
        hitY =
            (int) (calDistance(0, 0, circleX, circleY) * Math.sin(Math
                .toRadians(rotateDegree + textDegree)));
      }
      canvas.restore();
      // 设置点击区域
      myHitRect.set(hitX + getWidth() / 2 - RADIUS, hitY + getHeight() / 2
          - RADIUS, hitX + getWidth() / 2 + RADIUS, hitY + getHeight() / 2
          + RADIUS);

      if(isDebug)
      {
        Paint myPaint = new Paint();
        myPaint.setColor(Color.BLACK);
        canvas.drawLine(0, 0, hitX + getWidth() / 2, hitY + getHeight() / 2,
            myPaint);
        canvas.drawRect(myHitRect, getPaint());
      }

      canvas.restore();

      // 绘制控制按钮
      canvas.translate(getWidth() / 2, getWidth() / 2);
      canvas.drawCircle(circleX, circleY, RADIUS, paint);
    }
  }

  @Override
  protected void onTextChanged(CharSequence text, int start, int lengthBefore,
      int lengthAfter)
  {
    super.onTextChanged(text, start, lengthBefore, lengthAfter);
    Log.i(TAG, "start=" + start + ", lengthBefore=" + lengthBefore
        + ", lengthAfter" + lengthAfter);

    if(text.toString().length() == 0
    /*
     * 创建EditText对象时，会回调onTextChanged，此时getHint()获取不到hint的
     * 内容，所以添加这个条件避免创建view时执行getHint()操作
     */
    && !(start == 0 && lengthBefore == 0 && lengthAfter == 0))
    {
      // 没有输入时，根据hint来设置view的大小
      setMaxWidthAndHeight(getHint().toString());
    }
    else
    {
      // 有输入后，根据文本内容设置view的大小
      setMaxWidthAndHeight(text.toString());
    }
  }

  /**
   * 初始化
   * 
   * @param context
   */
  private void init(Context context)
  {
    this.context = context;
    setFocusable(false);
    setPadding(50, 50, 50, 50);
    setLongClickable(false);
    setHint("Press here to input");
    if(isDebug)
    {
      setBackgroundResource(R.drawable.image_view_bg);
    }
    else
    {
      // 设为0表示清空背景
      setBackgroundResource(0);
    }

    // 初始化paint
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setColor(Color.RED);

    // 初始化各种宽高
    int len = (int) getPaint().measureText(getHint().toString());
    maxLineWidth = len + 100;
    maxHeight = len + 100;
    hintWidth = len;

    // 计算控制按钮相对于中心点的初始角度
    int originX = 0, originY = 0;
    originX = hintWidth / 2 + RADIUS;
    originY = getLineCount() * getLineHeight() / 2 + RADIUS * 2;
    hintDegree = (float) Math.toDegrees(Math.atan2(originY, originX));

    myHitRect = new Rect();
  }

  /**
   * 设置view的显示大小
   * 
   * @return 带两个元素的int数组，0是widthMeasureSpec，1是heightMeasureSpce
   */
  private int[] setSize()
  {
    int widthMeasureSpec =
        MeasureSpec.makeMeasureSpec(maxLineWidth, MeasureSpec.EXACTLY);
    int heightMeasureSpec =
        MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
    int[] result = {widthMeasureSpec, heightMeasureSpec};
    return result;
  }

  /**
   * 设置控制按钮是否可见
   * 
   * @param isVisible
   */
  public void setCtrlBallVisible(boolean isVisible)
  {
    isCtrlBallVisible = isVisible;
  }

  /**
   * 弹出/隐藏键盘
   */
  private void hideSoftInput()
  {
    InputMethodManager imm =
        (InputMethodManager) context
            .getSystemService(Context.INPUT_METHOD_SERVICE);
    if(imm.isActive())
    {
      imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }
  }

  /**
   * 启用/禁用EditText文字输入功能
   * 
   * @param isEnable
   */
  private void enableEditText(boolean isEnable)
  {
    setFocusable(isEnable);
    setFocusableInTouchMode(isEnable);
    if(!isEnable)
    {
      hideSoftInput();
    }
    setLongClickable(isEnable);
  }

  /**
   * 改变view的大小
   * 
   * @param f 当前触摸点与原点的距离和上一个触摸点与原点的距离的比值
   */
  private void changeViewSize(float f)
  {
    // 改变字体的大小
    this.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize *= f);

    // 更新行的宽高
    if(getText().toString().length() != 0)
    {
      setMaxWidthAndHeight(getText().toString());
    }
    else
    {
      setMaxWidthAndHeight(getHint().toString());
    }

    // 改变背景的大小
    scaleBackground();

    // 移动view，实现以中心点放大缩小的效果
    int moveX = (lastMaxLineWidth - maxLineWidth) / 2;
    int moveY = (lastMaxHeight - maxHeight) / 2;
    moveView(moveX, moveY);

  }

  /**
   * 移动view
   * 
   * @param x
   * @param y
   */
  private void moveView(int x, int y)
  {
    params = (LayoutParams) this.getLayoutParams();
    params.leftMargin += x;
    params.topMargin += y;
    this.setLayoutParams(params);
  }

  /**
   * 判断触摸移动的范围是否大于阈值：
   * 是，作移动事件处理，禁用EditText功能
   * 否，作点击事件处理，使能EditText功能
   * 
   * @param dx x轴移动的距离
   * @param dy y轴移动的距离
   */
  private void setActionUpState(int dx, int dy)
  {
    if(Math.abs(dx) < DISTANCE)
    {
      if(Math.abs(dy) < DISTANCE)
      {
        enableEditText(true);
      }
      else
      {
        enableEditText(false);
      }
    }
    else
    {
      enableEditText(false);
    }
  }

  /**
   * 计算旋转的角度
   * 
   * @param x 当前触摸点的x坐标
   * @param y 当前触摸点的y坐标
   * @return 当前触摸点与上一个触摸点相对于view中心的角度差
   */
  private float calDeltaDegree(float x, float y)
  {
    double theata = Math.atan2(y, x);

    /*
     * Math.atan2计算出来的角度范围是-π~π，
     * 如果结果小于0，加2π把结果范围转化为0~2π
     */
    theata = theata < 0 ? theata + Math.PI * 2 : theata;

    float result = (float) Math.toDegrees(theata - lastTheata);
    lastTheata = theata;

    return result;
  }

  /**
   * 设置canvas旋转过的角度
   * 
   * @param deltaDegree 触摸点移动前后的角度差
   */
  private void setRawDegree(float deltaDegree)
  {
    this.rotateDegree += deltaDegree;

    // 将旋转角度的值控制在0~360之间
    if(rotateDegree > 360)
    {
      rotateDegree -= 360;
    }
    else if(rotateDegree < 0)
    {
      rotateDegree += 360;
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    if(textSize == 0)
    {
      textSize = this.getTextSize();
    }

    switch(event.getAction())
    {
      case MotionEvent.ACTION_DOWN:
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

        isRotationOrTransition = false;

        if(myHitRect.contains(downViewX, downViewY)) // 触摸点在控制按钮上
        {
          // Log.i(TAG, "ssssssssssssssssssssssssss");

          lastTheata = Math.atan2(event.getY() - getHeight() / 2, event.getX() - getWidth() / 2);
          // 直接将第一次触摸的点到view中心的距离设为lastDist，供移动时计算使用
          lastDist =
              calDistance(getWidth() / 2, getHeight() / 2, downViewX, downViewY);

          // 设为true，表示进入旋转和缩放状态
          isRotationOrTransition = true;

          enableEditText(false);
          invalidate();
        }
        else
        // 触摸点在view里，但不在控制按钮上
        {
          // Log.i(TAG, "dfadfasfafafafa");

          isRotationOrTransition = false;
          invalidate();
        }
        break;

      case MotionEvent.ACTION_UP:
        isRotationOrTransition = false;
        int deltaRawX = (int) (event.getRawX() - downRawX);
        int deltaRawY = (int) (event.getRawY() - downRawY);
        // 判断触摸点的移动范围是否大于阈值
        setActionUpState(deltaRawX, deltaRawY);
        invalidate();
        break;

      case MotionEvent.ACTION_MOVE:
        if(isRotationOrTransition) // 旋转和缩放状态
        {
          // 获取触摸点移动后相对于view中心点的角度差
          float result =
              calDeltaDegree(event.getX() - getWidth() / 2, event.getY()
                  - getHeight() / 2);
          // 设置canvas的旋转角度
          setRawDegree(result);
          invalidate();
          // 计算当前触摸点距离view中心点的距离
          double curDist =
              calDistance(getWidth() / 2, getHeight() / 2, event.getX(),
                  event.getY());

          // 改变view的大小
          changeViewSize((float) (curDist / lastDist));

          // 保存这次的距离
          lastDist = curDist;
        }
        else
        // 移动状态
        {
          int moveX = (int) (event.getRawX() - lastRawX);
          int moveY = (int) (event.getRawY() - lastRawY);

          // 移动view
          moveView(moveX, moveY);
        }

        // 移动过程中进用EditText功能
        enableEditText(false);

        // 保存当前触摸点的信息
        lastViewX = (int) event.getX();
        lastViewY = (int) event.getY();
        lastRawX = (int) event.getRawX();
        lastRawY = (int) event.getRawY();

        break;

      default:
        break;
    }

    return super.onTouchEvent(event);
  }

  // 计算最大行宽和高度
  private void setMaxWidthAndHeight(String text)
  {
    // 以"\n"作为分隔符分割字符串
    String[] strs = text.toString().split("\n");

    if(text.toString().length() != 0)
    {
      int textWidth = (int) getPaint().measureText(text);
      // 计算自宽
      int wordWidth = textWidth / text.length();

      // 找出宽度最大的行
      int max = 0;
      for(int i = 0; i < strs.length; i++)
      {
        max =
            (int) (max > strs[i].length() * wordWidth ? max : strs[i].length()
                * wordWidth);
      }
      // 保存之前行的最大宽度和EditText的最大高度
      lastMaxLineWidth = maxLineWidth;
      lastMaxHeight = maxHeight;

      // 保存目前最大的行宽和EditText的最大高度
      maxLineWidth = max + wordWidth;
      maxHeight = getLineHeight() * strs.length;

      /*
       * 比较maxLineWidth和maxHeight，令两者都等于他们之间的最大值。
       * 方便显示canvas旋转的内容
       */
      if(maxLineWidth > maxHeight)
      {
        maxLineWidth += getPaddingLeft() + getPaddingRight() + wordWidth;
        maxHeight = maxLineWidth;
      }
      else
      {
        maxHeight += getPaddingBottom() + getPaddingTop() + wordWidth;
        maxLineWidth = maxHeight;
      }
    }
  }

  /**
   * 计算两点之间的距离
   * 
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   * @return 两点之间的距离
   */
  private double calDistance(float x1, float y1, float x2, float y2)
  {
    return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
  }

  /**
   * 设置背景
   * 
   * @param resId 图片的resource id
   */
  public void setTextBackground(int resId)
  {
    textBg = BitmapFactory.decodeResource(getResources(), resId);
    scaleBackground();
  }

  /**
   * 缩放背景
   */
  private void scaleBackground()
  {
    if(textBg != null)
    {
      bmp = Bitmap.createScaledBitmap(textBg, maxLineWidth, maxHeight, false);
    }
  }
}
