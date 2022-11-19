package com.dmburackov.timer

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dmburackov.timer.databinding.WorkoutListItemBinding
import org.w3c.dom.Text

class WorkoutRecyclerViewAdapter(private val viewModel : MainViewModel)
    : RecyclerView.Adapter<WorkoutRecyclerViewAdapter.WorkoutViewHolder>()
{

    class WorkoutViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val binding = WorkoutListItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.workout_list_item, parent, false)
        return WorkoutViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = viewModel.db.getWorkoutByPosition(position)
        holder.binding.titleText.text = workout.title
        holder.binding.warmupText.text = holder.binding.warmupText.text.toString().plus("${workout.warmup} sec")
        holder.binding.workText.text = holder.binding.workText.text.toString().plus("${workout.work} sec")
        holder.binding.restText.text = holder.binding.restText.text.toString().plus("${workout.rest} sec")
        holder.binding.cyclesText.text = holder.binding.cyclesText.text.toString().plus(workout.cycles.toString())
        holder.binding.cooldownText.text = holder.binding.cooldownText.text.toString().plus("${workout.cooldown} sec")

        holder.binding.mainLayout.setBackgroundColor(Color.parseColor("#${workout.color}"))
        //holder.binding.mainLayout.background?.setTint(Color.parseColor("#000000"))

        holder.binding.playButton.setOnClickListener {
            viewModel.workoutGo.value = workout.id
        }
        holder.binding.editButton.setOnClickListener {
            viewModel.workoutEdit.value = workout.id
        }
    }

    override fun getItemCount(): Int {
        return viewModel.db.size()
    }

}
