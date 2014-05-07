package com.GoTravel.Go1978.authorization;


import org.json.JSONObject;

import com.GoTravel.Go1978.authorization.SinaApplication.OnSinaCompleteListener;
import com.GoTravel.Go1978.authorization.TencentApplication.BaseApiCompleteListener;
import com.GoTravel.Go1978.authorization.TencentApplication.BaseUiCompleteListener;


/**
 * 构造一个抽象的监听类，用于实现调用第三方接口后的回调函数
 * 
 * @author Wilson 20131228
 */
public abstract class BaseCompleteListener implements BaseUiCompleteListener,
    BaseApiCompleteListener, OnSinaCompleteListener
{
  // 调用新浪微博接口后回调的方法 
  public abstract void doSinaComplete();

  // 调用腾讯接口后回调的方法
  public abstract void doApiComplete(JSONObject response, Object state);

  // 调用腾讯接口后回调的方法
  public abstract void doUiComplete();

}
