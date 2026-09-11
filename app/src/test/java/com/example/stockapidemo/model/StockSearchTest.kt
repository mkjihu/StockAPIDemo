package com.example.stockapidemo.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class StockSearchTest {
    private val stocks = listOf(
        StockValuation(code = "2330", name = "台積電"),
        StockValuation(code = "1101", name = "台泥"),
        StockValuation(code = "0050", name = "元大台灣50"),
        StockValuation(code = "00980A", name = "主動基金"),
        StockValuation()
    )

    private fun search(query: String) = stocks.filterStocks(query, { it.code }, { it.name })

    @Test fun partialNameMatchesAllRelatedStocks() {
        assertEquals(listOf("2330", "1101", "0050"), search("台").map { it.code })
    }

    @Test fun codePreservesLeadingZerosAndIgnoresCase() {
        assertEquals("0050", search(" 0050 ").single().code)
        assertEquals("00980A", search("980a").single().code)
    }

    @Test fun blankRestoresOriginalList() {
        assertSame(stocks, search(" \n "))
    }

    @Test fun noMatchAndNullFieldsAreHandled() {
        assertEquals(emptyList<StockValuation>(), search("不存在"))
    }

    @Test fun successiveSearchesDoNotDiscardOriginalData() {
        search("2330")
        assertEquals("1101", search("台泥").single().code)
        assertEquals(5, search("").size)
    }

    @Test fun averageAndTradeUseSameMatchingRules() {
        val averages = listOf(StockAverage(code = "2330", name = "台積電"))
        val trades = listOf(StockTrade(code = "2330", name = "台積電"))
        assertEquals(1, averages.filterStocks("台積", { it.code }, { it.name }).size)
        assertEquals(1, trades.filterStocks("233", { it.code }, { it.name }).size)
    }
}
