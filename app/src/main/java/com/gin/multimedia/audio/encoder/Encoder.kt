package com.gin.multimedia.audio.encoder

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/31 17:09
 * @see
 */
interface Encoder {
    fun start()
    fun encode(buffer: ByteArray, offset: Int, length: Int): ByteArray
}