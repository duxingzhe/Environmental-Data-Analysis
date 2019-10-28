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