package it.mobile.timetable.view;

import it.mobile.timetable.R;
import it.mobile.timetable.data.Lecture;

import java.util.Collection;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class DaySlidePageAdapter extends PagerAdapter {

	private Context mContext;
    private LayoutInflater mLayoutInflater;
    private Collection<Lecture> mData;

    public DaySlidePageAdapter(Context context, Collection<Lecture> data) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = data;
    }
 
    @Override
    public int getCount() {
        return 5;
    }
 
 
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.day, container, false);
        for (Lecture l : mData){
        	if (l.getDayOfWeek() == position){
        		View myView = Util.getView(mContext,l);
        		((RelativeLayout)itemView).addView(myView);
        	}
        }
        container.addView(itemView);
        return itemView;
    }

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}

	@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
	
}
