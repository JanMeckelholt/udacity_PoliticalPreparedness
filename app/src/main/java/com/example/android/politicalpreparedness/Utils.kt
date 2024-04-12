package com.example.android.politicalpreparedness

import com.example.android.politicalpreparedness.representative.model.Channel

fun getFacebookUrl(channels: List<Channel>): String? {
    return channels.filter { channel -> channel.type == "Facebook" }
        .map { channel -> "https://www.facebook.com/${channel.id}" }
        .firstOrNull()
}

fun getTwitterUrl(channels: List<Channel>): String? {
    return channels.filter { channel -> channel.type == "Twitter" }
        .map { channel -> "https://www.twitter.com/${channel.id}" }
        .firstOrNull()
}