package com.example.android.politicalpreparedness.network.models

import com.example.android.politicalpreparedness.representative.model.Office
import com.example.android.politicalpreparedness.representative.model.Official

data class RepresentativeResponse(
        val offices: List<Office>,
        val officials: List<Official>
)