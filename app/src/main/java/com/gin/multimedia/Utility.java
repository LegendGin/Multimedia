package com.gin.multimedia;

import android.app.Activity;
import android.databinding.BindingAdapter;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/29 16:31
 * @see
 */
public class Utility {
    @BindingAdapter(value = {"context", "data"},requireAll = false)
    public static void setData(RecyclerView recyclerView, Activity context, Map<String, Class<?>> data) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new MainAdapter(recyclerView.getContext(), data));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    public static byte[] intToByteArray(int data) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data).array();
    }

    public static byte[] shortToByteArray(short data) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(data).array();
    }

    public static short byteArrayToShort(byte[] b) {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public static int byteArrayToInt(byte[] b) {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
}
