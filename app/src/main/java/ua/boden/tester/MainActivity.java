package ua.boden.tester;

//import static com.boden.lingvolearner.services.ContextHolder.getLearningManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

//import com.boden.lingvolearner.pojo.WordCard;
//import com.boden.lingvolearner.services.ContextHolder;
//import com.boden.lingvolearner.services.DictionaryFileManipulator;
//import com.boden.lingvolearner.services.Stage;
//import com.boden.lingvolearner.sqlite.DBManager;
//import com.boden.lingvolearner.sqlite.DatabaseHelper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ua.boden.tester.services.ContextHolder;
import ua.boden.tester.services.Stage;

import static ua.boden.tester.services.ContextHolder.getLearningManager;

@TargetApi(30)
public class MainActivity extends Activity {

    private static final int REQUEST_CODE_OPTION_ACTIVITY = 2;
    private static final int REQUEST_CODE_SELECTDICT = 3;
    private static final int REQUEST_CODE_SELECT_DB = 4;
    private static final int REQUEST_CODE_PLAYER_ACTIVITY = 5;
    private static final int REQUEST_CODE_LAST_OPEND_ACTIVITY = 3;
    private static final int IDD_SET_START_NUMBER = 1;

    public static final String APP_PREFERENCES = "TesterLearnerPrefs";
    private Menu menu;

    private MainFormUiUpdator mainFormUiUpdator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_window);
        setTitle(R.string.app_name2);
        mainFormUiUpdator = new MainFormUiUpdator(this);
        ContextHolder.registerUiUpdator(Stage.FOREIGN_TO_NATIVE, mainFormUiUpdator);
//        ContextHolder.registerUiUpdator(Stage.NATIVE_TO_FOREIGN, mainFormUiUpdator);
        ContextHolder.registerUiUpdator(Stage.WRITING_WORDS, new WritingWordsUiUpdator(this));
//
        ContextHolder.getUiUpdator(Stage.FOREIGN_TO_NATIVE).createNewActivity();
        ContextHolder.getInstance()
                .createSettingsHolder(new UserPreferencesAdapter(getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)));

//        copyDefaultDbIfNotPresent();
//        selectDictionaryFromDB();
        selectCategoryFromDB();
    }

//    private void copyDefaultDbIfNotPresent() {
//        DBManager dbManager = new DBManager(this);
//        try {
//            dbManager.open();
//            dbManager.fetchLanguages();
//        } catch (android.database.sqlite.SQLiteException sqe) {
//            dbManager.close();
//            InputStream myInput;
//            try {
//                myInput = getAssets().open(DatabaseHelper.DB_NAME);
//                dbManager.copyDBfile(myInput);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public Menu getMenu() {
        return menu;
    }

    private void startDictFileSelection() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        try {
            startActivityForResult(intent, REQUEST_CODE_SELECTDICT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startDictFileSelectionForSamsung() {
        Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        intent.putExtra("CONTENT_TYPE", "*/*");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        try {
            startActivityForResult(intent, REQUEST_CODE_SELECTDICT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
//        ContextHolder.getWordSpeaker().destroy();
        super.onDestroy();
    }

//    private int getStartFromNumber() {
//        return ContextHolder.getSettingsHolder().getStartFromNumber();
//    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
//            case REQUEST_CODE_OPTION_ACTIVITY:
//                if (getLearningManager() != null)
//                    getLearningManager().startLearning();
//                break;
//            case REQUEST_CODE_SELECTDICT:
//                if (resultCode == RESULT_OK) {
//                    loadDictionary(data.getData());
//                }
//                break;
            case REQUEST_CODE_SELECT_DB:
                if (resultCode == Activity.RESULT_OK) {
                    getLearningManager().startLearning();
                } else if (resultCode == 2) {
                    startDictFileSelection();
                }
                break;
        }
    }

    private boolean loadDictionary(Uri uri) {
        System.out.println("uri=" + uri);
        try {
//            List<WordCard> allWordCards = DictionaryFileManipulator
//                    .loadDictionaryByLines(getContentResolver().openInputStream(uri));
//            ContextHolder.getInstance().createLearningManager(allWordCards);
//            getLearningManager().startLearning();
        } catch (Exception e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(getApplicationContext(), "Не вдалось відкрити словник!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_exit:
                exit();
                return true;
//            case R.id.menu_help:
//                help();
//                return true;
//            case R.id.menu_dict_preview:
//                previewWholeDcitionary();
//                return true;
            case R.id.menu_next_step:
                if (getLearningManager().getCurrentStage().isLast()) {
                    ContextHolder.getUiUpdator(getLearningManager().getCurrentStage().getNext()).createNewActivity();
                }
                boolean endReached = getLearningManager().startNextStage();
                if (endReached) {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, getResources().getString(R.string.end_of_dict),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                return true;
            case R.id.menu_prev_step:
                if (getLearningManager().getCurrentStage().isLast()) {
                    ContextHolder.getUiUpdator(getLearningManager().getCurrentStage().getPrevious()).createNewActivity();
                }
                getLearningManager().startPreviousStage();
                return true;
//            case R.id.menu_open_db:
//                selectDictionaryFromDB();
//                return true;
            case R.id.menu_open_db:
                selectCategoryFromDB();
                return true;
//            case R.id.menu_open:
//                startDictFileSelection();
//                return true;
            case R.id.menu_settings:
                settings();
                return true;
//            case R.id.menu_player:
//                Intent intent = new Intent();
//                intent.setClass(MainFormActivity.this, PlayerActivity.class);
//                startActivityForResult(intent, REQUEST_CODE_PLAYER_ACTIVITY);
//                return true;
//
            default:
                return super.onOptionsItemSelected(item);
        }
//        return true;
    }

    private void settings() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, OptionActivity.class);
        startActivityForResult(intent, REQUEST_CODE_OPTION_ACTIVITY);

        System.out.println("setting closed");// ???????????
    }

    // private void lastOpened() {
    // Intent theIntent = new Intent();
    // theIntent.setClass(MainFormActivity.this, LastOpendActivity.class);
    // try {
    // startActivityForResult(theIntent, REQUEST_CODE_LAST_OPEND_ACTIVITY);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    private void help() {
//        Intent intent = new Intent();
//        intent.setClass(MainFormActivity.this, HelpActivity.class);
//        intent.putExtra(HelpActivity.CONTENT, getHelpFileContent());
//        startActivity(intent);
    }

//    private String getHelpFileContent() {
//        InputStream iFile;
//        String s;
//        try {
//            iFile = getResources().openRawResource(R.raw.help);
//            InputStreamReader tmp = new InputStreamReader(iFile, "UTF8");
//            BufferedReader dataIO = new BufferedReader(tmp);
//            StringBuffer sBuffer = new StringBuffer();
//            String strLine = null;
//            while ((strLine = dataIO.readLine()) != null) {
//                sBuffer.append(strLine);
//            }
//            dataIO.close();
//            iFile.close();
//            s = sBuffer.toString();
//        } catch (Exception e) {
////            s = getResources().getString(R.string.coud_not_open_help);
//        }
//        return s;
//    }

//    private void previewWholeDcitionary() {
//        Intent intent = new Intent();
//        intent.setClass(MainFormActivity.this, HelpActivity.class);
//        intent.putExtra(HelpActivity.CONTENT, getDictViewContent());
//        startActivity(intent);
//    }

//    private void selectDictionaryFromDB() {
//
//        Intent intent = new Intent();
//        intent.setClass(MainFormActivity.this, SelectDbDictionaryActivity.class);
//        // intent.putExtra(HelpActivity.CONTENT, getDictViewContent());
//        // startActivity(intent);
//        startActivityForResult(intent, REQUEST_CODE_SELECT_DB);
//    }

    private void selectCategoryFromDB() {

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SelectCategoryActivity.class);
        // intent.putExtra(HelpActivity.CONTENT, getDictViewContent());
        // startActivity(intent);
        startActivityForResult(intent, REQUEST_CODE_SELECT_DB);
    }
//
//    private String getDictViewContent() {
//        StringBuilder sb = new StringBuilder("<ol>");
//        ContextHolder.getAllWordCards().forEach(card -> {
//            sb.append("<li>" + card.toString() + "</li>");
//        });
//        sb.append("</ol>");
//        return sb.toString();
//    }

    private void exit() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//        menu.setHeaderTitle(R.string.parameters);
//        menu.add(R.string.smaller_size).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                ContextHolder.getSettingsHolder().decreaseTextSize();
//                mainFormUiUpdator.updateList();
//                return true;
//            }
//        });
//        menu.add(R.string.bigger_size).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                ContextHolder.getSettingsHolder().increaseTextSize();
//                mainFormUiUpdator.updateList();
//                return true;
//            }
//        });
//        menu.add(R.string.smaller_distance).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                ContextHolder.getSettingsHolder().decreaseTextPadding();
//                mainFormUiUpdator.updateList();
//                return true;
//            }
//        });
//        menu.add(R.string.bigger_distance).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                ContextHolder.getSettingsHolder().increaseTextPadding();
//                mainFormUiUpdator.updateList();
//                return true;
//            }
//        });
//
//        menu.add(R.string.start_from).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                showDialog(IDD_SET_START_NUMBER);
//                return true;
//            }
//        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
//        switch (id) {
//            case IDD_SET_START_NUMBER:
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage(R.string.start_from_number);
//                final EditText inputStartNumber = new EditText(this);
//                inputStartNumber.setText("" + (getStartFromNumber() + 1));
//                inputStartNumber.setFocusable(true);
//                inputStartNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
//                inputStartNumber.setSelection(inputStartNumber.getText().toString().length());
//                builder.setView(inputStartNumber);
//
//                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        String newStartNumber = inputStartNumber.getText().toString();
//                        // Log.i("DEBUG_lAST","Name1="+newStartNumber);
//                        // Log.i("DEBUG_lAST","Name2="+newStartNumber);
//                        if (newStartNumber.length() > 0) {
//                            try {
//                                int startFromNumber = Integer.parseInt(inputStartNumber.getText().toString()) - 1;
//                                if (startFromNumber < 0) {
//                                    throw new Exception();
//                                }
//                                // Log.i("DEBUG_lAST", "startFromNumber=" +
//                                // startFromNumber);
//
//                                ContextHolder.getSettingsHolder().updateStartNumber(startFromNumber);
//                                getLearningManager().startLearning();
//
//                            } catch (Exception e) {
//                                Toast toast = Toast.makeText(MainFormActivity.this,
//                                        getResources().getString(R.string.wrong_number), Toast.LENGTH_SHORT);
//                                toast.show();
//                            }
//                        }
//                        dialog.dismiss();
//                        MainFormActivity.this.removeDialog(IDD_SET_START_NUMBER);
//                    }
//                });
//                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//                builder.setCancelable(false);
//                return builder.create();
//
//            default:
                return null;
//        }
    }
}
