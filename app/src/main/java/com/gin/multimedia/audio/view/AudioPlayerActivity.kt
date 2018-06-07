package com.gin.multimedia.audio.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.gin.multimedia.R
import com.gin.multimedia.audio.adapter.AudioListAdapter
import kotlinx.android.synthetic.main.audio_player_activity.*
import java.io.File

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/30 11:05
 * @see
 */
class AudioPlayerActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player_activity)
        val dir = "$externalCacheDir/audio/"
        val fDir = File(dir)
        if (fDir.exists()) {
            rv_list.layoutManager = LinearLayoutManager(this)
            rv_list.adapter = AudioListAdapter(this, fDir.listFiles().toList())
        }
    }
}