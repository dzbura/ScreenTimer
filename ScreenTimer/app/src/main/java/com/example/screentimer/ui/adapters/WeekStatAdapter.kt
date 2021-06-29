package com.example.screentimer.ui.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.screentimer.R
import com.example.screentimer.data.WeekStat
import com.example.screentimer.databinding.WeekStatItemBinding

class WeekStatAdapter internal constructor(
    private val mListener: WeekStatItemClickListener
): ListAdapter<WeekStat, WeekStatAdapter.WeekStatViewHolder>(WeekStatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekStatViewHolder {
        return WeekStatViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: WeekStatViewHolder, position: Int) {
        val weekStat = getItem(position)
        holder.bind(weekStat, mListener)
    }

    override fun getItemCount(): Int {
        val result = super.getItemCount()
        return result
    }

    class WeekStatViewHolder(val binding: WeekStatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currentWeekStat: WeekStat, listener: WeekStatItemClickListener) {
            binding.weekStat = currentWeekStat
            binding.clickListener = listener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): WeekStatAdapter.WeekStatViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: WeekStatItemBinding = DataBindingUtil.inflate(
                    layoutInflater, R.layout.week_stat_item,
                    parent, false
                )
                return WeekStatAdapter.WeekStatViewHolder(binding)

            }
        }

    }

}

interface WeekStatItemClickListener {
    fun chooseWeekStat(weekStat: WeekStat)
}

private class WeekStatDiffCallback : DiffUtil.ItemCallback<WeekStat>() {
    override fun areItemsTheSame(oldItem: WeekStat, newItem: WeekStat): Boolean {
        return oldItem.startDate == newItem.startDate && oldItem.endDate == newItem.endDate
    }

    override fun areContentsTheSame(oldItem: WeekStat, newItem: WeekStat): Boolean {
        return oldItem.startDate == newItem.startDate && oldItem.endDate == newItem.endDate
    }
}