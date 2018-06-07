package com.gin.multimedia.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.gin.multimedia.audio.encoder.AmrEncoder
import com.gin.multimedia.audio.encoder.AudioEncoder
import com.gin.multimedia.audio.encoder.Encoder

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/28 16:36
 * @see
 */
class AmrNBRecorder: AudioManager() {

    private var audioRecord: AudioRecord
    private val audioEncoder: Encoder = AmrEncoder()
    private var isStart = false
    private var minBufferSize: Int
    private val TAG = "audiomanager"
    var audioCallback: AudioCallback? = null

    init {
        minBufferSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT)
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,
                8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize * 4)
        Log.e(TAG, "minBufferSize:$minBufferSize")
    }

    override fun setCallback(callback: AudioCallback) {
        this.audioCallback = callback
    }

    private fun isPrepared() = audioRecord.state == AudioRecord.STATE_INITIALIZED

    override fun startRecord() {
        if (isPrepared()) {
            audioRecord.startRecording()
            isStart = true
            audioEncoder.start()
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

        private var offset = 0

        override fun run() {
            while (isStart) {
                var buffer = ByteArray(320)
                var ret = audioRecord.read(buffer, 0, buffer.size)
                when (ret) {
                    AudioRecord.ERROR_INVALID_OPERATION -> Log.e(TAG , "Error ERROR_INVALID_OPERATION")
                    AudioRecord.ERROR_BAD_VALUE -> Log.e(TAG , "Error ERROR_BAD_VALUE")
                    else -> {
                        offset += ret
                        val frame = audioEncoder.encode(buffer, 0, ret)
                        if (frame.isNotEmpty()) {
                            audioCallback?.onAudioCaptured(frame, 0, frame.size)
                        }
                        Log.e(TAG , "OK, Captured "+ret+" bytes !")
                    }
                }
            }
        }
    }
}