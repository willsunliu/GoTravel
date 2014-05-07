package com.example.demohsv;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment.SavedState;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.BitmapFactory.Options;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity
{

  private static final String TAG = "DemoHSV";

  private static final float CONVERSION_VALUE_HUE = 180f;
  private static final float CONVERSION_VALUE_SATURATION = 100f;
  private static final float CONVERSION_VALUE_VALUE = 200f;

  private static final int REQUEST_GALLERY = 0x111;

  ImageView iv;
  Bitmap src = null;

  TextView red1, red2, red3, red4, red5;
  TextView green1, green2, green3, green4, green5;
  TextView blue1, blue2, blue3, blue4, blue5;
  TextView alpha1, alpha2, alpha3, alpha4, alpha5;

  SeekBar hueBar, saturationBar, valueBar;
  TextView tvHue, tvSaturation, tvValue;

  TextView tvTime;

  HSVDatabaseHelper dbHelper;

  boolean isLoading = false;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    dbHelper = new HSVDatabaseHelper(this, "HSV.db3", 1);

    if(src == null)
    {
      AssetManager assetMgr = getAssets();
      try
      {
        InputStream is = assetMgr.open("110.jpg");
        src = BitmapFactory.decodeStream(is);
      }
      catch(IOException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      iv = (ImageView) findViewById(R.id.imave);
      iv.setImageBitmap(src);
    }

    MyOnSeekBarChangeListener seekBarListener = new MyOnSeekBarChangeListener();
    hueBar = (SeekBar) findViewById(R.id.seek_hue);
    hueBar.setOnSeekBarChangeListener(seekBarListener);
    saturationBar = (SeekBar) findViewById(R.id.seek_saturation);
    saturationBar.setOnSeekBarChangeListener(seekBarListener);
    valueBar = (SeekBar) findViewById(R.id.seek_value);
    valueBar.setOnSeekBarChangeListener(seekBarListener);

    tvHue = (TextView) findViewById(R.id.tv_hue);
    tvSaturation = (TextView) findViewById(R.id.tv_saturation);
    tvValue = (TextView) findViewById(R.id.tv_value);

    tvTime = (TextView) findViewById(R.id.tv_time);

    red1 = (TextView) findViewById(R.id.red_1);
    red2 = (TextView) findViewById(R.id.red_2);
    red3 = (TextView) findViewById(R.id.red_3);
    red4 = (TextView) findViewById(R.id.red_4);
    red5 = (TextView) findViewById(R.id.red_5);
    green1 = (TextView) findViewById(R.id.green_1);
    green2 = (TextView) findViewById(R.id.green_2);
    green3 = (TextView) findViewById(R.id.green_3);
    green4 = (TextView) findViewById(R.id.green_4);
    green5 = (TextView) findViewById(R.id.green_5);
    blue1 = (TextView) findViewById(R.id.blue_1);
    blue2 = (TextView) findViewById(R.id.blue_2);
    blue3 = (TextView) findViewById(R.id.blue_3);
    blue4 = (TextView) findViewById(R.id.blue_4);
    blue5 = (TextView) findViewById(R.id.blue_5);
    alpha1 = (TextView) findViewById(R.id.alpha_1);
    alpha2 = (TextView) findViewById(R.id.alpha_2);
    alpha3 = (TextView) findViewById(R.id.alpha_3);
    alpha4 = (TextView) findViewById(R.id.alpha_4);
    alpha5 = (TextView) findViewById(R.id.alpha_5);
  }

  /*
   * (non-Javadoc)
   * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
   * android.content.Intent)
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    if(resultCode == RESULT_OK)
    {
      switch(requestCode)
      {
        case REQUEST_GALLERY:
          if(src != null && !src.isRecycled())
          {
            src.recycle();
            src = null;
          }
          iv.setImageBitmap(null);

          Uri uri = data.getData();
          String[] column = {MediaStore.Images.Media.DATA};
          Cursor cursor =
              getContentResolver().query(uri, column, null, null, null);
          cursor.moveToFirst();
          int columnIndex = cursor.getColumnIndex(column[0]);
          String path = cursor.getString(columnIndex);
          cursor.close();
          BitmapFactory.Options opts = new BitmapFactory.Options();
          opts.inJustDecodeBounds = true;
          BitmapFactory.decodeFile(path, opts);
          int width = opts.outWidth;
          int height = opts.outHeight;
          int ratio = 1;
          if(width > 1920 || height > 1920)
          {
            if(width > height)
            {
              ratio = Math.round(opts.outHeight / 1080f);
            }
            else
            {
              ratio = Math.round(opts.outWidth / 1080f);
            }
          }
          opts.inJustDecodeBounds = false;
          opts.inSampleSize = ratio;
          src = BitmapFactory.decodeFile(path, opts);
          iv = (ImageView) findViewById(R.id.imave);
          iv.setImageBitmap(src);
          break;

        default:
          break;
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  /*
   * (non-Javadoc)
   * @see android.app.Activity#onDestroy()
   */
  @Override
  protected void onDestroy()
  {
    iv.clearColorFilter();
    iv.setImageBitmap(null);
    if(src != null && !src.isRecycled())
    {
      src.recycle();
      src = null;
    }
    System.gc();
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  /*
   * (non-Javadoc)
   * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    LayoutInflater inflater = getLayoutInflater();
    switch(item.getItemId())
    {
      case R.id.action_email:
        final View emailDialogLayout =
            inflater.inflate(R.layout.email_dialog,
                (ViewGroup) findViewById(R.id.email_dialog));
        new AlertDialog.Builder(this).setTitle("E-mail Address")
            .setView(emailDialogLayout)
            .setPositiveButton("确定", new DialogInterface.OnClickListener()
            {

              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                String content = getEmailContent();
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"
                    + ((EditText) emailDialogLayout
                        .findViewById(R.id.email_addr)).getText().toString()));
                intent.putExtra(Intent.EXTRA_SUBJECT, "ColorMatrix");
                intent.putExtra(Intent.EXTRA_TEXT, content);
                startActivity(intent);
              }

              private String getEmailContent()
              {
                String content = "";
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from hsv", null);
                while(cursor.moveToNext())
                {
                  String matrix = "";
                  try
                  {
                    JSONObject json =
                        new JSONObject(cursor.getString(cursor
                            .getColumnIndex(HSVDatabaseHelper.MATRIX)));
                    matrix += "red:\n";
                    matrix += json.getString(JSON_RED1) + ", ";
                    matrix += json.getString(JSON_RED2) + ", ";
                    matrix += json.getString(JSON_RED3) + ", ";
                    matrix += json.getString(JSON_RED4) + ", ";
                    matrix += json.getString(JSON_RED5);
                    matrix += "\n";
                    matrix += "green:\n";
                    matrix += json.getString(JSON_GREEN1) + ", ";
                    matrix += json.getString(JSON_GREEN2) + ", ";
                    matrix += json.getString(JSON_GREEN3) + ", ";
                    matrix += json.getString(JSON_GREEN4) + ", ";
                    matrix += json.getString(JSON_GREEN5);
                    matrix += "\n";
                    matrix += "blue:\n";
                    matrix += json.getString(JSON_BLUE1) + ", ";
                    matrix += json.getString(JSON_BLUE2) + ", ";
                    matrix += json.getString(JSON_BLUE3) + ", ";
                    matrix += json.getString(JSON_BLUE4) + ", ";
                    matrix += json.getString(JSON_BLUE5);
                    matrix += "\n";
                    matrix += "alpha:\n";
                    matrix += json.getString(JSON_ALPHA1) + ", ";
                    matrix += json.getString(JSON_ALPHA2) + ", ";
                    matrix += json.getString(JSON_ALPHA3) + ", ";
                    matrix += json.getString(JSON_ALPHA4) + ", ";
                    matrix += json.getString(JSON_ALPHA5);
                    matrix += "\n";
                  }
                  catch(JSONException e)
                  {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                  content +=
                      cursor.getString(cursor
                          .getColumnIndex(HSVDatabaseHelper.NAME)) + "\n\n";
                  content +=
                      "Hue: "
                          + cursor.getString(cursor
                              .getColumnIndex(HSVDatabaseHelper.HUE)) + "\n";
                  content +=
                      "Saturation: "
                          + cursor.getString(cursor
                              .getColumnIndex(HSVDatabaseHelper.SATURATION))
                          + "\n";
                  content +=
                      "Value: "
                          + cursor.getString(cursor
                              .getColumnIndex(HSVDatabaseHelper.VALUE)) + "\n";
                  content += matrix + "\n";
                  content += "------------我是分割线------------\n\n";
                }
                return content;
              }
            }).setNegativeButton("取消", null).show();
        break;
      case R.id.action_load:
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final Cursor cursor = db.rawQuery("select * from hsv", null);
        new AlertDialog.Builder(this).setTitle("Colors")
            .setCursor(cursor, new DialogInterface.OnClickListener()
            {

              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                cursor.moveToPosition(which);
                load(cursor);
                db.close();
              }
            }, HSVDatabaseHelper.NAME).setNegativeButton("取消", null).show();
        break;
      case R.id.action_save:
        final View saveDialogLayout =
            inflater.inflate(R.layout.save_dialog,
                (ViewGroup) findViewById(R.id.save_dialog));
        new AlertDialog.Builder(this).setTitle("MatrixName")
            .setView(saveDialogLayout)
            .setPositiveButton("确定", new DialogInterface.OnClickListener()
            {

              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                String name =
                    ((EditText) saveDialogLayout.findViewById(R.id.et_name))
                        .getText().toString();
                save(name);
              }
            }).setNegativeButton("取消", null).show();
        break;
      case R.id.action_pic:
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_GALLERY);
        break;

      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private void save(String name)
  {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(HSVDatabaseHelper.NAME, name);
    values.put(HSVDatabaseHelper.HUE, hueBar.getProgress());
    values.put(HSVDatabaseHelper.SATURATION, saturationBar.getProgress());
    values.put(HSVDatabaseHelper.VALUE, valueBar.getProgress());
    values.put(HSVDatabaseHelper.MATRIX, convertMatrixToJson());
    db.insertOrThrow(HSVDatabaseHelper.TABLE_NAME, "name", values);
    db.close();
  }

  public static final String JSON_RED1 = "red1";
  public static final String JSON_RED2 = "red2";
  public static final String JSON_RED3 = "red3";
  public static final String JSON_RED4 = "red4";
  public static final String JSON_RED5 = "red5";
  public static final String JSON_GREEN1 = "green1";
  public static final String JSON_GREEN2 = "green2";
  public static final String JSON_GREEN3 = "green3";
  public static final String JSON_GREEN4 = "green4";
  public static final String JSON_GREEN5 = "green5";
  public static final String JSON_BLUE1 = "blue1";
  public static final String JSON_BLUE2 = "blue2";
  public static final String JSON_BLUE3 = "blue3";
  public static final String JSON_BLUE4 = "blue4";
  public static final String JSON_BLUE5 = "blue5";
  public static final String JSON_ALPHA1 = "alpha1";
  public static final String JSON_ALPHA2 = "alpha2";
  public static final String JSON_ALPHA3 = "alpha3";
  public static final String JSON_ALPHA4 = "alpha4";
  public static final String JSON_ALPHA5 = "alpha5";

  private String convertMatrixToJson()
  {
    JSONObject json = new JSONObject();
    try
    {
      json.put(JSON_RED1, red1.getText().toString());
      json.put(JSON_RED2, red2.getText().toString());
      json.put(JSON_RED3, red3.getText().toString());
      json.put(JSON_RED4, red4.getText().toString());
      json.put(JSON_RED5, red5.getText().toString());
      json.put(JSON_GREEN1, green1.getText().toString());
      json.put(JSON_GREEN2, green2.getText().toString());
      json.put(JSON_GREEN3, green3.getText().toString());
      json.put(JSON_GREEN4, green4.getText().toString());
      json.put(JSON_GREEN5, green5.getText().toString());
      json.put(JSON_BLUE1, blue1.getText().toString());
      json.put(JSON_BLUE2, blue2.getText().toString());
      json.put(JSON_BLUE3, blue3.getText().toString());
      json.put(JSON_BLUE4, blue4.getText().toString());
      json.put(JSON_BLUE5, blue5.getText().toString());
      json.put(JSON_ALPHA1, red1.getText().toString());
      json.put(JSON_ALPHA2, red2.getText().toString());
      json.put(JSON_ALPHA3, red3.getText().toString());
      json.put(JSON_ALPHA4, red4.getText().toString());
      json.put(JSON_ALPHA5, red5.getText().toString());
    }
    catch(JSONException e)
    {
      e.printStackTrace();
    }
    return json.toString();
  }

  private void load(Cursor cursor)
  {
    int hue = cursor.getInt(cursor.getColumnIndex(HSVDatabaseHelper.HUE));
    int saturation =
        cursor.getInt(cursor.getColumnIndex(HSVDatabaseHelper.SATURATION));
    int value = cursor.getInt(cursor.getColumnIndex(HSVDatabaseHelper.VALUE));
    isLoading = true;
    hueBar.setProgress(hue);
    saturationBar.setProgress(saturation);
    valueBar.setProgress(value);

    cursor.close();
  }

  private class MyOnSeekBarChangeListener implements OnSeekBarChangeListener
  {

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
        boolean fromUser)
    {
      switch(seekBar.getId())
      {
        case R.id.seek_hue:
          progress -= CONVERSION_VALUE_HUE;
          tvHue.setText("" + progress);
          break;
        case R.id.seek_saturation:
          progress -= CONVERSION_VALUE_SATURATION;
          tvSaturation.setText("" + progress);
          break;
        case R.id.seek_value:
          progress -= CONVERSION_VALUE_VALUE;
          tvValue.setText("" + progress);
          break;

        default:
          break;
      }

      if(isLoading)
      {
        convert();
        isLoading = false;
      }
      // else {
      // convert();
      // }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
      convert();
    }

  }

  private void convert()
  {
    long start = System.currentTimeMillis();
    ColorMatrix srcCm = new ColorMatrix();
    ColorMatrix hueCm = getHueColorMatrix();
    ColorMatrix saturationCm = getSaturationColorMatrix();
    ColorMatrix valueCm = getValueColorMatrix();
    srcCm.postConcat(hueCm);
    srcCm.postConcat(saturationCm);
    srcCm.postConcat(valueCm);
    ColorFilter filter = new ColorMatrixColorFilter(srcCm);
    iv.setColorFilter(filter);
    long end = System.currentTimeMillis();
    tvTime.setText("Used(s): " + String.format("%.2f", (end - start) / 1000f));
    setMatrixText(srcCm.getArray());
  }

  private void setMatrixText(float[] array)
  {
    red1.setText(String.format("%.4f", array[0]));
    red2.setText(String.format("%.4f", array[1]));
    red3.setText(String.format("%.4f", array[2]));
    red4.setText(String.format("%.4f", array[3]));
    red5.setText(String.format("%.4f", array[4]));
    green1.setText(String.format("%.4f", array[5]));
    green2.setText(String.format("%.4f", array[6]));
    green3.setText(String.format("%.4f", array[7]));
    green4.setText(String.format("%.4f", array[8]));
    green5.setText(String.format("%.4f", array[9]));
    blue1.setText(String.format("%.4f", array[10]));
    blue2.setText(String.format("%.4f", array[11]));
    blue3.setText(String.format("%.4f", array[12]));
    blue4.setText(String.format("%.4f", array[13]));
    blue5.setText(String.format("%.4f", array[14]));
    alpha1.setText(String.format("%.4f", array[15]));
    alpha2.setText(String.format("%.4f", array[16]));
    alpha3.setText(String.format("%.4f", array[17]));
    alpha4.setText(String.format("%.4f", array[18]));
    alpha5.setText(String.format("%.4f", array[19]));
  }

  private ColorMatrix getValueColorMatrix()
  {
    float value = valueBar.getProgress() - CONVERSION_VALUE_VALUE;
    ColorMatrix cm = new ColorMatrix();
    cm.set(new float[] {1, 0, 0, 0, value, 0, 1, 0, 0, value, 0, 0, 1, 0,
        value, 0, 0, 0, 1, 0});
    return cm;
  }

  private ColorMatrix getSaturationColorMatrix()
  {
    float sat = saturationBar.getProgress() / CONVERSION_VALUE_SATURATION;
    ColorMatrix cm = new ColorMatrix();
    cm.setSaturation(sat);
    return cm;
  }

  private ColorMatrix getHueColorMatrix()
  {
    float degrees = hueBar.getProgress() - CONVERSION_VALUE_HUE;
    ColorMatrix cm0 = new ColorMatrix();
    ColorMatrix cm1 = new ColorMatrix();
    ColorMatrix cm2 = new ColorMatrix();
    cm0.setRotate(0, degrees);
    cm1.setRotate(1, degrees);
    cm2.setRotate(2, degrees);
    cm0.postConcat(cm1);
    cm0.postConcat(cm2);
    return cm0;
  }

}
