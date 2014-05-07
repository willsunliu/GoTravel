package com.GoTravel.Go1978.extracomponent;


import com.GoTravel.Go1978.log.MyLog;

import com.GoTravel.Go1978.R;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.text.TextPaint;


/**
 * 关于文字编辑的类
 * 
 * @author Wilson 20140224
 */
public class Text
{
  private static final String TAG = "Text";

  // 默认文字的大小
  public static final int DEFAULT_TEXT_SIZE = 200;
  // 默认StaticLayout的宽度
  public static final int DEFAULT_WIDHT = 1000;
  // 默认SpacingAdd
  public static final float DEFAULT_SPACING_ADD = 0.0f;
  // 默认SpacingMultipler
  public static final float DEFAULT_SPACING_MULTIPLIER = 1.0f;

  Context context;
  private String text;
  private float spacingadd;
  private float spacingmult;
  // 保存在内存的照片的缩放比例
  private int sampleSize;
  // 字体类型
  private Typeface typeface;
  // 字体颜色
  private int color;
  // 用于绘制文字的画笔
  private TextPaint textPaint;
  // 左上角坐标
  private float left;
  private float top;
  // StaticLayout的宽高
  private int width;
  private int height;
  // 文字是否被选中的标识
  private boolean selected;

  // 绘制文字的Rect
  private Rect drawingRect;

  // 文字的旋转角度
  private double textRotation = 0;
  // 用户对view上照片的缩放比例
  private double textZoom = 1f;
  // 文字size的缩放比例
  private double textSizeRatio = 1f;

  public Text(Context context, Typeface typeface, int color, float left,
      float top, int sampleSize)
  {
    this.context = context;
    this.text = context.getResources().getString(R.string.hint);
    this.spacingmult = DEFAULT_SPACING_MULTIPLIER;
    this.spacingadd = DEFAULT_SPACING_ADD;
    this.sampleSize = sampleSize;
    this.typeface = typeface;
    this.color = color;
    this.selected = true;
    this.width = DEFAULT_WIDHT;
    this.height = 0;
    this.left = left - getWidth() * 0.5f / sampleSize;
    this.top = top;

    // 初始化画笔
    this.textPaint = new TextPaint();
    this.textPaint.setAntiAlias(true);
    this.textPaint.setTypeface(this.getTypeface());
    this.textPaint.setTextSize(1f * DEFAULT_TEXT_SIZE / sampleSize);
    this.textPaint.setColor(this.getColor());
    this.textPaint.setTextAlign(Align.LEFT);
  }

  /**
   * @return the textType
   */
  public Typeface getTypeface()
  {
    return typeface;
  }

  /**
   * @param textType the textType to set
   */
  public void setTypeface(Typeface typeface)
  {
    this.typeface = typeface;
  }

  /**
   * @return the color
   */
  public int getColor()
  {
    return color;
  }

  /**
   * @param color the color to set
   */
  public void setColor(int color)
  {
    this.color = color;
  }

  /**
   * @return the selected
   */
  public boolean isSelected()
  {
    return selected;
  }

  /**
   * @param selected the selected to set
   */
  public void setSelected(boolean selected)
  {
    this.selected = selected;
  }

  /**
   * @return the textPaint
   */
  public TextPaint getTextPaint()
  {
    return textPaint;
  }

  /**
   * @param textPaint the textPaint to set
   */
  public void setTextPaint(TextPaint textPaint)
  {
    this.textPaint = textPaint;
  }

  /**
   * @return the left
   */
  public float getLeft()
  {
    return left;
  }

  /**
   * @param left the left to set
   */
  public void setLeft(float left)
  {
    this.left = left;
  }

  /**
   * @return the top
   */
  public float getTop()
  {
    return top;
  }

  /**
   * @param top the top to set
   */
  public void setTop(float top)
  {
    this.top = top;
  }

  /**
   * @return the width
   */
  public int getWidth()
  {
    return width;
  }

  /**
   * @param width the width to set
   */
  public void setWidth(int width)
  {
    this.width = width;
  }

  /**
   * @return the height
   */
  public int getHeight()
  {
    return height;
  }

  /**
   * @param height the height to set
   */
  public void setHeight(int height)
  {
    this.height = height;
  }

  /**
   * @return the drawingRect
   */
  public Rect getDrawingRect()
  {
    return drawingRect;
  }

  /**
   * @param drawingRect the drawingRect to set
   */
  public void setDrawingRect(Rect drawingRect)
  {
    this.drawingRect = drawingRect;
  }

  /**
   * @return the textRotation
   */
  public double getTextRotation()
  {
    return textRotation;
  }

  /**
   * @param textRotation the textRotation to set
   */
  public void setTextRotation(double textRotation)
  {
    this.textRotation += textRotation;
  }

  /**
   * @return the textZoom
   */
  public double getTextZoom()
  {
    return textZoom;
  }

  /**
   * @param textZoom the textZoom to set
   */
  public void setTextZoom(double textZoom)
  {
    this.textZoom *= textZoom;
    int lastW = this.getWidth();
    this.textPaint.setTextSize(1f * DEFAULT_TEXT_SIZE / this.sampleSize
        * (float) (this.textZoom * this.textSizeRatio));
    this.width = Math.round(DEFAULT_WIDHT * (float) this.textZoom);
    int curW = this.width;
    int dw = lastW - curW;
    this.left += (dw * 0.5f / sampleSize);
  }

  /**
   * @return the sampleSize
   */
  public int getSampleSize()
  {
    return sampleSize;
  }

  /**
   * @param sampleSize the sampleSize to set
   */
  public void setSampleSize(int sampleSize)
  {
    this.sampleSize = sampleSize;
  }

  /**
   * @return the textSizeRatio
   */
  public double getTextSizeRatio()
  {
    return textSizeRatio;
  }

  /**
   * @param textSizeRatio the textSizeRatio to set
   */
  public void setTextSizeRatio(double textSizeRatio)
  {
    this.textSizeRatio *= textSizeRatio;
    this.textPaint.setTextSize(1f * DEFAULT_TEXT_SIZE / this.sampleSize
        * (float) (this.textZoom * this.textSizeRatio));
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

  /**
   * @return the spacingadd
   */
  public float getSpacingadd()
  {
    return spacingadd;
  }

  /**
   * @param spacingadd the spacingadd to set
   */
  public void setSpacingadd(float spacingadd)
  {
    this.spacingadd = spacingadd;
  }

  /**
   * @return the spacingmult
   */
  public float getSpacingmult()
  {
    return spacingmult;
  }

  /**
   * @param spacingmult the spacingmult to set
   */
  public void setSpacingmult(float spacingmult)
  {
    this.spacingmult = spacingmult;
  }
}
