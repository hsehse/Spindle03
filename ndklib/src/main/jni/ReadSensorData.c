//
// Created by sodgy on 2020-05-02.
//

#include "com_example_ndklib_NativeWrapper.h"

JNIEXPORT jint JNICALL Java_com_example_ndklib_NativeWrapper_readSensorData(JNIEnv *env, jobject obj, jint a, jint b){
    return a+b;
}
