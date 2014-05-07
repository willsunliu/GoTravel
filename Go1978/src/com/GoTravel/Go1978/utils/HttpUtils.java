package com.GoTravel.Go1978.utils;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.GoTravel.Go1978.log.MyLog;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.webkit.CookieManager;


/**
 * @author Wilson 20131202
 *         HttpTool用于和服务器建立连接，通过GET和POST方法与服务器通信。
 *         建立通信连接的步骤：
 *         1. 创建HttpTool实例
 *         2. 调用createPostConnection或createGetConnection创建对应的POST或GET连接
 *         3. 调用get*, send*等方法与服务器进行通信
 *         4. 通信完毕后调用releaseConnection释放连接
 */
public class HttpUtils
{
  private static final String TAG = "HttpUtils";
  
  public static final int NO_IMAGE_ID = -2;

  private int streamSize = 128;
  private int readTimeout = 10000; // milliseconds
  private int connectTimeout = 15000; // milliseconds
  
  private int headerLen = 4;

  // 用于通信的HttpURLConnection
  HttpURLConnection conn;

  /**
   * 创建request method为post的Http连接
   * 
   * @param myUrl 将要建立连接的url
   * @param contentType Http传输的数据类型
   * @throws MalformedURLException
   * @throws ProtocolException
   * @throws IOException
   */
  public void createPostConnection(String myUrl, String contentType, int size)
      throws MalformedURLException, ProtocolException, IOException
  {
    if(conn != null)
    {
      // 如果conn存在并且request method不为post，断开连接，重新创建
//      if(!conn.getRequestMethod().equals("POST"))
//      {
        conn.disconnect();
        conn = setupPostHttpURLConnection(myUrl, contentType, size);
//      }
    }
    // conn不存在，创建一个conn
    else
    {
      conn = setupPostHttpURLConnection(myUrl, contentType, size);
    }
  }

  @SuppressWarnings("unused")
  private void createGetConnection(String myUrl, String contentType)
      throws MalformedURLException, ProtocolException, IOException
  {
    if(conn != null)
    {
//      if(!conn.getRequestMethod().equals("GET"))
//      {
        conn.disconnect();
        conn = setupGetHttpURLConnection(myUrl);
//      }
    }
    else
    {
      conn = setupGetHttpURLConnection(myUrl);
    }
  }

  private HttpURLConnection setupGetHttpURLConnection(String myUrl)
      throws IOException
  {
    URL url = null;
    url = new URL(myUrl);
    System.setProperty("http.keepAlive", "false");
    conn = (HttpURLConnection) url.openConnection();
    conn.connect();
    return conn;
  }

  /**
   * 初始化HttpURLConnection对象
   * 
   * @param myUrl 将要建立连接的URL
   * @param contentType HTTP传输的数据类型
   * @return 返回HttpURLConnection实例
   * @throws MalformedURLException
   * @throws IOException
   * @throws ProtocolException
   */
  private HttpURLConnection setupPostHttpURLConnection(String myUrl,
      String contentType, int size) throws MalformedURLException, IOException,
      ProtocolException
  {
    MyLog.i(TAG, "setupHttpURLConnection");
    URL url = new URL(myUrl);

    /*
     * Android的HttpURLConnection存在一个bug，如果关闭一个可读的InputStream会污染
     * connection pool。为了避免这个bug导致连接出现问题，关闭connection pool
     */
    System.setProperty("http.keepAlive", "false");

    // 打开连接
    conn = (HttpURLConnection) url.openConnection();

    /*
     * 设置每次传输的流的大小，可以有效防止手机因内存不足崩溃。
     * 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的HTTP请求正文的流
     */
    conn.setChunkedStreamingMode(streamSize * 1024);

    // 设置读取超时时间和连接超时时间
    conn.setReadTimeout(readTimeout);
    conn.setConnectTimeout(connectTimeout /* milliseconds */);

    conn.setRequestMethod("POST");

    /*
     * post包输入流和输出流都要开启
     */
    conn.setDoInput(true);
    conn.setDoOutput(true);

    // 不使用缓存
    conn.setUseCaches(false);

    /*
     * 设置HTTP包头部信息
     */
     conn.setRequestProperty("Connection", "Keep-Alive");
    if(contentType.startsWith("text/"))
    {
      conn.setRequestProperty("Charset", "UTF-8");
    }
    conn.setRequestProperty("Content-Type", contentType);
    MyLog.i(TAG, "size=" + size);
    if (size != -1) {
      conn.setRequestProperty("Content-Length", String.valueOf(size));
    }
    CookieManager cookieManager = CookieManager.getInstance();
    String cookie = cookieManager.getCookie(new URL(myUrl).getHost());
    MyLog.i(TAG, "Cookies=" + cookie);
    conn.setRequestProperty("Cookie", cookie);

    // Can't read response before getting output stream
    // int response = conn.getResponseCode();

    // 建立连接
    conn.connect();

    return conn;
  }

  /**
   * 释放HttpURLConnection
   */
  public void releaseConnection()
  {
    if(conn != null)
    {
      conn.disconnect();
      conn = null;
    }
  }

  /**
   * 发送文本内容到服务器
   * 
   * @param form 文本内容
   * @return 返回服务器返回的响应性息
   * @throws IOException
   * @throws HttpConnectionException
   */
  public String sendContent(String form) throws IOException,
      HttpConnectionException
  {
    MyLog.i(TAG, "postComment");

    // 检查HttpURLConnection是否已经建立
    if(conn == null || conn.getRequestMethod().equals("GET"))
    {
      throw new HttpConnectionException(
          "HttpURLConnection is null or request method is invalid.");
    }

    byte[] formBuf = null; // 用于存放文本内容的字节数组
    String content = null; // 用于接收服务器返回的响应
    DataOutputStream dos = null; // 数据输出流，用于向服务器写数据
    InputStream is = null; // 输入流，用于从服务器读取响应
    ByteArrayOutputStream baos = null; // 用于暂时存放读取到的服务器响应

    try
    {
      /*
       * 向服务器发送数据
       */
      formBuf = form.getBytes("UTF-8");
      dos = new DataOutputStream(conn.getOutputStream());
      dos.write(formBuf);
      dos.flush();
      dos.close();
      MyLog.i(TAG, "Finish dos writing!");
      MyLog.i(TAG, conn.getResponseCode() + "");

      /*
       * 读取服务器的响应
       */
      String encoding = conn.getContentEncoding(); // 获取包的编码格式
      is = conn.getInputStream();
      int read = -1;
      baos = new ByteArrayOutputStream();
      while((read = is.read()) != -1)
      {
        // 将响应内容暂存在baos中
        baos.write(read);
      }
      byte[] data = baos.toByteArray();
      if(encoding != null)
      {
        content = new String(data, encoding);
      }
      else
      {
        content = new String(data);
      }
      MyLog.i(TAG, "content is " + content);
    }
    catch(UnsupportedEncodingException e)
    {
      e.printStackTrace();
      MyLog.e(TAG, "UnsupportedEncodingException!");
    }
    finally
    {
      if(is != null)
      {
        is.close();
      }
      if(dos != null)
      {
        dos.close();
      }
      if(baos != null)
      {
        baos.close();
      }
    }
    return content;
  }

  /**
   * 向服务器发送文件
   * 
   * @param authId 服务器返回的图片id
   * @param picPath 文件路径
   * @return 返回服务器的响应内容
   * @throws IOException
   * @throws HttpConnectionException
   */
  public String sendFile(ByteBuffer headBuffer, String picPath) throws IOException,
      HttpConnectionException
  {
    MyLog.i(TAG, "postPic");
    if(conn == null || conn.getRequestMethod().equals("GET"))
    {
      throw new HttpConnectionException(
          "HttpURLConnection is null or request method is invalid.");
    }

    InputStream is = null; // 输入流，用于从服务器读取响应
    FileInputStream fis = null; // 文件输入流，用于读取文件
    DataOutputStream dos = null; // 数据输出流，用于向服务器写数据
    ByteArrayOutputStream baos = null; // 用于暂时存放读取到的服务器响应

    try
    {
      /*
       * 向服务器发送文件
       */
      dos = new DataOutputStream(conn.getOutputStream());
      MyLog.i(TAG, "getOutputStream successfully!");
      fis = new FileInputStream(picPath);
      byte[] buffer = new byte[8192];
//      if(authId != NO_IMAGE_ID)
//      {
//        ByteBuffer bb = ByteBuffer.allocate(4);
//        bb.order(ByteOrder.LITTLE_ENDIAN);
//        bb.putInt(authId);
//        dos.write(bb.array());
//        MyLog.i(TAG, "write authId into dos successfully!");
//      }
      if (headBuffer != null) {
        dos.write(headBuffer.array());
        MyLog.i(TAG, "write authId into dos successfully!");
      }
      int count = 0;
      while((count = fis.read(buffer)) != -1)
      {
        dos.write(buffer, 0, count);
      }
      MyLog.i(TAG, "wrtie file into dos successfully!");
      fis.close();
      dos.flush();
      MyLog.i(TAG, "Finish dos writing!");
      MyLog.i(TAG, conn.getResponseCode() + "");

      /*
       * 获取服务器的响应信息
       */
      String encoding = conn.getContentEncoding();
      is = conn.getInputStream();
      int read = -1;
      baos = new ByteArrayOutputStream();
      while((read = is.read()) != -1)
      {
        baos.write(read);
      }
      byte[] data = baos.toByteArray();
      String content = null;
      if(encoding != null)
      {
        content = new String(data, encoding);
      }
      else
      {
        content = new String(data);
      }
      MyLog.i(TAG, "content is " + content);
      is.close();
      dos.close();
      baos.close();
      return content;
    }
    finally
    {
      if(is != null)
      {
        is.close();
      }
      if(dos != null)
      {
        dos.close();
      }
      if(baos != null)
      {
        baos.close();
      }
      if(fis != null)
      {
        fis.close();
      }
    }
  }

  /**
   * @author Wilson 20131202
   *         当用户在调用send*等方法前，没有创建HttpURLConnection对象时，抛出此异常
   */
  private class HttpConnectionException extends Exception
  {
    public HttpConnectionException()
    {
      super();
    }

    public HttpConnectionException(String msg)
    {
      super(msg);
    }
  }

  public static int getConnectedType(Context context)
  {
    if(context != null)
    {
      ConnectivityManager myConnectivityManager =
          (ConnectivityManager) context
              .getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo myNetworkInfo = myConnectivityManager.getActiveNetworkInfo();
      if(myNetworkInfo != null && myNetworkInfo.isAvailable())
      {
        return myNetworkInfo.getType();
      }
    }
    return -1;
  }

  public void setStreamSize(int streamSize)
  {
    this.streamSize = streamSize;
  }

  public void setReadTimeout(int readTimeout)
  {
    this.readTimeout = readTimeout;
  }

  public void setConnectTimeout(int connectTimeout)
  {
    this.connectTimeout = connectTimeout;
  }

  /**
   * @return the headerLen
   */
  public int getHeaderLen()
  {
    return headerLen;
  }

  /**
   * @param headerLen the headerLen to set
   */
  public void setHeaderLen(int headerLen)
  {
    this.headerLen = headerLen;
  }
}
