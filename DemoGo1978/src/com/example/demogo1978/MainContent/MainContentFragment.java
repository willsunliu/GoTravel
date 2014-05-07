package com.example.demogo1978.MainContent;


import java.lang.reflect.Field;

import com.example.demogo1978.R;
import com.example.demogo1978.R.id;
import com.example.demogo1978.R.layout;

import android.R.integer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MainContentFragment extends Fragment
{

  public static final int TRAVEL_FRAGMENT = 0;
  public static final int LIFE_FRAGMENT = 1;
  public static final int KNOWLEDGE_FRAGMENT = 2;
  public static final int EQUITMENT_FRAGMENT = 3;
  public static final int NOTIFICATION_FRAGMENT = 4;
  public static final int ARTICLE_FRAGMENT = 5;

  TravelFragment travelFragment;
  LifeFragment lifeFragment;
  KnowledgeFragment knowledgeFragment;
  EquitmentFragment equitmentFragment;
  NotificationFragment notificationFragment;
  ArticleFragment articleFragment;

  /*
   * (non-Javadoc)
   * @see
   * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
   * android.view.ViewGroup, android.os.Bundle)
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View rootView =
        inflater.inflate(R.layout.fragment_main_content, container, false);

    travelFragment = new TravelFragment();
    lifeFragment = new LifeFragment();
    knowledgeFragment = new KnowledgeFragment();
    equitmentFragment = new EquitmentFragment();
    notificationFragment = new NotificationFragment();
    articleFragment = new ArticleFragment();

    getChildFragmentManager().beginTransaction()
        .add(R.id.main_content_container, travelFragment).commit();

    return rootView;
  }

  /*
   * (non-Javadoc)
   * @see android.support.v4.app.Fragment#onDetach()
   */
  @Override
  public void onDetach()
  {
    super.onDetach();

    try
    {
      Field childFragmentManager =
          Fragment.class.getDeclaredField("mChildFragmentManager");
      childFragmentManager.setAccessible(true);
      childFragmentManager.set(this, null);
    }
    catch(NoSuchFieldException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch(IllegalAccessException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void changeChildFragment(int index)
  {
    switch(index)
    {
      case TRAVEL_FRAGMENT:
        getChildFragmentManager().beginTransaction()
            .replace(R.id.main_content_container, travelFragment).commit();
        break;
      case LIFE_FRAGMENT:
        getChildFragmentManager().beginTransaction()
            .replace(R.id.main_content_container, lifeFragment).commit();
        break;
      case KNOWLEDGE_FRAGMENT:
        getChildFragmentManager().beginTransaction()
            .replace(R.id.main_content_container, knowledgeFragment).commit();
        break;
      case EQUITMENT_FRAGMENT:
        getChildFragmentManager().beginTransaction()
            .replace(R.id.main_content_container, equitmentFragment).commit();
        break;
      case NOTIFICATION_FRAGMENT:
        getChildFragmentManager().beginTransaction()
            .replace(R.id.main_content_container, notificationFragment)
            .commit();
        break;
      case ARTICLE_FRAGMENT:

        break;

      default:
        break;
    }
  }

  public void showArticle()
  {
    FragmentTransaction transition =
        getChildFragmentManager().beginTransaction();
    transition.replace(R.id.main_content_container, articleFragment);
    transition.addToBackStack(null);
    transition.commit();
  }

  public boolean popBack()
  {
    return getChildFragmentManager().popBackStackImmediate();
  }
}
