package com.example.stockapidemo

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stockapidemo.view.StockSearchBubble
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.stockapidemo.adapter.StockTabAdapter
import com.example.stockapidemo.databinding.ActivityMainBinding
import com.example.stockapidemo.model.StockTabItem
import com.example.stockapidemo.view.StockAverageFragment
import com.example.stockapidemo.view.StockTradeFragment
import com.example.stockapidemo.view.StockValuationFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tabAdapter: StockTabAdapter
    private lateinit var searchBubble: StockSearchBubble
    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery
    private val queryChanges = PublishSubject.create<String>()
    private val disposables = CompositeDisposable()

    private val pageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            tabAdapter.selectTab(position)
            binding.recyclerViewTab.smoothScrollToPosition(position)
            if (::searchBubble.isInitialized) searchBubble.hideKeyboard()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        WindowCompat.getInsetsController(window, binding.root).isAppearanceLightNavigationBars =
            resources.getBoolean(R.bool.light_system_bars)
        initView()
        initListener()
        initSearch(savedInstanceState)
    }

    private fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            val keyboard = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.setPadding(bars.left, bars.top, bars.right, maxOf(bars.bottom, keyboard))
            insets
        }
        tabAdapter = StockTabAdapter { position ->
            binding.viewPager.setCurrentItem(position, true)
        }
        binding.recyclerViewTab.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewTab.adapter = tabAdapter
        tabAdapter.submitList(
            listOf(
                StockTabItem(getString(R.string.tab_valuation), true),
                StockTabItem(getString(R.string.tab_average)),
                StockTabItem(getString(R.string.tab_trade))
            )
        )
        binding.viewPager.adapter = StockPagerAdapter()
    }

    private fun initListener() {
        binding.viewPager.registerOnPageChangeCallback(pageCallback)
    }

    private fun initSearch(state: Bundle?) {
        disposables.add(queryChanges
            .switchMap { query ->
                if (query.isBlank()) io.reactivex.rxjava3.core.Observable.just("")
                else io.reactivex.rxjava3.core.Observable.just(query)
                    .delay(300, TimeUnit.MILLISECONDS)
            }
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _searchQuery.value = it })
        searchBubble = StockSearchBubble(this, binding.stockSearch) { queryChanges.onNext(it) }
        val query = state?.getString("search_query").orEmpty()
        _searchQuery.value = query
        searchBubble.restore(state?.getBoolean("search_open") == true, query)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val keyboardVisible = ViewCompat.getRootWindowInsets(binding.root)
                    ?.isVisible(WindowInsetsCompat.Type.ime()) == true
                when {
                    keyboardVisible -> searchBubble.hideKeyboard()
                    searchBubble.isOpen -> searchBubble.close()
                    else -> {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        })
    }

    fun hideSearchKeyboard() = searchBubble.hideKeyboard()

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("search_query", searchBubble.query)
        outState.putBoolean("search_open", searchBubble.isOpen)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        disposables.clear()
        searchBubble.dispose()
        binding.viewPager.unregisterOnPageChangeCallback(pageCallback)
        super.onDestroy()
    }

    private inner class StockPagerAdapter : FragmentStateAdapter(this@MainActivity) {
        override fun getItemCount() = 3

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> StockValuationFragment()
            1 -> StockAverageFragment()
            2 -> StockTradeFragment()
            else -> error("Unknown page: $position")
        }
    }
}
