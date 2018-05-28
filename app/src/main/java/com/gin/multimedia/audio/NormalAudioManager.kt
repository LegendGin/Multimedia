package com.gin.multimedia.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/28 16:36
 * @see
 */
class NormalAudioManager: AudioManager() {

    private var audioRecord: AudioRecord

    init {
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioRecord.getMinBufferSize(44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT))
    }

    private fun isPrepared() = audioRecord.state == AudioRecord.STATE_INITIALIZED

    override fun startRecord() {
        if (isPrepared()) {
            audioRecord.startRecording()
        }
    }

    override fun stopRecord() {
    }

    override fun play(path: String) {
    }
}