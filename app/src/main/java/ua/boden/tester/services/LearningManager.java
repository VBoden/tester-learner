package ua.boden.tester.services;

import ua.boden.tester.pojo.Answer;
import ua.boden.tester.pojo.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static ua.boden.tester.services.ContextHolder.getSettingsHolder;

public class LearningManager {

	public static final int MAX_POSSIBLE_ANSWERS = 10;
	private Stage currentStage;
	private Map<Stage, AbstractStrategy> stagesStrategies;

	private List<Question> allQuestions;
	private int currentCartdNum;

	private int questionsCount, krok_povtory, k_zal_sliv;
	private int kilk[] = new int[MAX_POSSIBLE_ANSWERS];
	private List<String> spisok = new ArrayList<>();
	private Random rnd = new Random();

	public LearningManager(List<Question> allQuestions) {
		this.allQuestions = allQuestions;
		questionsCount = allQuestions.size();
		createStagesMap();
		currentStage = Stage.FOREIGN_TO_NATIVE;
	}

	private void createStagesMap() {
		stagesStrategies = new HashMap<>();
		stagesStrategies.put(Stage.FOREIGN_TO_NATIVE, new ForeignToNativeStrategy());
		stagesStrategies.put(Stage.WRITING_WORDS, new WritingWordsStrategy());
	}

	public AbstractStrategy getCurrentStrategy() {
		return stagesStrategies.get(currentStage);
	}

	public int getTotalWordsCount() {
		return allQuestions.size();
	}

	public Question getCurrentCard() {
		return allQuestions.get(currentCartdNum);
	}

	public void startLearning() {
		currentStage = Stage.getFirst();
		startNewStage();
	}

	public void startNewStage() {
		krok_povtory = getSettingsHolder().getRepeatCount();
		resetCycle();
		changeCurrentCardNum();
		updateWordChoices();
		if (Objects.nonNull(ContextHolder.getUiUpdator(currentStage))) {
			ContextHolder.getUiUpdator(currentStage).updateOnStageStart();
		}
	}

	public boolean startNextStage() {
		boolean startedFromBegining = false;
		boolean newPortionStarted = false;
		if (currentStage.isLast()) {
			int startFrom = ContextHolder.getSettingsHolder().getStartFromNumber();
//			if (startFrom == rozmir_mas - WORDS_IN_CYCLE) {
				startFrom = 0;
				startedFromBegining = true;
//			} else {
//				startFrom = Math.min(startFrom + WORDS_IN_CYCLE, rozmir_mas - WORDS_IN_CYCLE);
//			}
			ContextHolder.getSettingsHolder().updateStartNumber(startFrom);
			newPortionStarted = true;
		}
		if (currentStage.getNext().isLast()) {
			ContextHolder.getUiUpdator(currentStage.getNext()).createNewActivity();
		}
		currentStage = currentStage.getNext();
		startNewStage();
		if (newPortionStarted && Objects.nonNull(ContextHolder.getUiUpdator(currentStage))) {
			ContextHolder.getUiUpdator(currentStage).updateUiOnNewPortionStarted();
		}
		return startedFromBegining;
	}

	public void startPreviousStage() {
		boolean newPortionStarted = false;
		if (currentStage.isFirst()) {
			int startFrom = ContextHolder.getSettingsHolder().getStartFromNumber();
			startFrom = 0;// Math.max(startFrom - WORDS_IN_CYCLE, 0);
			ContextHolder.getSettingsHolder().updateStartNumber(startFrom);
			newPortionStarted = true;
			ContextHolder.getUiUpdator(currentStage.getPrevious()).createNewActivity();
		}
		currentStage = currentStage.getPrevious();
		startNewStage();
		if (newPortionStarted && Objects.nonNull(ContextHolder.getUiUpdator(currentStage))) {
			ContextHolder.getUiUpdator(currentStage).updateUiOnNewPortionStarted();
		}
	}

	public void checkAnswer(String answer) {
		int startFrom = ContextHolder.getSettingsHolder().getStartFromNumber();
		if (answer.equals(getWordAnswer())) {
			k_zal_sliv--;
			kilk[currentCartdNum - startFrom]++;
			if (k_zal_sliv == 0) {
				krok_povtory--;
				resetCycle();
			}
			if (krok_povtory > 0) {
				changeCurrentCardNum();
				updateWordChoices();
				ContextHolder.getUiUpdator(currentStage).updateWord();
			} else {
				if (currentStage.getNext().isLast()) {
					ContextHolder.getUiUpdator(currentStage.getNext()).createNewActivity();
				}
				ContextHolder.getUiUpdator(currentStage).updateOnStageEnd();
				startNextStage();
			}
		} else {
			k_zal_sliv++;
			kilk[currentCartdNum - startFrom]--;
			ContextHolder.getUiUpdator(currentStage).showHint(getWordToDisplay(), getWordAnswer());
		}
	}

	private void changeCurrentCardNum() {
		int startFromNumber = getStartFromNumber();
		do {
			currentCartdNum = startFromNumber + rnd.nextInt(100) % allQuestions.size();
		} while (kilk[currentCartdNum - startFromNumber] >= 1);
	}

	private int getStartFromNumber() {
		return getSettingsHolder().getStartFromNumber();
	}

	private void resetCycle() {
		k_zal_sliv = questionsCount;// WORDS_IN_CYCLE;
		for (int i = 0; i < MAX_POSSIBLE_ANSWERS; i++) {
			kilk[i] = 0;
		}
	}

	public void updateWordChoices() {
		List<Answer> answers = allQuestions.get(currentCartdNum).getAnswers();
		spisok.clear();
		for (int i = 0; i < answers.size(); i++) {
			spisok.add(answers.get(i).getValue());
		}
	}

	public String getWordToDisplay() {
		return getCurrentStrategy().getWordToDisplay(getCurrentCard());
	}

	public String getWordAnswer() {
		return getCurrentStrategy().getWordToCheck(getCurrentCard());
	}

	public String[] getWordChoices() {
		return spisok.toArray(new String[]{});
	}

	public Stage getCurrentStage() {
		return currentStage;
	}

	public Question getWordCard(int index) {
		return allQuestions.get(index);
	}

	public List<Question> getAllWordCards() {
		return allQuestions;
	}

	public boolean hasPreviousStep() {
		return !(/*ContextHolder.getSettingsHolder().getStartFromNumber() == 0 &&*/ currentStage.isFirst());
	}
}
