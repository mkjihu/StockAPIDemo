package com.example.stockapidemo.view

import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.view.isVisible
import com.example.stockapidemo.MainActivity
import com.example.stockapidemo.databinding.ViewStockSearchBinding

class StockSearchBubble(
    private val activity: MainActivity,
    private val binding: ViewStockSearchBinding,
    private val onQueryChanged: (String) -> Unit
) {
    var isOpen = false
        private set
    val query: String get() = binding.query.text.toString()
    private var animator: ValueAnimator? = null
    private val buttonSize = (56 * activity.resources.displayMetrics.density).toInt()

    init {
        binding.searchButton.setOnClickListener { open() }
        binding.closeButton.setOnClickListener { close() }
        binding.query.doAfterTextChanged { onQueryChanged(it.toString()) }
        binding.query.setOnEditorActionListener { _, _, _ ->
            hideKeyboard()
            true
        }
    }

    fun restore(open: Boolean, text: String) {
        isOpen = open
        binding.query.setText(text)
        binding.root.post { showContents(open) }
    }

    private fun open() {
        if (isOpen || animator?.isRunning == true) return
        isOpen = true
        binding.searchButton.isVisible = false
        animateWidth(buttonSize, binding.root.width) {
            showContents(true)
            binding.query.requestFocus()
            WindowCompat.getInsetsController(activity.window, binding.query)
                .show(WindowInsetsCompat.Type.ime())
        }
    }

    fun close() {
        if (!isOpen) return
        isOpen = false
        hideKeyboard()
        binding.query.setText("")
        binding.searchContents.isVisible = false
        animateWidth(binding.bubble.width, buttonSize) { showContents(false) }
    }

    fun hideKeyboard() {
        binding.query.clearFocus()
        WindowCompat.getInsetsController(activity.window, binding.query)
            .hide(WindowInsetsCompat.Type.ime())
    }

    private fun showContents(open: Boolean) {
        binding.bubble.layoutParams = binding.bubble.layoutParams.apply {
            width = if (open) binding.root.width else buttonSize
        }
        binding.searchContents.isVisible = open
        binding.searchButton.isVisible = !open
    }

    private fun animateWidth(from: Int, to: Int, finished: () -> Unit) {
        animator?.removeAllListeners()
        animator?.cancel()
        animator = ValueAnimator.ofInt(from, to).apply {
            duration = 280
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                binding.bubble.layoutParams = binding.bubble.layoutParams.apply {
                    width = it.animatedValue as Int
                }
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) = finished()
            })
            start()
        }
    }

    fun dispose() {
        animator?.removeAllListeners()
        animator?.cancel()
    }
}
