package com.example.wluexams;

import android.support.v4.app.Fragment;

public class ExamListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		
		return new ExamListFragment();
	}
	
	
	
}