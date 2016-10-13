package it.mobile.timetable.view;

import it.mobile.timetable.R;
import it.mobile.timetable.data.Lecture;
import it.mobile.timetable.data.Time;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RelativeLayout;

public class Util {

	private Util(){}
	
	public static int getColorID(Context context, String course){
		TypedArray ar = context.getResources().obtainTypedArray(
				R.array.androidcolors);
		int idx = Math.abs(course.hashCode()) % ar.length();
		int colorResID = ar.getResourceId(idx,0);
		ar.recycle();
		return colorResID;
	}
	
	public static LectureLayout getView(Context context, Lecture l) {
		LectureLayout ll = new LectureLayout(context, null,
				l.getCourse().getName(), l.getCourse().getTeacher(), l.getDayOfWeek(),
				l.getStartTime().toString(), l.getEndTime().toString(), l.getClassroom(), l.getCourse().getDescription());
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		Time start = l.getStartTime();
		Time end = l.getEndTime();
		int height_dip = end.difference(start);
		int height_px = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, height_dip, metrics);
		int topMargin_dip = start.difference(Time.START_TIME);
		int topMargin_px = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, topMargin_dip, metrics);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, height_px);
		params.topMargin = topMargin_px;
		ll.setLayoutParams(params);
		return ll;
	}
	
	public static boolean exportToGcal(Context context, Collection<Lecture> lectures){
		ContentResolver resolver = context.getContentResolver();
		String[] projection = {
				Calendars._ID,
				Calendars.NAME,
				Calendars.ACCOUNT_NAME, 
				Calendars.ACCOUNT_TYPE 
		};
		Cursor calCursor = resolver.query(
				Calendars.CONTENT_URI,
				projection, 
				Calendars.VISIBLE+" = 1 AND "+Calendars.ACCOUNT_TYPE+" LIKE '%google%'",
				null,
				null);
		if (!calCursor.moveToFirst()) {
			return false;
		}
		long myGoogleCalID = calCursor.getLong(0);
		ContentValues values = new ContentValues();
		values.put(Events.CALENDAR_ID, myGoogleCalID);
		
		Calendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getDefault());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		for (Lecture l : lectures){
			int day = Calendar.MONDAY;
			long startMillis = 0;
			long endMillis = 0;
			
			if (l.getDayOfWeek() == 1) day = Calendar.TUESDAY;
			else if (l.getDayOfWeek() == 2) day = Calendar.WEDNESDAY;
			else if (l.getDayOfWeek() == 3) day = Calendar.THURSDAY;
			else if (l.getDayOfWeek() == 4) day = Calendar.FRIDAY;
			cal.set(Calendar.DAY_OF_WEEK, day);
			cal.set(Calendar.HOUR_OF_DAY, l.getStartTime().getHour());
			cal.set(Calendar.MINUTE, l.getStartTime().getMinute());
			startMillis = cal.getTimeInMillis();
			
			cal.set(Calendar.HOUR_OF_DAY, l.getEndTime().getHour());
			cal.set(Calendar.MINUTE, l.getEndTime().getMinute());
			endMillis = cal.getTimeInMillis();
			
			values.put(Events.DTSTART, startMillis);
			values.put(Events.DTEND, endMillis);
			values.put(Events.TITLE, l.getCourse().getName());
			values.put(Events.EVENT_LOCATION, "PoliTO");
			
			values.put(Events.EVENT_TIMEZONE, "Europe/Berlin");
			values.put(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
			values.put(Events.GUESTS_CAN_MODIFY, 1);

			resolver.insert(Events.CONTENT_URI, values);
		}
		return true;
	}
	
}
