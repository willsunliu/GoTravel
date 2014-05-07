package com.example.demogo1978.view;


import java.util.ArrayList;

import com.example.demogo1978.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;


public class TopBar extends RelativeLayout
{
  private static final String TAG = "TopBar";
  private Context context;

  View centerView;

  LinearLayout rightView;
  int leftIconWidth;
  int rightIconHeight;

  LinearLayout leftView;

  private void init(Context context)
  {
    this.context = context;

    rightView = new LinearLayout(context);
    leftView = new LinearLayout(context);
  }

  public TopBar(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    init(context);
  }

  public TopBar(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init(context);
  }

  public TopBar(Context context)
  {
    super(context);
    init(context);
  }

  public void addCenterLogo(View logo, int width, int height)
  {
    removeView(centerView);
    centerView = logo;
    LayoutParams centerLayoutParams = new LayoutParams(width, height);
    centerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
    addView(centerView, centerLayoutParams);
  }

  public void addMenuView(int location, ArrayList<View> views, int width,
      int height, int marginLeft, int marginTop, int marginRight,
      int marginBottom)
  {
    removeView(rightView);

    LinearLayout container;
    if(RelativeLayout.ALIGN_PARENT_RIGHT == location)
    {
      container = rightView;
    }
    else
    {
      container = leftView;
    }

    LayoutParams layoutParams =
        new LayoutParams((width + marginLeft + marginRight) * views.size(),
            height + marginTop + marginBottom);
    layoutParams.addRule(location);
    layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
    for(View view : views)
    {
      LinearLayout.LayoutParams cellLayoutParams =
          new LinearLayout.LayoutParams(width, height);
      cellLayoutParams.setMargins(marginLeft, marginTop, marginRight,
          marginBottom);
      container.addView(view, cellLayoutParams);
    }
    addView(container, layoutParams);

  }

}
