package com.luxuan.encoder.input.gl.render.filters.object;

import android.opengl.GLES20;

import com.luxuan.encoder.util.gl.GifStreamObject;

import java.io.IOException;
import java.io.InputStream;

public class GifObjectFilterRender extends BaseObjectFilterRender{

    public GifObjectFilterRender(){
        super();
        streamObject=new GifStreamObject();
    }

    @Override
    protected void drawFilter(){
        super.drawFilter();
        int position=((GifStreamObject)streamObject).updateFrame(streamObjectTextureId.length);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, streamObjectTextureId[position]);

        GLES20.glUniform1f(uAlphaHandle, streamObjectTextureId[position]==-1?0f:alpha);
    }

    public void setGif(InputStream inputStream ) throws IOException {
        ((GifStreamObject) streamObject).load(inputStream);
        textureLoader.setGifStreamObject((GifStreamObject)streamObject);
        shouldLoad=true;
    }
}
