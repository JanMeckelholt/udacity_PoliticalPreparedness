package com.example.android.politicalpreparedness.representative.adapter

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.politicalpreparedness.R

import timber.log.Timber


@BindingAdapter("profileImage")
fun fetchImage(imageView: ImageView, src: String?) {
    Timber.i("fetching img: $src")
    src?.let {
        imageView.contentDescription =
            imageView.context.getString(R.string.representativeProfilePicture)
        val imgUri = src
            .toUri()
            .buildUpon()
            .scheme("https")
            .build()
        Timber.i("imageuri: $imgUri")

        Glide
            .with(imageView.context)
            .load(imgUri).apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_profile)
                    .circleCrop()
            )
            .into(imageView)

    }
}
