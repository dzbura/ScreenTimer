package com.example.screentimer.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.screentimer.R
import com.example.screentimer.data.DayStat
import com.example.screentimer.databinding.DayStatItemBinding


class DayStatAdapter internal constructor(
    private val mListener: DayStatItemClickListener
): ListAdapter<DayStat, DayStatAdapter.DayStatViewHolder>(DayStatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayStatViewHolder {
        return DayStatViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DayStatViewHolder, position: Int) {
        val dayStat = getItem(position)
        holder.bind(dayStat, mListener)
    }

    override fun getItemCount(): Int {
        val result = super.getItemCount()
        return result
    }

    class DayStatViewHolder(val binding: DayStatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currentDayStat: DayStat, listener: DayStatItemClickListener) {
            binding.dayStat = currentDayStat
            binding.clickListener = listener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): DayStatAdapter.DayStatViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: DayStatItemBinding = DataBindingUtil.inflate(
                    layoutInflater, R.layout.day_stat_item,
                    parent, false
                )
                return DayStatAdapter.DayStatViewHolder(binding)

            }
        }

    }

}

interface DayStatItemClickListener {
    fun chooseDayStat(dayStat: DayStat)
}

private class DayStatDiffCallback : DiffUtil.ItemCallback<DayStat>() {
    override fun areItemsTheSame(oldItem: DayStat, newItem: DayStat): Boolean {
        return oldItem.toalTime == newItem.toalTime && oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: DayStat, newItem: DayStat): Boolean {
        return oldItem.toalTime == newItem.toalTime && oldItem.date == newItem.date
    }
}