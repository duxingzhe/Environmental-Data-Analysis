package com.luxuan.encoder.input.gl.render;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.Surface;

import com.luxuan.encoder.input.gl.render.filters.BaseFilterRender;
import com.luxuan.encoder.input.gl.render.filters.NoFilterRender;

import java.util.ArrayList;
import java.util.List;

public class ManagerRender {

    public static int numFilters=1;

    private CameraRender cameraRender;
    private List<BaseFilterRender> baseFilterRenders=new ArrayList<>();
    private ScreenRender screenRender;

    private int width;
    private int height;
    private int previewWidth;
    private int previewHeight;
    private Context context;

    public ManagerRender(){
        cameraRender=new CameraRender();
        for(int i=0;i<numFilters;i++){
            baseFilterRenders.add(new NoFilterRender());
        }
        screenRender=new ScreenRender();
    }

    public void initGl(Context context, int encoderWidth, int encoderHeight, int previewWidth, int previewHeight){
        this.context=context;
        this.width=encoderWidth;
        this.height=encoderHeight;
        this.previewWidth=previewWidth;
        this.previewHeight=previewHeight;
        cameraRender.initGl(width, height, context, previewWidth, previewHeight);
        for(int i=0;i<numFilters;i++){
            int textureId=i==0?cameraRender.getTextureId():baseFilterRenders.get(i-1).getTextureId();
            baseFilterRenders.get(i).setPreviousTextureId(textureId);
            baseFilterRenders.get(i).initGl(width, height, context, previewWidth, previewHeight);
            baseFilterRenders.get(i).initFBOLink();
        }
        screenRender.setStreamSize(encoderWidth, encoderHeight);
        screenRender.setTexId(baseFilterRender.get(numFilters-1).getTextureId());
        screenRender.initGl(context);
    }

    public void drawOffScreen(){
        cameraRender.draw();
        for(BaseFilterRender baseFilterRender : baseFilterRenders){
            baseFilterRender.draw();
        }
    }

    public void drawScreen(int width, int height, boolean keepAspectRatio){
        screenRender.draw(width, height, keepAspectRatio);
    }

    public void release(){
        cameraRender.release();
        for(BaseFilterRender baseFilterRender : baseFilterRenders){
            baseFilterRender.release();
        }
        screenRender.release();
    }

    public void enableAA(boolean AAEnabled){
        screenRender.setAAEnabled(AAEnabled);
    }

    public boolean isAAEnabled(){
        return screenRender.isAAEnabled;
    }

    public void updateFrame(){
        cameraRender.updateTextureImage();
    }

    public SurfaceTexture getSurfaceTexture(){
        return cameraRender.getSurfaceTexture();
    }

    public Surface getSurface(){
        return cameraRender.getSurface();
    }

    public void setFilter(int position, BaseFilterRender baseFilterRender){
        final int id=baseFilterRenders.get(position).getPreviousTextureId();
        final RenderHandler renderHandler=baseFilterRenders.get(position).getRenderHandler();
        baseFilterRenders.get(position).release();
        baseFilterRenders.set(position, baseFilterRender);
        baseFilterRenders.get(position).initGl(width, height, context, previewWidth, previewHeight);
        baseFilterRenders.get(position).setPreviousTextureId(id);
        baseFilterRenders.get(position).setRenderHandler(renderHandler);
    }

    public void setCameraRotation(int rotation){
        cameraRender.setRotation(rotation);
    }

    public void setCameraFlip(boolean isFlipHorizontal, boolean isFlipVertical){
        cameraRender.setFlip(isFlipHorizontal, isFlipVertical);
    }
}
