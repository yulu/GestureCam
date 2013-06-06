package com.littlecheesecake.gesturecam;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CamLayer extends SurfaceView implements SurfaceHolder.Callback{

	//camera instance
	private Camera mCamera;
	//Surface holder
	private SurfaceHolder mHolder;
	//camera Id
	private int mCameraId;
	//camera para
	private Camera.Parameters params;
	
	@SuppressLint("NewApi")
	private final Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
	
   
	//front/back camera paramter
	private final int FRONT = 1;
	private final int BACK = 0;
	private int which = BACK;
	
	//rotation parameter
	private int up = 90;
	private int down = 270;
	private int rot = up;

	//zoom
	int currentZoomLevel = 1, maxZoomLevel = 0;
	
	CamLayer(Activity context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
	/**
	 * Handles camera opening
	 */
	@SuppressLint("NewApi")
	private void openCamera(int which, int rot){
		if(mCamera != null){
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		
		if(mCameraId >= 0){
			Camera.getCameraInfo(mCameraId, mCameraInfo);
			if(which == FRONT)
				mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
			else 
				mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			
			params = mCamera.getParameters();
			
			/**
			 * set focus mode
			 */
			List<String> FocusModes = params.getSupportedFocusModes();
            if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
            {
            	params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } 
            
    			mCamera.setParameters(params);
			
		}
		
		if(mCamera == null){
			return;
		}
			
		mCamera.setDisplayOrientation(rot);
		mCamera.startPreview();
		
		
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.

	    openCamera(BACK, up);
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
	
	public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }
	
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        openCamera(which, rot);
		try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
	
	/**
	 * Must called from Activity.onPause()
	 */
	public void onPause(){
		if(mCamera != null){
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
	
	public void onStop(){
		if(mCamera != null){
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
	
	/**
	 * Selects either front-facing or back-facing camera
	 */
	@SuppressLint("NewApi")
	public void flipit() {
		synchronized(this) {
	    //myCamera is the Camera object
	    if (Camera.getNumberOfCameras()>=2) {
	        //"which" is just an integer flag
	        
	        if(which == FRONT){
	        	openCamera(BACK, rot);
	        	which = BACK;
	        }else{
	        	openCamera(FRONT, rot);
	        	which = FRONT;
	        }

	    }
	    try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	}
	
	/**
	 * flipt the camera upside down
	 */
	public void upsidedownit(){
		synchronized(this){
			if(rot == up){
				rot = down;
				openCamera(which, rot);
			}else{
				rot = up;
				openCamera(which, rot);
			}
		}
	    try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Zoom in and out
	 */
	public void zoomin(float m){
		 if(params.isZoomSupported()){    
			    maxZoomLevel = params.getMaxZoom();
			    
			    float zoom = (float)currentZoomLevel;
			    if( m > 1)
			    	zoom +=  m;   
			    else
			    	zoom = zoom *m;
			    
			    currentZoomLevel = (int)zoom;
			    		    
			    if(currentZoomLevel > maxZoomLevel)
			    	currentZoomLevel = maxZoomLevel;
			    if(currentZoomLevel < 1)
			    	currentZoomLevel = 1;
			    
			    params.setZoom(currentZoomLevel);
			    mCamera.setParameters(params);
			    
		 }
	}
   
}