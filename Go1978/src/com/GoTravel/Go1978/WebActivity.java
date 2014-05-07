package com.GoTravel.Go1978;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.GoTravel.Go1978.GoTravelApplication.UPLOAD_TYPE;
import com.GoTravel.Go1978.authorization.BaseCompleteListener;
import com.GoTravel.Go1978.authorization.MyAuthorization;
import com.GoTravel.Go1978.constants.SinaConstants;
import com.GoTravel.Go1978.constants.TencentConstants;
import com.GoTravel.Go1978.extracomponent.WebLinearLayout;
import com.GoTravel.Go1978.extracomponent.WebLinearLayout.OnKbdStateChangedListener;
import com.GoTravel.Go1978.log.MyLog;
import com.GoTravel.Go1978.utils.HttpUtils;
import com.GoTravel.Go1978.utils.ImageUtils;
import com.GoTravel.Go1978.R;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;


/**
 * WebActivity用于显示网页内容，以及实现一些第三方登录需要的回调函数
 * 
 * @author Wilson 20131228
 */
public class WebActivity extends BaseActivity
{
  private static final String TAG = "WebActivity";

  private static final String QA_UPLOAD_URL =
      "http://www.go1978.com/plugin.php?id=answer:phone&act=input";
  private static final String DEBUG_QA_UPLOAD_URL =
      "http://192.168.3.65/ecmalltwo/src/plugin.php?id=answer:phone&act=input";
  // private static final String ALBUM_UPLOAD_URL_STRING =
  private static final String DEBUG_ALBUM_UPLOAD_URL =
      "http://192.168.3.65/ecmalltwo/src/plugin.php?id=javasyn&mod=upload_album_img";

  // Handler信息 - 腾讯第三方登录
  private static final int HANDLER_TENCENT_WEB_LOGIN = 0x10;
  // Handler信息 - 腾讯第三方登出
  private static final int HANDLER_TENCENT_WEB_LOGOUT = 0x11;
  // Handler信息 - WebView显示ProgressBar
  private static final int HANDLER_WEB_VIEW_SHOW_PROGRESS_BAR = 0X12;
  // Handler信息 - WebView隐藏ProgressBar
  private static final int HANDLER_WEB_VIEW_HIDE_PROGRESS_BAR = 0x13;

  // 启动Camera拍照的标识
  static final int REQUEST_CAMERA_CODE = 0x01;
  // 启动PictureEditorActivity的标识
  static final int REQUEST_PICTURE_EDITOR_CODE = 0x02;
  // 启动Gallery的标识
  static final int REQUEST_IMAGE_GALLERY_CODE = 0x03;

  // 从PictureEditorActivity到QA的标识
  boolean isFromEditorTOQA = false;

  // 全部上传完毕
  boolean isAllUploadSuccessfully = false;

  // 存放当前获取到的图片的路径
  String currentPhotoPath;
  // 从PictureEditorActivity返回的图片路径
  String resultPath;

  // 离开WebActivity之前保存当前的url
  String storedUrl = null;
  // 在QA的编辑界面下，离开WebActivity之前保存json
  String storedJson = null;

  int albumId = 0;

  private GoTravelApplication application;
  private WebLinearLayout webLinearLayout;
  private WebView myWebView;
  private MyAuthorization authorization;
  private ProgressBar progressBar;

  Handler mainHandler = new Handler()
  {

    @Override
    public void handleMessage(Message msg)
    {
      switch(msg.what)
      {
        case HANDLER_TENCENT_WEB_LOGIN: // 通过腾讯接口登录需要处理的UI事件
          MyLog.i(TAG, "mainHandler HANDLER_TENCENT_WEB_LOGIN");
          loadPluginUrl(TencentConstants.QQ_LOGIN_URL);
          break;

        case HANDLER_TENCENT_WEB_LOGOUT: // 通过腾讯接口退出需要处理的UI事件
          break;

        case HANDLER_WEB_VIEW_SHOW_PROGRESS_BAR: // Web调用ProgressBar
          if(progressBar.getVisibility() == View.INVISIBLE
              || progressBar.getVisibility() == View.GONE)
          {
            progressBar.setVisibility(View.VISIBLE);
          }
          break;

        case HANDLER_WEB_VIEW_HIDE_PROGRESS_BAR: // Web隐藏ProgressBar
          if(progressBar.getVisibility() == View.VISIBLE)
          {
            progressBar.setVisibility(View.GONE);
          }
          break;

        default:
          break;
      }
    }

  };

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    // requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_web);
    MyLog.i(TAG, "WebActivity onCreate");

    application = (GoTravelApplication) getApplication();

    authorization = application.getMyAuthorization();
    // 设置腾讯第三方登录需要用到的回调函数
    authorization.setupBaseCompleteListener(new MyBaseCompleteListener());

    if(savedInstanceState != null)
    {
      storedUrl =
          savedInstanceState.getString(GoTravelApplication.BUNDLE_KEY_URL);
      storedJson =
          savedInstanceState.getString(GoTravelApplication.BUNDLE_KEY_QA_JSON);
    }

    webLinearLayout = (WebLinearLayout) findViewById(R.id.layout_web_root);
    webLinearLayout
        .setOnKbdStateChangedListener(new OnKbdStateChangedListener()
        {

          public void onKbdStateChanged(int state)
          {
            switch(state)
            {
              case WebLinearLayout.KEYBOARD_STATE_SHOW:

                break;

              case WebLinearLayout.KEYBOARD_STATE_HIDE:
                MyLog.i(TAG, "hide keyboard");
                String javascript =
                    "javascript:if($(':focus')) $(':focus').blur()";
                myWebView.loadUrl(javascript);
                break;

              default:
                break;
            }
          }
        });
    // 初始化WebView
    initWebView();

    progressBar = (ProgressBar) findViewById(R.id.loading_bar);

  }

  @Override
  protected void onStart()
  {
    MyLog.i(TAG, "WebActivity onStart");
    super.onStart();
  }

  @Override
  protected void onResume()
  {

    super.onResume();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    MyLog.i(TAG, "WebActivity onActivityResult");
    MyLog.i(TAG, "requestCode=" + requestCode);
    MyLog.i(TAG, "resultCode=" + resultCode);
    if(requestCode == REQUEST_CAMERA_CODE) // 从Camera返回
    {
      if(resultCode == RESULT_OK)
      {
        ImageUtils.galleryAddPic(getApplicationContext(), currentPhotoPath);
        // 启动PictureEditorActivity
        callPictureEditorActivity(currentPhotoPath);
      }
      else
      {
        File file = new File(currentPhotoPath);
        if(file.exists())
        {
          if(file.isFile())
          {
            file.delete();
          }
        }
      }
    }
    else if(requestCode == REQUEST_PICTURE_EDITOR_CODE)
    {
      if(resultCode == RESULT_OK_QA)
      {
        MyLog.i(TAG, "uploadType=" + application.getUploadType());
        if(application.getUploadType() == UPLOAD_TYPE.ALBUM)
        {
          isFromEditorTOQA = false;
          Bundle bundle = data.getExtras();
          resultPath = bundle.getString("file_path");
          new AlertDialog.Builder(this)
              .setMessage(R.string.upload_to_album)
              .setPositiveButton(R.string.str_btn_yes,
                  new DialogInterface.OnClickListener()
                  {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                      // 检测当前网络类型
                      int connectedType =
                          HttpUtils.getConnectedType(WebActivity.this);
                      if(connectedType != -1)
                      {
                        // 如果不是使用wifi，弹出对话框警告，让用户选择是否继续上传
                        if(connectedType != ConnectivityManager.TYPE_WIFI)
                        {
                          new AlertDialog.Builder(WebActivity.this)
                              .setTitle("网络连接提醒")
                              .setMessage("您现在使用的是运营商网络，继续使用可能会被运营商收取流量费用")
                              .setPositiveButton("继续使用",
                                  new DialogInterface.OnClickListener()
                                  {

                                    public void onClick(DialogInterface dialog,
                                        int which)
                                    {
                                      if(!(application.getDebugMode() == GoTravelApplication.WEB_DEBUG_MODE || application
                                          .getDebugMode() == GoTravelApplication.ALL_DEBUG))
                                      {
                                        new UploadAsyncTask(resultPath)
                                            .execute(GoTravelApplication.POST,
                                                QA_UPLOAD_URL,
                                                GoTravelApplication.UPLOAD);
                                      }
                                      else
                                      {
                                        new UploadAsyncTask(resultPath)
                                            .execute(GoTravelApplication.POST,
                                                DEBUG_QA_UPLOAD_URL,
                                                GoTravelApplication.UPLOAD);
                                      }
                                    }
                                  })
                              .setNegativeButton("放弃",
                                  new DialogInterface.OnClickListener()
                                  {

                                    public void onClick(DialogInterface dialog,
                                        int which)
                                    {
                                      return;
                                    }
                                  }).create().show();
                        }
                        // 使用wifi，直接上传
                        else
                        {
                          if(!(application.getDebugMode() == GoTravelApplication.WEB_DEBUG_MODE || application
                              .getDebugMode() == GoTravelApplication.ALL_DEBUG))
                          {
                            new UploadAsyncTask(resultPath).execute(
                                GoTravelApplication.POST, QA_UPLOAD_URL,
                                GoTravelApplication.UPLOAD);
                          }
                          else
                          {
                            MyLog.i(TAG, DEBUG_QA_UPLOAD_URL);
                            new UploadAsyncTask(resultPath).execute(
                                GoTravelApplication.POST,
                                DEBUG_ALBUM_UPLOAD_URL,
                                GoTravelApplication.UPLOAD);
                          }
                        }
                      }

                    }
                  }).setNegativeButton(R.string.str_btn_no, null).show();
        }
        else
        {
          isFromEditorTOQA = true;
          Bundle bundle = data.getExtras();
          resultPath = bundle.getString("file_path");
          myWebView.reload();
          // String script =
          // "javascript:picName('" + resultPath + "', '" + storedJson + "')";
          // MyLog.i(TAG, script);
          // myWebView.loadUrl(script);
        }
      }
    }
    else if(requestCode == REQUEST_IMAGE_GALLERY_CODE) // 从Gallery返回
    {
      if(resultCode == RESULT_OK)
      {
        // 获取从Gallery返回的所选图片的文件路径
        Uri selectedImageUri = data.getData();
        MyLog.i(TAG, "image path is: " + selectedImageUri.toString());
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor =
            getContentResolver().query(selectedImageUri, filePathColumn, null,
                null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        // 启动PictureEditorActivity
        callPictureEditorActivity(filePath);
      }
    }
    else
    {
      // 第三方登录需要在onActivityResult实现的方法集合
      authorization.initActivityResultCallback(requestCode, resultCode, data);
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState)
  {
    // 从bundle中恢复图片的路径
    currentPhotoPath =
        savedInstanceState.getString(GoTravelApplication.BUNDLE_KEY_PHOTO_PATH);
    storedUrl =
        savedInstanceState.getString(GoTravelApplication.BUNDLE_KEY_URL);
    storedJson =
        savedInstanceState.getString(GoTravelApplication.BUNDLE_KEY_QA_JSON);
    albumId =
        savedInstanceState.getInt(GoTravelApplication.BUNDLE_KEY_ALBUM_ID);
    super.onRestoreInstanceState(savedInstanceState);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
    // 保存图片的路径到bundle仲
    outState.putString(GoTravelApplication.BUNDLE_KEY_PHOTO_PATH,
        currentPhotoPath);
    // 保存当前WebView的url
    outState.putString(GoTravelApplication.BUNDLE_KEY_URL, storedUrl);
    outState.putString(GoTravelApplication.BUNDLE_KEY_QA_JSON, storedJson);
    outState.putInt(GoTravelApplication.BUNDLE_KEY_ALBUM_ID, albumId);
    super.onSaveInstanceState(outState);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case R.id.menu_clear_cache: // 清缓存
        myWebView.clearCache(true);
        CookieManager.getInstance().removeAllCookie();
        break;
      case R.id.action_tencent_login: // 腾讯第三方登录
        MyLog.i(TAG, "Click Tencent Login Item");
        authorization.login(TencentConstants.THIRD_PARTY_NAME_TENCENT,
            WebActivity.this);
        break;
      case R.id.action_sina_login: // 新浪第三方登录
        MyLog.i(TAG, "Click Sina Login Item");
        authorization.login(SinaConstants.THIRD_PARTY_NAME_SINA,
            WebActivity.this);
        break;
      case R.id.action_share: // 分享
        MyLog.i(TAG, "Click Share Item");
        Intent intent = new Intent(WebActivity.this, UploadPicActivity.class);
        startActivity(intent);
        break;
      case R.id.action_camera: // 启动Camera
        MyLog.i(TAG, "Click Camera Item");
        openCamera();
        break;
      case R.id.action_editor: // 启动PictureEditorActivity
        openGallery();
        break;
      case R.id.action_settings:
        try
        {
          MyLog.i(
              TAG,
              "host="
                  + new URL(
                      "http://192.168.3.109/plugin.php?id=javasyn&mod=cookies")
                      .getHost());
        }
        catch(MalformedURLException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        break;

    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see
   * android.support.v7.app.ActionBarActivity#onConfigurationChanged(android
   * .content.res.Configuration)
   */
  @Override
  public void onConfigurationChanged(Configuration newConfig)
  {
    MyLog.i(TAG, "onConfigurationChanged");
    super.onConfigurationChanged(newConfig);
  }

  /**
   * 启动Camera
   */
  private void openCamera()
  {
    if(isIntentAvailable(MediaStore.ACTION_IMAGE_CAPTURE))
    {
      dispatchCameraIntent(REQUEST_CAMERA_CODE);
    }
  }

  /**
   * 启动Camera
   * 
   * @param type 启动Camera的作用，为QA或者Album拍摄
   */
  private void openCamera(UPLOAD_TYPE type)
  {
    application.setUploadType(type);
    openCamera();
  }

  /**
   * 打开Gallery
   */
  private void openGallery()
  {
    Intent selectedIntent = new Intent();
    selectedIntent.setType("image/*");
    selectedIntent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(selectedIntent, REQUEST_IMAGE_GALLERY_CODE);
  }

  /**
   * 启动PictureEditorActivity
   * 
   * @param filePath 图片路径
   */
  private void callPictureEditorActivity(String filePath)
  {
    Intent intent = new Intent(WebActivity.this, PictureEditorActivity.class);
    intent.putExtra(GoTravelApplication.BUNDLE_KEY_PHOTO_PATH, filePath);
    startActivityForResult(intent, REQUEST_PICTURE_EDITOR_CODE);
  }

  /**
   * 设置Intent，启动Camera
   * 
   * @param reqCode
   */
  private void dispatchCameraIntent(int reqCode)
  {
    MyLog.i(TAG, "reqCode=" + reqCode);
    File f = null;
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    try
    {
      f = ImageUtils.createPhotoFile();
      currentPhotoPath = f.getAbsolutePath();
      MyLog.i(TAG, "currentPhotoPath=" + currentPhotoPath);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    startActivityForResult(intent, reqCode);
  }

  /**
   * 判断设备是否具有Intent请求的功能
   * 
   * @param action Intent的请求内容
   * @return true，设备具有此功能；false，设备不具有此功能
   */
  private boolean isIntentAvailable(String action)
  {
    PackageManager packageManager = getApplicationContext().getPackageManager();
    if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA))
    {
      Intent intent = new Intent(action);
      List<ResolveInfo> featureList =
          packageManager.queryIntentActivities(intent,
              PackageManager.MATCH_DEFAULT_ONLY);
      return featureList.size() > 0;
    }
    else
    {
      return false;
    }
  }

  /**
   * 网页登陆，App登陆完成后调用此方法对Web进行登陆
   * 
   * @param url
   */
  private void loadPluginUrl(String url)
  {
    myWebView.loadUrl(url);
  }

  /**
   * 封装了供Web调用本地功能的JavaScript
   * 
   * @author Wilson 20131228
   */
  public class JavaScriptObject
  {
    /**
     * 打开Gallery选择照片，选择完成后跳转去PictureEditorActivity
     */
    @JavascriptInterface
    public void editor()
    {
      openGallery();
    }

    /**
     * 打开设备自带的Camera
     */
    @JavascriptInterface
    public void camera()
    {
      openCamera();
    }

    /**
     * 打开相机，拍照，编辑，最后上传到网站的相册
     */
    @JavascriptInterface
    public void openCameraForAlbum()
    {
      openCamera(GoTravelApplication.UPLOAD_TYPE.ALBUM);
    }

    /**
     * 打开相机，拍照，编辑，最后上传到网站的QA
     */
    @JavascriptInterface
    public void openCameraForQA(String json)
    {
      storedJson = json;
      MyLog.i(TAG, storedJson);
      openCamera(GoTravelApplication.UPLOAD_TYPE.QA);
    }

    /**
     * 上传到QA
     * 
     * @param jsonStr php传过来的页面的内容
     */
    @JavascriptInterface
    public void uploadToQA(final String jsonStr, final String callback)
    {
      MyLog.i(TAG, "json=" + jsonStr);
      // 检测当前网络类型
      int connectedType = HttpUtils.getConnectedType(WebActivity.this);
      if(connectedType != -1)
      {
        // 如果不是使用wifi，弹出对话框警告，让用户选择是否继续上传
        if(connectedType != ConnectivityManager.TYPE_WIFI)
        {
          new AlertDialog.Builder(WebActivity.this).setTitle("网络连接提醒")
              .setMessage("您现在使用的是运营商网络，继续使用可能会被运营商收取流量费用")
              .setPositiveButton("继续使用", new DialogInterface.OnClickListener()
              {

                public void onClick(DialogInterface dialog, int which)
                {
                  if(!(application.getDebugMode() == GoTravelApplication.WEB_DEBUG_MODE || application
                      .getDebugMode() == GoTravelApplication.ALL_DEBUG))
                  {
                    new UploadAsyncTask(jsonStr, callback).execute(
                        GoTravelApplication.POST, QA_UPLOAD_URL,
                        GoTravelApplication.UPLOAD);
                  }
                  else
                  {
                    new UploadAsyncTask(jsonStr, callback).execute(
                        GoTravelApplication.POST, DEBUG_QA_UPLOAD_URL,
                        GoTravelApplication.UPLOAD);
                  }
                }
              }).setNegativeButton("放弃", new DialogInterface.OnClickListener()
              {

                public void onClick(DialogInterface dialog, int which)
                {
                  return;
                }
              }).create().show();
        }
        // 使用wifi，直接上传
        else
        {
          if(!(application.getDebugMode() == GoTravelApplication.WEB_DEBUG_MODE || application
              .getDebugMode() == GoTravelApplication.ALL_DEBUG))
          {
            new UploadAsyncTask(jsonStr, callback).execute(
                GoTravelApplication.POST, QA_UPLOAD_URL,
                GoTravelApplication.UPLOAD);
          }
          else
          {
            MyLog.i(TAG, DEBUG_QA_UPLOAD_URL);
            new UploadAsyncTask(jsonStr, callback).execute(
                GoTravelApplication.POST, DEBUG_QA_UPLOAD_URL,
                GoTravelApplication.UPLOAD);
          }
        }
      }

    }

    /**
     * 打开UploadPicActivity，选择照片再上传
     */
    @JavascriptInterface
    public void uploadAlbum()
    {

    }

    /**
     * 显示一个对话框，供用户选择打开Camera或者Gallery
     */
    @JavascriptInterface
    public void getPictureInAlbum(int id)
    {
      MyLog.i(TAG, "id=" + id);
      albumId = id;
      MyLog.i(TAG, "albumId=" + albumId);
      new AlertDialog.Builder(WebActivity.this)
          .setTitle("Get Picture")
          .setItems(new String[] {"Camera", "Gallery"},
              new DialogInterface.OnClickListener()
              {

                public void onClick(DialogInterface dialog, int which)
                {
                  switch(which)
                  {
                    case 0:
                      openCamera(UPLOAD_TYPE.ALBUM);
                      break;
                    case 1:
                      openGallery();
                      break;
                    default:
                      break;
                  }
                }
              }).create().show();
    }

    /**
     * 显示一个对话框，供用户选择打开Camera或者Gallery
     */
    @JavascriptInterface
    public void getPicture()
    {
      new AlertDialog.Builder(WebActivity.this)
          .setTitle("Get Picture")
          .setItems(new String[] {"Camera", "Gallery"},
              new DialogInterface.OnClickListener()
              {

                public void onClick(DialogInterface dialog, int which)
                {
                  switch(which)
                  {
                    case 0:
                      openCamera();
                      break;
                    case 1:
                      openGallery();
                      break;
                    default:
                      break;
                  }
                }
              }).create().show();
    }

    /**
     * 让网页调用的login方法
     */
    @JavascriptInterface
    public void webDoLogin()
    {
      authorization.login(TencentConstants.THIRD_PARTY_NAME_TENCENT,
          WebActivity.this);
    }

    /**
     * 让网页调用的logout方法
     */
    @JavascriptInterface
    public void webDoLogout()
    {
      authorization.logout(WebActivity.this);
      mainHandler.sendEmptyMessage(HANDLER_TENCENT_WEB_LOGOUT);
    }

    /**
     * 显示ProgressBar
     */
    @JavascriptInterface
    public void showLoadingBar()
    {
      mainHandler.sendEmptyMessage(HANDLER_WEB_VIEW_SHOW_PROGRESS_BAR);
    }

    /**
     * 隐藏ProgressBar
     */
    @JavascriptInterface
    public void hideLoadingBar()
    {
      mainHandler.sendEmptyMessage(HANDLER_WEB_VIEW_HIDE_PROGRESS_BAR);
    }

    /**
     * 清除WebView产生的缓存
     */
    @JavascriptInterface
    public void clearCache()
    {
      // clearCache()会清除app中所有WebView的缓存
      myWebView.clearCache(true);
    }

    /**
     * 清除WebView缓存
     */
    @JavascriptInterface
    public void clearCookies()
    {
      MyLog.i(TAG, "clear cookies");
      CookieManager.getInstance().removeAllCookie();
    }
  }

  /**
   * 可以通过重写onJsAlert()自定义JavaScript alert的样式
   * 
   * @author Wilson 20131228
   */
  private class MyWebChromeClient extends WebChromeClient
  {

    @Override
    public boolean onJsAlert(WebView view, String url, String message,
        JsResult result)
    {
      return super.onJsAlert(view, url, message, result);
    }

  }

  /**
   * 继承WebViewClient，重写部分方法定义WebView的不同功能
   * 
   * @author Wilson 20131228
   */
  private class MyWebViewClient extends WebViewClient
  {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
      // WebView自己处理其中触发的链接
      view.loadUrl(url);
      return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon)
    {
      mainHandler.sendEmptyMessage(HANDLER_WEB_VIEW_SHOW_PROGRESS_BAR);
      super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url)
    {
      // 如果是QQ登录的URL，调用Javascript的checkqqid方法检查openid
      if(url.equals(TencentConstants.QQ_LOGIN_URL))
      {
        String javascript = "";
        String openId = authorization.getTencentOpenId();

        // 调用Javascript的checkqqid方法
        javascript = "javascript:checkqqid('" + openId + "')";
        myWebView.loadUrl(javascript);
      }

      // 如果是从Editor返回QA，让php记录图片的路径
      if(isFromEditorTOQA)
      {
        MyLog.i(TAG, "from editor to qa");
        String script =
            "javascript:picName('" + resultPath + "', '" + storedJson + "')";
        MyLog.i(TAG, script);
        myWebView.loadUrl(script);
        isFromEditorTOQA = false;
      }

      storedUrl = url;
      super.onPageFinished(view, url);
      mainHandler.sendEmptyMessage(HANDLER_WEB_VIEW_HIDE_PROGRESS_BAR);
    }

  }

  /**
   * 初始化WebView
   */
  @SuppressLint("SetJavaScriptEnabled")
  private void initWebView()
  {
    myWebView = (WebView) findViewById(R.id.web_view);
    WebSettings webSettings = myWebView.getSettings();

    // 允许WebView使用Javascript
    webSettings.setJavaScriptEnabled(true);

    /*
     * 允许Javascript自动打开window，即允许Javascript function window.open()。
     * 要允许Javascript弹出alert窗口必须设置为true。
     */
    webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

    // 支持WebView内建的缩放机制，guang'wang
    webSettings.setSupportZoom(true);
    /*
     * The built-in mechanisms are the only currently supported zoom
     * mechanisms, so it is recommended that this setting is always enabled.
     */
    webSettings.setBuiltInZoomControls(true);

    if(!(application.getDebugMode() == GoTravelApplication.WEB_DEBUG_MODE || application
        .getDebugMode() == GoTravelApplication.ALL_DEBUG))
    {
      // 支持缓存
      webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    // 支持接收cookie
    CookieManager.getInstance().setAcceptCookie(true);

    // 设置WebChromeClient，允许Javascript弹出alert
    myWebView.setWebChromeClient(new MyWebChromeClient());
    // 设置WebViewClient，由WebView处理所有的链接，并且load完页面后处理一些事件
    myWebView.setWebViewClient(new MyWebViewClient());
    // 添加Javascript接口，供网页调用本地功能，如android.test()
    myWebView.addJavascriptInterface(new JavaScriptObject(), "gotravel");

    if(storedUrl == null)
    {
      MyLog.i(TAG, "storedUrl is null");
      if(application.getDebugMode() == GoTravelApplication.WEB_DEBUG_MODE
          || application.getDebugMode() == GoTravelApplication.ALL_DEBUG)
      {
        myWebView
            .loadUrl("http://192.168.3.65/ecmalltwo/src/plugin.php?id=answer:phone&act=home");
        // myWebView.loadUrl("http://192.168.3.65/test/index.html");
//         myWebView.loadUrl("http://192.168.3.109/");
        // myWebView.loadUrl(GoTravelApplication.INDEX_URL);
//         myWebView
//         .loadUrl("http://192.168.3.65/ecmalltwo/src/home.php?mod=space&uid=1&do=album&id=1");
        // myWebView
        // .loadUrl("http://192.168.3.65/ecmalltwo/src/plugin.php?id=answer:p hone&act=home");
        // myWebView.loadUrl("http://192.168.3.65/ecmalltwo/src/plugin.php?id=avatar:phone&act=home");
        // myWebView.loadUrl("http://192.168.3.65/ecmalltwo/src/plugin.php?id=avatar:phone&act=login");
      }
      else
      {
        myWebView.loadUrl(GoTravelApplication.INDEX_URL);
      }
    }
    else
    {
      MyLog.i(TAG, "storedUrl=" + storedUrl);
      myWebView.loadUrl(storedUrl);
      storedUrl = null;
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
    // 如果用户退回键并且WebView的内容可回退，返回上一页
    if((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack())
    {
      myWebView.goBack();
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  private class MyBaseCompleteListener extends BaseCompleteListener
  {

    // 通过新浪登录成功后会回调此函数
    @Override
    public void doSinaComplete()
    {
      MyLog.i(TAG, "doSinaComplete");
      Toast.makeText(getApplicationContext(),
          "uid=" + authorization.getSinaUid(), Toast.LENGTH_LONG).show();
    }

    // 调用腾讯提供的API接口成功后回调此函数
    @Override
    public void doApiComplete(JSONObject response, Object state)
    {
      MyLog.i(TAG, "doApiComplete");
    }

    // 通过QQ登录成功后会回调此函数
    @Override
    public void doUiComplete()
    {
      MyLog.i(TAG, "doUiComplete");
      SharedPreferences.Editor editor =
          getApplicationContext().getSharedPreferences(
              GoTravelApplication.PREFS_LOCAL_FILE, Context.MODE_PRIVATE)
              .edit();
      editor.putString(GoTravelApplication.PREFS_THIRD_PARTY,
          authorization.getThirdPartyName());
      editor.commit();

      // 登录完毕，通知UI线程更新UI
      Message msg = new Message();
      msg.what = HANDLER_TENCENT_WEB_LOGIN;
      mainHandler.sendMessage(msg);
    }

  }

  /**
   * 用于上传文件的AsyncTask
   * 
   * @author Wilson 20140110
   */
  private class UploadAsyncTask extends AsyncTask<String, Void, String>
  {

    ProgressDialog dialog = null;
    String path = null;
    String callback = null;

    String jsonStr;
    JSONObject response = null;
    JSONObject status = null;

    int id = 0;

    boolean canUploadFile = false;

    public UploadAsyncTask()
    {
      super();
      canUploadFile = true;
    }

    public UploadAsyncTask(String str)
    {
      super();

      if(application.getUploadType() == UPLOAD_TYPE.ALBUM)
      {
        this.path = str;
        canUploadFile = true;
      }
      else if(application.getUploadType() == UPLOAD_TYPE.QA)
      {
        this.jsonStr = str;
        canUploadFile = false;
      }

    }

    public UploadAsyncTask(String jsonStr, String callback)
    {
      super();

      this.jsonStr = jsonStr;
      canUploadFile = true;

      this.callback = callback;
    }

    @Override
    protected void onPreExecute()
    {
      // 显示一个ProgressDialog等待网络操作的结束
      dialog = new ProgressDialog(WebActivity.this);
      dialog.setMessage(application.getString(R.string.uploading));
      dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      dialog.setCanceledOnTouchOutside(false);
      dialog.show();
    }

    @Override
    protected String doInBackground(String... params)
    {
      boolean hasPicName = false;

      try
      {
        if(params[0].equals(GoTravelApplication.POST))
        {
          if(params[2].equals(GoTravelApplication.UPLOAD))
          {
            // 如果有json，先上传json
            if(jsonStr != null)
            {
              JSONObject json;

              json = new JSONObject(jsonStr);

              id = json.getInt(GoTravelApplication.JSON_KEY_QID);
              MyLog.i(TAG, "qid=" + id);

              JSONArray jsonArray = json.names();
              for(int i = 0; i < jsonArray.length(); i++)
              {
                String name = jsonArray.getString(i);
                if(name.equals(GoTravelApplication.JSON_KEY_PIC_NAME))
                {
                  hasPicName = true;
                  path = json.getString(GoTravelApplication.JSON_KEY_PIC_NAME);
                  json.remove(GoTravelApplication.JSON_KEY_PIC_NAME);
                  jsonStr = json.toString();
                }
              }

              // 上传json
              application.getHttpUtils().createPostConnection(params[1],
                  "application/json", -1);
              String result = application.getHttpUtils().sendContent(jsonStr);
              application.getHttpUtils().releaseConnection();
              MyLog.i(TAG, "result=" + result);

              response = new JSONObject(result);
              status =
                  response.getJSONObject(GoTravelApplication.JSON_KEY_STATUS);
              if(status.getInt(GoTravelApplication.JSON_KEY_STATUS_CODE) == GoTravelApplication.SERVER_RESPONSE_OK)
              {
                canUploadFile = true;
              }
              else
              {
                canUploadFile = false;
              }
            }

            if(canUploadFile)
            {
              // Album的上传
              if(application.getUploadType() == GoTravelApplication.UPLOAD_TYPE.ALBUM)
              {
                MyLog.i(TAG, "upload picture to album");
                MyLog.i(TAG, "path=" + path);
                String contentType = null;
                int size = 0;
                contentType = ImageUtils.getContentType(path);
                size = ImageUtils.getImageSize(path);
                application.getHttpUtils().createPostConnection(params[1],
                    contentType, size);
                ByteBuffer headBuf = ByteBuffer.allocate(4);
                headBuf.order(ByteOrder.LITTLE_ENDIAN);
                MyLog.i(TAG, "head albumId=" + albumId);
                headBuf.putInt(albumId);
                headBuf.flip();

                String result = null;
                result = application.getHttpUtils().sendFile(headBuf, path);
                application.getHttpUtils().releaseConnection();
                response = new JSONObject(result);

                status =
                    response.getJSONObject(GoTravelApplication.JSON_KEY_STATUS);
                if(status.getInt(GoTravelApplication.JSON_KEY_STATUS_CODE) == GoTravelApplication.SERVER_RESPONSE_OK)
                {
                  MyLog.i(TAG, "upload successfully");
                  return "true";
                }
                else
                {
                  return "false";
                }
              }
              // QA的上传
              else if(application.getUploadType() == GoTravelApplication.UPLOAD_TYPE.QA)
              {
                MyLog.i(TAG, "upload file");
                JSONObject data;
                int type = 0;

                if(!hasPicName)
                {
                  isAllUploadSuccessfully = true;
                  return "true";
                }
                else
                {
                  data =
                      response.getJSONObject(GoTravelApplication.JSON_KEY_DATA);
                }

                type = data.getInt(GoTravelApplication.JSON_KEY_DATA_TYPE);
                // id = data.getInt(GoTravelApplication.JSON_KEY_DATA_ID);

                String contentType = null;
                int size = 0;
                // 获取Content-Type
                contentType = ImageUtils.getContentType(path);
                MyLog.i(TAG, "contentType=" + contentType);
                // 获取文件大小
                size = ImageUtils.getImageSize(path);
                // 建立Post链接
                application.getHttpUtils().createPostConnection(params[1],
                    contentType, size);

                // 给文件添加头部：前4字节为type，后4字节为id
                ByteBuffer headBuf = ByteBuffer.allocate(8);
                headBuf.order(ByteOrder.LITTLE_ENDIAN);
                // ByteBuffer typeBuf = ByteBuffer.allocate(4);
                // typeBuf.order(ByteOrder.LITTLE_ENDIAN);
                // typeBuf.putInt(type);
                // typeBuf.flip();
                // ByteBuffer idBuf = ByteBuffer.allocate(4);
                // idBuf.order(ByteOrder.LITTLE_ENDIAN);
                // idBuf.putInt(id);
                // idBuf.flip();
                // headBuf.put(typeBuf);
                // headBuf.put(idBuf);
                headBuf.putInt(type);
                headBuf.putInt(id);
                headBuf.flip();
                MyLog.i(TAG, "head type=" + headBuf.getInt() + ", id="
                    + headBuf.getInt());

                String result = null;
                result = application.getHttpUtils().sendFile(headBuf, path);
                application.getHttpUtils().releaseConnection();

                response = new JSONObject(result);

                status =
                    response.getJSONObject(GoTravelApplication.JSON_KEY_STATUS);
                if(status.getInt(GoTravelApplication.JSON_KEY_STATUS_CODE) == GoTravelApplication.SERVER_RESPONSE_OK)
                {
                  MyLog.i(TAG, "upload successfully");
                  return "true";
                }
                else
                {
                  return "false";
                }

              }
              else
              {
                MyLog.i(TAG, "upload to album");
              }
            }
            else
            {
              return "false";
            }

          }
        }
        return null;
      }
      catch(Exception e)
      {
        e.printStackTrace();
        return "Unable to retrieve web page. URL may be invalid.";
      }
    }

    @Override
    protected void onPostExecute(String result)
    {
      dialog.dismiss();
      if(result.equals("true"))
      {
        // 如果网页有给回调函数，上传完毕后调用该回调函数
        if(this.callback != null)
        {
          MyLog.i(TAG, "callback=" + callback);
          myWebView.loadUrl("javascript:" + callback);
        }
      }
      else
      {
        new AlertDialog.Builder(WebActivity.this)
            .setTitle(R.string.upload_failed)
            .setMessage(R.string.uploaded_failed_message)
            .setPositiveButton(R.string.str_btn_yes, null)
            .setNegativeButton(R.string.str_btn_no, null).show();
      }
    }

  }
}
