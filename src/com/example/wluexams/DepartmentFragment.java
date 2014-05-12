package com.example.wluexams;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DepartmentFragment extends ListFragment implements SearchView.OnQueryTextListener{

	private ArrayList<Department> departments = new ArrayList<Department>();
	private ArrayAdapter<Department> adapter;
	private SearchView mSearchView;
	private ListView mListView;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		loadDepartments();
		
		
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
	

	public void loadDepartments() {

		GetDepartments getter = new GetDepartments();
		getter.execute();

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		Department d = (Department)(getListAdapter()).getItem(position);
		Intent i = new Intent(getActivity(), ExamListActivity.class);
		i.putExtra(ExamListFragment.DEP_ID, Integer.toString(d.getID()));
		startActivity(i);

	}

	private class GetDepartments extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			departments.clear();
			String link = "http://hopper.wlu.ca/~wluexams/php/Departments/list.php";
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			DatabaseConnection conn = new DatabaseConnection();
			String jsonResult = conn.getData(link, nameValuePairs);
			return jsonResult;

		}

		protected void onPostExecute(String result) {

			JSONParser parser = new JSONParser();

			try {

				Object obj = parser.parse(result);
				JSONArray array = (JSONArray) obj;
				for (Object dept : array) {

					JSONObject jsonObject = (JSONObject) dept;
					int ID = Integer.parseInt(jsonObject.get("ID").toString());
					String name = jsonObject.get("name").toString();
					departments.add(new Department(ID, name));
					
				}

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {

						adapter = new ArrayAdapter<Department>(getActivity(),android.R.layout.simple_list_item_1, departments);
						setListAdapter(adapter);

					}

				});

			} catch (ParseException e) {

				e.printStackTrace();
			}

		}

	}

}
