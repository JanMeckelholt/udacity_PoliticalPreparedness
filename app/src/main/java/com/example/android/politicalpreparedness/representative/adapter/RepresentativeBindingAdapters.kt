package com.example.android.politicalpreparedness.representative.adapter

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R
import com.squareup.picasso.Picasso
import timber.log.Timber


@BindingAdapter("profileImage")
fun fetchImage(imageView: ImageView, src: String?) {
    Timber.i("fetching img: $src")
    src?.let {
        imageView.contentDescription = imageView.context.getString(R.string.representativeProfilePicture)
        val imgUri = src
            .toUri()
            .buildUpon()
            .scheme("https")
            .build()
        Picasso.with(imageView.context)
            .load(imgUri)
            .placeholder(R.drawable.ic_profile)
            .into(imageView)
    }
}
