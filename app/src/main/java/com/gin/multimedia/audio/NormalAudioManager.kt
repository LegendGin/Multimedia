package com.gin.multimedia.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.gin.multimedia.audio.encoder.AudioEncoder

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/28 16:36
 * @see
 */
class NormalAudioManager: AudioManager() {

    private var audioRecord: AudioRecord
    private val audioEncoder = AudioEncoder()
    private var isStart = false
    private var minBufferSize: Int
    private val TAG = "audiomanager"
    var audioCallback: AudioCallback? = null

    init {
        minBufferSize = AudioRecord.getMinBufferSize(44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT)
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize * 4)
    }

    override fun setCallback(callback: AudioCallback) {
        this.audioCallback = callback
    }

    private fun isPrepared() = audioRecord.state == AudioRecord.STATE_INITIALIZED

    override fun startRecord() {
        if (isPrepared()) {
            audioRecord.startRecording()
            isStart = true
            AudioThread().start()
        } else {

        }
    }

    override fun stopRecord() {
        isStart = false
        audioRecord.stop()
    }

    override fun play(path: String) {
    }

    inner class AudioThread: Thread() {
        override fun run() {
            while (isStart) {
                var buffer = ByteArray(1024)
                var ret = audioRecord.read(buffer, 0, buffer.size)
                when (ret) {
                    AudioRecord.ERROR_INVALID_OPERATION -> Log.e(TAG , "Error ERROR_INVALID_OPERATION")
                    AudioRecord.ERROR_BAD_VALUE -> Log.e(TAG , "Error ERROR_BAD_VALUE")
                    else -> {
                        audioCallback?.onAudioCaptured(buffer, 0, ret)
                        Log.e(TAG , "OK, Captured "+ret+" bytes !")
                    }
                }
            }
        }
    }
}