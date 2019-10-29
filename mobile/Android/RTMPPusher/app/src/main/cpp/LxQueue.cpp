//
// Created by Luxuan on 2019/10/26.
//

#include "LxQueue.h"

LxQueue::LxQueue()
{
    pthread_mutex_init(&mutexPacket, NULL);
    pthread_cond_init(&condPacket, NULL);
}

LxQueue::~LxQueue()
{
    clearQueue();
    pthread_mutex_destroy(&mutexPacket);
    pthread_cond_destroy(&condPacket);
}

int LxQueue::putRtmpPacket(RTMPPacket *packet)
{
    pthread_mutex_lock(&mutexPacket);
    queuePacket.push(packet);
    pthread_cond_signal(&condPacket);
    pthread_mutex_unlock(&mutexPacket);
    return 0;
}

RTMPPacket *LxQueue::getRtmpPacket()
{
    pthread_mutex_lock(&mutexPacket);

    RTMPPacket *p=NULL;
    if(!queuePacket.empty()) {
        p = queuePacket.front();
        queuePacket.pop();
    }
    else
    {
        pthread_cond_wait(&condPacket, &mutexPacket);
    }
    pthread_mutex_unlock(&mutexPacket);
    return p;
}