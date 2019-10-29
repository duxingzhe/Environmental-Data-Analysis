//
// Created by Luxuan on 2019/10/26.
//

#ifndef RTMPPUSHER_LXQUEUE_H
#define RTMPPUSHER_LXQUEUE_H

#include "queue"
#include "pthread.h"
#include "AndroidLog.h"

extern "C"
{
#include "librtmp/rtmp.h"
};

class LxQueue {

public:
    std::queue<RTMPPacket *> queuePacket;
    pthread_mutex_t mutexPacket;
    pthread_cond_t condPacket;

public:
    LxQueue();
    ~LxQueue();

    int putRtmpPacket(RTMPPacket *packet);

    RTMPPacket* getRtmpPacket();

    void clearQueue();

    void notifyQueue();
};



#endif //RTMPPUSHER_LXQUEUE_H
