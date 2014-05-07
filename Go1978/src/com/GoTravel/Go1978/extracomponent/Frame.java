package com.GoTravel.Go1978.extracomponent;


import java.io.IOException;
import java.io.InputStream;

import com.GoTravel.Go1978.constants.Go1978Constants;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.NinePatchDrawable;


/**
 * 关于相框的类
 * 
 * @author Wilson 20140224
 */
public class Frame
{
  // 相框类型
  public static final int NONE = 0;
  public static final int FRAME_1 = 1;
  public static final int FRAME_2 = 2;
  public static final int FRAME_3 = 3;
  public static final int FRAME_4 = 4;
  public static final int FRAME_5 = 5;
  public static final int FRAME_6 = 6;
  public static final int FRAME_7 = 7;
  public static final int FRAME_8 = 8;
  public static final int FRAME_9 = 9;

  // 代表相框的几个部分：左上角，右上角，右下角，左下角，左边，上边，右边，下边
  public static final int LEFT_TOP_CORNER = 0;
  public static final int RIGHT_TOP_CORNER = 1;
  public static final int RIGHT_BOTTOM_CORNER = 2;
  public static final int LEFT_BOTTOM_CORNER = 3;
  public static final int LEFT_BORDER = 4;
  public static final int TOP_BORDER = 5;
  public static final int RIGHT_BORDER = 6;
  public static final int BOTTOM_BORDER = 7;

  private Context context;
  private int type = -1;

  // 左上角
  private Bitmap cornerLT;
  // 右上角
  private Bitmap cornerRT;
  // 右下角
  private Bitmap cornerRB;
  // 左下角
  private Bitmap cornerLB;
  private int cornerW;
  private int cornerH;

  // 左边
  private Bitmap borderL;
  // 上边
  private Bitmap borderT;
  // 右边
  private Bitmap borderR;
  // 下边
  private Bitmap borderB;
  private int borderW;
  private int borderH;

  // 左边界截取框的起始坐标
  private int croplbx;
  private int croplby;
  // 上边界截取框的起始坐标
  private int croptbx;
  private int croptby;
  // 右边界截取框的起始坐标
  private int croprbx;
  private int croprby;
  // 下边界截取框的起始坐标
  private int cropbbx;
  private int cropbby;

  public Frame(Context context, int type)
  {
    this.context = context;

    setFrame(type);
  }

  /**
   * 设置所有相关的宽度和高度
   * 
   * @param frame
   */
  private void setAllWH(int[] frame)
  {
    cornerW = frame[Go1978Constants.FRAME_CORNER_WIDTH];
    cornerH = frame[Go1978Constants.FRAME_CORNER_HEIGHT];
    borderW = frame[Go1978Constants.FRAME_BORDER_WIDTH];
    borderH = frame[Go1978Constants.FRAME_BORDER_HEIGHT];

    croplbx = frame[Go1978Constants.CROP_LEFT_BORDER_X];
    croplby = frame[Go1978Constants.CROP_LEFT_BORDER_Y];
    croptbx = frame[Go1978Constants.CROP_TOP_BORDER_X];
    croptby = frame[Go1978Constants.CROP_TOP_BORDER_Y];
    croprbx = frame[Go1978Constants.CROP_RIGHT_BORDER_X];
    croprby = frame[Go1978Constants.CROP_RIGHT_BORDER_Y];
    cropbbx = frame[Go1978Constants.CROP_BOTTOM_BORDER_X];
    cropbby = frame[Go1978Constants.CROP_BOTTOM_BORDER_Y];
  }

  /**
   * 生成边角的图片元素
   * 
   * @param frame 原相框图片
   * @param element 元素类型
   * @param elementW 元素的宽
   * @param elementH 元素的高
   * @param startX 起始x坐标
   * @param startY 起始y坐标
   */
  private void createElementBitmap(Bitmap frame, int element, int elementW,
      int elementH, int startX, int startY)
  {
    // 用于截取矩形
    Rect srcRect =
        new Rect(startX, startY, startX + elementW, startY + elementH);
    // 用于绘制矩形
    Rect dstRect = new Rect(0, 0, srcRect.width(), srcRect.height());
    Bitmap cropBmp =
        Bitmap
            .createBitmap(srcRect.width(), srcRect.height(), Config.ARGB_8888);
    // 把截取的内容绘制出来
    Canvas canvas = new Canvas(cropBmp);
    canvas.drawBitmap(frame, srcRect, dstRect, null);

    if(element == LEFT_TOP_CORNER)
    {
      this.cornerLT = cropBmp;
    }
    else if(element == RIGHT_TOP_CORNER)
    {
      this.cornerRT = cropBmp;
    }
    else if(element == RIGHT_BOTTOM_CORNER)
    {
      this.cornerRB = cropBmp;
    }
    else if(element == LEFT_BOTTOM_CORNER)
    {
      this.cornerLB = cropBmp;
    }
    else if(element == LEFT_BORDER)
    {
      this.borderL = cropBmp;
    }
    else if(element == TOP_BORDER)
    {
      this.borderT = cropBmp;
    }
    else if(element == RIGHT_BORDER)
    {
      this.borderR = cropBmp;
    }
    else if(element == BOTTOM_BORDER)
    {
      this.borderB = cropBmp;
    }

  }

  /**
   * 根据type设置相框类型
   * 
   * @param frame the frame to set
   * @throws IOException
   */
  public void setFrame(int type)
  {
    this.type = type;

    String fileName = "frames/frame_"+type+".png";
    
    switch(type)
    {
      case NONE:
        release();
        return;
      case FRAME_1:
        setAllWH(Go1978Constants.frame1);
        break;
      case FRAME_2:
        setAllWH(Go1978Constants.frame2);
        break;
      case FRAME_3:
        setAllWH(Go1978Constants.frame3);
        break;
      case FRAME_4:
        setAllWH(Go1978Constants.frame4);
        break;
      case FRAME_5:
        setAllWH(Go1978Constants.frame5);
        break;
      case FRAME_6:
        setAllWH(Go1978Constants.frame6);
        break;
      case FRAME_7:
        setAllWH(Go1978Constants.frame7);
        break;
      case FRAME_8:
        setAllWH(Go1978Constants.frame8);
        break;
      case FRAME_9:
        setAllWH(Go1978Constants.frame9);
        break;

      default:
        break;
    }

    Bitmap frame = null;
    InputStream is;
    try
    {
      is = context.getAssets().open(fileName);
      frame = BitmapFactory.decodeStream(is);
      is.close();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }

    // 生成绘制相框需要用到的所有元素
    createElementBitmap(frame, LEFT_TOP_CORNER, cornerW, cornerH, 0, 0);
    createElementBitmap(frame, RIGHT_TOP_CORNER, cornerW, cornerH,
        frame.getWidth() - cornerW, 0);
    createElementBitmap(frame, RIGHT_BOTTOM_CORNER, cornerW, cornerH,
        frame.getWidth() - cornerW, frame.getHeight() - cornerH);
    createElementBitmap(frame, LEFT_BOTTOM_CORNER, cornerW, cornerH, 0,
        frame.getHeight() - cornerH);

    createElementBitmap(frame, LEFT_BORDER, borderW, borderH, 0 + croplbx,
        cornerH + croplby);
    createElementBitmap(frame, RIGHT_BORDER, borderW, borderH, frame.getWidth()
        - borderW + croprbx, cornerH + croprby);
    createElementBitmap(frame, TOP_BORDER, borderW, borderH, cornerW + croptbx,
        0 + croptby);
    createElementBitmap(frame, BOTTOM_BORDER, borderW, borderH, cornerW
        + cropbbx, frame.getHeight() - borderH + cropbby);
    
    if (frame != null && !frame.isRecycled()) {
      frame.recycle();
      frame = null;
    }
    System.gc();
  }

  /**
   * 释放资源
   */
  public void release()
  {
    if(cornerLT != null)
    {
      cornerLT.recycle();
      cornerLT = null;
    }
    if(cornerRT != null)
    {
      cornerRT.recycle();
      cornerRT = null;
    }
    if(cornerRB != null)
    {
      cornerRB.recycle();
      cornerRB = null;
    }
    if(cornerLB != null)
    {
      cornerLB.recycle();
      cornerLB = null;
    }
    if(borderL != null)
    {
      borderL.recycle();
      borderL = null;
    }
    if(borderT != null)
    {
      borderT.recycle();
      borderT = null;
    }
    if(borderR != null)
    {
      borderR.recycle();
      borderR = null;
    }
    if(borderB != null)
    {
      borderB.recycle();
      borderB = null;
    }
    System.gc();
  }

  /**
   * @return the type
   */
  public int getType()
  {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(int type)
  {
    this.type = type;
  }

  /**
   * @return the cornerLT
   */
  public Bitmap getCornerLT()
  {
    return cornerLT;
  }

  /**
   * @param cornerLT the cornerLT to set
   */
  public void setCornerLT(Bitmap cornerLT)
  {
    this.cornerLT = cornerLT;
  }

  /**
   * @return the cornerRT
   */
  public Bitmap getCornerRT()
  {
    return cornerRT;
  }

  /**
   * @param cornerRT the cornerRT to set
   */
  public void setCornerRT(Bitmap cornerRT)
  {
    this.cornerRT = cornerRT;
  }

  /**
   * @return the cornerRB
   */
  public Bitmap getCornerRB()
  {
    return cornerRB;
  }

  /**
   * @param cornerRB the cornerRB to set
   */
  public void setCornerRB(Bitmap cornerRB)
  {
    this.cornerRB = cornerRB;
  }

  /**
   * @return the cornerLB
   */
  public Bitmap getCornerLB()
  {
    return cornerLB;
  }

  /**
   * @param cornerLB the cornerLB to set
   */
  public void setCornerLB(Bitmap cornerLB)
  {
    this.cornerLB = cornerLB;
  }

  /**
   * @return the borderL
   */
  public Bitmap getBorderL()
  {
    return borderL;
  }

  /**
   * @param borderL the borderL to set
   */
  public void setBorderL(Bitmap borderL)
  {
    this.borderL = borderL;
  }

  /**
   * @return the borderT
   */
  public Bitmap getBorderT()
  {
    return borderT;
  }

  /**
   * @param borderT the borderT to set
   */
  public void setBorderT(Bitmap borderT)
  {
    this.borderT = borderT;
  }

  /**
   * @return the borderR
   */
  public Bitmap getBorderR()
  {
    return borderR;
  }

  /**
   * @param borderR the borderR to set
   */
  public void setBorderR(Bitmap borderR)
  {
    this.borderR = borderR;
  }

  /**
   * @return the borderB
   */
  public Bitmap getBorderB()
  {
    return borderB;
  }

  /**
   * @param borderB the borderB to set
   */
  public void setBorderB(Bitmap borderB)
  {
    this.borderB = borderB;
  }

}
