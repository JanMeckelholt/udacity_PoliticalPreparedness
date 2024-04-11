package com.example.android.politicalpreparedness.representative.model

import com.squareup.moshi.Json

data class Office (
    val name: String,
    @Json(name="divisionId") val division: Division,
    @Json(name="officialIndices") val officialIndices: List<Int>
) {
    fun getRepresentatives(officials: List<Official>): List<Representative> {
        return this.officialIndices.map { index ->
            Representative(officials[index], this)
        }
    }
}
