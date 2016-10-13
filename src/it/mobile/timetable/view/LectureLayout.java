package it.mobile.timetable.view;

import it.mobile.timetable.DetailsActivity;
import it.mobile.timetable.R;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.*;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LectureLayout extends RelativeLayout implements OnClickListener {
	
	private String courseName;
	private String teacherName;
	private String schedule;
	private String classroom;
	private String description;
	private int colorResID;
	
	public LectureLayout(Context context, AttributeSet set, String course, String teacher, 
			int day, String time1, String time2, String room, String descr) {
		super(context, set);
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.lecture,this);
		
		courseName = course;
		teacherName = teacher;
		description = descr;
		classroom = room;
		
		TypedArray ar = context.getResources().obtainTypedArray(R.array.dayStrings);
		schedule = ar.getString(day)+" "+time1+" - "+time2;
		ar.recycle();
		
		((TextView) findViewById(R.id.textView1)).setText(course);
		((TextView) findViewById(R.id.scheduleSummaryText)).setText(time1+"\n"+time2);

		colorResID = Util.getColorID(this.getContext(), course);
		setBackgroundColor(getResources().getColor(colorResID));
		
		setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		LectureLayout caller = (LectureLayout) v;
		Intent intent = new Intent(v.getContext(), DetailsActivity.class);
		intent.putExtra("course", caller.courseName);
		intent.putExtra("teacher", caller.teacherName);
		intent.putExtra("time", caller.schedule);
		intent.putExtra("room", caller.classroom);
		intent.putExtra("description", caller.description);
		intent.putExtra("color", caller.colorResID);
		caller.getContext().startActivity(intent);
	}
	
}
