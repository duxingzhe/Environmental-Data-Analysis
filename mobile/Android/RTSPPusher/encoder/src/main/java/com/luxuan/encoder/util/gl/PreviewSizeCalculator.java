package com.luxuan.encoder.util.gl;

import android.opengl.GLES20;

public class PreviewSizeCalculator {

    public static void calculateViewPort(boolean keepAspectRatio, int previewWidth, int previewHeight, int streamWidth,
                                         int streamHeight){
        if(keepAspectRatio){
            if(previewWidth>previewHeight){
                int realWidth=previewHeight*streamWidth/streamHeight;
                GLES20.glViewport((previewWidth-realWidth/2), 0, realWidth, previewHeight);
            }else{
                int realHeight=previewHeight*streamWidth/streamWidth;
                GLES20.glViewport(0, (previewHeight-realHeight)/2, previewWidth, realHeight);
            }
        }else{
            GLES20.glViewport(0, 0, previewWidth, previewHeight);
        }
    }
}
