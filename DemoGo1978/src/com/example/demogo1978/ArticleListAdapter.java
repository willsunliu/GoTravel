package com.example.demogo1978;


import java.util.List;

import com.example.demogo1978.MainContent.ArticleListItem;
import com.example.demogo1978.MainContent.ExpandingLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;


public class ArticleListAdapter extends BaseAdapter
{

  private Activity activity;
  List<ArticleListItem> list;

  public ArticleListAdapter(Activity activity, List<ArticleListItem> list)
  {
    this.activity = activity;
    this.list = list;
  }

  @Override
  public int getCount()
  {
    // TODO Auto-generated method stub
    return list.size();
  }

  @Override
  public Object getItem(int position)
  {
    // TODO Auto-generated method stub
    return list.get(position);
  }

  @Override
  public long getItemId(int position)
  {
    // TODO Auto-generated method stub
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    ArticleListItem data = list.get(position);

    final ViewHolder viewHolder;

    if(convertView == null)
    {
      viewHolder = new ViewHolder();
      convertView =
          LayoutInflater.from(activity).inflate(R.layout.article_list_item,
              parent, false);
      convertView.setLayoutParams(new ListView.LayoutParams(
          AbsListView.LayoutParams.MATCH_PARENT,
          AbsListView.LayoutParams.WRAP_CONTENT));
      viewHolder.titleTextView =
          (TextView) convertView.findViewById(R.id.item_title);
      viewHolder.likeImageView =
          (ImageView) convertView.findViewById(R.id.item_like_pic);
      viewHolder.likeTextView =
          (TextView) convertView.findViewById(R.id.item_like_amount);
      viewHolder.commentImageView =
          (ImageView) convertView.findViewById(R.id.item_comment_pic);
      viewHolder.commentTextView =
          (TextView) convertView.findViewById(R.id.item_comment_amount);
      viewHolder.mainImageView =
          (ImageView) convertView.findViewById(R.id.item_main_pic);
      viewHolder.mainContentView =
          (TextView) convertView.findViewById(R.id.item_content);
      viewHolder.expandingLayout =
          (ExpandingLayout) convertView.findViewById(R.id.expanding_layout);
      viewHolder.linearLayout =
          (LinearLayout) convertView.findViewById(R.id.item_linear_layout);
      convertView.setTag(viewHolder);
    }
    else
    {
      viewHolder = (ViewHolder) convertView.getTag();
    }

//     LinearLayout.LayoutParams linearLayoutParams =
//     new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
//     614);
//     viewHolder.linearLayout.setLayoutParams(linearLayoutParams);

    viewHolder.titleTextView.setText(data.getTitle());
    viewHolder.likeTextView.setText(data.getLikeAmount());
    viewHolder.commentTextView.setText(data.getCommentAmount());

    viewHolder.likeImageView.setImageDrawable(activity.getResources()
        .getDrawable(data.getLikeImgResId()));
    viewHolder.commentImageView.setImageDrawable(activity.getResources()
        .getDrawable(data.getCommentImgResId()));
    viewHolder.mainImageView.setImageDrawable(activity.getResources()
        .getDrawable(data.getMainImgResId()));
    viewHolder.mainContentView.setText(R.string.item_content);

    viewHolder.expandingLayout.setExpandedHeight(data.getExpandedHeight());
    viewHolder.expandingLayout.setSizeChangedListener(data);
    viewHolder.expandingLayout.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        Log.i("sdfafaf", "expanding layout clicked");
        ((MainActivity) activity).showArticle();
      }
    });

    if(data.isExpanded())
    {
      viewHolder.expandingLayout.setVisibility(View.VISIBLE);
    }
    else
    {
      viewHolder.expandingLayout.setVisibility(View.GONE);
    }

    return convertView;
  }

  /**
   * 用于取代setHasTransientState(true)方法，setHasTransientState()在API 16以后
   * 才能使用
   */
  @Override
  public boolean hasStableIds()
  {
    return true;
  }

  private class ViewHolder
  {
    public TextView titleTextView;
    public ImageView likeImageView;
    public TextView likeTextView;
    public ImageView commentImageView;
    public TextView commentTextView;
    public ImageView mainImageView;
    public TextView mainContentView;
    public ExpandingLayout expandingLayout;
    public LinearLayout linearLayout;
  }
}
