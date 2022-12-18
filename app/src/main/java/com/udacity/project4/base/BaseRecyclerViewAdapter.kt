package com.udacity.project4.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T>(private val callback: ((item: T) -> Unit)? = null) :
    RecyclerView.Adapter<DataBindingViewHolder<T>>() {

    private var _itemsDataList: MutableList<T> = mutableListOf()

    /**
     * Returns the _items data
     */
    private val itemsDataList: List<T>
        get() = this._itemsDataList

    override fun getItemCount() = _itemsDataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<T> {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = DataBindingUtil
            .inflate<ViewDataBinding>(layoutInflater, getLayoutRes(viewType), parent, false)

        binding.lifecycleOwner = getLifecycleOwner()

        return DataBindingViewHolder(binding)
    }//end onCreateViewHolder()

    override fun onBindViewHolder(holder: DataBindingViewHolder<T>, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            callback?.invoke(item)
        }//end setOnClickListener
    }//end onBindViewHolder()

    //Add data to the actual Dataset
    private fun getItem(position: Int) = _itemsDataList[position]

    // to add the data to the database
    fun addData(data: List<T>) {
        _itemsDataList.addAll(data)
        notifyDataSetChanged()
    }//end addData()

    // to clear the data
    fun clear() {
        _itemsDataList.clear()
        notifyDataSetChanged()
    }//end clear()

    @LayoutRes
    abstract fun getLayoutRes(viewType: Int): Int

    open fun getLifecycleOwner(): LifecycleOwner? {
        return null
    }//end getLifeCycleOwner()
}//end abstract class()