package com.janlangr.nativeraytracer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.janlangr.nativeraytracerlib.RayTracerLibrary;


public class MainActivity extends Activity {
	protected WakeLock wakeLock;
	
	RayTracerView rtview;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        Display display = getWindowManager().getDefaultDisplay();
        
        rtview = new RayTracerView(this, display.getWidth(), display.getHeight());
        
        setContentView(rtview);
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Game");
    }
	
	@Override
	public void onResume() {
		super.onResume();
		wakeLock.acquire();
		
		rtview.resume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		wakeLock.release();
		Log.d("ACTIVITY", "NativeRayTracer pausing.");
		
		rtview.pause();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		RayTracerLibrary.finish();
	}
}
