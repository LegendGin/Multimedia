package com.gin.multimedia.video

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import com.gin.multimedia.R
import com.gin.multimedia.Utils
import com.gin.multimedia.video.camera.CameraManager
import com.gin.multimedia.video.camera.CameraPreview
import kotlinx.android.synthetic.main.camera_activity.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import permissions.dispatcher.*
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/6/5 17:42
 * @see
 */
@RuntimePermissions
class CameraPreviewActivity: AppCompatActivity() {

    private val manager = CameraManager()
    private var camera: Camera? = null
    private var preview: CameraPreview? = null
    private val mediaRecorder = MediaRecorder()
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_activity)
        btn_start.setOnClickListener {
            startPreviewWithPermissionCheck()
        }

        btn_pic.onClick {
            takePhotoWithPermissionCheck()
        }

        btn_video.onClick {
            recordVideoWithPermissionCheck()
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun recordVideo() {
        camera?.let {
            if (!isRecording) {
                isRecording = true
                it.unlock()
                mediaRecorder.setCamera(it)
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA)
                mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))
                mediaRecorder.setOutputFile(Utils.getOutputFile(this@CameraPreviewActivity, Utils.getTimeStamp() + ".mp4").toString())
                mediaRecorder.setPreviewDisplay(preview?.holder?.surface)
                mediaRecorder.prepare()
                mediaRecorder.start()
            } else {
                isRecording = false
                mediaRecorder.stop()
                it.lock()
            }
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun takePhoto() {
        camera?.let {
            it.takePicture(null, { data, camera ->
                val output = Utils.getOutputFile(this@CameraPreviewActivity, Utils.getTimeStamp() + ".bmp")
                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(output)
                    if (data != null) {
                        fos?.write(data)
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    fos?.close()
                }
            }, { data, camera ->
                val output = Utils.getOutputFile(this@CameraPreviewActivity, Utils.getTimeStamp() + ".jpg")
                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(output)
                    fos?.write(data)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    fos?.close()
                }
            })
        }
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun startPreview() {
        camera = manager.getCameraInstance()
        camera?.let {
            preview = CameraPreview(this, it)
            container.removeAllViews()
            container.addView(preview)
        }
//        preview?.start()
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    fun showRationaleForCamera(request: PermissionRequest) {
        val dialog = AlertDialog.Builder(this)
                .setMessage("请打开相机权限")
                .setPositiveButton("ok") { dialog, which ->
                    dialog.dismiss()
                }.create()
        dialog.show()
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun onCameraDenied() {
        Toast.makeText(this, "请打开权限", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    fun onCameraNeverAskAgain() {
        Toast.makeText(this, "请打开权限", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        camera?.release()
        camera = null
        mediaRecorder.release()
    }
}