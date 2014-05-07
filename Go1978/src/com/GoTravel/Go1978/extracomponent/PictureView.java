package com.GoTravel.Go1978.extracomponent;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.text.StaticLayout;
import android.text.Layout.Alignment;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.GoTravel.Go1978.EditTextActivity;
import com.GoTravel.Go1978.PictureEditorActivity;
import com.GoTravel.Go1978.R;
import com.GoTravel.Go1978.log.MyLog;
import com.GoTravel.Go1978.utils.ImageUtils;


/**
 * Created by will on 1/26/14.
 */
public class PictureView extends View
{

  private static final String TAG = "PictureView";

  // 用于固定编辑后的照片大小为1080 * 1440
  private static final int PIXEL_1080 = 1080;

  // 正常状态常量
  private static final int STATE_NORMAL = 0;
  // 初始化状态常量
  private static final int STATE_INIT = 1;
  // 缩放状态常量
  private static final int STATE_ZOOM = 2;
  // 拖放状态常量
  private static final int STATE_DRAG = 3;
  // 添加Sticker
  private static final int ADD_STICKER = 4;
  // 拖动Sticker
  private static final int DRAG_STICKER = 5;
  // 旋转Sticker
  private static final int ROTATE_STICKER = 6;
  // 设置相框
  private static final int SET_FRAME = 7;
  // 添加文字
  private static final int ADD_TEXT = 8;
  // 拖动文字
  private static final int DRAG_TEXT = 9;
  // 旋转文字
  private static final int ROTATE_TEXT = 10;
  // 缩放文字
  private static final int RESIZE_TEXT = 11;
  // 编辑文字
  private static final int EDIT_TEXT = 12;
  // 设置滤镜
  private static final int SET_FILTER = 13;

  private static final int DRAW_ROTATE_BUTTON = 0;
  private static final int DRAW_CANCEL_BUTTON = 1;
  private static final int DRAW_RESIZE_BUTTON = 2;

  // 记录当前的状态
  private int state = 0;

  Context context;
  // PictureView所在的Activity
  Activity activity;
  // 用于绘制Sticker，Text被选中时的边框
  Paint paint;

  // 照片路径
  private String photoPath = null;
  // 加载到内存后的照片
  Bitmap srcBmp;

  // 显示在Canvas上的按钮的宽高
  private static final int BMP_BUTTON_WH = 60;
  // 旋转按钮
  Bitmap rotateBmp;
  // 旋转按钮的Rect区域
  Rect rotateBmpRect;
  // 删除按钮
  Bitmap deleteBmp;
  // 删除按钮的Rect区域
  Rect deleteBmpRect;
  // 字体大小设置按钮
  Bitmap resizeBmp;
  // 字体大小设置按钮的Rect区域
  Rect resizeBmpRect;

  // view的范围,屏幕上用于显示的控件的范围
  Rect viewRect;
  // canvas的可见范围，与viewRect成比例，如果visibleRect足够大，会包含canvas的空白部分
  Rect visibleRect;
  // 底图的可视范围，如果visibleRect打范围小于底图，displayRect与visibleRect重合
  Rect displayRect;
  // 在view上的绘制范围
  Rect drawingRect;
  
  // view上的sticker和text Rect
  Rect textRect = null;
  Rect stickerRect = null;

  // 可视区域的宽高
  int viewWidth = 0;
  int viewHeight = 0;

  // 图片显示位置的左上角相对于view左上角的偏移量
  int offsetX = 0;
  int offsetY = 0;

  // 手指按下时的位置
  float downRawX, downRawY;
  // 手指按下的时间点
  long downTime;
  // 上一次手指所在的位置（以屏幕左上角为坐标原点）
  float lastRawX, lastRawY;
  // 上一次手指所在的位置（以相对的view左上角为坐标原点）
  float lastX, lastY;
  // 上一次两指之间的距离
  double lastDistance;
  // 初始化时的Sample Size
  int inSampleSize = 1;
  // 缩放比例
  double totalRatio = 1.0;

  /* Sticker */
  // 存储Sticker的List
  private ArrayList<Sticker> stickerList = new ArrayList<Sticker>();
  // 被选中的Sticker的下标
  int selectedStickerNum = -1;
  // 被选中的Sticker的前一个theata
  double lastStickerTheata;
  // 被选中的Sticker的前一个distance -- 触摸点距离中心点的距离
  double lastStickerDistance;
  // 触摸点是否在Sticker上
  boolean isOnSticker = false;

  /* Frame */
  private Frame frame;

  /* Text */
  // 存储Text的List
  private ArrayList<Text> textList = new ArrayList<Text>();
  // 被选中的Text的下标
  int selectedTextNum = -1;
  // 被选中的Text的前一个theata
  double lastTextTheata;
  // 被选中的Text的前一个distance -- 触摸点距中心点的距离
  double lastTextDistance;
  // 触摸点是否在Text上
  boolean isOnText = false;
  // 移动标志位
  boolean hasMove = false;

  /**
   * PictureView构造器
   * 
   * @param context
   * @param attrs
   */
  public PictureView(Context context, AttributeSet attrs)
  {
    super(context, attrs);

    this.context = context;

    init();
  }

  /**
   * 初始化PictureView
   */
  private void init()
  {
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setStrokeWidth(5);
    // 设置为只绘制边框
    paint.setStyle(Style.STROKE);
    paint.setColor(Color.WHITE);

    viewRect = new Rect();
    visibleRect = new Rect();
    displayRect = new Rect();
    drawingRect = new Rect();

    state = STATE_INIT;

    // 初始化Sticker和Text用到的控制按钮
    rotateBmp =
        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
            context.getResources(), R.drawable.rotate_btn_64), BMP_BUTTON_WH,
            BMP_BUTTON_WH, true);
    rotateBmpRect = new Rect();
    deleteBmp =
        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
            context.getResources(), R.drawable.delete_btn_64), BMP_BUTTON_WH,
            BMP_BUTTON_WH, true);
    deleteBmpRect = new Rect();
    resizeBmp =
        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
            context.getResources(), R.drawable.resize_btn_64), BMP_BUTTON_WH,
            BMP_BUTTON_WH, true);
    resizeBmpRect = new Rect();

    // 初始化相框，开始时不自带相框
    frame = new Frame(context, Frame.NONE);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right,
      int bottom)
  {
    super.onLayout(changed, left, top, right, bottom);

    // 计算View的宽高
    viewWidth = right - left;
    viewHeight = bottom - top;

    // 设置view的Rect
    viewRect.set(0, 0, viewWidth, viewHeight);
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    MyLog.i(TAG, "state="+state);
    if(photoPath != null)
    {
      Log.i(TAG, "state=" + state);
      switch(state)
      {

        case STATE_INIT:
          initDraw(canvas);
          break;

        case STATE_ZOOM:
          zoomDraw(canvas);
          break;

        case STATE_DRAG:
          dragDraw(canvas);
          break;

        case STATE_NORMAL:
        case ADD_STICKER:
        case DRAG_STICKER:
        case ROTATE_STICKER:
        case ADD_TEXT:
        case DRAG_TEXT:
        case ROTATE_TEXT:
        case RESIZE_TEXT:
        case EDIT_TEXT:
        case SET_FRAME:
        case SET_FILTER:
          drawElements(canvas);
          break;
      }

    }

  }

  private void drawFrameCorner(Canvas canvas, Bitmap corner, int x, int y)
  {
    corner =
        Bitmap.createScaledBitmap(corner, corner.getWidth() / inSampleSize,
            corner.getHeight() / inSampleSize, true);
    canvas.drawBitmap(corner, x, y, null);

    if(corner != null && !corner.isRecycled())
    {
      corner.recycle();
      corner = null;
    }
    System.gc();
  }

  private void drawFrameBorder(Canvas canvas, Bitmap border, int type, int x,
      int y, int count)
  {
    border =
        Bitmap.createScaledBitmap(border, border.getWidth() / inSampleSize,
            border.getHeight() / inSampleSize, true);

    for(int i = 0; i < count; i++)
    {
      if(type == Frame.LEFT_BORDER || type == Frame.RIGHT_BORDER)
      {
        canvas.drawBitmap(border, x, y + border.getHeight() * i, null);
      }
      else if(type == Frame.TOP_BORDER || type == Frame.BOTTOM_BORDER)
      {
        canvas.drawBitmap(border, x + border.getWidth() * i, y, null);
      }
    }

    if(border != null && !border.isRecycled())
    {
      border.recycle();
      border = null;
    }
    System.gc();
  }

  /**
   * 绘制各种元素到view上，包括照片，Sticker，Frame，Filter，Text
   * 
   * @param canvas
   */
  private void drawElements(Canvas canvas)
  {
    // 生成一个画布，用于绘制各种元素到内存
    Bitmap memBmp =
        Bitmap.createBitmap(srcBmp.getWidth(), srcBmp.getHeight(),
            Config.ARGB_8888);
    Canvas myCanvas = new Canvas(memBmp);

    // 绘制照片
    myCanvas.drawBitmap(srcBmp, 0, 0, null);

    // 绘制Sticker
    int stickerNum = -1;
    Sticker sticker = null;
    int len = stickerList.size();
    int left = 0;
    int top = 0;
    int right = 0;
    int bottom = 0;
    Rect stickerDrawingRect = new Rect();
    for(int i = 0; i < len; i++)
    {
      sticker = stickerList.get(i);

      // 生成Sticker相对于srcBmp的绘制区域 -- 以srcBmp的左上角为坐标原点
      left = Math.round(sticker.getLeft());
      top = Math.round(sticker.getTop());
      right = Math.round(sticker.getLeft() + sticker.getStickerRect().width());
      bottom = Math.round(sticker.getTop() + sticker.getStickerRect().height());
      stickerDrawingRect.set(left, top, right, bottom);
      sticker.setDrawingRect(stickerDrawingRect);

      myCanvas.save();
      // 旋转Sticker
      myCanvas.rotate(Math.round(Math.toDegrees(sticker.getStickerRotation())),
          stickerDrawingRect.centerX(), stickerDrawingRect.centerY());
      // 如果此Sticker被选中，绘制边框，记录下标
      if(sticker.isSelected())
      {
        stickerNum = i;

        paint.setStrokeWidth(5);
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.WHITE);
        myCanvas.drawRect(stickerDrawingRect, paint);
        paint.setColor(Color.YELLOW);
        paint.setStyle(Style.FILL);
      }
      // 绘制
      myCanvas.drawBitmap(sticker.getSticker(), sticker.getStickerRect(),
          stickerDrawingRect, null);
      myCanvas.restore();
    }

    // 绘制Text
    Text text = null;
    len = textList.size();
    int textNum = -1;
    TextPaint textPaint = null;
    Rect textDrawingRect = new Rect();
    for(int i = 0; i < len; i++)
    {
      text = textList.get(i);
      textPaint = text.getTextPaint();
      /*
       * StaticLayout可以定义一个宽度固定的layout，用于绘制文字，实现自动换行
       * StaticLayout(CharSequence source, TextPaint paint, int width,
       * Layout.Alignment align, float spacingmult, float spacingadd, boolean
       * includepad)
       * source: 文字
       * paint: 绘制文字的画笔
       * width: StaticLayout的宽度
       * align: 文字的对齐方式
       * spacingmult: factor by which to scale the font size to get the default
       * line spacing
       * spacingadd: amount to add to the default line spacing
       * includepad:
       */
      StaticLayout staticLayout =
          new StaticLayout(text.getText(), textPaint, text.getWidth()
              / inSampleSize, Alignment.ALIGN_NORMAL, text.getSpacingmult(),
              text.getSpacingadd(), true);
      text.setHeight(staticLayout.getHeight());

      // 生成Text相对于srcBmp的绘制区域 -- 以srcBmp的左上角为坐标原点
      textDrawingRect.set(0, 0, staticLayout.getWidth(),
          staticLayout.getHeight());
      textDrawingRect.offset(Math.round(text.getLeft()),
          Math.round(text.getTop() - staticLayout.getHeight() * 0.5f));
      text.setDrawingRect(textDrawingRect);

      myCanvas.save();
      // StaticLayout默认绘制在(0, 0)处，所以先平移
      myCanvas.translate(text.getLeft(),
          text.getTop() - staticLayout.getHeight() * 0.5f);
      // 旋转画布
      myCanvas.rotate(
          Math.round(Math.toDegrees(text.getTextRotation())),
          textDrawingRect.exactCenterX() - text.getLeft(),
          textDrawingRect.exactCenterY()
              - (text.getTop() - staticLayout.getHeight() * 0.5f));
      // 绘制StaticLayout
      staticLayout.draw(myCanvas);
      myCanvas.restore();

      // 如果此Text被选中，绘制边框，记录下标
      if(text.isSelected())
      {
        textNum = i;

        paint.setStrokeWidth(5);
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.WHITE);
        myCanvas.save();
        myCanvas.rotate(Math.round(Math.toDegrees(text.getTextRotation())),
            textDrawingRect.exactCenterX(), textDrawingRect.exactCenterY());
        myCanvas.drawRect(textDrawingRect, paint);
        myCanvas.restore();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Style.FILL);
      }
    }

    // 绘制相框
    if(frame.getType() != Frame.NONE)
    {
      // 绘制相框的角
      drawFrameCorner(myCanvas, frame.getCornerLT(), 0, 0);
      drawFrameCorner(myCanvas, frame.getCornerRT(), srcBmp.getWidth()
          - frame.getCornerRT().getWidth() / inSampleSize, 0);
      drawFrameCorner(myCanvas, frame.getCornerRB(), srcBmp.getWidth()
          - frame.getCornerRB().getWidth() / inSampleSize, srcBmp.getHeight()
          - frame.getCornerRB().getHeight() / inSampleSize);
      drawFrameCorner(myCanvas, frame.getCornerLB(), 0, srcBmp.getHeight()
          - frame.getCornerLB().getHeight() / inSampleSize);

      int length = 0;
      int count = 0;
      // 绘制相框的边框
      length =
          srcBmp.getHeight() - 2 * frame.getCornerLT().getHeight()
              / inSampleSize;
      count = length / (frame.getBorderL().getHeight() / inSampleSize);
      count +=
          length % (frame.getBorderL().getHeight() / inSampleSize) == 0 ? 0 : 1;
      drawFrameBorder(myCanvas, frame.getBorderL(), Frame.LEFT_BORDER, 0, frame
          .getCornerLT().getHeight() / inSampleSize, count);
      drawFrameBorder(myCanvas, frame.getBorderR(), Frame.RIGHT_BORDER,
          srcBmp.getWidth() - frame.getBorderR().getWidth() / inSampleSize,
          frame.getCornerRT().getHeight() / inSampleSize, count);
      length =
          srcBmp.getWidth() - 2 * frame.getCornerLT().getWidth() / inSampleSize;
      count = length / (frame.getBorderT().getWidth() / inSampleSize);
      count +=
          length % (frame.getBorderT().getWidth() / inSampleSize) == 0 ? 0 : 1;
      drawFrameBorder(myCanvas, frame.getBorderB(), Frame.BOTTOM_BORDER, frame
          .getCornerRB().getWidth() / inSampleSize, srcBmp.getHeight()
          - frame.getBorderB().getHeight() / inSampleSize, count);
      drawFrameBorder(myCanvas, frame.getBorderT(), Frame.TOP_BORDER, frame
          .getCornerLT().getWidth() / inSampleSize, 0, count);
    }

    // 将绘制在内存的Bitmap绘制到屏幕上
    canvas.drawBitmap(memBmp, displayRect, drawingRect, null);

    if(memBmp != null && !memBmp.isRecycled())
    {
      memBmp.recycle();
      memBmp = null;
    }
    System.gc();

    // 如果有被选中的Sticker，绘制控制按钮
    if(stickerNum != -1)
    {
      drawButton(canvas, DRAW_CANCEL_BUTTON, stickerList.get(stickerNum)
          .getLeft(), stickerList.get(stickerNum).getTop(),
          stickerList.get(stickerNum).getDrawingRect());
      drawButton(canvas, DRAW_ROTATE_BUTTON, stickerList.get(stickerNum)
          .getLeft(), stickerList.get(stickerNum).getTop(),
          stickerList.get(stickerNum).getDrawingRect());
    }

    // 如果有被选中的文字，绘制控制按钮
    if(textNum != -1)
    {
      MyLog.i(TAG, "textNum=" + textNum);
      drawButton(canvas, DRAW_CANCEL_BUTTON, textList.get(textNum).getLeft(),
          textList.get(textNum).getTop() - textList.get(textNum).getHeight()
              * 0.5f, textList.get(textNum).getDrawingRect());
      drawButton(canvas, DRAW_ROTATE_BUTTON, textList.get(textNum).getLeft(),
          textList.get(textNum).getTop() - textList.get(textNum).getHeight()
              * 0.5f, textList.get(textNum).getDrawingRect());
      drawButton(canvas, DRAW_RESIZE_BUTTON, textList.get(textNum).getLeft(),
          textList.get(textNum).getTop() - textList.get(textNum).getHeight()
              * 0.5f, textList.get(textNum).getDrawingRect());
    }
  }

  /**
   * 绘制控制按钮
   * 
   * @param canvas 画布
   * @param buttonType 按钮的类型
   * @param left 按钮的左边线的位置
   * @param top 按钮的上边线的位置
   * @param rect 按钮将要被绘制的矩形位置
   */
  private void drawButton(Canvas canvas, int buttonType, float left, float top,
      Rect rect)
  {
    int centerX = 0;
    int centerY = 0;
    // 根据不同的按钮类型确定中心点位置
    if(buttonType == DRAW_CANCEL_BUTTON || buttonType == DRAW_ROTATE_BUTTON)
    {
      centerX =
          Math.round((left + rect.width() - displayRect.left)
              * (1f * drawingRect.width() / displayRect.width()))
              + drawingRect.left;
    }
    else if(buttonType == DRAW_RESIZE_BUTTON)
    {
      centerX =
          Math.round((left - displayRect.left)
              * (1f * drawingRect.width() / displayRect.width()))
              + drawingRect.left;
    }
    if(buttonType == DRAW_CANCEL_BUTTON)
    {
      centerY =
          Math.round((top - displayRect.top)
              * (1f * drawingRect.height() / displayRect.height()))
              + drawingRect.top;
    }
    else if(buttonType == DRAW_ROTATE_BUTTON
        || buttonType == DRAW_RESIZE_BUTTON)
    {
      centerY =
          Math.round((top + rect.height() - displayRect.top)
              * (1f * drawingRect.height() / displayRect.height()))
              + drawingRect.top;
    }
    // 根据按钮的类型绘制相应的按钮
    switch(buttonType)
    {
      case DRAW_CANCEL_BUTTON:
        canvas.drawBitmap(deleteBmp, centerX - BMP_BUTTON_WH / 2, centerY
            - BMP_BUTTON_WH / 2, null);
        deleteBmpRect.set(centerX - BMP_BUTTON_WH / 2, centerY - BMP_BUTTON_WH
            / 2, centerX + BMP_BUTTON_WH / 2, centerY + BMP_BUTTON_WH / 2);
        break;
      case DRAW_ROTATE_BUTTON:
        canvas.drawBitmap(rotateBmp, centerX - BMP_BUTTON_WH / 2, centerY
            - BMP_BUTTON_WH / 2, null);
        rotateBmpRect.set(centerX - BMP_BUTTON_WH / 2, centerY - BMP_BUTTON_WH
            / 2, centerX + BMP_BUTTON_WH / 2, centerY + BMP_BUTTON_WH / 2);
        break;
      case DRAW_RESIZE_BUTTON:
        canvas.drawBitmap(resizeBmp, centerX - BMP_BUTTON_WH / 2, centerY
            - BMP_BUTTON_WH / 2, null);
        resizeBmpRect.set(centerX - BMP_BUTTON_WH / 2, centerY - BMP_BUTTON_WH
            / 2, centerX + BMP_BUTTON_WH / 2, centerY + BMP_BUTTON_WH / 2);
        break;

      default:
        break;
    }

  }

  /**
   * 建立大小合适的照片来进行编辑
   */
  private void setupSrcBmp()
  {
    if(srcBmp != null && !srcBmp.isRecycled())
    {
      srcBmp.recycle();
      srcBmp = null;
    }
    System.gc();

    // 加载前计算缩放比例inSampleSize
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;

    BitmapFactory.decodeFile(getPhotoPath(), options);
    int outHeight = options.outHeight;

    // 计算缩放比例
    if(outHeight > PIXEL_1080)
    {
      inSampleSize = outHeight / PIXEL_1080;
      if(inSampleSize % 2 != 0)
      {
        inSampleSize -= 1;
      }
    }

    // 加载缩放后的图片
    options.inSampleSize = inSampleSize;
    options.inJustDecodeBounds = false;
    srcBmp = BitmapFactory.decodeFile(getPhotoPath(), options);

    /*
     * 由于Samsun手机的默认照相机拍出来的照片会自动旋转90度，为了这类将照片
     * 正常显示，在这里读取照片的EXIF信息，获取旋转的角度，将照片需旋转回来
     */
    int degree = ImageUtils.getRotationDegree(context, getPhotoPath());
    srcBmp = ImageUtils.rotateBitmap(srcBmp, degree);
  }

  /**
   * 初始化图像，并绘制到view
   * 
   * @param canvas
   */
  private void initDraw(Canvas canvas)
  {
    // 设置srcBmp
    setupSrcBmp();

    // 设置visible的Rect
    if(srcBmp.getWidth() > srcBmp.getHeight())
    {
      visibleRect.set(0, 0, srcBmp.getWidth(), viewHeight * srcBmp.getWidth()
          / viewWidth);
    }
    else
    {
      visibleRect.set(0, 0, viewWidth * srcBmp.getHeight() / viewHeight,
          srcBmp.getHeight());
    }

    // 设置照片需要显示的区域
    displayRect.set(0, 0, srcBmp.getWidth(), srcBmp.getHeight());

    setDisplayRect(0, 0);

    setViewDrawingRect(displayRect.width(), displayRect.height());

    drawElements(canvas);
  }

  /**
   * 绘制拖动后的图像
   * 
   * @param canvas
   */
  private void dragDraw(Canvas canvas)
  {
    setDisplayRect(visibleRect.left, visibleRect.top);

    setViewDrawingRect(displayRect.width(), displayRect.height());

    drawElements(canvas);
  }

  /**
   * 绘制缩放后的图像
   * 
   * @param canvas
   */
  private void zoomDraw(Canvas canvas)
  {
    setDisplayRect(visibleRect.left, visibleRect.top);

    Log.i(TAG, "display=" + displayRect.toShortString());

    setViewDrawingRect(displayRect.width(), displayRect.height());

    drawElements(canvas);
  }

  /**
   * 设置DisplayRect
   * 
   * @param left displayRect的左边
   * @param top displayRect的上边
   */
  private void setDisplayRect(int left, int top)
  {
    int displayW = 0, displayH = 0;
    if(visibleRect.width() <= srcBmp.getWidth())
    {
      displayW = visibleRect.width();
    }
    else
    {
      displayW = srcBmp.getWidth();
    }
    if(visibleRect.height() <= srcBmp.getHeight())
    {
      displayH = visibleRect.height();
    }
    else
    {
      displayH = srcBmp.getHeight();
    }
    displayRect.set(left, top, left + displayW, top + displayH);
  }

  /**
   * 设置View的绘制区域
   * 
   * @param displayW
   * @param displayH
   */
  private void setViewDrawingRect(int displayW, int displayH)
  {
    if(srcBmp.getWidth() > srcBmp.getHeight())
    {
      drawingRect.set(0, 0, displayW * viewRect.height() / displayH,
          viewRect.height());
      int drawingRectW = viewRect.width();
      int drawingRectH = displayH * viewRect.width() / displayW;
      int drawingRectL =
          viewRect.width() - drawingRectW > 0 ? Math
              .round((viewRect.width() - drawingRectW) * 0.5f) : 0;
      int drawingRectT =
          viewRect.height() - drawingRectH > 0 ? Math
              .round((viewRect.height() - drawingRectH) * 0.5f) : 0;
      int drawingRectR = drawingRectL + viewRect.width();
      int drawingRectB = drawingRectT + displayH * viewRect.width() / displayW;
      drawingRect.set(drawingRectL, drawingRectT, drawingRectR, drawingRectB);
      offsetX = drawingRectL;
      offsetY = drawingRectT;
    }
    else
    {
      drawingRect.set(0, 0, displayW * viewRect.height() / displayH,
          viewRect.height());
      int drawingRectW = displayW * viewRect.height() / displayH;
      int drawingRectH = viewRect.height();
      int drawingRectL =
          viewRect.width() - drawingRectW > 0 ? Math
              .round((viewRect.width() - drawingRectW) * 0.5f) : 0;
      int drawingRectT =
          viewRect.height() - drawingRectH > 0 ? Math
              .round((viewRect.height() - drawingRectH) * 0.5f) : 0;
      int drawingRectR = drawingRectL + displayW * viewRect.height() / displayH;
      int drawingRectB = drawingRectT + viewRect.height();
      drawingRect.set(drawingRectL, drawingRectT, drawingRectR, drawingRectB);
      offsetX = drawingRectL;
      offsetY = drawingRectT;
    }
  }

  /**
   * 手指按下时触发的事件
   * 
   * @param event
   */
  private void downAction(MotionEvent event)
  {
    // 记录手指按下时的信息
    downTime = System.currentTimeMillis();
    downRawX = event.getRawX();
    downRawY = event.getRawY();
    lastRawX = downRawX;
    lastRawY = downRawY;
    lastX = event.getX();
    lastY = event.getY();

    isOnSticker = false;
    isOnText = false;

    // 将触摸点的坐标转换成canvas的坐标
    float cDownX =
        event.getX() * displayRect.width() / drawingRect.width()
            + displayRect.left;
    float cDownY =
        event.getY() * displayRect.height() / drawingRect.height()
            + displayRect.top;
    Log.i(TAG, "cx=" + cDownX + ", cy=" + cDownY);

    // 判断触摸点是否落在Text上
    int len = textList.size();
    Text text = null;
    for(int i = 0; i < len; i++)
    {
      text = textList.get(i);
      textRect = text.getDrawingRect();
      textRect.set(textRect.left - BMP_BUTTON_WH / 2, textRect.top
          - BMP_BUTTON_WH / 2, textRect.right + BMP_BUTTON_WH / 2,
          textRect.bottom + BMP_BUTTON_WH / 2);
      textRect.offset(offsetX * displayRect.width() / drawingRect.width()
          + displayRect.left,
          offsetY * displayRect.height() / drawingRect.height()
              + displayRect.top);

      // 判断触摸点是否在旋转按钮上
      if(text.isSelected()
          && rotateBmpRect.contains(Math.round(event.getX()),
              Math.round(event.getY())))
      {
        isOnText = true;
        state = ROTATE_TEXT;

        lastTextDistance =
            getDistance(cDownX, cDownY, textRect.exactCenterX(),
                textRect.exactCenterY());

        lastTextTheata = Math.atan2(cDownY, cDownX);
      }
      // 判断触摸点是否在调整字体大小按钮上
      else if(text.isSelected()
          && resizeBmpRect.contains(Math.round(event.getX()),
              Math.round(event.getY())))
      {
        isOnText = true;
        state = RESIZE_TEXT;

        lastTextDistance =
            getDistance(cDownX, cDownY, textRect.exactCenterX(),
                textRect.exactCenterY());

        lastTextTheata = Math.atan2(cDownY, cDownX);
      }
      // 判断触摸点是否在删除按钮上
      else if(text.isSelected()
          && deleteBmpRect.contains(Math.round(event.getX()),
              Math.round(event.getY())))
      {
        textList.remove(selectedTextNum);
      }
      // 判断触摸点是否在Text上
      else if(textRect.contains(Math.round(cDownX), Math.round(cDownY)))
      {
        // 如果Text之前没有被选中，选中Text
        if(!text.isSelected())
        {
          text.setSelected(true);
          selectedTextNum = i;
          state = DRAG_TEXT;
          isOnText = true;
        }
        // 如果Text之前已经被选中，进入Edit_Text模式
        else
        {
          state = EDIT_TEXT;
          isOnText = true;
        }
      }
      // 如果触摸点不在Text的范围内，设为非选中状态
      else
      {
        text.setSelected(false);
      }
    }

    len = stickerList.size();
    Sticker sticker = null;
    for(int i = 0; i < len; i++)
    {
      sticker = stickerList.get(i);
      stickerRect = sticker.getDrawingRect();
      stickerRect.set(stickerRect.left - BMP_BUTTON_WH / 2, stickerRect.top
          - BMP_BUTTON_WH / 2, stickerRect.right + BMP_BUTTON_WH / 2,
          stickerRect.bottom + BMP_BUTTON_WH / 2);
      stickerRect.offset(offsetX * displayRect.width() / drawingRect.width()
          + displayRect.left,
          offsetY * displayRect.height() / drawingRect.height()
              + displayRect.top);

      // 判断触摸点是否在旋转按钮上
      if(sticker.isSelected()
          && rotateBmpRect.contains(Math.round(event.getX()),
              Math.round(event.getY())))
      {
        isOnSticker = true;
        state = ROTATE_STICKER;

        lastStickerDistance =
            getDistance(cDownX, cDownY,
                stickerRect.exactCenterX(), stickerRect.exactCenterY());

        lastStickerTheata = Math.atan2(cDownY, cDownX);
      }
      // 判断触摸点是否在删除按钮上
      else if(sticker.isSelected()
          && deleteBmpRect.contains(Math.round(event.getX()),
              Math.round(event.getY())))
      {
        stickerList.remove(selectedStickerNum);
      }
      // 判断触摸点是否在sticker上
      else if(stickerRect.contains(Math.round(cDownX), Math.round(cDownY)))
      {
        sticker.setSelected(true);
        isOnSticker = true;
        state = DRAG_STICKER;
        selectedStickerNum = i;
      }
      else
      {
        sticker.setSelected(false);
      }
    }

    Log.i(TAG, "state=" + state);
    invalidate();

    // 如果触摸点不在sticker和text上，设置为STATE_DRAG
    if(!isOnSticker && !isOnText)
    {
      state = STATE_DRAG;
    }
  }

  /**
   * 拖动照片
   * 
   * @param event
   */
  private void dragPhotoAction(MotionEvent event)
  {
    int newLeft = visibleRect.left;
    int newRight = visibleRect.right;
    int newTop = visibleRect.top;
    int newBottom = visibleRect.bottom;

    int dx = (int) (event.getRawX() - lastRawX);
    int dy = (int) (event.getRawY() - lastRawY);
    if(Math.abs(dx) > 5 || Math.abs(dy) > 5)
    {

      // 如果visibleRect的宽小于照片的宽，不允许将visibleRect拖出照片的范围
      if(visibleRect.width() < srcBmp.getWidth())
      {
        if(dx < 0)
        {
          if(visibleRect.right - dx > srcBmp.getWidth())
          {
            newRight = srcBmp.getWidth();
          }
          else
          {
            newRight -= dx;
          }
          newLeft = newRight - visibleRect.width();
        }
        else
        {
          if(visibleRect.left - dx < 0)
          {
            newLeft = 0;
          }
          else
          {
            newLeft -= dx;
          }
          newRight = newLeft + visibleRect.width();
        }
      }

      // 如果visibleRect的高小于照片的高，不允许将visibleRect拖出照片的范围
      if(visibleRect.height() < srcBmp.getHeight())
      {
        if(dy < 0)
        {
          if(visibleRect.bottom - dy > srcBmp.getHeight())
          {
            newBottom = srcBmp.getHeight();
          }
          else
          {
            newBottom -= dy;
          }
          newTop = newBottom - visibleRect.height();
        }
        else
        {
          if(visibleRect.top - dy < 0)
          {
            newTop = 0;
          }
          else
          {
            newTop -= dy;
          }
          newBottom = newTop + visibleRect.height();
        }
      }

      visibleRect.set(newLeft, newTop, newRight, newBottom);
      invalidate();

      lastRawX = event.getRawX();
      lastRawY = event.getRawY();
      lastX = event.getX();
      lastY = event.getY();
    }
  }

  /**
   * 缩放照片
   * 
   * @param event
   */
  private void zoomPhotoAction(MotionEvent event)
  {
    // 有两个手指触摸时才进行缩放操作
    if(event.getPointerCount() == 2)
    {
      double currentDistance =
          getDistance(event.getX(0), event.getY(0), event.getX(1),
              event.getY(1));

      if(Math.abs(currentDistance - lastDistance) > 5)
      {
        int newLeft = visibleRect.left;
        int newTop = visibleRect.top;

        // 计算缩放比例
        double ratio = currentDistance / lastDistance;

        int visibleW = (int) (visibleRect.width() / ratio);
        int visibleH = (int) (visibleRect.height() / ratio);

        if(srcBmp.getWidth() > srcBmp.getHeight())
        {
          /*
           * 如果照片的宽大于高，不允许visibleRect的宽大于照片的宽，但允许
           * visibleRect的高大于照片的高。这样做是为了保持visibleRect的宽
           * 高比例与view的宽高比例一致
           */
          if(visibleW > srcBmp.getWidth())
          {
            visibleW = srcBmp.getWidth();
            visibleH = visibleW * viewRect.height() / viewRect.width();
          }
        }
        else if(srcBmp.getHeight() > srcBmp.getWidth())
        {
          /*
           * 如果照片的高大于宽，不允许visibleRect的高大于照片的高，但允许
           * visibleRect的宽大于照片的宽。这样做是为了保持visibleRect的宽
           * 高比例与view的宽高比例一致
           */
          if(visibleH > srcBmp.getHeight())
          {
            visibleH = srcBmp.getHeight();
            visibleW = visibleH * viewRect.width() / viewRect.height();
          }
        }

        /*
         * 如果visibleRect的left大于0，并且横向超出了照片的范围，调整
         * visibleRect的位置
         */
        if(visibleRect.left + visibleW > srcBmp.getWidth()
            && visibleRect.left > 0)
        {
          newLeft =
              visibleRect.left
                  - (visibleRect.left + visibleW - srcBmp.getWidth());
          if(newLeft < 0)
          {
            newLeft = 0;
          }
        }

        /*
         * 如果visibleRect的top大于0，并且纵向超出了照片的范围，调整
         * visibleRect的位置
         */
        if(visibleRect.top + visibleH > srcBmp.getHeight()
            && visibleRect.top > 0)
        {
          newTop =
              visibleRect.top
                  - (visibleRect.top + visibleH - srcBmp.getHeight());
          if(newTop < 0)
          {
            newTop = 0;
          }
        }
        visibleRect.set(newLeft, newTop, newLeft + visibleW, newTop + visibleH);
        Log.i(TAG, "visibleRect=" + visibleRect.toShortString());

        invalidate();

        totalRatio = ratio;
        lastDistance = currentDistance;
      }
    }
  }

  /**
   * 拖动Sticker
   * 
   * @param event
   */
  private void dragElementAction(MotionEvent event, int state)
  {
    int dx =
        Math.round((event.getRawX() - lastRawX) * 1f * displayRect.width()
            / drawingRect.width());
    int dy =
        Math.round((event.getRawY() - lastRawY) * 1f * displayRect.height()
            / drawingRect.height());

    if(state == DRAG_STICKER)
    {
      Sticker sticker = stickerList.get(selectedStickerNum);
      sticker.setLeft(sticker.getLeft() + dx);
      sticker.setTop(sticker.getTop() + dy);
      MyLog.i(TAG, "sticker left=" + sticker.getLeft());
      MyLog.i(TAG, "sticker top=" + sticker.getTop());
    }
    else if(state == DRAG_TEXT || state == EDIT_TEXT)
    {
      hasMove = true;
      Text text = textList.get(selectedTextNum);
      text.setLeft(text.getLeft() + dx);
      text.setTop(text.getTop() + dy);
      MyLog.i(TAG, "text left=" + text.getLeft());
      MyLog.i(TAG, "text top=" + text.getTop());
    }

    invalidate();

    lastRawX = event.getRawX();
    lastRawY = event.getRawY();
    lastX = event.getX();
    lastY = event.getY();
  }

  /**
   * 旋转Sticker, Text
   * 
   * @param event
   */
  private void rotateElementAction(MotionEvent event, int state)
  {
    float cMoveX =
        event.getX() * 1f * displayRect.width() / drawingRect.width()
            + displayRect.left;
    float cMoveY =
        event.getY() * 1f * displayRect.height() / drawingRect.height()
            + displayRect.top;

    if(state == ROTATE_STICKER)
    {
      Sticker sticker = stickerList.get(selectedStickerNum);
      double stickerDistance =
          getDistance(cMoveX, cMoveY, stickerRect.exactCenterX(),
              stickerRect.exactCenterY());

      // 计算缩放比例
      if(Math.abs(stickerDistance - lastStickerDistance) > 5)
      {
        double stickerZoom = stickerDistance / lastStickerDistance;
        try
        {
          sticker.setStickerZoom(stickerZoom);
          lastStickerDistance = stickerDistance;
        }
        catch(IOException e)
        {
          e.printStackTrace();
        }
      }

      // 计算旋转角度
      double theata =
          Math.atan2(cMoveY - sticker.getDrawingRect().exactCenterY(), cMoveX
              - sticker.getDrawingRect().exactCenterX());
      sticker.setStickerRotation(theata - lastStickerTheata);
      lastStickerTheata = theata;
      // 如果旋转角度小于0，把角度转换到大于0，小于360
      if(sticker.getStickerRotation() < 0)
      {
        while(sticker.getStickerRotation() < 0)
        {
          sticker.setStickerRotation(2 * Math.PI);
        }
      }
      // 如果旋转角度大于360，把角度转换到大于0，小于360
      else if(sticker.getStickerRotation() > 2 * Math.PI)
      {
        while(sticker.getStickerRotation() > 2 * Math.PI)
        {
          sticker.setStickerRotation(-2 * Math.PI);
        }
      }
    }
    else if(state == ROTATE_TEXT)
    {
      Text text = textList.get(selectedTextNum);
      double textDistance =
          Math.sqrt(Math.pow(cMoveX - text.getDrawingRect().exactCenterX(), 2)
              + Math.pow(cMoveY - text.getDrawingRect().exactCenterY(), 2));
      // 计算缩放比例
      if(Math.abs(textDistance - lastStickerDistance) > 5)
      {
        double textZoom = textDistance / lastTextDistance;
        text.setTextZoom(textZoom);
        lastTextDistance = textDistance;
      }

      // 计算旋转角度
      double theata =
          Math.atan2(cMoveY - text.getDrawingRect().exactCenterY(), cMoveX
              - text.getDrawingRect().exactCenterX());
      text.setTextRotation(theata - lastTextTheata);
      lastTextTheata = theata;
      // 如果旋转角度小于0，把角度转换到大于0，小于360
      if(text.getTextRotation() < 0)
      {
        while(text.getTextRotation() < 0)
        {
          text.setTextRotation(2 * Math.PI);
        }
      }
      // 如果旋转角度大于360，把角度转换到大于0，小于360
      else if(text.getTextRotation() > 2 * Math.PI)
      {
        while(text.getTextRotation() > 2 * Math.PI)
        {
          text.setTextRotation(-2 * Math.PI);
        }
      }
    }

    invalidate();

    lastRawX = event.getRawX();
    lastRawY = event.getRawY();
    lastX = event.getX();
    lastY = event.getY();
  }

  /**
   * 调整文字大小
   * 
   * @param event
   */
  private void resizeTextAction(MotionEvent event)
  {

    float cMoveX =
        event.getX() * 1f * displayRect.width() / drawingRect.width()
            + displayRect.left;
    float cMoveY =
        event.getY() * 1f * displayRect.height() / drawingRect.height()
            + displayRect.top;
    double textDistance =
        getDistance(cMoveX, cMoveY, textList.get(selectedTextNum)
            .getDrawingRect().exactCenterX(), textList.get(selectedTextNum)
            .getDrawingRect().exactCenterY());

    if(Math.abs(textDistance - lastStickerDistance) > 5)
    {
      double textSizeRatio = textDistance / lastTextDistance;
      textList.get(selectedTextNum).setTextSizeRatio(textSizeRatio);
      lastTextDistance = textDistance;
    }

    invalidate();

    lastRawX = event.getRawX();
    lastRawY = event.getRawY();
    lastX = event.getX();
    lastY = event.getY();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    switch(event.getActionMasked())
    {
      case MotionEvent.ACTION_DOWN:
        downAction(event);
        break;

      case MotionEvent.ACTION_POINTER_DOWN:
        if(event.getPointerCount() == 2)
        {
          lastDistance =
              getDistance(event.getX(0), event.getY(0), event.getX(1),
                  event.getY(1));
          state = STATE_ZOOM;
        }
        break;

      case MotionEvent.ACTION_MOVE:
        if(state == STATE_DRAG)
        {
          dragPhotoAction(event);
        }
        else if(state == STATE_ZOOM)
        {
          zoomPhotoAction(event);
        }
        else if(state == DRAG_STICKER)
        {
          dragElementAction(event, DRAG_STICKER);
        }
        else if(state == ROTATE_STICKER)
        {
          rotateElementAction(event, ROTATE_STICKER);
        }
        else if(state == DRAG_TEXT || state == EDIT_TEXT)
        {
          dragElementAction(event, state);
        }
        else if(state == ROTATE_TEXT)
        {
          rotateElementAction(event, ROTATE_TEXT);
        }
        else if(state == RESIZE_TEXT)
        {
          resizeTextAction(event);
        }
        break;

      case MotionEvent.ACTION_UP:
        if(state == EDIT_TEXT)
        {
          if(!hasMove)
          {
            Intent intent = new Intent();
            intent.setClass(context, EditTextActivity.class);
            intent.putExtra(PictureEditorActivity.BUNDLE_TEXT,
                textList.get(selectedTextNum).getText());
            activity.startActivityForResult(intent,
                PictureEditorActivity.REQUEST_CODE_EDIT_TEXT);
          }
        }
        hasMove = false;
        state = STATE_NORMAL;
        break;
    }
    return true;
  }

  /**
   * 计算(x1, y1)和(x2, y2)之间的距离
   * 
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   * @return
   */
  private double getDistance(float x1, float y1, float x2, float y2)
  {
    return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
  }

  /* Coloring */
  /**
   * 设置照片滤镜
   * 
   * @param filter
   */
  public void setFilter(int filter)
  {
    if(Filter.currentColor != filter)
    {
      state = SET_FILTER;
      setupSrcBmp();
      srcBmp = Filter.setFilter(srcBmp, filter);
    }
  }

  /* Sticker */
  /**
   * 添加Sticker
   * 
   * @param type
   * @throws IOException
   */
  public void addSticker(int type) throws IOException
  {
    Sticker sticker =
        new Sticker(context, type, displayRect.centerX(),
            displayRect.centerY(), inSampleSize);
    stickerList.add(sticker);
    selectedStickerNum = stickerList.size() - 1;
  }

  /* Text */
  /**
   * 添加文字
   * 
   * @param textType
   * @param color
   */
  public void addText(Typeface textType, int color)
  {
    MyLog.i(TAG, "addText");
    Text text =
        new Text(context, textType, color, displayRect.centerX(),
            displayRect.centerY(), inSampleSize);
    textList.add(text);
    selectedTextNum = textList.size() - 1;
    state = ADD_TEXT;
  }

  /* Frame */
  /**
   * 设置相框
   * 
   * @param type
   * @throws IOException
   */
  public void setFrame(int type)
  {
    frame.setFrame(type);
    state = SET_FRAME;
  }

  /**
   * @return the photoPath
   */
  public String getPhotoPath()
  {
    return photoPath;
  }

  /**
   * @param photoPath the photoPath to set
   */
  public void setPhotoPath(String photoPath)
  {
    this.photoPath = photoPath;
  }

  /**
   * 设置当前PictureView所在的Activity
   * 
   * @param activity
   */
  public void setCurrentActivity(Activity activity)
  {
    this.activity = activity;
  }

  /**
   * 设置Text的文字为EditTextActivity返回的值
   * 
   * @param text
   */
  public void setText(String text)
  {
    textList.get(selectedTextNum).setText(text);
  }

  /**
   * 释放资源
   */
  private void release()
  {
    if(rotateBmp != null && !rotateBmp.isRecycled())
    {
      rotateBmp.recycle();
      rotateBmp = null;
    }
    if(deleteBmp != null && !deleteBmp.isRecycled())
    {
      deleteBmp.recycle();
      deleteBmp = null;
    }
    if(resizeBmp != null && !resizeBmp.isRecycled())
    {
      resizeBmp.recycle();
      resizeBmp = null;
    }
    System.gc();
  }

  /**
   * 释放照片
   */
  public void releasePhoto()
  {
    if(srcBmp != null && !srcBmp.isRecycled())
    {
      srcBmp.recycle();
      srcBmp = null;
    }
  }

  /**
   * 取消所有编辑
   */
  public void cancel()
  {
    int len = stickerList.size();
    for(int i = 0; i < len; i++)
    {
      stickerList.get(i).release();
    }
    stickerList.clear();
    textList.clear();
    setFilter(Filter.NO_COLOR);
    setFrame(Frame.NONE);
  }

  /**
   * 保存
   * 
   * @param dirPath 保存的文件夹路径
   * @return
   */
  public String save(String dirPath)
  {
    int len = 0;
    int srcWidth = 0;
    int srcHeight = 0;

    // 绘制照片
    Bitmap memBmp =
        Bitmap.createBitmap(srcBmp.getWidth(), srcBmp.getHeight(),
            Config.ARGB_8888);
    Canvas myCanvas = new Canvas(memBmp);
    myCanvas.drawBitmap(srcBmp, 0, 0, null);
    srcWidth = srcBmp.getWidth();
    srcHeight = srcBmp.getHeight();

    if(srcBmp != null && !srcBmp.isRecycled())
    {
      srcBmp.recycle();
      srcBmp = null;
    }
    System.gc();

    // 绘制所有Sticker
    Sticker sticker = null;
    len = stickerList.size();
    for(int i = 0; i < len; i++)
    {
      sticker = stickerList.get(i);

      // 设置Sticker的绘制区域
      int left = Math.round(sticker.getLeft());
      int top = Math.round(sticker.getTop());
      int right =
          Math.round(sticker.getLeft() + sticker.getStickerRect().width());
      int bottom =
          Math.round(sticker.getTop() + sticker.getStickerRect().height());
      Rect stickerDrawingRect = new Rect(left, top, right, bottom);
      sticker.setDrawingRect(stickerDrawingRect);
      Log.i(TAG, "sticker rect=" + sticker.getStickerRect().toShortString());
      Log.i(TAG, "rect=" + sticker.getDrawingRect().toShortString());

      myCanvas.save();
      // 旋转Sticker
      myCanvas.rotate(Math.round(Math.toDegrees(sticker.getStickerRotation())),
          stickerDrawingRect.centerX(), stickerDrawingRect.centerY());
      // 绘制Sticker
      myCanvas.drawBitmap(sticker.getSticker(), sticker.getStickerRect(),
          stickerDrawingRect, null);
      myCanvas.restore();

      sticker.release();
    }

    // 绘制所有Text
    Text text = null;
    len = textList.size();
    TextPaint textPaint = null;
    Rect textDrawingRect = new Rect();
    for(int i = 0; i < len; i++)
    {
      text = textList.get(i);
      textPaint = text.getTextPaint();
      // 创建一个带文字的StaticLayout
      StaticLayout staticLayout =
          new StaticLayout(text.getText(), textPaint, text.getWidth()
              / inSampleSize, Alignment.ALIGN_NORMAL, 1.5f, 0.0f, true);
      text.setHeight(staticLayout.getHeight());
      // 设置Text的绘制区域
      textDrawingRect.set(0, 0, staticLayout.getWidth(),
          staticLayout.getHeight());
      textDrawingRect.offset(Math.round(text.getLeft()),
          Math.round(text.getTop() - staticLayout.getHeight() * 0.5f));
      text.setDrawingRect(textDrawingRect);

      myCanvas.save();
      // 平移StaticLayout
      myCanvas.translate(text.getLeft(),
          text.getTop() - staticLayout.getHeight() * 0.5f);
      // 旋转
      myCanvas.rotate(
          Math.round(Math.toDegrees(text.getTextRotation())),
          textDrawingRect.exactCenterX() - text.getLeft(),
          textDrawingRect.exactCenterY()
              - (text.getTop() - staticLayout.getHeight() * 0.5f));
      // 绘制
      staticLayout.draw(myCanvas);
      myCanvas.restore();
    }

    // 绘制相框
    if(frame.getType() > 0)
    {
      drawFrameCorner(myCanvas, frame.getCornerLT(), 0, 0);
      drawFrameCorner(myCanvas, frame.getCornerRT(), srcWidth
          - frame.getCornerRT().getWidth() / inSampleSize, 0);
      drawFrameCorner(myCanvas, frame.getCornerRB(), srcWidth
          - frame.getCornerRB().getWidth() / inSampleSize, srcHeight
          - frame.getCornerRB().getHeight() / inSampleSize);
      drawFrameCorner(myCanvas, frame.getCornerLB(), 0, srcHeight
          - frame.getCornerLB().getHeight() / inSampleSize);

      int length = 0;
      int count = 0;
      length = srcHeight - 2 * frame.getCornerLT().getHeight() / inSampleSize;
      count = length / (frame.getBorderL().getHeight() / inSampleSize);
      count +=
          length % (frame.getBorderL().getHeight() / inSampleSize) == 0 ? 0 : 1;
      drawFrameBorder(myCanvas, frame.getBorderL(), Frame.LEFT_BORDER, 0, frame
          .getCornerLT().getHeight() / inSampleSize, count);
      drawFrameBorder(myCanvas, frame.getBorderR(), Frame.RIGHT_BORDER,
          srcWidth - frame.getBorderR().getWidth() / inSampleSize, frame
              .getCornerRT().getHeight() / inSampleSize, count);
      length = srcWidth - 2 * frame.getCornerLT().getWidth() / inSampleSize;
      count = length / (frame.getBorderT().getWidth() / inSampleSize);
      count +=
          length % (frame.getBorderT().getWidth() / inSampleSize) == 0 ? 0 : 1;
      drawFrameBorder(myCanvas, frame.getBorderB(), Frame.BOTTOM_BORDER, frame
          .getCornerRB().getWidth() / inSampleSize, srcHeight
          - frame.getBorderB().getHeight() / inSampleSize, count);
      drawFrameBorder(myCanvas, frame.getBorderT(), Frame.TOP_BORDER, frame
          .getCornerLT().getWidth() / inSampleSize, 0, count);
      frame.release();
    }

    // 设置保存的照片比例
    Bitmap bitmap = null;
    if(srcWidth > srcHeight)
    {
      bitmap = Bitmap.createBitmap(1440, 1080, Config.ARGB_8888);
    }
    else
    {
      bitmap = Bitmap.createBitmap(1080, 1440, Config.ARGB_8888);
    }

    // 将编辑后的Bitmap绘制到待保存的Bitmap
    Rect srcRect = new Rect(0, 0, memBmp.getWidth(), memBmp.getHeight());
    Rect dstRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    Canvas savingCanvas = new Canvas(bitmap);
    savingCanvas.drawBitmap(memBmp, srcRect, dstRect, null);

    if(memBmp != null && !memBmp.isRecycled())
    {
      memBmp.recycle();
      memBmp = null;
    }
    System.gc();

    savingCanvas.save(Canvas.ALL_SAVE_FLAG);
    savingCanvas.restore();

    String timeStamp =
        new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            .format(new Date());
    File file = new File(photoPath);
    // 设置文件路径和文件名
    String picName =
        ImageUtils.EDITED_PIC_PREFIX + timeStamp + "_" + file.getName();
    dirPath = dirPath + "/" + picName;
    try
    {
      // 写入文件
      FileOutputStream fos = new FileOutputStream(dirPath);
      bitmap.compress(CompressFormat.JPEG, 100, fos);
      fos.close();
    }
    catch(FileNotFoundException e)
    {
      dirPath = null;
      e.printStackTrace();
    }
    catch(IOException e)
    {
      dirPath = null;
      e.printStackTrace();
    }
    finally
    {

      if(bitmap != null && bitmap.isRecycled())
      {
        bitmap.recycle();
        bitmap = null;
      }
      System.gc();

      this.release();
    }
    return dirPath;

    // /**
    // * 保存
    // *
    // * @param path 保存的路径
    // * @return
    // */
    // public String save(String path)
    // {
    // // curCanvas.save(Canvas.ALL_SAVE_FLAG);
    // // curCanvas.restore();
    // Bitmap bitmap =
    // Bitmap
    // .createBitmap(this.getWidth(), this.getHeight(), Config.ARGB_8888);
    // Canvas canvas = new Canvas(bitmap);
    // canvas.drawBitmap(src, 0, 0, null);
    // Paint logoPaint = new Paint();
    // logoPaint.setStyle(Paint.Style.STROKE);
    // logoPaint.setAlpha(logo.getAlpha());
    // canvas
    // .drawBitmap(logo.getLogo(), logo.getTopX(), logo.getTopY(), logoPaint);
    // Sticker sticker = null;
    // for(int i = 0; i < stickerList.size(); i++)
    // {
    // sticker = stickerList.get(i);
    // canvas.drawBitmap(sticker.getSticker(), sticker.getStickerX(),
    // sticker.getStickerY(), null);
    // }
    // DragableEditTextBmpWrapper bmpWrapper = null;
    // for(int i = 0; i < editTextList.size(); i++)
    // {
    // bmpWrapper = editTextList.get(i);
    // canvas.drawBitmap(bmpWrapper.getBmp(), bmpWrapper.getTopX(),
    // bmpWrapper.getTopY(), null);
    // }
    // canvas.save(Canvas.ALL_SAVE_FLAG);
    // canvas.restore();
    // File file = new File(picPath);
    // String picName = ImageUtils.EDITED_PIC_PREFIX + file.getName();
    // Log.i(TAG, "fileName=" + picName);
    // try
    // {
    // path = path + "/" + picName;
    // FileOutputStream fos = new FileOutputStream(path);
    // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
    // fos.close();
    // return path;
    // }
    // catch(FileNotFoundException e)
    // {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // return null;
    // }
    // catch(IOException e)
    // {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // return null;
    // }
    // }
  }
}
