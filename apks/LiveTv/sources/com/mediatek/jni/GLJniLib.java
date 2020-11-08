package com.mediatek.jni;

import java.io.PrintStream;

public class GLJniLib {
    public static native int add(int i, int i2);

    public static native void nativeDone();

    public static native int nativeGetHeight();

    public static native int nativeGetWidth();

    public static native void nativeInit();

    public static native void nativeRender();

    public static native void nativeResize(int i, int i2);

    public static native void nativeTimingChange();

    public static native void nativeUnInit();

    public static native void nativeUpdateTexture(int i);

    static {
        try {
            System.loadLibrary("vss");
            System.out.println("System.loadLibrary libvss: successfully");
        } catch (UnsatisfiedLinkError e) {
            PrintStream printStream = System.out;
            printStream.println("System.loadLibrary" + e);
            e.printStackTrace(System.out);
        }
    }
}
