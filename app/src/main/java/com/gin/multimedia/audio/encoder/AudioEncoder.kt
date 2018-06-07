package com.gin.multimedia.audio.encoder

import android.media.*
import android.media.MediaCodec.*
import android.media.MediaFormat
import android.util.Log


/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/30 17:26
 * @see
 */
class AudioEncoder: Encoder {

    private var mediaCodecInfo: MediaCodecInfo?
    private var mediaCodec : MediaCodec
    private var outputFormat: MediaFormat

    init {
        mediaCodecInfo = selectCodec("audio/3gpp")
        mediaCodec = MediaCodec.createEncoderByType("audio/3gpp")
        val format = MediaFormat()
        format.setString(MediaFormat.KEY_MIME, "audio/3gpp")
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1)
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, 44100)
        format.setInteger(MediaFormat.KEY_BIT_RATE, 128000)
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
//        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 16384)
        mediaCodec.configure(format,
                null,
                null,
                CONFIGURE_FLAG_ENCODE
                )
        outputFormat = mediaCodec.outputFormat
    }

    override fun start() {
        mediaCodec.start()
    }

    fun stop() {
        mediaCodec.stop()
        mediaCodec.release()
    }

    override fun encode(buffer: ByteArray, offset: Int, length: Int): ByteArray {
        val inputBufferId = mediaCodec.dequeueInputBuffer(1000)
        if (inputBufferId >= 0) {
            val inputBuffer = mediaCodec.inputBuffers[inputBufferId]
            // fill inputBuffer with valid data
            inputBuffer.clear()
            inputBuffer.put(buffer, offset, length)
            if (length < buffer.size) {
                mediaCodec.queueInputBuffer(inputBufferId, 0, length, System.currentTimeMillis(), BUFFER_FLAG_END_OF_STREAM)
            } else {
                mediaCodec.queueInputBuffer(inputBufferId, 0, length, System.currentTimeMillis(), BUFFER_FLAG_CODEC_CONFIG)
            }
        }

        val bufferInfo = MediaCodec.BufferInfo()
        /*if (length < buffer.size) {
            bufferInfo.set(0, length, System.currentTimeMillis(), BUFFER_FLAG_END_OF_STREAM)
        } else {
            bufferInfo.set(0, length, System.currentTimeMillis(), BUFFER_FLAG_CODEC_CONFIG)
        }*/
        val outputBufferId = mediaCodec.dequeueOutputBuffer(bufferInfo, 1000)
        if (outputBufferId >= 0) {
            val outputBuffer = mediaCodec.outputBuffers[outputBufferId]
            val bufferFormat = mediaCodec.outputFormat // option A
            // bufferFormat is identical to outputFormat
            // outputBuffer is ready to be processed or rendered.
            val frame = ByteArray(bufferInfo.size)
            outputBuffer.get(frame, 0, bufferInfo.size)
            Log.e("encoder", "bufferInfo:${bufferInfo.size}")
            mediaCodec.releaseOutputBuffer(outputBufferId, false)
            return frame
        } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            // Subsequent data will conform to new format.
            // Can ignore if using getOutputFormat(outputBufferId)
            outputFormat = mediaCodec.outputFormat // option B
            Log.e("encoder", "INFO_OUTPUT_FORMAT_CHANGED")
            return ByteArray(0)
        } else {
            return ByteArray(0)
        }
    }

    private fun selectCodec(mimeType: String): MediaCodecInfo? {
        val numCodecs = MediaCodecList.getCodecCount()
        for (i in 0 until numCodecs) {
            val codecInfo = MediaCodecList.getCodecInfoAt(i)

            if (!codecInfo.isEncoder) {
                continue
            }

            val types = codecInfo.supportedTypes
            for (j in types.indices) {
                if (types[j].equals(mimeType, ignoreCase = true)) {
                    return codecInfo
                }
            }
        }
        return null
    }
}