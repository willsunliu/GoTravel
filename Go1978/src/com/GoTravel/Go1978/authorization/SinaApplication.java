package com.GoTravel.Go1978.authorization;


import java.text.SimpleDateFormat;
import java.util.Locale;

import com.GoTravel.Go1978.GoTravelApplication;
import com.GoTravel.Go1978.constants.SinaConstants;
import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class SinaApplication
{
  private static final String TAG = "SinaApplication";

  private Context context;

  private SharedPreferences prefs;
  private SharedPreferences.Editor editor;

  Weibo mWeibo;
  private Oauth2AccessToken mAccessToken;
  /** 注意：SsoHandler 仅当sdk支持sso时有效 */
  private SsoHandler mSsoHandler;

  private String uid;
  private String token;
  private String expires_in;

  private OnSinaCompleteListener myOnSinaCompleteListener;
  private IWeiboAPI weiboAPI;

  public SinaApplication(Context context)
  {
    this.context = context;

    prefs =
        context.getSharedPreferences(SinaConstants.PREFS_SINA_FILE,
            Context.MODE_PRIVATE);
    editor = prefs.edit();
    uid = prefs.getString(SinaConstants.UID, "");
    token = prefs.getString(SinaConstants.ACCESS_TOKEN, "");

    // 获取Weibo实例
    mWeibo =
        Weibo.getInstance(SinaConstants.APP_KEY, SinaConstants.REDIRECT_URL,
            SinaConstants.SCOPE_STRING);
  }

  /**
   * oauth login
   * 
   * @param activity 用到第三方接口的Activity
   */
  public void OauthLogin(Activity activity)
  {
    mWeibo.anthorize(activity, new AuthDialogListener());
  }

  /**
   * sso login
   * 
   * @param activity 用到第三方接口的Activity
   */
  public void SsoLogin(Activity activity)
  {
    mSsoHandler = new SsoHandler(activity, mWeibo);
    mSsoHandler.authorize(new AuthDialogListener(), null);
  }

  /**
   * 检查登录状态
   * 
   * @return 处于登录状态返回true；不处于登录状态返回false
   */
  public boolean isSessionValid()
  {
    if(mAccessToken == null)
    {
      return false;
    }
    else
    {
      if(mAccessToken.isSessionValid())
      {
        return true;
      }
      else
      {
        return false;
      }
    }
  }

  /**
   * 新浪登录时的回调接口
   * 
   * @author Wilson 20131228
   */
  private class AuthDialogListener implements WeiboAuthListener
  {

    public void onCancel()
    {
      // TODO Auto-generated method stub

    }

    // 登录成功回调此函数
    public void onComplete(Bundle values)
    {
      String token = values.getString(SinaConstants.ACCESS_TOKEN);
      String expires_in = values.getString(SinaConstants.EXPIRES_IN);
      String uid = values.getString(SinaConstants.UID);
      Log.i(TAG, "sina token=" + token + "; expires_in=" + expires_in
          + "; uid=" + uid);

      // 新浪SDK提供的类，由access_token和expires_in生成一个判断登录状态的token
      mAccessToken = new Oauth2AccessToken(token, expires_in);

      if(mAccessToken.isSessionValid())
      {
        // 登录成功，更新配置文件
        String date =
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                .format(new java.util.Date(mAccessToken.getExpiresTime()));
        Toast.makeText(
            context,
            "认证成功：\r\naccess_token: " + token + "\r\nexpires_in: " + expires_in
                + "\r\n有效期：" + date, Toast.LENGTH_LONG).show();
        editor.clear();
        editor.commit();
        editor.putString(GoTravelApplication.PREFS_THIRD_PARTY, "sina");
        editor.putString(SinaConstants.ACCESS_TOKEN, token);
        editor.putString(SinaConstants.EXPIRES_IN, expires_in);
        editor.putString(SinaConstants.UID, uid);
        editor.commit();
        myOnSinaCompleteListener.doSinaComplete();
      }
    }

    public void onError(WeiboDialogError arg0)
    {
      // TODO Auto-generated method stub

    }

    public void onWeiboException(WeiboException arg0)
    {
      // TODO Auto-generated method stub

    }

  }

  /**
   * 初始化新浪API实例
   * 
   * @param activity 用到第三方接口的Activity
   */
  public void initWeiboAPI(Activity activity)
  {
    IWeiboAPI weiboAPI =
        WeiboSDK.createWeiboAPI(activity, SinaConstants.APP_KEY);
    weiboAPI.registerApp();
  }

  /**
   * 分享文字内容
   * 
   * @param activity 用到第三方接口的Activity
   * @param text 文字内容
   */
  public void reqTextMsg(Activity activity, String text)
  {
    WeiboMessage weiboMessage = new WeiboMessage();
    TextObject textObj = new TextObject();
    textObj.text = text;
    weiboMessage.mediaObject = textObj;
    SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
    req.transaction = String.valueOf(System.currentTimeMillis());
    req.message = weiboMessage;
    weiboAPI.sendRequest(activity, req);
  }

  /**
   * 分享图片和文字混合的内容
   * 
   * @param activity 用到第三方接口的Activity
   * @param path 图片路径
   * @param text 文字内容
   */
  public void reqMultiMsg(Activity activity, String path, String text)
  {
    TextObject textObj = new TextObject();
    textObj.text = text;
    ImageObject imgObj = new ImageObject();
    imgObj.imagePath = path;

    WeiboMultiMessage multiMsg = new WeiboMultiMessage();
    multiMsg.textObject = textObj;
    multiMsg.imageObject = imgObj;
    SendMultiMessageToWeiboRequest multiRequest =
        new SendMultiMessageToWeiboRequest();
    multiRequest.multiMessage = multiMsg;
    multiRequest.transaction = String.valueOf(System.currentTimeMillis());
    weiboAPI.sendRequest(activity, multiRequest);
  }

  /**
   * 判断是否是sso login
   * 
   * @return 是sso login返回true，否则返回false
   */
  public boolean isSsoLogin()
  {
    if(mSsoHandler != null)
    {
      return true;
    }

    return false;
  }

  /**
   * 使用sso login需要在onActivityResult调用此方法
   * 
   * @param requestCode
   * @param resultCode
   * @param data
   */
  public void SsoHandlerAuthorizeCallBack(int requestCode, int resultCode,
      Intent data)
  {
    mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
  }

  /**
   * 在调用新浪第三方接口后被回调的接口
   * 
   * @author Wilson 20131228
   */
  public interface OnSinaCompleteListener
  {
    public void doSinaComplete();
  }

  /**
   * 设置OnSinaCompleteListener
   * 
   * @param onSinaCompleteListener 实现了OnSinaCompleteListener的实例
   */
  public void setOnCompleteListener(
      OnSinaCompleteListener onSinaCompleteListener)
  {
    myOnSinaCompleteListener = onSinaCompleteListener;
  }

  /**
   * 获取uid
   * 
   * @return uid
   */
  public String getUid()
  {
    return uid;
  }

  /**
   * 设置uid
   * 
   * @param uid
   */
  public void setUid(String uid)
  {
    this.uid = uid;
  }

  /**
   * 获取access_token
   * 
   * @return access_token
   */
  public String getAccessToken()
  {
    return token;
  }

  /**
   * 设置access_token
   * 
   * @param token
   */
  public void setAccessToken(String token)
  {
    this.token = token;
  }

  /**
   * 获取expires_in
   * 
   * @return expires_in
   */
  public String getExpiresIn()
  {
    return expires_in;
  }

  /**
   * 设置expires_in
   * 
   * @param expires_in
   */
  public void setExpiresIn(String expires_in)
  {
    this.expires_in = expires_in;
  }

  /**
   * 获取IWeiboAPI实例
   * 
   * @return IWeiboAPI实例
   */
  public IWeiboAPI getIWeiboAPI()
  {
    return weiboAPI;
  }
}
