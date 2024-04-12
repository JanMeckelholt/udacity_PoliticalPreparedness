package com.example.android.politicalpreparedness.representative.model

import android.os.Parcelable
import com.example.android.politicalpreparedness.getFacebookUrl
import com.example.android.politicalpreparedness.getTwitterUrl
import java.util.UUID
import kotlinx.parcelize.Parcelize

data class Representative (
        val official: Official,
        val office: Office
)

fun Representative.toStoreabel() : StoreableRepresentative{
        return StoreableRepresentative(
                id = UUID.randomUUID().toString(),
                name = official.name,
                party = official.party,
                photoUrl = official.photoUrl,
                www = official.urls?.first(),
                facebook = official.channels?.let { getFacebookUrl(it) },
                twitter = official.channels?.let { getTwitterUrl(it) }
        )
}

@Parcelize
data class StoreableRepresentative (
        val id: String,
        val name :String,
        val party: String?,
        val photoUrl: String?,
        val www: String?,
        val facebook : String?,
        val twitter : String?,
) : Parcelable