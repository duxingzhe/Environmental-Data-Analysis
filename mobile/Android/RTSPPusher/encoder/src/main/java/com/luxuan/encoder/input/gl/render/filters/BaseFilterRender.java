package com.luxuan.encoder.input.gl.render.filters;

import android.content.Context;
import android.opengl.GLES20;

import com.luxuan.encoder.input.gl.render.BaseRenderOffScreen;
import com.luxuan.encoder.input.gl.render.RenderHandler;
import com.luxuan.encoder.util.gl.GlUtil;


public abstract class BaseFilterRender extends BaseRenderOffScreen {

   private int width;
   private int height;
   private int previewWidth;
   private int previewHeight;

   protected int previousTextureId;
   private RenderHandler renderHandler=new RenderHandler();

   @Override
   public void initGl(int width, int height, Context context, int previewWidth, int previewHeight){
       this.width=width;
       this.height=height;
       this.previewWidth=previewWidth;
       this.previewHeight=previewHeight;
       GlUtil.checkGlError("initGl start");
       initGlFilter(context);
       GlUtil.checkGlError("initGl end");
   }

   protected abstract void initGlFilter(Context context);

   @Override
   public void draw(){
       GlUtil.checkGlError("drawFilter start");
       GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, renderHandler.getFboId()[0]);
       GLES20.glViewport(0, 0, width, height);
       drawFilter();
       GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
       GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
       GlUtil.checkGlError("drawFilter end");
   }

   protected abstract void drawFilter();

   public void setPreviousTextureId(int textureId){
       this.previousTextureId=textureId;
   }

   @Override
    public int getTextureId(){
       return renderHandler.getTexId()[0];
   }

   protected int getWidth(){
       return width;
   }

   protected int getHeight(){
       return height;
   }

   public int getPreviewWidth(){
       return previewWidth;
   }

   public int getPreviewHeight(){
       return previewHeight;
   }

   public int getPreviousTextureId(){
       return previousTextureId;
   }

   public RenderHandler getRenderHandler(){
       return renderHandler;
   }

   public void setRenderHandler(RenderHandler renderHandler){
       this.renderHandler=renderHandler;
   }
}
