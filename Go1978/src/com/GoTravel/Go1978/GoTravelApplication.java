package com.GoTravel.Go1978;


import com.GoTravel.Go1978.authorization.MyAuthorization;
import com.GoTravel.Go1978.log.MyLog;
import com.GoTravel.Go1978.utils.HttpUtils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * 用于代替默认的Application：
 * 1. 记录一些默认的静态常量
 * 2. 实现公用的方法获取MyAuthorization实例（单例类）
 * 
 * @author Wilson 20131228
 */
public class GoTravelApplication extends Application
{
  private static final String TAG = "GoTravelApplication";

  // GO1978 URL
  public static final String INDEX_URL =
      "http://www.go1978.com/portal.php?mod=guide&mobile=2";

  // Preference - preference文件名.
  public static final String PREFS_LOCAL_FILE = "GoTravelPrefs";
  // Preference key - 第三方登录的名称
  public static final String PREFS_THIRD_PARTY = "third_party";
  // Preference key - Tencent分服务器返回的sid
  public static final String PREFS_O2YW_2132_SID = "O2Yw_2132_sid";

  // Bundle key - 图片路径
  public static final String BUNDLE_KEY_PHOTO_PATH = "photopath";
  // Bundle key - 当前url
  public static final String BUNDLE_KEY_URL = "url";
  // Bundle key - QA的form json
  public static final String BUNDLE_KEY_QA_JSON = "qa_json";
  // Bundle key - album id
  public static final String BUNDLE_KEY_ALBUM_ID = "albumId";

  // JSON key - Tencent分服务器返回的sid
  public static final String JSON_KEY_O2YW_2132_SID = "O2Yw_2132_sid";
  // JSON key - status
  public static final String JSON_KEY_STATUS = "status";
  // JSON key - status.message
  public static final String JSON_KEY_STATUS_MESSAGE = "message";
  // JSON key - status.code
  public static final String JSON_KEY_STATUS_CODE = "code";
  // JSON key - data
  public static final String JSON_KEY_DATA = "data";
  // JSON key - data.file
  public static final String JSON_KEY_DATA_FILE = "file";
  // JSON key - data.files
  public static final String JSON_KEY_DATA_FILES = "files";
  // JSON key - data.type
  public static final String JSON_KEY_DATA_TYPE = "type";
  // JSON key - data.id
  public static final String JSON_KEY_DATA_ID = "id";
  // JSON key - cancel_upload
  public static final String JSON_KEY_CANCEL_UPLOAD = "cancel_upload";
  // JSON key - comment
  public static final String JSON_KEY_COMMENT = "comment";
  // JSON key - file_num
  public static final String JSON_KEY_FILE_NUM = "file_num";
  // JSON key - sina_weibo
  public static final String JSON_KEY_WEIBO = "sina_weibo";
  // JSON key - sina_weibo.share_to_weibo
  public static final String JSON_KEY_SINA_SHARE_TO_WEIBO = "share_to_weibo";
  // JSON key - sina_weibo.selected_pic_num
  public static final String JSON_KEY_SINA_SELECTED_PICTURE_NUM =
      "selected_pic_num";
  // JSON key - pic_name
  public static final String JSON_KEY_PIC_NAME = "pic_name";
  // JSON key - qid
  public static final String JSON_KEY_QID = "qid";

  // Server response - 200: OK
  public static final int SERVER_RESPONSE_OK = 200;
  // Server response - 500: ERROR
  public static final int SERVER_RESPONSE_ERROR = 500;

  // Debug模式标志位
  public static final int ALL_DEBUG = 0;
  public static final int PICTURE_EDITOR_DEBUG_MODE = 1;
  public static final int WEB_DEBUG_MODE = 2;
  
  // 1：debug模式；其他为非debug模式
  private int debugMode = 2;

  // 记录用户使用的第三方登录的名称
  private String thirdPartyLogin;
  // 用于进行登录验证和调用各种第三方接口
  private MyAuthorization authorization;

  // 上传的标识
  public static final String UPLOAD = "upload";
  // 上传出错，再上传的标识
  public static final String REUPLOAD = "reupload";
  
  // public static final String GET = "get";
  public static final String POST = "post";
  HttpUtils httpUtils;

  /**
   * 上传图片类型的枚举类
   * 
   * @author Wilson 20140105
   */
  public enum UPLOAD_TYPE
  {
    NONE, ALBUM, // 上传到相册
    QA // 上传到QA
  }

  enum FAIL_ACTION
  {
    WAITING, CONTINUOU, CANCEL
  }

  // 上传图片的类型
  private UPLOAD_TYPE uploadType = UPLOAD_TYPE.NONE;

  @Override
  public void onCreate()
  {
    SharedPreferences prefs =
        getSharedPreferences(PREFS_LOCAL_FILE, Context.MODE_PRIVATE);
    thirdPartyLogin = prefs.getString(PREFS_THIRD_PARTY, "");
    // 创建MyAuthorization实例
    authorization =
        new MyAuthorization(getApplicationContext(), thirdPartyLogin);
    MyLog.i(TAG, "thirdPartyLogin=" + thirdPartyLogin);

    if(httpUtils == null)
    {
      httpUtils = new HttpUtils();
    }

    super.onCreate();
  }

  /**
   * 获取MyAuthorization实例
   * 
   * @return MyAuthorization
   */
  public MyAuthorization getMyAuthorization()
  {
    if(authorization != null)
    {
      return authorization;
    }

    return new MyAuthorization(getApplicationContext());
  }

  /**
   * 获取DEBUG_FLAG的值
   * 
   * @return DEBUG_FLAG的值
   */
  public int getDebugMode()
  {
    return debugMode;
  }

  /**
   * 获取uploadType
   * 
   * @return uploadType
   */
  public UPLOAD_TYPE getUploadType()
  {
    return this.uploadType;
  }

  /**
   * 设置uploadType
   * 
   * @param type
   */
  public void setUploadType(UPLOAD_TYPE type)
  {
    this.uploadType = type;
  }

  /**
   * 获取HttpUtils
   * 
   * @return HttpUtils
   */
  public HttpUtils getHttpUtils()
  {
    return httpUtils;
  }

}
