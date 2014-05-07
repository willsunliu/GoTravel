package com.GoTravel.Go1978.extracomponent;


import java.util.ArrayList;

import com.GoTravel.Go1978.R;
import com.GoTravel.Go1978.extracomponent.ZoomImageView.PhotoChangedListener;
import com.GoTravel.Go1978.log.MyLog;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;


public class MyFrameLayout extends FrameLayout
{
  private static final String TAG = "MyFrameLayout";

  private Context context;

  private float downX, downY;

  private PhotoWrapper photo;
  private ZoomImageView zoomImageView;
  private ArrayList<StickerView> stickerList = new ArrayList<StickerView>();
  int selectedIndex = -1;

  private float zoomRatio = 1.0f;

  public MyFrameLayout(Context context, AttributeSet attrs)
  {
    super(context, attrs);

    this.context = context;
  }

  /*
   * (non-Javadoc)
   * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
   */
  @Override
  public boolean dispatchTouchEvent(MotionEvent ev)
  {
    switch(ev.getActionMasked())
    {
      case MotionEvent.ACTION_DOWN:
        downX = ev.getX();
        downY = ev.getY();
        break;

      case MotionEvent.ACTION_POINTER_DOWN:
        if(selectedIndex != -1)
        {
          stickerList.get(selectedIndex).setSelected(false);
        }
        break;

      case MotionEvent.ACTION_POINTER_UP:
        break;
      case MotionEvent.ACTION_UP:
        if(Math.abs(ev.getX() - downX) < 16 && Math.abs(ev.getY() - downY) < 16)
        {
          for(int i = 0; i < stickerList.size(); i++)
          {
            StickerView sticker = stickerList.get(i);
            Rect rect = new Rect();
            sticker.getHitRect(rect);
            if(rect.contains((int) ev.getX(), (int) ev.getY()))
            {
              sticker.setSelected(true);
            }
            else
            {
              sticker.setSelected(false);
              selectedIndex = -1;
            }
          }
        }
        break;
      case MotionEvent.ACTION_MOVE:

        break;

      default:
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  public void addZoomImageView(ZoomImageView zoomImageView)
  {
    FrameLayout.LayoutParams fp =
        new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT);
    zoomImageView.setPhotoZoomListener(new PhotoChangedListener()
    {

      public void photoZoom(float photoRatio, float pixelRatioW,
          float pixelRatioH)
      {
        for(StickerView sticker : stickerList)
        {
          sticker.setZoomRatio(photoRatio, pixelRatioW, pixelRatioH);
        }
      }

      public void photoTranslate(float moveX, float moveY)
      {
        for(StickerView sticker : stickerList)
        {
          sticker.setTranslation(moveX, moveY);
        }
      }
    });
    this.addView(zoomImageView, fp);
    this.zoomImageView = zoomImageView;
  }

  public void addSticker(int type, float photoScaling)
  {
    zoomRatio = zoomImageView.getTotalRatio();

    StickerView sticker = new StickerView(context, photoScaling, zoomRatio);
    FrameLayout.LayoutParams fp =
        new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT);
    this.addView(sticker, fp);
    stickerList.add(sticker);
  }

  /**
   * @return the photo
   */
  public PhotoWrapper getPhoto()
  {
    return photo;
  }

  /**
   * @param photo the photo to set
   */
  public void setPhoto(PhotoWrapper photo)
  {
    this.photo = photo;
    if(photo != null)
    {
      // zoomImageView.setImageBitmap(photo.getScaledBitmap());
    }
  }
}
