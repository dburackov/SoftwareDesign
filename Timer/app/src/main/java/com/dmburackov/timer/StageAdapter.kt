package com.dmburackov.timer

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dmburackov.timer.databinding.StageListItemBinding

class StageAdapter(private val workout: Workout) : RecyclerView.Adapter<StageAdapter.StageHolder>() {
    private var current = 0

    class StageHolder(private val view : View) : RecyclerView.ViewHolder(view) {
        val binding = StageListItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StageHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.stage_list_item, parent, false)
        return StageHolder(listItem)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StageHolder, position: Int) {
        holder.binding.mainLayout.setBackgroundColor(Color.parseColor("#${workout.color}"))

        if (position == current) {
            holder.binding.mainLayout.setBackgroundColor(Color.parseColor("#000000"))
        }

        holder.binding.timeText.text = "${position + 1}. " + Workout.getStageName(position, workout.size()) + ": " + workout.getValues()[position].toString()
    }

    override fun getItemCount(): Int = workout.getValues().size

    fun setCurrentStage(pos : Int) {
        val temp = current
        current = pos
        notifyItemChanged(temp)
        notifyItemChanged(pos)
    }
}