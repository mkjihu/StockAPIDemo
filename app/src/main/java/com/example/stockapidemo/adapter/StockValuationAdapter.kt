package com.example.stockapidemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.example.stockapidemo.R
import com.example.stockapidemo.databinding.ItemStockValuationBinding
import com.example.stockapidemo.model.StockValuation

class StockValuationAdapter(
    private val isExpanded: (String) -> Boolean,
    private val toggleExpanded: (String) -> Boolean
) : BaseQuickAdapter<StockValuation, DataBindingHolder<ItemStockValuationBinding>>() {
    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int) =
        DataBindingHolder(ItemStockValuationBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(
        holder: DataBindingHolder<ItemStockValuationBinding>, position: Int, item: StockValuation?
    ) {
        holder.binding.apply {
            root.setBackgroundColor(ContextCompat.getColor(root.context,
                if (position % 2 == 0) R.color.stock_row_background else R.color.stock_row_alternate
            ))
            columns.code.text = displayValue(item?.code)
            columns.name.text = displayValue(item?.name)
            columns.peRatio.text = displayValue(item?.peRatio)
            dividend.text = root.context.getString(R.string.dividend_value, displayValue(item?.dividendYield))
            pbRatio.text = root.context.getString(R.string.pb_value, displayValue(item?.pbRatio))
            val code = item?.code.orEmpty()
            val expanded = isExpanded(code)
            expandableLayout.setExpanded(expanded, false)
            columns.arrow.animate().cancel()
            columns.arrow.rotation = if (expanded) 180f else 0f
            columns.root.isFocusable = true
            columns.root.setOnClickListener {
                if (code.isNotBlank()) {
                    val next = toggleExpanded(code)
                    expandableLayout.setExpanded(next, true)
                    columns.arrow.animate().rotation(if (next) 180f else 0f).setDuration(250).start()
                }
            }
        }
    }

    private fun displayValue(value: String?): String =
        value?.trim()?.takeIf { it.isNotEmpty() && it != "--" } ?: "—"
}
