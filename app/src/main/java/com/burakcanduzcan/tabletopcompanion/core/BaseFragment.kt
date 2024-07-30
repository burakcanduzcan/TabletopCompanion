package com.burakcanduzcan.tabletopcompanion.core

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseFragment<T : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> T,
) : Fragment() {

    private var _binding: T? = null
    protected val binding: T get() = _binding!!

    abstract val viewModel: ViewModel

    private var isClickable = true
    private var soundMediaPlayer: MediaPlayer? = null

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

    override fun onDestroy() {
        super.onDestroy()
        soundMediaPlayer?.release()
        soundMediaPlayer = null
    }

    protected fun safeClick(action: () -> Unit) {
        if (isClickable) {
            isClickable = false
            CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                isClickable = true
            }
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

    fun playSound(@RawRes soundRes: Int) {
        if (soundMediaPlayer == null) {
            soundMediaPlayer = MediaPlayer.create(requireContext(), soundRes)
            soundMediaPlayer!!.isLooping = false
            soundMediaPlayer!!.start()
        } else {
            //to prevent sounds collapsing;
            //stop, release
            soundMediaPlayer!!.stop()
            soundMediaPlayer!!.release()
            //and recreate
            soundMediaPlayer = MediaPlayer.create(requireContext(), soundRes)
            soundMediaPlayer!!.isLooping = false
            soundMediaPlayer!!.start()
        }
    }
}