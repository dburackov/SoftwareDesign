package com.dmburackov.timer

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmburackov.timer.databinding.DialogPaletteBinding

class PaletteDialog(private var workout: Workout) : DialogFragment(), PaletteRecyclerViewAdapter.Listener {
    private lateinit var binding: DialogPaletteBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogPaletteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.paletteRecycler.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.paletteRecycler.adapter = PaletteRecyclerViewAdapter(workout.color, this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClick(color: String) {
        workout.color = color
        (activity as MainActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#${color}")))
        dismiss()
    }
}