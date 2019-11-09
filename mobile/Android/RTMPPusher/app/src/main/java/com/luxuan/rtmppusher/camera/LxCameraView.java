package com.luxuan.rtmppusher.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.WindowManager;

import com.luxuan.rtmppusher.egl.LXEGLSurfaceView;

public class LxCameraView extends LXEGLSurfaceView {

    private LxCameraRender lxCameraRender;
    private LxCamera lxCamera;

    private int cameraId= Camera.CameraInfo.CAMERA_FACING_BACK;

    private int textureId=-1;

    public LxCameraView(Context context){
        this(context, null);
    }

    public LxCameraView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public LxCameraView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        lxCameraRender=new LxCameraRender(context);
        lxCamera=new LxCamera(context);
        setRender(lxCameraRender);
        previewAngle(context);
        lxCameraRender.setOnSurfaceCreateListener(new LxCameraRender.OnSurfaceCreateListener(){

            @Override
            public void onSurfaceCreate(SurfaceTexture surfaceTexture, int tid){
                lxCamera.initCamera(surfaceTexture, cameraId);
                textureId=tid;
            }
        });
    }

    public void onDestroy(){
        if(lxCamera!=null){
            lxCamera.stopPreview();
        }
    }

    public void previewAngle(Context context){
        int angle=((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        lxCameraRender.resetMatrix();
        switch(angle){
            case Surface.ROTATION_0:
                if(cameraId==Camera.CameraInfo.CAMERA_FACING_BACK){
                    lxCameraRender.setAngle(90,0,0,1);
                    lxCameraRender.setAngle(180, 1, 0,0);
                }else{
                    lxCameraRender.setAngle(90f, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_90:
                if(cameraId==Camera.CameraInfo.CAMERA_FACING_BACK){
                    lxCameraRender.setAngle(180,0,0,1);
                    lxCameraRender.setAngle(180, 0, 1,0);
                }else{
                    lxCameraRender.setAngle(90f, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_180:
                if(cameraId==Camera.CameraInfo.CAMERA_FACING_BACK){
                    lxCameraRender.setAngle(90,0,0,1);
                    lxCameraRender.setAngle(180, 0, 1,0);
                }else{
                    lxCameraRender.setAngle(-90f, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_270:
                if(cameraId==Camera.CameraInfo.CAMERA_FACING_BACK){
                    lxCameraRender.setAngle(180, 1, 0,0);
                }else{
                    lxCameraRender.setAngle(0f, 0f, 0f, 1f);
                }
                break;
        }
    }

    public int getTextureId(){
        return textureId;
    }
}
