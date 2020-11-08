package com.mediatek.wwtv.setting.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CachedInputStream extends FilterInputStream {
    private static final int CHUNK_SIZE = 16384;
    private ArrayList<byte[]> mBufs = new ArrayList<>();
    private int mCount = 0;
    private int mMarkLimit;
    private int mMarkPos = -1;
    private int mOverrideMarkLimit;
    private int mPos = 0;
    private byte[] tmp = new byte[1];

    public CachedInputStream(InputStream in) {
        super(in);
    }

    public boolean markSupported() {
        return true;
    }

    public void setOverrideMarkLimit(int overrideMarkLimit) {
        this.mOverrideMarkLimit = overrideMarkLimit;
    }

    public int getOverrideMarkLimit() {
        return this.mOverrideMarkLimit;
    }

    public void mark(int readlimit) {
        int chunks;
        int readlimit2 = readlimit < this.mOverrideMarkLimit ? this.mOverrideMarkLimit : readlimit;
        if (this.mMarkPos >= 0 && (chunks = this.mPos / 16384) > 0) {
            int removedBytes = chunks * 16384;
            List<byte[]> subList = this.mBufs.subList(0, chunks);
            releaseChunks(subList);
            subList.clear();
            this.mPos -= removedBytes;
            this.mCount -= removedBytes;
        }
        this.mMarkPos = this.mPos;
        this.mMarkLimit = readlimit2;
    }

    public void reset() throws IOException {
        if (this.mMarkPos >= 0) {
            this.mPos = this.mMarkPos;
            return;
        }
        throw new IOException("mark has been invalidated");
    }

    public int read() throws IOException {
        if (read(this.tmp, 0, 1) <= 0) {
            return -1;
        }
        return this.tmp[0] & 255;
    }

    public void close() throws IOException {
        if (this.in != null) {
            this.in.close();
            this.in = null;
        }
        releaseChunks(this.mBufs);
    }

    private static void releaseChunks(List<byte[]> bufs) {
        ByteArrayPool.get16KBPool().releaseChunks(bufs);
    }

    private byte[] allocateChunk() {
        return ByteArrayPool.get16KBPool().allocateChunk();
    }

    private boolean invalidate() {
        if (this.mCount - this.mMarkPos <= this.mMarkLimit) {
            return false;
        }
        this.mMarkPos = -1;
        this.mCount = 0;
        this.mPos = 0;
        releaseChunks(this.mBufs);
        this.mBufs.clear();
        return true;
    }

    public int read(byte[] buffer, int offset, int count) throws IOException {
        if (this.in == null) {
            throw streamClosed();
        } else if (this.mMarkPos == -1) {
            return this.in.read(buffer, offset, count);
        } else {
            if (count == 0) {
                return 0;
            }
            int copied = copyMarkedBuffer(buffer, offset, count);
            int count2 = count - copied;
            int offset2 = offset + copied;
            int totalReads = copied;
            while (true) {
                if (count2 > 0) {
                    if (this.mPos == this.mBufs.size() * 16384) {
                        this.mBufs.add(allocateChunk());
                    }
                    int currentBuf = this.mPos / 16384;
                    int indexInBuf = this.mPos - (currentBuf * 16384);
                    byte[] buf = this.mBufs.get(currentBuf);
                    int leftInBuffer = ((currentBuf + 1) * 16384) - this.mPos;
                    int reads = this.in.read(buf, indexInBuf, count2 > leftInBuffer ? leftInBuffer : count2);
                    if (reads <= 0) {
                        break;
                    }
                    System.arraycopy(buf, indexInBuf, buffer, offset2, reads);
                    this.mPos += reads;
                    this.mCount += reads;
                    totalReads += reads;
                    offset2 += reads;
                    count2 -= reads;
                    if (invalidate()) {
                        int reads2 = this.in.read(buffer, offset2, count2);
                        if (reads2 > 0) {
                            totalReads += reads2;
                        }
                    }
                } else {
                    break;
                }
            }
            if (totalReads == 0) {
                return -1;
            }
            return totalReads;
        }
    }

    private int copyMarkedBuffer(byte[] buffer, int offset, int read) {
        int totalRead = 0;
        while (read > 0 && this.mPos < this.mCount) {
            int currentBuf = this.mPos / 16384;
            int indexInBuf = this.mPos - (currentBuf * 16384);
            byte[] buf = this.mBufs.get(currentBuf);
            int end = (currentBuf + 1) * 16384;
            if (end > this.mCount) {
                end = this.mCount;
            }
            int leftInBuffer = end - this.mPos;
            int toRead = read > leftInBuffer ? leftInBuffer : read;
            System.arraycopy(buf, indexInBuf, buffer, offset, toRead);
            offset += toRead;
            read -= toRead;
            totalRead += toRead;
            this.mPos += toRead;
        }
        return totalRead;
    }

    public int available() throws IOException {
        if (this.in != null) {
            return (this.mCount - this.mPos) + this.in.available();
        }
        throw streamClosed();
    }

    public long skip(long byteCount) throws IOException {
        if (this.in == null) {
            throw streamClosed();
        } else if (this.mMarkPos < 0) {
            return this.in.skip(byteCount);
        } else {
            long totalSkip = (long) (this.mCount - this.mPos);
            if (totalSkip > byteCount) {
                totalSkip = byteCount;
            }
            this.mPos = (int) (((long) this.mPos) + totalSkip);
            long byteCount2 = byteCount - totalSkip;
            while (byteCount2 > 0) {
                if (this.mPos == this.mBufs.size() * 16384) {
                    this.mBufs.add(allocateChunk());
                }
                int currentBuf = this.mPos / 16384;
                int indexInBuf = this.mPos - (currentBuf * 16384);
                byte[] buf = this.mBufs.get(currentBuf);
                int leftInBuffer = ((currentBuf + 1) * 16384) - this.mPos;
                int reads = this.in.read(buf, indexInBuf, (int) (byteCount2 > ((long) leftInBuffer) ? (long) leftInBuffer : byteCount2));
                if (reads <= 0) {
                    return totalSkip;
                }
                this.mPos += reads;
                this.mCount += reads;
                byteCount2 -= (long) reads;
                totalSkip += (long) reads;
                if (invalidate()) {
                    if (byteCount2 > 0) {
                        return totalSkip + this.in.skip(byteCount2);
                    }
                    return totalSkip;
                }
            }
            return totalSkip;
        }
    }

    private static IOException streamClosed() {
        return new IOException("stream closed");
    }
}
