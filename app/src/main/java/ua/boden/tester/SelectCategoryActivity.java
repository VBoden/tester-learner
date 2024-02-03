package ua.boden.tester;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.boden.tester.pojo.Answer;
import ua.boden.tester.pojo.Category;
import ua.boden.tester.pojo.Question;
import ua.boden.tester.services.ContextHolder;
import ua.boden.tester.sqlite.DBManager;

public class SelectCategoryActivity extends Activity {
	private static final String ID_SEP = "=id=";
	private ListView listView;
	private DBManager dbManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_category);
		listView = (ListView) findViewById(R.id.list);

		dbManager = new DBManager(this);
		dbManager.open();
		Cursor cursor = dbManager.fetchCategories();
		String[] categories = fetchList("defaultTitle", cursor);
		ContextHolder.getInstance().setCategories(categories);
		listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_left,
				removeIds(ContextHolder.getInstance().getCategories())));

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View itemClicked, int position, long id) {

				List<Question> allWordCards = loadFromDB(position);
				boolean isLoaded = allWordCards.size() > 0;
				if (!isLoaded) {
					Toast toast = Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.noWordsFound), Toast.LENGTH_SHORT);
					toast.show();
				} else {
					ContextHolder.getInstance().createLearningManager(allWordCards);
					ContextHolder.getSettingsHolder().setStartFromNumber(0);
					Intent returnIntent = new Intent();
					setResult(Activity.RESULT_OK, returnIntent);
					finish();
				}
			}

			private List<Question> loadFromDB(int position) {
				Cursor cursor;
				// if (categoryOrDictionary.isChecked()) {
				// if (position == 0) {
				// cursor = dbManager.fetchEntitiesWithoutCategory();
				// } else {
				String selected = ContextHolder.getInstance().getCategories()[position];
				String selectionId = selected.split(ID_SEP)[1];
				cursor = dbManager.fetchEntitiesByCategory(selectionId);
				// }
				// } else {
				// if (position == 0) {
				// cursor = dbManager.fetchEntitiesWithoutDictionary();
				// } else {
				// String selected =
				// ContextHolder.getInstance().getDictionaries()[position];
				// String selectionId = selected.split(ID_SEP)[1];
				// cursor = dbManager.fetchEntitiesByDictionary(selectionId);
				// }
				// }
				// boolean shuffleSelected = shuffleCheckBox.isChecked();
				// ContextHolder.getSettingsHolder().setShuffleWords(shuffleSelected);
				List<Question> allWordCards = loadWordsTranslation(cursor, true);
				// if (shuffleSelected) {
				Collections.shuffle(allWordCards);
				// }
				return allWordCards;
				// return null;
			}
		});
		// TreeNode root = TreeNode.root();
		// TreeNode parent = new TreeNode("node-name");
		// TreeNode child0 = new TreeNode("child-node-name-1");
		// TreeNode child1 = new TreeNode("child_node-name-2");
		//
		// parent.addChildren(child0, child1);
		// root.addChild(parent);
		// AndroidTreeView tView = new AndroidTreeView(this, root);
		// LayoutInflater vi = (LayoutInflater)
		// getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// View rootView = vi.inflate(R.layout.activity_select_category, null,
		// false);
		//// final View rootView = inflater.inflate(R.layout.fragment_default,
		// null, false);
		// final ViewGroup containerView = (ViewGroup)
		// rootView.findViewById(R.id.container);
		// containerView.addView(tView.getView());
	}

	private String[] removeIds(String[] items) {
		String[] cleared = new String[items.length];
		for (int i = 0; i < items.length; i++) {
			cleared[i] = items[i].split(ID_SEP)[0];
		}
		return cleared;
	}

	private List<Question> loadWordsTranslation(Cursor cursor, boolean shuffleSelected) {
		List<Question> questions = new ArrayList<>();
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				Question question = new Question();
				question.setText(cursor.getString(1));
				question.setAnswers(loadAnswers(cursor.getString(0)));
				questions.add(question);
			}
		}
		return questions;
	}

	private List<Answer> loadAnswers(String questionId) {
		Cursor cursor = dbManager.fetchAnswersByQuestion(questionId);
		List<Answer> answers = new ArrayList<>();
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				Answer answer = new Answer();
				answer.setValue(cursor.getString(0));
				answer.setCorrect(cursor.getInt(1) > 0);
				answers.add(answer);
			}
		}
		return answers;
	}

	private String[] fetchList(String firstElement, Cursor cursor) {
		String[] items = new String[] {};
		if (cursor != null && cursor.getCount() > 0) {
			int i = 0;
			List<String> itemsList = new ArrayList<>();
			List<Category> categories = new ArrayList<>();
			Map<Integer, Category> idToCat = new HashMap<>();
			List<Category> rootCategory = new ArrayList<>();
			while (cursor.moveToNext()) {
				Category category = new Category();
				category.setId(cursor.getInt(0));
				category.setName(cursor.getString(1));
				category.setSupperCategoryId(cursor.getInt(2));
				if (category.getSupperCategoryId() == 0) {
					rootCategory.add(category);
				}
				categories.add(category);
				idToCat.put(category.getId(), category);
				// itemsList.add(cursor.getString(1) + ID_SEP +
				// cursor.getInt(0));
				// i++;
			}
			for (Category category : categories) {
				int supperCategoryId = category.getSupperCategoryId();
				if (idToCat.containsKey(supperCategoryId)) {
					List<Category> subCategories = idToCat.get(supperCategoryId).getSubCategories();
					if (!subCategories.contains(category)) {
						subCategories.add(category);
					}
				}
			}
			addSubCategories(rootCategory, itemsList, 0);
//			Collections.sort(itemsList);
			// itemsList.add(0, firstElement + "=id=-1");
			items = itemsList.toArray(items);
		}
		return items;
	}

	private void addSubCategories(List<Category> subCategories, List<String> itemsList, int depth) {
		Collections.sort(subCategories, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
		for (Category category : subCategories) {
			itemsList.add(String.join("", Collections.nCopies(depth, " - "))+category.getName() + ID_SEP + category.getId());
			addSubCategories(category.getSubCategories(), itemsList, depth+1);
		}
	}
}