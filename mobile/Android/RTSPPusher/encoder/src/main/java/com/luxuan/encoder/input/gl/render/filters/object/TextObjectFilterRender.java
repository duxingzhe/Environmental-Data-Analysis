package com.luxuan.encoder.input.gl.render.filters.object;

import android.graphics.Typeface;
import android.opengl.GLES20;

import com.luxuan.encoder.util.gl.TextStreamObject;

public class TextObjectFilterRender extends BaseObjectFilterRender {

    public TextObjectFilterRender(){
        super();
        streamObject=new TextStreamObject();
    }

    @Override
    protected void drawFilter(){
        super.drawFilter();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, streamObjectTextureId[0]);
        GLES20.glUniform1f(uAlphaHandle, streamObjectTextureId[0]==-1?0f:alpha);
    }

    public void setText(String text, float textSize, int textColor){
        setText(text, textSize, textColor, null);
    }

    public void setText(String text, float textSize, int textColor, Typeface typeface){
        ((TextStreamObject)streamObject).load(text, textSize, textColor, typeface);
        textureLoader.setTextStreamObject((TextStreamObject)streamObject);
        shouldLoad=true;
    }
}
