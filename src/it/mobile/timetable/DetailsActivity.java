package it.mobile.timetable;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			TextView courseTV = (TextView) findViewById(R.id.courseTextView);
			courseTV.setText(extras.getString("course"));
			
			TextView teacherTV = (TextView) findViewById(R.id.teacherTextView);
			teacherTV.setText(extras.getString("teacher"));
			
			TextView scheduleTV = (TextView) findViewById(R.id.scheduleTextView);
			scheduleTV.setText(extras.getString("time") + "\n" + 
					getResources().getString(R.string.room)+": "+extras.getString("room"));
			
			TextView descrTV = (TextView) findViewById(R.id.descriptionTextView);
			descrTV.setText(getResources().getString(R.string.details_title) + ":\n" + 
					extras.getString("description"));
			
			RelativeLayout rl = (RelativeLayout) findViewById(R.id.backgroundCourseLayout);
			rl.setBackgroundColor(getResources().getColor(extras.getInt("color")));
		}
	}
}
