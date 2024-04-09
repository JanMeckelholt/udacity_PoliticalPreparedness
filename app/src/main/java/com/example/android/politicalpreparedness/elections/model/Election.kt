package com.example.android.politicalpreparedness.elections.model

import android.os.Parcelable
import androidx.room.*
import com.example.android.politicalpreparedness.representative.model.Division
import com.squareup.moshi.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "election_table")
data class Election(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "name")val name: String,
        //@ColumnInfo(name = "electionDay")val electionDay: String,
        @ColumnInfo(name = "electionDay")val electionDay: Date,
        //@ColumnInfo(name="ocdDivisionId")val division: String
        @Embedded(prefix = "division_") @Json(name="ocdDivisionId") val division: Division
) : Parcelable