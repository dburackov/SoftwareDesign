package com.dmburackov.converter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.dmburackov.converter.databinding.FragmentKeyboardBinding

class KeyboardFragment : Fragment() {
    private lateinit var binding: FragmentKeyboardBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeyboardBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            key0Text.setOnClickListener {
                viewModel.newKey.value = "0"
            }
            key1Text.setOnClickListener {
                viewModel.newKey.value = "1"
            }
            key2Text.setOnClickListener {
                viewModel.newKey.value = "2"
            }
            key3Text.setOnClickListener {
                viewModel.newKey.value = "3"
            }
            key4Text.setOnClickListener {
                viewModel.newKey.value = "4"
            }
            key5Text.setOnClickListener {
                viewModel.newKey.value = "5"
            }
            key6Text.setOnClickListener {
                viewModel.newKey.value = "6"
            }
            key7Text.setOnClickListener {
                viewModel.newKey.value = "7"
            }
            key8Text.setOnClickListener {
                viewModel.newKey.value = "8"
            }
            key9Text.setOnClickListener {
                viewModel.newKey.value = "9"
            }
            keyPointText.setOnClickListener {
                viewModel.newKey.value = "."
            }
            keyDeleteText.setOnClickListener {
                viewModel.newKey.value = "delete"
            }
            keyACText.setOnClickListener {
                viewModel.newKey.value = "clear"
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = KeyboardFragment()
    }
}