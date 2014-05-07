package com.GoTravel.Go1978.constants;


/**
 * 使用新浪第三方接口需要用到的静态常量
 * 
 * @author Wilson 20131228
 */
public class SinaConstants
{
  // 123GO通过新浪登录的url
  public static final String WEIBO_LOGIN_URL =
      "http://www.123go.net.cn/plugin.php?id=javasyn&mod=weibologin";
  // 新浪app key
  public static final String APP_KEY = "871746258";
  // 新浪登录成功后的重定向url
  public static final String REDIRECT_URL =
      "http://www.123go.net.cn/weibo/callback.php";
  // 登录时需要申请的权限
  public static final String SCOPE_STRING =
      "email,direct_message_read,direct_message_write,"
          + "friendships_groups_read,friendships_group_write,statuses_to_me_read,"
          + "follow_app_official_microblog," + "invitation_write";

  // 第三方名称
  public static final String THIRD_PARTY_NAME_SINA = "sina";
  // 新浪配置文件的文件名
  public static final String PREFS_SINA_FILE = "SinaPrefs";
  // access_token
  public static final String ACCESS_TOKEN = "access_token";
  // uid
  public static final String UID = "uid";
  // expires_in
  public static final String EXPIRES_IN = "expires_in";
}
