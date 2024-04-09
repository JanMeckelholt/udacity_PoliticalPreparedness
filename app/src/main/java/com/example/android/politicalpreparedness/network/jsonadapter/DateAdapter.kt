package com.example.android.politicalpreparedness.network.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateAdapter {
    private var dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY)
    @FromJson
    fun dateFromJson(electionDay: String): Date {
        return dateFormat.parse(electionDay) ?: return dateFormat.parse("1900-01-01")!!

    }
    @ToJson
    fun dateToJson (electionDay: Date): String {
        return dateFormat.format(electionDay)
    }
}

