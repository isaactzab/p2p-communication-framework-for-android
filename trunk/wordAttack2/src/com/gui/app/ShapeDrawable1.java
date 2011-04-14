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

package com.gui.app;

import java.util.ArrayList;

import net.clc.bt.R;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

public class ShapeDrawable1 extends GraphicsActivity implements OnTouchListener {
		
	SoundManager mSoundManager;	
	ShapeView sv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        Log.e("WIDTH", "" + width);
        Log.e("HEIGHT", "" + height);
        
       sv = new ShapeView(this);
        sv.setOnTouchListener(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(sv);


         
    }

	public boolean onTouch(View v, MotionEvent event) {
		Log.d("string", "Touched");
		//variable int 
		event.getX();
    	ArrayList<ArrayList<Integer>> drawState = new ArrayList<ArrayList<Integer> >();
    	sv.setDrawState(drawState);
    	mSoundManager = new SoundManager();
		mSoundManager.initSounds(getBaseContext());
		mSoundManager.addSound(1, R.raw.sound);	
		return false;
	}
}

