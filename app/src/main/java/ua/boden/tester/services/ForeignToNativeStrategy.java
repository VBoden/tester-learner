package ua.boden.tester.services;

public class ForeignToNativeStrategy extends AbstractStrategy {

	@Override
	public Stage getPreviousStage() {
		return Stage.WRITING_ANSWER;
	}

	@Override
	public Stage getNextStage() {
		return Stage.WRITING_ANSWER;
	}

}
