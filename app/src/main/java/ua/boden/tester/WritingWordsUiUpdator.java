package ua.boden.tester;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ua.boden.tester.services.ContextHolder;
import ua.boden.tester.services.UiUpdator;

import static ua.boden.tester.services.ContextHolder.getLearningManager;

public class WritingWordsUiUpdator implements UiUpdator {

	private TextView questionText;
	private EditText questionAnswerField;
	private MainActivity mainActivity;

	public WritingWordsUiUpdator(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	public void updateWord() {
		questionText.setText(getLearningManager().getWordToDisplay());
		questionAnswerField.setText("");
	}

	@Override
	public void updateUiOnNewPortionStarted() {
		int startFromNumber = ContextHolder.getSettingsHolder().getStartFromNumber();
		Context context = mainActivity.getApplicationContext();
		Toast toast = Toast.makeText(context,
				mainActivity.getResources().getString(R.string.words) /*+ getLearningManager().getWordCard(startFromNumber).getWord()
						+ "-" + getLearningManager().getWordCard(startFromNumber + 9).getWord() + " ("*/
						+ (startFromNumber + 1) + "-" + (startFromNumber + 10) /*+ ")"*/,
				Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void updateOnStageStart() {
		updateWord();
	}

	@Override
	public void createNewActivity() {
		mainActivity.setContentView(R.layout.form3);

		questionText = (TextView) mainActivity.findViewById(R.id.word3);
		questionAnswerField = (EditText) mainActivity.findViewById(R.id.word2);

		final Button answerButton = (Button) mainActivity.findViewById(R.id.but0);
		answerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ContextHolder.getLearningManager().checkAnswer(questionAnswerField.getText().toString());
			}
		});
		updateOnStageStart();
	}

	@Override
	public void updateOnStageEnd() {
		ContextHolder.getUiUpdator(ContextHolder.getLearningManager().getCurrentStage().getNext()).createNewActivity();
	}

	@Override
	public void showHint(String word, String answer) {
		Context context = mainActivity.getApplicationContext();
		Toast toast = Toast.makeText(context, word + " - " + answer, Toast.LENGTH_SHORT);
		toast.show();
	}
}
