package com.GoTravel.Go1978;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class EditTextActivity extends BaseActivity
{

  EditText et;
  Button btn;
  String text = null;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_text);

    Intent intent = getIntent();
    Bundle bundle = intent.getExtras();
    if(bundle != null)
    {
      text = bundle.getString(PictureEditorActivity.BUNDLE_TEXT);
    }

    et = (EditText) findViewById(R.id.text);
    if(text != null)
    {
      et.setText(text);
    }
    btn = (Button) findViewById(R.id.ok);
    btn.setOnClickListener(new OnClickListener()
    {

      public void onClick(View v)
      {
        text = et.getText().toString();
        Intent intent =
            new Intent(EditTextActivity.this, PictureEditorActivity.class);
        intent.putExtra(PictureEditorActivity.BUNDLE_TEXT, text);
        setResult(RESULT_OK, intent);
        finish();
      }
    });
  }

}
