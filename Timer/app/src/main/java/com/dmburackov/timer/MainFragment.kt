package com.dmburackov.timer

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmburackov.timer.databinding.FragmentMainBinding
import com.google.android.material.color.MaterialColors


class MainFragment : Fragment(), MenuProvider, WorkoutRecyclerViewAdapter.Listener {
    private lateinit var binding: FragmentMainBinding
    private val viewModel : MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        (activity as MainActivity).supportActionBar?.title = "Workouts: ${viewModel.db.size()}"
        (activity as MainActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.purple_500)))
        binding.timersRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.timersRecycler.adapter = WorkoutRecyclerViewAdapter(viewModel, this)

        binding.createButton.setOnClickListener {
            viewModel.workoutEdit = -1
            findNavController().navigate(R.id.action_mainFragment_to_editFragment)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_fragment_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId) {
            R.id.settings_item -> {
                findNavController().navigate(R.id.action_mainFragment_to_preferenceFragment)
                true
            }
            else -> false
        }
    }

    override fun onPlayButtonClick() {
        findNavController().navigate(R.id.action_mainFragment_to_timerFragment)
    }

    override fun onEditButtonClick() {
        findNavController().navigate(R.id.action_mainFragment_to_editFragment)
    }

}