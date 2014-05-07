package com.example.demogo1978.view;


import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;


public class ViewPagerScrollBar extends LinearLayout
{

  Context context;
  Paint paint;
  Rect rect;
  
  private void init(Context context) {
    this.context = context;
    setOrientation(LinearLayout.HORIZONTAL);
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setTextSize(30);
    paint.setColor(Color.BLACK);
  }
  
  public ViewPagerScrollBar(Context context)
  {
    super(context);
    init(context);
  }

  public ViewPagerScrollBar(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init(context);
  }

  public ViewPagerScrollBar(Context context, AttributeSet attrs,
      int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  /* (non-Javadoc)
   * @see android.view.View#onDraw(android.graphics.Canvas)
   */
  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    canvas.drawRect(rect, paint);
  }
  
  public void setRect(Rect rect) {
    this.rect = rect;
    invalidate();
  }
}
