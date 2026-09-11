package com.example.stockapidemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.example.stockapidemo.R
import com.example.stockapidemo.databinding.ItemStockTradeBinding
import com.example.stockapidemo.model.StockTrade
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class StockTradeAdapter :
    BaseQuickAdapter<StockTrade, DataBindingHolder<ItemStockTradeBinding>>() {

    private val expandedCodes = mutableSetOf<String>()
    private val numberFormat = DecimalFormat("#,##0.########", DecimalFormatSymbols(Locale.US))

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<ItemStockTradeBinding> {
        return DataBindingHolder(
            ItemStockTradeBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: DataBindingHolder<ItemStockTradeBinding>,
        position: Int,
        item: StockTrade?
    ) {
        holder.binding.apply {
            val context = root.context
            val background = if (position % 2 == 0) {
                R.color.stock_row_background
            } else {
                R.color.stock_row_alternate
            }
            root.setBackgroundColor(ContextCompat.getColor(context, background))
            columns.code.text = displayValue(item?.code)
            columns.name.text = displayValue(item?.name)
            columns.closingPrice.text = displayValue(item?.closingPrice)

            val change = item?.change?.trim()?.replace(",", "")?.toBigDecimalOrNull()
            columns.change.text = if (change == null) {
                "—"
            } else {
                (if (change.signum() > 0) "+" else "") + change.stripTrailingZeros().toPlainString()
            }
            when (change?.signum()) {
                1 -> columns.change.setTextColor(ContextCompat.getColor(context, R.color.trade_up))
                -1 -> columns.change.setTextColor(ContextCompat.getColor(context, R.color.trade_down))
                else -> columns.change.setTextColor(columns.closingPrice.textColors)
            }

            openingPrice.text = context.getString(R.string.opening_value, displayValue(item?.openingPrice))
            highestPrice.text = context.getString(R.string.highest_value, displayValue(item?.highestPrice))
            lowestPrice.text = context.getString(R.string.lowest_value, displayValue(item?.lowestPrice))
            tradeVolume.text = context.getString(R.string.volume_value, formatNumber(item?.tradeVolume))
            tradeValue.text = context.getString(R.string.trade_value, formatNumber(item?.tradeValue))
            transaction.text = context.getString(R.string.transaction_value, formatNumber(item?.transaction))

            val code = item?.code.orEmpty()
            val expanded = expandedCodes.contains(code)
            expandableLayout.setExpanded(expanded, false)
            columns.arrow.animate().cancel()
            columns.arrow.rotation = if (expanded) 180f else 0f
            columns.root.setOnClickListener {
                if (code.isNotBlank()) {
                    if (!expandedCodes.add(code)) expandedCodes.remove(code)
                    val next = expandedCodes.contains(code)
                    expandableLayout.setExpanded(next, true)
                    columns.arrow.animate()
                        .rotation(if (next) 180f else 0f)
                        .setDuration(250)
                        .start()
                }
            }
        }
    }

    private fun displayValue(value: String?): String =
        value?.trim()?.takeIf { it.isNotEmpty() && it != "--" } ?: "—"

    private fun formatNumber(value: String?): String {
        val number = value?.trim()?.replace(",", "")?.toBigDecimalOrNull() ?: return "—"
        return numberFormat.format(number)
    }
}