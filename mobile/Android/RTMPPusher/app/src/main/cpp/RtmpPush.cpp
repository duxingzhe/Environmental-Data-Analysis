//
// Created by Luxuan on 2019/10/26.
//

#include "RtmpPush.h"

RtmpPush::RtmpPush(const char *url, LxCallJava *lxCallJava)
{
    this->url=static_cast<char *>(malloc(512));
    strcpy(this->url, url);
    this->queue=new LxQueue();
    this->lxCallJava=lxCallJava;
}

RtmpPush::~RtmpPush()
{
    queue->notifyQueue();
    queue->clearQueue();
    free(url);
}

void *callBackPush(void *data)
{

    RtmpPush *rtmpPush=static_cast<RtmpPush *>(data);
    rtmpPush->startPushing=false;
    rtmpPush->rtmp=RTMP_Alloc();
    RTMP_Init(rtmpPush->rtmp);
    rtmpPush->rtmp->Link.timeout=10;
    rtmpPush->rtmp->Link.lFlags |= RTMP_LF_LIVE;
    RTMP_SetupURL(rtmpPush->rtmp, rtmpPush->url);
    RTMP_EnableWrite(rtmpPush->rtmp);

    if(!RTMP_Connect(rtmpPush->rtmp, NULL))
    {
        rtmpPush->lxCallJava->onConnectFail("cannot connect the url");
        goto end;
    }

    if(!RTMP_ConnectStream(rtmpPush->rtmp, 0))
    {
        rtmpPush->lxCallJava->onConnectFail("cannot connect the stream of service");
        goto end;
    }

    rtmpPush->lxCallJava->onConnectSuccess();
    rtmpPush->startPushing=true;
    rtmpPush->startTime=RTMP_GetTime();

    while(true)
    {
        if(!rtmpPush->startPushing)
        {
            break;
        }

        RTMPPacket *packet=NULL;
        packet=rtmpPush->queue->getRtmpPacket();

        if(packet!=NULL)
        {
            int result=RTMP_SendPacket(rtmpPush->rtmp, packet, 1);
            LOGD("RTMP_SendPacket result is %d", result);
            RTMPPacket_Free(packet);
            free(packet);
            packet=NULL;
        }
    }

end:
    RTMP_Close(rtmpPush->rtmp);
    RTMP_Free(rtmpPush->rtmp);
    rtmpPush->rtmp=NULL;
    pthread_exit(&rtmpPush->push_thread);
}

void RtmpPush::init()
{
    lxCallJava->onConnectInt(LX_THREAD_MAIN);
    pthread_create(&push_thread, NULL, callBackPush, this);
}
