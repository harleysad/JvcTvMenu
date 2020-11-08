package com.mediatek.wwtv.setting.util;

import java.util.ArrayList;
import java.util.List;

public final class ByteArrayPool {
    public static final int CHUNK16K = 16384;
    public static final int DEFAULT_MAX_NUM = 8;
    private static final ByteArrayPool sChunk16K = new ByteArrayPool(16384, 8);
    private final ArrayList<byte[]> mCachedBuf = new ArrayList<>(this.mMaxNum);
    private final int mChunkSize;
    private final int mMaxNum;

    private ByteArrayPool(int chunkSize, int maxNum) {
        this.mChunkSize = chunkSize;
        this.mMaxNum = maxNum;
    }

    public static ByteArrayPool get16KBPool() {
        return sChunk16K;
    }

    public byte[] allocateChunk() {
        synchronized (this.mCachedBuf) {
            int size = this.mCachedBuf.size();
            if (size > 0) {
                byte[] remove = this.mCachedBuf.remove(size - 1);
                return remove;
            }
            byte[] bArr = new byte[this.mChunkSize];
            return bArr;
        }
    }

    public void clear() {
        synchronized (this.mCachedBuf) {
            this.mCachedBuf.clear();
        }
    }

    public void releaseChunk(byte[] buf) {
        if (buf != null && buf.length == this.mChunkSize) {
            synchronized (this.mCachedBuf) {
                if (this.mCachedBuf.size() < this.mMaxNum) {
                    this.mCachedBuf.add(buf);
                }
            }
        }
    }

    public void releaseChunks(List<byte[]> bufs) {
        synchronized (this.mCachedBuf) {
            int i = 0;
            int c = bufs.size();
            while (true) {
                if (i >= c) {
                    break;
                } else if (this.mCachedBuf.size() == this.mMaxNum) {
                    break;
                } else {
                    byte[] buf = bufs.get(i);
                    if (buf != null && buf.length == this.mChunkSize) {
                        this.mCachedBuf.add(bufs.get(i));
                    }
                    i++;
                }
            }
        }
    }
}
