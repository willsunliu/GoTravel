package com.GoTravel.Go1978.constants;


/**
 * 使用腾讯第三方接口需要用到的静态常量
 * 
 * @author Wilson 20131228
 */
public class TencentConstants
{
  // 第三方名称
  public static final String THIRD_PARTY_NAME_TENCENT = "tencent";
  // 123GO使用腾讯登录的url
  public static final String QQ_LOGIN_URL =
      "http://www.123go.net.cn/plugin.php?id=javasyn&mod=qqlogin";

  // Tencent 分配给第三方应用的 appid
  public static final String APP_ID = "100564474";

  // 应用需要获得哪些接口的权限，由","分隔
  public static final String SCOPE_ALL = "all";
  public static final String SCOPE_GET_SIMPLE_USERINFO_STRING =
      "get_simple_userinfo";

  // 配置文件的key值
  public static final String PREFS_TENCENT_FILE = "TencentPrefs";
  public static final String PREFS_KEY_OPENID = "openid";
  public static final String PREFS_KEY_ACCESS_TOKEN = "access_token";
  public static final String PREFS_KEY_EXPIRES_IN = "expires_in";
  public static final String PREFS_KEY_NICK_NAME = "nickname";
}
