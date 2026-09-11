package com.example.stockapidemo.repository

import com.example.stockapidemo.network.HttpApiClient
import com.example.stockapidemo.network.TwseApi

// 先提供資料入口；清理、錯誤處理與執行緒策略於 API 接入階段補上。
class StockRepository(private val api: TwseApi = HttpApiClient.api) {
    fun getStockValuations() = api.getStockValuations()
    fun getStockAverages() = api.getStockAverages()
    fun getStockTrades() = api.getStockTrades()
}