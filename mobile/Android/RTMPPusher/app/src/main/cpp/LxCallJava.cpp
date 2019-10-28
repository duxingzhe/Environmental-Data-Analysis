//
// Created by Luxuan on 2019/10/26.
//

#include "LxCallJava.h"

LxCallJava::LxCallJava(JavaVM *javaVM, JNIEnv *jniEnv, jobject *jobj)
{
    this->jniEnv=jniEnv;
    this->jobj=jniEnv->NewGlobalRef(*jobj);

    jclass jlz=jniEnv->GetObjectClass(this->jobj);
    jmid_connecting=jniEnv->GetMethodID(jlz, "onConnecting", "()V");
    jmid_connectSuccess=jniEnv->GetMethodID(jlz, "onConnectSuccess", "()V");
    jmid_connectFail=jniEnv->GetMethodID(jlz, "onConnectFail","(LJava/lang/String;)V");
}

LxCallJava::~LxCallJava()
{
    jniEnv->DeleteGlobalRef(jobj);
    javaVM=NULL;
    jniEnv=NULL;
};

void LxCallJava::onConnectInt(int type)
{
    if(type == LX_THREAD_CHILD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0)!=JNI_OK)
        {
            return;
        }

        jniEnv->CallVoidMethod(jobj, jmid_connecting);
        javaVM->DetachCurrentThread();
    }
    else
    {
        jniEnv->CallVoidMethod(jobj, jmid_connecting);
    }
}