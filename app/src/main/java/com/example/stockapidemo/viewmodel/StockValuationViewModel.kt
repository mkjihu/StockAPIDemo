package com.example.stockapidemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stockapidemo.model.StockValuation
import com.example.stockapidemo.repository.StockRepository
import com.example.stockapidemo.model.filterStocks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class StockValuationViewModel : ViewModel() {
    private val repository by lazy { StockRepository() }
    private val disposables = CompositeDisposable()
    private var loaded = false
    private var allRows = emptyList<StockValuation>()
    private var keyword = ""
    val hasSearch: Boolean get() = keyword.isNotBlank()
    val dataDates: List<String> get() = allRows.mapNotNull { it.date?.takeIf(String::isNotBlank) }.distinct()
    private val expandedCodes = mutableSetOf<String>()
    private val _stockList = MutableLiveData<List<StockValuation>>(emptyList())
    val stockList: LiveData<List<StockValuation>> = _stockList
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun isExpanded(code: String) = code in expandedCodes

    fun toggleExpanded(code: String): Boolean {
        if (!expandedCodes.add(code)) expandedCodes.remove(code)
        return isExpanded(code)
    }

    fun search(query: String) {
        keyword = query.trim()
        _stockList.value = allRows.filterStocks(keyword, { it.code }, { it.name })
    }

    fun loadIfNeeded() {
        if (loaded || _isLoading.value == true) return
        _isLoading.value = true
        _errorMessage.value = null
        disposables.add(repository.getStockValuations()
            .subscribeOn(Schedulers.io())
            .map { rows -> rows.filter { !it.code.isNullOrBlank() } }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ rows ->
                loaded = true
                allRows = rows
                search(keyword)
                _isLoading.value = false
            }, { error ->
                _errorMessage.value = error.localizedMessage ?: error.javaClass.simpleName
                _isLoading.value = false
            }))
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
