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
import com.example.android.politicalpreparedness.getFacebookUrl
import com.example.android.politicalpreparedness.getTwitterUrl
import com.example.android.politicalpreparedness.representative.model.Channel
import com.example.android.politicalpreparedness.representative.model.StoreableRepresentative

class RepresentativeListAdapter(private val representativeListener: RepresentativeListener): ListAdapter<StoreableRepresentative, RepresentativeViewHolder>(RepresentativeViewHolder.DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        return RepresentativeViewHolder(RepresentativeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class RepresentativeViewHolder(val binding: RepresentativeListItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: StoreableRepresentative) {
        binding.representative = item
        binding.representativePhoto.setImageResource(R.drawable.ic_profile)
        item.facebook?.let { enableLink(binding.facebookIcon, it) }
        item.twitter?.let { enableLink(binding.twitterIcon, it) }
        item.www?.let { enableLink(binding.wwwIcon, it) }
        binding.executePendingBindings()
    }

    private fun showSocialLinks(channels: List<Channel>) {
        val facebookUrl = getFacebookUrl(channels)
        if (!facebookUrl.isNullOrBlank()) { enableLink(binding.facebookIcon, facebookUrl) }

        val twitterUrl = getTwitterUrl(channels)
        if (!twitterUrl.isNullOrBlank()) { enableLink(binding.twitterIcon, twitterUrl) }
    }

    private fun showWWWLinks(urls: List<String>) {
        enableLink(binding.wwwIcon, urls.first())
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
    companion object DiffCallback : DiffUtil.ItemCallback<StoreableRepresentative>() {
        override fun areItemsTheSame(oldItem: StoreableRepresentative, newItem: StoreableRepresentative): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StoreableRepresentative, newItem: StoreableRepresentative): Boolean {
            return oldItem == newItem
        }

    }

}


class RepresentativeListener(val clickListener: (representative: StoreableRepresentative) -> Unit) {
    fun onClick(representative: StoreableRepresentative) = clickListener(representative)
}

@BindingAdapter("representativesListData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<StoreableRepresentative>?) {
    val adapter = recyclerView.adapter as RepresentativeListAdapter
    adapter.submitList(data)
}