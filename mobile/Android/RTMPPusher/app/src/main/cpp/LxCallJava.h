//
// Created by Luxuan on 2019/10/26.
//

#ifndef RTMPPUSHER_WLCALLJAVA_H
#define RTMPPUSHER_WLCALLJAVA_H

#include <cwchar>
#include "jni.h"

#define WL_THREAD_MAIN 1
#define WL_THREAD_CHILD 2

class LxCallJava {

public:

    JNIEnv *jniEnv=NULL;
    JavaVM *javaVM=NULL;
    jobject jobj;

    jmethodID jmid_connecting;
    jmethodID jmid_connectSuccess;
    jmethodID jmid_connectFail;

public:
    LxCallJava(JavaVM *javaVM, JNIEnv *jniEnv, jobject *obj);
    ~LxCallJava();
    void onConnectInt(int type);
    void onConnectSuccess();
    void onConnectFail(char *msg);

};

#endif //RTMPPUSHER_WLCALLJAVA_H
