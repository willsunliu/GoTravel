package com.GoTravel.Go1978;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;


/**
 * ActionBarActivity的子类，实现锁定屏幕垂直显示，、其他
 * Activity继承BaaseActivity就可以实现锁定屏幕垂直显示的功能。
 * 
 * @author Wilson 20131223
 */
public class BaseActivity extends Activity
{
  int RESULT_OK_QA = 1;
  int RESULT_OK_ALBUM = 2;
  
  int sdk;
  int screenW, screenH;

  @SuppressWarnings("deprecation")
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    sdk = android.os.Build.VERSION.SDK_INT;
    if(sdk >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2)
    {
      Point size = new Point();
      getWindowManager().getDefaultDisplay().getSize(size);
      screenW = size.x;
      screenH = size.y;
    } else {
      screenW = getWindowManager().getDefaultDisplay().getWidth();
      screenH = getWindowManager().getDefaultDisplay().getHeight();
    }
    // Set orientation to portrait
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    // Hide title bar
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    
    super.onCreate(savedInstanceState);
  }

}
