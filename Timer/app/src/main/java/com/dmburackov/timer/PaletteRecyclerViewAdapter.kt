package com.dmburackov.timer

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dmburackov.timer.databinding.PaletteListItemBinding

class PaletteRecyclerViewAdapter(private val currentColor : String, val listener: Listener)
    : RecyclerView.Adapter<PaletteRecyclerViewAdapter.PaletteViewHolder>()
{

    class PaletteViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val binding = PaletteListItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaletteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.palette_list_item, parent, false)
        return PaletteViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: PaletteViewHolder, position: Int) {
        holder.binding.imageView.background?.setTint(Color.parseColor("#${Workout.colors[position]}"))
        if (Workout.colors[position] == currentColor) {
            holder.binding.imageView.setImageResource(R.drawable.ic_done_24)
        }
        holder.itemView.setOnClickListener {
            listener.onClick(Workout.colors[position])
        }
    }

    override fun getItemCount(): Int {
        return Workout.colors.size
    }

    interface Listener {
        fun onClick(color : String)
    }
}