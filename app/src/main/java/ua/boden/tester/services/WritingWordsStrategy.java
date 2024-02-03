package ua.boden.tester.services;

public class WritingWordsStrategy extends AbstractStrategy {

	@Override
	public Stage getPreviousStage() {
		return Stage.FOREIGN_TO_NATIVE;
	}

	@Override
	public Stage getNextStage() {
		return Stage.FOREIGN_TO_NATIVE;
	}

}
