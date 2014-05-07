package com.example.demogo1978.Tutor;


import java.util.ArrayList;

import com.example.demogo1978.BaseActivity;
import com.example.demogo1978.BaseFragmentPagerAdapter;
import com.example.demogo1978.MainActivity;
import com.example.demogo1978.MyData;
import com.example.demogo1978.R;
import com.example.demogo1978.R.drawable;
import com.example.demogo1978.R.id;
import com.example.demogo1978.R.layout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class TutorActivity extends BaseActivity implements OnPageChangeListener
{

  private static final String TAG = "TutorActivity";
  private static final int COUNT = 5;

  ArrayList<Fragment> tutorList;
  ViewPager viewPager;

  /*
   * (non-Javadoc)
   * @see com.example.demogo1978.BaseActivity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tutor);

    tutorList = new ArrayList<Fragment>();
    for(int i = 0; i < COUNT; i++)
    {
      TutorFragment fragment = new TutorFragment();
      fragment.setTutorImage(getResources()
          .getDrawable(MyData.tutorPictures[i]));
      tutorList.add(fragment);
    }

    BaseFragmentPagerAdapter adapter =
        new BaseFragmentPagerAdapter(getSupportFragmentManager(), tutorList);

    viewPager = (ViewPager) findViewById(R.id.tutor_pager);
    viewPager.setAdapter(adapter);
    viewPager.setOnPageChangeListener(this);
  }

  @Override
  public void onPageScrollStateChanged(int state)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onPageScrolled(int arg0, float arg1, int arg2)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onPageSelected(int position)
  {
    Log.i(TAG, "" + position);
    for(int i = 0; i < COUNT; i++)
    {
      if(i == position)
      {
        Log.i(TAG, "" + position);
        ((ImageView) findViewById(MyData.tutorBalls[i]))
            .setImageDrawable(getResources().getDrawable(
                R.drawable.current_ball));
      }
      else
      {
        ((ImageView) findViewById(MyData.tutorBalls[i]))
            .setImageDrawable(getResources().getDrawable(R.drawable.other_ball));
      }
    }
    if(4 == position)
    {
      ((Fragment) tutorList.get(position)).getView()
          .findViewById(R.id.tutor_finish_btn).setVisibility(View.VISIBLE);
    } else {
      ((Fragment) tutorList.get(position)).getView()
      .findViewById(R.id.tutor_finish_btn).setVisibility(View.INVISIBLE);
    }
  }
  
  private void begin(View view) {
    Intent intent = new Intent(TutorActivity.this, MainActivity.class);
    startActivity(intent);
    this.finish();
  }

  private int getTutorImageDrawable(int index)
  {

    int resId = -1;

    switch(index)
    {
      case 0:
        resId = R.drawable.tutor_1;
        break;
      case 1:
        resId = R.drawable.tutor_2;
        break;
      case 2:
        resId = R.drawable.tutor_3;
        break;
      case 3:
        resId = R.drawable.tutor_4;
        break;
      case 4:
        resId = R.drawable.tutor_5;
        break;

      default:
        resId = -1;
        break;
    }

    return resId;
  }

}
