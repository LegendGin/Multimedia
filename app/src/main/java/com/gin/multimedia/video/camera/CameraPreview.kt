package com.gin.multimedia.video.camera

import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOError
import java.io.IOException

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/6/5 17:11
 * @see
 */
class CameraPreview(context: Context, private val camera: Camera): SurfaceView(context), SurfaceHolder.Callback {

    private var surfaceHolder: SurfaceHolder

    init {
        surfaceHolder = holder
        surfaceHolder.addCallback(this)
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        Log.e("surfaceChanged", "surfaceChanged")
        if (surfaceHolder.surface == null) {
            return
        }

        try {
            camera.stopPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        startPreview(surfaceHolder)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        Log.e("surfaceDestroyed", "surfaceDestroyed")
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.e("surfaceCreated", "surfaceCreated")
        startPreview(holder)
    }

    private fun startPreview(holder: SurfaceHolder?) {
        try {
            camera.setDisplayOrientation(90)
            camera.setPreviewDisplay(holder)
            camera.startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun start() {
        camera.startPreview()
    }
}