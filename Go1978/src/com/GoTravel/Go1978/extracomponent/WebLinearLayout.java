package com.GoTravel.Go1978.extracomponent;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;


public class WebLinearLayout extends LinearLayout
{

  public static final int KEYBOARD_STATE_INIT = 0;
  public static final int KEYBOARD_STATE_SHOW = 1;
  public static final int KEYBOARD_STATE_HIDE = 2;

  private boolean hasInit = false;
  private boolean hasKeyboard = false;
  private int bottomLine;

  private OnKbdStateChangedListener onKbdStateChangedListener;

  public WebLinearLayout(Context context)
  {
    super(context);
    // TODO Auto-generated constructor stub
  }

  public WebLinearLayout(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    // TODO Auto-generated constructor stub
  }

  /*
   * (non-Javadoc)
   * @see android.widget.LinearLayout#onLayout(boolean, int, int, int, int)
   */
  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b)
  {
    super.onLayout(changed, l, t, r, b);
    if(!hasInit)
    {
      hasInit = true;
      bottomLine = b;
      if(onKbdStateChangedListener != null)
      {
        onKbdStateChangedListener.onKbdStateChanged(KEYBOARD_STATE_INIT);
      }
    }
    else
    {
      bottomLine = bottomLine < b ? b : bottomLine;
    }

    if(hasInit && bottomLine > b)
    {
      hasKeyboard = true;
      if (onKbdStateChangedListener != null) {
        onKbdStateChangedListener.onKbdStateChanged(KEYBOARD_STATE_SHOW);
      }
    }
    
    if (hasInit && hasKeyboard && bottomLine == b) {
      hasKeyboard = false;
      if (onKbdStateChangedListener != null) {
        onKbdStateChangedListener.onKbdStateChanged(KEYBOARD_STATE_HIDE);
      }
    }
  }

  public void setOnKbdStateChangedListener(
      OnKbdStateChangedListener onKbdStateChangedListener)
  {
    this.onKbdStateChangedListener = onKbdStateChangedListener;
  }

  public interface OnKbdStateChangedListener
  {
    public void onKbdStateChanged(int state);
  }
}
