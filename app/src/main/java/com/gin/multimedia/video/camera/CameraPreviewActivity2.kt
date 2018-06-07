package com.gin.multimedia.video

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.*
import com.gin.multimedia.R
import com.gin.multimedia.Utils
import com.gin.multimedia.video.camera.CameraManager
import com.gin.multimedia.video.camera.CameraPreview
import kotlinx.android.synthetic.main.camera_activity2.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import permissions.dispatcher.*
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.Executors

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/6/5 17:42
 * @see
 */
@RuntimePermissions
class CameraPreviewActivity2: AppCompatActivity() {

    private val manager = CameraManager()
    private var camera: Camera? = manager.getCameraInstance()
    private val mediaRecorder = MediaRecorder()
    private var isRecording = false
    private var surface: SurfaceTexture? = null
    private var adapter: ArrayAdapter<String>? = null
    private var supportedPicSizes = camera?.parameters?.supportedPictureSizes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_activity2)

        setSupportActionBar(tool)
        adapter = object : ArrayAdapter<String>(this, R.layout.pic_size_item,
                R.id.tv_size, supportedPicSizes?.map {
            "${it.width} * ${it.height}"
        }) {}
        spinner.adapter = adapter

        btn_start.setOnClickListener {
            startPreviewWithPermissionCheck()
        }

        btn_pic.onClick {
            val size = supportedPicSizes?.get(spinner.selectedItemPosition)
            val params = camera?.parameters
            params?.setPictureSize(size?.width ?: 0, size?.height ?: 0)
            camera?.parameters = params
            takePhotoWithPermissionCheck()
        }

        btn_video.onClick {
            recordVideoWithPermissionCheck()
        }

        texture.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
                Log.e("TextureSizeChanged","onSurfaceTextureSizeChanged")
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
                Log.e("onSurfaceTextureUpdated","onSurfaceTextureUpdated")
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                Log.e("onDestroyed","onSurfaceTextureDestroyed")
                camera?.stopPreview()
                return true
            }

            override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                Log.e("TextureAvailable","onSurfaceTextureAvailable")
                this@CameraPreviewActivity2.surface = surface
            }

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
                mediaRecorder.setOutputFile(Utils.getOutputFile(this@CameraPreviewActivity2, Utils.getTimeStamp() + ".mp4").toString())
//                mediaRecorder.setPreviewDisplay(texture.surfaceTexture)
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
            it.autoFocus({
                success, camera ->
                if (success) {
                    camera.takePicture(null, null, { data, camera ->
                        Executors.newSingleThreadExecutor().execute(
                                {
                                    val output = Utils.getOutputFile(this@CameraPreviewActivity2, Utils.getTimeStamp() + ".jpg")
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
                                }
                        )
                    })
                }
            })
        }
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun startPreview() {
        camera = manager.getCameraInstance()
        camera?.let {
            it.setPreviewTexture(surface)
            it.startPreview()
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