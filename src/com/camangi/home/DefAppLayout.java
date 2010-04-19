package com.camangi.home;

import com.camangi.home.R;
import com.camangi.home.R.drawable;
import com.camangi.home.R.id;
import com.camangi.home.R.layout;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DefAppLayout extends LinearLayout {

	private Intent intent;
	private Drawable icon;
	private int resourceId;
	private String title;
	private Context mycontext;
	
	private LayoutInflater inflater;
	
	public DefAppLayout(Context context) {		
		super(context);
		// TODO Auto-generated constructor stub
		mycontext = context;
		inflater = LayoutInflater.from(context);
	}
	
	public void setValues(Intent rintent, Drawable ricon, String rtitle)
	{
	
			intent = rintent;
			icon = ricon;
			title = rtitle;
		
		
	}

	
	
	public class ViewHolder
	{
		LinearLayout appLayout;
		ImageView icon;
		TextView txt;				
	}
	
	public View getLinkView(final Intent rintent, int rid, String rtitle)
	{
		intent = rintent;
		resourceId = rid;
		title = rtitle;
		
		View convertView = inflater.inflate(R.layout.def_application, null);
		ViewHolder holder = new ViewHolder();
		
			
			holder.icon = (ImageView)	convertView.findViewById(R.id.icon_app);
            holder.txt = (TextView)	convertView.findViewById(R.id.label);
            holder.appLayout = (LinearLayout)	convertView.findViewById(R.id.applayout);
            convertView.setTag(holder);
		
		//holder.icon.setImageDrawable(icon);
		holder.icon.setImageResource(resourceId);
		holder.txt.setText(title);
		holder.appLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mycontext.startActivity(intent);
			}
		});
		
		holder.appLayout.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					//v.setBackgroundColor(Color.YELLOW);
					v.setBackgroundResource(R.drawable.icon_bg);
					break;
				case MotionEvent.ACTION_UP:
					//v.setBackgroundColor(Color.BLUE);
					v.setBackgroundResource(0);
					break;
				}
				return false;
			}
		});
		return convertView;
	}
	
	public View getView(final Intent rintent, Drawable ricon, String rtitle)
	{
		intent = rintent;
		icon = ricon;
		title = rtitle;
		
		View convertView = inflater.inflate(R.layout.def_application, null);
		ViewHolder holder = new ViewHolder();
		
			
			holder.icon = (ImageView)	convertView.findViewById(R.id.icon_app);
            holder.txt = (TextView)	convertView.findViewById(R.id.label);
            holder.appLayout = (LinearLayout)	convertView.findViewById(R.id.applayout);
            convertView.setTag(holder);
		
		holder.icon.setImageDrawable(icon);
		holder.txt.setText(title);
		holder.appLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mycontext.startActivity(intent);
			}
		});
		
		holder.appLayout.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					//v.setBackgroundColor(Color.YELLOW);
					v.setBackgroundResource(R.drawable.icon_bg);
					break;
				case MotionEvent.ACTION_UP:
					//v.setBackgroundColor(Color.BLUE);
					v.setBackgroundResource(0);
					break;
				}
				return false;
			}
		});
		return convertView;
	}
}
