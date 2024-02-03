package ua.boden.tester.services;

public interface UiUpdator {
	void updateWord();

	void updateUiOnNewPortionStarted();

	void updateOnStageStart();

	void updateOnStageEnd();

	void createNewActivity();

	void showHint(String word, String answer);
}
