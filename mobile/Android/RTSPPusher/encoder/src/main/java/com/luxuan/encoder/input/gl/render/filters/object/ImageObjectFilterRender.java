package com.luxuan.encoder.input.gl.render.filters.object;

import android.opengl.GLES20;

import com.luxuan.encoder.util.gl.ImageStreamObject;

public class ImageObjectFilterRender extends BaseObjectFilterRender {

    public ImageObjectFilterRender(){
        super();
        streamObject=new ImageStreamObject();
    }

    @Override
    protected void drawFilter(){
        super.drawFilter();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, streamObjectTextureId[0]);

        GLES20.glUniform1f(uAlphaHandle, streamObjectTextureId[0]==-1?0f:alpha);
    }
}
