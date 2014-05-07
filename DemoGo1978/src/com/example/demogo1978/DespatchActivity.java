package com.example.demogo1978;


import com.example.demogo1978.Tutor.TutorActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.BoringLayout.Metrics;
import android.util.DisplayMetrics;
import android.util.Log;


public class DespatchActivity extends BaseActivity
{

  boolean isFirstIn = false;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);

    ((MyApplication) getApplication()).setLogicalDensity(metrics.density);

    Intent intent = new Intent();
    if(isFirstIn)
    {
      intent.setClass(DespatchActivity.this, TutorActivity.class);
    }
    else
    {
      intent.setClass(DespatchActivity.this, MainActivity.class);
    }
    startActivity(intent);
    this.finish();
  }
}
