package com.gin.multimedia

import android.app.Activity
import android.content.Context
import android.databinding.BindingAdapter
import android.os.Environment
import android.os.SystemClock
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/29 16:05
 * @see
 */
class Utils {
    companion object {
        @JvmStatic
        @BindingAdapter("bind:data")
        fun setData(recyclerView: RecyclerView, data: Map<String, Class<Activity>>) {
            recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
            recyclerView.adapter = MainAdapter(recyclerView.context, data)
        }

        fun getOutputFile(context: Context, fileName: String): File {
            val sdCard = Environment.getExternalStorageDirectory()
            val path = StringBuilder(sdCard.path).append(File.separator)
                    .append(context.packageName).append(File.separator)
            val dir = File(path.toString())
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(path.append(fileName).toString())
            if (!file.exists()) {
                file.createNewFile()
            }
            return file
        }

        fun getTimeStamp(): String {
            val fmt = SimpleDateFormat("yyyyMMddHHmmss")
            return fmt.format(Date(System.currentTimeMillis()))
        }
    }
}