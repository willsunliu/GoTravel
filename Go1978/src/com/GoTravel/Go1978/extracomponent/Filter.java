package com.GoTravel.Go1978.extracomponent;


import com.GoTravel.Go1978.log.MyLog;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;


/**
 * 关于滤镜的类
 * 
 * @author Wilson 20140221
 */
public class Filter
{
  private static final String TAG = "Filter";

  // 用作数组下标
  public static final int RED = 0;
  public static final int GREEN = 1;
  public static final int BLUE = 2;

  // 记录当前滤镜
  public static int currentColor = 0;

  public static final int NO_COLOR = 0;

  /*
   * R(r): Red G(g): Green B(b): Blue
   * Setting1：
   * R = 1.43r + (-0.46)g + 0.22b
   * G = 0.14r + 0.81g + 0.24b
   * B = 0.06r + 0.04g + 0.75b
   */
  public static final int COLOR_1 = 1;
  public static final int color1[][] = { {143, -46, 22,}, {14, 81, 24,},
      {6, 4, 75,},};

  /*
   * R(r): Red G(g): Green B(b): Blue
   * Setting2：
   * R = 1.88r + 0.79g + (-1.03)b
   * G = (-0.08)r + 1.01g + 0.08b
   * B = 0.18r + 0.16g + 0.81b
   */
  public static final int COLOR_2 = 2;
  public static final int color2[][] = { {188, 79, -103,}, {-8, 101, 8,},
      {18, 16, 81,},};

  /*
   * R(r): Red G(g): Green B(b): Blue
   * Setting3：
   * R = (-0.42)r + 1.96g + 0.08b
   * G = (-0.08)r + 1.17g + (-0.1)b
   * B = 0.55r + 0.36g + 0.2b
   */
  public static final int COLOR_3 = 3;
  public static final int color3[][] = { {-42, 196, 8,}, {-8, 117, -10,},
      {55, 36, 20,},};

  /**
   * 生成添加滤镜后的bitmap
   * 
   * @param red 设置滤镜的RED的参数
   * @param green 设置滤镜的GREEN的参数
   * @param blue 设置滤镜的BLUE的参数
   * @return 返回添加滤镜效果后的bitmap
   */
  public static Bitmap getFilterBitmap(Bitmap srcBmp, float[] red,
      float[] green, float[] blue)
  {
    MyLog.i(TAG, "getFilterBitmap");
    long start = System.currentTimeMillis();
    int width = srcBmp.getWidth();
    int height = srcBmp.getHeight();
    Bitmap bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
    int pixColor = 0;
    int pixR = 0;
    int pixG = 0;
    int pixB = 0;
    int newR = 0;
    int newG = 0;
    int newB = 0;
    int[] pixels = new int[width * height];
    srcBmp.getPixels(pixels, 0, width, 0, 0, width, height);
    for(int i = 0; i < height; i++)
    {
      for(int j = 0; j < width; j++)
      {
        pixColor = pixels[width * i + j];
        pixR = Color.red(pixColor);
        pixG = Color.green(pixColor);
        pixB = Color.blue(pixColor);
        newR =
            Math.min(
                255,
                Math.max(
                    0,
                    (int) Math.floor(red[0] * pixR + red[1] * pixG + red[2]
                        * pixB)));
        newG =
            Math.min(
                255,
                Math.max(
                    0,
                    (int) Math.floor(green[0] * pixR + green[1] * pixG
                        + green[2] * pixB)));
        newB =
            Math.min(
                255,
                Math.max(
                    0,
                    (int) Math.floor(blue[0] * pixR + blue[1] * pixG + blue[2]
                        * pixB)));
        int newColor = Color.argb(255, newR, newG, newB);
        pixels[width * i + j] = newColor;
      }
    }

    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    long end = System.currentTimeMillis();
    MyLog.d(TAG, "Used time=" + (end - start));

    return bitmap;
  }

  /**
   * 将百分数转为小数
   * 
   * @param red 设置滤镜的RED的参数
   * @param green 设置滤镜的GREEN的参数
   * @param blue 设置滤镜的BLUE的参数
   * @return 返回转换后的数组
   */
  private static float[] getRgbArray(int red, int green, int blue)
  {
    float[] rgb = {red * 0.01f, green * 0.01f, blue * 0.01f};
    return rgb;
  }

  /**
   * 设置滤镜效果
   * 
   * @param filter 滤镜标识
   */
  public static Bitmap setFilter(Bitmap srcBmp, int filter)
  {
    switch(filter)
    {
      case NO_COLOR:
        currentColor = NO_COLOR;
        return srcBmp;

      case COLOR_1:
        currentColor = COLOR_1;
        return getFilterBitmap(
            srcBmp,
            getRgbArray(color1[RED][RED], color1[RED][GREEN], color1[RED][BLUE]),
            getRgbArray(color1[GREEN][RED], color1[GREEN][GREEN],
                color1[GREEN][BLUE]),
            getRgbArray(color1[BLUE][RED], color1[BLUE][GREEN],
                color1[BLUE][BLUE]));
      case COLOR_2:
        currentColor = COLOR_2;
        return getFilterBitmap(
            srcBmp,
            getRgbArray(color2[RED][RED], color2[RED][GREEN], color2[RED][BLUE]),
            getRgbArray(color2[GREEN][RED], color2[GREEN][GREEN],
                color2[GREEN][BLUE]),
            getRgbArray(color2[BLUE][RED], color2[BLUE][GREEN],
                color2[BLUE][BLUE]));
      case COLOR_3:
        currentColor = COLOR_3;
        return getFilterBitmap(
            srcBmp,
            getRgbArray(color3[RED][RED], color3[RED][GREEN], color3[RED][BLUE]),
            getRgbArray(color3[GREEN][RED], color3[GREEN][GREEN],
                color3[GREEN][BLUE]),
            getRgbArray(color3[BLUE][RED], color3[BLUE][GREEN],
                color3[BLUE][BLUE]));
      default:
        return srcBmp;
    }
  }
}
