package com.example.android.politicalpreparedness.elections.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ElectionListItemBinding
import com.example.android.politicalpreparedness.elections.model.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
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
    textView.text = electionDay.toString()
}

@BindingAdapter("electionListData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Election>?) {
    val adapter = recyclerView.adapter as ElectionListAdapter
    adapter.submitList(data)
}

@BindingAdapter("electionDetailCorrespondenceAddress")
fun bindTextViewToElectionDetailCorrespondenceAddress(textView: TextView, electionDetailResponse: VoterInfoResponse?) {
    if (electionDetailResponse == null ||  electionDetailResponse.state.isNullOrEmpty()){
        textView.text = ""
        textView.visibility = View.GONE
        return
    }
    val administrationBody = electionDetailResponse.state[0].electionAdministrationBody
    if (administrationBody.correspondenceAddress == null) {
        textView.text = ""
        textView.visibility = View.GONE
        return
    }
    val line2InclLineBreak = if (administrationBody.correspondenceAddress.line2.isNullOrEmpty()){
        ""
    } else {
        administrationBody.correspondenceAddress.line2 + "\n"
    }

    "${administrationBody.correspondenceAddress.line1}\n${line2InclLineBreak}${administrationBody.correspondenceAddress.zip} ${administrationBody.correspondenceAddress.city}\n${administrationBody.correspondenceAddress.state}".also { textView.text = it }
    textView.visibility = View.VISIBLE
}

@BindingAdapter("electionDetailVotingLocations")
fun bindTextViewToElectionDetailVotingLocations(textView: TextView, electionDetailResponse: VoterInfoResponse?) {
    if (electionDetailResponse == null ||  electionDetailResponse.state.isNullOrEmpty()){
        textView.text = ""
        textView.visibility = View.GONE
        return
    }
    val administrationBody = electionDetailResponse.state[0].electionAdministrationBody
    textView.text = textView.context.getString(R.string.voting_locations)
    textView.visibility = View.VISIBLE
    textView.setOnClickListener {
        val url = administrationBody.votingLocationFinderUrl
        val i = Intent(Intent.ACTION_VIEW)
        i.setData(Uri.parse(url))
        textView.context.startActivity(i)
    }
}

@BindingAdapter("electionDetailBallotInformation")
fun bindTextViewToElectionDetailBallotInformation(textView: TextView, electionDetailResponse: VoterInfoResponse?) {
    if (electionDetailResponse == null ||  electionDetailResponse.state.isNullOrEmpty()){
        textView.text = ""
        textView.visibility = View.GONE
        return
    }
    val administrationBody = electionDetailResponse.state[0].electionAdministrationBody
    textView.text = textView.context.getString(R.string.ballot_information)
    textView.visibility = View.VISIBLE
    textView.setOnClickListener {
        val url = administrationBody.ballotInfoUrl
        val i = Intent(Intent.ACTION_VIEW)
        i.setData(Uri.parse(url))
        textView.context.startActivity(i)
    }
}