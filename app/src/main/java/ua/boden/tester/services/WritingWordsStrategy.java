package ua.boden.tester.services;

public class WritingWordsStrategy extends AbstractStrategy {

	@Override
	public Stage getPreviousStage() {
		return Stage.SELECTING_ANSWER;
	}

	@Override
	public Stage getNextStage() {
		return Stage.SELECTING_ANSWER;
	}

}
