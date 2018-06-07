package com.gin.multimedia.audio

import java.io.File

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/30 14:35
 * @see
 */
interface AudioPlayer {
    fun setPath(path: String)
    fun setFile(file: File)
    fun setFileList(fileList: List<File>)
    fun play()
    fun pause()
    fun resume()
    fun stop()
}