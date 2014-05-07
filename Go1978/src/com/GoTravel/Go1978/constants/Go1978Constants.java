package com.GoTravel.Go1978.constants;


import android.graphics.Color;
import android.graphics.Typeface;

import com.GoTravel.Go1978.R;


/**
 * Go1978涉及的参数，以及资源文件的ID
 * 
 * @author IT01
 */
public class Go1978Constants
{
  // 工具栏的层级
  public static int LEVEL_1 = 1;
  public static int LEVEL_2 = 2;

  // 工具栏的类型
  public static int COLORING = 1;
  public static int FRAME = 2;
  public static int STICKER = 3;
  public static int TEXT = 4;

  /* Filter */
  // 设置滤镜按钮的基准View ID
  public static final int FILTER_BASE_ID = 100;
  // 滤镜的资源ID
  public static String filters[] = {"filters/filter_1.png",
      "filters/filter_2.png", "filters/filter_3.png",};

  /* Frame */
  // 设置相框按钮的基准ID
  public static final int FRAME_BASE_ID = 200;
  // 相框的资源ID
  public static String frames[] = {"frames/frame_1.png", "frames/frame_2.png",
      "frames/frame_3.png", "frames/frame_4.png", "frames/frame_5.png",
      "frames/frame_6.png", "frames/frame_7.png", "frames/frame_8.png",
      "frames/frame_9.png",};
  // 相框数组的下标
  public static final int FRAME_CORNER_WIDTH = 0;
  public static final int FRAME_CORNER_HEIGHT = 1;
  public static final int FRAME_BORDER_WIDTH = 2;
  public static final int FRAME_BORDER_HEIGHT = 3;
  public static final int CROP_LEFT_BORDER_X = 4;
  public static final int CROP_LEFT_BORDER_Y = 5;
  public static final int CROP_TOP_BORDER_X = 6;
  public static final int CROP_TOP_BORDER_Y = 7;
  public static final int CROP_RIGHT_BORDER_X = 8;
  public static final int CROP_RIGHT_BORDER_Y = 9;
  public static final int CROP_BOTTOM_BORDER_X = 10;
  public static final int CROP_BOTTOM_BORDER_Y = 11;
  /*
   * 数组每个元素代表的意思：
   * 0. 截取相框角的矩形所需的宽度
   * 1. 截取相框角的矩形所需的高度
   * 2. 截取相框边的矩形所需的宽度
   * 3. 截取相框边的矩形所需的高度
   * 4. 截取相框左边的矩形的起始x坐标
   * 5. 截取相框左边的矩形的起始y坐标
   * 6. 截取相框上边的矩形的起始x坐标
   * 7. 截取相框上边的矩形的起始y坐标
   * 8. 截取相框右边的矩形的起始x坐标
   * 9. 截取相框右边的矩形的起始y坐标
   * 10. 截取相框下边的矩形的起始x坐标
   * 11. 截取相框下边的矩形的起始y坐标
   */
  public static int frame1[] = {100, 100, 300, 300, 0, 0, 0, 0, 0, 0, 0, 0,};
  public static int frame2[] = {240, 240, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0,};
  public static int frame3[] = {240, 240, 80, 80, 0, 0, 0, 0, 0, 0, 0, 0,};
  public static int frame4[] = {300, 300, 80, 80, 0, 0, 50, 0, 0, 0, 50, 0,};
  public static int frame5[] = {240, 240, 80, 80, 0, 0, 0, 0, 0, 0, 0, 0,};
  public static int frame6[] = {240, 240, 80, 80, 0, 0, 0, 0, 0, 0, 0, 0,};
  public static int frame7[] = {240, 240, 80, 80, 0, 0, 0, 0, 0, 0, 0, 0,};
  public static int frame8[] = {240, 240, 80, 80, 0, 0, 0, 0, 0, 0, 0, 0,};
  public static int frame9[] = {240, 240, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0,};

  // 设置第一层Sticker按钮的基准View ID
  public static final int STICKER_LEVEL_1_BASE_ID = 300;
  // 第一层Sticker的资源ID
  public static String stickers[] = {"stickers1/sticker_1.png",
      "stickers1/sticker_2.png", "stickers1/sticker_3.png",
      "stickers1/sticker_4.png", "stickers1/sticker_5.png",
      "stickers1/sticker_6.png", "stickers1/sticker_7.png",
      "stickers1/sticker_8.png", "stickers1/sticker_9.png",
      "stickers1/sticker_10.png", "stickers1/sticker_11.png",
      "stickers1/sticker_12.png", "stickers1/sticker_13.png",
      "stickers1/sticker_14.png", "stickers1/sticker_15.png",
      "stickers1/sticker_16.png", "stickers1/sticker_17.png",
      "stickers1/sticker_18.png", "stickers1/sticker_19.png",
      "stickers1/sticker_20.png", "stickers1/sticker_21.png",
      "stickers1/sticker_22.png", "stickers1/sticker_23.png",
      "stickers1/sticker_24.png", "stickers1/sticker_25.png",
      "stickers1/sticker_26.png", "stickers1/sticker_27.png",
      "stickers1/sticker_28.png", "stickers1/sticker_29.png",
      "stickers1/sticker_30.png", "stickers1/sticker_31.png",
      "stickers1/sticker_32.png", "stickers1/sticker_33.png",
      "stickers1/sticker_34.png", "stickers1/sticker_35.png",
      "stickers1/sticker_36.png",};
  // 设置第二层Sticker按钮的基准View ID
  public static final int STICKER_LEVEL_2_BASE_ID = 400;

  // 第二层Sticker的资源ID
  public static String sticker_1s[] = {"stickers2/sticker_1_1.png",
      "stickers2/sticker_2_1.png", "stickers2/sticker_3_1.png",
      "stickers2/sticker_4_1.png", "stickers2/sticker_5_1.png",
      "stickers2/sticker_6_1.png", "stickers2/sticker_7_1.png",
      "stickers2/sticker_8_1.png", "stickers2/sticker_9_1.png",};
  public static String sticker_2s[] = {"stickers2/sticker_1_2.png",
      "stickers2/sticker_2_2.png", "stickers2/sticker_3_2.png",
      "stickers2/sticker_4_2.png", "stickers2/sticker_5_2.png",
      "stickers2/sticker_6_2.png", "stickers2/sticker_7_2.png",
      "stickers2/sticker_8_2.png", "stickers2/sticker_9_2.png",};
  public static String sticker_3s[] = {"stickers2/sticker_1_3.png",
      "stickers2/sticker_2_3.png", "stickers2/sticker_3_3.png",
      "stickers2/sticker_4_3.png", "stickers2/sticker_5_3.png",
      "stickers2/sticker_6_3.png", "stickers2/sticker_7_3.png",
      "stickers2/sticker_8_3.png", "stickers2/sticker_9_3.png",};
  public static String sticker_4s[] = {"stickers2/sticker_1_4.png",
      "stickers2/sticker_2_4.png", "stickers2/sticker_3_4.png",
      "stickers2/sticker_4_4.png", "stickers2/sticker_5_4.png",
      "stickers2/sticker_6_4.png", "stickers2/sticker_7_4.png",
      "stickers2/sticker_8_4.png", "stickers2/sticker_9_4.png",};
  public static String sticker_5s[] = {"stickers2/sticker_1_5.png",
      "stickers2/sticker_2_5.png", "stickers2/sticker_3_5.png",
      "stickers2/sticker_4_5.png", "stickers2/sticker_5_5.png",
      "stickers2/sticker_6_5.png", "stickers2/sticker_7_5.png",
      "stickers2/sticker_8_5.png", "stickers2/sticker_9_5.png",};
  public static String sticker_6s[] = {"stickers2/sticker_1_6.png",
      "stickers2/sticker_2_6.png", "stickers2/sticker_3_6.png",
      "stickers2/sticker_4_6.png", "stickers2/sticker_5_6.png",
      "stickers2/sticker_6_6.png", "stickers2/sticker_7_6.png",
      "stickers2/sticker_8_6.png", "stickers2/sticker_9_6.png",};
  public static String sticker_7s[] = {"stickers2/sticker_1_7.png",
      "stickers2/sticker_2_7.png", "stickers2/sticker_3_7.png",
      "stickers2/sticker_4_7.png", "stickers2/sticker_5_7.png",
      "stickers2/sticker_6_7.png", "stickers2/sticker_7_7.png",
      "stickers2/sticker_8_7.png", "stickers2/sticker_9_7.png",};
  public static String sticker_8s[] = {"stickers2/sticker_1_8.png",
      "stickers2/sticker_2_8.png", "stickers2/sticker_3_8.png",
      "stickers2/sticker_4_8.png", "stickers2/sticker_5_8.png",
      "stickers2/sticker_6_8.png", "stickers2/sticker_7_8.png",
      "stickers2/sticker_8_8.png", "stickers2/sticker_9_8.png",};
  public static String sticker_9s[] = {"stickers2/sticker_1_9.png",
      "stickers2/sticker_2_9.png", "stickers2/sticker_3_9.png",
      "stickers2/sticker_4_9.png", "stickers2/sticker_5_9.png",
      "stickers2/sticker_6_9.png", "stickers2/sticker_7_9.png",
      "stickers2/sticker_8_9.png", "stickers2/sticker_9_9.png",};
  public static String sticker_10s[] = {"stickers2/sticker_1_10.png",
      "stickers2/sticker_2_10.png", "stickers2/sticker_3_10.png",
      "stickers2/sticker_4_10.png", "stickers2/sticker_5_10.png",
      "stickers2/sticker_6_10.png", "stickers2/sticker_7_10.png",
      "stickers2/sticker_8_10.png", "stickers2/sticker_9_10.png",};
  public static String sticker_11s[] = {"stickers2/sticker_1_11.png",
      "stickers2/sticker_2_11.png", "stickers2/sticker_3_11.png",
      "stickers2/sticker_4_11.png", "stickers2/sticker_5_11.png",
      "stickers2/sticker_6_11.png", "stickers2/sticker_7_11.png",
      "stickers2/sticker_8_11.png", "stickers2/sticker_9_11.png",};
  public static String sticker_12s[] = {"stickers2/sticker_1_12.png",
      "stickers2/sticker_2_12.png", "stickers2/sticker_3_12.png",
      "stickers2/sticker_4_12.png", "stickers2/sticker_5_12.png",
      "stickers2/sticker_6_12.png", "stickers2/sticker_7_12.png",
      "stickers2/sticker_8_12.png", "stickers2/sticker_9_12.png",};
  public static String sticker_13s[] = {"stickers2/sticker_1_13.png",
      "stickers2/sticker_2_13.png", "stickers2/sticker_3_13.png",
      "stickers2/sticker_4_13.png", "stickers2/sticker_5_13.png",
      "stickers2/sticker_6_13.png", "stickers2/sticker_7_13.png",
      "stickers2/sticker_8_13.png", "stickers2/sticker_9_13.png",};
  public static String sticker_14s[] = {"stickers2/sticker_1_14.png",
      "stickers2/sticker_2_14.png", "stickers2/sticker_3_14.png",
      "stickers2/sticker_4_14.png", "stickers2/sticker_5_14.png",
      "stickers2/sticker_6_14.png", "stickers2/sticker_7_14.png",
      "stickers2/sticker_8_14.png", "stickers2/sticker_9_14.png",};
  public static String sticker_15s[] = {"stickers2/sticker_1_15.png",
      "stickers2/sticker_2_15.png", "stickers2/sticker_3_15.png",
      "stickers2/sticker_4_15.png", "stickers2/sticker_5_15.png",
      "stickers2/sticker_6_15.png", "stickers2/sticker_7_15.png",
      "stickers2/sticker_8_15.png", "stickers2/sticker_9_15.png",};
  public static String sticker_16s[] = {"stickers2/sticker_1_16.png",
      "stickers2/sticker_2_16.png", "stickers2/sticker_3_16.png",
      "stickers2/sticker_4_16.png", "stickers2/sticker_5_16.png",
      "stickers2/sticker_6_16.png", "stickers2/sticker_7_16.png",
      "stickers2/sticker_8_16.png", "stickers2/sticker_9_16.png",};
  public static String sticker_17s[] = {"stickers2/sticker_1_17.png",
      "stickers2/sticker_2_17.png", "stickers2/sticker_3_17.png",
      "stickers2/sticker_4_17.png", "stickers2/sticker_5_17.png",
      "stickers2/sticker_6_17.png", "stickers2/sticker_7_17.png",
      "stickers2/sticker_8_17.png", "stickers2/sticker_9_17.png",};
  public static String sticker_18s[] = {"stickers2/sticker_1_18.png",
      "stickers2/sticker_2_18.png", "stickers2/sticker_3_18.png",
      "stickers2/sticker_4_18.png", "stickers2/sticker_5_18.png",
      "stickers2/sticker_6_18.png", "stickers2/sticker_7_18.png",
      "stickers2/sticker_8_18.png", "stickers2/sticker_9_18.png",};
  public static String sticker_19s[] = {"stickers2/sticker_1_19.png",
      "stickers2/sticker_2_19.png", "stickers2/sticker_3_19.png",
      "stickers2/sticker_4_19.png", "stickers2/sticker_5_19.png",
      "stickers2/sticker_6_19.png", "stickers2/sticker_7_19.png",
      "stickers2/sticker_8_19.png", "stickers2/sticker_9_19.png",};
  public static String sticker_20s[] = {"stickers2/sticker_1_20.png",
      "stickers2/sticker_2_20.png", "stickers2/sticker_3_20.png",
      "stickers2/sticker_4_20.png", "stickers2/sticker_5_20.png",
      "stickers2/sticker_6_20.png", "stickers2/sticker_7_20.png",
      "stickers2/sticker_8_20.png", "stickers2/sticker_9_20.png",};
  public static String sticker_21s[] = {"stickers2/sticker_1_21.png",
      "stickers2/sticker_2_21.png", "stickers2/sticker_3_21.png",
      "stickers2/sticker_4_21.png", "stickers2/sticker_5_21.png",
      "stickers2/sticker_6_21.png", "stickers2/sticker_7_21.png",
      "stickers2/sticker_8_21.png", "stickers2/sticker_9_21.png",};
  public static String sticker_22s[] = {"stickers2/sticker_1_22.png",
      "stickers2/sticker_2_22.png", "stickers2/sticker_3_22.png",
      "stickers2/sticker_4_22.png", "stickers2/sticker_5_22.png",
      "stickers2/sticker_6_22.png", "stickers2/sticker_7_22.png",
      "stickers2/sticker_8_22.png", "stickers2/sticker_9_22.png",};
  public static String sticker_23s[] = {"stickers2/sticker_1_23.png",
      "stickers2/sticker_2_23.png", "stickers2/sticker_3_23.png",
      "stickers2/sticker_4_23.png", "stickers2/sticker_5_23.png",
      "stickers2/sticker_6_23.png", "stickers2/sticker_7_23.png",
      "stickers2/sticker_8_23.png", "stickers2/sticker_9_23.png",};
  public static String sticker_24s[] = {"stickers2/sticker_1_24.png",
      "stickers2/sticker_2_24.png", "stickers2/sticker_3_24.png",
      "stickers2/sticker_4_24.png", "stickers2/sticker_5_24.png",
      "stickers2/sticker_6_24.png", "stickers2/sticker_7_24.png",
      "stickers2/sticker_8_24.png", "stickers2/sticker_9_24.png",};
  public static String sticker_25s[] = {"stickers2/sticker_1_25.png",
      "stickers2/sticker_2_25.png", "stickers2/sticker_3_25.png",
      "stickers2/sticker_4_25.png", "stickers2/sticker_5_25.png",
      "stickers2/sticker_6_25.png", "stickers2/sticker_7_25.png",
      "stickers2/sticker_8_25.png", "stickers2/sticker_9_25.png",};
  public static String sticker_26s[] = {"stickers2/sticker_1_26.png",
      "stickers2/sticker_2_26.png", "stickers2/sticker_3_26.png",
      "stickers2/sticker_4_26.png", "stickers2/sticker_5_26.png",
      "stickers2/sticker_6_26.png", "stickers2/sticker_7_26.png",
      "stickers2/sticker_8_26.png", "stickers2/sticker_9_26.png",};
  public static String sticker_27s[] = {"stickers2/sticker_1_27.png",
      "stickers2/sticker_2_27.png", "stickers2/sticker_3_27.png",
      "stickers2/sticker_4_27.png", "stickers2/sticker_5_27.png",
      "stickers2/sticker_6_27.png", "stickers2/sticker_7_27.png",
      "stickers2/sticker_8_27.png", "stickers2/sticker_9_27.png",};
  public static String sticker_28s[] = {"stickers2/sticker_1_28.png",
      "stickers2/sticker_2_28.png", "stickers2/sticker_3_28.png",
      "stickers2/sticker_4_28.png", "stickers2/sticker_5_28.png",
      "stickers2/sticker_6_28.png", "stickers2/sticker_7_28.png",
      "stickers2/sticker_8_28.png", "stickers2/sticker_9_28.png",};
  public static String sticker_29s[] = {"stickers2/sticker_1_29.png",
      "stickers2/sticker_2_29.png", "stickers2/sticker_3_29.png",
      "stickers2/sticker_4_29.png", "stickers2/sticker_5_29.png",
      "stickers2/sticker_6_29.png", "stickers2/sticker_7_29.png",
      "stickers2/sticker_8_29.png", "stickers2/sticker_9_29.png",};
  public static String sticker_30s[] = {"stickers2/sticker_1_30.png",
      "stickers2/sticker_2_30.png", "stickers2/sticker_3_30.png",
      "stickers2/sticker_4_30.png", "stickers2/sticker_5_30.png",
      "stickers2/sticker_6_30.png", "stickers2/sticker_7_30.png",
      "stickers2/sticker_8_30.png", "stickers2/sticker_9_30.png",};
  public static String sticker_31s[] = {"stickers2/sticker_1_31.png",
      "stickers2/sticker_2_31.png", "stickers2/sticker_3_31.png",
      "stickers2/sticker_4_31.png", "stickers2/sticker_5_31.png",
      "stickers2/sticker_6_31.png", "stickers2/sticker_7_31.png",
      "stickers2/sticker_8_31.png", "stickers2/sticker_9_31.png",};
  public static String sticker_32s[] = {"stickers2/sticker_1_32.png",
      "stickers2/sticker_2_32.png", "stickers2/sticker_3_32.png",
      "stickers2/sticker_4_32.png", "stickers2/sticker_5_32.png",
      "stickers2/sticker_6_32.png", "stickers2/sticker_7_32.png",
      "stickers2/sticker_8_32.png", "stickers2/sticker_9_32.png",};
  public static String sticker_33s[] = {"stickers2/sticker_1_33.png",
      "stickers2/sticker_2_33.png", "stickers2/sticker_3_33.png",
      "stickers2/sticker_4_33.png", "stickers2/sticker_5_33.png",
      "stickers2/sticker_6_33.png", "stickers2/sticker_7_33.png",
      "stickers2/sticker_8_33.png", "stickers2/sticker_9_33.png",};
  public static String sticker_34s[] = {"stickers2/sticker_1_34.png",
      "stickers2/sticker_2_34.png", "stickers2/sticker_3_34.png",
      "stickers2/sticker_4_34.png", "stickers2/sticker_5_34.png",
      "stickers2/sticker_6_34.png", "stickers2/sticker_7_34.png",
      "stickers2/sticker_8_34.png", "stickers2/sticker_9_34.png",};
  public static String sticker_35s[] = {"stickers2/sticker_1_35.png",
      "stickers2/sticker_2_35.png", "stickers2/sticker_3_35.png",
      "stickers2/sticker_4_35.png", "stickers2/sticker_5_35.png",
      "stickers2/sticker_6_35.png", "stickers2/sticker_7_35.png",
      "stickers2/sticker_8_35.png", "stickers2/sticker_9_35.png",};
  public static String sticker_36s[] = {"stickers2/sticker_1_36.png",
      "stickers2/sticker_2_36.png", "stickers2/sticker_3_36.png",
      "stickers2/sticker_4_36.png", "stickers2/sticker_5_36.png",
      "stickers2/sticker_6_36.png", "stickers2/sticker_7_36.png",
      "stickers2/sticker_8_36.png", "stickers2/sticker_9_36.png",};

  public static final String[][] allStickers = {sticker_1s, sticker_2s,
      sticker_3s, sticker_4s, sticker_5s, sticker_6s, sticker_7s, sticker_8s,
      sticker_9s, sticker_10s, sticker_11s, sticker_12s, sticker_13s,
      sticker_14s, sticker_15s, sticker_16s, sticker_17s, sticker_18s,
      sticker_19s, sticker_20s, sticker_21s, sticker_22s, sticker_23s,
      sticker_24s, sticker_25s, sticker_26s, sticker_27s, sticker_28s,
      sticker_29s, sticker_30s, sticker_31s, sticker_32s, sticker_33s,
      sticker_34s, sticker_35s, sticker_36s,};

  // 设置第一层文字按钮的基准View ID
  public static final int TEXT_LEVEL_1_BASE_ID = 500;
  // 字体数组
  public static Typeface ttfs[] = {
      Typeface.create(Typeface.DEFAULT, Typeface.NORMAL),
      Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL),
      Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL),
      Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL),
      Typeface.create(Typeface.SERIF, Typeface.NORMAL),
      Typeface.create(Typeface.DEFAULT, Typeface.BOLD),
      Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD),
      Typeface.create(Typeface.MONOSPACE, Typeface.BOLD),
      Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD),
      Typeface.create(Typeface.SERIF, Typeface.BOLD),
      Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC),
      Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD_ITALIC),
      Typeface.create(Typeface.MONOSPACE, Typeface.BOLD_ITALIC),
      Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC),
      Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC),
      Typeface.create(Typeface.DEFAULT, Typeface.ITALIC),
      Typeface.create(Typeface.DEFAULT_BOLD, Typeface.ITALIC),
      Typeface.create(Typeface.MONOSPACE, Typeface.ITALIC),
      Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC),
      Typeface.create(Typeface.SERIF, Typeface.ITALIC),};
  // 设置第二层文字按钮的基准View ID
  public static final int TEXT_LEVEL_2_BASE_ID = 600;
  // 文字颜色数组
  public static Integer text_colors[] = {Integer.valueOf(Color.BLACK),
      Integer.valueOf(Color.BLUE), Integer.valueOf(Color.CYAN),
      Integer.valueOf(Color.DKGRAY), Integer.valueOf(Color.GRAY),
      Integer.valueOf(Color.GREEN), Integer.valueOf(Color.LTGRAY),
      Integer.valueOf(Color.MAGENTA), Integer.valueOf(Color.RED),
      Integer.valueOf(Color.WHITE), Integer.valueOf(Color.YELLOW),};
  // public static String ttfs[] = {
  // "fonts/AndroidClock.ttf",
  // "fonts/AndroidClock_Highlight.ttf",
  // "fonts/AndroidClock_Solid.ttf",
  // "fonts/AndroidEmoji.ttf",
  // "fonts/Clockopia.ttf",
  // "fonts/DroidKufi-Bold.ttf",
  // "fonts/DroidKufi-Regular.ttf",
  // "fonts/DroidNaskh-Bold.ttf",
  // "fonts/DroidNaskh-Regular.ttf",
  // "fonts/DroidNaskhUI-Regular.ttf",
  // "fonts/DroidSans-Bold.ttf",
  // "fonts/DroidSans.ttf",
  // "fonts/DroidSansArabic.ttf",
  // "fonts/DroidSansArmenian.ttf",
  // "fonts/DroidSansEthiopic-Bold.ttf",
  // "fonts/DroidSansEthiopic-Regular.ttf",
  // "fonts/DroidSansFallback.ttf",
  // "fonts/DroidSansFallbackFull.ttf",
  // "fonts/DroidSansFallbackLegacy.ttf",
  // "fonts/DroidSansGeorgian.ttf",
  // "fonts/DroidSansHebrew-Bold.ttf",
  // "fonts/DroidSansHebrew-Regular.ttf",
  // "fonts/DroidSansJapanese.ttf",
  // "fonts/DroidSansMono.ttf",
  // "fonts/DroidSerif-Bold.ttf",
  // "fonts/DroidSerif-BoldItalic.ttf",
  // "fonts/DroidSerif-Italic.ttf",
  // "fonts/DroidSerif-Regular.ttf",
  // "fonts/MTLc3m.ttf",
  // "fonts/MTLmr3m.ttf",
  // "fonts/Roboto-Bold.ttf",
  // "fonts/Roboto-BoldItalic.ttf",
  // "fonts/Roboto-Italic.ttf",
  // "fonts/Roboto-Light.ttf",
  // "fonts/Roboto-LightItalic.ttf",
  // "fonts/Roboto-Regular.ttf",
  // "fonts/Roboto-Thin.ttf",
  // "fonts/Roboto-ThinItalic.ttf",
  // "fonts/RobotoCondensed-Bold.ttf",
  // "fonts/RobotoCondensed-BoldItalic.ttf",
  // "fonts/RobotoCondensed-Italic.ttf",
  // "fonts/RobotoCondensed-Regular.ttf",
  // };
}
