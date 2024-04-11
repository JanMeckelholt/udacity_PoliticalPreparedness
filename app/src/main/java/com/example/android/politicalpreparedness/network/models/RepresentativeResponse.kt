package com.example.android.politicalpreparedness.network.models

import com.example.android.politicalpreparedness.representative.model.Office
import com.example.android.politicalpreparedness.representative.model.Official
import com.example.android.politicalpreparedness.representative.model.Representative

data class RepresentativeResponse(
        val offices: List<Office>,
        val officials: List<Official>
)

fun RepresentativeResponse.getRepresentatives(): List<Representative> {
        val reps : MutableList<Representative> = arrayListOf()
        for (office in offices) {
                reps.addAll(office.getRepresentatives(officials))
        }
        return reps
}