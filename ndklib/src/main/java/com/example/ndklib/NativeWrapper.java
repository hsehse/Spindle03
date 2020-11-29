package com.example.ndklib;

public class NativeWrapper {
    static {
        System.loadLibrary("ndklib");
    }
    //네이티브로 구현할 함수 이름 선언
    public native int readSensorData(int a, int b);
}
