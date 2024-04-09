package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.network.jsonadapter.DateAdapter
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://www.googleapis.com/civicinfo/v2/"
private const val ELECTIONS_PATH = "elections"
private const val REPRESENTATIVES_PATH = "representatives"
private const val VOTERINFO_PATH = "voterinfo"

// TODO: Add adapters for Java Date and custom adapter ElectionAdapter (included in project)
private val moshi = Moshi.Builder()
    .add(DateAdapter())
    .add(ElectionAdapter())
    .add(KotlinJsonAdapterFactory())
    .build()


private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(CivicsHttpClient.getClient())
    .baseUrl(BASE_URL)
    .build()

/**
 *  Documentation for the Google Civics API Service can be found at https://developers.google.com/civic-information/docs/v2
 */

interface CivicsApiService {

    @GET(ELECTIONS_PATH)
    suspend fun getElections(): ElectionResponse

    @GET(VOTERINFO_PATH)
    suspend fun getVoterInfoForAddress(
        @Query("address") address: String,
    ): VoterInfoResponse

    @GET(VOTERINFO_PATH)
    suspend fun getVoterInfoForElection(
        @Query("electionId") electionId: Long,
        @Query("address") address: String,
    ): VoterInfoResponse

    @GET(REPRESENTATIVES_PATH)
    suspend fun getRepresentatives(
        @Query("address") address: String,
    ): RepresentativeResponse

}

object CivicsApi {
    val retrofitService: CivicsApiService by lazy {
        retrofit.create(CivicsApiService::class.java)
    }
}