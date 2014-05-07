package com.example.demogo1978.MainContent;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.example.demogo1978.OnVerticalScrollActionListener;
import com.example.demogo1978.R;

import android.R.integer;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationSet;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;


public class ArticleListView extends ListView
{

  private static final String TAG = "ArticleListView";

  private Context context;

  private int extraHeight;
  private int collapsedHeight = 0;

  private boolean mShouldRemoveObserver = false;
  private int[] mTranslate;
  private List<View> mViewsToDraw = new ArrayList<View>();

  private OnVerticalScrollActionListener myOnVerticalScrollActionListener;
  private float pointX;
  private float pointY;
  private float lastX;
  private float lastY;
  private float lastDeltaY = 0;
  private int touchSlop;

  private static final int NO_SCROLL = 0;
  private static final int SCROLL_UP = 1;
  private static final int SCROLL_DOWN = 2;
  private int scrollState = NO_SCROLL;

  public ArticleListView(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    init(context);
  }

  public ArticleListView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init(context);
  }

  public ArticleListView(Context context)
  {
    super(context);
    init(context);
  }

  private void init(Context context)
  {
    this.context = context;
    setOnItemClickListener(mItemClickListener);

    touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
  }

  private AdapterView.OnItemClickListener mItemClickListener =
      new AdapterView.OnItemClickListener()
      {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
        {
          ArticleListItem viewObj =
              (ArticleListItem) getItemAtPosition(getPositionForView(view));
          Log.i(TAG, view.findViewById(R.id.item_linear_layout).getHeight()
              + "");
          if(viewObj.isExpanded())
          {

            collapseView(view);
          }
          else
          {
            if(collapsedHeight == 0)
            {
              collapsedHeight = view.getHeight();
            }
            Log.i(TAG, "collapsedHeight=" + collapsedHeight);
            expandView(view);
          }
        }
      };

  private int[] getTopAndBottomTranslations(int top, int bottom, int yDelta,
      boolean isExpanding)
  {
    Log.i(TAG, "top=" + top + "; bottom=" + bottom + "; yDelta=" + yDelta);
    int yTranslateTop = 0;
    int yTranslateBottom = yDelta;

    int height = bottom - top;

    if(isExpanding)
    {
      boolean isOverTop = top < 0;
      boolean isBellowBottom = (top + height + yDelta) > getHeight();
      if(isOverTop)
      {
        yTranslateTop = top;
        yTranslateBottom = yDelta - yTranslateTop;
      }
      else if(isBellowBottom)
      {
        int deltaBelow = top + height + yDelta - getHeight();
        yTranslateTop = top - deltaBelow < 0 ? top : deltaBelow;
        yTranslateBottom = yDelta - yTranslateTop;
      }

      extraHeight = yDelta;
    }
    else
    {
      // int offset = computeVerticalScrollOffset();
      // int range = computeVerticalScrollRange();
      // int extent = computeVerticalScrollExtent();
      // int leftoverExtent = range - offset - extent;
      //
      // boolean isCollapsingBelowBottom = (yTranslateBottom > leftoverExtent);
      // boolean isCellCompletelyDisappearing = bottom - yTranslateBottom < 0;
      //
      // if(isCollapsingBelowBottom)
      // {
      // yTranslateTop = yTranslateBottom - leftoverExtent;
      // yTranslateBottom = yDelta - yTranslateTop;
      // }
      // else if(isCellCompletelyDisappearing)
      // {
      // yTranslateBottom = bottom;
      // yTranslateTop = yDelta - yTranslateBottom;
      // }
    }

    return new int[] {yTranslateTop, yTranslateBottom};
  }

  private void expandView(final View view)
  {
    final ArticleListItem viewObj =
        (ArticleListItem) getItemAtPosition(getPositionForView(view));

    /* Store the original top and bottom bounds of all the cells. */
    final int oldTop = view.getTop();
    final int oldBottom = view.getBottom();

    final HashMap<View, int[]> oldCoordinates = new HashMap<View, int[]>();

    int childCount = getChildCount();
    for(int i = 0; i < childCount; i++)
    {
      View v = getChildAt(i);
      // v.setHasTransientState(true);
      oldCoordinates.put(v, new int[] {v.getTop(), v.getBottom()});
    }

    /* Update the layout so the extra content becomes visible. */
    final View expandingLayout = view.findViewById(R.id.expanding_layout);
    expandingLayout.setVisibility(View.VISIBLE);

    /*
     * Add an onPreDraw Listener to the listview. onPreDraw will get invokde
     * after onLayout and onMeasure have run bur before anything has been drawn.
     * This means that the final post layout properties for all the items have
     * already determined, but still have not been rendered onto the screen.
     */
    final ViewTreeObserver observer = getViewTreeObserver();
    observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {

      @Override
      public boolean onPreDraw()
      {
        if(!mShouldRemoveObserver)
        {
          mShouldRemoveObserver = true;

          /*
           * Calculate what parameters should be for setSelectionFromTop. The
           * ListView must be offset in a way, such that after the animation
           * takes place, all the cells that remain visible are rendered
           * completely by the ListView.
           */
          int newTop = view.getTop();
          int newBottom = view.getBottom();

          int newHeight = newBottom - newTop;
          int oldHeight = oldBottom - oldTop;
          int delta = newHeight - oldHeight;

          mTranslate =
              getTopAndBottomTranslations(oldTop, oldBottom, delta, true);

          int currentTop = view.getTop();
          int futureTop = oldTop - mTranslate[0];

          int firstChildStartTop = getChildAt(0).getTop();
          int firstVisiblePosition = getFirstVisiblePosition();
          int deltaTop = currentTop - futureTop;

          int i;
          int childCount = getChildCount();
          for(i = 0; i < childCount; i++)
          {
            View v = getChildAt(i);
            int height = v.getBottom() - Math.max(0, v.getTop());
            if(deltaTop - height > 0)
            {
              firstVisiblePosition++;
              deltaTop -= height;
            }
            else
            {
              break;
            }
          }

          if(i > 0)
          {
            firstChildStartTop = 0;
          }

          setSelectionFromTop(firstVisiblePosition, firstChildStartTop
              - deltaTop);

          /*
           * Request another layout to update the layout parameters of the
           * cells.
           */
          requestLayout();

          /*
           * Return false such that the ListView does not redraw its contents on
           * this layout but only updates all the parameters associated with its
           * children.
           */
          return false;
        }

        /*
         * Remove the predraw listener so this method does not keep getting
         * called.
         */
        mShouldRemoveObserver = false;
        observer.removeOnPreDrawListener(this);

        int yTranslateTop = mTranslate[0];
        int yTranslateBottom = mTranslate[1];

        ArrayList<Animator> animations = new ArrayList<Animator>();

        int index = indexOfChild(view);

        /*
         * Loop through all the views that were on the screen before the cell
         * was expanded. Some cells will still be children of the ListView while
         * others will not. The cells that remain children of the ListView
         * simply have their bounds animated appropriately. The cells that are
         * no longer children of the ListView also have their bounds animated,
         * but must also be added to a list views which wil be drawn in
         * dispatchDraw.
         */
        for(View v : oldCoordinates.keySet())
        {
          int[] old = oldCoordinates.get(v);
          v.setTop(old[0]);
          v.setBottom(old[1]);
          if(v.getParent() == null)
          {
            mViewsToDraw.add(v);
            int delta = old[0] < oldTop ? -yTranslateTop : yTranslateBottom;
            Log.i(TAG, "delta1=" + delta);
            animations.add(getAnimation(v, delta, delta));
          }
          else
          {
            int i = indexOfChild(v);
            if(v != view)
            {
              int delta = i > index ? yTranslateBottom : -yTranslateTop;
              Log.i(TAG, "delta2=" + delta);
              animations.add(getAnimation(v, delta, delta));
            }
          }
        }

        /* Adds animation for expanding the cell that was clicked. */
        animations.add(getAnimation(view, -yTranslateTop, yTranslateBottom));

        /* Adds an animation for fading in the extra content. */
        animations.add(ObjectAnimator.ofFloat(
            view.findViewById(R.id.expanding_layout), View.ALPHA, 0, 1));

        /* Disable the ListView for the duration of the animation. */
        setEnabled(false);
        setClickable(false);

        /* Play all the animations created above together at the same time. */
        AnimatorSet s = new AnimatorSet();
        s.playTogether(animations);
        s.addListener(new AnimatorListenerAdapter()
        {

          /*
           * (non-Javadoc)
           * @see
           * android.animation.AnimatorListenerAdapter#onAnimationEnd(android
           * .animation.Animator)
           */
          @Override
          public void onAnimationEnd(Animator animation)
          {
            viewObj.setExpanded(true);
            setEnabled(true);
            setClickable(true);
            mViewsToDraw.clear();
          }
        });
        s.start();
        return true;
      }
    });
  }

  /*
   * (non-Javadoc)
   * @see android.widget.ListView#dispatchDraw(android.graphics.Canvas)
   */
  @Override
  protected void dispatchDraw(Canvas canvas)
  {
    super.dispatchDraw(canvas);

    if(mViewsToDraw.size() == 0)
    {
      return;
    }

    for(View v : mViewsToDraw)
    {
      canvas.translate(0, v.getTop());
      v.draw(canvas);
      canvas.translate(0, -v.getTop());
    }
  }

  private void collapseView(final View view)
  {
    final ArticleListItem viewObj =
        (ArticleListItem) getItemAtPosition(getPositionForView(view));

    // Store the original top and bottom bounds of all the cells.
    final int oldTop = view.getTop();
    final int oldBottom = view.getBottom();

    final HashMap<View, int[]> oldCoordinates = new HashMap<View, int[]>();

    int childCount = getChildCount();
    for(int i = 0; i < childCount; i++)
    {
      Log.i(TAG, "i=" + i);
      View v = getChildAt(i);
      oldCoordinates.put(v, new int[] {v.getTop(), v.getBottom()});
    }

    // Update the layout so the extra content becomes invisible.
    AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, collapsedHeight);
    view.setLayoutParams(new AbsListView.LayoutParams(
        AbsListView.LayoutParams.MATCH_PARENT, collapsedHeight));
    // int collapsedHeight =
    // view.findViewById(R.id.item_linear_layout).getHeight();
    // collapsedHeight =
    // (int) (collapsedHeight
    // * context.getResources().getDisplayMetrics().density + 0.5f);
    // Log.i(TAG, "collapsedHeight=" + collapsedHeight + "; logicalDensity="
    // + logicalDensity);
    // view.setLayoutParams(new AbsListView.LayoutParams(
    // AbsListView.LayoutParams.MATCH_PARENT, view.getBottom()));
    // final View expandingLayout = view.findViewById(R.id.expanding_layout);
    // expandingLayout.setVisibility(View.GONE);
    // final View expandingLayout = view.findViewById(R.id.expanding_layout);
    // expandingLayout.setVisibility(View.VISIBLE);

    // Add an onPreDraw listener
    final ViewTreeObserver observer = getViewTreeObserver();
    observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {

      @Override
      public boolean onPreDraw()
      {
        if(!mShouldRemoveObserver)
        {
          /*
           * Same as for expandingView, the parameters for setSelectionFromTop
           * must be determined such that the necessary cells of the ListView
           * are rendered and added to it.
           */
          mShouldRemoveObserver = true;

          int newTop = view.getTop();
          int newBottom = view.getBottom();

          int newHeight = newBottom - newTop;
          int oldHeight = oldBottom - oldTop;
          int deltaHeight = oldHeight - newHeight;

          mTranslate =
              getTopAndBottomTranslations(oldTop, oldBottom, extraHeight, false);
          // getTopAndBottomTranslations(oldTop, oldBottom, deltaHeight, false);

          int currentTop = view.getTop();
          int futureTop = oldTop + mTranslate[0];

          int firstChildStartTop = getChildAt(0).getTop();
          int firstVisiblePosition = getFirstVisiblePosition();
          int deltaTop = currentTop - futureTop;
          Log.i(TAG, "deltaTop="+deltaTop);
          int i;
          int childCount = getChildCount();
          for(i = 0; i < childCount; i++)
          {
            View v = getChildAt(i);
            int height = v.getBottom() - Math.max(0, v.getTop());
            if(deltaTop - height > 0)
            {
              firstVisiblePosition++;
              deltaTop -= height;
            }
            else
            {
              break;
            }
          }

          if(i > 0)
          {
            firstChildStartTop = 0;
          }

          setSelectionFromTop(firstVisiblePosition, firstChildStartTop
              - deltaTop);

          requestLayout();

          return false;
        }

        mShouldRemoveObserver = false;
        observer.removeOnPreDrawListener(this);

        int yTranslateTop = mTranslate[0];
        int yTranslateBottom = mTranslate[1];

        int index = indexOfChild(view);
        int childCount = getChildCount();
        for(int i = 0; i < childCount; i++)
        {
          View v = getChildAt(i);
          int[] old = oldCoordinates.get(v);
          if(old != null)
          {
            /*
             * If the cell was present in the ListView before the collapse and
             * after the collapse then the bounds are reset to their old values.
             */
            Log.i(TAG, old[0] + "; " + old[1]);
            v.setTop(old[0]);
            v.setBottom(old[1]);
          }
          else
          {
            /*
             * If the cell is present in the ListView after the collapse but not
             * before the collapse then the bounds are calculated using the
             * bottom
             * and top translation of the collapsing cell.
             */
            int delta = i > index ? yTranslateBottom : -yTranslateTop;
            v.setTop(v.getTop() + delta);
            v.setBottom(v.getBottom() + delta);
          }
        }

        final View expandingLayout = view.findViewById(R.id.expanding_layout);

        // Animates all the cells present on the screen after the collapse.
        ArrayList<Animator> animations = new ArrayList<Animator>();
        Log.i(TAG, "childCount=" + childCount);
        for(int i = 0; i < childCount; i++)
        {
          View v = getChildAt(i);
          if(v != null)
          {
            float diff = i > index ? -yTranslateBottom : yTranslateTop;
            animations.add(getAnimation(v, diff, diff));
          }
        }
        // for (View v: oldCoordinates.keySet()) {
        // int[] old = oldCoordinates.get(v);
        // v.setTop(old[0]);
        // v.setBottom(old[1]);
        // if(v.getParent() == null)
        // {
        // mViewsToDraw.add(v);
        // float diff = old[0] > oldTop ? -yTranslateBottom : yTranslateTop;
        // Log.i(TAG, "diff1="+diff);
        // animations.add(getAnimation(v, diff, diff));
        // } else {
        // int i = indexOfChild(v);
        // if(v != view)
        // {
        // float diff = i > index ? -yTranslateBottom : yTranslateTop;
        // Log.i(TAG, "i="+i+"; diff2="+diff);
        // animations.add(getAnimation(v, diff, diff));
        // }
        // }
        // }

        // Adds animation for collapsing the cell that was clicked.
        animations.add(getAnimation(view, yTranslateTop, -yTranslateBottom));

        // Adds an animation for fading out the extra content.
        animations.add(ObjectAnimator
            .ofFloat(expandingLayout, View.ALPHA, 1, 0));

        // Disabled the ListView for the duration of the animation.
        setEnabled(false);
        setClickable(false);

        // Play all the animations created above together at the same time.
        AnimatorSet s = new AnimatorSet();
        s.playTogether(animations);
        s.addListener(new AnimatorListenerAdapter()
        {

          /*
           * (non-Javadoc)
           * @see
           * android.animation.AnimatorListenerAdapter#onAnimationEnd(android
           * .animation.Animator)
           */
          @Override
          public void onAnimationEnd(Animator animation)
          {
            expandingLayout.setVisibility(View.GONE);
            view.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT));
            viewObj.setExpanded(false);
            setEnabled(true);
            setClickable(true);
            /*
             * Note that alpha must be set back to 1 in case this view is reused
             * by a cell that was expanded, but not yet collapsed, so its state
             * should presist in an expanded state with the extra content
             * visible.
             */
            expandingLayout.setAlpha(1);
          }
        });
        s.start();

        return true;
      }
    });
  }

  private Animator getAnimation(final View view, float translateTop,
      float translateBottom)
  {
    int top = view.getTop();
    int bottom = view.getBottom();

    int endTop = (int) (top + translateTop);
    int endBottom = (int) (bottom + translateBottom);

    PropertyValuesHolder translationTop =
        PropertyValuesHolder.ofInt("top", top, endTop);
    Log.i(TAG, "top=" + top + "; endTop=" + endTop);
    PropertyValuesHolder translationBottom =
        PropertyValuesHolder.ofInt("bottom", bottom, endBottom);
    Log.i(TAG, "bottom=" + bottom + "; endBottom=" + endBottom);

    return ObjectAnimator.ofPropertyValuesHolder(view, translationTop,
        translationBottom);
  }

  /*
   * (non-Javadoc)
   * @see android.widget.AbsListView#onTouchEvent(android.view.MotionEvent)
   */
  @Override
  public boolean onTouchEvent(MotionEvent ev)
  {
    switch(ev.getAction())
    {
      case MotionEvent.ACTION_DOWN:
        pointX = ev.getRawX();
        pointY = ev.getRawY();
        lastX = pointX;
        lastY = pointY;
        lastDeltaY = 0;
        break;
      case MotionEvent.ACTION_UP:
        myOnVerticalScrollActionListener.onVerticalScrollFinished();
        scrollState = NO_SCROLL;
        break;
      case MotionEvent.ACTION_MOVE:
        int vector = (int) Math.ceil(ev.getRawY() - lastY);
        if(Math.abs(vector) < touchSlop)
        {
          return false;
        }
        float deltaY = 0.f;

        switch(scrollState)
        {
          case NO_SCROLL:
            if(vector > 0)
            {
              scrollState = SCROLL_DOWN;
            }
            else
            {
              scrollState = SCROLL_UP;
            }
            break;
          case SCROLL_UP:
            if(vector > 0)
            {
              pointX = lastX;
              pointY = lastY;
              lastDeltaY = 0;
              scrollState = SCROLL_DOWN;
            }
            break;
          case SCROLL_DOWN:
            if(vector < 0)
            {
              pointX = lastX;
              pointY = lastY;
              lastDeltaY = 0;
              scrollState = SCROLL_UP;
            }
            break;
        }
        deltaY = ev.getRawY() - pointY;
        myOnVerticalScrollActionListener.onVerticalScrolling(deltaY
            - lastDeltaY);
        lastX = ev.getRawX();
        lastY = ev.getRawY();
        lastDeltaY = deltaY;
        break;
    }
    return super.onTouchEvent(ev);
  }

  public void setOnVerticalScrollActionListener(
      OnVerticalScrollActionListener listener)
  {
    myOnVerticalScrollActionListener = listener;
  }

}
