package com.example.stockapidemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.example.stockapidemo.databinding.ItemStockTabBinding
import com.example.stockapidemo.model.StockTabItem

class StockTabAdapter(onTabClick: (Int) -> Unit) :
    BaseQuickAdapter<StockTabItem, DataBindingHolder<ItemStockTabBinding>>() {
    init {
        setOnItemClickListener { _, _, position -> onTabClick(position) }
    }

    fun selectTab(position: Int) {
        if (position !in items.indices) return
        items.forEachIndexed { index, item ->
            val selected = index == position
            if (item.selected != selected) {
                this[index] = item.copy(selected = selected)
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<ItemStockTabBinding> {
        return DataBindingHolder(
            ItemStockTabBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: DataBindingHolder<ItemStockTabBinding>,
        position: Int,
        item: StockTabItem?
    ) {
        holder.binding.apply {
            this.item = item
            executePendingBindings()
        }
    }
}