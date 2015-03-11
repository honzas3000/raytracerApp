package com.janlangr.nativeraytracer;

import android.util.Log;

public class FPSCounter {
	private int frameCount = 0, returnFPS = 0;
	private long lastRestartNano = 0;
	
	public FPSCounter() {}
	
	public void getFPS(long currentNano) {
		frameCount++;
		if(currentNano > lastRestartNano + 1000000000) {
			lastRestartNano = currentNano;
			returnFPS = frameCount;
			frameCount = 0;
			
			Log.d("FPS", ""+returnFPS);
		}
	}
}
