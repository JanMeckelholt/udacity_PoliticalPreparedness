package com.example.android.politicalpreparedness.network.models

import com.example.android.politicalpreparedness.election.model.Election
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ElectionResponse(
        val kind: String,
        val elections: List<Election>
)