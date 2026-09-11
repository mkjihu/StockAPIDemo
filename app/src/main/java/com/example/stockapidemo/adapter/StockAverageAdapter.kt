package com.example.stockapidemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.example.stockapidemo.R
import com.example.stockapidemo.databinding.ItemStockAverageBinding
import com.example.stockapidemo.model.StockAverage

class StockAverageAdapter :
    BaseQuickAdapter<StockAverage, DataBindingHolder<ItemStockAverageBinding>>() {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<ItemStockAverageBinding> {
        return DataBindingHolder(
            ItemStockAverageBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: DataBindingHolder<ItemStockAverageBinding>,
        position: Int,
        item: StockAverage?
    ) {
        holder.binding.apply {
            val background = if (position % 2 == 0) {
                R.color.stock_row_background
            } else {
                R.color.stock_row_alternate
            }
            root.setBackgroundColor(ContextCompat.getColor(root.context, background))
            columns.code.text = displayValue(item?.code)
            columns.name.text = displayValue(item?.name)
            columns.closingPrice.text = displayValue(item?.closingPrice)
            columns.monthlyAveragePrice.text = displayValue(item?.monthlyAveragePrice)
        }
    }

    private fun displayValue(value: String?): String =
        value?.trim()?.takeIf { it.isNotEmpty() && it != "--" } ?: "—"
}