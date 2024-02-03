package ua.boden.tester.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static ua.boden.tester.services.ContextHolder.getSettingsHolder;

public class DBManager {

	private static final String WHERE_AND = " where ";
	private static final String JOIN_DICTIONARY = "left join words_dictionaryentry_dictionary as ed on e.id=ed.dictionaryentry_id ";
	private static final String SELECT_CATEGORY_ENTRY = "SELECT w.word,e.transcription,t.word,t.notes FROM words_dictionaryentry as e join words_word as w on w.id=e.word_id join words_word as t on t.id=e.translation_id ";
	private static final String JOIN_CATEGORY = "left join words_word_category as wc on w.id=wc.word_id ";
	private static final String TABLE_CATEGORIES = "category";
	private static final String TABLE_DICTIONARIES = "words_dictionary";
	private static final String TABLE_LANGUAGES = "words_language";
	public static final String ID = "id";
	public static final String CODE = "code";
	public static final String NAME = "name";
	public static final String DESC = "description";

	private DatabaseHelper dbHelper;

	private Context context;

	private SQLiteDatabase database;

	public DBManager(Context c) {
		context = c;
	}

	public DBManager open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		if (dbHelper != null)
			dbHelper.close();
	}

	public Cursor fetchCategories() {
		String[] columns = new String[] { ID, NAME, "supper_category_id" };
		Cursor cursor = database.query(TABLE_CATEGORIES, columns, null, null, null, null, null);
		return cursor;
	}

	public Cursor fetchEntitiesByCategory(String categoryId) {
		String query = "SELECT id, text, type_id"
				+ " FROM question as q JOIN question_categories as qc on q.id=qc.question_id" + WHERE_AND
				+ "qc.category_id=?;";
		Cursor cursor = database.rawQuery(query, new String[] { categoryId });
		return cursor;
	}

	public Cursor fetchAnswersByQuestion(String questionId) {
		String query = "SELECT a.value, a.is_correct"
				+ " FROM question_answers as qa JOIN answer as a ON qa.answers_id=a.id" + WHERE_AND
				+ "question_id=?;";
		Cursor cursor = database.rawQuery(query, new String[] { questionId });
		return cursor;
	}

	// public Cursor fetchEntitiesWithoutCategory() {
	//
	// String query = SELECT_DICTIONARY_ENTRY + JOIN_CATEGORY
	// + String.format(WHERE_AND, getLanguagesCondition(), "wc.category_id is
	// null");
	// Cursor cursor = database.rawQuery(query, null);
	// return cursor;
	// }

	// public Cursor fetchEntitiesByDictionary(String dictionaryId) {
	//
	// String query = SELECT_DICTIONARY_ENTRY + JOIN_DICTIONARY
	// + String.format(WHERE_AND, getLanguagesCondition(),
	// "ed.dictionary_id=?");
	// Cursor cursor = database.rawQuery(query, new String[] { dictionaryId });
	// return cursor;
	// }
	//
	// public Cursor fetchEntitiesWithoutDictionary() {
	//
	// String query = SELECT_DICTIONARY_ENTRY + JOIN_DICTIONARY
	// + String.format(WHERE_AND, getLanguagesCondition(), "ed.dictionary_id is
	// null");
	// Cursor cursor = database.rawQuery(query, null);
	// return cursor;
	// }

	public void copyDBfile(InputStream inputStream) throws FileNotFoundException, IOException {
		String dbPath = "/data/data/" + context.getApplicationContext().getPackageName() + "/databases/"
				+ DatabaseHelper.DB_NAME;
		File dbFile = new File(dbPath);
		if (!dbFile.getParentFile().exists()) {
			dbFile.getParentFile().mkdirs();
		}
		// if (!dbFile.exists()) {
		dbFile.getParentFile().mkdirs();
		copyStream(inputStream, new FileOutputStream(dbFile));
		// }

	}

	private void copyStream(InputStream is, OutputStream os) throws IOException {
		try {
			byte buf[] = new byte[1024];
			int c = 0;
			while (true) {
				c = is.read(buf);
				if (c == -1)
					break;
				os.write(buf, 0, c);
			}
		} finally {
			is.close();
			os.close();
		}
	}
}
