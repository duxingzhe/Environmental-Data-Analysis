#include <jni.h>
#include <string>

#include "RtmpPush.h"
#include "LxCallJava.h"

RtmpPush *rtmpPush=NULL;
LxCallJava *lxCallJava=NULL;
JavaVM *javaVM=NULL;
bool exit=true;

extern "C"
JNIEXPORT void JNICALL
Java_com_luxuan_pusher_push_LxPushVideo_initPush(JNIEnv *env, jobject instance, jstring pushUrl)
{
    const char *pushUrl_=env->GetStringUTFChars(pushUrl, 0);

    if(lxCallJava==NULL)
    {
        exit=false;
        lxCallJava=new LxCallJava(javaVM, env, &instance);
        rtmpPush=new RtmpPush(pushUrl_, lxCallJava);
        rtmpPush->init();
    }

    env->ReleaseStringUTFChars(pushUrl, pushUrl_);
}

extern "C"
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
    javaVM=vm;
    JNIEnv* env;
    if(vm->GetEnv((void **)&env, JNI_VERSION_1_4)!=JNI_OK)
    {
        if(LOG_SHOW)
        {
            LOGE("GetEnv failed!");
        }

        return -1;
    }

    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved)
{
    javaVM=NULL;
}