package ua.boden.tester;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ua.boden.tester.services.ContextHolder;
import ua.boden.tester.services.UiUpdator;

import static ua.boden.tester.services.ContextHolder.getLearningManager;

@TargetApi(30)
public class MainFormUiUpdator implements UiUpdator {
	private TextView Vtransc;
	private TextView Vword;
	private ListView listView;
	private MainActivity mainActivity;
	private ArrayAdapter<String> adapt;

	public MainFormUiUpdator(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	private int getStartFromNumber() {
		return ContextHolder.getSettingsHolder().getStartFromNumber();
	}

	public void chooseAndFillNativeWord() {
		Vword.setText(getLearningManager().getWordToDisplay());
	}

	public void listSetAdapter() {
		adapt = new ArrayAdapter<String>(mainActivity, R.layout.list_item,
				getLearningManager().getWordChoices()) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row;

				if (null == convertView) {
					LayoutInflater mInflater = mainActivity.getLayoutInflater();
					row = mInflater.inflate(R.layout.list_item, null);
				} else {
					row = convertView;
				}

				TextView tv = (TextView) row.findViewById(R.id.textView1);
				float textSize = ContextHolder.getSettingsHolder().getTextSize();
				if (textSize == 0) {
					textSize = tv.getTextSize();
				}
				tv.setTextSize(textSize);
				int textPadding = ContextHolder.getSettingsHolder().getTextPadding();
				if (textPadding == 0) {
					textPadding = tv.getPaddingBottom();
				}
				tv.setPadding(0, textPadding, 0, textPadding);
				tv.setText(Html.fromHtml(getItem(position)));
				return row;
			}
		};
		listView.setAdapter(adapt);
	}

	public void updateList() {
		adapt.notifyDataSetChanged();
	}

	@Override
	public void updateUiOnNewPortionStarted() {
		int startFromNumber = getStartFromNumber();
		Context context = mainActivity.getApplicationContext();
//		Toast toast = Toast.makeText(context,
//				mainActivity.getResources().getString(R.string.words) + /*ContextHolder.getAllWordCards().get(startFromNumber).getWord() + "-"
//						+ ContextHolder.getAllWordCards().get(startFromNumber + 9).getWord() + " ("*/ + (startFromNumber + 1) + "-"
//						+ (startFromNumber + 10) /*+ ")"*/,
//				Toast.LENGTH_SHORT);
//		toast.show();
	}

	@Override
	public void updateWord() {
		Vword.setText(getLearningManager().getWordToDisplay());
		listSetAdapter();
	}

	@Override
	public void updateOnStageStart() {
		Vword.setText(getLearningManager().getWordToDisplay());
		listSetAdapter();
//		mainActivity.getMenu().getItem(0).setEnabled(getLearningManager().hasPreviousStep());
	}

	@Override
	public void createNewActivity() {
		mainActivity.setContentView(R.layout.main_window);
		mainActivity.setTitle(R.string.app_name2);

		Vword = (TextView) mainActivity.findViewById(R.id.word);
		Vword.setText(R.string.selectdict);
		Vtransc = (TextView) mainActivity.findViewById(R.id.transcription);
		listView = (ListView) mainActivity.findViewById(R.id.list);
		mainActivity.registerForContextMenu(listView);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View itemClicked, int position, long id) {
				getLearningManager().checkAnswer(getLearningManager().getWordChoices()[position]);
			}
		});
	}

	@Override
	public void updateOnStageEnd() {
		// TODO Auto-generated method stub
	}

	@Override
	public void showHint(String word, String answer) {
		Context context = mainActivity.getApplicationContext();
		Toast toast = Toast.makeText(context, word + " - " + answer, Toast.LENGTH_SHORT);
		toast.show();
	}
}
