package com.burakcanduzcan.tabletopcompanion.ui.setup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.burakcanduzcan.tabletopcompanion.databinding.FragmentSetupBinding

class SetupFragment : Fragment() {

    private lateinit var binding: FragmentSetupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSetupBinding.inflate(inflater)

        return binding.root
    }
}