package com.gin.multimedia.video.camera

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera


/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/6/5 11:35
 * @see
 */
class CameraManager {

    fun checkCameraHardware(context: Context): Boolean {
        return if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            true
        } else {
            false
        }
    }

    fun getCameraInstance(): Camera? {
        var c: Camera? = null
        try {
            c = Camera.open() // attempt to get a Camera instance
            val parameters = c?.parameters
            parameters?.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            c?.parameters = parameters
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace()
        }

        return c // returns null if camera is unavailable
    }

}