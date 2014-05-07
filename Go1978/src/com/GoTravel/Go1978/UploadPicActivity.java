package com.GoTravel.Go1978;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sina.weibo.sdk.api.BaseResponse;
import com.sina.weibo.sdk.api.IWeiboHandler;
import com.sina.weibo.sdk.constant.Constants;
import com.GoTravel.Go1978.authorization.MyAuthorization;
import com.GoTravel.Go1978.constants.SinaConstants;
import com.GoTravel.Go1978.log.MyLog;
import com.GoTravel.Go1978.utils.HttpUtils;
import com.GoTravel.Go1978.utils.ImageUtils;
import com.GoTravel.Go1978.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;


/**
 * UploadPicActivity用于实现分享功能：
 * 1. 上传图片到服务器
 * 2. 分享图片到新浪微博
 * 
 * @author Wilson 20131228
 */
public class UploadPicActivity extends BaseActivity implements
    IWeiboHandler.Response
{
  private static final String TAG = "UploadPicActivity";
  private static final int MP = TableLayout.LayoutParams.MATCH_PARENT;
  private static final int WC = TableLayout.LayoutParams.WRAP_CONTENT;

  // 图片上传到服务器的URL
  private static final String UPLOAD_PIC_URL =
  // "http://www.123go.net.cn/plugin.php?id=javasyn&mod=upload_album_img";
      "http://192.168.3.109/plugin.php?id=javasyn&mod=upload_album_img";

  // 缩略图的ID
  private static final int SELECTED_IMAGE_BASE_ID = 0x120;
  // table row的ID
  private static final int ROW_BASE_ID = 0x130;
  // 可上传图片数量的最大值
  private static final int MAX_PIC_NUM = 8;

  private String picPath = null;
  // 存放待分享图片路径的List
  private ArrayList<String> fileList = new ArrayList<String>();
  // 上传失败的文件列表
  ArrayList<HashMap<String, Integer>> failList =
      new ArrayList<HashMap<String, Integer>>();

  private int picCount = 0;

  private int rowCount = 0;

  GoTravelApplication application;
  MyAuthorization authorization;

  Button btnPostPic, btnTextMsgShare, btnMultiMsgShare;
  EditText etComment, picNum;
  CheckBox cbShared;
  TableLayout picTable;
  HttpUtils httpUtils;

  String strComment;
  boolean isSharedtoWeibo;
  String selectedPicNum;

  // 用于判断上传是否成功
  private boolean isAllUploadSuccessfully = false;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose_pic);
    application = (GoTravelApplication) getApplication();
    authorization = application.getMyAuthorization();

    httpUtils = new HttpUtils();

    cbShared = (CheckBox) findViewById(R.id.cb_share);
    cbShared.setOnCheckedChangeListener(new OnCheckedChangeListener()
    {

      public void
          onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        if(isChecked)
        {
          isSharedtoWeibo = true;
          picNum.setEnabled(true);
          picNum.setText("1");
        }
        else
        {
          isSharedtoWeibo = false;
          picNum.setText("");
          picNum.setEnabled(false);
        }
      }

    });
    cbShared.setChecked(false);

    picNum = (EditText) findViewById(R.id.pic_num);
    picNum.setEnabled(false);

    // 添加一个image view，用来显示被选择的经过被缩放的图片
    addImageView();

    // 初始化新浪SDK
    if(authorization.getThirdPartyName().equals(
        SinaConstants.THIRD_PARTY_NAME_SINA))
    {
      MyLog.i(TAG, "init sina sdk");
      authorization.initSinaSDK(UploadPicActivity.this);
      authorization.setupSinaResponseListener(getIntent(), this);
    }

    etComment = (EditText) findViewById(R.id.et_comment);

    btnPostPic = (Button) findViewById(R.id.btn_post_pic);
    btnPostPic.setOnClickListener(new OnClickListener()
    {

      public void onClick(View v)
      {
        // 如果网络正常，到后台进行http通信
        if(isNetworkAvailable())
        {
          String jsonStr = null;
          JSONObject json = new JSONObject();

          try
          {
            // 文本内容
            json.put(GoTravelApplication.JSON_KEY_COMMENT, etComment.getText()
                .toString());
            // 需要上传的图片数量
            json.put(GoTravelApplication.JSON_KEY_FILE_NUM, fileList.size());
            // 告知服务器是否需要分享图片到新浪微博
            json.put(GoTravelApplication.JSON_KEY_SINA_SHARE_TO_WEIBO,
                isSharedtoWeibo);
            // 被选择的需要分享到第三方的图片
            json.put(GoTravelApplication.JSON_KEY_SINA_SELECTED_PICTURE_NUM,
                picNum.getText().toString());
            jsonStr = json.toString();
          }
          catch(JSONException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }

          new HttpAsyncTask(jsonStr).execute(GoTravelApplication.POST,
              UPLOAD_PIC_URL, GoTravelApplication.UPLOAD);
        }
        else
        {
          Toast.makeText(getApplicationContext(),
              "No network connection available.", Toast.LENGTH_LONG).show();
        }
      }

    });

    btnTextMsgShare = (Button) findViewById(R.id.btn_share_text_msg);
    btnTextMsgShare.setOnClickListener(new OnClickListener()
    {

      public void onClick(View v)
      {
        // 分享纯文本的内容到新浪微博
        authorization.shareTextMsgToSina(UploadPicActivity.this, etComment
            .getText().toString());
      }

    });

    btnMultiMsgShare = (Button) findViewById(R.id.btn_share_multi_msg);
    btnMultiMsgShare.setOnClickListener(new OnClickListener()
    {

      public void onClick(View v)
      {
        // 分享带图片和文字的内容到新浪微博
        authorization.shareMultiMsgToSina(UploadPicActivity.this,
            fileList.get(0), etComment.getText().toString());
      }

    });

  }

  /**
   * 添加ImageView到TableLayout
   */
  private void addImageView()
  {
    boolean isNewRow = false;
    TableRow tableRow = null;
    TableRow.LayoutParams lp = new TableRow.LayoutParams(144, 144);
    lp.setMargins(16, 16, 0, 0);

    picTable = (TableLayout) findViewById(R.id.pic_table);

    ImageView imageView = new ImageView(this);
    // ImageView的ID从SELECTED_IMAGE_BASE_ID到SELECTED_IMAGE_BASE_ID + 7
    imageView.setId(SELECTED_IMAGE_BASE_ID + picCount);
    imageView.setBackgroundResource(R.drawable.image_view_bg);
    imageView.setOnClickListener(new MyImageViewClickListener());

    // 创建TableRow，每个TableRow显示4张缩略图
    if(picCount == 0 || picCount == 4)
    {
      tableRow = new TableRow(this);
      tableRow.setId(ROW_BASE_ID + rowCount);
      isNewRow = true;
    }
    else
    {
      tableRow = (TableRow) picTable.findViewById(ROW_BASE_ID + rowCount - 1);
    }
    tableRow.addView(imageView, lp);
    picCount += 1;
    TableLayout.LayoutParams params = new TableLayout.LayoutParams(MP, WC);
    if(isNewRow)
    {
      picTable.addView(tableRow, params);
      rowCount += 1;
    }
  }

  // 实现新浪微博分享功能必须重载此函数
  @Override
  protected void onNewIntent(Intent intent)
  {
    if(authorization.getThirdPartyName().equals(
        SinaConstants.THIRD_PARTY_NAME_SINA))
    {
      authorization.setupSinaResponseListener(intent, this);
    }
    super.onNewIntent(intent);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    Bitmap bitmap = null;
    if(resultCode == RESULT_OK)
    {
      // 从data中获取选中图片的路径
      Uri selectedImageUri = data.getData();
      MyLog.i(TAG, "image path is: " + selectedImageUri.toString());
      String[] filePathColumn = {MediaStore.Images.Media.DATA};
      Cursor cursor =
          getContentResolver().query(selectedImageUri, filePathColumn, null,
              null, null);
      cursor.moveToFirst();
      int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
      String filePath = cursor.getString(columnIndex);
      cursor.close();
      picPath = filePath;
      if(picPath == null)
      {
        Toast.makeText(getApplicationContext(), "Choose Picture Fail!",
            Toast.LENGTH_LONG).show();
        return;
      }

      // 把路径添加到fileList
      fileList.add(filePath);

      // 创建缩略图
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      bitmap = BitmapFactory.decodeFile(filePath, options);
      boolean needRotate = false;
      if(options.outWidth > options.outHeight)
      {
        needRotate = true;
      }
      options.inJustDecodeBounds = false;
      options.inSampleSize = 10;
      bitmap = BitmapFactory.decodeFile(filePath, options);
      bitmap = ThumbnailUtils.extractThumbnail(bitmap, 80, 80);
      Matrix matrix = new Matrix();
      if(needRotate)
      {
        matrix.setRotate(90);
      }
      bitmap = Bitmap.createBitmap(bitmap, 0, 0, 80, 80, matrix, true);
    }

    // 添加bitmap到ImageView，并新建一个ImageView用于添加下一张图片
    if(bitmap != null)
    {
      ImageView imageView = (ImageView) findViewById(requestCode);
      imageView.setImageBitmap(bitmap);
      MyLog.i(TAG, (requestCode - SELECTED_IMAGE_BASE_ID + 1) + "");
      if(requestCode - SELECTED_IMAGE_BASE_ID + 1 == picCount)
      {
        MyLog.i(TAG, "addImageView");
        addImageView();
      }
    }
  }

  /**
   * 实现OnClickListener接口供ImageView使用，实现点击转到Gallery选择图片并添加
   * 
   * @author Wilson 20131228
   */
  private class MyImageViewClickListener implements OnClickListener
  {

    public void onClick(View v)
    {
      int clickId = SELECTED_IMAGE_BASE_ID;
      Intent intent = new Intent();
      intent.setType("image/*");
      intent.setAction(Intent.ACTION_GET_CONTENT);

      for(int i = 0; i < picCount; i++)
      {
        if(v.getId() == SELECTED_IMAGE_BASE_ID + i)
        {
          clickId += i;
          break;
        }
      }
      startActivityForResult(intent, clickId);
    }

  }

  /**
   * 检测设备网络是否可用
   * 
   * @return 网络可用返回true；网络不可用返回false
   */
  private boolean isNetworkAvailable()
  {
    ConnectivityManager connMgr =
        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    return(networkInfo != null && networkInfo.isConnected());
  }

  /**
   * 继承AsyncTask，启动别的线程去完成网络的各种操作
   * 
   * @author Wilson 20131228
   */
  private class HttpAsyncTask extends AsyncTask<String, String, String>
  {
    private String jsonStr = null;
    private String callback = null;
    private ProgressDialog dialog = null;
    String failNumString = null;
    String path = null;
    ArrayList<HashMap<String, Integer>> tmpFailList =
        new ArrayList<HashMap<String, Integer>>();
    JSONObject response;
    JSONObject status;
    boolean canUploadFile = false;

    public HttpAsyncTask()
    {
      super();
      canUploadFile = true;
    }

    public HttpAsyncTask(String json)
    {
      super();
      this.jsonStr = json;
      canUploadFile = false;
    }

    public HttpAsyncTask(String json, String callback)
    {
      super();

      this.jsonStr = json;
      canUploadFile = false;

      this.callback = callback;
    }

    @Override
    protected void onPreExecute()
    {
      isAllUploadSuccessfully = false;

      // 显示一个ProgressDialog等待网络操作的结束
      dialog = new ProgressDialog(UploadPicActivity.this);
      dialog.setMessage(application.getString(R.string.uploading));
      dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      dialog.setCanceledOnTouchOutside(false);
      dialog.show();
    }

    @Override
    protected String doInBackground(String... params)
    {

      try
      {
        // Post
        if(params[0].equals(GoTravelApplication.POST))
        {

          if(params[2].equals(GoTravelApplication.UPLOAD))
          {

            // 1.以Json格式发送相关信息到服务器
            if(jsonStr != null)
            {
              JSONObject json = new JSONObject(jsonStr);
              JSONArray jsonArray = json.names();
              boolean hasPicName = false;
              for(int i = 0; i < jsonArray.length(); i++)
              {
                String name = jsonArray.getString(i);
                if(name.equals(GoTravelApplication.JSON_KEY_PIC_NAME))
                {
                  hasPicName = true;
                }
              }
              if(hasPicName)
              {
                path = json.getString(GoTravelApplication.JSON_KEY_PIC_NAME);
                json.remove(GoTravelApplication.JSON_KEY_PIC_NAME);
                String jsonStr = json.toString();
              }
              application.getHttpUtils().createPostConnection(params[1],
                  "application/json", -1);
              String result = application.getHttpUtils().sendContent(jsonStr);
              application.getHttpUtils().releaseConnection();
              response = new JSONObject(result);
              status =
                  response.getJSONObject(GoTravelApplication.JSON_KEY_STATUS);
              if(status.getInt(GoTravelApplication.JSON_KEY_STATUS_CODE) == GoTravelApplication.SERVER_RESPONSE_OK)
              {
                canUploadFile = true;
              }
            }

            // 2.开始图片的上传
            if(canUploadFile)
            {
              JSONArray imageIds;
              // 如果没有图片，上传结束
              if(fileList.size() <= 0)
              {
                isAllUploadSuccessfully = true;
                return "true";
              }
              // 如果有图片，先根据服务器返回的id生成图片id
              else
              {
                JSONObject data =
                    response.getJSONObject(GoTravelApplication.JSON_KEY_DATA);
                imageIds =
                    data.getJSONArray(GoTravelApplication.JSON_KEY_DATA_FILES);
              }

              // 开始上传
              for(int i = 0; i < fileList.size(); i++)
              {
                String contentType = null;
                String path = null;
                int size = 0;
                path = fileList.get(i);
                contentType = ImageUtils.getContentType(fileList.get(i));
                size = ImageUtils.getImageSize(fileList.get(i));
                int imageId = imageIds.optInt(i, -1);
                MyLog.i(TAG, "imageId=" + imageId);
                if(imageId == -1)
                {
                  return "Image ID Array ERROR";
                }

                upload(params[1], contentType, path, size, i, imageId);

              }

              return null;
            }
            else
            {
              return "Server reject!";
            }

          }
          else if(params[2].equals(GoTravelApplication.REUPLOAD))
          {
            for(int i = 0; i < failList.size(); i++)
            {
              String path = fileList.get(failList.get(i).get("index"));
              String contentType = null;
              int size = 0;
              contentType = ImageUtils.getContentType(path);
              size = ImageUtils.getImageSize(path);

              upload(params[1], contentType, path, size,
                  failList.get(i).get("index"), failList.get(i).get("image_id"));

            }

            return null;
          }
        } // POST

        return "Unknowed http method.";
      }
      catch(Exception exception)
      {
        return "Unable to retrieve web page. URL may be invalid.";
      }
    }

    @Override
    protected void onPostExecute(String result)
    {
      // 如果上传成功，更新UI
      if(isAllUploadSuccessfully)
      {
        etComment.setText("");

        ImageView imageView = null;
        for(int i = 0; i < picCount; i++)
        {
          imageView = (ImageView) findViewById(SELECTED_IMAGE_BASE_ID + i);
          imageView.setImageBitmap(null);
          imageView.destroyDrawingCache();
        }
        dialog.dismiss();
      }
      // 有失败，询问是否需要重新上传失败的图片
      else
      {
        dialog.dismiss();
        failList = tmpFailList;
        new AlertDialog.Builder(UploadPicActivity.this).setTitle("上传失败")
            .setMessage("图片" + failNumString + "上传失败，是否重新上传？")
            .setPositiveButton("是", new DialogInterface.OnClickListener()
            {

              public void onClick(DialogInterface dialog, int which)
              {
                new HttpAsyncTask().execute(GoTravelApplication.POST,
                    UPLOAD_PIC_URL, GoTravelApplication.REUPLOAD);
              }
            }).setNegativeButton("否", new DialogInterface.OnClickListener()
            {

              public void onClick(DialogInterface dialog, int which)
              {
                UploadPicActivity.this.finish();
              }
            }).show();
      }
    }

    private void upload(String url, String contentType, String path, int size,
        int index, int imageId) throws Exception
    {
      String result = null;
      httpUtils.createPostConnection(url, contentType, size);
      ByteBuffer bb = ByteBuffer.allocate(4);
      bb.order(ByteOrder.LITTLE_ENDIAN);
      bb.putInt(imageId);
      result = httpUtils.sendFile(bb, path);
      httpUtils.releaseConnection();
      response = new JSONObject(result);

      // 判断上传是否成功
      status = response.getJSONObject(GoTravelApplication.JSON_KEY_STATUS);

      if(!(status.getInt(GoTravelApplication.JSON_KEY_STATUS_CODE) == GoTravelApplication.SERVER_RESPONSE_OK))
      {
        isAllUploadSuccessfully = false;
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("index", index);
        map.put("image_id", imageId);
        tmpFailList.add(map);
        if(failNumString == null)
        {
          failNumString = "" + (index + 1);
        }
        else
        {
          failNumString = failNumString + "," + (index + 1);
        }
      }
    }
  }

  /**
   * 新浪微博分享的回调函数
   */
  public void onResponse(BaseResponse baseResp)
  {
    MyLog.i(TAG, "onResponse, baseResp=" + baseResp.toString());
    switch(baseResp.errCode)
    {
      case Constants.ErrorCode.ERR_OK:
        Toast.makeText(this, "Post Successfully!", Toast.LENGTH_LONG).show();
        break;
      case Constants.ErrorCode.ERR_CANCEL:
        Toast.makeText(this, "User cancel the post", Toast.LENGTH_LONG).show();
        break;
      case Constants.ErrorCode.ERR_FAIL:
        Toast.makeText(this, baseResp.errMsg + ": fail!!", Toast.LENGTH_LONG)
            .show();
        break;
    }
  }

}
