package com.camangi.home;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.camangi.home.Recents_Adapter.ViewHolder;
import com.camangi.home.R;
import com.camangi.home.R.drawable;
import com.camangi.home.R.id;
import com.camangi.home.R.layout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Applications_Adapter extends BaseAdapter {

	private static final String TAG = "Applications_Adapter";
	
	private ArrayList<ApplicationInfo> arylistApp;	
	private Context myContext;
	private LayoutInflater  mInflater;
	
	int mGalleryItemBackground;
	
	public Applications_Adapter(Context c, ArrayList<ApplicationInfo> app)
	{
		myContext =c;
		arylistApp = app;
		mInflater = LayoutInflater.from(myContext);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arylistApp.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return arylistApp.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Rect mOldBounds = new Rect();
		final ApplicationInfo info = arylistApp.get(position);
		
		ViewHolder holder = new ViewHolder();
        if (convertView == null) {
           
            convertView = mInflater.inflate(R.layout.application1, parent, false);
           
            holder.icon = (ImageView)	convertView.findViewById(R.id.icon_app);
            holder.txt = (TextView)	convertView.findViewById(R.id.label);
            holder.appLayout = (LinearLayout)	convertView.findViewById(R.id.applayout);
            convertView.setTag(holder);
        }
        else
        {
        	holder = (ViewHolder)convertView.getTag();
        }

        Drawable icon = info.icon;
        
        
        
        if (!info.filtered) {
            //final Resources resources = getContext().getResources();
            int width = 48;//(int) resources.getDimension(android.R.dimen.app_icon_size);
            int height = 48;//(int) resources.getDimension(android.R.dimen.app_icon_size);

            final int iconWidth = icon.getIntrinsicWidth();
            final int iconHeight = icon.getIntrinsicHeight();

            if (icon instanceof PaintDrawable) {
                PaintDrawable painter = (PaintDrawable) icon;
                painter.setIntrinsicWidth(width);
                painter.setIntrinsicHeight(height);
            }

            if (width > 0 && height > 0 && (width < iconWidth || height < iconHeight)) {
                final float ratio = (float) iconWidth / iconHeight;

                if (iconWidth > iconHeight) {
                    height = (int) (width / ratio);
                } else if (iconHeight > iconWidth) {
                    width = (int) (height * ratio);
                }

                final Bitmap.Config c =
                        icon.getOpacity() != PixelFormat.OPAQUE ?
                            Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
                final Bitmap thumb = Bitmap.createBitmap(width, height, c);
                final Canvas canvas = new Canvas(thumb);
                canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, 0));
                // Copy the old bounds to restore them later
                // If we were to do oldBounds = icon.getBounds(),
                // the call to setBounds() that follows would
                // change the same instance and we would lose the
                // old bounds
                mOldBounds.set(icon.getBounds());
                icon.setBounds(0, 0, width, height);
                icon.draw(canvas);
                icon.setBounds(mOldBounds);
                icon = info.icon = new BitmapDrawable(thumb);
                info.bmp=thumb;
                
                info.filtered = true;
            }
        }

       
        holder.txt.setText(info.title);
        holder.icon.setImageDrawable(icon);
        //checkDayOfWeek();
       
        holder.appLayout.setBackgroundResource(R.drawable.icon_bg);
        
        holder.appLayout.setOnTouchListener(new LinearLayout.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction())
				{
				
				case MotionEvent.ACTION_DOWN:
					//Log.d(TAG, "appLayout action down");
					v.setBackgroundResource(R.drawable.icon_bg_over);
					break;
				case MotionEvent.ACTION_UP:
					//Log.d(TAG, "appLayout action up");
					v.setBackgroundResource(R.drawable.icon_bg);
					myContext.startActivity(info.intent);
					break;
				}
				return true;
			}});
        return convertView;
	}	
	
	public class ViewHolder
	{
		LinearLayout appLayout;
		ImageView icon;
		TextView txt;				
	}

	private void checkDayOfWeek(ViewHolder holder)
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
	        	holder.appLayout.setBackgroundResource(R.drawable.appbg_purple);            
	            break;
	        case Calendar.MONDAY:
	        	holder.appLayout.setBackgroundResource(R.drawable.appbg_red);        
	            break;
	        case Calendar.TUESDAY:
	        	holder.appLayout.setBackgroundResource(R.drawable.appbg_oragne);        
	            break;
	        case Calendar.WEDNESDAY:
	        	holder.appLayout.setBackgroundResource(R.drawable.appbg_yellow);        
	            break;
	        case Calendar.THURSDAY:
	        	holder.appLayout.setBackgroundResource(R.drawable.appbg_green);        
	            break;
	        case Calendar.FRIDAY:
	        	holder.appLayout.setBackgroundResource(R.drawable.appbg_blue);        
	            break;
	        case Calendar.SATURDAY:
	        	holder.appLayout.setBackgroundResource(R.drawable.appbg_indigo);        
	            break;           
	        }
		}
		
    }
}
