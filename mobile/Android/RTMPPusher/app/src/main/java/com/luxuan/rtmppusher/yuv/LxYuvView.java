package com.luxuan.rtmppusher.yuv;

import android.content.Context;
import android.util.AttributeSet;

import com.luxuan.rtmppusher.egl.LXEGLSurfaceView;

public class LxYuvView extends LXEGLSurfaceView {

    private LxYuvRender lxYuvRender;

    public LxYuvView(Context context) {
        this(context, null);
    }

    public LxYuvView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LxYuvView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        lxYuvRender=new LxYuvRender(context);
        setRender(lxYuvRender);
        setRenderMode(LXEGLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setFrameData(int w, int h, byte[] by, byte[] bu, byte[] bv){
        if(lxYuvRender!=null){
            lxYuvRender.setFrameData(w, h, by, bu, bv);
            requestRender();
        }
    }
}
