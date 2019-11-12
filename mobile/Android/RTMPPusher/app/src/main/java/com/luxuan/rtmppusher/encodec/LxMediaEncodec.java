package com.luxuan.rtmppusher.encodec;

import android.content.Context;

public class LxMediaEncodec extends LxBaseMediaEncoder{

    private LxEncodecRender lxEncodecRender;

    public LxMediaEncodec(Context context,int textureId){
        super(context);
        lxEncodecRender=new LxEncodecRender(context, textureId);
        setRender(lxEncodecRender);
        setRenderMode(LxBaseMediaEncoder.RENDERMODE_CONTINUOUSLY);
    }
}
