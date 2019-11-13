package com.luxuan.rtmppusher.push;

import android.content.Context;

import com.luxuan.rtmppusher.encodec.LxBaseMediaEncoder;

public class LxPushEncodec extends LxBasePushEncoder {

    private LxEncodecPushRender lxEncodecPushRender;

    public LxPushEncodec(Context context, int textureId){
        super(context);
        lxEncodecPushRender=new LxEncodecPushRender(context, textureId);
        setRender(lxEncodecPushRender);
        setRenderMode(LxBaseMediaEncoder.RENDERMODE_CONTINUOUSLY);
    }
}
