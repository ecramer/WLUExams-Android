package com.example.wluexams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UserExamAdapter extends ArrayAdapter<Exam> {
	
	private FragmentActivity fragActivity;
	
	public UserExamAdapter(ArrayList<Exam> exams, FragmentActivity fragActivity) {
		
		super(fragActivity, 0, exams);
		this.fragActivity = fragActivity;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			
			convertView = fragActivity.getLayoutInflater().inflate(R.layout.list_item_exam, null);
			
		}
		
		Exam e = getItem(position);
		
		TextView courseName = (TextView)convertView.findViewById(R.id.exam_list_item_title);
		courseName.setText(e.getCourseCode() + " " + e.getSection());
		
		TextView date = (TextView)convertView.findViewById(R.id.exam_list_item_date);
		SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a", Locale.CANADA);
		date.setText(formatter.format(e.getDate()));
		
		return convertView;
		
	}
	

	
}