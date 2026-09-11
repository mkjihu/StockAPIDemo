package com.example.stockapidemo.network

import com.example.stockapidemo.repository.StockRepository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Live integration tests using the real API and Gson mapping.
 * Requires internet; TWSE outages or empty responses intentionally fail these tests.
 * Logs appear in Android Studio Run / Gradle console, not device Logcat.
 */
class TwseApiLiveTest {
    private val repository = StockRepository(HttpApiClient.api)

    @Test(timeout = 60_000)
    fun valuationsReturnsData() {
        verify("BWIBBU_ALL", repository.getStockValuations()) {
            !it.code.isNullOrBlank() && !it.name.isNullOrBlank() && !it.date.isNullOrBlank()
        }
    }

    @Test(timeout = 60_000)
    fun averagesReturnsData() {
        verify("STOCK_DAY_AVG_ALL", repository.getStockAverages()) {
            !it.code.isNullOrBlank() && !it.name.isNullOrBlank() && !it.date.isNullOrBlank()
        }
    }

    @Test(timeout = 60_000)
    fun tradesReturnsData() {
        verify("STOCK_DAY_ALL", repository.getStockTrades()) {
            !it.code.isNullOrBlank() && !it.name.isNullOrBlank() && !it.date.isNullOrBlank()
        }
    }

    private fun <T : Any> verify(
        endpoint: String,
        request: Flowable<List<T>>,
        hasRequiredFields: (T) -> Boolean
    ) {
        println("[TwseApiTest][$endpoint] START https://openapi.twse.com.tw/v1/exchangeReport/$endpoint")
        val subscriber = request
            .subscribeOn(Schedulers.io())
            .timeout(50, TimeUnit.SECONDS)
            .doOnError { error ->
                System.err.println("[TwseApiTest][$endpoint] ERROR " + error)
            }
            .test()

        subscriber.awaitDone(55, TimeUnit.SECONDS)
            .assertComplete()
            .assertNoErrors()
            .assertValueCount(1)

        val rows = subscriber.values().single()
        println("[TwseApiTest][$endpoint] count=" + rows.size)
        rows.take(3).forEachIndexed { index, row ->
            println("[TwseApiTest][$endpoint] sample[$index]=$row")
        }
        assertFalse("$endpoint returned an empty list", rows.isEmpty())
        assertTrue(
            "$endpoint contains rows missing Code, Name or Date; check JSON mapping",
            rows.all(hasRequiredFields)
        )
        println("[TwseApiTest][$endpoint] PASS")
    }
}