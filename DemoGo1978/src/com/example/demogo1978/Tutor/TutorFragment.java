package com.example.demogo1978.Tutor;


import com.example.demogo1978.MainActivity;
import com.example.demogo1978.R;
import com.example.demogo1978.R.id;
import com.example.demogo1978.R.layout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class TutorFragment extends Fragment
{

  Drawable drawable;
  ImageView imageView;
  Button button;

  public TutorFragment()
  {

  }

  /*
   * (non-Javadoc)
   * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
   * android.view.ViewGroup, android.os.Bundle)
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View rootView = inflater.inflate(R.layout.fragment_tutor, container, false);
    imageView = (ImageView) rootView.findViewById(R.id.tutor_image);
    if(drawable != null)
    {
      imageView.setImageDrawable(drawable);
    }
    button = (Button) rootView.findViewById(R.id.tutor_finish_btn);
    button.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        Intent intent =
            new Intent(TutorFragment.this.getActivity(), MainActivity.class);
        TutorFragment.this.startActivity(intent);
        TutorFragment.this.getActivity().finish();
      }
    });
    return rootView;
  }

  public void setTutorImage(Drawable drawable)
  {
    this.drawable = drawable;
  }

}
