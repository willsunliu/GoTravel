package com.GoTravel.Go1978.extracomponent;


import android.graphics.Bitmap;


/**
 * 图片封装类
 * 
 * @author Wilson 20140110
 */
public class PhotoWrapper
{
  // 图片路径
  private String path;
  // 显示图片的缩放比例
  private float scaling;
  // 缩放后的图片
  private Bitmap scaledBitmap;

  /**
   * @return the path
   */
  public String getPath()
  {
    return path;
  }

  /**
   * @param path the path to set
   */
  public void setPath(String path)
  {
    this.path = path;
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
   * @return the scaledBitmap
   */
  public Bitmap getScaledBitmap()
  {
    return scaledBitmap;
  }

  /**
   * @param scaledBitmap the scaledBitmap to set
   */
  public void setScaledBitmap(Bitmap scaledBitmap)
  {
    this.scaledBitmap = scaledBitmap;
  }

  /**
   * 释放图片资源
   */
  public void release()
  {
    if(scaledBitmap != null && scaledBitmap.isRecycled())
    {
      scaledBitmap.recycle();
    }
  }

}
