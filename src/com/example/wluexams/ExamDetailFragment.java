package com.example.wluexams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ExamDetailFragment extends Fragment {

	public static final String EXTRA_EXAM = "com.example.wluexams.exam";
	private String isSaved = "UNKNOWN";
	private Exam e;
	private GoogleMap map;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_exam_detail, parent, false);
		e = (Exam) getActivity().getIntent().getSerializableExtra(EXTRA_EXAM);
		CheckExam checker = new CheckExam();
		checker.execute(Integer.toString(e.getID()));
		getActivity().setTitle(e.getCourseCode());

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

		}

		TextView courseCode = (TextView) v.findViewById(R.id.course_code);
		courseCode.setText(e.getCourseCode() + " " + e.getSection());

		TextView location = (TextView) v.findViewById(R.id.location_name);
		location.setText(e.getLocationName() + ", " + e.getRoom());

		TextView department = (TextView) v.findViewById(R.id.exam_department);
		department.setText(e.getDepID());

		TextView date = (TextView) v.findViewById(R.id.date);

		SimpleDateFormat formatter = new SimpleDateFormat(
				"MMMM d, yyyy 'at' h:mm a", Locale.CANADA);
		date.setText(formatter.format(e.getDate()));

		if (!e.getLocationName().equals("Online")) {

			FragmentManager myFM = getActivity().getSupportFragmentManager();

			SupportMapFragment myMAPF = (SupportMapFragment) myFM
					.findFragmentById(R.id.map);
			map = myMAPF.getMap();
			LatLng locale = new LatLng(e.getLatitude(), e.getLongitude());

			map.addMarker(new MarkerOptions().position(locale));
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(locale, 18));

		}

		return v;

	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_exam_detail, menu);

	}

	public void onPrepareOptionsMenu(Menu menu) {

		super.onPrepareOptionsMenu(menu);

		if (isSaved.equals("YES")) {

			menu.findItem(R.id.menu_item_add_exam).setVisible(false);
			menu.findItem(R.id.menu_item_delete_exam).setVisible(true);

		} else if (isSaved.equals("NO")) {

			menu.findItem(R.id.menu_item_add_exam).setVisible(true);
			menu.findItem(R.id.menu_item_delete_exam).setVisible(false);

		}

	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menu_item_add_exam:
			AddExam adder = new AddExam();
			adder.execute(Integer.toString(e.getID()));
			return true;
		case R.id.menu_item_delete_exam:
			RemoveExam remover = new RemoveExam();
			remover.execute(Integer.toString(e.getID()));
			return true;
		case android.R.id.home:
			getActivity().finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}

	}

	private class AddExam extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String examID = params[0];
			SharedPreferences pref = getActivity().getSharedPreferences(
					"MyPref", 0); // 0 - for private mode
			String userID = pref.getString("userID", null);

			String link = "http://hopper.wlu.ca/~wluexams/php/Users/Exams/add.php";
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("userID", userID));
			nameValuePairs.add(new BasicNameValuePair("examID", examID));
			DatabaseConnection conn = new DatabaseConnection();
			conn.getData(link, nameValuePairs);
			return null;
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		protected void onPostExecute(Void result) {

			isSaved = "YES";
			Toast.makeText(getActivity(), "Exam added!", Toast.LENGTH_SHORT)
					.show();
			getActivity().invalidateOptionsMenu();

		}

	}

	private class RemoveExam extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String examID = params[0];
			SharedPreferences pref = getActivity().getSharedPreferences(
					"MyPref", 0); // 0 - for private mode
			String userID = pref.getString("userID", null);

			String link = "http://hopper.wlu.ca/~wluexams/php/Users/Exams/remove.php";
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("userID", userID));
			nameValuePairs.add(new BasicNameValuePair("examID", examID));
			DatabaseConnection conn = new DatabaseConnection();
			conn.getData(link, nameValuePairs);
			return null;
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		protected void onPostExecute(Void result) {

			isSaved = "NO";
			Toast.makeText(getActivity(), "Exam removed!", Toast.LENGTH_SHORT)
					.show();
			getActivity().invalidateOptionsMenu();

		}

	}

	private class CheckExam extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String examID = params[0];
			SharedPreferences pref = getActivity().getSharedPreferences(
					"MyPref", 0); // 0 - for private mode
			String userID = pref.getString("userID", null);

			String link = "http://hopper.wlu.ca/~wluexams/php/Users/Exams/check.php";
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("userID", userID));
			nameValuePairs.add(new BasicNameValuePair("examID", examID));
			DatabaseConnection conn = new DatabaseConnection();
			String jsonResult = conn.getData(link, nameValuePairs);
			return jsonResult;
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		protected void onPostExecute(String result) {

			try {

				JSONParser parser = new JSONParser();
				Object obj = parser.parse(result);
				JSONObject jsonObject = (JSONObject) obj;

				if (jsonObject.get("response").toString().equals("true")) {

					isSaved = "YES";

				} else {

					isSaved = "NO";

				}
				getActivity().invalidateOptionsMenu();

			} catch (ParseException e) {

				e.printStackTrace();

			}

		}

	}

}
