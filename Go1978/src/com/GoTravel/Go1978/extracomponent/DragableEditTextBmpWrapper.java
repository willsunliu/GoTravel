package com.GoTravel.Go1978.extracomponent;


import android.graphics.Bitmap;


/**
 * 封装了可拖动图片的信息
 * 
 * @author Wilson 20131228
 */
public class DragableEditTextBmpWrapper
{
  private Bitmap bmp;
  private int topX, topY;

  public DragableEditTextBmpWrapper(Bitmap bmp, int topX, int topY)
  {
    this.bmp = bmp;
    this.topX = topX;
    this.topY = topY;
  }

  /**
   * 检查bitmap是否存在
   * 
   * @return 存在返回ture，否则返回false
   */
  public boolean checkBitmap()
  {
    if(bmp != null && !bmp.isRecycled())
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  /**
   * 回收bitmap
   */
  public void recycleBmp()
  {
    if(bmp != null && !bmp.isRecycled())
    {
      bmp.recycle();
      bmp = null;
    }
  }

  /**
   * 获取bitmap的topX
   * 
   * @return topX
   */
  public int getTopX()
  {
    return this.topX;
  }

  /**
   * 获取bitmap的topY
   * 
   * @return topY
   */
  public int getTopY()
  {
    return this.topY;
  }

  /**
   * 获取bitmap
   * 
   * @return bitmap
   */
  public Bitmap getBmp()
  {
    return this.bmp;
  }
}
