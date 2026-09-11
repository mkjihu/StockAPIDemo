package com.example.stockapidemo.model

fun <T> List<T>.filterStocks(
    keyword: String,
    code: (T) -> String?,
    name: (T) -> String?
): List<T> {
    val query = keyword.trim()
    if (query.isEmpty()) return this
    return filter {
        code(it).orEmpty().contains(query, ignoreCase = true) ||
            name(it).orEmpty().contains(query, ignoreCase = true)
    }
}
