package com.gin.multimedia.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.AudioTrack.MODE_STREAM
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.Executors

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/30 14:36
 * @see
 */
class SimpleAudioPlayer: AudioPlayer {

    private var file: File? = null
    private var fileList = listOf<File>()
    private val executor = Executors.newSingleThreadExecutor()
    private val SAMPLE_RATE = 44100
    private val CHANNEL = AudioFormat.CHANNEL_IN_STEREO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private val BUFFER = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL, AUDIO_FORMAT)
    private val audioTrack = AudioTrack(AudioManager.STREAM_MUSIC,
            SAMPLE_RATE,
            CHANNEL,
            AUDIO_FORMAT,
            BUFFER,
            MODE_STREAM
            )

    private var playTask : PlayTask? = null

    override fun setPath(path: String) {
        val file = File(path)
        setFile(file)
    }

    override fun setFile(file: File) {
        this.file = file
    }

    override fun setFileList(fileList: List<File>) {
        this.fileList = fileList
    }

    override fun play() {
        if (audioTrack.state == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.stop()
        }
        if (audioTrack.state == AudioTrack.STATE_INITIALIZED) {
            audioTrack.play()
        }
        playTask = PlayTask()
        executor.execute(playTask)
    }

    override fun pause() {
        if (audioTrack.state == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.pause()
        }
    }

    override fun resume() {
        if (audioTrack.state == AudioTrack.PLAYSTATE_PAUSED) {
            audioTrack.play()
        }
    }

    override fun stop() {
        if (audioTrack.state == AudioTrack.PLAYSTATE_PLAYING) {
            playTask?.stop()
            audioTrack.stop()
            audioTrack.release()
        }
    }

    inner class PlayTask : Runnable {

        private var isPlaying = true
        private var dis = DataInputStream(BufferedInputStream(FileInputStream(file)))

        override fun run() {
            var buffer = ByteArray(BUFFER)
            var len: Int
            while (isPlaying) {
                len = dis.read(buffer, 0, buffer.size)
                if(len != -1) {
                    audioTrack.write(buffer, 0, len)
                } else {
                    break
                }
            }
        }

        fun stop() {
            isPlaying = false
            dis.close()
        }
    }
}