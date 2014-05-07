package com.GoTravel.Go1978;


import com.GoTravel.Go1978.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;


/**
 * 启动应用时弹出SplashActivity显示一个logo画面
 * 
 * @author Wilson 20131228
 */
public class SplashActivity extends BaseActivity
{

  // 延时时间
  private final int SPLASH_DISPLAY_TIME = 2000;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_splash);

    // 延时SPLASH_DISPALY_TIME后，关闭此Activity并启动WebActivity
    new Handler().postDelayed(new Runnable()
    {

      public void run()
      {
        Intent intent = new Intent(SplashActivity.this, WebActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
      }
    }, SPLASH_DISPLAY_TIME);
  }

}
