package com.GoTravel.Go1978.authorization;


import com.GoTravel.Go1978.constants.SinaConstants;
import com.GoTravel.Go1978.constants.TencentConstants;
import com.sina.weibo.sdk.api.IWeiboHandler.Response;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


/**
 * 封装统一第三方接口的方法，如登录，退出，分享等
 * 
 * @author Wilson 20131228
 */
public class MyAuthorization
{
  private static final String TAG = "MyAuthorization";

  private Context context;
  private String thirdPartyName;

  // 腾讯第三方接口的基础类
  private TencentApplication tencent;
  // 新浪第三方接口的基础类
  private SinaApplication sina;

  private BaseCompleteListener baseCompleteListener;

  public MyAuthorization(Context context)
  {
    this.context = context;
    this.thirdPartyName = "";
    tencent = new TencentApplication(context);
    sina = new SinaApplication(context);
  }

  public MyAuthorization(Context context, String thirdPartyName)
  {
    this.context = context;
    this.thirdPartyName = thirdPartyName;
    tencent = new TencentApplication(context);
    sina = new SinaApplication(context);
  }

  /**
   * 第三方登录
   * 
   * @param partyName 所使用的第三方名字
   * @param activity 登录功能所在的Activity
   */
  public void login(String partyName, Activity activity)
  {
    thirdPartyName = partyName;

    // 腾讯第三方登录
    if(thirdPartyName.equals(TencentConstants.THIRD_PARTY_NAME_TENCENT))
    {
      tencent.setBaseUiCompleteListener(baseCompleteListener);
      tencent.login(activity);
    }
    // 新浪第三方登录
    else if(thirdPartyName.equals(SinaConstants.THIRD_PARTY_NAME_SINA))
    {
      sina.setOnCompleteListener(baseCompleteListener);
      sina.SsoLogin(activity);
    }
  }

  /*
   * 判断是否处于登录状态
   */
  public boolean isLogin()
  {
    // 腾讯
    if(thirdPartyName.equals(TencentConstants.THIRD_PARTY_NAME_TENCENT))
    {
      return tencent.isSessionValid();
    }
    // 新浪
    if(thirdPartyName.equals(SinaConstants.THIRD_PARTY_NAME_SINA))
    {
      return sina.isSessionValid();
    }

    return false;
  }

  /**
   * 账户退出
   * 
   * @param activity 退出功能所在的Activity
   */
  public void logout(Activity activity)
  {
    // 腾讯
    if(thirdPartyName.equals(TencentConstants.THIRD_PARTY_NAME_TENCENT))
    {
      tencent.logout(activity);
    }
    else
    {

    }
  }

  /**
   * 初始化新浪SDK
   * 
   * @param activity 用到第三方接口的Activity
   */
  public void initSinaSDK(Activity activity)
  {
    sina.initWeiboAPI(activity);
  }

  /**
   * 设置监听新浪接口调用情况的监听方法
   * 
   * @param intent
   * @param response IWeiboHandler.Response的实例
   */
  public void setupSinaResponseListener(Intent intent, Response response)
  {
    sina.getIWeiboAPI().responseListener(intent, response);
  }

  /**
   * 初始化调用第三方接口的Activity的onActivityResult方法
   * 
   * @param requestCode
   * @param resultCode
   * @param data
   */
  public void initActivityResultCallback(int requestCode, int resultCode,
      Intent data)
  {
    // 腾讯
    if(thirdPartyName.equals(TencentConstants.THIRD_PARTY_NAME_TENCENT))
    {
      tencent.setOnActivityResult(requestCode, resultCode, data);
    }
    // 新浪
    else if(thirdPartyName.equals(SinaConstants.THIRD_PARTY_NAME_SINA))
    {
      sina.SsoHandlerAuthorizeCallBack(requestCode, resultCode, data);
    }
  }

  /**
   * 分享文字内容到新浪微博
   * 
   * @param activity 用到第三方接口的Activity
   * @param text 文字内容
   */
  public void shareTextMsgToSina(Activity activity, String text)
  {
    sina.reqTextMsg(activity, text);
  }

  /**
   * 分享文字和图片混合的内容到新浪微博
   * 
   * @param activity 用到第三方接口的Activity
   * @param bitmapPath 图片路径
   * @param text 文字内容
   */
  public void shareMultiMsgToSina(Activity activity, String bitmapPath,
      String text)
  {
    sina.reqMultiMsg(activity, bitmapPath, text);
  }

  /**
   * 获取第三方名字
   * 
   * @return 第三方名字
   */
  public String getThirdPartyName()
  {
    return thirdPartyName;
  }

  /**
   * 设置第三方接口的回调函数
   * 
   * @param baseCompleteListener 实现了第三方接口的回调函数的类的实例
   */
  public void setupBaseCompleteListener(
      BaseCompleteListener baseCompleteListener)
  {
    this.baseCompleteListener = baseCompleteListener;
  }

  /**
   * 获取新浪的uid
   * 
   * @return 新浪uid
   */
  public String getSinaUid()
  {
    return sina.getUid();
  }

  /**
   * 获取腾讯的openid
   * 
   * @return 腾讯openid
   */
  public String getTencentOpenId()
  {
    return tencent.getOpenId();
  }

  /**
   * 获取腾讯的token
   * 
   * @return 腾讯token
   */
  public String getTencentToken()
  {
    return tencent.getAccessToken();
  }

  /**
   * 获取腾讯的expires_in
   * 
   * @return 腾讯expires_in
   */
  public long getExpiresIn()
  {
    return tencent.getExpiresIn();
  }
}
