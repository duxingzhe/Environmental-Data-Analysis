package com.luxuan.encoder.input.video;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;

import java.io.IOException;
import java.util.List;

public class Camera1ApiManager {

    private String TAG="Camera1ApiManager";
    private Camera camera=null;
    private SurfaceView surfaceView;
    private TextureView textureView;
    private SurfaceTexture surfaceTexture;
    private GetCameraData getCameraData;
    private boolean running=false;
    private boolean lanternEnable=false;
    private int cameraSelect;
    private boolean isFrontCamera=false;
    private boolean isPortrait=false;
    private int cameraFacing=Camera.CameraInfo.CAMERA_FACING_BACK;
    private Context context;

    private int width=640;
    private int height=480;
    private int fps=30;
    private int rotation=0;
    private int imageFormat= ImageFormat.NV21;
    private byte[] yuvBuffer;
    private List<Camera.Size> previewSizeBack;
    private List<Camera.Size> previewSizeFront;
    private float distance;

    public interface FaceDetectorCallback{
        void onGetFaces(Camera.Face[] faces);
    }

    public Camera1ApiManager(SurfaceView surfaceView, GetCameraData getCameraData){
        this.surfaceView=surfaceView;
        this.getCameraData=getCameraData;
        this.context=context;
        init();
    }

    public Camera1ApiManager(TextureView textureView, GetCameraData getCameraData){
        this.textureView=textureView;
        this.getCameraData=getCameraData;
        this.context=textureView.getContext();
        init();
    }

    public Camera1ApiManager(SurfaceView surfaceView, Context context){
        this.surfaceView=surfaceView;
        this.context=context;
        init();
    }

    private void init(){
        cameraSelect=selectCameraBack();
        previewSizeBack=getPreviewSize();
        cameraSelect=selecCameraFront();
        previewSizeFront=getPreviewSize();
    }

    public void setRotation(int rotation){
        this.rotation=rotation;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture){
        this.surfaceTexture=surfaceTexture;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public void start(CameraHelper.Facing cameraFacing, int width, int height, int fps){
        int facing= cameraFacing==CameraHelper.Facing.BACK?Camera.CameraInfo.CAMERA_FACING_BACK:
                Camera.CameraInfo.CAMERA_FACING_FRONT;
        this.width=width;
        this.height=height;
        this.fps=fps;
        this.cameraFacing=facing;
        cameraSelect=facing==Camera.CameraInfo.CAMERA_FACING_BACK?selectCameraBack():selectCameraFront();
        start();
    }

    public void start(int width, int height, int fps){
        CameraHelper.Facing facing=
                cameraFacing==Camera.CameraInfo.CAMERA_FACING_BACK?CameraHelper.Facing.BACK:
                        CameraHelper.Facing.FRONT;
        start(facing, width, height, fps);
    }

    private void start(){
        if(!checkCanOpen()){
            throw new CameraOpenException("This camera resolution can't be opened");
        }
        yuvBuffer=new byte[width*height*3/2];
        try{
            camera=Camera.open(cameraSelect);
            Camera.CameraInfo info=new Camera.CameraInfo();
            Camera.getCameraInfo(cameraSelect, info);
            isFrontCamera=info.facing==Camera.CameraInfo.CAMERA_FACING_FRONT;
            isPortrait=context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
            Camera.Parameters parameters=camera.getParameters();
            parameters.setPreviewSize(width, height);
            parameters.setPreviewFormat(imageFormat);
            int[] range=adaptFpsRange(fps, parameters.getSupportedPreviewFpsRange());
            parameters.setPreviewFpsRange(range[0], range[1]);

            List<String> supportedFocusModes=parameters.getSupportedFocusModes();
            if(supportedFocusModes!=null&&!supportedFocusModes.isEmpty()){
                if(supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }else if(supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }else{
                    parameters.setFocusMode(supportedFocusModes.get(0));
                }
            }
            camera.setParameters(parameters);
            camera.setDisplayOrientation(rotation);
            if(surfaceView!=null){
                camera.setPreviewDisplay(surfaceView.getHolder());
                camera.addCallbackBuffer(yuvBuffer);
                camera.setPreviewCallbackWithBuffer(this);
            }else if(textureView!=null){
                camera.setPreviewTexture(textureView.getSurfaceTexture());
                camera.addCallbackBuffer(yuvBuffer);
                camera.setPreviewCallbackWithBuffer(this);
            }else{
                camera.setPreviewTexture(surfaceTexture);
            }
            camera.startPreview();
            running=true;
            Log.i(TAG, width+"X"+height);
        }catch(IOException e){
            Log.e(TAG, "Error", e);
        }
    }

    private int selectCameraBack(){
        return selectCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    private int selectCameraFront(){
        return selectCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    private int selectCamera(int facing){
        int number=Camera.getNumberOfCameras();
        for(int i=0;i<number;i++){
            Camera.CameraInfo info=new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if(info.facing==facing){
                return i;
            }
        }
        return 0;
    }
}
