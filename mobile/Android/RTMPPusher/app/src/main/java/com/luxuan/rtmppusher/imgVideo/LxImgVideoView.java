package com.luxuan.rtmppusher.imgVideo;

import android.content.Context;
import android.util.AttributeSet;

import com.luxuan.rtmppusher.egl.LXEGLSurfaceView;

public class LxImgVideoView extends LXEGLSurfaceView {

    private LxImgVideoRender lxImgVideoRender;
    private int fboTextureId;

    public LxImgVideoView(Context context) {
        this(context, null);
    }

    public LxImgVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LxImgVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        lxImgVideoRender=new LxImgVideoRender(context);
        setRender(lxImgVideoRender);
        setRenderMode(LXEGLSurfaceView.RENDERMODE_WHEN_DIRTY);
        lxImgVideoRender.setOnRenderCreateListener(new LxImgVideoRender.OnRenderCreateListener(){
            @Override
            public void onCreate(int textureId){
                fboTextureId=textureId;
            }
        });
    }

    public void setCurrentImg(int imgSrc){
        if(lxImgVideoRender!=null){
            lxImgVideoRender.setCurrentImgSrc(imgSrc);
            requestRender();
        }
    }

    public int getFboTextureId(){
        return fboTextureId;
    }
}
