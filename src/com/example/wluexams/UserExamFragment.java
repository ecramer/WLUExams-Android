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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class UserExamFragment extends ListFragment  {

	private ArrayList<Exam> exams = new ArrayList<Exam>();
	
	private Button searchButton;
	private View myView;


	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getActivity().setTitle("My Exams");
		setHasOptionsMenu(true);
		UserExamAdapter adapter = new UserExamAdapter(exams, getActivity());
		setListAdapter(adapter);

	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.userexam_list_context, menu);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_userexam_list_view,
				container, false);
		ListView listView = (ListView)v.findViewById(android.R.id.list);
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			
			registerForContextMenu(listView);
			
		} else {
			
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
				
				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					
					//required but nothing is coming
					return false;
				}
				
				@Override
				public void onDestroyActionMode(ActionMode mode) {
					
					//required but nothing is coming
					
				}
				
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.userexam_list_context, menu);
					return true;
				}
				
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					
					switch(item.getItemId()){
					case R.id.menu_item_remove_exam:
						UserExamAdapter adapter = (UserExamAdapter)getListAdapter();
						for (int i = adapter.getCount() -1; i >= 0; i--){
							if (getListView().isItemChecked(i)){
								Exam e = adapter.getItem(i);
								exams.remove(e);
								RemoveExam remover = new RemoveExam();
								remover.execute(Integer.toString(e.getID()));
							}
							
						}
						if(exams.size()==0){
							myView.findViewById(R.id.emptyTextView).setVisibility(
									View.VISIBLE);
							myView.findViewById(R.id.search_button).setVisibility(
									View.VISIBLE);
						}
						mode.finish();
						adapter.notifyDataSetChanged();
						return true;
					}
					return false;
				}
				
				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position,
						long id, boolean checked) {
					
					//nothing is coming
					
				}
			});
			
		}
		
		
		
		myView = v;
		searchButton = (Button) v.findViewById(R.id.search_button);
		searchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(getActivity(), SearchActivity.class);
				startActivity(i);

			}
		});

		return v;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int position = info.position;
		UserExamAdapter adapter = (UserExamAdapter)getListAdapter();
		Exam e = adapter.getItem(position);
		switch(item.getItemId()){
		case R.id.menu_item_remove_exam:
			exams.remove(e);
			RemoveExam remover = new RemoveExam();
			remover.execute(Integer.toString(e.getID()));
			adapter.notifyDataSetChanged();
			if(exams.size()==0){
				myView.findViewById(R.id.emptyTextView).setVisibility(
						View.VISIBLE);
				myView.findViewById(R.id.search_button).setVisibility(
						View.VISIBLE);
			}
			return true;
		}
		
		return super.onContextItemSelected(item);
	}
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_user_exam, menu);

	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.action_search:
			Intent i = new Intent(getActivity(), SearchActivity.class);
			startActivity(i);
			return true;
		case R.id.logout:
			SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
			Editor editor = pref.edit();
			editor.clear();
			editor.commit();
			Intent intent = new Intent(getActivity(),LoginActivity.class);
			startActivity(intent);
			getActivity().finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}

	}

	public void loadExams() {

		GetExams getter = new GetExams();
		getter.execute();

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
		exams.clear();
		UserExamAdapter adapter = (UserExamAdapter)getListAdapter();
		adapter.notifyDataSetChanged();
		loadExams();

	}
	
	private class RemoveExam extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String examID = params[0];
			SharedPreferences pref = getActivity().getSharedPreferences(
					"MyPref", 0); // 0 - for private mode
			String userID = pref.getString("userID", null);

			String link = Config.serverURL + "/Users/Exams/remove.php";
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("userID", userID));
			nameValuePairs.add(new BasicNameValuePair("examID", examID));
			DatabaseConnection conn = new DatabaseConnection();
			conn.getData(link, nameValuePairs);
			return null;
			
		}

	}

	private class GetExams extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			SharedPreferences pref = getActivity().getSharedPreferences(
					"MyPref", 0); // 0 - for private mode
			String userID = pref.getString("userID", null);

			String link = Config.serverURL + "/Users/Exams/list.php";
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("userID", userID));
			DatabaseConnection conn = new DatabaseConnection();
			String jsonResult = conn.getData(link, nameValuePairs);

			return jsonResult;

		}

		protected void onPostExecute(String result) {

			JSONParser parser = new JSONParser();

			try {

				Object obj = parser.parse(result);
				if (obj instanceof JSONObject) {

					// there are no things
					UserExamAdapter adapter = (UserExamAdapter)getListAdapter();
					adapter.notifyDataSetChanged();
					myView.findViewById(R.id.emptyTextView).setVisibility(
							View.VISIBLE);
					myView.findViewById(R.id.search_button).setVisibility(
							View.VISIBLE);

				} else {

					myView.findViewById(R.id.emptyTextView).setVisibility(
							View.INVISIBLE);
					myView.findViewById(R.id.search_button).setVisibility(
							View.INVISIBLE);

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
