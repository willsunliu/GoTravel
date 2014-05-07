package com.example.demogo1978.MainContent;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


public class ExpandingLayout extends RelativeLayout
{

  private OnSizeChangedListener mSizeChangedListener;
  private int mExpandedHeight = -1;

  public ExpandingLayout(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    // TODO Auto-generated constructor stub
  }

  public ExpandingLayout(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    // TODO Auto-generated constructor stub
  }

  public ExpandingLayout(Context context)
  {
    super(context);
    // TODO Auto-generated constructor stub
  }

  /*
   * (non-Javadoc)
   * @see android.widget.RelativeLayout#onMeasure(int, int)
   */
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    if(mExpandedHeight > 0)
    {
      heightMeasureSpec =
          MeasureSpec.makeMeasureSpec(mExpandedHeight, MeasureSpec.AT_MOST);
    }
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  /*
   * (non-Javadoc)
   * @see android.view.View#onSizeChanged(int, int, int, int)
   */
  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh)
  {
    mExpandedHeight = h;
    // Notifies the list data object corresponding to this layout that size has
    // changed.
    mSizeChangedListener.onSizeChanged(h);
  }

  /**
   * @return the mExpandedHeight
   */
  public int getExpandedHeight()
  {
    return mExpandedHeight;
  }

  /**
   * @param mExpandedHeight the mExpandedHeight to set
   */
  public void setExpandedHeight(int mExpandedHeight)
  {
    this.mExpandedHeight = mExpandedHeight;
  }

  public void setSizeChangedListener(OnSizeChangedListener listener)
  {
    mSizeChangedListener = listener;
  }
}
