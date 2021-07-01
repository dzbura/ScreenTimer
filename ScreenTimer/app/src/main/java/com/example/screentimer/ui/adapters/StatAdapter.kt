package com.example.screentimer.ui.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.screentimer.R
import com.example.screentimer.data.Stat
import com.example.screentimer.databinding.StatItemBinding

class StatAdapter internal constructor(): ListAdapter<Stat, StatAdapter.StatViewHolder>(StatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        return StatViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        val stat = getItem(position)
        holder.bind(stat)
    }

    override fun getItemCount(): Int {
        val result = super.getItemCount()
        return result
    }

    class StatViewHolder(val binding: StatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currentStat: Stat) {
            binding.stat = currentStat
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): StatAdapter.StatViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: StatItemBinding = DataBindingUtil.inflate(
                    layoutInflater, R.layout.stat_item,
                    parent, false
                )
                return StatAdapter.StatViewHolder(binding)

            }
        }

    }

}

interface StatItemClickListener {
    fun chooseStat(stat: Stat)
}

private class StatDiffCallback : DiffUtil.ItemCallback<Stat>() {
    override fun areItemsTheSame(oldItem: Stat, newItem: Stat): Boolean {
        return oldItem.totalTime == newItem.totalTime && oldItem.packageName == newItem.packageName
    }

    override fun areContentsTheSame(oldItem: Stat, newItem: Stat): Boolean {
        return oldItem.totalTime == newItem.totalTime && oldItem.packageName == newItem.packageName
    }
}