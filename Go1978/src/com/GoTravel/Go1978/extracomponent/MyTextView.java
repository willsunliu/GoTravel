package com.GoTravel.Go1978.extracomponent;



import com.GoTravel.Go1978.R;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class MyTextView extends View
{
  private Context context;

  private String text;
  private String hint;
  private int textSize;

  public MyTextView(Context context)
  {
    super(context);
    init(context);
  }

  public MyTextView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init(context);
  }

  public MyTextView(Context context, AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context)
  {
    this.context = context;
    hint = context.getString(R.string.hint);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    // TODO Auto-generated method stub
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    // TODO Auto-generated method stub
    super.onDraw(canvas);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    switch(event.getAction())
    {
      case MotionEvent.ACTION_DOWN:
        
        break;
      case MotionEvent.ACTION_UP:
        break;
      case MotionEvent.ACTION_MOVE:
        break;
      default:
        break;
    }
    return super.onTouchEvent(event);
  }
  
}
