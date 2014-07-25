package com.example.wluexams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ExamListFragment extends ListFragment implements
		SearchView.OnQueryTextListener {

	public static final String DEP_ID = "com.example.wluexams.by_dept";
	private ArrayList<Exam> exams = new ArrayList<Exam>();
	
	private SearchView mSearchView;
	private ListView mListView;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

		}
		
		UserExamAdapter adapter = new UserExamAdapter(exams, getActivity());
		setListAdapter(adapter);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.searchable_listview, container, false);
		mSearchView = (SearchView) v.findViewById(R.id.search_view);
		mListView = (ListView) v.findViewById(android.R.id.list);
		mListView.setTextFilterEnabled(true);
		setupSearchView();
		return v;

	}

	private void setupSearchView() {
		mSearchView.setIconifiedByDefault(false);
		mSearchView.setOnQueryTextListener(this);
		mSearchView.setSubmitButtonEnabled(false);
		mSearchView.setQueryHint("Search Here");
	}

	public boolean onQueryTextChange(String newText) {
		if (TextUtils.isEmpty(newText)) {
			
			mListView.clearTextFilter();
			
		} else {

			mListView.setFilterText(newText.toString());
			
		}
		return true;
	}

	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	public void loadExams() {
		
		exams.clear();
		UserExamAdapter adapter = (UserExamAdapter)getListAdapter();
		adapter.notifyDataSetChanged();
		GetExams getter = new GetExams();
		getter.execute();

	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		Exam e = ((UserExamAdapter) getListAdapter()).getItem(position);
		Intent i = new Intent(getActivity(), ExamDetailActivity.class);
		i.putExtra(ExamDetailFragment.EXTRA_EXAM, e);
		startActivity(i);

	}

	public void onResume() {

		super.onResume();
		loadExams();

	}

	private class GetExams extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			String departmentID = getActivity().getIntent().getStringExtra(
					DEP_ID);
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			String link;

			if (departmentID == null) {

				link = Config.serverURL + "/Exams/list.php";

			} else {

				link = Config.serverURL + "/Departments/getall.php";
				nameValuePairs.add(new BasicNameValuePair("departmentID",
						departmentID));

			}

			DatabaseConnection conn = new DatabaseConnection();
			String jsonResult = conn.getData(link, nameValuePairs);
			return jsonResult;

		}

		protected void onPostExecute(String result) {

			JSONParser parser = new JSONParser();

			try {

				Object obj = parser.parse(result);
				if (obj instanceof JSONObject) {

					// no courses for this user

				} else {

					JSONArray array = (JSONArray) obj;
					for (Object myExam : array) {

						JSONObject jsonObject = (JSONObject) myExam;
						int ID = Integer.parseInt(jsonObject.get("ID")
								.toString());
						String courseID = jsonObject.get("courseID").toString();
						String section = jsonObject.get("section").toString();

						String date = jsonObject.get("date").toString();
						String time = jsonObject.get("time").toString();

						String dateTime = date + " " + time;
						SimpleDateFormat formatter = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss", Locale.CANADA);
						Date theDate = formatter.parse(dateTime);

						String room = jsonObject.get("room").toString();
						String location = jsonObject.get("location").toString();

						Float latitude = null, longitude = null;

						if (!location.equals("Online")) {
							latitude = Float.parseFloat(jsonObject.get("lat")
									.toString());
							longitude = Float.parseFloat(jsonObject.get("long")
									.toString());
						}
						String depID = jsonObject.get("depName").toString();

						exams.add(new Exam(ID, depID, courseID, section,
								theDate, room, location, latitude, longitude));
					}

					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {

							UserExamAdapter adapter = (UserExamAdapter)getListAdapter();
							adapter.notifyDataSetChanged();

						}

					});

				}

			} catch (ParseException e) {

				e.printStackTrace();
			} catch (java.text.ParseException e) {

				e.printStackTrace();
			}

		}

	}

}
