package com.littlecheesecake.gesturecam;

import android.app.Activity;
import android.graphics.PointF;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
	private CamLayer mPreview;
	
	//multi touch event
	private float oldDist = 1f;
	private PointF mid = new PointF(); 
	private int mode = 0;
	private boolean down = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CamLayer(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
			
        mPreview.setOnTouchListener(new OnSwipeTouchListener(){
			 public void onSwipeTop() {
				 if(down){
				 Thread t = new Thread() {
	                    public void run() {
	                    	mPreview.upsidedownit();
	                    }
	                };
	                t.start();

			    }
			 }
			    public void onSwipeRight() {
			    	if(down){
			    	Thread t = new Thread() {
	                    public void run() {
	                    	mPreview.flipit();
	                    }
	                };
	                t.start();
			    }
			    }
			    public void onSwipeLeft() {
			    	if(down){
			    	Thread t = new Thread() {
	                    public void run() {
	                    	mPreview.flipit();
	                    }
	                };
	                t.start();
			    }
			    }
			    public void onSwipeBottom() {
					 if(down){
						 Thread t = new Thread() {
			                    public void run() {
			                    	mPreview.upsidedownit();
			                    }
			                };
			                t.start();

					    }
					 }
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		switch(event.getAction() & MotionEvent.ACTION_MASK){
			case MotionEvent.ACTION_DOWN:
				down = true;
				break;
				
			case MotionEvent.ACTION_POINTER_DOWN:
				down = false;
				oldDist = spacing(event);
				if(oldDist > 10f){
					midPoint(mid, event);
					mode = 1;
				}
				break;
				
		    case MotionEvent.ACTION_UP:
		    	 down = true;
		    	 mode = 0;
			     break;
			     
			case MotionEvent.ACTION_POINTER_UP:
				 down =false;
		         mode = 0;
		         break;
		
			case MotionEvent.ACTION_MOVE:
				if(mode == 1){
					float newDist = spacing(event);
					if(newDist > 10f){
						float m = newDist/oldDist;
						mPreview.zoomin(m);
					}
				}
				break;
		}
		return true;
		
	}
	 
	 /** Determine the space between the first two fingers */
	 private float spacing(MotionEvent event) {
	    float x = event.getX(0) - event.getX(1);
	    float y = event.getY(0) - event.getY(1);
	    return FloatMath.sqrt(x * x + y * y);
	 }

	 /** Calculate the mid point of the first two fingers */
	 private void midPoint(PointF point, MotionEvent event) {
	    float x = event.getX(0) + event.getX(1);
	    float y = event.getY(0) + event.getY(1);
	    point.set(x / 2, y / 2);
	 }
	
	
	 @Override
		public void onDestroy(){
			super.onDestroy();
			mPreview.onStop();
		}
		
		@Override
		public void onPause(){
			super.onPause();
			mPreview.onPause();
		}
		
		@Override
		public void onResume(){
			super.onResume();
		
		}

}

