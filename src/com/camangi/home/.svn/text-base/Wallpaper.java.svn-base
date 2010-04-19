/*
 * Copyright (C) 2006 The Android Open Source Project
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

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Gallery.LayoutParams;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import com.camangi.home.R;
import com.camangi.home.R.drawable;
import com.camangi.home.R.id;
import com.camangi.home.R.layout;

/**
 * Wallpaper picker for the Home application. User can choose from
 * a gallery of stock photos.
 */
public class Wallpaper extends Activity implements
        AdapterView.OnItemSelectedListener, OnClickListener {
    
    private static final String LOG_TAG = "Wallpaper";

    private static final Integer[] THUMB_IDS = {
    	R.drawable.wallpaper_black_small,
    	R.drawable.wallpaper_lake_small,
        R.drawable.wallpaper_sunset_small,
        R.drawable.wallpaper_beach_small,
        R.drawable.wallpaper_snow_leopard_small,
        R.drawable.wallpaper_path_small,
        R.drawable.wallpaper_sunrise_small,
        R.drawable.wallpaper_mountain_small,
        R.drawable.wallpaper_road_small,
        R.drawable.wallpaper_jellyfish_small,
        R.drawable.wallpaper_zanzibar_small,
        R.drawable.wallpaper_blue_small,
        R.drawable.wallpaper_grey_small,
        R.drawable.wallpaper_green_small,
        R.drawable.wallpaper_pink_small,
        
        
    };

    private static final Integer[] IMAGE_IDS = {
    	R.drawable.wallpaper_black,
    	R.drawable.wallpaper_lake,
        R.drawable.wallpaper_sunset,
        R.drawable.wallpaper_beach,
        R.drawable.wallpaper_snow_leopard,
        R.drawable.wallpaper_path,
        R.drawable.wallpaper_sunrise,
        R.drawable.wallpaper_mountain,
        R.drawable.wallpaper_road,
        R.drawable.wallpaper_jellyfish,
        R.drawable.wallpaper_zanzibar,
        R.drawable.wallpaper_blue,
        R.drawable.wallpaper_grey,
        R.drawable.wallpaper_green,
        R.drawable.wallpaper_pink,
        
    };

    private Gallery mGallery;
    private ImageView mImageView;
    private ImageView mImageBg;
    private boolean mIsWallpaperSet;

    private BitmapFactory.Options mOptions;
    private Bitmap mBitmap;

    private ArrayList<Integer> mThumbs;
    private ArrayList<Integer> mImages;
        
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        findWallpapers();
        setContentView(R.layout.wallpaper_chooser);

        /*
        mGallery = (Gallery) findViewById(R.id.gallery);
        mGallery.setAdapter(new ImageAdapter(this));
        mGallery.setOnItemSelectedListener(this);
        mGallery.setOnItemClickListener(this);
        */

        mOptions = new BitmapFactory.Options();
        mOptions.inDither = false;
        mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

        mGallery = (Gallery) findViewById(R.id.gallery);
        mGallery.setAdapter(new ImageAdapter(this));
        mGallery.setOnItemSelectedListener(this);
        mGallery.setCallbackDuringFling(false);

        Button b = (Button) findViewById(R.id.set);
        b.setOnClickListener(this);

        //mImageView = (ImageView) findViewById(R.id.wallpaper);
        mImageBg = (ImageView)	findViewById(R.id.wallpaperbg);
    }
    private void findWallpapers() {
        mThumbs = new ArrayList<Integer>(THUMB_IDS.length + 4);
        Collections.addAll(mThumbs, THUMB_IDS);

        mImages = new ArrayList<Integer>(IMAGE_IDS.length + 4);
        Collections.addAll(mImages, IMAGE_IDS);

        final Resources resources = getResources();
        final String[] extras = resources.getStringArray(R.array.extra_wallpapers);
        final String packageName = getApplication().getPackageName();

        for (String extra : extras) {
            int res = resources.getIdentifier(extra, "drawable", packageName);
            if (res != 0) {
                final int thumbRes = resources.getIdentifier(extra + "_small",
                        "drawable", packageName);

                if (thumbRes != 0) {
                    mThumbs.add(thumbRes);
                    mImages.add(res);
                }
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mIsWallpaperSet = false;
    }

    /*
    public void onItemSelected(AdapterView parent, View v, int position, long id) {
        getWindow().setBackgroundDrawableResource(IMAGE_IDS[position]);
    }
    */
    public void onItemSelected(AdapterView parent, View v, int position, long id) {
    	/*
        final ImageView view = mImageView;
        Bitmap b = BitmapFactory.decodeResource(getResources(), mImages.get(position), mOptions);
        view.setImageBitmap(b);
        */
        final ImageView viewbg = mImageBg;
        Bitmap b = BitmapFactory.decodeResource(getResources(), mImages.get(position), mOptions);
        viewbg.setImageBitmap(b);
        // Help the GC
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        mBitmap = b;

        //final Drawable drawable = view.getDrawable();
        final Drawable drawable = viewbg.getDrawable();
        drawable.setFilterBitmap(true);
        drawable.setDither(true);
    }
    
    /*
    public void onItemClick(AdapterView parent, View v, int position, long id) {
        selectWallpaper(position);
    }
	*/
    /*
     * When using touch if you tap an image it triggers both the onItemClick and
     * the onTouchEvent causing the wallpaper to be set twice. Synchronize this
     * method and ensure we only set the wallpaper once.
     */
    private void selectWallpaper(int position) {
    	if (mIsWallpaperSet) {
            return;
        }

        mIsWallpaperSet = true;
        try {
            InputStream stream = getResources().openRawResource(mImages.get(position));
            setWallpaper(stream);
            setResult(RESULT_OK);
            finish();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to set wallpaper: " + e);
        }
    }

    public void onNothingSelected(AdapterView parent) {
    }
    
   

    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mLayoutInflater;
        public ImageAdapter(Context c) {
            mContext = c;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        public int getCount() {
            return THUMB_IDS.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
        	/*
            ImageView i = new ImageView(mContext);

            i.setImageResource(THUMB_IDS[position]);
            i.setAdjustViewBounds(true);
            i.setLayoutParams(new Gallery.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            i.setBackgroundResource(android.R.drawable.picture_frame);
            return i;
            */
        	 ImageView image;

             if (convertView == null) {
                 image = (ImageView) mLayoutInflater.inflate(R.layout.wallpaper_item, parent, false);
             } else {
                 image = (ImageView) convertView;
             }

             image.setImageResource(THUMB_IDS[position]);
             image.getDrawable().setDither(true);
             return image;
        }

    }

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		selectWallpaper(mGallery.getSelectedItemPosition());
	}

}

    
