package it.mobile.timetable.view;

import it.mobile.timetable.R;
import it.mobile.timetable.SearchActivity;
import it.mobile.timetable.data.Lecture;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TableLayout;
import android.widget.TextView;

public class LectureSearchAdapter extends ArrayAdapter<Lecture> {
	
	private List<Lecture> data;
	private String courseNameParam, teacherNameParam;
	private Filter filterByCourseName, filterByTeacherName;
	
	public LectureSearchAdapter(Context context, int resource, List<Lecture> list) {
		super(context, resource, list);
		data = list;
		
		/* Setting two filters */
		filterByCourseName = new Filter() {
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				data = (List<Lecture>) results.values;
				LectureSearchAdapter.this.notifyDataSetChanged();
			}
			
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				courseNameParam = constraint.toString();
				SearchActivity sa = (SearchActivity) getContext();
                FilterResults results = new FilterResults();
                results.values = sa.getData().filter(courseNameParam, teacherNameParam);
                return results;
			}
		};
		
		filterByTeacherName = new Filter() {
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				data = (List<Lecture>) results.values;
				LectureSearchAdapter.this.notifyDataSetChanged();
			}
			
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				teacherNameParam = constraint.toString();
				SearchActivity sa = (SearchActivity) getContext();
                FilterResults results = new FilterResults();
                results.values = sa.getData().filter(courseNameParam, teacherNameParam);
                return results;
			}
		};
	}
	
	public void filterByCourseName(String constraint){
		filterByCourseName.filter(constraint);
	}
	
	public void filterByTeacherName(String constraint){
		filterByTeacherName.filter(constraint);
	}
	
	@Override
	public View getView(int pos, View convertView, ViewGroup p) {
		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = li.inflate(R.layout.result_item, p, false);
		}
		Lecture l = getItem(pos);
		Resources r = getContext().getResources();

		TypedArray days = r.obtainTypedArray(R.array.dayStrings);
		String day = days.getString(l.getDayOfWeek());
		days.recycle();
		TextView dayTV = (TextView) convertView.findViewById(R.id.resultDayTextView);
		dayTV.setText(day);
		
		TableLayout tl = (TableLayout) convertView.findViewById(R.id.resultDetailsTableLayout);
		TextView tv = (TextView) tl.findViewById(R.id.resultTitleTextView);
		tv.setText(l.getCourse().getName());
		
		TextView detailsTv = (TextView) tl.findViewById(R.id.resultDetailsTextView);
		detailsTv.setText(" "+l.getStartTime()+" - "+l.getEndTime());
		
		convertView.setBackgroundColor(r.getColor(
				Util.getColorID(getContext(), l.getCourse().getName())));

		return convertView;
	}
	
	@Override
	public Lecture getItem(int position) {
		return data.get(position);
	}
	
	@Override
	public int getCount() {
		return data.size();
	}
	
	public List<Lecture> getData() {
		return data;
	}

}
