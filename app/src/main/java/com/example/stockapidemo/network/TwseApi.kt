package com.example.stockapidemo.network

import com.example.stockapidemo.model.StockAverage
import com.example.stockapidemo.model.StockTrade
import com.example.stockapidemo.model.StockValuation
import io.reactivex.rxjava3.core.Flowable
import retrofit2.http.GET

interface TwseApi {
    @GET("exchangeReport/BWIBBU_ALL")
    fun getStockValuations(): Flowable<List<StockValuation>>

    @GET("exchangeReport/STOCK_DAY_AVG_ALL")
    fun getStockAverages(): Flowable<List<StockAverage>>

    @GET("exchangeReport/STOCK_DAY_ALL")
    fun getStockTrades(): Flowable<List<StockTrade>>
}