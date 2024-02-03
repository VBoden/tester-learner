package ua.boden.tester;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ua.boden.tester.sqlite.DBManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ua.boden.tester.services.ContextHolder.getSettingsHolder;

public class OptionActivity extends Activity {
	private static final int REQUEST_CODE_SELECT_DB = 1;


	private EditText edit2;
	private List<String> languages = new ArrayList<>();
	private List<String> languageCodes = new ArrayList<>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);
		setUpRepeatCountField();
		setUpUpdateDB();
	}

	private void setUpUpdateDB() {
		Button updateDB = findViewById(R.id.updateDB);
		updateDB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("*/*");
				try {
					startActivityForResult(intent, REQUEST_CODE_SELECT_DB);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}




	private void setUpRepeatCountField() {
		edit2 = (EditText) findViewById(R.id.edit2);
		edit2.setText("" + getSettingsHolder().getRepeatCount());
		edit2.setInputType(InputType.TYPE_CLASS_NUMBER);
		// Log.i("DEBUG_OPTION_ACTIVITY","1");
		edit2.setSelection(edit2.getText().toString().length());
		edit2.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (edit2.getText().length() > 0) {
					getSettingsHolder().setRepeatCount(Integer.parseInt(edit2.getText().toString()));
				}
			}
		});
	}



	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
//		case REQUEST_CODE_SELECT_DIR:
//			if (resultCode == RESULT_OK) {
//				Bundle extras = data.getExtras();
//				String result = extras.getString(EXT_NAME_DIR) + "/";
//				if (result != null) {
//					pathToSoundFilesField.setText(result);
//					getSettingsHolder().setPathToSoundFiles(result);
//				}
//			}
//			break;
		case REQUEST_CODE_SELECT_DB:
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				DBManager dbManager = new DBManager(this);
				dbManager.close();
				try {
					dbManager.copyDBfile(getContentResolver().openInputStream(uri));
					Toast toast = Toast.makeText(getApplicationContext(), "Базу успішно оновлено.", Toast.LENGTH_SHORT);
					toast.show();
				} catch (IOException e) {
					e.printStackTrace();
					Toast toast = Toast.makeText(getApplicationContext(), "Проблеми із читанням/записом файлу!",
							Toast.LENGTH_SHORT);
					toast.show();
				} catch (Exception e) {
					e.printStackTrace();
					Toast toast = Toast.makeText(getApplicationContext(), "Не вдалось оновити базу!",
							Toast.LENGTH_SHORT);
					toast.show();
				} finally {
					dbManager.close();
				}
			}
			break;
		}
	}
}
