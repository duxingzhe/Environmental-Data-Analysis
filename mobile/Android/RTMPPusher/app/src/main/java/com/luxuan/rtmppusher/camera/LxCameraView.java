package com.luxuan.rtmppusher.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;

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
}
