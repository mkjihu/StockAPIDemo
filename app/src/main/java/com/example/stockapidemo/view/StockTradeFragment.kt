package com.example.stockapidemo.view

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stockapidemo.R
import com.example.stockapidemo.MainActivity
import com.example.stockapidemo.adapter.StockTradeAdapter
import com.example.stockapidemo.databinding.FragmentStockTradeBinding
import com.example.stockapidemo.viewmodel.StockTradeViewModel

class StockTradeFragment : Fragment() {
    private var _binding: FragmentStockTradeBinding? = null
    private val viewModel: StockTradeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentStockTradeBinding.inflate(inflater, container, false).also {
            _binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        initListener()
        observeData()
        viewModel.loadIfNeeded()
    }

    private fun initView() {
        val binding = requireNotNull(_binding)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.columns.apply {
            root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.tab_surface))
            code.setText(R.string.security_code)
            name.setText(R.string.security_name)
            closingPrice.setText(R.string.closing_price)
            change.setText(R.string.change_price)
            arrow.visibility = View.INVISIBLE
            listOf(code, name, closingPrice, change).forEach {
                it.setTypeface(null, Typeface.BOLD)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = StockTradeAdapter().apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    private fun initListener() {
        requireNotNull(_binding).retry.setOnClickListener { viewModel.loadIfNeeded() }
    }

    private fun observeData() {
        val binding = requireNotNull(_binding)
        val adapter = binding.recyclerView.adapter as StockTradeAdapter

        val activity = requireActivity() as MainActivity
        activity.searchQuery.observe(viewLifecycleOwner) { query ->
            viewModel.search(query)
            binding.recyclerView.scrollToPosition(0)
        }
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) activity.hideSearchKeyboard()
            }
        })
        viewModel.stockList.observe(viewLifecycleOwner) { rows ->
            adapter.submitList(rows)
            val dates = viewModel.dataDates
                .distinct()
                .joinToString("、") { formatDate(it) }
            binding.dataDate.text = if (dates.isEmpty()) {
                getString(R.string.date_pending)
            } else {
                getString(R.string.data_date, dates)
            }
            updateMessage()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progress.isVisible = it
            updateMessage()
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { updateMessage() }
    }

    private fun updateMessage() {
        val binding = _binding ?: return
        val error = viewModel.errorMessage.value
        val loading = viewModel.isLoading.value == true
        binding.retry.isVisible = !loading && error != null
        binding.message.isVisible =
            !loading && (error != null || viewModel.stockList.value.isNullOrEmpty())
        binding.message.text = if (error != null) {
            getString(R.string.load_failed, error)
        } else {
            getString(if (viewModel.hasSearch) R.string.search_no_results else R.string.empty_description)
        }
    }

    private fun formatDate(raw: String): String {
        if (raw.length != 7 || raw.any { !it.isDigit() }) return raw
        return (raw.take(3).toInt() + 1911).toString() +
            "/" + raw.substring(3, 5) + "/" + raw.takeLast(2)
    }

    override fun onDestroyView() {
        _binding?.recyclerView?.adapter = null
        _binding = null
        super.onDestroyView()
    }
}