package com.GoTravel.Go1978;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.client.RedirectException;

import com.GoTravel.Go1978.GoTravelApplication.UPLOAD_TYPE;
import com.GoTravel.Go1978.constants.Go1978Constants;
import com.GoTravel.Go1978.extracomponent.Filter;
import com.GoTravel.Go1978.extracomponent.Frame;
import com.GoTravel.Go1978.extracomponent.PictureView;
import com.GoTravel.Go1978.extracomponent.Sticker;
import com.GoTravel.Go1978.log.MyLog;
import com.GoTravel.Go1978.utils.ImageUtils;
import com.GoTravel.Go1978.R;

import android.R.integer;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * PictureEditorActivity用于实现图片编辑的功能，其中包括：
 * 1. 添加滤镜
 * 2. 设置logo
 * 3. 添加sticker
 * 4. 添加文字
 * 
 * @author Wilson 20131228
 */
public class PictureEditorActivity extends BaseActivity
{
  private static final String TAG = "PictureEditorActivity";

  public static final int REQUEST_CODE_EDIT_TEXT = 1;
  public static final String BUNDLE_TEXT = "text";

  public static final int TOOL_BAR_PIXEL = 160;
  public static final int TOOL_PIXEL = 96;

  // Handler通知UI线程控件图片加载完成
  private static final int HANDLER_FINISH_LOADING_WIDGET = 0;
  // Handler通知UI线程刷新画布
  private static final int HANDLER_INVALIDATE = 1;
  // Handler通知UI线程隐藏ProgressDialog
  private static final int HANDLER_DISMISS_PROGRESS_DIALOG = 2;
  // Handler通知UI线程保存成功
  private static final int HANDLER_SAVE_SUCCESSFULLY = 3;

  // MyPictureView editor;
  // DragImageView imageView;
  ImageView imageColor, imageSticker, imageFrame, imageText, imageCancel,
      imageOK, imageBack;
  ProgressDialog progressDialog;
  GoTravelApplication application;
  ViewTreeObserver viewTreeObserver;

  // 用于显示编辑的内容
  PictureView pv;
  // 照片路径
  String picPath;

  // 用于显示编辑工具条
  LinearLayout toolBar;
  // 滤镜工具栏图片
  ArrayList<Bitmap> filters = new ArrayList<Bitmap>();
  // 相框工具栏图片
  ArrayList<Bitmap> frames = new ArrayList<Bitmap>();
  // Level 1 Sticker工具栏图片
  ArrayList<Bitmap> level1Stickers = new ArrayList<Bitmap>();
  // Level 2 Sticker工具栏图片
  ArrayList<Bitmap> level2Stickers = new ArrayList<Bitmap>();

  // 工具栏类型
  int toolBarType;
  // Sticker类型
  int stickerType;
  // Sticker颜色
  int stickerColor;
  // 文字类型
  int textType;
  // 文字的颜色
  int color = 0;
  // 滤镜类型
  int filter = -1;
  // 相框类型
  int type = -1;

  // 是否处于添加文字状态的标识
  boolean isAddingText = false;

  int topHeight = 0;

  Handler handler = new Handler()
  {

    /*
     * (non-Javadoc)
     * @see android.os.Handler#handleMessage(android.os.Message)
     */
    @Override
    public void handleMessage(Message msg)
    {
      switch(msg.what)
      {
        case HANDLER_FINISH_LOADING_WIDGET:
          pv.setPhotoPath(picPath);
          createImageGallery(Go1978Constants.LEVEL_1, Go1978Constants.COLORING,
              filters);
          progressDialog.dismiss();
          break;
        case HANDLER_INVALIDATE:
          pv.invalidate();
          progressDialog.dismiss();
          break;
        case HANDLER_DISMISS_PROGRESS_DIALOG:
          progressDialog.dismiss();
          break;
        case HANDLER_SAVE_SUCCESSFULLY:
          progressDialog.dismiss();
          Toast.makeText(getApplicationContext(),
              getResources().getString(R.string.save_ok), Toast.LENGTH_LONG)
              .show();
          break;

        default:
          break;
      }
      super.handleMessage(msg);
    }

  };

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pic_editor);
    MyLog.i(TAG, "onCreate");
    application = (GoTravelApplication) getApplication();

    // 获取图片路径，传递给PictureView生成图片并显示
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    String picPath =
        extras.getString(GoTravelApplication.BUNDLE_KEY_PHOTO_PATH);
    MyLog.i(TAG, "picPath=" + picPath);

    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage(getApplication().getString(R.string.loading));
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.show();

    pv = (PictureView) findViewById(R.id.picture_view);
    pv.setCurrentActivity(PictureEditorActivity.this);
    this.picPath = picPath;

    // 工具栏的回退按钮
    imageBack = (ImageView) findViewById(R.id.back_btn);
    imageBack.setOnClickListener(new OnClickListener()
    {

      public void onClick(View v)
      {
        if(toolBarType == Go1978Constants.STICKER)
        {
          int len = level2Stickers.size();
          for(int i = 0; i < len; i++)
          {
            Bitmap bmp = level2Stickers.get(i);
            if(bmp != null && !bmp.isRecycled())
            {
              bmp.recycle();
              bmp = null;
            }
          }
          level2Stickers.clear();
          System.gc();

          createImageGallery(Go1978Constants.LEVEL_1, Go1978Constants.STICKER,
              level1Stickers);
        }
        else if(toolBarType == Go1978Constants.TEXT)
        {
          createTextGallery(Go1978Constants.LEVEL_1, Go1978Constants.TEXT,
              Go1978Constants.ttfs);
          textType = -1;
        }
      }
    });

    // 工具栏
    toolBar = (LinearLayout) findViewById(R.id.btn_gallery);

    // 设置button
    setupImageView(imageColor, R.id.image_view_filter);
    setupImageView(imageFrame, R.id.image_view_frame);
    setupImageView(imageSticker, R.id.image_view_sticker);
    setupImageView(imageText, R.id.image_view_text);
    setupImageView(imageCancel, R.id.image_view_cancel);
    setupImageView(imageOK, R.id.image_view_ok);

    new Thread(new Runnable()
    {

      public void run()
      {
        loadingWidgetBitmap();
        Message msg = new Message();
        msg.what = HANDLER_FINISH_LOADING_WIDGET;
        handler.sendMessage(msg);
      }
    }).start();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    if(resultCode == RESULT_OK)
    {
      // 获取EditTextActivity返回的文字信息
      if(requestCode == REQUEST_CODE_EDIT_TEXT)
      {
        Bundle bundle = data.getExtras();
        String text = bundle.getString(BUNDLE_TEXT);
        MyLog.i(TAG, "text=" + text);
        pv.setText(text);
      }
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onPause()
  {
    MyLog.i(TAG, "onPause");
    progressDialog.dismiss();
    super.onPause();
  }

  /* (non-Javadoc)
   * @see android.app.Activity#onStart()
   */
  @Override
  protected void onStart()
  {
    MyLog.i(TAG, "onStart");
    pv.invalidate();
    super.onStart();
  }

  /* (non-Javadoc)
   * @see android.app.Activity#onRestart()
   */
  @Override
  protected void onRestart()
  {
    // TODO Auto-generated method stub
    MyLog.i(TAG, "onRestart");
    super.onRestart();
  }

  /* (non-Javadoc)
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume()
  {
    // TODO Auto-generated method stub
    MyLog.i(TAG, "onResume");
    super.onResume();
  }

  /* (non-Javadoc)
   * @see android.app.Activity#onStop()
   */
  @Override
  protected void onStop()
  {
    // TODO Auto-generated method stub
    MyLog.i(TAG, "onStop");
    super.onStop();
  }

  @Override
  protected void onDestroy()
  {
    MyLog.i(TAG, "onDestroy");
    progressDialog.dismiss();

    super.onDestroy();

    Bitmap bmp = null;
    int len = 0;

    len = filters.size();
    for(int i = 0; i < len; i++)
    {
      bmp = filters.get(i);
      if(bmp != null && !bmp.isRecycled())
      {
        bmp.recycle();
        bmp = null;
      }
    }
    filters.clear();

    len = frames.size();
    for(int i = 0; i < len; i++)
    {
      bmp = frames.get(i);
      if(bmp != null && !bmp.isRecycled())
      {
        bmp.recycle();
        bmp = null;
      }
    }
    frames.clear();

    len = level1Stickers.size();
    for(int i = 0; i < len; i++)
    {
      bmp = level1Stickers.get(i);
      if(bmp != null && !bmp.isRecycled())
      {
        bmp.recycle();
        bmp = null;
      }
    }
    level1Stickers.clear();

    pv.releasePhoto();

    System.gc();
  }

  private Bitmap getScaledBitmap(Resources mResources, int resId,
      BitmapFactory.Options opts)
  {
    BitmapFactory.decodeResource(mResources, resId, opts);
    float ratio = 1f * opts.outWidth / opts.outHeight;

    Bitmap tmp = BitmapFactory.decodeResource(mResources, resId);
    Bitmap bmp =
        Bitmap.createScaledBitmap(tmp, Math.round(TOOL_PIXEL * ratio),
            Math.round(TOOL_PIXEL * ratio), true);
    if(tmp != null && !tmp.isRecycled())
    {
      tmp.recycle();
      tmp = null;
    }
    System.gc();
    return bmp;
  }

  private Bitmap getScaledBitmap(AssetManager assetManager, String fileName,
      BitmapFactory.Options opts) throws IOException
  {
    InputStream is = assetManager.open(fileName);
    BitmapFactory.decodeStream(is, null, opts);
    float ratio = 1f * opts.outWidth / opts.outHeight;

    Bitmap tmp = BitmapFactory.decodeStream(is);
    Bitmap bmp =
        Bitmap.createScaledBitmap(tmp, Math.round(TOOL_PIXEL * ratio),
            Math.round(TOOL_PIXEL * ratio), true);
    if(tmp != null && !tmp.isRecycled())
    {
      tmp.recycle();
      tmp = null;
    }
    System.gc();
    return bmp;
  }

  /**
   * 加载按钮控件用到的所有图片，保证用户切换工具条时的顺畅度
   */
  private void loadingWidgetBitmap()
  {
    Bitmap bmp = null;
    Resources mResources = getApplicationContext().getResources();
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    AssetManager assetManager = getAssets();

    MyLog.i(TAG, "filter");
    String[] filterGroup = Go1978Constants.filters;
    int len = 0;
    try
    {
      len = filterGroup.length;
      for(int i = 0; i < len; i++)
      {
        bmp = getScaledBitmap(assetManager, filterGroup[i], options);
        filters.add(bmp);
      }
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }

    MyLog.i(TAG, "frame");
    String[] frameGroup = Go1978Constants.frames;
    // group = Go1978Constants.frames;
    try
    {
      len = frameGroup.length;
      for(int i = 0; i < len; i++)
      {
        bmp = getScaledBitmap(assetManager, frameGroup[i], options);
        frames.add(bmp);
      }
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }

    MyLog.i(TAG, "sticker");
    String[] stickerGroup = Go1978Constants.stickers;
    try
    {
      len = stickerGroup.length;
      for(int i = 0; i < len; i++)
      {
        bmp = getScaledBitmap(assetManager, stickerGroup[i], options);
        level1Stickers.add(bmp);
      }
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }

    // for(int i = 0; i < 36; i++)
    // {
    // ArrayList<Bitmap> level2SubStickers = new ArrayList<Bitmap>();
    // group = Go1978Constants.allStickers[i];
    // for(int j = 0; j < 9; j++)
    // {
    // bmp = getScaledBitmap(mResources, group[j], options);
    // level2SubStickers.add(bmp);
    // }
    // stickers.put(i + 1, level2SubStickers);
    // }
    MyLog.i(TAG, "finish");
  }

  /**
   * 设置ImageView
   * 
   * @param imageView
   * @param resId
   */
  private void setupImageView(ImageView imageView, int resId)
  {
    imageView = (ImageView) findViewById(resId);
    imageView.setOnClickListener(new MyOnClickListener());
  }

  /**
   * 实现OnClickListener接口
   * 
   * @author Wilson 20131228
   */
  private class MyOnClickListener implements OnClickListener
  {

    public void onClick(View v)
    {
      int len = level2Stickers.size();
      for(int i = 0; i < len; i++)
      {
        Bitmap bmp = level2Stickers.get(i);
        if(bmp != null && !bmp.isRecycled())
        {
          bmp.recycle();
          bmp = null;
        }
      }
      level2Stickers.clear();
      System.gc();

      switch(v.getId())
      {
        case R.id.image_view_filter: // 添加滤镜
          createImageGallery(Go1978Constants.LEVEL_1, Go1978Constants.COLORING,
              filters);
          break;
        case R.id.image_view_frame:
          createImageGallery(Go1978Constants.LEVEL_1, Go1978Constants.FRAME,
              frames);
          break;
        case R.id.image_view_sticker: // 添加sticker
          createImageGallery(Go1978Constants.LEVEL_1, Go1978Constants.STICKER,
              level1Stickers);
          break;
        case R.id.image_view_text: // 添加文本
          createTextGallery(Go1978Constants.LEVEL_1, Go1978Constants.TEXT,
              Go1978Constants.ttfs);
          break;
        case R.id.image_view_cancel: // 取消编辑的内容
          progressDialog.show();
          progressDialog.getWindow().setLayout(550, 150);
          new Thread(new Runnable()
          {

            public void run()
            {
              pv.cancel();
              Message msg = new Message();
              msg.what = HANDLER_INVALIDATE;
              handler.sendMessage(msg);
            }
          }).start();

          break;
        case R.id.image_view_ok: // 保存
          progressDialog.show();
          progressDialog.getWindow().setLayout(550, 150);
          new Thread(new Runnable()
          {

            public void run()
            {
              saveEditation();
              Message msg = new Message();
              msg.what = HANDLER_SAVE_SUCCESSFULLY;
              handler.sendMessage(msg);
            }
          }).start();
          break;

        default:
          break;
      }
    }

  }

  /**
   * 添加MyEditText到图片编辑器
   */
  // @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  // private void addEditText()
  // {
  // FrameLayout.LayoutParams fp =
  // new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
  // LayoutParams.WRAP_CONTENT);
  //
  // if(application.getDebugMode() ==
  // GoTravelApplication.PICTURE_EDITOR_DEBUG_MODE
  // || application.getDebugMode() == GoTravelApplication.ALL_DEBUG)
  // {
  // mEditText = new MyEditText(getApplicationContext(), true);
  // }
  // else
  // {
  // mEditText = new MyEditText(getApplicationContext());
  // }
  //
  // mEditText.setGravity(Gravity.CENTER);
  // mEditText.setTextBackground(R.drawable.text_bg);
  // fl.addView(mEditText, fp);
  // isAddingText = true;
  // // 更新Menu状态
  // PictureEditorActivity.this.getWindow().invalidatePanelMenu(
  // Window.FEATURE_OPTIONS_PANEL);
  // dtv = new DragTextView(this, photo.getScaling());
  // FrameLayout.LayoutParams fp =
  // new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
  // FrameLayout.LayoutParams.WRAP_CONTENT);
  // myFrameLayout.addView(dtv, fp);
  // }

  /**
   * 修改Logo的透明度，只有25，50，75，90四个值
   */
  // private void changeLogoTransparency()
  // {
  // switch(logoAlphaCount)
  // {
  // case 0:
  // logo.setAlpha(25);
  // break;
  // case 1:
  // logo.setAlpha(50);
  // break;
  // case 2:
  // logo.setAlpha(75);
  // break;
  // case 3:
  // logo.setAlpha(90);
  // break;
  // }
  // if(logoAlphaCount == 3)
  // {
  // logoAlphaCount = 0;
  // }
  // else
  // {
  // logoAlphaCount++;
  // }
  // // editor.invalidate();
  // }

  /**
   * 修改Logo的位置，只能在图片的四个角落
   */
  // private void changeLogoPosition()
  // {
  // switch(logoPosCount)
  // {
  // case 0:
  // logo.setPosition(0, 0);
  // break;
  // case 1:
  // // logo.setPosition(editor.getWidth() - logo.getWidth(), 0);
  // break;
  // case 2:
  // // logo.setPosition(0, .getHeight() - logo.getHeight());
  // break;
  // case 3:
  // // logo.setPosition(editor.getWidth() - logo.getWidth(),
  // // editor.getHeight() - logo.getHeight());
  // }
  // if(logoPosCount == 3)
  // {
  // logoPosCount = 0;
  // }
  // else
  // {
  // logoPosCount++;
  // }
  // // editor.invalidate();
  // }

  /**
   * 保存编辑的内容
   */
  private void saveEditation()
  {
    File saveDir = null;

    // 获取保存图片的目录
    if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
    {
      saveDir =
          new File(
              Environment
                  .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
              ImageUtils.ALBUM_NAME);

      if(null != saveDir)
      {
        if(!saveDir.mkdir())
        {
          if(!saveDir.exists())
          {
            MyLog.d(TAG, "failed to create directory");
            return;
          }
        }
      }
    }
    else
    {
      MyLog.v(TAG, "External storage is not mounted READ/WRITE.");
      return;
    }

    // 保存
    String result = null;
    result = pv.save(saveDir.getAbsolutePath().toString());

    // 把图片放到Gallery，其他应用也能看到此应用的图片
    if(result != null)
    {
      ImageUtils.galleryAddPic(getApplicationContext(), result);
      
      MyLog.i(TAG, "upload to QA");
      Intent intent = new Intent(PictureEditorActivity.this, WebActivity.class);
      intent.putExtra("file_path", result);
      setResult(RESULT_OK_QA, intent);
      this.finish();
    }

    Message msg = new Message();
    msg.what = HANDLER_SAVE_SUCCESSFULLY;
    handler.sendMessage(msg);

    
    // MyLog.i(TAG, "upload type=" + application.getUploadType());
    // if(application.getUploadType() == UPLOAD_TYPE.ALBUM)
    // {
    // MyLog.i(TAG, "upload to album");
    // // 检测当前网络类型
    // int connectedType = HttpUtils.getConnectedType(this);
    // if(connectedType != -1)
    // {
    // if(connectedType != ConnectivityManager.TYPE_WIFI)
    // {
    // new AlertDialog.Builder(PictureEditorActivity.this)
    // .setTitle("网络连接提醒").setMessage("您现在使用的是运营商网络，继续使用可能会被运营商收取流量费用")
    // .setPositiveButton("继续使用", new DialogInterface.OnClickListener()
    // {
    //
    // public void onClick(DialogInterface dialog, int which)
    // {
    // // 上传
    // MyLog.i(TAG, "result=" + result);
    // new UploadAsyncTask(null).execute(GoTravelApplication.POST,
    // result);
    // }
    // }).setNegativeButton("放弃", new DialogInterface.OnClickListener()
    // {
    //
    // public void onClick(DialogInterface dialog, int which)
    // {
    // return;
    // }
    // }).create().show();
    // }
    // else
    // {
    // // 上传
    // MyLog.i(TAG, "result=" + result);
    // new UploadAsyncTask(null).execute(GoTravelApplication.POST, result);
    // }
    // }
    // }
    // else if(application.getUploadType() == UPLOAD_TYPE.QA)
    // {
    // MyLog.i(TAG, "upload to QA");
    // Intent intent = new Intent(PictureEditorActivity.this,
    // WebActivity.class);
    // intent.putExtra("file_path", result);
    // setResult(RESULT_OK_QA, intent);
    // this.finish();
    // }
    // else if(application.getUploadType() == UPLOAD_TYPE.NONE)
    // {
    // Toast.makeText(getApplicationContext(), "Save successfully!",
    // Toast.LENGTH_LONG).show();
    // finish();
    // }

  }

  private class UploadAsyncTask extends AsyncTask<String, Void, String>
  {
    String json = null;

    private ProgressDialog dialog;
    private String contentType = null;

    public UploadAsyncTask(String json)
    {
      super();
      this.json = json;
    }

    @Override
    protected void onPreExecute()
    {
      // 显示一个ProgressDialog等待网络操作的结束
      dialog = new ProgressDialog(PictureEditorActivity.this);
      dialog.setMessage(application.getString(R.string.uploading));
      dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      dialog.setCanceledOnTouchOutside(false);
      dialog.show();
    }

    @Override
    protected String doInBackground(String... params)
    {
      if(params[0].equals(GoTravelApplication.POST))
      {
        // 确定http包头的Content-Type
        contentType = ImageUtils.getContentType(params[1]);

        // 上传到相册
        if(application.getUploadType() == UPLOAD_TYPE.ALBUM)
        {
          try
          {
            MyLog.i(TAG, "Upload to QA, contentType=" + contentType);
            int size = 0;
            size = ImageUtils.getImageSize(params[1]);
            application.getHttpUtils().createPostConnection(
                "http://192.168.3.65/test/upload.php", contentType, size);
            String result =
                application.getHttpUtils().sendFile(null, params[1]);
            application.getHttpUtils().releaseConnection();
            return result;
          }
          catch(Exception e)
          {
            e.printStackTrace();
          }
        }
        // 上传到QA
        else if(application.getUploadType() == UPLOAD_TYPE.QA)
        {

        }
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
      dialog.dismiss();
      Toast.makeText(getApplicationContext(), "Save and upload successfully!",
          Toast.LENGTH_LONG).show();
      finish();
    }

  }

  /**
   * 设置字体工具栏显示的字体样式
   * 
   * @param tv
   * @param id
   * @param tf
   * @param color
   * @param onClickListener
   */
  private void setTextStyle(TextView tv, int id, Typeface tf, int color,
      OnClickListener onClickListener)
  {
    tv.setId(id);
    tv.setTypeface(tf);
    tv.setTextColor(color);
    tv.setOnClickListener(onClickListener);
  }

  /**
   * 生成第字体选择工具栏
   * 
   * @param level 第几层工具栏
   * @param type 工具栏显示的类型
   * @param group 包含工具栏显示内容的数组
   */
  private <T> void createTextGallery(int level, int type, T[] group)
  {
    // 先清除之前的内容
    toolBar.removeAllViews();
    toolBarType = type;
    imageBack.setVisibility(View.GONE);

    // 遍历数组的内容，显示到工具栏上
    for(int i = 0; i < group.length; i++)
    {
      // 每个图标所占的位置大小
      LinearLayout layout = new LinearLayout(getApplicationContext());
      layout.setLayoutParams(new LayoutParams(TOOL_BAR_PIXEL, TOOL_BAR_PIXEL));
      layout.setGravity(Gravity.CENTER);

      // 用于显示文字的TextView
      TextView tv = new TextView(getApplicationContext());
      tv.setLayoutParams(new LayoutParams(TOOL_PIXEL, TOOL_PIXEL));
      tv.setGravity(Gravity.CENTER);
      tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 48);
      tv.setText("Abc");
      if(level == Go1978Constants.LEVEL_1 && type == Go1978Constants.TEXT)
      {
        setTextStyle(tv, Go1978Constants.TEXT_LEVEL_1_BASE_ID + i + 1,
            (Typeface) group[i], Color.BLACK, new OnTextLevel1ClickListener());
      }
      else if(level == Go1978Constants.LEVEL_2 && type == Go1978Constants.TEXT)
      {
        setTextStyle(tv, Go1978Constants.TEXT_LEVEL_2_BASE_ID + i + 1,
            Go1978Constants.ttfs[textType], ((Integer) group[i]).intValue(),
            new OnTextLevel2ClickListener());
      }

      layout.addView(tv);
      toolBar.addView(layout);
    }
  }

  // private void createLevel1TextGallery(int level, int type, String[] group)
  // {
  // gallery.removeAllViews();
  // galleryType = type;
  // back.setVisibility(View.GONE);
  //
  // for(int i = 0; i < group.length; i++)
  // {
  // LinearLayout layout = new LinearLayout(getApplicationContext());
  // layout.setLayoutParams(new LayoutParams(TOOL_BAR_PIXEL, TOOL_BAR_PIXEL));
  // layout.setGravity(Gravity.CENTER);
  //
  // TextView tv = new TextView(getApplicationContext());
  // tv.setLayoutParams(new LayoutParams(TOOL_PIXEL, TOOL_PIXEL));
  // tv.setGravity(Gravity.CENTER);
  // tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 48);
  // tv.setText("Abc");
  // Typeface tf = Typeface.createFromAsset(getAssets(), group[i]);
  // tv.setId(Go1978Constants.TEXT_LEVEL_1_BASE_ID + i + 1);
  // tv.setTypeface(tf);
  // tv.setOnClickListener(new OnTextLevel1ClickListener());
  // layout.addView(tv);
  // gallery.addView(layout);
  // }
  // }

  /**
   * 第一层字体工具栏的点击事件监听器
   * 
   * @author Wilson 20140224
   */
  private class OnTextLevel1ClickListener implements OnClickListener
  {

    public void onClick(View v)
    {
      // 根据View ID确定选中的字体类型
      switch(v.getId())
      {
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 1:
          textType = 0;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 2:
          textType = 1;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 3:
          textType = 2;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 4:
          textType = 3;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 5:
          textType = 4;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 6:
          textType = 5;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 7:
          textType = 6;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 8:
          textType = 7;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 9:
          textType = 8;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 10:
          textType = 9;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 11:
          textType = 10;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 12:
          textType = 11;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 13:
          textType = 12;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 14:
          textType = 13;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 15:
          textType = 14;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 16:
          textType = 15;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 17:
          textType = 16;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 18:
          textType = 17;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 19:
          textType = 18;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 20:
          textType = 19;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 21:
          textType = 20;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 22:
          textType = 21;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 23:
          textType = 22;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 24:
          textType = 23;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 25:
          textType = 24;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 26:
          textType = 25;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 27:
          textType = 26;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 28:
          textType = 27;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 29:
          textType = 28;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 30:
          textType = 29;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 31:
          textType = 30;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 32:
          textType = 31;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 33:
          textType = 32;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 34:
          textType = 33;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 35:
          textType = 34;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 36:
          textType = 35;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 37:
          textType = 36;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 38:
          textType = 37;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 39:
          textType = 38;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 40:
          textType = 39;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 41:
          textType = 40;
          break;
        case Go1978Constants.TEXT_LEVEL_1_BASE_ID + 42:
          textType = 41;
          break;

        default:
          break;
      }

      // 根据所选字体，生成第二层字体颜色选择工具栏
      createTextGallery(Go1978Constants.LEVEL_2, Go1978Constants.TEXT,
          Go1978Constants.text_colors);

      // 第二层工具栏需要显示回退按钮
      imageBack.setVisibility(View.VISIBLE);
    }

  }

  // private void createLevel2TextGallery(int level, int type, String textType,
  // int[] group)
  // {
  // gallery.removeAllViews();
  // galleryType = type;
  //
  // for(int i = 0; i < group.length; i++)
  // {
  // LinearLayout layout = new LinearLayout(getApplicationContext());
  // layout.setLayoutParams(new LayoutParams(TOOL_BAR_PIXEL, TOOL_BAR_PIXEL));
  // layout.setGravity(Gravity.CENTER);
  //
  // TextView tv = new TextView(getApplicationContext());
  // tv.setLayoutParams(new LayoutParams(TOOL_PIXEL, TOOL_PIXEL));
  // tv.setGravity(Gravity.CENTER);
  // tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 48);
  // tv.setText("Abc");
  // Typeface tf = Typeface.createFromAsset(getAssets(), textType);
  // tv.setId(Go1978Constants.TEXT_LEVEL_2_BASE_ID + i + 1);
  // tv.setTypeface(tf);
  // tv.setTextColor(group[i]);
  // tv.setOnClickListener(new OnTextLevel2ClickListener());
  // layout.addView(tv);
  // gallery.addView(layout);
  // }
  // }

  /**
   * 第一层字体工具栏的点击事件监听器
   * 
   * @author Wilson 20140224
   */
  private class OnTextLevel2ClickListener implements OnClickListener
  {

    public void onClick(View v)
    {

      switch(v.getId())
      {
        case Go1978Constants.TEXT_LEVEL_2_BASE_ID + 1:
          color = 0;
          break;
        case Go1978Constants.TEXT_LEVEL_2_BASE_ID + 2:
          color = 1;
          break;
        case Go1978Constants.TEXT_LEVEL_2_BASE_ID + 3:
          color = 2;
          break;
        case Go1978Constants.TEXT_LEVEL_2_BASE_ID + 4:
          color = 3;
          break;
        case Go1978Constants.TEXT_LEVEL_2_BASE_ID + 5:
          color = 4;
          break;
        case Go1978Constants.TEXT_LEVEL_2_BASE_ID + 6:
          color = 5;
          break;
        case Go1978Constants.TEXT_LEVEL_2_BASE_ID + 7:
          color = 6;
          break;
        case Go1978Constants.TEXT_LEVEL_2_BASE_ID + 8:
          color = 7;
          break;
        case Go1978Constants.TEXT_LEVEL_2_BASE_ID + 9:
          color = 8;
          break;
        case Go1978Constants.TEXT_LEVEL_2_BASE_ID + 10:
          color = 9;
          break;
        case Go1978Constants.TEXT_LEVEL_2_BASE_ID + 11:
          color = 10;
          break;
      }

      progressDialog.show();
      progressDialog.getWindow().setLayout(550, 150);
      new Thread(new Runnable()
      {

        public void run()
        {
          // 往PictureView添加文字
          pv.addText(Go1978Constants.ttfs[textType],
              Go1978Constants.text_colors[color].intValue());
          Message msg = new Message();
          msg.what = HANDLER_INVALIDATE;
          handler.sendMessage(msg);
        }
      }).start();

    }

  }

  /**
   * 生成第带图片的选择工具栏
   * 
   * @param level 第几层
   * @param type 工具栏类型
   * @param group 包含显示内容的数组
   */
  private void createImageGallery(int level, int type, ArrayList<Bitmap> group)
  {
    // 先清除之前的内容
    toolBar.removeAllViews();
    toolBarType = type;

    // 遍历数组的内容，显示到工具栏上
    int len = group.size();
    for(int i = 0; i < len; i++)
    {
      // 每个图标所占的空间
      LinearLayout layout = new LinearLayout(getApplicationContext());
      layout.setLayoutParams(new LayoutParams(TOOL_BAR_PIXEL, TOOL_BAR_PIXEL));
      layout.setGravity(Gravity.CENTER);

      ImageView imageView = new ImageView(getApplicationContext());
      imageView.setLayoutParams(new LayoutParams(TOOL_PIXEL, TOOL_PIXEL));
      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
      imageView.setScaleType(ScaleType.FIT_CENTER);
      imageView.setImageBitmap(group.get(i));

      // 根据层级，类型，确定View ID，点击监听器，和回退按钮的显示和隐藏
      if(level == Go1978Constants.LEVEL_1 && type == Go1978Constants.STICKER)
      {
        imageView.setId(Go1978Constants.STICKER_LEVEL_1_BASE_ID + i + 1);
        imageView.setOnClickListener(new OnLevel1StickerClickListener());
        imageBack.setVisibility(View.GONE);
      }
      else if(level == Go1978Constants.LEVEL_2
          && type == Go1978Constants.STICKER)
      {
        imageView.setId(Go1978Constants.STICKER_LEVEL_2_BASE_ID + i + 1);
        imageView.setOnClickListener(new OnLevel2StickerClickListener());
        imageBack.setVisibility(View.VISIBLE);
      }
      else if(type == Go1978Constants.FRAME)
      {
        imageView.setId(Go1978Constants.FRAME_BASE_ID + i + 1);
        imageView.setOnClickListener(new OnFrameClickListener());
        imageBack.setVisibility(View.GONE);
      }
      else if(type == Go1978Constants.COLORING)
      {
        imageView.setId(Go1978Constants.FILTER_BASE_ID + i + 1);
        imageView.setOnClickListener(new OnFilterClickListener());
        imageBack.setVisibility(View.GONE);
      }
      else
      {
        imageBack.setVisibility(View.GONE);
      }

      layout.addView(imageView);
      toolBar.addView(layout);
    }
  }

  /**
   * 生成第带图片的选择工具栏
   * 
   * @param level 第几层
   * @param type 工具栏类型
   * @param group 包含显示内容的数组
   */
  private void createImageGallery(int level, int type, int[] group)
  {
    // 先清除之前的内容
    toolBar.removeAllViews();
    toolBarType = type;

    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;

    // 遍历数组的内容，显示到工具栏上
    for(int i = 0; i < group.length; i++)
    {
      // 生成工具栏显示的图片
      Bitmap bmp = getScaledBitmap(getResources(), group[i], options);
      level2Stickers.add(bmp);

      // 每个图标所占的空间
      LinearLayout layout = new LinearLayout(getApplicationContext());
      layout.setLayoutParams(new LayoutParams(TOOL_BAR_PIXEL, TOOL_BAR_PIXEL));
      layout.setGravity(Gravity.CENTER);

      ImageView imageView = new ImageView(getApplicationContext());
      imageView.setLayoutParams(new LayoutParams(TOOL_PIXEL, TOOL_PIXEL));
      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
      imageView.setImageBitmap(bmp);

      // 根据层级，类型，确定View ID，点击监听器，和回退按钮的显示和隐藏
      if(level == Go1978Constants.LEVEL_1 && type == Go1978Constants.STICKER)
      {
        imageView.setId(Go1978Constants.STICKER_LEVEL_1_BASE_ID + i + 1);
        imageView.setOnClickListener(new OnLevel1StickerClickListener());
        imageBack.setVisibility(View.GONE);
      }
      else if(level == Go1978Constants.LEVEL_2
          && type == Go1978Constants.STICKER)
      {
        imageView.setId(Go1978Constants.STICKER_LEVEL_2_BASE_ID + i + 1);
        imageView.setOnClickListener(new OnLevel2StickerClickListener());
        imageBack.setVisibility(View.VISIBLE);
      }
      else if(type == Go1978Constants.FRAME)
      {
        imageView.setId(Go1978Constants.FRAME_BASE_ID + i + 1);
        imageView.setOnClickListener(new OnFrameClickListener());
        imageBack.setVisibility(View.GONE);
      }
      else if(type == Go1978Constants.COLORING)
      {
        imageView.setId(Go1978Constants.FILTER_BASE_ID + i + 1);
        imageView.setOnClickListener(new OnFilterClickListener());
        imageBack.setVisibility(View.GONE);
      }
      else
      {
        imageBack.setVisibility(View.GONE);
      }

      layout.addView(imageView);
      toolBar.addView(layout);
    }
  }
  private void createImageGallery(int level, int type, String[] group)
  {
    // 先清除之前的内容
    toolBar.removeAllViews();
    toolBarType = type;
    
    int len = 0;
    Bitmap bmp = null;
    AssetManager assetManager = getAssets();
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    
    try
    {
      len = group.length;
      for(int i = 0; i < len; i++)
      {
        bmp = getScaledBitmap(assetManager, group[i], options);
        level2Stickers.add(bmp);
        
      }
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    
    len = level2Stickers.size();
    // 遍历数组的内容，显示到工具栏上
    for(int i = 0; i < len; i++)
    {
      // 每个图标所占的空间
      LinearLayout layout = new LinearLayout(getApplicationContext());
      layout.setLayoutParams(new LayoutParams(TOOL_BAR_PIXEL, TOOL_BAR_PIXEL));
      layout.setGravity(Gravity.CENTER);
      
      ImageView imageView = new ImageView(getApplicationContext());
      imageView.setLayoutParams(new LayoutParams(TOOL_PIXEL, TOOL_PIXEL));
      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
      imageView.setImageBitmap(level2Stickers.get(i));
      
      // 根据层级，类型，确定View ID，点击监听器，和回退按钮的显示和隐藏
      if(level == Go1978Constants.LEVEL_1 && type == Go1978Constants.STICKER)
      {
        imageView.setId(Go1978Constants.STICKER_LEVEL_1_BASE_ID + i + 1);
        imageView.setOnClickListener(new OnLevel1StickerClickListener());
        imageBack.setVisibility(View.GONE);
      }
      else if(level == Go1978Constants.LEVEL_2
          && type == Go1978Constants.STICKER)
      {
        imageView.setId(Go1978Constants.STICKER_LEVEL_2_BASE_ID + i + 1);
        imageView.setOnClickListener(new OnLevel2StickerClickListener());
        imageBack.setVisibility(View.VISIBLE);
      }
      else if(type == Go1978Constants.FRAME)
      {
        imageView.setId(Go1978Constants.FRAME_BASE_ID + i + 1);
        imageView.setOnClickListener(new OnFrameClickListener());
        imageBack.setVisibility(View.GONE);
      }
      else if(type == Go1978Constants.COLORING)
      {
        imageView.setId(Go1978Constants.FILTER_BASE_ID + i + 1);
        imageView.setOnClickListener(new OnFilterClickListener());
        imageBack.setVisibility(View.GONE);
      }
      else
      {
        imageBack.setVisibility(View.GONE);
      }
      
      layout.addView(imageView);
      toolBar.addView(layout);
    }
  }

  /**
   * 滤镜工具栏点击监听器
   * 
   * @author Wilson 20140224
   */
  public class OnFilterClickListener implements OnClickListener
  {

    public void onClick(View v)
    {
      // 确定滤镜的类型
      switch(v.getId())
      {
        case Go1978Constants.FILTER_BASE_ID + 1:
          filter = Filter.COLOR_1;
          break;
        case Go1978Constants.FILTER_BASE_ID + 2:
          filter = Filter.COLOR_2;
          break;
        case Go1978Constants.FILTER_BASE_ID + 3:
          filter = Filter.COLOR_3;
          break;
      }

      progressDialog.show();
      progressDialog.getWindow().setLayout(550, 150);
      new Thread(new Runnable()
      {

        public void run()
        {
          pv.setFilter(filter);
          Message msg = new Message();
          msg.what = HANDLER_INVALIDATE;
          handler.sendMessage(msg);
        }
      }).start();
    }

  }

  /**
   * 相框工具栏点击监听器
   * 
   * @author Wilson 20140224
   */
  public class OnFrameClickListener implements OnClickListener
  {

    public void onClick(View v)
    {

      switch(v.getId())
      {
        case Go1978Constants.FRAME_BASE_ID + 1:
          type = Frame.FRAME_1;
          break;
        case Go1978Constants.FRAME_BASE_ID + 2:
          type = Frame.FRAME_2;
          break;
        case Go1978Constants.FRAME_BASE_ID + 3:
          type = Frame.FRAME_3;
          break;
        case Go1978Constants.FRAME_BASE_ID + 4:
          type = Frame.FRAME_4;
          break;
        case Go1978Constants.FRAME_BASE_ID + 5:
          type = Frame.FRAME_5;
          break;
        case Go1978Constants.FRAME_BASE_ID + 6:
          type = Frame.FRAME_6;
          break;
        case Go1978Constants.FRAME_BASE_ID + 7:
          type = Frame.FRAME_7;
          break;
        case Go1978Constants.FRAME_BASE_ID + 8:
          type = Frame.FRAME_8;
          break;
        case Go1978Constants.FRAME_BASE_ID + 9:
          type = Frame.FRAME_9;
          break;

        default:
          break;
      }

      progressDialog.show();
      progressDialog.getWindow().setLayout(550, 150);
      new Thread(new Runnable()
      {

        public void run()
        {
          pv.setFrame(type);
          Message msg = new Message();
          msg.what = HANDLER_INVALIDATE;
          handler.sendMessage(msg);
        }
      }).start();

    }

  }

  /**
   * 第一层Sticker工具栏点击监听器
   * 
   * @author Wilson 20140224
   */
  public class OnLevel1StickerClickListener implements OnClickListener
  {

    public void onClick(View v)
    { 
      switch(v.getId())
      {
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 1:
          createImageGallery(Go1978Constants.LEVEL_2,
              Go1978Constants.STICKER, Go1978Constants.sticker_1s);
          stickerType = Sticker.STICKER_1;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 2:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_2s);
          stickerType = Sticker.STICKER_2;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 3:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_3s);
          stickerType = Sticker.STICKER_3;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 4:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_4s);
          stickerType = Sticker.STICKER_4;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 5:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_5s);
          stickerType = Sticker.STICKER_5;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 6:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_6s);
          stickerType = Sticker.STICKER_6;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 7:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_7s);
          stickerType = Sticker.STICKER_7;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 8:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_8s);
          stickerType = Sticker.STICKER_8;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 9:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_9s);
          stickerType = Sticker.STICKER_9;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 10:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_10s);
          stickerType = Sticker.STICKER_10;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 11:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_11s);
          stickerType = Sticker.STICKER_11;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 12:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_12s);
          stickerType = Sticker.STICKER_12;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 13:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_13s);
          stickerType = Sticker.STICKER_13;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 14:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_14s);
          stickerType = Sticker.STICKER_14;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 15:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_15s);
          stickerType = Sticker.STICKER_15;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 16:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_16s);
          stickerType = Sticker.STICKER_16;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 17:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_17s);
          stickerType = Sticker.STICKER_17;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 18:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_18s);
          stickerType = Sticker.STICKER_18;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 19:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_19s);
          stickerType = Sticker.STICKER_19;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 20:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_20s);
          stickerType = Sticker.STICKER_20;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 21:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_21s);
          stickerType = Sticker.STICKER_21;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 22:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_22s);
          stickerType = Sticker.STICKER_22;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 23:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_23s);
          stickerType = Sticker.STICKER_23;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 24:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_24s);
          stickerType = Sticker.STICKER_24;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 25:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_25s);
          stickerType = Sticker.STICKER_25;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 26:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_26s);
          stickerType = Sticker.STICKER_26;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 27:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_27s);
          stickerType = Sticker.STICKER_27;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 28:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_28s);
          stickerType = Sticker.STICKER_28;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 29:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_29s);
          stickerType = Sticker.STICKER_29;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 30:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_30s);
          stickerType = Sticker.STICKER_30;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 31:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_31s);
          stickerType = Sticker.STICKER_31;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 32:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_32s);
          stickerType = Sticker.STICKER_32;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 33:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_33s);
          stickerType = Sticker.STICKER_33;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 34:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_34s);
          stickerType = Sticker.STICKER_34;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 35:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_35s);
          stickerType = Sticker.STICKER_35;
          break;
        case Go1978Constants.STICKER_LEVEL_1_BASE_ID + 36:
          createImageGallery(Go1978Constants.LEVEL_2, Go1978Constants.STICKER,
              Go1978Constants.sticker_36s);
          stickerType = Sticker.STICKER_36;
          break;

        default:
          break;
      }
    }

  }

  /**
   * 第二层Sticker工具栏点击监听器
   * 
   * @author Wilson 20140224
   */
  private class OnLevel2StickerClickListener implements OnClickListener
  {

    public void onClick(View v)
    {

      switch(v.getId())
      {
        case Go1978Constants.STICKER_LEVEL_2_BASE_ID + 1:
          stickerColor = stickerType + 1;
          break;
        case Go1978Constants.STICKER_LEVEL_2_BASE_ID + 2:
          stickerColor = stickerType + 2;
          break;
        case Go1978Constants.STICKER_LEVEL_2_BASE_ID + 3:
          stickerColor = stickerType + 3;
          break;
        case Go1978Constants.STICKER_LEVEL_2_BASE_ID + 4:
          stickerColor = stickerType + 4;
          break;
        case Go1978Constants.STICKER_LEVEL_2_BASE_ID + 5:
          stickerColor = stickerType + 5;
          break;
        case Go1978Constants.STICKER_LEVEL_2_BASE_ID + 6:
          stickerColor = stickerType + 6;
          break;
        case Go1978Constants.STICKER_LEVEL_2_BASE_ID + 7:
          stickerColor = stickerType + 7;
          break;
        case Go1978Constants.STICKER_LEVEL_2_BASE_ID + 8:
          stickerColor = stickerType + 8;
          break;
        case Go1978Constants.STICKER_LEVEL_2_BASE_ID + 9:
          stickerColor = stickerType + 9;
          break;
      }

      progressDialog.show();
      progressDialog.getWindow().setLayout(550, 150);
      new Thread(new Runnable()
      {

        public void run()
        {
          try
          {
            pv.addSticker(stickerColor);
            Message msg = new Message();
            msg.what = HANDLER_INVALIDATE;
            handler.sendMessage(msg);
          }
          catch(IOException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }).start();

    }

  }
}
