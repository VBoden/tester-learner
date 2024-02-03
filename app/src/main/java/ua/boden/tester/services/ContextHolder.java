package ua.boden.tester.services;

import ua.boden.tester.pojo.Category;
import ua.boden.tester.pojo.Question;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ContextHolder {

	private static ContextHolder instance;

	private static final Map<Stage, UiUpdator> STAGES_UI_UPDATORS = new HashMap<>();
	private LearningManager learningManager;
	private SettingsHolder settingsHolder;
	private String[] categoriesList;
//	private String[] dictionaries;
//	private List<List<Question>> loadedWordCards = new ArrayList<>();
	private Map<Integer, Category> idToCategory;

	public static ContextHolder getInstance() {
		if (Objects.isNull(instance)) {
			instance = new ContextHolder();
		}
		return instance;
	}

	public LearningManager createLearningManager(List<Question> allWordCards) {
		this.learningManager = new LearningManager(allWordCards);
		return learningManager;
	}

	public SettingsHolder createSettingsHolder(UserPreferences userPreferences) {
		this.settingsHolder = new SettingsHolder(userPreferences);
		return settingsHolder;
	}

	public static LearningManager getLearningManager() {
		return getInstance().learningManager;
	}

	public static SettingsHolder getSettingsHolder() {
		return getInstance().settingsHolder;
	}

	public static void registerUiUpdator(Stage stage, UiUpdator updator) {
		STAGES_UI_UPDATORS.put(stage, updator);
	}

	public static UiUpdator getUiUpdator(Stage stage) {
		return STAGES_UI_UPDATORS.get(stage);
	}

	public static List<Question> getAllWordCards() {
		return getLearningManager().getAllWordCards();
	}

	public String[] getCategories() {
		return categoriesList;
	}

	public void setCategoriesList(String[] categories) {
		this.categoriesList = categories;
	}

	public Map<Integer, Category> getIdToCategory() {
		return idToCategory;
	}

	public void setIdToCategory(Map<Integer, Category> idToCategory) {
		this.idToCategory = idToCategory;
	}

//	public String[] getDictionaries() {
//		return dictionaries;
//	}
//
//	public void setDictionaries(String[] dictionaries) {
//		this.dictionaries = dictionaries;
//	}
//
//	public List<List<Question>> getLoadedWordCards() {
//		return loadedWordCards;
//	}
//
//	public void setLoadedWordCards(List<List<Question>> loadedWordCards) {
//		this.loadedWordCards = loadedWordCards;
//	}

}
