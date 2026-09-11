package com.example.stockapidemo.model

import com.google.gson.annotations.SerializedName

// 自訂分頁列
data class StockTabItem(
    val title: String,
    val selected: Boolean = false
)

// API 原始資料：字串保留前導零、空值及來源格式，數值轉換於後續資料處理階段實作。
data class StockValuation(
    @SerializedName("Date") val date: String? = null,
    @SerializedName("Code") val code: String? = null,
    @SerializedName("Name") val name: String? = null,
    @SerializedName("PEratio") val peRatio: String? = null,
    @SerializedName("DividendYield") val dividendYield: String? = null,
    @SerializedName("PBratio") val pbRatio: String? = null
)

data class StockAverage(
    @SerializedName("Date") val date: String? = null,
    @SerializedName("Code") val code: String? = null,
    @SerializedName("Name") val name: String? = null,
    @SerializedName("ClosingPrice") val closingPrice: String? = null,
    @SerializedName("MonthlyAveragePrice") val monthlyAveragePrice: String? = null
)

data class StockTrade(
    @SerializedName("Date") val date: String? = null,
    @SerializedName("Code") val code: String? = null,
    @SerializedName("Name") val name: String? = null,
    @SerializedName("TradeVolume") val tradeVolume: String? = null,
    @SerializedName("TradeValue") val tradeValue: String? = null,
    @SerializedName("OpeningPrice") val openingPrice: String? = null,
    @SerializedName("HighestPrice") val highestPrice: String? = null,
    @SerializedName("LowestPrice") val lowestPrice: String? = null,
    @SerializedName("ClosingPrice") val closingPrice: String? = null,
    @SerializedName("Change") val change: String? = null,
    @SerializedName("Transaction") val transaction: String? = null
)