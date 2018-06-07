package com.gin.multimedia.base

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/30 11:10
 * @see
 */
abstract class BaseAdapter<T>(private val context: Context, private var data: List<T>): RecyclerView.Adapter<BaseAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val itemViewDelegates = SparseArrayCompat<ItemViewDelegate<T>>()

    init {

    }

    fun addItemViewDelegate(itemViewDelegate: ItemViewDelegate<T>) {
        itemViewDelegates.put(itemViewDelegates.size(), itemViewDelegate)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = data[position]
        for (i in 0..itemViewDelegates.size()) {
            if (itemViewDelegates.valueAt(i).isViewForType(item)) {
                return itemViewDelegates.keyAt(i)
            }
        }
        throw IllegalArgumentException("can't find correspond delegate")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = itemViewDelegates.get(viewType)
        return ViewHolder(DataBindingUtil.inflate(inflater, item.getLayoutId(), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        convert(holder, data[position], position)
    }

    open fun convert(holder: ViewHolder, item: T, position: Int) {
        for (i in 0 until itemViewDelegates.size()) {
            val delegate = itemViewDelegates.valueAt(i)
            if (delegate.isViewForType(item)) {
                delegate.convert(holder, item, position)
            }
        }
    }

    class ViewHolder(private var binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

        fun <D : ViewDataBinding> getDataBinding(): D {
            return binding as D
        }
    }

    interface ItemViewDelegate<T>{
        fun isViewForType(item: T): Boolean
        fun getLayoutId(): Int
        fun convert(holder: ViewHolder, item: T, position: Int)
    }
}