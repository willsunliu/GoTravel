package com.example.demohsv;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class HSVDatabaseHelper extends SQLiteOpenHelper
{

  public static final String TABLE_NAME = "hsv";
  public static final String NAME = "name";
  public static final String HUE = "hue";
  public static final String SATURATION = "saturation";
  public static final String VALUE = "value";

  public static final String MATRIX = "matrix";

//  private final String CREATE_TABLE_SQL = "create table " + TABLE_NAME
//      + "(_id integer primary key autoincrement, " + NAME + ", " + "HUE" + ", "
//      + SATURATION + ", " + VALUE + ", " + RED1 + ", " + RED2 + ", " + RED3
//      + ", " + RED4 + ", " + RED5 + ", " + GREEN1 + ", " + GREEN2 + ", "
//      + GREEN3 + ", " + GREEN4 + ", " + GREEN5 + ", " + BLUE1 + ", " + BLUE2
//      + ", " + BLUE3 + ", " + BLUE4 + ", " + BLUE5 + ", " + ALPHA1 + ", "
//      + ALPHA2 + ", " + ALPHA3 + ", " + ALPHA4 + ", " + ALPHA5 + ")";
  private final String CREATE_TABLE_SQL = "create table " + TABLE_NAME
      + "(_id integer primary key autoincrement, " + NAME + ", " + HUE + ", "
      + SATURATION + ", " + VALUE + ", " + MATRIX + ")";

  public HSVDatabaseHelper(Context context, String name, int version)
  {
    super(context, name, null, version);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void onCreate(SQLiteDatabase db)
  {
    db.execSQL(CREATE_TABLE_SQL);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    // TODO Auto-generated method stub

  }

}
