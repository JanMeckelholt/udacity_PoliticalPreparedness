package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.android.politicalpreparedness.databinding.ElectionListItemBinding
import com.example.android.politicalpreparedness.network.models.Election
import java.util.Date

class ElectionListAdapter(private val electionListener: ElectionListener)
    : ListAdapter<Election, ElectionViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder(ElectionListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val election = getItem(position)
        holder.itemView.setOnClickListener {
            electionListener.onClick(election)
        }
        holder.bind(election)
    }


    // TODO: Add companion object to inflate ViewHolder (from)


    companion object DiffCallback : DiffUtil.ItemCallback<Election>() {
        override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.id == newItem.id
        }

    }
    }

class ElectionListener(val clickListener: (election: Election) -> Unit) {
    fun onClick(election: Election) = clickListener(election)
}


class ElectionViewHolder(private var binding: ElectionListItemBinding) : ViewHolder(binding.root) {
    fun bind(election: Election){
        binding.election = election
        binding.executePendingBindings()
    }
}


@BindingAdapter("electionDateText")
fun bindTextViewToElectionDay(textView: TextView, electionDay: Date) {
    val context = textView.context
    textView.text = electionDay.toString()
}