package com.gin.multimedia

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.gin.multimedia.databinding.MainItemBinding
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/29 16:07
 * @see
 */
class MainAdapter(private val context: Context, private val data: Map<String, Class<*>>): RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.main_item, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mainItemBinding.name = data.keys.elementAt(position)
        holder.mainItemBinding.executePendingBindings()
        holder.itemView.onClick {
            val intent = Intent(this@MainAdapter.context, data.values.elementAt(position))
            this@MainAdapter.context.startActivity(intent)
        }
    }

    class ViewHolder(var mainItemBinding: MainItemBinding) : RecyclerView.ViewHolder(mainItemBinding.root) {

    }
}