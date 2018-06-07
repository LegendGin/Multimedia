package com.gin.multimedia.audio.bean;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/30 16:31
 * @see
 */

public class AmrNBFileWriter {

    private String mFilepath;
    private int mDataSize = 0;
    private DataOutputStream mDataOutputStream;

    public boolean openFile(String filepath, int sampleRateInHz, int channels, int bitsPerSample) throws IOException {
        if (mDataOutputStream != null) {
            closeFile();
        }
        mFilepath = filepath;
        mDataSize = 0;
        mDataOutputStream = new DataOutputStream(new FileOutputStream(filepath));
        return writeHeader(sampleRateInHz, bitsPerSample, channels);
    }

    public boolean closeFile() throws IOException {
        boolean ret = true;
        if (mDataOutputStream != null) {
//            ret = writeDataSize();
            mDataOutputStream.close();
            mDataOutputStream = null;
        }
        return ret;
    }

    private boolean writeHeader(int sampleRateInHz, int channels, int bitsPerSample) {
        if (mDataOutputStream == null) {
            return false;
        }

        try {
            byte[] head = new byte[]{'#', '!', 'A', 'M', 'R', '\n'};
            mDataOutputStream.write(head);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean writeDataSize() {
        if (mDataOutputStream == null) {
            return false;
        }

        try {
            RandomAccessFile wavFile = new RandomAccessFile(mFilepath, "rw");
            wavFile.seek(WavFileHeader.WAV_CHUNKSIZE_OFFSET);
            wavFile.write(intToByteArray((mDataSize + WavFileHeader.WAV_CHUNKSIZE_EXCLUDE_DATA)), 0, 4);
            wavFile.seek(WavFileHeader.WAV_SUB_CHUNKSIZE2_OFFSET);
            wavFile.write(intToByteArray((mDataSize)), 0, 4);
            wavFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static byte[] intToByteArray(int data) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data).array();
    }

    private static byte[] shortToByteArray(short data) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(data).array();
    }

    public boolean writeData(byte[] buffer, int offset, int count) {
        if (mDataOutputStream == null) {
            return false;
        }

        try {
            mDataOutputStream.write(buffer, offset, count);
            mDataSize += count;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
