package com.example.demogo1978.MainContent;

public class ArticleListItem implements OnSizeChangedListener
{
  private String mTitle;
  private String mLikeAmount;
  private String mCommentAmount;
  private String mContent;
  private int mLikeImgResId;
  private int mCommentImgResId;
  private int mMainImgResId;

  private boolean mIsExpanded;
  private int mCollaspedHeight;
  private int mExpandedHeight;

  public ArticleListItem(String title, String likeAmount, String commentAmount,
      String Content, int likeImgResId, int commentImgResId, int mainImgResId,
      int collaspedHeight)
  {
    mTitle = title;
    mLikeAmount = likeAmount;
    mCommentAmount = commentAmount;
    mLikeImgResId = likeImgResId;
    mCommentImgResId = commentImgResId;
    mMainImgResId = mainImgResId;
    mCollaspedHeight = collaspedHeight;
    mExpandedHeight = -1;
    mIsExpanded = false;
  }

  /**
   * @return the mTitle
   */
  public String getTitle()
  {
    return mTitle;
  }

  /**
   * @param mTitle the mTitle to set
   */
  public void setTitle(String mTitle)
  {
    this.mTitle = mTitle;
  }

  /**
   * @return the mLikeAmount
   */
  public String getLikeAmount()
  {
    return mLikeAmount;
  }

  /**
   * @param mLikeAmount the mLikeAmount to set
   */
  public void setLikeAmount(String mLikeAmount)
  {
    this.mLikeAmount = mLikeAmount;
  }

  /**
   * @return the mCommentAmount
   */
  public String getCommentAmount()
  {
    return mCommentAmount;
  }

  /**
   * @param mCommentAmount the mCommentAmount to set
   */
  public void setCommentAmount(String mCommentAmount)
  {
    this.mCommentAmount = mCommentAmount;
  }

  /**
   * @return the mLikeImgResId
   */
  public int getLikeImgResId()
  {
    return mLikeImgResId;
  }

  /**
   * @param mLikeImgResId the mLikeImgResId to set
   */
  public void setLikeImgResId(int mLikeImgResId)
  {
    this.mLikeImgResId = mLikeImgResId;
  }

  /**
   * @return the mCommentImgResId
   */
  public int getCommentImgResId()
  {
    return mCommentImgResId;
  }

  /**
   * @param mCommentImgResId the mCommentImgResId to set
   */
  public void setCommentImgResId(int mCommentImgResId)
  {
    this.mCommentImgResId = mCommentImgResId;
  }

  /**
   * @return the mMainImgResId
   */
  public int getMainImgResId()
  {
    return mMainImgResId;
  }

  /**
   * @param mMainImgResId the mMainImgResId to set
   */
  public void setMainImgResId(int mMainImgResId)
  {
    this.mMainImgResId = mMainImgResId;
  }

  /**
   * @return the mIsExpanded
   */
  public boolean isExpanded()
  {
    return mIsExpanded;
  }

  /**
   * @param mIsExpanded the mIsExpanded to set
   */
  public void setExpanded(boolean mIsExpanded)
  {
    this.mIsExpanded = mIsExpanded;
  }

  /**
   * @return the mCollaspedHeight
   */
  public int getCollaspedHeight()
  {
    return mCollaspedHeight;
  }

  /**
   * @param mCollaspedHeight the mCollaspedHeight to set
   */
  public void setCollaspedHeight(int mCollaspedHeight)
  {
    this.mCollaspedHeight = mCollaspedHeight;
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

  /**
   * @return the mContent
   */
  public String getmContent()
  {
    return mContent;
  }

  /**
   * @param mContent the mContent to set
   */
  public void setmContent(String mContent)
  {
    this.mContent = mContent;
  }

  @Override
  public void onSizeChanged(int newHeight)
  {
    setExpandedHeight(newHeight);
  }
}
