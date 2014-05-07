package com.example.demogo1978;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MyApplication extends Application
{
  SharedPreferences prefs;
  
  /* (non-Javadoc)
   * @see android.app.Application#onCreate()
   */
  @Override
  public void onCreate()
  {
    prefs = getSharedPreferences("init_prefs", MODE_PRIVATE);
    super.onCreate();
  }
  
  public void setLogicalDensity(float logicalDensity) {
    Editor editor = prefs.edit();
    editor.putFloat("logical_density", logicalDensity);
    editor.apply();
  }
  
  public float getLogicalDensity() {
    return prefs.getFloat("logical_density", 1.f);
  }
}
