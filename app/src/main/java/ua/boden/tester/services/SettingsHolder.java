package ua.boden.tester.services;

public class SettingsHolder {
	public static final String REPEAT_COUNT = "repeatCount";
	public static final String TEXT_SIZE = "textSize";
	public static final String TEXT_PADDING = "textPadding";

	private UserPreferences userPrefs;
	private int textPadding;
	private float textSize;
	private int startFromNumber;
	private int repeatCount;

	public SettingsHolder(UserPreferences userPrefs) {
		this.userPrefs = userPrefs;
		init();
	}

	private void init() {
		textSize = userPrefs.getFloat(TEXT_SIZE, 20);
		textPadding = userPrefs.getInt(TEXT_PADDING, 10);
		repeatCount = userPrefs.getInt(REPEAT_COUNT, 5);

//		getDictsFromSettings();
//		if (userPrefs.contains(DICTIONARIES) == true) {
//			String s = userPrefs.getString(DICTIONARIES, "");
//			if (s.length() > 0) {
//				// Log.i("DEBUG_INFO_MY", "length>0");
//				dict = new Dict(s.substring(s.indexOf("<dict>") + 6, s.indexOf("</dict>")));
//				// Log.i("DEBUG_INFO_MY", "was created Dict");
//
//				startFromNumber = dict.getBeginFrom();
//				// Log.i("DEBUG_INFO_MY", "now started loadDict");
//
//			}
//		}
	}

	public void updateStartNumber(int startFromNumber) {
//		int totalWords = ContextHolder.getLearningManager().getTotalWordsCount();
//		if (startFromNumber + 10 > totalWords) {
//			startFromNumber = totalWords - 10;
//		}
		this.startFromNumber = 0;// startFromNumber;
//		updateDictionatyStartNumber(startFromNumber);
	}

	public void decreaseTextSize() {
		textSize -= 2;
		saveTextSize();
	}

	public void increaseTextSize() {
		textSize += 2;
		saveTextSize();
	}

	private void saveTextSize() {
		userPrefs.saveFloat(TEXT_SIZE, textSize);
	}

	public void decreaseTextPadding() {
		textPadding -= 1;
		saveTextPadding();
	}

	public void increaseTextPadding() {
		textPadding += 1;
		saveTextPadding();
	}

	private void saveTextPadding() {
		userPrefs.saveInt(TEXT_PADDING, textPadding);
	}

	public int getTextPadding() {
		return textPadding;
	}

	public void setTextPadding(int textPadding) {
		this.textPadding = textPadding;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public int getStartFromNumber() {
		return startFromNumber;
	}

	public void setStartFromNumber(int startFrom) {
		this.startFromNumber = startFrom;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
		userPrefs.saveInt(REPEAT_COUNT, repeatCount);
	}

}
