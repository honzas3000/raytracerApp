package com.janlangr.nativeraytracer;

import java.io.IOException;

import parser.ObjParser;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.janlangr.nativeraytracerlib.RayTracerLibrary;

public class RayTracerView extends View implements Runnable {
	private static final long MAX_DELTA_NANO = 30000000;
	
	private long delta, startTime;
	private boolean running = true;
	private Thread t;
	
//	private native void initRayTracer();
//	private native int rayTraceScene(Bitmap bitmap);
	
	private Rect viewPort;
	
	private ObjParser objParser;
	
	Button leftBtn;
	
	// Debugging
	private FPSCounter fpsc;
	private boolean render = true;

	public RayTracerView(Context context, int width, int height) {
		super(context);
		
		Log.d("OBJ", "MEM: "+Runtime.getRuntime().maxMemory());
		
		startTime = System.nanoTime();
		
		fpsc = new FPSCounter();
		viewPort = new Rect(0, 0, width, height);
		
		objParser = new ObjParser((Activity) context);
		
		Log.d("OBJ", "free heap memory: "+Runtime.getRuntime().freeMemory());
		Log.d("OBJ", "total heap memory: "+Runtime.getRuntime().totalMemory());
		
		long init_time = System.nanoTime();
		
		try {
			objParser.parseOBJFileStaticObject("teapot.obj");
		} catch (IOException e) {
			Log.d("OBJ", "Failed to open obj file.");
		}
		
		Log.d("OBJ", "free heap memory: "+Runtime.getRuntime().freeMemory());
		Log.d("OBJ", "total heap memory: "+Runtime.getRuntime().totalMemory());
		
		RayTracerLibrary.addPointLight(3.0f, 3.0f, -6.0f, 1.0f, 1.0f, 1.0f);
		
		int cubeMat = RayTracerLibrary.addMaterial(0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 20.0f, 0.0f, 1.0f);
		int floorMat = RayTracerLibrary.addMaterial(0.6f, 0.6f, 0.6f, 0.5f, 1.0f, 20.0f, 0.0f, 1.0f);
		int trMat = RayTracerLibrary.addMaterial(0.6f, 0.6f, 1.0f, 0.1f, 0.1f, 20.0f, 0.7f, 1.0f);
		
		
		
		RayTracerLibrary.addVertex(-1.0f, -1.0f, -1.0f);
		RayTracerLibrary.addVertex(1.0f, -1.0f, -1.0f);
		RayTracerLibrary.addVertex(1.0f, 1.0f, -1.0f);
		RayTracerLibrary.addVertex(-1.0f, 1.0f, -1.0f);
		RayTracerLibrary.addVertex(-1.0f, -1.0f, 1.0f);
		RayTracerLibrary.addVertex(1.0f, -1.0f, 1.0f);
		RayTracerLibrary.addVertex(1.0f, 1.0f, 1.0f);
		RayTracerLibrary.addVertex(-1.0f, 1.0f, 1.0f);
		
		RayTracerLibrary.addTriangle(0, 1, 2, cubeMat);
		RayTracerLibrary.addTriangle(0, 2, 3, cubeMat);
		
		RayTracerLibrary.addTriangle(1, 5, 6, cubeMat);
		RayTracerLibrary.addTriangle(1, 6, 2, cubeMat);
		
		RayTracerLibrary.addTriangle(5, 4, 7, cubeMat);
		RayTracerLibrary.addTriangle(5, 7, 6, cubeMat);
		
		RayTracerLibrary.addTriangle(4, 0, 3, cubeMat);
		RayTracerLibrary.addTriangle(4, 3, 7, cubeMat);
		
		RayTracerLibrary.addTriangle(3, 2, 6, cubeMat);
		RayTracerLibrary.addTriangle(3, 6, 7, cubeMat);
		
		RayTracerLibrary.addTriangle(4, 5, 1, cubeMat);
		RayTracerLibrary.addTriangle(4, 1, 0, cubeMat);
		
		
		RayTracerLibrary.addVertex(-3.0f, -2.0f, -3.0f);
		RayTracerLibrary.addVertex(3.0f, -2.0f, -3.0f);
		RayTracerLibrary.addVertex(3.0f, -2.0f, 3.0f);
		RayTracerLibrary.addVertex(-3.0f, -2.0f, 3.0f);
		
		RayTracerLibrary.addTriangle(8, 9, 10, floorMat);
		RayTracerLibrary.addTriangle(8, 10, 11, floorMat);
		
		
		RayTracerLibrary.addVertex(-1.0f, -1.0f, -2.0f);
		RayTracerLibrary.addVertex(1.0f, -1.0f, -2.0f);
		RayTracerLibrary.addVertex(1.0f, 1.0f, -2.0f);
		RayTracerLibrary.addVertex(-1.0f, 1.0f, -2.0f);
		RayTracerLibrary.addVertex(-1.0f, -1.0f, -1.5f);
		RayTracerLibrary.addVertex(1.0f, -1.0f, -1.5f);
		RayTracerLibrary.addVertex(1.0f, 1.0f, -1.5f);
		RayTracerLibrary.addVertex(-1.0f, 1.0f, -1.5f);
		
		RayTracerLibrary.addTriangle(0+12, 1+12, 2+12, trMat);
		RayTracerLibrary.addTriangle(0+12, 2+12, 3+12, trMat);
		
		RayTracerLibrary.addTriangle(1+12, 5+12, 6+12, trMat);
		RayTracerLibrary.addTriangle(1+12, 6+12, 2+12, trMat);
		
		RayTracerLibrary.addTriangle(5+12, 4+12, 7+12, trMat);
		RayTracerLibrary.addTriangle(5+12, 7+12, 6+12, trMat);
		
		RayTracerLibrary.addTriangle(4+12, 0+12, 3+12, trMat);
		RayTracerLibrary.addTriangle(4+12, 3+12, 7+12, trMat);
		
		RayTracerLibrary.addTriangle(3+12, 2+12, 6+12, trMat);
		RayTracerLibrary.addTriangle(3+12, 6+12, 7+12, trMat);
		
		RayTracerLibrary.addTriangle(4+12, 5+12, 1+12, trMat);
		RayTracerLibrary.addTriangle(4+12, 1+12, 0+12, trMat);
		
		// Dynamic data.
		int simplex = RayTracerLibrary.createDynamicObject(4, 4);
		int v1 = RayTracerLibrary.addDynamicVertex(simplex, -1.0f, 0.0f, -1.0f);
		int v2 = RayTracerLibrary.addDynamicVertex(simplex, 1.0f, 0.0f, -1.0f);
		int v3 = RayTracerLibrary.addDynamicVertex(simplex, 0.0f, 0.0f, 2.0f);
		int v4 = RayTracerLibrary.addDynamicVertex(simplex, 0.0f, 2.0f, 0.0f);
		
		RayTracerLibrary.addDynamicTriangle(simplex, v2, v1, v3, cubeMat);
		RayTracerLibrary.addDynamicTriangle(simplex, v1, v2, v4, cubeMat);
		RayTracerLibrary.addDynamicTriangle(simplex, v2, v3, v4, cubeMat);
		RayTracerLibrary.addDynamicTriangle(simplex, v3, v1, v4, cubeMat);
		
		
		
		Log.d("LIBRAY", "Geometry data loading took: "+(((double)(System.nanoTime() - init_time))*(10e-10)) + " s");
		
		init_time = System.nanoTime();
		RayTracerLibrary.init(context);
		Log.d("LIBRAY", "RayTracerLib init (incl. BVH): "+(((double)(System.nanoTime() - init_time))*(10e-10)) + " s");
		
		Log.d("OBJ", "free heap memory: "+Runtime.getRuntime().freeMemory());
    	Log.d("OBJ", "total heap memory: "+Runtime.getRuntime().totalMemory());
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		delta = System.nanoTime() - startTime;
    	startTime = System.nanoTime();
    	
//        if(delta > MAX_DELTA_NANO) {
//            delta = MAX_DELTA_NANO;
//        }
        
//        Log.d("TIME", ""+(double)delta*(10e-10));
    	fpsc.getFPS(System.nanoTime());
        
    	if(true) {
    		int result = RayTracerLibrary.rayTraceScene((float)(delta*(10e-10)));
    		
    		if(result == 0) {
//    			Log.d("LIBRAY", "Posting invalidate");
    	        this.postInvalidate();
    		}
    		render = false;
    	}
    	
		canvas.drawBitmap(RayTracerLibrary.bitmap, null, viewPort, null);
		invalidate();
	}
	
	public void pause() {
//		Log.d("LIBRAY", "RayTracerView.pause() called.");
//		
//		running = false;
//		try {
//			t.join(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public void resume() {
//		Log.d("LIBRAY", "RayTracerView.resume() called.");
//		
//		running = true;
//		t = new Thread(this);
//		t.start();
	}

	@Override
	public void run() {
		startTime = System.nanoTime();
		
		while(running) {
			delta = System.nanoTime() - startTime;
	    	startTime = System.nanoTime();
	    	
//	        if(delta > MAX_DELTA_NANO) {
//	            delta = MAX_DELTA_NANO;
//	        }
	        
//	        Log.d("TIME", ""+(double)delta*(10e-10));
	    	fpsc.getFPS(System.nanoTime());
	        
	    	if(true) {
	    		int result = RayTracerLibrary.rayTraceScene((float)(delta*(10e-10)));
	    		
	    		if(result == 0) {
//	    			Log.d("LIBRAY", "Posting invalidate");
	    	        this.postInvalidate();
	    		}
	    		render = false;
	    	}
		}
		
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		
	}
}
