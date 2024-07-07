package com.burakcanduzcan.tabletopcompanion.core

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> T,
) : Fragment() {

    private var _binding: T? = null
    protected val binding: T get() = _binding!!

    abstract val viewModel: ViewModel

    private var isClickable = true

    abstract fun initUi()
    abstract fun initListeners()
    abstract fun initObservables()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initListeners()
        initObservables()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun safeClick(action: () -> Unit) {
        if (isClickable) {
            isClickable = false
            Thread {
                Thread.sleep(500)
                isClickable = true
            }.start()
            action.invoke()
        }
    }

    fun setTitle(title: String) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    fun dismissKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (requireActivity().currentFocus != null) {
            imm.hideSoftInputFromWindow(requireActivity().currentFocus!!.applicationWindowToken, 0)
        }
    }
}