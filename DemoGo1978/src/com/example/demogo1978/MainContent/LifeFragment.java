package com.example.demogo1978.MainContent;

import com.example.demogo1978.R;
import com.example.demogo1978.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LifeFragment extends Fragment
{

  /* (non-Javadoc)
   * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View rootView = inflater.inflate(R.layout.fragment_life, container, false);
    return rootView;
  }

}
