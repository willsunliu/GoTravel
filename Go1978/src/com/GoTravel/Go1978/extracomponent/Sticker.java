package com.GoTravel.Go1978.extracomponent;


import java.io.IOException;
import java.io.InputStream;

import com.GoTravel.Go1978.constants.Go1978Constants;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;


/**
 * 关于Sticker的类
 * 
 * @author Wilson 20140221
 */
public class Sticker
{
  Context context;

  // Sticker的Resource id
  int stickerId;
  // Sticker的路径
  String path;

  private Bitmap sticker;

  // 存在内存的被编辑照片的缩放比例
  private int sampleSize = 1;
  // 用户缩放View上显示的照片时的缩放比例
  // private double totalRatio = 1f;
  // 用户通过旋转按钮对Sticker进行缩放的缩放比例
  private double stickerZoom = 1f;
  // 用户通过旋转按钮对Sticker进行旋转的旋转角度，0~2π
  private double stickerRotation = 0;

  // Sticker的中心点坐标
  private int centerX;
  private int centerY;
  // Sticker的左上角坐标
  private float left;
  private float top;
  // Sticker所占的Rect
  private Rect stickerRect;
  // Sticker绘制到View上时用到的Rect
  private Rect drawingRect;
  // Sticker是否被选中的标识
  private boolean isSelected = false;

  public Sticker(Context context, int type, float left, float top,
      int sampleSize) throws IOException
  {
    this.context = context;

    this.sampleSize = sampleSize;

    Bitmap tmp = createStickerBitmap(type);

    // 根据sample缩放Sticker作为sticker保存在内存的原图
    this.sticker =
        Bitmap.createScaledBitmap(tmp,
            Math.round(1f * tmp.getWidth() / sampleSize),
            Math.round(1f * tmp.getHeight() / sampleSize), true);

    if(tmp != null && !tmp.isRecycled())
    {
      tmp.recycle();
      tmp = null;
    }

    this.stickerRect =
        new Rect(0, 0, this.sticker.getWidth(), this.sticker.getHeight());

    centerX = Math.round(this.sticker.getWidth() * 0.5f);
    centerY = Math.round(this.sticker.getHeight() * 0.5f);

    this.left = left - this.sticker.getWidth() * 0.5f;
    this.top = top - this.sticker.getHeight() * 0.5f;

    this.isSelected = true;
  }

  /**
   * 从资源文件加载Sticker
   * 
   * @param resources
   * @param id
   * @return
   */
  private Bitmap getStickerFromResource(Resources resources, int id)
  {
    this.stickerId = id;
    return BitmapFactory.decodeResource(resources, id);
  }

  private Bitmap getStickerFromResource(AssetManager assetMgr, String path)
  {
    this.path = path;
    InputStream is = null;
    try
    {
      is = assetMgr.open(path);
    }
    catch(IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return BitmapFactory.decodeStream(is);
  }

  /**
   * 释放资源
   */
  public void release()
  {
    if(sticker != null)
    {
      sticker.recycle();
      sticker = null;
    }

    System.gc();
  }

  /**
   * @return the centerX
   */
  public int getCenterX()
  {
    return centerX;
  }

  /**
   * @param centerX the centerX to set
   */
  public void setCenterX(int centerX)
  {
    this.centerX = centerX;
  }

  /**
   * @return the centerY
   */
  public int getCenterY()
  {
    return centerY;
  }

  /**
   * @param centerY the centerY to set
   */
  public void setCenterY(int centerY)
  {
    this.centerY = centerY;
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
   * @return the sticker
   */
  public Bitmap getSticker()
  {
    return sticker;
  }

  /**
   * @param sticker the sticker to set
   */
  public void setSticker(Bitmap sticker)
  {
    this.sticker = sticker;
  }

  /**
   * @return the stickerRect
   */
  public Rect getStickerRect()
  {
    return stickerRect;
  }

  /**
   * @param stickerRect the stickerRect to set
   */
  public void setStickerRect(Rect stickerRect)
  {
    this.stickerRect = stickerRect;
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
  }

  /**
   * @return the stickerZoom
   */
  public double getStickerZoom()
  {
    return stickerZoom;
  }

  /**
   * 用户通过旋转按钮缩放Sticker
   * 
   * @param stickerZoom the stickerZoom to set
   * @throws IOException
   */
  public void setStickerZoom(double stickerZoom) throws IOException
  {
    this.stickerZoom *= stickerZoom;
    int lastW = this.sticker.getWidth();
    int lastH = this.sticker.getHeight();

    if(this.sticker != null && this.sticker.isRecycled())
    {
      this.sticker.recycle();
      this.sticker = null;
    }
    System.gc();

    InputStream is = context.getAssets().open(path);
    Bitmap tmp = BitmapFactory.decodeStream(is);
    // Bitmap tmp =
    // BitmapFactory.decodeResource(context.getResources(), stickerId);
    this.sticker =
        Bitmap.createScaledBitmap(
            tmp,
            Math.round(1f * tmp.getWidth() / sampleSize
                * (float) this.stickerZoom),
            Math.round(1f * tmp.getHeight() / sampleSize
                * (float) this.stickerZoom), true);
    int curW = this.sticker.getWidth();
    int curH = this.sticker.getHeight();
    int dw = lastW - curW;
    int dh = lastH - curH;
    this.left += (dw * 0.5f);
    this.top += (dh * 0.5f);
    this.stickerRect.set(0, 0, Math.round(this.sticker.getWidth()),
        Math.round(this.sticker.getHeight()));

    if(tmp != null && !tmp.isRecycled())
    {
      tmp.recycle();
      tmp = null;
    }
    System.gc();
  }

  /**
   * @return the stickerRotation
   */
  public double getStickerRotation()
  {
    return stickerRotation;
  }

  /**
   * @param stickerRotation the stickerRotation to set
   */
  public void setStickerRotation(double stickerRotation)
  {
    this.stickerRotation += stickerRotation;
  }

  /**
   * 根据type加载Sticker
   * 
   * @param type
   * @return
   */
  private Bitmap createStickerBitmap(int type)
  {
    AssetManager assetMgr = context.getAssets();

    switch(type)
    {
    // 自行车
      case STICKER_1_1:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_1s[0]);

      case STICKER_2_1:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_1s[1]);

      case STICKER_3_1:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_1s[2]);

      case STICKER_4_1:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_1s[3]);

      case STICKER_5_1:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_1s[4]);

      case STICKER_6_1:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_1s[5]);

      case STICKER_7_1:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_1s[6]);

      case STICKER_8_1:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_1s[7]);

      case STICKER_9_1:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_1s[8]);

        // 闹钟
      case STICKER_1_2:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_2s[0]);

      case STICKER_2_2:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_2s[1]);

      case STICKER_3_2:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_2s[2]);

      case STICKER_4_2:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_2s[3]);

      case STICKER_5_2:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_2s[4]);

      case STICKER_6_2:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_2s[5]);

      case STICKER_7_2:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_2s[6]);

      case STICKER_8_2:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_2s[7]);

      case STICKER_9_2:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_2s[8]);

        // 眼镜
      case STICKER_1_3:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_3s[0]);

      case STICKER_2_3:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_3s[1]);

      case STICKER_3_3:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_3s[2]);

      case STICKER_4_3:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_3s[3]);

      case STICKER_5_3:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_3s[4]);

      case STICKER_6_3:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_3s[5]);

      case STICKER_7_3:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_3s[6]);

      case STICKER_8_3:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_3s[7]);

      case STICKER_9_3:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_3s[8]);

        // 皇冠1
      case STICKER_1_4:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_4s[0]);

      case STICKER_2_4:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_4s[1]);

      case STICKER_3_4:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_4s[2]);

      case STICKER_4_4:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_4s[3]);

      case STICKER_5_4:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_4s[4]);

      case STICKER_6_4:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_4s[5]);

      case STICKER_7_4:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_4s[6]);

      case STICKER_8_4:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_4s[7]);

      case STICKER_9_4:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_4s[8]);

        // 皇冠2
      case STICKER_1_5:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_5s[0]);

      case STICKER_2_5:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_5s[1]);

      case STICKER_3_5:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_5s[2]);

      case STICKER_4_5:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_5s[3]);

      case STICKER_5_5:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_5s[4]);

      case STICKER_6_5:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_5s[5]);

      case STICKER_7_5:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_5s[6]);

      case STICKER_8_5:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_5s[7]);

      case STICKER_9_5:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_5s[8]);

        // 皇冠3
      case STICKER_1_6:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_6s[0]);

      case STICKER_2_6:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_6s[1]);

      case STICKER_3_6:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_6s[2]);

      case STICKER_4_6:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_6s[3]);

      case STICKER_5_6:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_6s[4]);

      case STICKER_6_6:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_6s[5]);

      case STICKER_7_6:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_6s[6]);

      case STICKER_8_6:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_6s[7]);

      case STICKER_9_6:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_6s[8]);

        // 皇冠4
      case STICKER_1_7:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_7s[0]);

      case STICKER_2_7:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_7s[1]);

      case STICKER_3_7:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_7s[2]);

      case STICKER_4_7:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_7s[3]);

      case STICKER_5_7:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_7s[4]);

      case STICKER_6_7:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_7s[5]);

      case STICKER_7_7:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_7s[6]);

      case STICKER_8_7:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_7s[7]);

      case STICKER_9_7:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_7s[8]);

        // 耳机
      case STICKER_1_8:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_8s[0]);

      case STICKER_2_8:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_8s[1]);

      case STICKER_3_8:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_8s[2]);

      case STICKER_4_8:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_8s[3]);

      case STICKER_5_8:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_8s[4]);

      case STICKER_6_8:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_8s[5]);

      case STICKER_7_8:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_8s[6]);

      case STICKER_8_8:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_8s[7]);

      case STICKER_9_8:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_8s[8]);

        // 星星
      case STICKER_1_9:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_9s[0]);

      case STICKER_2_9:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_9s[1]);

      case STICKER_3_9:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_9s[2]);

      case STICKER_4_9:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_9s[3]);

      case STICKER_5_9:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_9s[4]);

      case STICKER_6_9:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_9s[5]);

      case STICKER_7_9:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_9s[6]);

      case STICKER_8_9:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_9s[7]);

      case STICKER_9_9:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_9s[8]);

        // 帽子
      case STICKER_1_10:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_10s[0]);

      case STICKER_2_10:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_10s[1]);

      case STICKER_3_10:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_10s[2]);

      case STICKER_4_10:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_10s[3]);

      case STICKER_5_10:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_10s[4]);

      case STICKER_6_10:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_10s[5]);

      case STICKER_7_10:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_10s[6]);

      case STICKER_8_10:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_10s[7]);

      case STICKER_9_10:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_10s[8]);

        // 发带
      case STICKER_1_11:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_11s[0]);

      case STICKER_2_11:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_11s[1]);

      case STICKER_3_11:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_11s[2]);

      case STICKER_4_11:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_11s[3]);

      case STICKER_5_11:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_11s[4]);

      case STICKER_6_11:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_11s[5]);

      case STICKER_7_11:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_11s[6]);

      case STICKER_8_11:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_11s[7]);

      case STICKER_9_11:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_11s[8]);

        // 摩天轮
      case STICKER_1_12:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_12s[0]);

      case STICKER_2_12:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_12s[1]);

      case STICKER_3_12:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_12s[2]);

      case STICKER_4_12:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_12s[3]);

      case STICKER_5_12:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_12s[4]);

      case STICKER_6_12:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_12s[5]);

      case STICKER_7_12:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_12s[6]);

      case STICKER_8_12:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_12s[7]);

      case STICKER_9_12:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_12s[8]);

        // 玫瑰
      case STICKER_1_13:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_13s[0]);

      case STICKER_2_13:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_13s[1]);

      case STICKER_3_13:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_13s[2]);

      case STICKER_4_13:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_13s[3]);

      case STICKER_5_13:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_13s[4]);

      case STICKER_6_13:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_13s[5]);

      case STICKER_7_13:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_13s[6]);

      case STICKER_8_13:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_13s[7]);

      case STICKER_9_13:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_13s[8]);

        // 钻石
      case STICKER_1_14:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_14s[0]);

      case STICKER_2_14:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_14s[1]);

      case STICKER_3_14:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_14s[2]);

      case STICKER_4_14:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_14s[3]);

      case STICKER_5_14:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_14s[4]);

      case STICKER_6_14:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_14s[5]);

      case STICKER_7_14:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_14s[6]);

      case STICKER_8_14:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_14s[7]);

      case STICKER_9_14:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_14s[8]);

        // 飞机
      case STICKER_1_15:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_15s[0]);

      case STICKER_2_15:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_15s[1]);

      case STICKER_3_15:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_15s[2]);

      case STICKER_4_15:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_15s[3]);

      case STICKER_5_15:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_15s[4]);

      case STICKER_6_15:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_15s[5]);

      case STICKER_7_15:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_15s[6]);

      case STICKER_8_15:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_15s[7]);

      case STICKER_9_15:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_15s[8]);

        // 板凳
      case STICKER_1_16:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_16s[0]);

      case STICKER_2_16:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_16s[1]);

      case STICKER_3_16:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_16s[2]);

      case STICKER_4_16:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_16s[3]);

      case STICKER_5_16:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_16s[4]);

      case STICKER_6_16:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_16s[5]);

      case STICKER_7_16:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_16s[6]);

      case STICKER_8_16:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_16s[7]);

      case STICKER_9_16:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_16s[8]);

        // 铁塔
      case STICKER_1_17:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_17s[0]);

      case STICKER_2_17:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_17s[1]);

      case STICKER_3_17:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_17s[2]);

      case STICKER_4_17:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_17s[3]);

      case STICKER_5_17:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_17s[4]);

      case STICKER_6_17:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_17s[5]);

      case STICKER_7_17:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_17s[6]);

      case STICKER_8_17:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_17s[7]);

      case STICKER_9_17:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_17s[8]);

        // 电话
      case STICKER_1_18:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_18s[0]);

      case STICKER_2_18:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_18s[1]);

      case STICKER_3_18:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_18s[2]);

      case STICKER_4_18:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_18s[3]);

      case STICKER_5_18:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_18s[4]);

      case STICKER_6_18:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_18s[5]);

      case STICKER_7_18:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_18s[6]);

      case STICKER_8_18:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_18s[7]);

      case STICKER_9_18:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_18s[8]);

        // 鸟笼
      case STICKER_1_19:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_19s[0]);

      case STICKER_2_19:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_19s[1]);

      case STICKER_3_19:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_19s[2]);

      case STICKER_4_19:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_19s[3]);

      case STICKER_5_19:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_19s[4]);

      case STICKER_6_19:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_19s[5]);

      case STICKER_7_19:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_19s[6]);

      case STICKER_8_19:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_19s[7]);

      case STICKER_9_19:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_19s[8]);

        // 大轮自行车
      case STICKER_1_20:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_20s[0]);

      case STICKER_2_20:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_20s[1]);

      case STICKER_3_20:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_20s[2]);

      case STICKER_4_20:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_20s[3]);

      case STICKER_5_20:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_20s[4]);

      case STICKER_6_20:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_20s[5]);

      case STICKER_7_20:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_20s[6]);

      case STICKER_8_20:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_20s[7]);

      case STICKER_9_20:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_20s[8]);

        // 留声机
      case STICKER_1_21:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_21s[0]);

      case STICKER_2_21:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_21s[1]);

      case STICKER_3_21:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_21s[2]);

      case STICKER_4_21:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_21s[3]);

      case STICKER_5_21:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_21s[4]);

      case STICKER_6_21:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_21s[5]);

      case STICKER_7_21:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_21s[6]);

      case STICKER_8_21:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_21s[7]);

      case STICKER_9_21:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_21s[8]);

        // 灯柱
      case STICKER_1_22:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_22s[0]);

      case STICKER_2_22:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_22s[1]);

      case STICKER_3_22:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_22s[2]);

      case STICKER_4_22:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_22s[3]);

      case STICKER_5_22:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_22s[4]);

      case STICKER_6_22:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_22s[5]);

      case STICKER_7_22:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_22s[6]);

      case STICKER_8_22:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_22s[7]);

      case STICKER_9_22:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_22s[8]);

        // 蝴蝶
      case STICKER_1_23:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_23s[0]);

      case STICKER_2_23:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_23s[1]);

      case STICKER_3_23:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_23s[2]);

      case STICKER_4_23:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_23s[3]);

      case STICKER_5_23:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_23s[4]);

      case STICKER_6_23:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_23s[5]);

      case STICKER_7_23:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_23s[6]);

      case STICKER_8_23:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_23s[7]);

      case STICKER_9_23:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_23s[8]);

        // 文件包
      case STICKER_1_24:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_24s[0]);

      case STICKER_2_24:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_24s[1]);

      case STICKER_3_24:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_24s[2]);

      case STICKER_4_24:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_24s[3]);

      case STICKER_5_24:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_24s[4]);

      case STICKER_6_24:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_24s[5]);

      case STICKER_7_24:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_24s[6]);

      case STICKER_8_24:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_24s[7]);

      case STICKER_9_24:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_24s[8]);

        // 心形1
      case STICKER_1_25:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_25s[0]);

      case STICKER_2_25:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_25s[1]);

      case STICKER_3_25:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_25s[2]);

      case STICKER_4_25:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_25s[3]);

      case STICKER_5_25:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_25s[4]);

      case STICKER_6_25:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_25s[5]);

      case STICKER_7_25:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_25s[6]);

      case STICKER_8_25:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_25s[7]);

      case STICKER_9_25:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_25s[8]);

        // 兔子
      case STICKER_1_26:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_26s[0]);

      case STICKER_2_26:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_26s[1]);

      case STICKER_3_26:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_26s[2]);

      case STICKER_4_26:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_26s[3]);

      case STICKER_5_26:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_26s[4]);

      case STICKER_6_26:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_26s[5]);

      case STICKER_7_26:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_26s[6]);

      case STICKER_8_26:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_26s[7]);

      case STICKER_9_26:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_26s[8]);

        // 皇冠5
      case STICKER_1_27:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_27s[0]);

      case STICKER_2_27:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_27s[1]);

      case STICKER_3_27:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_27s[2]);

      case STICKER_4_27:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_27s[3]);

      case STICKER_5_27:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_27s[4]);

      case STICKER_6_27:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_27s[5]);

      case STICKER_7_27:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_27s[6]);

      case STICKER_8_27:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_27s[7]);

      case STICKER_9_27:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_27s[8]);

        // 心形2
      case STICKER_1_28:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_28s[0]);

      case STICKER_2_28:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_28s[1]);

      case STICKER_3_28:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_28s[2]);

      case STICKER_4_28:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_28s[3]);

      case STICKER_5_28:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_28s[4]);

      case STICKER_6_28:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_28s[5]);

      case STICKER_7_28:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_28s[6]);

      case STICKER_8_28:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_28s[7]);

      case STICKER_9_28:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_28s[8]);

        // 豪宅
      case STICKER_1_29:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_29s[0]);

      case STICKER_2_29:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_29s[1]);

      case STICKER_3_29:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_29s[2]);

      case STICKER_4_29:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_29s[3]);

      case STICKER_5_29:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_29s[4]);

      case STICKER_6_29:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_29s[5]);

      case STICKER_7_29:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_29s[6]);

      case STICKER_8_29:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_29s[7]);

      case STICKER_9_29:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_29s[8]);

        // 横条1
      case STICKER_1_30:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_30s[0]);

      case STICKER_2_30:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_30s[1]);

      case STICKER_3_30:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_30s[2]);

      case STICKER_4_30:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_30s[3]);

      case STICKER_5_30:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_30s[4]);

      case STICKER_6_30:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_30s[5]);

      case STICKER_7_30:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_30s[6]);

      case STICKER_8_30:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_30s[7]);

      case STICKER_9_30:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_30s[8]);

        // 横条2
      case STICKER_1_31:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_31s[0]);

      case STICKER_2_31:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_31s[1]);

      case STICKER_3_31:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_31s[2]);

      case STICKER_4_31:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_31s[3]);

      case STICKER_5_31:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_31s[4]);

      case STICKER_6_31:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_31s[5]);

      case STICKER_7_31:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_31s[6]);

      case STICKER_8_31:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_31s[7]);

      case STICKER_9_31:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_31s[8]);

        // 横条3
      case STICKER_1_32:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_32s[0]);

      case STICKER_2_32:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_32s[1]);

      case STICKER_3_32:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_32s[2]);

      case STICKER_4_32:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_32s[3]);

      case STICKER_5_32:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_32s[4]);

      case STICKER_6_32:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_32s[5]);

      case STICKER_7_32:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_32s[6]);

      case STICKER_8_32:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_32s[7]);

      case STICKER_9_32:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_32s[8]);

        // 酒
      case STICKER_1_33:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_33s[0]);

      case STICKER_2_33:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_33s[1]);

      case STICKER_3_33:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_33s[2]);

      case STICKER_4_33:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_33s[3]);

      case STICKER_5_33:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_33s[4]);

      case STICKER_6_33:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_33s[5]);

      case STICKER_7_33:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_33s[6]);

      case STICKER_8_33:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_33s[7]);

      case STICKER_9_33:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_33s[8]);

        // 斜塔
      case STICKER_1_34:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_34s[0]);

      case STICKER_2_34:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_34s[1]);

      case STICKER_3_34:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_34s[2]);

      case STICKER_4_34:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_34s[3]);

      case STICKER_5_34:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_34s[4]);

      case STICKER_6_34:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_34s[5]);

      case STICKER_7_34:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_34s[6]);

      case STICKER_8_34:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_34s[7]);

      case STICKER_9_34:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_34s[8]);

        // 大笨钟
      case STICKER_1_35:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_35s[0]);

      case STICKER_2_35:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_35s[1]);

      case STICKER_3_35:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_35s[2]);

      case STICKER_4_35:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_35s[3]);

      case STICKER_5_35:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_35s[4]);

      case STICKER_6_35:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_35s[5]);

      case STICKER_7_35:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_35s[6]);

      case STICKER_8_35:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_35s[7]);

      case STICKER_9_35:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_35s[8]);

        // 大桥
      case STICKER_1_36:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_36s[0]);

      case STICKER_2_36:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_36s[1]);

      case STICKER_3_36:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_36s[2]);

      case STICKER_4_36:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_36s[3]);

      case STICKER_5_36:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_36s[4]);

      case STICKER_6_36:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_36s[5]);

      case STICKER_7_36:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_36s[6]);

      case STICKER_8_36:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_36s[7]);

      case STICKER_9_36:
        return getStickerFromResource(assetMgr, Go1978Constants.sticker_36s[8]);

    }

    return null;
  }

  private static final String TAG = "Sticker";

  // 自行车
  public static final int STICKER_1 = 0;
  public static final int STICKER_1_1 = STICKER_1 + 1;
  public static final int STICKER_2_1 = STICKER_1 + 2;
  public static final int STICKER_3_1 = STICKER_1 + 3;
  public static final int STICKER_4_1 = STICKER_1 + 4;
  public static final int STICKER_5_1 = STICKER_1 + 5;
  public static final int STICKER_6_1 = STICKER_1 + 6;
  public static final int STICKER_7_1 = STICKER_1 + 7;
  public static final int STICKER_8_1 = STICKER_1 + 8;
  public static final int STICKER_9_1 = STICKER_1 + 9;
  // 闹钟
  public static final int STICKER_2 = 9;
  public static final int STICKER_1_2 = STICKER_2 + 1;
  public static final int STICKER_2_2 = STICKER_2 + 2;
  public static final int STICKER_3_2 = STICKER_2 + 3;
  public static final int STICKER_4_2 = STICKER_2 + 4;
  public static final int STICKER_5_2 = STICKER_2 + 5;
  public static final int STICKER_6_2 = STICKER_2 + 6;
  public static final int STICKER_7_2 = STICKER_2 + 7;
  public static final int STICKER_8_2 = STICKER_2 + 8;
  public static final int STICKER_9_2 = STICKER_2 + 9;
  // 眼镜
  public static final int STICKER_3 = 18;
  public static final int STICKER_1_3 = STICKER_3 + 1;
  public static final int STICKER_2_3 = STICKER_3 + 2;
  public static final int STICKER_3_3 = STICKER_3 + 3;
  public static final int STICKER_4_3 = STICKER_3 + 4;
  public static final int STICKER_5_3 = STICKER_3 + 5;
  public static final int STICKER_6_3 = STICKER_3 + 6;
  public static final int STICKER_7_3 = STICKER_3 + 7;
  public static final int STICKER_8_3 = STICKER_3 + 8;
  public static final int STICKER_9_3 = STICKER_3 + 9;
  // 皇冠1
  public static final int STICKER_4 = 27;
  public static final int STICKER_1_4 = STICKER_4 + 1;
  public static final int STICKER_2_4 = STICKER_4 + 2;
  public static final int STICKER_3_4 = STICKER_4 + 3;
  public static final int STICKER_4_4 = STICKER_4 + 4;
  public static final int STICKER_5_4 = STICKER_4 + 5;
  public static final int STICKER_6_4 = STICKER_4 + 6;
  public static final int STICKER_7_4 = STICKER_4 + 7;
  public static final int STICKER_8_4 = STICKER_4 + 8;
  public static final int STICKER_9_4 = STICKER_4 + 9;
  // 皇冠2
  public static final int STICKER_5 = 36;
  public static final int STICKER_1_5 = STICKER_5 + 1;
  public static final int STICKER_2_5 = STICKER_5 + 2;
  public static final int STICKER_3_5 = STICKER_5 + 3;
  public static final int STICKER_4_5 = STICKER_5 + 4;
  public static final int STICKER_5_5 = STICKER_5 + 5;
  public static final int STICKER_6_5 = STICKER_5 + 6;
  public static final int STICKER_7_5 = STICKER_5 + 7;
  public static final int STICKER_8_5 = STICKER_5 + 8;
  public static final int STICKER_9_5 = STICKER_5 + 9;
  // 皇冠3
  public static final int STICKER_6 = 45;
  public static final int STICKER_1_6 = STICKER_6 + 1;
  public static final int STICKER_2_6 = STICKER_6 + 2;
  public static final int STICKER_3_6 = STICKER_6 + 3;
  public static final int STICKER_4_6 = STICKER_6 + 4;
  public static final int STICKER_5_6 = STICKER_6 + 5;
  public static final int STICKER_6_6 = STICKER_6 + 6;
  public static final int STICKER_7_6 = STICKER_6 + 7;
  public static final int STICKER_8_6 = STICKER_6 + 8;
  public static final int STICKER_9_6 = STICKER_6 + 9;
  // 皇冠4
  public static final int STICKER_7 = 54;
  public static final int STICKER_1_7 = STICKER_7 + 1;
  public static final int STICKER_2_7 = STICKER_7 + 2;
  public static final int STICKER_3_7 = STICKER_7 + 3;
  public static final int STICKER_4_7 = STICKER_7 + 4;
  public static final int STICKER_5_7 = STICKER_7 + 5;
  public static final int STICKER_6_7 = STICKER_7 + 6;
  public static final int STICKER_7_7 = STICKER_7 + 7;
  public static final int STICKER_8_7 = STICKER_7 + 8;
  public static final int STICKER_9_7 = STICKER_7 + 9;
  // 耳机
  public static final int STICKER_8 = 63;
  public static final int STICKER_1_8 = STICKER_8 + 1;
  public static final int STICKER_2_8 = STICKER_8 + 2;
  public static final int STICKER_3_8 = STICKER_8 + 3;
  public static final int STICKER_4_8 = STICKER_8 + 4;
  public static final int STICKER_5_8 = STICKER_8 + 5;
  public static final int STICKER_6_8 = STICKER_8 + 6;
  public static final int STICKER_7_8 = STICKER_8 + 7;
  public static final int STICKER_8_8 = STICKER_8 + 8;
  public static final int STICKER_9_8 = STICKER_8 + 9;
  // 星星
  public static final int STICKER_9 = 72;
  public static final int STICKER_1_9 = STICKER_9 + 1;
  public static final int STICKER_2_9 = STICKER_9 + 2;
  public static final int STICKER_3_9 = STICKER_9 + 3;
  public static final int STICKER_4_9 = STICKER_9 + 4;
  public static final int STICKER_5_9 = STICKER_9 + 5;
  public static final int STICKER_6_9 = STICKER_9 + 6;
  public static final int STICKER_7_9 = STICKER_9 + 7;
  public static final int STICKER_8_9 = STICKER_9 + 8;
  public static final int STICKER_9_9 = STICKER_9 + 9;
  // 帽子
  public static final int STICKER_10 = 81;
  public static final int STICKER_1_10 = STICKER_10 + 1;
  public static final int STICKER_2_10 = STICKER_10 + 2;
  public static final int STICKER_3_10 = STICKER_10 + 3;
  public static final int STICKER_4_10 = STICKER_10 + 4;
  public static final int STICKER_5_10 = STICKER_10 + 5;
  public static final int STICKER_6_10 = STICKER_10 + 6;
  public static final int STICKER_7_10 = STICKER_10 + 7;
  public static final int STICKER_8_10 = STICKER_10 + 8;
  public static final int STICKER_9_10 = STICKER_10 + 9;
  // 发带
  public static final int STICKER_11 = 90;
  public static final int STICKER_1_11 = STICKER_11 + 1;
  public static final int STICKER_2_11 = STICKER_11 + 2;
  public static final int STICKER_3_11 = STICKER_11 + 3;
  public static final int STICKER_4_11 = STICKER_11 + 4;
  public static final int STICKER_5_11 = STICKER_11 + 5;
  public static final int STICKER_6_11 = STICKER_11 + 6;
  public static final int STICKER_7_11 = STICKER_11 + 7;
  public static final int STICKER_8_11 = STICKER_11 + 8;
  public static final int STICKER_9_11 = STICKER_11 + 9;
  // 摩天轮
  public static final int STICKER_12 = 99;
  public static final int STICKER_1_12 = STICKER_12 + 1;
  public static final int STICKER_2_12 = STICKER_12 + 2;
  public static final int STICKER_3_12 = STICKER_12 + 3;
  public static final int STICKER_4_12 = STICKER_12 + 4;
  public static final int STICKER_5_12 = STICKER_12 + 5;
  public static final int STICKER_6_12 = STICKER_12 + 6;
  public static final int STICKER_7_12 = STICKER_12 + 7;
  public static final int STICKER_8_12 = STICKER_12 + 8;
  public static final int STICKER_9_12 = STICKER_12 + 9;
  // 玫瑰
  public static final int STICKER_13 = 108;
  public static final int STICKER_1_13 = STICKER_13 + 1;
  public static final int STICKER_2_13 = STICKER_13 + 2;
  public static final int STICKER_3_13 = STICKER_13 + 3;
  public static final int STICKER_4_13 = STICKER_13 + 4;
  public static final int STICKER_5_13 = STICKER_13 + 5;
  public static final int STICKER_6_13 = STICKER_13 + 6;
  public static final int STICKER_7_13 = STICKER_13 + 7;
  public static final int STICKER_8_13 = STICKER_13 + 8;
  public static final int STICKER_9_13 = STICKER_13 + 9;
  // 钻石
  public static final int STICKER_14 = 117;
  public static final int STICKER_1_14 = STICKER_14 + 1;
  public static final int STICKER_2_14 = STICKER_14 + 2;
  public static final int STICKER_3_14 = STICKER_14 + 3;
  public static final int STICKER_4_14 = STICKER_14 + 4;
  public static final int STICKER_5_14 = STICKER_14 + 5;
  public static final int STICKER_6_14 = STICKER_14 + 6;
  public static final int STICKER_7_14 = STICKER_14 + 7;
  public static final int STICKER_8_14 = STICKER_14 + 8;
  public static final int STICKER_9_14 = STICKER_14 + 9;
  // 飞机
  public static final int STICKER_15 = 126;
  public static final int STICKER_1_15 = STICKER_15 + 1;
  public static final int STICKER_2_15 = STICKER_15 + 2;
  public static final int STICKER_3_15 = STICKER_15 + 3;
  public static final int STICKER_4_15 = STICKER_15 + 4;
  public static final int STICKER_5_15 = STICKER_15 + 5;
  public static final int STICKER_6_15 = STICKER_15 + 6;
  public static final int STICKER_7_15 = STICKER_15 + 7;
  public static final int STICKER_8_15 = STICKER_15 + 8;
  public static final int STICKER_9_15 = STICKER_15 + 9;
  // 板凳
  public static final int STICKER_16 = 135;
  public static final int STICKER_1_16 = STICKER_16 + 1;
  public static final int STICKER_2_16 = STICKER_16 + 2;
  public static final int STICKER_3_16 = STICKER_16 + 3;
  public static final int STICKER_4_16 = STICKER_16 + 4;
  public static final int STICKER_5_16 = STICKER_16 + 5;
  public static final int STICKER_6_16 = STICKER_16 + 6;
  public static final int STICKER_7_16 = STICKER_16 + 7;
  public static final int STICKER_8_16 = STICKER_16 + 8;
  public static final int STICKER_9_16 = STICKER_16 + 9;
  // 铁塔
  public static final int STICKER_17 = 144;
  public static final int STICKER_1_17 = STICKER_17 + 1;
  public static final int STICKER_2_17 = STICKER_17 + 2;
  public static final int STICKER_3_17 = STICKER_17 + 3;
  public static final int STICKER_4_17 = STICKER_17 + 4;
  public static final int STICKER_5_17 = STICKER_17 + 5;
  public static final int STICKER_6_17 = STICKER_17 + 6;
  public static final int STICKER_7_17 = STICKER_17 + 7;
  public static final int STICKER_8_17 = STICKER_17 + 8;
  public static final int STICKER_9_17 = STICKER_17 + 9;
  // 电话
  public static final int STICKER_18 = 153;
  public static final int STICKER_1_18 = STICKER_18 + 1;
  public static final int STICKER_2_18 = STICKER_18 + 2;
  public static final int STICKER_3_18 = STICKER_18 + 3;
  public static final int STICKER_4_18 = STICKER_18 + 4;
  public static final int STICKER_5_18 = STICKER_18 + 5;
  public static final int STICKER_6_18 = STICKER_18 + 6;
  public static final int STICKER_7_18 = STICKER_18 + 7;
  public static final int STICKER_8_18 = STICKER_18 + 8;
  public static final int STICKER_9_18 = STICKER_18 + 9;
  // 鸟笼
  public static final int STICKER_19 = 162;
  public static final int STICKER_1_19 = STICKER_19 + 1;
  public static final int STICKER_2_19 = STICKER_19 + 2;
  public static final int STICKER_3_19 = STICKER_19 + 3;
  public static final int STICKER_4_19 = STICKER_19 + 4;
  public static final int STICKER_5_19 = STICKER_19 + 5;
  public static final int STICKER_6_19 = STICKER_19 + 6;
  public static final int STICKER_7_19 = STICKER_19 + 7;
  public static final int STICKER_8_19 = STICKER_19 + 8;
  public static final int STICKER_9_19 = STICKER_19 + 9;
  // 大轮自行车
  public static final int STICKER_20 = 171;
  public static final int STICKER_1_20 = STICKER_20 + 1;
  public static final int STICKER_2_20 = STICKER_20 + 2;
  public static final int STICKER_3_20 = STICKER_20 + 3;
  public static final int STICKER_4_20 = STICKER_20 + 4;
  public static final int STICKER_5_20 = STICKER_20 + 5;
  public static final int STICKER_6_20 = STICKER_20 + 6;
  public static final int STICKER_7_20 = STICKER_20 + 7;
  public static final int STICKER_8_20 = STICKER_20 + 8;
  public static final int STICKER_9_20 = STICKER_20 + 9;
  // 留声机
  public static final int STICKER_21 = 180;
  public static final int STICKER_1_21 = STICKER_21 + 1;
  public static final int STICKER_2_21 = STICKER_21 + 2;
  public static final int STICKER_3_21 = STICKER_21 + 3;
  public static final int STICKER_4_21 = STICKER_21 + 4;
  public static final int STICKER_5_21 = STICKER_21 + 5;
  public static final int STICKER_6_21 = STICKER_21 + 6;
  public static final int STICKER_7_21 = STICKER_21 + 7;
  public static final int STICKER_8_21 = STICKER_21 + 8;
  public static final int STICKER_9_21 = STICKER_21 + 9;
  // 灯柱
  public static final int STICKER_22 = 189;
  public static final int STICKER_1_22 = STICKER_22 + 1;
  public static final int STICKER_2_22 = STICKER_22 + 2;
  public static final int STICKER_3_22 = STICKER_22 + 3;
  public static final int STICKER_4_22 = STICKER_22 + 4;
  public static final int STICKER_5_22 = STICKER_22 + 5;
  public static final int STICKER_6_22 = STICKER_22 + 6;
  public static final int STICKER_7_22 = STICKER_22 + 7;
  public static final int STICKER_8_22 = STICKER_22 + 8;
  public static final int STICKER_9_22 = STICKER_22 + 9;
  // 蝴蝶
  public static final int STICKER_23 = 198;
  public static final int STICKER_1_23 = STICKER_23 + 1;
  public static final int STICKER_2_23 = STICKER_23 + 2;
  public static final int STICKER_3_23 = STICKER_23 + 3;
  public static final int STICKER_4_23 = STICKER_23 + 4;
  public static final int STICKER_5_23 = STICKER_23 + 5;
  public static final int STICKER_6_23 = STICKER_23 + 6;
  public static final int STICKER_7_23 = STICKER_23 + 7;
  public static final int STICKER_8_23 = STICKER_23 + 8;
  public static final int STICKER_9_23 = STICKER_23 + 9;
  // 文件包
  public static final int STICKER_24 = 207;
  public static final int STICKER_1_24 = STICKER_24 + 1;
  public static final int STICKER_2_24 = STICKER_24 + 2;
  public static final int STICKER_3_24 = STICKER_24 + 3;
  public static final int STICKER_4_24 = STICKER_24 + 4;
  public static final int STICKER_5_24 = STICKER_24 + 5;
  public static final int STICKER_6_24 = STICKER_24 + 6;
  public static final int STICKER_7_24 = STICKER_24 + 7;
  public static final int STICKER_8_24 = STICKER_24 + 8;
  public static final int STICKER_9_24 = STICKER_24 + 9;
  // 心形1
  public static final int STICKER_25 = 216;
  public static final int STICKER_1_25 = STICKER_25 + 1;
  public static final int STICKER_2_25 = STICKER_25 + 2;
  public static final int STICKER_3_25 = STICKER_25 + 3;
  public static final int STICKER_4_25 = STICKER_25 + 4;
  public static final int STICKER_5_25 = STICKER_25 + 5;
  public static final int STICKER_6_25 = STICKER_25 + 6;
  public static final int STICKER_7_25 = STICKER_25 + 7;
  public static final int STICKER_8_25 = STICKER_25 + 8;
  public static final int STICKER_9_25 = STICKER_25 + 9;
  // 兔子
  public static final int STICKER_26 = 225;
  public static final int STICKER_1_26 = STICKER_26 + 1;
  public static final int STICKER_2_26 = STICKER_26 + 2;
  public static final int STICKER_3_26 = STICKER_26 + 3;
  public static final int STICKER_4_26 = STICKER_26 + 4;
  public static final int STICKER_5_26 = STICKER_26 + 5;
  public static final int STICKER_6_26 = STICKER_26 + 6;
  public static final int STICKER_7_26 = STICKER_26 + 7;
  public static final int STICKER_8_26 = STICKER_26 + 8;
  public static final int STICKER_9_26 = STICKER_26 + 9;
  // 皇冠5
  public static final int STICKER_27 = 234;
  public static final int STICKER_1_27 = STICKER_27 + 1;
  public static final int STICKER_2_27 = STICKER_27 + 2;
  public static final int STICKER_3_27 = STICKER_27 + 3;
  public static final int STICKER_4_27 = STICKER_27 + 4;
  public static final int STICKER_5_27 = STICKER_27 + 5;
  public static final int STICKER_6_27 = STICKER_27 + 6;
  public static final int STICKER_7_27 = STICKER_27 + 7;
  public static final int STICKER_8_27 = STICKER_27 + 8;
  public static final int STICKER_9_27 = STICKER_27 + 9;
  // 心形2
  public static final int STICKER_28 = 243;
  public static final int STICKER_1_28 = STICKER_28 + 1;
  public static final int STICKER_2_28 = STICKER_28 + 2;
  public static final int STICKER_3_28 = STICKER_28 + 3;
  public static final int STICKER_4_28 = STICKER_28 + 4;
  public static final int STICKER_5_28 = STICKER_28 + 5;
  public static final int STICKER_6_28 = STICKER_28 + 6;
  public static final int STICKER_7_28 = STICKER_28 + 7;
  public static final int STICKER_8_28 = STICKER_28 + 8;
  public static final int STICKER_9_28 = STICKER_28 + 9;
  // 豪宅
  public static final int STICKER_29 = 252;
  public static final int STICKER_1_29 = STICKER_29 + 1;
  public static final int STICKER_2_29 = STICKER_29 + 2;
  public static final int STICKER_3_29 = STICKER_29 + 3;
  public static final int STICKER_4_29 = STICKER_29 + 4;
  public static final int STICKER_5_29 = STICKER_29 + 5;
  public static final int STICKER_6_29 = STICKER_29 + 6;
  public static final int STICKER_7_29 = STICKER_29 + 7;
  public static final int STICKER_8_29 = STICKER_29 + 8;
  public static final int STICKER_9_29 = STICKER_29 + 9;
  // 横条1
  public static final int STICKER_30 = 261;
  public static final int STICKER_1_30 = STICKER_30 + 1;
  public static final int STICKER_2_30 = STICKER_30 + 2;
  public static final int STICKER_3_30 = STICKER_30 + 3;
  public static final int STICKER_4_30 = STICKER_30 + 4;
  public static final int STICKER_5_30 = STICKER_30 + 5;
  public static final int STICKER_6_30 = STICKER_30 + 6;
  public static final int STICKER_7_30 = STICKER_30 + 7;
  public static final int STICKER_8_30 = STICKER_30 + 8;
  public static final int STICKER_9_30 = STICKER_30 + 9;
  // 横条2
  public static final int STICKER_31 = 270;
  public static final int STICKER_1_31 = STICKER_31 + 1;
  public static final int STICKER_2_31 = STICKER_31 + 2;
  public static final int STICKER_3_31 = STICKER_31 + 3;
  public static final int STICKER_4_31 = STICKER_31 + 4;
  public static final int STICKER_5_31 = STICKER_31 + 5;
  public static final int STICKER_6_31 = STICKER_31 + 6;
  public static final int STICKER_7_31 = STICKER_31 + 7;
  public static final int STICKER_8_31 = STICKER_31 + 8;
  public static final int STICKER_9_31 = STICKER_31 + 9;
  // 横条3
  public static final int STICKER_32 = 279;
  public static final int STICKER_1_32 = STICKER_32 + 1;
  public static final int STICKER_2_32 = STICKER_32 + 2;
  public static final int STICKER_3_32 = STICKER_32 + 3;
  public static final int STICKER_4_32 = STICKER_32 + 4;
  public static final int STICKER_5_32 = STICKER_32 + 5;
  public static final int STICKER_6_32 = STICKER_32 + 6;
  public static final int STICKER_7_32 = STICKER_32 + 7;
  public static final int STICKER_8_32 = STICKER_32 + 8;
  public static final int STICKER_9_32 = STICKER_32 + 9;
  // 酒
  public static final int STICKER_33 = 288;
  public static final int STICKER_1_33 = STICKER_33 + 1;
  public static final int STICKER_2_33 = STICKER_33 + 2;
  public static final int STICKER_3_33 = STICKER_33 + 3;
  public static final int STICKER_4_33 = STICKER_33 + 4;
  public static final int STICKER_5_33 = STICKER_33 + 5;
  public static final int STICKER_6_33 = STICKER_33 + 6;
  public static final int STICKER_7_33 = STICKER_33 + 7;
  public static final int STICKER_8_33 = STICKER_33 + 8;
  public static final int STICKER_9_33 = STICKER_33 + 9;
  // 斜塔
  public static final int STICKER_34 = 297;
  public static final int STICKER_1_34 = STICKER_34 + 1;
  public static final int STICKER_2_34 = STICKER_34 + 2;
  public static final int STICKER_3_34 = STICKER_34 + 3;
  public static final int STICKER_4_34 = STICKER_34 + 4;
  public static final int STICKER_5_34 = STICKER_34 + 5;
  public static final int STICKER_6_34 = STICKER_34 + 6;
  public static final int STICKER_7_34 = STICKER_34 + 7;
  public static final int STICKER_8_34 = STICKER_34 + 8;
  public static final int STICKER_9_34 = STICKER_34 + 9;
  // 大笨钟
  public static final int STICKER_35 = 306;
  public static final int STICKER_1_35 = STICKER_35 + 1;
  public static final int STICKER_2_35 = STICKER_35 + 2;
  public static final int STICKER_3_35 = STICKER_35 + 3;
  public static final int STICKER_4_35 = STICKER_35 + 4;
  public static final int STICKER_5_35 = STICKER_35 + 5;
  public static final int STICKER_6_35 = STICKER_35 + 6;
  public static final int STICKER_7_35 = STICKER_35 + 7;
  public static final int STICKER_8_35 = STICKER_35 + 8;
  public static final int STICKER_9_35 = STICKER_35 + 9;
  // 大桥
  public static final int STICKER_36 = 315;
  public static final int STICKER_1_36 = STICKER_36 + 1;
  public static final int STICKER_2_36 = STICKER_36 + 2;
  public static final int STICKER_3_36 = STICKER_36 + 3;
  public static final int STICKER_4_36 = STICKER_36 + 4;
  public static final int STICKER_5_36 = STICKER_36 + 5;
  public static final int STICKER_6_36 = STICKER_36 + 6;
  public static final int STICKER_7_36 = STICKER_36 + 7;
  public static final int STICKER_8_36 = STICKER_36 + 8;
  public static final int STICKER_9_36 = STICKER_36 + 9;

}
