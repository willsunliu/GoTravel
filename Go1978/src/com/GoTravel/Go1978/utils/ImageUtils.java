package com.GoTravel.Go1978.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;

import com.GoTravel.Go1978.R;
import com.GoTravel.Go1978.extracomponent.PhotoWrapper;
import com.GoTravel.Go1978.log.MyLog;


public class ImageUtils
{
  private static final String TAG = "ImageFileUtils";

  public static final String ALBUM_NAME = "GoTravel";
  private static final String CAMERA_DIR = "/dcim/";
  private static final String JPEG_FILE_PREFIX = "IMG_";
  private static final String JPEG_FILE_SUFFIX = ".jpg";
  // 被编辑过的图片名的前缀
  public static final String EDITED_PIC_PREFIX = "EDITED_";

  /**
   * 创建用于保存图片的文件
   * 
   * @return 用于保存文件的图片
   * @throws IOException
   */
  public static File createPhotoFile() throws IOException
  {
    String timeStamp =
        new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(new Date());
    MyLog.i(TAG, "timeStamp="+timeStamp);
    String photoFileName = JPEG_FILE_PREFIX + timeStamp;
    MyLog.i(TAG, "photoFileName="+photoFileName);
    File albumF = getAlbumDir();
    File photoF = new File(albumF, photoFileName + JPEG_FILE_SUFFIX);
    
    return photoF;
  }

  /**
   * 获取相册路径
   * 
   * @return 相册路径
   */
  private static File getAlbumDir()
  {
    File storageDir = null;
    if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
    {
      storageDir =
          new File(Environment.getExternalStorageDirectory() + CAMERA_DIR
              + ALBUM_NAME);

      if(storageDir != null)
      {
        if(!storageDir.mkdir())
        {
          if(!storageDir.exists())
          {
            MyLog.e(TAG, "create dir fail!");
            return null;
          }
        }
      }
    }
    else
    {
      MyLog.e(TAG, "External storage is not mounted READ/WRITE.");
    }

    return storageDir;
  }

  /**
   * 创建用于显示在编辑区域的图片
   * 
   * @param path 图片路径
   * @return
   */
  public static Bitmap createBitmap(String path)
  {
    boolean needRotate = false;

    Bitmap bmp = BitmapFactory.decodeFile(path);
    if(bmp.getWidth() > bmp.getHeight())
    {
      needRotate = true;
    }
    Matrix matrix = new Matrix();
    if(needRotate)
    {
      matrix.postRotate(90);
    }
    matrix.postScale(((float) 1) / 4, ((float) 1) / 4);
    bmp =
        Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix,
            false);
    return bmp;
  }

  /**
   * 旋转Bitmap
   * 
   * @param bmp Bitmap
   * @param degree 旋转角度
   * @return 旋转后的Bitmap
   */
  public static Bitmap rotateBitmap(Bitmap bmp, int degree)
  {
    Matrix matrix = new Matrix();
    matrix.postRotate(degree);
    bmp =
        Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix,
            true);
    return bmp;
  }

  /**
   * 读文件的EXIF信息，获取图片是否有旋转过，旋转的角度是多少
   * 
   * @param path
   * @return 旋转过的角度
   */
  private static int readPictureDegree(String path)
  {
    int degree = 0;
    try
    {
      ExifInterface exif = new ExifInterface(path);
      int orientation =
          exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
              ExifInterface.ORIENTATION_NORMAL);
      switch(orientation)
      {
        case ExifInterface.ORIENTATION_ROTATE_90:
          degree = 90;
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          degree = 180;
          break;
        case ExifInterface.ORIENTATION_ROTATE_270:
          degree = 270;
          break;
      }
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }

    return degree;
  }

  /**
   * 遍历samsungs数组，看exif是否代表三星的机型
   * 
   * @param context
   * @param exif
   * @return exif代表三星机型返回true，否则返回false
   */
  private static boolean matchSamSung(Context context, String exif)
  {
    String[] arr = context.getResources().getStringArray(R.array.samsungs);
    for(String model : arr)
    {
      if(model.equals(exif))
      {
        return true;
      }
    }

    return false;
  }

  /**
   * 获取图片EXIF信息中的TAG_MODEL
   * 
   * @param path
   * @return TAG_MODEL
   */
  private static String readEXIF(String path)
  {
    String sModel = "";
    try
    {
      ExifInterface exif = new ExifInterface(path);
      sModel = exif.getAttribute(ExifInterface.TAG_MODEL);
      MyLog.i(TAG, "sModel=" + sModel);
    }
    catch(IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return sModel;
  }

  /**
   * 度图片的EXIF信息，如果是三星设备拍摄的照片，获取旋转角度
   * 
   * @param context
   * @param path
   * @return 旋转的角度
   */
  public static int getRotationDegree(Context context, String path)
  {
    String exif = readEXIF(path);
    if(!(exif == null || exif.equals("")))
    {
      if(matchSamSung(context, exif))
      {
        return readPictureDegree(path);
      }
    }

    return 0;
  }

  /**
   * 创建Bitmap
   * 
   * @param path
   * @param screenW
   * @param screenH
   * @return
   */
  public static PhotoWrapper createPhotoWrapper(Context context, String path,
      int screenW, int screenH)
  {
    PhotoWrapper photoWrapper = new PhotoWrapper();
    photoWrapper.setPath(path);

    int degree = getRotationDegree(context, path);
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inPreferredConfig = Config.ARGB_8888;
    options.inPurgeable = true;
    options.inInputShareable = true;
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(path, options);
    int w = options.outWidth;
    int h = options.outHeight;
    MyLog.i(TAG, "w="+w);
    MyLog.i(TAG, "h="+h);
    MyLog.i(TAG, "degree=" + degree);
    int inSampleSize = 1;
    int heightRatio = 0, widthRatio = 0;
    if(degree != 90)
    {
      if(w > screenW || h > screenH)
      {
        heightRatio = (int) ((float) h / (float) screenH) + 1;
        widthRatio = (int) ((float) w / (float) screenW) + 1;
      }
    }
    else
    {
      if(w > screenH || h > screenW)
      {
        heightRatio = (int) ((float) w / (float) screenH) + 1;
        widthRatio = (int) ((float) h / (float) screenW) + 1;
        MyLog.i(TAG, "heightRatio="+heightRatio);
        MyLog.i(TAG, "widthRatio="+widthRatio);
      }
    }
    inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    options.inSampleSize = inSampleSize;
    options.inJustDecodeBounds = false;

    MyLog.i(TAG, "inSampleSize=" + inSampleSize);
    if(inSampleSize == 0)
    {
      photoWrapper.setScaling(1f);
    }
    else
    {
      photoWrapper.setScaling(1f / inSampleSize);
    }
    // photoWrapper.setScaling(1f / inSampleSize);

    Bitmap bmp;
    if(degree == 90)
    {
      bmp = rotateBitmap(BitmapFactory.decodeFile(path, options), degree);
    }
    else
    {
      bmp = BitmapFactory.decodeFile(path, options);
    }

    MyLog.i(TAG, "sw="+screenW);
    MyLog.i(TAG, "sh="+screenH);
    MyLog.i(TAG, "bmpw="+bmp.getWidth());
    MyLog.i(TAG, "bmph="+bmp.getHeight());
    photoWrapper.setScaledBitmap(bmp);

    return photoWrapper;
  }

  /**
   * 根据文件名后缀确定Http数据包包头的Content-Type
   * 
   * @param path 文件的路径
   * @return Content-Type
   */
  public static String getContentType(String path)
  {
    String contentType = null;

    if(path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".jpe"))
    {
      contentType = "image/jpeg";
    }
    else if(path.endsWith(".png"))
    {
      contentType = "image/png";
    }
    else if(path.endsWith(".gif"))
    {
      contentType = "image/gif";
    }
    else
    {
      contentType = "text/html";
    }

    return contentType;
  }

  /**
   * 获取图片的大小
   * 
   * @param path 图片路径
   * @return 图片的大小
   * @throws IOException
   */
  public static int getImageSize(String path) throws IOException
  {
    int size = 0;
    File file = new File(path);
    if(file.exists())
    {
      FileInputStream fis = new FileInputStream(file);
      size = fis.available();
    }
    return size;
  }
  
  /**
   * 把编辑后的图片添加到Gallery
   * 
   * @param path
   */
  public static void galleryAddPic(Context context, String path)
  {
    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    File f = new File(path);
    Uri contentUri = Uri.fromFile(f);
    mediaScanIntent.setData(contentUri);
    context.sendBroadcast(mediaScanIntent);
  }
}
