package com.dmburackov.timer

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.dmburackov.timer.databinding.FragmentEditBinding


class EditFragment : Fragment(), MenuProvider {
    private lateinit var binding: FragmentEditBinding
    private val viewModel : MainViewModel by activityViewModels()

    private var workout = Workout()
    private var createState = true
    private lateinit var toast : Toast

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        toast = Toast.makeText(requireActivity(), "Incorrect input values!", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)

        createState = viewModel.workoutEdit== -1
        (activity as MainActivity).supportActionBar?.title = if (createState) {"Create"} else {"Edit"}
        if (createState) {
            binding.titleEdit.setText("Workout ${viewModel.db.size() + 1}")
        } else {
            workout = viewModel.db.getWorkoutById(viewModel.workoutEdit)
            binding.titleEdit.setText(workout.title)
        }
        (activity as MainActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#${workout.color}")))

        binding.apply {
            warmupEdit.setText(workout.warmup.toString())
            workEdit.setText(workout.work.toString())
            restEdit.setText(workout.rest.toString())
            cyclesEdit.setText(workout.cycles.toString())
            cooldownEdit.setText(workout.cooldown.toString())
        }
        findErrors(binding.warmupEdit)
        findErrors(binding.workEdit)
        findErrors(binding.restEdit)
        findErrors(binding.cyclesEdit)
        findErrors(binding.cooldownEdit)
    }


    private fun findErrors(view: EditText) {
        view.addTextChangedListener {
            val str = view.text.toString()
            if (str.isEmpty()) {
                view.error = "This can't be blank"
            } else if (str == "0" || str == "00" || str == "000" || str == "0000") {
                view.error = "Value should be positive"
            } else {
                view.error = null
            }
        }
    }

    private fun checkInput(view : EditText) : Boolean {
        var result = true
        val str = view.text.toString()

        if (str.isEmpty()) {
            result = false
        } else if (str.toInt() <= 0) {
            result = false
        }

        return result
    }

    private fun getEditValue(view : EditText) : Int {
        val result = view.text.toString()
        return if (result.isNotEmpty()) result.toInt() else {1}
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_fragment_menu, menu)

        if (createState) {
            menu.getItem(0).isVisible = false
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId) {
            R.id.done_item -> {
                var correctInput = true
                correctInput = correctInput && checkInput(binding.warmupEdit)
                correctInput = correctInput && checkInput(binding.workEdit)
                correctInput = correctInput && checkInput(binding.restEdit)
                correctInput = correctInput && checkInput(binding.cyclesEdit)
                correctInput = correctInput && checkInput(binding.cooldownEdit)

                if (correctInput) {
                    workout.title = binding.titleEdit.text.toString()
                    workout.warmup = getEditValue(binding.warmupEdit)
                    workout.work = getEditValue(binding.workEdit)
                    workout.rest = getEditValue(binding.restEdit)
                    workout.cycles = getEditValue(binding.cyclesEdit)
                    workout.cooldown = getEditValue(binding.cooldownEdit)

                    if (createState) {
                        viewModel.db.insert(workout)
                    } else {
                        viewModel.db.updateWorkout(workout)
                    }

                    findNavController().navigate(R.id.action_editFragment_to_mainFragment)
                } else {
                    toast.show()
                }
                true
            }
            R.id.delete_item -> {
                val dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setTitle("Delete it?")
                    .setMessage("Are you sure you want to delete it?")
                    .setCancelable(true)
                    .setPositiveButton("delete") { _, _ ->
                        if (!createState) {
                            viewModel.db.deleteWorkoutById(workout.id)
                        }
                        findNavController().navigate(R.id.action_editFragment_to_mainFragment)
                    }
                    .setNegativeButton("cancel") {di, _ ->
                        di.cancel()
                    }
                    .show()
                true
            }
            R.id.color_item -> {
                val palette = PaletteDialog(workout)
                palette.show(parentFragmentManager, "palette")
                true
            }
//            android.R.id.home -> {
//                true
//            }
            else -> false
        }
    }
}