package com.gin.multimedia.audio

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/28 16:15
 * @see
 */
interface IAudioManager {
    fun startRecord()
    fun stopRecord()
    fun play(path: String)
    fun startRecordAndPlay()
    fun stopRecordAndPlay()
}