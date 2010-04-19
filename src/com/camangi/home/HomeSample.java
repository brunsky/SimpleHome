/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.camangi.home;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.camangi.home.R;
import com.camangi.home.R.anim;
import com.camangi.home.R.drawable;
import com.camangi.home.R.id;
import com.camangi.home.R.layout;
import com.camangi.home.R.string;



import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <b style='color:blue'>2010/03/30</b><br>
 * 修改<br>
 * 當只有一頁應用程式GridView時頁面顯示器不出現<br>
 * 新增<br>
 * 頁面顯示器<br>
 * 
 * <b style='color:blue'>2010/03/29</b><br>
 * 新增<br>
 * GridView顯示頁數 <br>
 * 
 * <b style='color:blue'>2010/03/26</b><br>
 * 修改<br>
 * GridView無限循環(不管數量多少)<br>
 * 
 * <b style='color:blue'>2010/03/25</b><br>
 * 新增<br>
 * 啟動TaskManager 服務<br>
 * 程式碼加註釋<br>
 * <br>
 * <b style='color:blue'>2010/03/24</b><br>
 * 新增<br>
 * 當search keyword 於軟體鍵盤上按搜尋時(keycode 66)會自動執行搜尋<br>
 * 修改<br>
 * 應用程式顯示由全部顯示改為不包括下方快捷列的應用程式顯示<br>
 * 解決<br>
 * 當按home key回到home時, 再按往右或往左切換應用程式顯示時，還會顯示第一頁<br>
 * <br>
 * <b style='color:blue'>2010/03/19</b><br>
 * 新增<br>
 * 固定應用程式排序以 sortapps陣列為主<br>
 * 修改<br>
 * 切換語系時造成下方固定應用程式列消失問題<br>
 * 新/刪應用程式時造成顯示異常問題<br>
 * @author LKevin
 *
 */ 
public class HomeSample extends Activity {
    /**
     * Tag used for logging errors.
     */
    private static final String LOG_TAG = "SimpleHome";

    /**
     * Keys during freeze/thaw.
     */
    private static final String KEY_SAVE_GRID_OPENED = "grid.opened";

    private static final String DEFAULT_FAVORITES_PATH = "etc/favorites.xml";

    private static final String TAG_FAVORITES = "favorites";
    private static final String TAG_FAVORITE = "favorite";
    private static final String TAG_PACKAGE = "package";
    private static final String TAG_CLASS = "class";    
    

    // Identifiers for option menu items
    private static final int MENU_WALLPAPER_SETTINGS = Menu.FIRST + 1;
    private static final int MENU_SEARCH = MENU_WALLPAPER_SETTINGS + 1;
    private static final int MENU_SETTINGS = MENU_SEARCH + 1;

    /**
     * Maximum number of recent tasks to query.
     */
    private static final int MAX_RECENT_TASKS = 8;

    private static boolean mWallpaperChecked;
    private static ArrayList<ApplicationInfo> mApplications;
    private static LinkedList<ApplicationInfo> mFavorites;

    private final BroadcastReceiver mWallpaperReceiver = new WallpaperIntentReceiver();
    private final BroadcastReceiver mApplicationsReceiver = new ApplicationsIntentReceiver();

    private GridView mGrid;
    
    private LayoutAnimationController mShowLayoutAnimation;
    private LayoutAnimationController mHideLayoutAnimation;

    private boolean mBlockAnimation;

    //private View mShowApplications;
    //private CheckBox mShowApplicationsCheck;

    //private ApplicationsStackLayout mApplicationsStack;

    private Animation mGridEntry_left, mGridEntry_right;
    private Animation mGridExit_left, mGridExit_right;
    private Animation imgFade_in;
    private Animation imgFade_out;
    private Animation layoutFade_in;
    private Animation layoutFade_out;
    //private Gallery favorite_Gallery;
    
    private LinearLayout singleLayout;
    private LinearLayout doubleLayout;
    
    private ImageView img_left;
    private ImageView img_right;
    
    private static final int SHOWAPPCOUNT = 12;
    
    private LinearLayout homeLayout;
    
    private boolean isShowMoverlImg = false;
    
    private GridView mGridback;
    
   
    /*
    private String[] defapps = {
    		"com.camangi.weather",
    		"com.android.email",
    		"com.android.music",
    		"com.android.browser",
    		"com.android.settings"    		
    };
    */
    /**
     * 預設固定的應用程式清單列表
     */
    private String[] defapps;
   
    /*
    String[] sortapps = {
    	"com.katdc.gmailweb",
    	"com.android.camera",
    	"com.android.camera",
    	"com.android.camera",
    	"com.fring",
    	"com.camangi.android.taskmanager",
    	"com.aldiko.android",
    	"com.camangi.android.TGMDialer",
    	"com.android.alarmclock",
    	"com.android.calendar",
    	"com.camangi.apkinstaller",
    	"com.katdc.googlebooksweb",
    	"com.android.calculator2",
    	"com.android.spare_parts"    	
    };
    */
    /**
     * 預先排定顯示於GridView的應用程式清單
     */
    private String[] sortapps;
    
    ArrayList<ApplicationInfo> defappinfos;
    private LinearLayout DefAppLayout;
    private AbsoluteLayout searchLayout;
    
    //搜尋
    private ImageView img_search;
    private EditText search_keyword;
    
    private int showGridId = 0;
    
    //page
    private LinearLayout pageLayout;   
    private TextView page_left, page_right;
    
    //music
    private ImageView img_music, img_pause, img_play;
    private TextView info_music;
    private boolean bIsPaused = false;
    boolean bIsRelease = false;
    private Animation music_in, music_out;
    private Animation musictitle_in, musictitle_out;
    private LinearLayout musicLayout, musictitleLayout;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.d(LOG_TAG, "onCreate");
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        setContentView(R.layout.home3);

        
        
        registerIntentReceivers();

        initDefArray();

        loadApplications(true);
 
        //Log.d(LOG_TAG, "setfindViews");
        setfindViews();
        
        setDefaultWallpaper();
        
        
        //Log.d(LOG_TAG, "bindApplications");
        
        bindApplications();
        //Log.d(LOG_TAG, "bindFavorites");
        //bindFavorites(true);
        //Log.d(LOG_TAG, "bindRecents");
        //bindRecents();
        //Log.d(LOG_TAG, "bindButtons");
        bindButtons();

        setAnimations();
        
        
        
        
        setListeners();
        
        checkDayOfWeek(img_left, false, true);
        checkDayOfWeek(img_right, false, false);
        
        
        
        startService();
        
        
        //Log.d(LOG_TAG, "OnCreate Complete");
       
        
    }
 
    private void initDefArray()
    {
    	defapps = getResources().getStringArray(R.array.defapps);
    	sortapps = getResources().getStringArray(R.array.sortapps);
    }
    /**
     * 判斷是否有安裝Taskmanager程式，若有則啟動taskmanager中的taskservice，做偵測記憶體服務
     */
    private void startService()
    {
    	if (mApplications != null && mApplications.size() > 0)
    	{
    		String packageName = "";
    		for (int i=0; i < mApplications.size(); i++)
    		{
    			//Log.d(LOG_TAG, "App package name: "+mApplications.get(i).packageName);
    			packageName = mApplications.get(i).packageName;
    			if (packageName.equals(getString(R.string.package_taskmanager)))
    			{
    				//Log.d(LOG_TAG, "start service");
    				//com.camangi.android.taskmanager.TaskService service = new com.camangi.android.taskmanager.TaskService();
    				//啟動不同應用程式service com.camangi.android.taskmanager.START_SERVICE 必須設定在manifest中 
    				 startService(new Intent (getString(R.string.intent_taskservice)));    				
    			}
    		}
    	}
    }
    
   
    
    /**
     * 將應用程式清單先依sortapps清單做排序後再排其他應用程式
     */
    private void sortApplications()
    {
    	ArrayList<ApplicationInfo> clone_apps = (ArrayList<ApplicationInfo>) mApplications.clone();
    	
    	ArrayList<ApplicationInfo> sortApps = new ArrayList<ApplicationInfo>();
    	String packageName = "";
    	String checkName = "";
    	ArrayList<Integer> arylistIndex  = new ArrayList<Integer>();
    	
    	boolean[] isFindSortApps = new boolean[sortapps.length]; 
    	for (int i = 0 ; i < sortapps.length; i++)
    	{
    		checkName = sortapps[i];
    		for (int j = 0; j < clone_apps.size(); j++)
    		{
	    		packageName = clone_apps.get(j).packageName;
	    		if (checkName.equals(packageName))
	    		{	    	
	    			if (!isFindSortApps[i])
	    			{
	    				boolean isSearchSortfind = false;
	    				for (int x = 0; x< arylistIndex.size(); x++)
	    				{
	    					if (j == arylistIndex.get(x))
	    					{
	    						isSearchSortfind = true;
	    						break;
	    					}
	    				}
	    				
	    				if (!isSearchSortfind)
	    				{
	    					sortApps.add(clone_apps.get(j));
	    					arylistIndex.add(j);
	    					break;
	    				}
	    				else
	    				{
	    					continue;
	    				}
	    				
	    			}
	    		}
    		}
    	}
    	
    	/*
    	ArrayList<ApplicationInfo> newApps = new ArrayList<ApplicationInfo>();
    	for (int i=0; i < sortApps.size(); i++)
    	{
    		Log.d(LOG_TAG, "sortApps["+i+"]: "+sortApps.get(i).title);
    		String title = (String) sortApps.get(i).title;
    		if (title.equals("Camera") || title.equals("Camcorder"))
    		{
    			
    			arylistIndex.set(i, -1);    			
    		}
    		else
    		{
    			Log.d(LOG_TAG, "newApps.add["+i+"]: "+sortApps.get(i).title);
    			newApps.add(sortApps.get(i));
    		}
    	}
    	
    	for (int i=0; i < newApps.size(); i++)
    	{
    		Log.d(LOG_TAG, "check new apps["+i+"]: "+newApps.get(i).title);
    	}
    	*/
    	
    	
    	for (int i=0; i< clone_apps.size(); i++)
    	{
    		int checkindex =0;
    		boolean isFind = false;
    		for (int j =0; j< arylistIndex.size(); j++)
    		{
    			checkindex = arylistIndex.get(j);
    			if (checkindex != -1)
    			{
    				if (i == checkindex)
    				{
    				isFind = true;
    				break;
    				}
    			}
    		}
    		
    		if (!isFind)
    		{
    			//newApps.add(clone_apps.get(i));
    			sortApps.add(clone_apps.get(i));
    		}
    	}
    	
    	
    
    	mApplications.clear();
    	mApplications = (ArrayList<ApplicationInfo>) sortApps.clone();
    	
    	//mApplications = (ArrayList<ApplicationInfo>) newApps.clone();
    	
    	/*
    	for (int i =0; i< mApplications.size(); i++)
    	{
    		Log.d(LOG_TAG, "sort app["+i+"]: "+mApplications.get(i).packageName);
    		
    	}
    	*/
    	
    	
    	 
    }
    
   
    private LinearLayout DefLayout;
    /**
     * 設定預設應用程式顯示設計
     */
    private void setDefApp()
    {
    	//imgDef01 = (ImageView)	findViewById(R.id.imgDef01);
    	//txtDef01 = (TextView)	findViewById(R.id.txtDef01);
    	//Log.d(LOG_TAG, "setDefApp");
    	DefLayout = (LinearLayout)	findViewById(R.id.DefLayout);
    	DefLayout.removeAllViews();
    	if (defappinfos  != null && defappinfos.size() > 0)
    	{
    		String packageName = "";
    		
    		setDefAppByName(DefLayout, getString(R.string.package_weather));   		
    		setDefAppByName(DefLayout, getString(R.string.package_email));
    		setDefAppByName(DefLayout, getString(R.string.package_music));    		
    		setDefAppByName(DefLayout, getString(R.string.package_browser));
    		setLinkMarket(DefLayout);
    		setDefAppByName(DefLayout, getString(R.string.package_settings));
    	}
    	
    	
    }
   
    private TextView TextView(HomeSample home) {
		// TODO Auto-generated method stub
		return null;
	}

    /**
     * 建立Market連結view 加到預設應用程式DefAppLayout 中
     * @param layout 應用程式view
     */
    private void setLinkMarket(LinearLayout layout)
    {
    	if (layout != null)
    	{
    		DefAppLayout applayout = new DefAppLayout(HomeSample.this);
    		Uri uri = Uri.parse(getString(R.string.link_camangimarket));
    		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    		
    		//applayout.setValues(defappinfos.get(i).intent, defappinfos.get(i).icon, (String) defappinfos.get(i).title);
    		//FIXME:修改Market title
    		layout.addView(applayout.getLinkView(intent, R.drawable.market, getString(R.string.info_market)));
    	}
    }
    
    /**
     * 根據packagename建立view 加到預設應用程式DefAppLayout 中
     * @param layout 應用程式view
     * @param checkPackageName 
     */
	private void setDefAppByName(LinearLayout layout, String checkPackageName)
    {
    	if (layout != null)
    	{
    		//Log.d(LOG_TAG, "setDefAppByName: "+ checkPackageName);
    		
        		String packageName = "";
        		
        		for (int i=0; i < defappinfos.size(); i++)
        		{
        			//Log.d(LOG_TAG, "defapp: "+ defappinfos.get(i).packageName);
        			packageName = defappinfos.get(i).packageName;
        		
        			if (packageName.equals(checkPackageName))
        			{
        				//imgDef01.setImageResource(R.drawable.ic_launcher_home);
        				//imgDef01.setImageDrawable(defappinfos.get(i).icon);
        				//txtDef01.setText(defappinfos.get(i).title);
        				try
        				{
        				DefAppLayout applayout = new DefAppLayout(HomeSample.this);
        				
        				layout.addView(applayout.getView(defappinfos.get(i).intent, defappinfos.get(i).icon, (String) defappinfos.get(i).title));
        				}
        				catch (Exception e)
        				{
        					e.printStackTrace();
        					//Log.d(LOG_TAG, "setDefAppByName error: "+ e.getMessage());
        				}
        			}
        			
        			
        		}
        	
    	}
    }
    /* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		
		super.onStart();
		 //Log.d(LOG_TAG, "onStart");
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.d(LOG_TAG, "onRestart");
		showGridId=0;
		bindApplications();
		
		
	}
	
	/**
	 * 判斷今天為星期幾決定其顏色，目前都固定用灰色
	 * @param img
	 * @param isDown
	 * @param isLeft
	 */
    private void checkDayOfWeek(ImageView img, boolean isDown, boolean isLeft)
    {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(new Date());
    	// Get the weekday and print it
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        //Log.d(LOG_TAG, "dayofweek: "+weekday);
        if (isLeft)
        {
	        switch (calendar.get(Calendar.DAY_OF_WEEK))
	        {
	        case Calendar.SUNDAY:        	
	        	if (isDown)
	        	{        		
	        		img.setImageResource(R.drawable.btn_left_purple_o);	            
	        	}
	        	else
	        	{
	        		img.setImageResource(R.drawable.btn_left_purple);	            
	        	}
	        	
	            break;
	        case Calendar.MONDAY:
	        	if (isDown)
	        	{
	        		img.setImageResource(R.drawable.btn_left_red_o);	        	
	        	}
	        	else
	        	{
	        		img.setImageResource(R.drawable.btn_left_red);	        	
	        	}
	            break;
	        case Calendar.TUESDAY:
	        	if (isDown)
	        	{
	        		img.setImageResource(R.drawable.btn_left_orange_o);	        	
	        	}
	        	else
	        	{
	        		img.setImageResource(R.drawable.btn_left_orange);	        	
	        	}
	            break;
	        case Calendar.WEDNESDAY:
	        	if (isDown)
	        	{
	        		img.setImageResource(R.drawable.btn_left_yellow_o);            	
	        	}
	        	else
	        	{
	        		img.setImageResource(R.drawable.btn_left_yellow);            	
	        	}
	        	
	            break;
	        case Calendar.THURSDAY:
	        	if (isDown)
	        	{
	        		img.setImageResource(R.drawable.btn_left_green_o);            	
	        	}
	        	else
	        	{
	        		img.setImageResource(R.drawable.btn_left_green);            	
	        	}        	
	            break;
	        case Calendar.FRIDAY:
	        	if (isDown)
	        	{
	        		img.setImageResource(R.drawable.btn_left_blue_o);
	        	}
	        	else
	        	{        	
	        		img.setImageResource(R.drawable.btn_left_blue);	        	
	        	}
	            break;
	        case Calendar.SATURDAY:
	        	if (isDown)
	        	{
	        		img.setImageResource(R.drawable.btn_left_indigo_o);	        	
	        	}
	        	else
	        	{
	        		img.setImageResource(R.drawable.btn_left_indigo);	        	
	        	}
	            break;            
	        }
	        
	        if (isDown)
        	{
        		img.setImageResource(R.drawable.left_over);	        	
        	}
        	else
        	{
        		img.setImageResource(R.drawable.left);	        	
        	}
        }
        else
        {
	        switch (calendar.get(Calendar.DAY_OF_WEEK))
	        {
	        case Calendar.SUNDAY:        	
	        	if (isDown)
	        	{        		
	        		img.setImageResource(R.drawable.btn_right_purple_o);	            
	        	}
	        	else
	        	{
	        		img.setImageResource(R.drawable.btn_right_purple);	            
	        	}
	        	
	            break;
	        case Calendar.MONDAY:
	        	if (isDown)
	        	{
	        		img.setImageResource(R.drawable.btn_right_red_o);	        	
	        	}
	        	else
	        	{
	        		img.setImageResource(R.drawable.btn_right_red);	        	
	        	}
	            break;
	        case Calendar.TUESDAY:
	        	if (isDown)
	        	{
	        		img.setImageResource(R.drawable.btn_right_orange_o);	        	
	        	}
	        	else
	        	{
	        		img.setImageResource(R.drawable.btn_right_orange);	        	
	        	}
	            break;
	        case Calendar.WEDNESDAY:
	        	if (isDown)
	        	{
	        		img.setImageResource(R.drawable.btn_right_yellow_o);            	
	        	}
	        	else
	        	{
	        		img.setImageResource(R.drawable.btn_right_yellow);            	
	        	}
	        	
	            break;
	        case Calendar.THURSDAY:
	        	if (isDown)
	        	{
	        		img.setImageResource(R.drawable.btn_right_green_o);            	
	        	}
	        	else
	        	{
	        		img.setImageResource(R.drawable.btn_right_green);            	
	        	}        	
	            break;
	        case Calendar.FRIDAY:
	        	if (isDown)
	        	{
	        		img.setImageResource(R.drawable.btn_right_blue_o);
	        	}
	        	else
	        	{        	
	        		img.setImageResource(R.drawable.btn_right_blue);	        	
	        	}
	            break;
	        case Calendar.SATURDAY:
	        	if (isDown)
	        	{
	        		img.setImageResource(R.drawable.btn_right_indigo_o);	        	
	        	}
	        	else
	        	{
	        		img.setImageResource(R.drawable.btn_right_indigo);	        	
	        	}
	            break;            
	        }
	        
	        if (isDown)
        	{
        		img.setImageResource(R.drawable.right_over);	        	
        	}
        	else
        	{
        		img.setImageResource(R.drawable.right);	        	
        	}
        }
    }
    
    /**
     * 設定動畫動作
     */
    private void setAnimations()
    {
    	mGridEntry_left = AnimationUtils.loadAnimation(this, R.anim.right_nec_grid_entry);
        mGridExit_left = AnimationUtils.loadAnimation(this, R.anim.right_nec_grid_exit);
        mGridEntry_right = AnimationUtils.loadAnimation(this, R.anim.left_nec_grid_entry);
        mGridExit_right = AnimationUtils.loadAnimation(this, R.anim.left_nec_grid_exit);
        //mGridEntry = AnimationUtils.loadAnimation(this, R.anim.grid_entry);
        //mGridExit = AnimationUtils.loadAnimation(this, R.anim.grid_exit);
        imgFade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imgFade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        
        layoutFade_in = AnimationUtils.loadAnimation(this, R.anim.layout_fade_in);
        layoutFade_out = AnimationUtils.loadAnimation(this, R.anim.layout_fade_out);
        
        //music
        music_in = AnimationUtils.loadAnimation(this, R.anim.music_in);
        music_out = AnimationUtils.loadAnimation(this, R.anim.music_out);
        musictitle_in = AnimationUtils.loadAnimation(this, R.anim.music_title_in);
        musictitle_out = AnimationUtils.loadAnimation(this, R.anim.music_title_out);
    }
    
    //TODO:執行緒判斷顯示時間間隔
    private Date showDate = null;    
    /**
     * 開始執行緒(偵測顯示左右圖示時間)
     */
    private void startThread()
    {    	
    	
    	showDate = new Date();
    	Thread thread = new Thread(runsleep);
        thread.start();
		
    }
    private static final long HIDE_TIMESPAN = 5 * 1000;
    /**
     * 執行緒回傳後，判斷其時間間隔是否大於5秒，若是則隱藏左右切換Grid圖示動畫，不是則重複偵測
     */
    private Handler handler = new Handler()
    {

		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Date checkDate = new Date();
			long diff = checkDate.getTime() - showDate.getTime();
			if (diff >= HIDE_TIMESPAN)
			{
				startFadeOutAni();
			}
			else
			{
				startThread();
			}
		}
    	
    };
    
    /**
     * 5秒後執行回傳動作
     */
    private Runnable runsleep = new Runnable()
	{
	    public void run() 
	    {
	        try {
				Thread.sleep(5000);
				handler.sendEmptyMessage(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	    }
	};
	
    
    
    /**
     * 開始執行淡入動畫
     */
    private void startFadeInAni()
    {
    	img_left.startAnimation(imgFade_in);
		img_right.startAnimation(imgFade_in);
		//pageLayout.startAnimation(layoutFade_in);
    }
    
    /**
     * 開始執行淡出動畫
     */
    private void startFadeOutAni()
    {
    	img_left.startAnimation(imgFade_out);
		img_right.startAnimation(imgFade_out);
		//pageLayout.startAnimation(layoutFade_out);
    }
    
    /**
     * 顯示左右切換Grid圖示
     */
    private void showImgBtn()
    {
    	if (!isShowMoverlImg)
		{
			img_left.setVisibility(View.VISIBLE);
			img_right.setVisibility(View.VISIBLE);	
			runLayoutShowAni();
			//page_left.setVisibility(View.VISIBLE);
			//page_right.setVisibility(View.VISIBLE);
			isShowMoverlImg = true;
			
		}
    }
    
    /**
     * 隱藏左右切換Grid圖示
     */
    private void hideImgBtn()
    {
    	if (isShowMoverlImg)
		{
			img_left.setVisibility(View.INVISIBLE);
			img_right.setVisibility(View.INVISIBLE);
			runLayoutHideAni();
			//page_left.setVisibility(View.INVISIBLE);
			//page_right.setVisibility(View.INVISIBLE);
			isShowMoverlImg = false;
		}
    }
    /**
     * 執行隱藏頁面顯示器動畫
     */    
    private void runLayoutHideAni()
    {
    	if (pageLayout.getVisibility()==View.VISIBLE)
		{
			pageLayout.startAnimation(layoutFade_out);
			pageLayout.setVisibility(View.INVISIBLE);
		}
    }
    /**
     * 執行顯示頁面顯示器動畫
     */
    private void runLayoutShowAni()
    {
    	if (pageLayout.getVisibility()==View.INVISIBLE)
    	{
    		pageLayout.setVisibility(View.VISIBLE);
    		pageLayout.startAnimation(layoutFade_in);
    	}
    }
    
    private int showMaxGridSize = 0;    
    /**
     * 計算Grid的個數 
     */
    private void countGridMaxSize()
    {
    	if (mApplications != null)
    	{
    		if (mApplications.size() > 0)
    		{
    			if (mApplications.size() % 12 ==0)
    			{
    				showMaxGridSize = (mApplications.size() / SHOWAPPCOUNT);
    			}
    			else
    			{
    				showMaxGridSize = (mApplications.size() / SHOWAPPCOUNT)+1;
    			}
    		}
    	}
    	//Log.d(LOG_TAG, "mApplications size: "+mApplications.size());
    	//Log.d(LOG_TAG, "showMaxGridSize: "+showMaxGridSize);
    	
    	//createPageImg();
    }
    
    private int maxGridViewSize = 22;
    private boolean isCircle = false;
    private boolean isSquare = false;
    private boolean isShowText = false;    
    private void createPageImg()
    {
    	
    	//pageLayout.setLayoutParams(new LayoutParams(200, LayoutParams.WRAP_CONTENT));
    	if (mApplications!= null && mApplications.size() > 0)
    	{
    		Log.d(LOG_TAG, "showMaxGridSize: "+ showMaxGridSize);
    		//create page count
    		if (pageLayout.getChildCount()!=0)
    		{
    			pageLayout.removeAllViews();
    		}
    		/*
    		isShowText = true;
    		if (isShowText)
    		{
    			pageLayout.removeAllViews();
    			String showpage = (showGridId+1) +"/" +showMaxGridSize;
    			page_left.setText(showpage);
    			page_right.setText(showpage);
    		}
    		else
    		{
    		isCircle = true;
	    		if (showMaxGridSize < maxGridViewSize)
	    		{
		    		for (int i=0; i < showMaxGridSize; i++)
		    		{
		    			
			    		ImageView img = new ImageView(Home.this);
			    		if (i == showGridId)
			    		{
			    			if (isCircle)
			    			{
			    				img.setImageResource(R.drawable.nowpage_big_c);
			    			}
			    			else
			    			{
			    				img.setImageResource(R.drawable.nowpage_big_s);
			    			}
			    		}
			    		else
			    		{
			    			if (isCircle)
			    			{
			    				img.setImageResource(R.drawable.pagebg_c);
			    			}
			    			else
			    			{
			    				img.setImageResource(R.drawable.pagebg_s);
			    			}
			    		}
			    		LayoutParams  params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			    		img.setLayoutParams(params);
			    		pageLayout.addView(img); 
		    		}
	    		}
    		}
    		*/
    		
    		TextView ftxt = new TextView(HomeSample.this);
    		ftxt.setText("");
    		ftxt.setWidth(10);
    		pageLayout.addView(ftxt);
    		if (showMaxGridSize > 1)
    		{
	    		if (showMaxGridSize < maxGridViewSize)
	    		{
	    			
	    			
		    		for (int i=0; i < showMaxGridSize; i++)
		    		{
		    			
			    		ImageView img = new ImageView(HomeSample.this);
			    		if (i == showGridId)
			    		{
			    			img.setImageResource(R.drawable.square);
			    		}
			    		else
			    		{
			    			img.setImageResource(R.drawable.square_o);
			    		}
			    		LayoutParams  params = new LayoutParams(25, 25);
			    		img.setId(i);
			    		img.setLayoutParams(params);
			    		img.setOnClickListener(new ImageView.OnClickListener(){
	
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Log.d(LOG_TAG, "change showgridid:"+showGridId);
								Log.d(LOG_TAG, "change gridid:"+v.getId());
								showDate = new Date();
								if (isShowMoverlImg)
								{
									if (v.getId() > showGridId)
									{
										runShowGridView(v.getId(), ANIMATION_RIGHT);
									}
									else if (v.getId() < showGridId)
									{
										
										runShowGridView(v.getId(), ANIMATION_LEFT);
									}
									else
									{
										
									}
								}
							}});
			    		pageLayout.addView(img); 
			    		TextView btxt = new TextView(HomeSample.this);
			    		btxt.setText("");
			    		btxt.setWidth(10);
			    		pageLayout.addView(btxt);
		    		}
	    		}
    		}
    	}
    	
    }
    private static final int ANIMATION_LEFT = 0;
    private static final int ANIMATION_RIGHT = 1;
    /**
     * 執行顯示應用程式GridView 動作
     * @param gid
     * @param ACTION
     */
    private void runShowGridView(int gid, int ACTION)
    {
    	showGridView(gid);
    	mGridback.setVisibility(View.VISIBLE);
    	
    	switch(ACTION)
    	{
    	case ANIMATION_LEFT:    		
										
			mGrid.startAnimation(mGridExit_left);	
			mGridback.setAdapter(new Applications_Adapter(HomeSample.this, arylistInfo));		
			mGridback.startAnimation(mGridEntry_left);
			
    		break;
    	case ANIMATION_RIGHT:    		
									
			mGrid.startAnimation(mGridExit_right);	
			mGridback.setAdapter(new Applications_Adapter(HomeSample.this, arylistInfo));		
			mGridback.startAnimation(mGridEntry_right);
			
    		break;
    	}
    	runLayoutShowAni();
    }
    /**
     * 設定對應於View中的變數
     */
    private void setfindViews()
    {
    	 if (mGrid == null) {
             mGrid = (GridView) findViewById(R.id.all_apps);
         }
         if (mGridback == null)
         {
         	mGridback = (GridView)	findViewById(R.id.all_apps_back);
         }
    	//favorite_Gallery = (Gallery) findViewById(R.id.myGallery);
    	
    	img_left = (ImageView)		findViewById(R.id.img_left);
    	img_right = (ImageView)	findViewById(R.id.img_right);
    	
    	homeLayout = (LinearLayout)	findViewById(R.id.home);
    	
    	//DefAppLayout = (LinearLayout)	findViewById(R.id.DefAppLayout);
    	//search
    	img_search = (ImageView)	findViewById(R.id.img_search);
    	search_keyword = (EditText)	findViewById(R.id.search_keyword);    	
    	searchLayout = (AbsoluteLayout) findViewById(R.id.searchLayout);
    	
    	//page
    	pageLayout = (LinearLayout)	findViewById(R.id.pageLayout);    	
    	page_left = (TextView)	findViewById(R.id.page_left);
    	page_right = (TextView)	findViewById(R.id.page_right);
    	
    	//music
    	img_music = (ImageView)	findViewById(R.id.img_music);
    	img_pause = (ImageView)	findViewById(R.id.img_pause);
    	img_play = (ImageView)	findViewById(R.id.img_play);    	
    	info_music = (TextView)	findViewById(R.id.info_music);
    	musicLayout = (LinearLayout) findViewById(R.id.musicLayout);
    	musictitleLayout = (LinearLayout)	findViewById(R.id.musictitleLayout);
    }
    
    
    /**
     * 執行顯示左右切換Grid圖示流程
     */
    private void RunShowImgProcess()
    {
    	if (!isShowMoverlImg)
		{
    		/*
    		if (showMaxGridSize > 1)
    		{
    		*/
    			showImgBtn();				
    			startFadeInAni();
    			startThread();
    		/*
    		}
    		*/
		}
    }
    
    private boolean isMusicOn = false;
    /**
     * 設定監聽事件
     */
    private void setListeners()
    {
    	/*
    	favorite_Gallery.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction()== MotionEvent.ACTION_DOWN)
				{
					Log.d(LOG_TAG, "favorite_Gallery touch");
					RunShowImgProcess();
				}
				return false;
			}
		})
		*/;
		search_keyword.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				//Log.d(LOG_TAG, event.toString());  
				if (event.getAction()==KeyEvent.ACTION_UP)
				{
					if (keyCode == 66)
					{
						searchKeyword();
					}
				}
				return false;
			}
		});
		img_search.setOnTouchListener(new ImageView.OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					//Log.d(LOG_TAG, "mouse down");
					img_search.setImageResource(R.drawable.go_over);
					break;
				case MotionEvent.ACTION_UP:
					//Log.d(LOG_TAG, "mouse up");
					img_search.setImageResource(R.drawable.go);	
					searchKeyword();
					break;
				}				
				return true;
			}});
		searchLayout.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				return true;
			}
		});
    	homeLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Log.d(LOG_TAG, "home click");
				RunShowImgProcess();
			}
		});
    	
    	img_left.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!mBlockAnimation)
		    	{
					showDate = new Date();
					mBlockAnimation = true;
					if (showGridId > 0 )
					{
						showGridId--;
					}
					else 
					{
						showGridId = showMaxGridSize-1;
					}
					Log.d(LOG_TAG, "showGridId:"+showGridId);
				
					runShowGridView(showGridId, ANIMATION_LEFT);
					
		    	}
				
			}});
    	img_left.setOnTouchListener(new Button.OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					checkDayOfWeek(img_left, true, true);
					break;
				case MotionEvent.ACTION_UP:
					checkDayOfWeek(img_left, false, true);
					break;
				}				
				
				return false;
			}});
    	img_right.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!mBlockAnimation)
		    	{
					showDate = new Date();
					mBlockAnimation = true;
					if (showGridId < (showMaxGridSize-1))
					{
						showGridId++;
					}
					else
					{
						showGridId= 0;
					}
					//Log.d(LOG_TAG, "showGridId:"+showGridId);
					
					runShowGridView(showGridId, ANIMATION_RIGHT);
		    	}
			}});
    	img_right.setOnTouchListener(new Button.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					checkDayOfWeek(img_right, true, false);
					break;
				case MotionEvent.ACTION_UP:
					checkDayOfWeek(img_right, false, false);
					break;
				}				
				return false;
			}});
    	
        mGridEntry_left.setAnimationListener(new ShowGrid_left());
        mGridExit_left.setAnimationListener(new HideGrid_left());
        mGridEntry_right.setAnimationListener(new ShowGrid_right());
        mGridExit_right.setAnimationListener(new HideGrid_right());
        
        imgFade_in.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}});
        
        imgFade_out.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				hideImgBtn();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}});
        //music control
        img_music.setOnClickListener(new ImageView.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/**
				 * 檢查是否有記憶卡，若有建立音樂清單
				 */
				Log.d(LOG_TAG, "isMusicOn: "+isMusicOn);
				if (!isMusicOn)
				{
					
					isMusicOn = true;
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
					{
						
						if (!bIsRelease)
						{
							if (myPlayer != null)
							{
								if (myPlayer.isPlaying())
								{
									if (musicLayout.getVisibility()== View.INVISIBLE)
									{
										musicLayout.setVisibility(View.VISIBLE);
										musicLayout.startAnimation(music_in);
									}
									if (musictitleLayout.getVisibility() == View.GONE)
									{	
										musictitleLayout.setVisibility(View.VISIBLE);
										musictitleLayout.startAnimation(musictitle_in);								
									}
								}
							}
							else
							{
								if (bIsPaused)
								{
									if (musicLayout.getVisibility()== View.INVISIBLE)
									{
										musicLayout.setVisibility(View.VISIBLE);
										musicLayout.startAnimation(music_in);
									}
									if (musictitleLayout.getVisibility() == View.GONE)
									{	
										musictitleLayout.setVisibility(View.VISIBLE);
										musictitleLayout.startAnimation(musictitle_in);								
									}
								}
								else
								{
									String rootpath = "/sdcard/";
									if (arylistMusic == null)
							    	{
							    		arylistMusic =new ArrayList<String>();
							    	}
							    	arylistMusic.clear();
									getSD(rootpath);
									if (arylistMusic != null && arylistMusic.size() > 0)
									{
										if (musicLayout.getVisibility()== View.INVISIBLE)
										{
											musicLayout.setVisibility(View.VISIBLE);
											musicLayout.startAnimation(music_in);
										}
									}
								}
							}
						}
						else
						{
							String rootpath = "/sdcard/";
							if (arylistMusic == null)
					    	{
					    		arylistMusic =new ArrayList<String>();
					    	}
					    	arylistMusic.clear();
							getSD(rootpath);
							if (arylistMusic != null && arylistMusic.size() > 0)
							{
								if (musicLayout.getVisibility()== View.INVISIBLE)
								{
									musicLayout.setVisibility(View.VISIBLE);
									musicLayout.startAnimation(music_in);
								}
							}
						}
					}
					else
					{
						Toast.makeText(HomeSample.this, "Please input SD card", Toast.LENGTH_LONG).show();
					}
				}
				else
				{
					isMusicOn = false;
					if (musictitleLayout.getVisibility() == View.VISIBLE)
					{					
						musictitleLayout.startAnimation(musictitle_out);
						musictitleLayout.setVisibility(View.GONE);
					}
					if (musicLayout.getVisibility()== View.VISIBLE)
					{
						musicLayout.startAnimation(music_out);	
						musicLayout.setVisibility(View.INVISIBLE);
					}					
				}
			}});
        img_pause.setOnClickListener(new ImageView.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try
				{
					if (!bIsRelease)
					{
						if (myPlayer != null)
						{
							if (bIsPaused)
							{
								//play
								myPlayer.start();
								bIsPaused = false;
							}
							else
							{
								//stop
								myPlayer.pause();
								bIsPaused = true;
							}
						}
					}
				}
				catch (Exception e)
				{
					Log.d(LOG_TAG, "pause error: "+e.getMessage());
					e.printStackTrace();
				}
			}});
        img_play.setOnClickListener(new ImageView.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/**
				 * 隨機播放一首歌曲
				 */
				if (arylistMusic != null && arylistMusic.size() > 0)
				{
					playRandomMusic();
				}
				
				
			}});
        
        
        
    }
    
    public MediaPlayer myPlayer;
   
    private void playRandomMusic()
    {
    	Random rand = new Random();
    	int index = rand.nextInt(arylistMusic.size());
		Log.d(LOG_TAG, "random: "+index);
		
		try
		{
			/*
			if (myPlayer.isPlaying())
			{
				myPlayer.reset();
			}
			*/
			
			myPlayer = new MediaPlayer();
			bIsRelease = false;
			myPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					try
					{
						myPlayer.release();
						if (musictitleLayout.getVisibility() == View.VISIBLE)
						{					
							musictitleLayout.startAnimation(musictitle_out);
							musictitleLayout.setVisibility(View.GONE);
						}
								
						bIsRelease = true;
						bIsPaused = false;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}});
			//MediaPlayer 讀取SD 檔案			
			myPlayer.reset();
			myPlayer.setDataSource(arylistMusic.get(index));
			myPlayer.prepare();
			  
			myPlayer.start();
			if (musictitleLayout.getVisibility() == View.GONE)
			{
			musictitleLayout.setVisibility(View.VISIBLE);
			musictitleLayout.startAnimation(musictitle_in);
			}
			String info = arylistMusic.get(index);
			info = info.substring(info.lastIndexOf("/")+1, info.length());
			info_music.setText(info);
			
		}
		catch (Exception e)
		{
			Log.d(LOG_TAG, "playRandomMusic error: "+e.getMessage());
			e.printStackTrace();
		}
		
    }
    ArrayList<String> arylistMusic;
    /**
     * 找出SD卡裡面所有檔案
     */
    private void getSD(String path)
    {
    	
    	
    	//設定目前所在路徑
    	File f = new File(path);
    	File[] files = f.listFiles();
    	
    	for (int i = 0; i < files.length; i++)
    	{
    		File file = files[i];
    		if (file.canRead())
    		{
    			if (file.isDirectory())
    			{
    				getSD(file.getPath());
    			}
    			else
    			{
    				if (getMusicFile(file.getPath()))
    				{
    					arylistMusic.add(file.getPath());
    					Log.d(LOG_TAG, "add arylistMusic: "+file.getPath());
    				}
    			}
    		}
    		
    	}
    }
    private boolean getMusicFile(String fName)
    {
    	boolean re;
    	
    	//取得副檔名
    	String end = fName.substring(fName.lastIndexOf(".")+1, fName.length()).toLowerCase();
    	
    	if (end.equals("mp3"))
    	{
    		re = true;
    	}
    	else
    	{
    		re = false;
    	}
    	
    	return re;
    }
   
    /**
     * 根據目前語系設定決定其搜尋結果
     */
    private void searchKeyword()
    {
    	if (search_keyword.getText().length()== 0)
		{
			Toast.makeText(HomeSample.this, getString(R.string.info_input_keyword), Toast.LENGTH_LONG).show();
		}
		else
		{
			//check lanaguage
			String local = "";
			Resources res = HomeSample.this.getResources();
			Configuration conf = res.getConfiguration();
			if (conf.locale.equals(Locale.TRADITIONAL_CHINESE))
			{
				local = "zh-TW";
			}
			else if (conf.locale.equals(Locale.SIMPLIFIED_CHINESE))
			{
				local = "zh-CN";
			}			
			else if (conf.locale.equals(Locale.JAPANESE))
			{
				local= "ja";
			}
			else if (conf.locale.equals(Locale.FRENCH))
			{
				local= "fr";
			}
			else if (conf.locale.equals(Locale.GERMAN))
			{
				local= "de";
			}
			else
			{
				local= "en";
			}
			
			
			try {
				
				String urlStr = "http://www.google.com.tw/search?hl="+local+"&q="+URLEncoder.encode(search_keyword.getText().toString(),"UTF-8");
								
				Uri uri = Uri.parse(urlStr);				
				Intent intent = new Intent(Intent.ACTION_VIEW,uri);
				startActivity(intent);	
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//urlStr = Uri.encode(urlStr);
			
		}
    }
    private ArrayList<ApplicationInfo> arylistInfo;
    /**
     * 建立顯示於GridView 中的應用程式清單
     */
    private void showGridView(int gid)
    {
    	//mGrid.setAdapter(new ApplicationsAdapter(this, arylistInfo));
    	showGridId = gid;
    	int startGridid = gid;
    	int startid = 0;
    	int endid =0;
    	Log.d(LOG_TAG, "showGridId: "+showGridId);
    	if (startGridid ==0)
    	{
    		startid = 0;
    		if (mApplications.size()< SHOWAPPCOUNT)
    		{
    			endid = mApplications.size();
    		}
    		else
    		{
    			endid = SHOWAPPCOUNT;
    		}
    	}
    	else if (startGridid < (showMaxGridSize-1))
    	{
    		startid = gid*SHOWAPPCOUNT;
    		endid = startid+SHOWAPPCOUNT;
    	}
    	else
    	{
    		startid = gid*SHOWAPPCOUNT;
    		endid= mApplications.size();
    	}
    	Log.d(LOG_TAG, "startid: "+startid+", endid: "+endid);
    	Log.d(LOG_TAG, "mApplications size: "+mApplications.size());
    	arylistInfo = new ArrayList<ApplicationInfo>();
    	if (mApplications != null)
    	{
    		if (mApplications.size() > 0)
    		{
    			for(int i=startid; i< endid; i++)
    			{
    				arylistInfo.add(mApplications.get(i));
    			}
    		}
    	}
    	
    	//set nowpage
    	createPageImg();
    	
    	
    		
    	
    	   
    	
        
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Close the menu
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            getWindow().closeAllPanels();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove the callback for the cached drawables or we leak
        // the previous Home screen on orientation change
        final int count = mApplications.size();
        for (int i = 0; i < count; i++) {
            mApplications.get(i).icon.setCallback(null);
        }

        unregisterReceiver(mWallpaperReceiver);
        unregisterReceiver(mApplicationsReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //bindRecents();
        Log.d(LOG_TAG, "onResume");
        if (musictitleLayout.getVisibility() == View.VISIBLE)
		{					
			musictitleLayout.startAnimation(musictitle_out);
			musictitleLayout.setVisibility(View.GONE);
		}
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        final boolean opened = state.getBoolean(KEY_SAVE_GRID_OPENED, false);
        /*
        if (opened) {
            showApplications(false);
        }
        */
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SAVE_GRID_OPENED, mGrid.getVisibility() == View.VISIBLE);
    }

    /**
     * Registers various intent receivers. The current implementation registers
     * only a wallpaper intent receiver to let other applications change the
     * wallpaper.
     */
    private void registerIntentReceivers() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED);
        registerReceiver(mWallpaperReceiver, filter);

        filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        registerReceiver(mApplicationsReceiver, filter);
    }

    /**
     * Creates a new appplications adapter for the grid view and registers it.
     */
    private void bindApplications() {
    	
    	sortApplications();
       
        
        ArrayList<ApplicationInfo>	arylistInfo = new ArrayList<ApplicationInfo>();
        if (mApplications != null && mApplications.size() > 0)
        {
        	if (mApplications.size() > 12)
        	{
		        for (int i = 0; i< 12;i++)
		        {
		        	arylistInfo.add(mApplications.get(i));
		        }
        	}
        	else
        	{
        		 for (int i = 0; i< mApplications.size();i++)
 		        {
 		        	arylistInfo.add(mApplications.get(i));
 		        }
        	}
        mGrid.setAdapter(new Applications_Adapter(this, arylistInfo));
        mGrid.setSelection(0);
        mGrid.setVisibility(View.VISIBLE);
        
        mGridback.setAdapter(new Applications_Adapter(this, arylistInfo));
        mGridback.setSelection(0);
        mGridback.setVisibility(View.INVISIBLE);
        
       
	        
	        setDefApp();
        }
        
        countGridMaxSize();
        createPageImg();
        /*
        if (mApplicationsStack == null) {
            mApplicationsStack = (ApplicationsStackLayout) findViewById(R.id.faves_and_recents);
        }
       */
    	/*
    	if (singleLayout == null)
    	{
    		singleLayout = (LinearLayout)	findViewById(R.id.single_layout);    		
    	}
    	if (doubleLayout == null)
    	{
    		doubleLayout = (LinearLayout)	findViewById(R.id.double_layout);
    	}
    	*/
    	//setApplicationInLayout();
    }

    /**
     * 設定應用程式顯示設計
     */
    private void setApplicationInLayout()
    {
    	
    	for (int position=0; position<mApplications.size(); position++)
    	{
    		
    	final ApplicationInfo info = mApplications.get(position);
        Drawable icon = info.icon;       
        Rect mOldBounds = new Rect();
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

        TextView textView = new TextView(HomeSample.this);                    
        textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
        textView.setPadding(8, 8, 8, 8);
        textView.setWidth(80);
        textView.setHeight(80);
        textView.setTextSize(12);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.CENTER_HORIZONTAL+Gravity.CLIP_VERTICAL);
        textView.setBackgroundResource(R.drawable.appbg);
        String showtitle = null;
        if (info.title.length()>12)
        {
        	showtitle = info.title.subSequence(0, 11)+"...";
        }
        else
        {
        	showtitle = info.title.toString();
        }
        textView.setText(showtitle);
        TextView space = new TextView(HomeSample.this); 
        space.setWidth(10);
        space.setHeight(10);
        
	        if ((position % 2) == 1)
	        {
	        	doubleLayout.addView(textView);
	        	doubleLayout.addView(space);
	        }
	        else
	        {
	        	singleLayout.addView(textView);
	        	singleLayout.addView(space);
	        }
    	}
    }
    
    /**
     * Binds actions to the various buttons.
     */
    private void bindButtons() {
        //mShowApplications = findViewById(R.id.show_all_apps);
        //mShowApplications.setOnClickListener(new ShowApplications());
        //mShowApplicationsCheck = (CheckBox) findViewById(R.id.show_all_apps_check);

        mGrid.setOnItemClickListener(new ApplicationLauncher());
        mGrid.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction()== MotionEvent.ACTION_DOWN)
				{
					//Log.d(LOG_TAG, "mGrid touch");
					RunShowImgProcess();
				}
				return false;
			}
		});
        mGridback.setOnItemClickListener(new ApplicationLauncher());
        mGridback.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction()== MotionEvent.ACTION_DOWN)
				{
					//Log.d(LOG_TAG, "mGridback touch");
					RunShowImgProcess(); 
				}
				return false;
			}
		});
    }

    /**
     * When no wallpaper was manually set, a default wallpaper is used instead.
     */
    private void setDefaultWallpaper() {
    	/*
        if (!mWallpaperChecked) {
            Drawable wallpaper = peekWallpaper();
            if (wallpaper == null) {
                try {
                    clearWallpaper();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Failed to clear wallpaper " + e);
                }
            } else {
                  
            	//getWindow().setBackgroundDrawable(new ClippedDrawable(wallpaper));
            	
                homeLayout.setBackgroundDrawable(wallpaper);
               
            }
            mWallpaperChecked = true;
        }
        */
    	try
    	{
    		homeLayout.setBackgroundDrawable(getWallpaper());
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }

    /**
     * Refreshes the favorite applications stacked over the all apps button.
     * The number of favorites depends on the user.
     */
    private void bindFavorites(boolean isLaunching) {
        if (!isLaunching || mFavorites == null) {

            if (mFavorites == null) {
                mFavorites = new LinkedList<ApplicationInfo>();
            } else {
                mFavorites.clear();
            }
            //mApplicationsStack.setFavorites(mFavorites);            
            
            FileReader favReader;

            // Environment.getRootDirectory() is a fancy way of saying ANDROID_ROOT or "/system".
            final File favFile = new File(Environment.getRootDirectory(), DEFAULT_FAVORITES_PATH);
            try {
                favReader = new FileReader(favFile);
            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, "Couldn't find or open favorites file " + favFile);
                return;
            }

            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            final PackageManager packageManager = getPackageManager();

            try {
                final XmlPullParser parser = Xml.newPullParser();
                parser.setInput(favReader);

                beginDocument(parser, TAG_FAVORITES);

                ApplicationInfo info;

                while (true) {
                    nextElement(parser);

                    String name = parser.getName();
                    if (!TAG_FAVORITE.equals(name)) {
                        break;
                    }

                    final String favoritePackage = parser.getAttributeValue(null, TAG_PACKAGE);
                    final String favoriteClass = parser.getAttributeValue(null, TAG_CLASS);

                    final ComponentName cn = new ComponentName(favoritePackage, favoriteClass);
                    intent.setComponent(cn);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    info = getApplicationInfo(packageManager, intent);
                    if (info != null) {
                        info.intent = intent;
                        mFavorites.addFirst(info);
                    }
                }
            } catch (XmlPullParserException e) {
                //Log.w(LOG_TAG, "Got exception parsing favorites.", e);
            } catch (IOException e) {
                //Log.w(LOG_TAG, "Got exception parsing favorites.", e);
            }
        }

        //mApplicationsStack.setFavorites(mFavorites);
    }

    private static void beginDocument(XmlPullParser parser, String firstElementName)
            throws XmlPullParserException, IOException {

        int type;
        while ((type = parser.next()) != XmlPullParser.START_TAG &&
                type != XmlPullParser.END_DOCUMENT) {
            // Empty
        }

        if (type != XmlPullParser.START_TAG) {
            throw new XmlPullParserException("No start tag found");
        }

        if (!parser.getName().equals(firstElementName)) {
            throw new XmlPullParserException("Unexpected start tag: found " + parser.getName() +
                    ", expected " + firstElementName);
        }
    }

    private static void nextElement(XmlPullParser parser) throws XmlPullParserException, IOException {
        int type;
        while ((type = parser.next()) != XmlPullParser.START_TAG &&
                type != XmlPullParser.END_DOCUMENT) {
            // Empty
        }
    }

    /**
     * Refreshes the recently launched applications stacked over the favorites. The number
     * of recents depends on how many favorites are present.
     */
    private void bindRecents() {
        final PackageManager manager = getPackageManager();
        final ActivityManager tasksManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        final List<ActivityManager.RecentTaskInfo> recentTasks = tasksManager.getRecentTasks(
                MAX_RECENT_TASKS, 0);

        final int count = recentTasks.size();
        final ArrayList<ApplicationInfo> recents = new ArrayList<ApplicationInfo>();

        for (int i = count - 1; i >= 0; i--) {
            final Intent intent = recentTasks.get(i).baseIntent;

            if (Intent.ACTION_MAIN.equals(intent.getAction()) &&
                    !intent.hasCategory(Intent.CATEGORY_HOME)) {

                ApplicationInfo info = getApplicationInfo(manager, intent);
                if (info != null) {
                    info.intent = intent;
                    if (!mFavorites.contains(info)) {
                        recents.add(info);
                    }
                }
            }
        }

        //mApplicationsStack.setRecents(recents);
        
        //Log.d(LOG_TAG, "setRecents");
        ArrayList<ApplicationInfo> arylistshowAdapter = new ArrayList<ApplicationInfo>();
        int scount = 0; 
        for (int i = recents.size()-1; i > 0 ; i--)
        {
        	if (scount < 7)
        	{
        		arylistshowAdapter.add(recents.get(i));
        		scount++;
        	}
        	else
        	{
        		break;
        	}
        }
        
        Recents_Adapter fadapter = new Recents_Adapter(this, recents); 
        //Recents_Adapter fadapter = new Recents_Adapter(this, arylistshowAdapter);
        //Log.d(LOG_TAG, "fadapter ok");
        /*
        try
        {
        	favorite_Gallery.setAdapter(fadapter);
        	if (recents.size()> 0)
        	{
        		int center = recents.size()/2;
        		favorite_Gallery.setSelection(center);
        	}
        }
        catch (Exception e)
        {
        	Log.d(LOG_TAG, "error: "+e.getMessage());
        }
        Log.d(LOG_TAG, "favorite_Gallery ok");
        */
    }

    private static ApplicationInfo getApplicationInfo(PackageManager manager, Intent intent) {
        final ResolveInfo resolveInfo = manager.resolveActivity(intent, 0);

        if (resolveInfo == null) {
            return null;
        }

        final ApplicationInfo info = new ApplicationInfo();
        final ActivityInfo activityInfo = resolveInfo.activityInfo;
        info.icon = activityInfo.loadIcon(manager);
        if (info.title == null || info.title.length() == 0) {
            info.title = activityInfo.loadLabel(manager);
        }
        if (info.title == null) {
            info.title = "";
        }
        return info;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
                case KeyEvent.KEYCODE_HOME:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_WALLPAPER_SETTINGS, 0, R.string.menu_wallpaper)
                 .setIcon(android.R.drawable.ic_menu_gallery)
                 .setAlphabeticShortcut('W');
        /*
        menu.add(0, MENU_SEARCH, 0, R.string.menu_search)
                .setIcon(android.R.drawable.ic_search_category_default)
                .setAlphabeticShortcut(SearchManager.MENU_KEY);
        */
        menu.add(0, MENU_SETTINGS, 0, R.string.menu_settings)
                .setIcon(android.R.drawable.ic_menu_preferences)
                .setIntent(new Intent(android.provider.Settings.ACTION_SETTINGS));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_WALLPAPER_SETTINGS:
                startWallpaper();
                return true;
            case MENU_SEARCH:
                onSearchRequested();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 開啟桌布啟動器，出現桌布選擇菜單
     */
    private void startWallpaper() {
        final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
        startActivity(Intent.createChooser(pickWallpaper, getString(R.string.menu_wallpaper)));
    }
    
    

    /**
     * Loads the list of installed applications in mApplications.
     */
    ArrayList<ApplicationInfo>  showApplications;  
    private void loadApplications(boolean isLaunching) {
    	//Log.d(LOG_TAG, "loadApplications: "+isLaunching);
    	
    	/*
        if (isLaunching && mApplications != null) {
        	Log.d(LOG_TAG, "loadApplications return ");
            return;
        }
		*/
    	
        PackageManager manager = getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        //Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
        

        if (apps != null) {
            final int count = apps.size();

            if (mApplications == null) {
                mApplications = new ArrayList<ApplicationInfo>(count);
            }
            mApplications.clear();
           
           if (defappinfos == null)
           {
        	   defappinfos = new ArrayList<ApplicationInfo>();
           }
           defappinfos.clear();
        	   
            for (int i = 0; i < count; i++) {
                ApplicationInfo application = new ApplicationInfo();
                ResolveInfo info = apps.get(i);

                application.title = info.loadLabel(manager);
                //Log.d(LOG_TAG, "application title: "+application.title);
                
                
                application.setActivity(new ComponentName(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name),
                        Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                
                

                
                String packageName = info.activityInfo.applicationInfo.packageName;
                application.packageName = packageName;
                if (packageName.equals(getString(R.string.package_music)))
                {
                	application.icon = getResources().getDrawable(R.drawable.music);
                }
                else
                {
                	application.icon = info.activityInfo.loadIcon(manager);	
                }
                
                //mApplications.add(application);
                createShowApps(packageName, application);
                createDefApps(packageName, application);
               
                //Log.d(LOG_TAG, "packageName"+"["+i+"]: "+info.activityInfo.applicationInfo.packageName);
                //checkDefApps(packageName, application); 
            }
        }
         
        //setDefApp();
    }
    
    /**
     * 建立預設應用程式(下方列)清單 
     * @param packageName packagename ex: com.camangi.home
     * @param Application ApplicationInfo
     */
    private void createDefApps(String packageName, ApplicationInfo Application)
    {
    	//Log.d(LOG_TAG, "createDefApps");
    	if (Application != null)
    	{
	    	if (packageName != null && packageName.length() > 0)
	    	{	    		 
	    		for (int i = 0; i< defapps.length; i++)
	    	    {
	    	     		if (packageName.equals(defapps[i]))
	    	    		{
	    	    			//Log.d(LOG_TAG, "add def app packageName: "+ packageName);
	    	    			defappinfos.add(Application);
	    	    		}
	    	    }
     	       
	    	}
	    	
	    }
    }
   
    
    /**
     * 建立預設顯示在Grid中的應用程式清單(去除預設應用程式)
     * @param packageName packagename ex: com.camangi.home
     * @param Application ApplicationInfo
     */
    private void createShowApps(String packageName, ApplicationInfo Application)
    {
    	//Log.d(LOG_TAG, "createShowApps");
    	if (Application != null)
    	{
	    	if (packageName != null && packageName.length() > 0)
	    	{	         
		         boolean isfind = false;
		         for (int i = 0; i < defapps.length; i++)
		         {
		        	 if (packageName.equals(defapps[i]))
		        	 {
		        		 isfind = true;
		        		 break;
		        	 }
		         }
		         
		         if (!isfind)
		         {
		        	 //Log.d(LOG_TAG, "packageName: "+packageName);
		        	 mApplications.add(Application);
		         }
	        	 
	    	}
    	}
         
    }
   

    

    /**
     * Receives intents from other applications to change the wallpaper.
     */
    private class WallpaperIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //getWindow().setBackgroundDrawable(new ClippedDrawable(getWallpaper()));
        	homeLayout.setBackgroundDrawable(getWallpaper());
        }
    }

    /**
     * Receives notifications when applications are added/removed.
     */
    private class ApplicationsIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        	//Log.d(LOG_TAG, "Applications changes");
        	setDefaultWallpaper();
            loadApplications(false);
            bindApplications();
            //bindRecents();
            //bindFavorites(false);
            
        }
    }

    /**
     * GridView adapter to show the list of all installed applications.
     */
    private class ApplicationsAdapter extends ArrayAdapter<ApplicationInfo> {
        private Rect mOldBounds = new Rect();

        public ApplicationsAdapter(Context context, ArrayList<ApplicationInfo> apps) {
            super(context, 0, apps);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ApplicationInfo info = mApplications.get(position);

            if (convertView == null) {
                final LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.application1, parent, false);
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

            final TextView textView = (TextView) convertView.findViewById(R.id.label);                      
            //textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
            textView.setText(info.title);
            final ImageView image = (ImageView) convertView.findViewById(R.id.icon_app);
            image.setImageDrawable(icon);
            

            return convertView;
        }
    }

    

    /**
     * Hides the applications grid when the layout animation is over.
     */
    private class HideGrid_left implements Animation.AnimationListener {
        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            mBlockAnimation = false;
            //mGrid.setAdapter(new Applications_Adapter(Home.this, arylistInfo));
            //mGrid.startAnimation(mGridEntry_left);
            mGrid.setVisibility(View.INVISIBLE);
            mGrid.setAdapter(new Applications_Adapter(HomeSample.this, arylistInfo));
            //runLayoutHideAni();
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    /**
     * Shows the applications grid when the layout animation is over.
     */
    private class ShowGrid_left implements Animation.AnimationListener {
        public void onAnimationStart(Animation animation) {
        	 
        }

        public void onAnimationEnd(Animation animation) {
            mBlockAnimation = false;
            // ViewDebug.stopHierarchyTracing();
        }
 
        public void onAnimationRepeat(Animation animation) {
        }
    }
    /**
     * Hides the applications grid when the layout animation is over.
     */
    private class HideGrid_right implements Animation.AnimationListener {
        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            mBlockAnimation = false;
            //mGrid.setAdapter(new Applications_Adapter(Home.this, arylistInfo));
            //mGrid.startAnimation(mGridEntry_right);
            mGrid.setVisibility(View.INVISIBLE);
            mGrid.setAdapter(new Applications_Adapter(HomeSample.this, arylistInfo));
            //runLayoutHideAni();
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    /**
     * Shows the applications grid when the layout animation is over.
     */
    private class ShowGrid_right implements Animation.AnimationListener {
        public void onAnimationStart(Animation animation) {
        	 
        }

        public void onAnimationEnd(Animation animation) {
            mBlockAnimation = false;
            // ViewDebug.stopHierarchyTracing();
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }
    /**
     * Starts the selected activity/application in the grid view.
     */
    private class ApplicationLauncher implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);
            //Log.d(LOG_TAG, "start name: "+app.toString()+" id: "+position);
            startActivity(app.intent); 
        }
    }

    /**
     * When a drawable is attached to a View, the View gives the Drawable its dimensions
     * by calling Drawable.setBounds(). In this application, the View that draws the
     * wallpaper has the same size as the screen. However, the wallpaper might be larger
     * that the screen which means it will be automatically stretched. Because stretching
     * a bitmap while drawing it is very expensive, we use a ClippedDrawable instead.
     * This drawable simply draws another wallpaper but makes sure it is not stretched
     * by always giving it its intrinsic dimensions. If the wallpaper is larger than the
     * screen, it will simply get clipped but it won't impact performance.
     */
    private class ClippedDrawable extends Drawable {
        private final Drawable mWallpaper;

        public ClippedDrawable(Drawable wallpaper) {
            mWallpaper = wallpaper;
        }

        @Override
        public void setBounds(int left, int top, int right, int bottom) {
            super.setBounds(left, top, right, bottom);
            // Ensure the wallpaper is as large as it really is, to avoid stretching it
            // at drawing time
            mWallpaper.setBounds(left, top, left + mWallpaper.getIntrinsicWidth(),
                    top + mWallpaper.getIntrinsicHeight());
        }

        public void draw(Canvas canvas) {
            mWallpaper.draw(canvas);
        }

        public void setAlpha(int alpha) {
            mWallpaper.setAlpha(alpha);
        }

        public void setColorFilter(ColorFilter cf) {
            mWallpaper.setColorFilter(cf);
        }

        public int getOpacity() {
            return mWallpaper.getOpacity();
        }
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		try
		{
			myPlayer.release();
			bIsRelease = true;
			bIsPaused = false;
			isMusicOn = false;
			if (musictitleLayout.getVisibility() == View.VISIBLE)
			{					
				musictitleLayout.startAnimation(musictitle_out);
				musictitleLayout.setVisibility(View.GONE);
			}
			if (musicLayout.getVisibility()== View.VISIBLE)
			{
				musicLayout.startAnimation(music_out);	
				musicLayout.setVisibility(View.INVISIBLE);
			}					
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		super.onPause();
	}

	

	
}
