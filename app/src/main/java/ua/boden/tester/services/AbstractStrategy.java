package ua.boden.tester.services;

import ua.boden.tester.pojo.Question;

public abstract class AbstractStrategy {

	public abstract Stage getPreviousStage();

	public abstract Stage getNextStage();

	public String getWordToDisplay(Question question){
		return question.getText();
	}

	public String getWordToCheck(Question question){
		return question.getAnswers().stream().filter(ans -> ans.isCorrect()).map(ans-> ans.getValue()).findAny().get();
	};
}
