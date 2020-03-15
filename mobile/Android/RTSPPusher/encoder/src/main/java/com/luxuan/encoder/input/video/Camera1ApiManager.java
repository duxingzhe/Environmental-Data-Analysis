package com.luxuan.encoder.input.video;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.TextureView;

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
        cameraSelect=facing==Camera.CameraInfo.CAMERA_FACING_BACK?slectCameraBack():selectCameraFront();
        start();
    }

    public void start(int width, int height, int fps){
        CameraHelper.Facing facing=
                cameraFacing==Camera.CameraInfo.CAMERA_FACING_BACK?CameraHelper.Facing.BACK:
                        CameraHelper.Facing.FRONT;
        start(facing, width, height, fps);
    }

    private void start(){

    }
}
