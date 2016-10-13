package it.mobile.timetable;

import it.mobile.timetable.data.Lecture;
import it.mobile.timetable.data.Timetable;
import it.mobile.timetable.data.TimetableImpl;
import it.mobile.timetable.view.DaySlidePageAdapter;
import it.mobile.timetable.view.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

public class HomeActivity extends FragmentActivity {

	private Timetable data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		if (savedInstanceState == null){
			InputStream jsonInputStream = null;
			try {
				jsonInputStream = getAssets().open("timetable.json");
			} catch (IOException e) {}
			/* Loading model data from JSON file */
			data = TimetableImpl.newInstance(jsonInputStream);
		}
		else{
			data = (Timetable) savedInstanceState.getSerializable("model");
		}
		
		Collection<Lecture> filter = data.filter(null, null);
		final ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
		final ActionBar actionBar = getActionBar();
		
		if (mViewPager != null){
			mViewPager.setOnPageChangeListener(
					new ViewPager.SimpleOnPageChangeListener() {
		                @Override
		                public void onPageSelected(int position) {
		                    actionBar.setSelectedNavigationItem(position);
		                }
		            });
			PagerAdapter pa = new DaySlidePageAdapter(this, filter);
			mViewPager.setAdapter(pa);
			
			ActionBar.TabListener tabListener = new ActionBar.TabListener() {
				@Override
				public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					mViewPager.setCurrentItem(tab.getPosition());
				}
				@Override
				public void onTabReselected(Tab tab, FragmentTransaction ft) {}
			};
			
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			TypedArray ar = getResources().obtainTypedArray(
					R.array.dayStrings);
			for (int i = 0; i < ar.length(); i++) {
		        actionBar.addTab(
		        	actionBar.newTab().setText(ar.getString(i)).setTabListener(tabListener));
		    }
			ar.recycle();
		}
		else{
			drawWeeklyView(filter);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null){
			outState.putSerializable("model", data);
		}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null){
			data = (Timetable) savedInstanceState.getSerializable("model");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_search) {
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra("model", data);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void drawWeeklyView(Collection<Lecture> lectures) {
		TypedArray aa = getResources().obtainTypedArray(R.array.dayRelativeLayouts);
		for (Lecture l : lectures) {
			int idx = l.getDayOfWeek();
			RelativeLayout currentDay = (RelativeLayout) 
					findViewById(aa.getResourceId(idx, 0));
			View myView = Util.getView(this,l);
			currentDay.addView(myView);
		}
		aa.recycle();
	}
	

}
