package com.GoTravel.Go1978.authorization;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.GoTravel.Go1978.constants.TencentConstants;
import com.GoTravel.Go1978.log.MyLog;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;


/**
 * @description : 此类封装涉及QQ直连的各种参数和功能函数
 * @author : wilson
 * @version : 1.00
 */
public class TencentApplication
{
  private static final String TAG = "DemoAuth -- TencentApplication";

  // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
  private Tencent mTencent;

  private SharedPreferences prefs;
  private SharedPreferences.Editor editor;

  private String openId;
  private String accessToken;
  private Long expires_in;
  private String nickName;

  private BaseUiCompleteListener myBaseUiCompleteListener;
  private BaseApiCompleteListener myBaseApiCompleteListener;

  public TencentApplication(Context context)
  {
    // 获取Tencent实例
//    mTencent = Tencent.createInstance(TencentConstants.APP_ID, context);

    /*
     * 从配置文件读取配置变量
     */
    prefs =
        context.getSharedPreferences(TencentConstants.PREFS_TENCENT_FILE,
            Context.MODE_PRIVATE);
    editor = prefs.edit();
    openId = prefs.getString(TencentConstants.PREFS_KEY_OPENID, "");
    accessToken = prefs.getString(TencentConstants.PREFS_KEY_ACCESS_TOKEN, "");
    expires_in =
        (prefs.getLong(TencentConstants.PREFS_KEY_EXPIRES_IN, 0) - System
            .currentTimeMillis()) / 1000;
    nickName = prefs.getString(TencentConstants.PREFS_KEY_NICK_NAME, "");
  }

  /**
   * @description : 登陆/校验登陆状态
   * @param activity : 发起登陆的应用的Activity
   */
  public void login(Activity activity)
  {
    IUiListener listener = new BaseUiListener();

    /*
     * 发起登陆/校验登陆状态。
     * 如果在调用login()之前，先调用setOpenId()和setAccessToken()，如果expires_in
     * 大于0，执行校验登陆状态。
     */
    mTencent.setOpenId(openId);
    mTencent.setAccessToken(accessToken, expires_in.toString());
    mTencent.login(activity, TencentConstants.SCOPE_ALL, listener);
  }

  /**
   * @description : 退出
   * @param activity : 发起退出的应用的Activity
   */
  public void logout(Activity activity)
  {
    mTencent.logout(activity);
  }

  /**
   * @description : 发送异步调用请求访问腾讯提供的OpenAPI
   * @param graphPath : 要调用的接口名称
   * @param params : 应用传入的邀请分享等参数
   * @param httpMethod : 使用的http方式
   * @param listener : 回调接口，IAPiListener实例
   * @param state : 状态对象，将会在回调时原样传回给listener，供应用识别异步调用。
   */
  public void requestAsync(String graphPath, Bundle params, String httpMethod,
      Object state, String scope, boolean needReAuth)
  {
    mTencent.requestAsync(graphPath, params, httpMethod, new BaseApiListener(
        scope, needReAuth), state);
  }

  /**
   * @description : 检查登陆状态
   * @return ： 如果处于登陆状态，返回true；否则返回false
   */
  public boolean checkSession()
  {
    return mTencent.isSessionValid();
  }

  /**
   * @description : 应用的onActivityResult()必须调用这个方法
   * @param requestCode
   * @param resultCode
   * @param data
   */
  public void setOnActivityResult(int requestCode, int resultCode, Intent data)
  {
    mTencent.onActivityResult(requestCode, resultCode, data);
  }

  /**
   * @description : 清除配置信息
   */
  public void clearPrefs()
  {
    editor.clear();
    editor.commit();
  }

  /**
   * @description : 用以接收QQ互联Android SDK已封装好的借口返回的调用结果，
   *              如：登陆、快速支付登陆、应用分享、应用邀请等接口
   * @author : Wilson
   * @version : 1.00
   */
  public class BaseUiListener implements IUiListener
  {

    public void onCancel()
    {
      MyLog.i(TAG, "BaseUiListener onCancel");
    }

    public void onComplete(JSONObject json)
    {
      MyLog.i(TAG, "BaseUiListener onComplete");
      BaseUiCompleteListener listener = myBaseUiCompleteListener;
      String openId = null;
      String token = null;
      long expires_in = 0;
      try
      {
        openId = json.getString(TencentConstants.PREFS_KEY_OPENID);
        token = json.getString(TencentConstants.PREFS_KEY_ACCESS_TOKEN);
        /*
         * expires_in在存储之前，需要经过如下换算：
         * System.currentTimeMillis() + expires_in * 1000;
         */
        expires_in =
            System.currentTimeMillis()
                + json.getLong(TencentConstants.PREFS_KEY_EXPIRES_IN) * 1000;
      }
      catch(JSONException e)
      {
        e.printStackTrace();
      }
      setOpenId(openId);
      setAccessToken(token);
      setExpiresIn(expires_in);
      listener.doUiComplete();
    }

    public void onError(UiError arg0)
    {
      MyLog.i(TAG, "BaseUiListener onError");
    }

  }

  /**
   * @description : 用以接收使用requestAsync、request等通用方法调用
   *              QQ互联Android SDK未封装的接口返回的调用结果，如：
   *              上传图片，查看相册等。
   * @author Wilson
   * @version 1.00
   */
  public class BaseApiListener implements IRequestListener
  {
    private String mScope = "all";
    private boolean mNeedReAuth = false;

    public BaseApiListener(String scope, boolean needReAuth)
    {
      mScope = scope;
      mNeedReAuth = needReAuth;
    }

    public void onComplete(JSONObject response, Object state)
    {
      BaseApiCompleteListener listener = myBaseApiCompleteListener;
      MyLog.i(TAG, "BaseApilistener onComplete");
      listener.doApiComplete(response, state);
    }

    public void onConnectTimeoutException(ConnectTimeoutException arg0,
        Object arg1)
    {
      // TODO Auto-generated method stub
      MyLog.i(TAG, "BaseApilistener onConnectTimeoutException");
    }

    public void onHttpStatusException(HttpStatusException arg0, Object arg1)
    {
      // TODO Auto-generated method stub
      MyLog.i(TAG, "BaseApilistener onHttpStatusException");
    }

    public void onIOException(IOException arg0, Object arg1)
    {
      // TODO Auto-generated method stub
      MyLog.i(TAG, "BaseApilistener onIOException");
    }

    public void onJSONException(JSONException arg0, Object arg1)
    {
      // TODO Auto-generated method stub
      MyLog.i(TAG, "BaseApilistener onJSONException");
    }

    public void
        onMalformedURLException(MalformedURLException arg0, Object arg1)
    {
      // TODO Auto-generated method stub
      MyLog.i(TAG, "BaseApilistener onMalformedURLException");
    }

    public void onNetworkUnavailableException(NetworkUnavailableException arg0,
        Object arg1)
    {
      // TODO Auto-generated method stub
      MyLog.i(TAG, "BaseApilistener onNetworkUnavailableException");
    }

    public void onSocketTimeoutException(SocketTimeoutException arg0,
        Object arg1)
    {
      // TODO Auto-generated method stub
      MyLog.i(TAG, "BaseApilistener onSocketTimeoutException");
    }

    public void onUnknowException(Exception arg0, Object arg1)
    {
      // TODO Auto-generated method stub
      MyLog.i(TAG, "BaseApilistener onUnknowException");
    }

  }

  public void setBaseUiCompleteListener(
      BaseUiCompleteListener baseUiCompleteListener)
  {
    myBaseUiCompleteListener = baseUiCompleteListener;
  }

  /**
   * @description : 根据实际需要，实现次接口，用于处理登陆、快速支付登陆、应用分享、
   *              应用邀请等接口执行成功后需要执行的代码
   * @param values : QQ Server根据用户调用的不同接口而返回的JSON字串
   */
  public interface BaseUiCompleteListener
  {
    public void doUiComplete();
  }

  public void setBaseApiCompleteListener(
      BaseApiCompleteListener baseApiCompleteListener)
  {
    myBaseApiCompleteListener = baseApiCompleteListener;
  }

  /**
   * @description : 根据实际需要，实现此接口，用于处理各种未封装API的返回结果
   * @param response : QQ Server根据用户调用的不同接口而返回的JSON字串
   * @param state : 状态对象，在调用requestAsync的时候传入，SDK原样返回
   */
  public interface BaseApiCompleteListener
  {
    public void doApiComplete(JSONObject response, Object state);
  }

  public String getOpenId()
  {
    return this.openId;
  }

  public void setOpenId(String openid)
  {
    this.openId = openid;
    editor.putString(TencentConstants.PREFS_KEY_OPENID, openid);
    editor.commit();
  }

  public String getAccessToken()
  {
    return this.accessToken;
  }

  public void setAccessToken(String accessToken)
  {
    this.accessToken = accessToken;
    editor.putString(TencentConstants.PREFS_KEY_ACCESS_TOKEN, accessToken);
    editor.commit();
  }

  public long getExpiresIn()
  {
    return this.expires_in;
  }

  public void setExpiresIn(long expires_in)
  {
    this.expires_in = expires_in;
    editor.putLong(TencentConstants.PREFS_KEY_EXPIRES_IN, expires_in);
    editor.commit();
  }

  public String getNickName()
  {
    return this.nickName;
  }

  public void setNickName(String nickName)
  {
    this.nickName = nickName;
    editor.putString(TencentConstants.PREFS_KEY_NICK_NAME, nickName);
    editor.commit();
  }

  public boolean isSessionValid()
  {
    if(!openId.equals("") && !accessToken.equals("") && expires_in > 0)
    {
      return true;
    }
    else
    {
      return false;
    }
  }

}
