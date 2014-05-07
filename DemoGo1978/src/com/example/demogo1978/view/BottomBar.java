package com.example.demogo1978.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;

public class BottomBar extends LinearLayout
{
  private static final String TAG = "BottomBar";
  
  private Context context;
  
  private void init(Context context) {
    Button btn1 = new Button(context);
    Button btn2 = new Button(context);
    Button btn3 = new Button(context);
    Button btn4 = new Button(context);
    Button btn5 = new Button(context);
    
    
  }
  
  public BottomBar(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    init(context);
  }
  public BottomBar(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init(context);
  }
  public BottomBar(Context context)
  {
    super(context);
    init(context);
  }

}
