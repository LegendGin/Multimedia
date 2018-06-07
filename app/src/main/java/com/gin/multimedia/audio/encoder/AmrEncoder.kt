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
class AmrEncoder: Encoder {

    private var mediaCodecInfo: MediaCodecInfo?
    private var mediaCodec : MediaCodec

    init {
        mediaCodecInfo = selectCodec("audio/3gpp")
        mediaCodec = MediaCodec.createEncoderByType("audio/3gpp")
        val format = MediaFormat()
        format.setString(MediaFormat.KEY_MIME, "audio/3gpp")
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1)
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, 44100)
        format.setInteger(MediaFormat.KEY_BIT_RATE, 4750)
//        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
//        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 650 * 2)
        mediaCodec.configure(format,
                null,
                null,
                CONFIGURE_FLAG_ENCODE
                )
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
            Log.e("amrencoder", "inputbuffer:${inputBuffer.limit()} position:${inputBuffer.position()} length:${length}")
            inputBuffer.put(buffer, offset, length)
            if (length > buffer.size) {
                mediaCodec.queueInputBuffer(inputBufferId, 0, 0, 0, BUFFER_FLAG_END_OF_STREAM)
            } else {
                mediaCodec.queueInputBuffer(inputBufferId, 0, length, 0, 0)
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