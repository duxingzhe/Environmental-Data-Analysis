package com.luxuan.encoder.input.gl;

import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.luxuan.encoder.util.gl.GifStreamObject;
import com.luxuan.encoder.util.gl.GlUtil;
import com.luxuan.encoder.util.gl.ImageStreamObject;
import com.luxuan.encoder.util.gl.TextStreamObject;

public class TextureLoader {

    private TextStreamObject textStreamObject;
    private GifStreamObject gifStreamObject;
    private ImageStreamObject imageStreamObject;

    public TextureLoader(){

    }

    public void setTextStreamObject(TextStreamObject textStreamObject){
        this.textStreamObject=textStreamObject;
        this.gifStreamObject=null;
        this.imageStreamObject=null;
    }

    public void setGifStreamObject(GifStreamObject gifStreamObject){
        this.textStreamObject=null;
        this.gifStreamObject=gifStreamObject;
        this.imageStreamObject=null;
    }

    public void setImageStreamObject(ImageStreamObject imageStreamObject){
        this.textStreamObject=null;
        this.gifStreamObject=null;
        this.imageStreamObject=imageStreamObject;
    }

    public int[] load(){
        int[] textureId=new int[]{-1};
        if(textStreamObject!=null){
            textureId=new int[textStreamObject.getNumFrames()];
            GlUtil.createTextures(textStreamObject.getNumFrames(), textureId, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textStreamObject.getImageBitmap(), 0);
            textStreamObject.recycle();
        }else if(imageStreamObject!=null){
            textureId=new int[imageStreamObject.getNumFrames()];
            GlUtil.createTextures(imageStreamObject.getNumFrames(), textureId, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, imageStreamObject.getImageBitmap(), 0);
            imageStreamObject.recycle();
        }else if(gifStreamObject!=null){
            textureId=new int[gifStreamObject.getNumFrames()];
            GlUtil.createTextures(gifStreamObject.getNumFrames(), textureId, 0);
            for(int i=0;i<gifStreamObject.getNumFrames();i++) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[i]);
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, gifStreamObject.getGifBitmaps()[i], 0);
            }
            gifStreamObject.recycle();
        }
        return textureId;
    }
}
