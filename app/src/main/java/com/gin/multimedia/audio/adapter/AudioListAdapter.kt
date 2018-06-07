package com.gin.multimedia.audio.adapter

import android.content.Context
import android.databinding.ViewDataBinding
import com.gin.multimedia.R
import com.gin.multimedia.audio.AudioPlayer
import com.gin.multimedia.audio.SimpleAudioPlayer
import com.gin.multimedia.base.BaseAdapter
import com.gin.multimedia.databinding.AudioListItemBinding
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.io.File

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/30 11:50
 * @see
 */
class AudioListAdapter(context: Context, audioList: List<File>): BaseAdapter<File>(context, audioList) {

    private val audioPlayer: AudioPlayer = SimpleAudioPlayer()

    init {
        addItemViewDelegate(Item())
    }

    inner class Item : ItemViewDelegate<File> {
        override fun isViewForType(item: File): Boolean {
            return true
        }

        override fun getLayoutId(): Int {
            return R.layout.audio_list_item
        }

        override fun convert(holder: ViewHolder, item: File, position: Int) {
            val dataBinding = holder.getDataBinding<AudioListItemBinding>()
            dataBinding.name = item.name
            dataBinding.root.onClick {
                audioPlayer.setFile(item)
                audioPlayer.play()
            }
        }

    }
}