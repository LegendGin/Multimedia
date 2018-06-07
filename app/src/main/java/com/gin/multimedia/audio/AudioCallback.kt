package com.gin.multimedia.audio

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/28 17:36
 * @see
 */
interface AudioCallback {
    fun onAudioCaptured(buffer: ByteArray, offset: Int, length: Int)
}