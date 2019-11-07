package com.luxuan.rtmppusher.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.luxuan.rtmppusher.util.DisplayUtil;

import java.io.IOException;

public class LxCamera {

    private Camera camera;

    private SurfaceTexture surfaceTexture;

    private int width;
    private int height;

    public LxCamera(Context context){
        this.width= DisplayUtil.getScreenWidth(context);
        this.height=DisplayUtil.getScreenHeight(context);
    }

    public void initCamera(SurfaceTexture surfaceTexture, int cameraId){
        this.surfaceTexture=surfaceTexture;
        setCameraParameters(cameraId);
    }

    private void setCameraParameters(int cameraId){
        try{
            camera=Camera.open(cameraId);
            camera.setPreviewTexture(surfaceTexture);
            Camera.Parameters parameters=camera.getParameters();

            parameters.setFlashMode("off");
            parameters.setPreviewFormat(ImageFormat.NV21);

            Camera.Size size=getFitSize(parameters.getSupportedPictureSizes());
            parameters.setPictureSize(size.width, size.height);

            size=getFitSize(parameters.getSupportedPreviewSizes());

            camera.setParameters(parameters);
            camera.startPreview();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
