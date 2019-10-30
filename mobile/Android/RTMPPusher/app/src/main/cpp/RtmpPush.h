//
// Created by Luxuan on 2019/10/26.
//

#ifndef RTMPPUSHER_RTMPPUSH_H
#define RTMPPUSHER_RTMPPUSH_H

#include <malloc.h>
#include <string.h>
#include "LxQueue.h"
#include "pthread.h"
#include "LxCallJava.h"

extern "C"
{
#include "librtmp/rtmp.h"
};

class RtmpPush
{

public:
    RTMP *rtmp=NULL;
    char *url=NULL;
    LxQueue *queue=NULL;
    pthread_t push_thread;
    LxCallJava *lxCallJava=NULL;
    bool startPushing=false;
    long startTime=0;

public:
    RtmpPush(const char *url, LxCallJava *lxCallJava);
    ~RtmpPush();

    void init();
    void pushSPSPPS(char *sps, int sps_len, char *pps, int pps_len);
    void pushVideoData(char *data, int data_len, bool keyframe);
    void pushAudioData(char *data, int data_len);
    void pushStop();
};

#endif //RTMPPUSHER_RTMPPUSH_H
