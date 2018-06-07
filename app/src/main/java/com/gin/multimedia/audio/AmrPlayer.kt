package com.gin.multimedia.audio

import android.media.*
import android.media.AudioManager
import android.media.AudioTrack.MODE_STREAM
import android.util.Log
import java.io.*
import java.util.concurrent.Executors

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/30 14:36
 * @see
 */
class AmrPlayer: AudioPlayer {

    private var file: File? = null
    private var fileList = listOf<File>()
    private val executor = Executors.newSingleThreadExecutor()
    private val SAMPLE_RATE = 8000
    private val CHANNEL = AudioFormat.CHANNEL_OUT_MONO
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
        private var randomAccessFile = RandomAccessFile(file, "r")
        private var dis = DataInputStream(BufferedInputStream(FileInputStream(file)))
        private var mediaCodec : MediaCodec

        init {
            Log.e("BUFFER", "$BUFFER")
            mediaCodec = MediaCodec.createDecoderByType("audio/3gpp")
            val format = MediaFormat()
            format.setString(MediaFormat.KEY_MIME, "audio/3gpp")
            format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1)
            format.setInteger(MediaFormat.KEY_SAMPLE_RATE, 8000)
//            format.setInteger(MediaFormat.KEY_BIT_RATE, 4750)
            mediaCodec.configure(format,
                    null,
                    null,
                    0
            )
        }

        override fun run() {
            var buffer = ByteArray(13)
            var len: Int
            randomAccessFile.seek(6)
            mediaCodec.start()
            while (isPlaying) {
                len = randomAccessFile.read(buffer, 0, buffer.size)
                if(len != -1) {
                    val decoded = decode(buffer, 0, len)
                    Log.e("decoded", "size:${decoded.size}")
                    if (decoded.isNotEmpty()) {
                        audioTrack.write(decoded, 0, decoded.size)
                    }
                } else {
                    break
                }
            }
        }

        private fun creation(): () -> Long {
            var i = 0L
            return {
                i++
            }
        }

        private val c = creation()

        fun decode(buffer: ByteArray, offset: Int, length: Int): ByteArray {
            val inputBufferId = mediaCodec.dequeueInputBuffer(1000)
            if (inputBufferId >= 0) {
                val inputBuffer = mediaCodec.inputBuffers[inputBufferId]
                // fill inputBuffer with valid data
                inputBuffer.clear()
                Log.e("amrencoder", "inputbuffer:${inputBuffer.limit()} position:${inputBuffer.position()} length:${length}")
                inputBuffer.put(buffer, offset, length)
                if (length > buffer.size) {
                    mediaCodec.queueInputBuffer(inputBufferId, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                } else {
                    mediaCodec.queueInputBuffer(inputBufferId, 0, length, c(), 0)
                }
            }

            val bufferInfo = MediaCodec.BufferInfo()
            val outputBufferId = mediaCodec.dequeueOutputBuffer(bufferInfo, 1000)
            var frame = ByteArray(bufferInfo.size)
            if (outputBufferId >= 0) {
                var outputBuffer = mediaCodec.outputBuffers[outputBufferId]
                Log.e("encoder", "output:${outputBuffer.position()} limit:${outputBuffer.limit()}")
                val bufferFormat = mediaCodec.outputFormat // option A
                // bufferFormat is identical to outputFormat
                // outputBuffer is ready to be processed or rendered.
                val output = outputBuffer.get(frame, 0, bufferInfo.size)
                Log.e("encoder", "bufferInfo:${bufferInfo.size}")
                mediaCodec.releaseOutputBuffer(outputBufferId, false)
            } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // Subsequent data will conform to new format.
                // Can ignore if using getOutputFormat(outputBufferId)
                Log.e("encoder", "INFO_OUTPUT_FORMAT_CHANGED")
            } else if (outputBufferId == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            }
            return frame
        }

        fun stop() {
            isPlaying = false
            dis.close()
        }
    }
}