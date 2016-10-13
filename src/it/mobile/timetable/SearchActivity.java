package it.mobile.timetable;

import it.mobile.timetable.data.Course;
import it.mobile.timetable.data.Lecture;
import it.mobile.timetable.data.Timetable;
import it.mobile.timetable.view.LectureSearchAdapter;
import it.mobile.timetable.view.Util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

public class SearchActivity extends Activity {

	private Timetable data;
	private Object[] courseHints, teacherHints;
	private LectureSearchAdapter adapter;
	
	public Timetable getData() {
		return data;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		if (savedInstanceState == null){
			Bundle extras = getIntent().getExtras();
			data = (Timetable) extras.getSerializable("model");
			Set<String> hints1 = new HashSet<String>();
			Set<String> hints2 = new HashSet<String>();
			for(Course c : data.getCourses()){
				hints1.add(c.getName());
				hints2.add(c.getTeacher());
			}
			courseHints = hints1.toArray();
			teacherHints = hints2.toArray();
		}
		else{
			data = (Timetable) savedInstanceState.getSerializable("model");
			courseHints = (Object[]) savedInstanceState.getSerializable("courseHints");
			teacherHints = (Object[]) savedInstanceState.getSerializable("teacherHints");
		}
		
		
		AutoCompleteTextView text1 = (AutoCompleteTextView) findViewById(R.id.courseEditText); 
		text1.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, courseHints));
		AutoCompleteTextView text2 = (AutoCompleteTextView) findViewById(R.id.teacherEditText); 
		text2.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, teacherHints));
		registerForContextMenu(text1);
		registerForContextMenu(text2);
		
		adapter = new LectureSearchAdapter(this, 0, (List<Lecture>)data.filter(null, null));
		ListView lv = (ListView) findViewById(R.id.list);
		lv.setEmptyView(findViewById(R.id.noResultsTextView));
		lv.setAdapter(adapter);
		text1.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {	}
			@Override
			public void afterTextChanged(Editable s) {
				adapter.filterByCourseName(s.toString());
			}
		});
		text2.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) {
				adapter.filterByTeacherName(s.toString());
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_export, menu);
		return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null){
			outState.putSerializable("model", data);
			outState.putSerializable("courseHints", courseHints);
			outState.putSerializable("teacherHints", teacherHints);
		}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null){
			data = (Timetable) savedInstanceState.getSerializable("model");
			courseHints = (Object[]) savedInstanceState.getSerializable("courseHints");
			teacherHints = (Object[]) savedInstanceState.getSerializable("teacherHints");
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	    menu.setHeaderTitle(R.string.menu);
		final AutoCompleteTextView tv = (AutoCompleteTextView) v;
		menu.add(0, v.getId(), 0, R.string.clear);
		menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				tv.setText("");
				return true;
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_export){
			new AlertDialog.Builder(this)
			.setTitle(R.string.exportTitle)
			.setMessage(R.string.confirmExport)
			.setIcon(R.drawable.gcal)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	if (adapter.getData().isEmpty()){
			    		Toast.makeText(SearchActivity.this,R.string.emptyResult, Toast.LENGTH_SHORT).show();
			    		return;
			    	}
			    	boolean exportOK = Util.exportToGcal(SearchActivity.this, adapter.getData());
					if (!exportOK){
						Toast.makeText(SearchActivity.this,R.string.noAccount, Toast.LENGTH_SHORT).show();
						return;
					}
					Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time");
					Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
					startActivity(intent);
			    }})
			 .setNegativeButton(android.R.string.no, null)
			 .show();
		}
		return super.onOptionsItemSelected(item);
	}

}
