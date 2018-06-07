package com.gin.multimedia.audio.view

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.audio_record_activitty.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.gin.multimedia.R
import com.gin.multimedia.audio.AudioCallback
import com.gin.multimedia.audio.IAudioManager
import com.gin.multimedia.audio.NormalAudioManager
import com.gin.multimedia.audio.bean.WaveFileWriter
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream


/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/28 17:41
 * @see
 */
class WaveRecordActivity: AppCompatActivity() {

    private val thisActivity = this
    private val audioManager: IAudioManager = NormalAudioManager()
    private var audioName = ""
    private var file: File? = null
    private val waveFileWriter = WaveFileWriter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_record_activitty)
        audioManager.setCallback(object : AudioCallback {
            override fun onAudioCaptured(buffer: ByteArray, offset: Int, length: Int) {
                try {
                    waveFileWriter.writeData(buffer, offset, length)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        btn_record.onClick {
            if (ContextCompat.checkSelfPermission(thisActivity,
                            Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                                Manifest.permission.READ_CONTACTS)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Toast.makeText(thisActivity, "需要录音权限", Toast.LENGTH_LONG).show()
                } else {

                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(thisActivity, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
                }
            } else {
                startRecord()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecord()
                } else {
                    Toast.makeText(thisActivity, "请打开录音权限", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startRecord() {
        if (!btn_record.isSelected) {
            audioName = "audio-${SystemClock.elapsedRealtime()}.wav"
            val path = "$externalCacheDir/audio/"
            file = File(path)
            file?.let {
                if (!it.exists()) {
                    it.mkdirs()
                }
                file = File(path, audioName)
                if (file!!.createNewFile()) {
//                    dis = DataOutputStream(BufferedOutputStream(FileOutputStream(file)))
                    waveFileWriter.openFile(file.toString(), 44100, AudioFormat.CHANNEL_IN_MONO, 16)
                }
            }
            audioManager.startRecord()
        } else {
            audioManager.stopRecord()
//            dis?.close()
            waveFileWriter.closeFile()
        }
        btn_record.isSelected = !btn_record.isSelected
    }
}