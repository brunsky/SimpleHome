package com.camangi.home;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.camangi.home.R;
import com.camangi.home.R.anim;
import com.camangi.home.R.drawable;
import com.camangi.home.R.id;
import com.camangi.home.R.layout;
import com.camangi.home.R.styleable;


import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Recents_Adapter extends BaseAdapter {

	private static final String TAG = "Favorite_Adapter";
	private Context myContext;
	
	
	private LayoutInflater  mInflater;
	private List<ApplicationInfo> mFavorites;
	int mGalleryItemBackground;
	
	public Recents_Adapter(Context c, List<ApplicationInfo> mApplications){
		Log.d(TAG, "Favorite_Adapter");
		myContext = c;
		mFavorites = mApplications;
		mInflater = LayoutInflater.from(c);
		
		TypedArray a = c.obtainStyledAttributes(R.styleable.Gallery);
		
	    mGalleryItemBackground = a.getResourceId(R.styleable.Gallery_android_galleryItemBackground, 0);
	    
	    a.recycle();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFavorites.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	ViewHolder holder;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//Log.d(TAG, "getView");
		
		
		if (convertView == null)
		{
			convertView = (View) mInflater.inflate(R.layout.rfavorite, parent, false);
			
			holder = new ViewHolder();
			holder.icon = (ImageView)	convertView.findViewById(R.id.favorite_icon);
			holder.txt = (TextView)	convertView.findViewById(R.id.favorite_txt);
			holder.favoriteLayout = (LinearLayout)	convertView.findViewById(R.id.favoritelayout);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.icon.setBackgroundDrawable(mFavorites.get(position).icon);
		//holder.icon.setImageResource(R.drawable.ic_launcher_home);
		//holder.icon.setScaleType(ImageView.ScaleType.FIT_XY);
		//holder.icon.setLayoutParams(new Gallery.LayoutParams(150, 150));
		//convertView.setBackgroundResource(mGalleryItemBackground);
		
		//Log.d(TAG, "set text:" +arylistCity.get(position));
		holder.txt.setText(mFavorites.get(position).title);
		//holder.txt.setText(showApp.get(position).toString());
		//holder.temp.setText(arylistTemperature.get(position).toString());
		//checkDayOfWeek(); 
		holder.favoriteLayout.setBackgroundResource(R.drawable.appbg_black);
		convertView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Animation animation = AnimationUtils.loadAnimation(myContext, R.anim.scale);
		    	ImageView img = (ImageView) v.findViewById(R.id.favorite_icon);
		    	animation.setAnimationListener(new AnimationListener(){

					@Override
					public void onAnimationEnd(Animation arg0) {
						// TODO Auto-generated method stub
						myContext.startActivity(mFavorites.get(position).intent);
					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub
						
					}});
		    	
		    	img.startAnimation(animation);
			}
		});
		
		return convertView;		
		
	}
	
	public class ViewHolder
	{
		LinearLayout favoriteLayout;
		ImageView icon;
		TextView txt;				
	}

	
	private void checkDayOfWeek()
    {
		if (holder != null)
		{
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(new Date());
    	// Get the weekday and print it
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        
	        switch (calendar.get(Calendar.DAY_OF_WEEK))
	        {
	        case Calendar.SUNDAY:
	        	holder.favoriteLayout.setBackgroundResource(R.drawable.appbg_purple);            
	            break;
	        case Calendar.MONDAY:
	        	holder.favoriteLayout.setBackgroundResource(R.drawable.appbg_red);        
	            break;
	        case Calendar.TUESDAY:
	        	holder.favoriteLayout.setBackgroundResource(R.drawable.appbg_oragne);        
	            break;
	        case Calendar.WEDNESDAY:
	        	holder.favoriteLayout.setBackgroundResource(R.drawable.appbg_yellow);        
	            break;
	        case Calendar.THURSDAY:
	        	holder.favoriteLayout.setBackgroundResource(R.drawable.appbg_green);        
	            break;
	        case Calendar.FRIDAY:
	        	holder.favoriteLayout.setBackgroundResource(R.drawable.appbg_blue);        
	            break;
	        case Calendar.SATURDAY:
	        	holder.favoriteLayout.setBackgroundResource(R.drawable.appbg_indigo);        
	            break;           
	        }
		}
    }
}
