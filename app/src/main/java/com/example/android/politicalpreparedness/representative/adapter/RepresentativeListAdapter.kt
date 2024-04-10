package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.RepresentativeListItemBinding
import com.example.android.politicalpreparedness.elections.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.elections.model.Election

import com.example.android.politicalpreparedness.representative.model.Channel
import com.example.android.politicalpreparedness.representative.model.Official
import com.example.android.politicalpreparedness.representative.model.Representative

class RepresentativeListAdapter(private val representativeListener: RepresentativeListener): ListAdapter<Official, RepresentativeViewHolder>(RepresentativeViewHolder.DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        return RepresentativeViewHolder(RepresentativeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class RepresentativeViewHolder(val binding: RepresentativeListItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Official) {
        binding.representative = item
        binding.representativePhoto.setImageResource(R.drawable.ic_profile)

        //TODO: Show social links ** Hint: Use provided helper methods
        //TODO: Show www link ** Hint: Use provided helper methods

        binding.executePendingBindings()
    }

    //TODO: Add companion object to inflate ViewHolder (from)

    private fun showSocialLinks(channels: List<Channel>) {
        val facebookUrl = getFacebookUrl(channels)
        if (!facebookUrl.isNullOrBlank()) { enableLink(binding.facebookIcon, facebookUrl) }

        val twitterUrl = getTwitterUrl(channels)
        if (!twitterUrl.isNullOrBlank()) { enableLink(binding.twitterIcon, twitterUrl) }
    }

    private fun showWWWLinks(urls: List<String>) {
        enableLink(binding.wwwIcon, urls.first())
    }

    private fun getFacebookUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Facebook" }
                .map { channel -> "https://www.facebook.com/${channel.id}" }
                .firstOrNull()
    }

    private fun getTwitterUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Twitter" }
                .map { channel -> "https://www.twitter.com/${channel.id}" }
                .firstOrNull()
    }

    private fun enableLink(view: ImageView, url: String) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }

    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Official>() {
        override fun areItemsTheSame(oldItem: Official, newItem: Official): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Official, newItem: Official): Boolean {
            return oldItem == newItem
        }

    }

}


class RepresentativeListener(val clickListener: (representative: Official) -> Unit) {
    fun onClick(representative: Official) = clickListener(representative)
}

@BindingAdapter("representativesListData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Official>?) {
    val adapter = recyclerView.adapter as RepresentativeListAdapter
    adapter.submitList(data)
}