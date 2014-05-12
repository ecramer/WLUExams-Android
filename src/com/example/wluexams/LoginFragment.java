package com.example.wluexams;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFragment extends Fragment {

	private Button btnRegister;
	private Button btnLogin;
	private EditText txtEmail;
	private EditText txtPassword;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
		if (pref.getString("userID", null) != null){
			
			Intent i = new Intent(getActivity(),UserExamActivity.class);
			startActivity(i);
			getActivity().finish();
			
		}
		
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceBundle) {

		View v = inflater.inflate(R.layout.fragment_login, container, false);
		txtEmail = (EditText) v.findViewById(R.id.email);
		txtPassword = (EditText) v.findViewById(R.id.password);
		btnRegister = (Button) v.findViewById(R.id.register);
		btnRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				RegisterUser registration = new RegisterUser();
				registration.execute(txtEmail.getText().toString(), txtPassword
						.getText().toString());

			}
		});

		btnLogin = (Button) v.findViewById(R.id.login);
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				LoginUser login = new LoginUser();
				login.execute(txtEmail.getText().toString(), txtPassword
						.getText().toString());

			}
		});
		return v;

	}
	
	private class RegisterUser extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			String email = params[0];
			String password = params[1];
			String link = "http://hopper.wlu.ca/~wluexams/php/Users/add.php";
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("password", password));
			DatabaseConnection conn = new DatabaseConnection();
			String jsonResult = conn.getData(link, nameValuePairs);
			return jsonResult;
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			JSONParser parser = new JSONParser();
			Object obj;
			try {
				
				obj = parser.parse(result);
				JSONObject jsonObject = (JSONObject) obj;
				
				if(jsonObject.get("id") == null) {
					
					Toast.makeText(getActivity(), "User already exists. Please try logging in.", Toast.LENGTH_SHORT).show();
					
				} else {
					
					String id = jsonObject.get("id").toString();
					SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
					Editor editor = pref.edit();
					editor.putString("userID", id);
					editor.commit();
					
					Intent i = new Intent(getActivity(),UserExamActivity.class);
					startActivity(i);
					getActivity().finish();

				}

				
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
			
			
		}
		
		
	}

	private class LoginUser extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

				String email = params[0];
				String password = params[1];
				String link = "http://hopper.wlu.ca/~wluexams/php/Users/authenticate.php";
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("password", password));
				DatabaseConnection conn = new DatabaseConnection();
				String jsonResult = conn.getData(link, nameValuePairs);
				return jsonResult;

		}

		@Override
		protected void onPostExecute(String result) {

			JSONParser parser = new JSONParser();
			Object obj;
			try {
				
				obj = parser.parse(result);
				JSONObject jsonObject = (JSONObject) obj;
				
				if(jsonObject.get("id") == null) {
					
					Toast.makeText(getActivity(), "Email and password combination incorrect.", Toast.LENGTH_SHORT).show();
					
				} else {
					
					String id = jsonObject.get("id").toString();
					SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
					Editor editor = pref.edit();
					editor.putString("userID", id);
					editor.commit();
					
					Intent i = new Intent(getActivity(),UserExamActivity.class);
					startActivity(i);
					getActivity().finish();

				}

				
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
			

		}

	}

}
