package ua.boden.tester;

//import static com.boden.lingvolearner.services.ContextHolder.getLearningManager;

import java.io.IOException;
import java.io.InputStream;

//import com.boden.lingvolearner.pojo.WordCard;
//import com.boden.lingvolearner.services.ContextHolder;
//import com.boden.lingvolearner.services.DictionaryFileManipulator;
//import com.boden.lingvolearner.services.Stage;
//import com.boden.lingvolearner.sqlite.DBManager;
//import com.boden.lingvolearner.sqlite.DatabaseHelper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import ua.boden.tester.services.ContextHolder;
import ua.boden.tester.services.Stage;
import ua.boden.tester.sqlite.DBManager;
import ua.boden.tester.sqlite.DatabaseHelper;

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
        ContextHolder.registerUiUpdator(Stage.SELECTING_ANSWER, mainFormUiUpdator);
        ContextHolder.registerUiUpdator(Stage.WRITING_ANSWER, new WritingAnswersUiUpdator(this));

        ContextHolder.getUiUpdator(Stage.SELECTING_ANSWER).createNewActivity();
        ContextHolder.getInstance()
                .createSettingsHolder(new UserPreferencesAdapter(getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)));

        copyDefaultDbIfNotPresent();
        selectCategoryFromDB();
    }

    private void copyDefaultDbIfNotPresent() {
        DBManager dbManager = new DBManager(this);
        try {
            dbManager.open();
        } catch (android.database.sqlite.SQLiteException sqe) {
            dbManager.close();
            InputStream myInput;
            try {
                myInput = getAssets().open(DatabaseHelper.DB_NAME);
                dbManager.copyDBfile(myInput);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Menu getMenu() {
        return menu;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    private int getStartFromNumber() {
//        return ContextHolder.getSettingsHolder().getStartFromNumber();
//    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_OPTION_ACTIVITY:
                if (getLearningManager() != null)
                    getLearningManager().startLearning();
                break;
            case REQUEST_CODE_SELECT_DB:
                if (resultCode == Activity.RESULT_OK) {
                    getLearningManager().startLearning();
                }
                break;
        }
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
            case R.id.menu_open_db:
                selectCategoryFromDB();
                return true;
            case R.id.menu_settings:
                settings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void settings() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, OptionActivity.class);
        startActivityForResult(intent, REQUEST_CODE_OPTION_ACTIVITY);
    }

    private void selectCategoryFromDB() {

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SelectCategoryActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_DB);
    }

    private void exit() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.parameters);
        menu.add(R.string.smaller_size).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ContextHolder.getSettingsHolder().decreaseTextSize();
                mainFormUiUpdator.updateList();
                return true;
            }
        });
        menu.add(R.string.bigger_size).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ContextHolder.getSettingsHolder().increaseTextSize();
                mainFormUiUpdator.updateList();
                return true;
            }
        });
        menu.add(R.string.smaller_distance).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ContextHolder.getSettingsHolder().decreaseTextPadding();
                mainFormUiUpdator.updateList();
                return true;
            }
        });
        menu.add(R.string.bigger_distance).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ContextHolder.getSettingsHolder().increaseTextPadding();
                mainFormUiUpdator.updateList();
                return true;
            }
        });

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
