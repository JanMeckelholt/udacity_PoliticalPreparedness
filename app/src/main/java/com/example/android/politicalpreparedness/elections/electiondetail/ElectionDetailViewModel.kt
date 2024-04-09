package com.example.android.politicalpreparedness.elections.electiondetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.elections.CivicApiStatus
import com.example.android.politicalpreparedness.elections.model.Election
import com.example.android.politicalpreparedness.network.CivicsApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

enum class CivicApiStatus { LOADING, ERROR, DONE }
class ElectionDetailViewModel(private val dataSource: ElectionDao) : ViewModel() {

    private val _status = MutableLiveData<CivicApiStatus?>()
    val status: LiveData<CivicApiStatus?>
        get() = _status

    private val _election = MutableLiveData<Election?>()

    val election: LiveData<Election?>
        get() = _election

    fun setElection(election: Election) {
        _election.value = election
        getDataFromCivic(election)
    }

    fun doneShowingSnackBar() {
        _status.value = CivicApiStatus.DONE
    }

    init {


    }

    //TODO: Add live data to hold voter info

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

    private fun getDataFromCivic(election: Election) {
        Timber.i("getting Data from Civic")
        viewModelScope.launch {
            _status.value = CivicApiStatus.LOADING
            _status.value = getElectionDetails(election)
        }
    }

    private suspend fun getElectionDetails(election: Election): CivicApiStatus {
        try {
            val electionId = election.id.toLong()
            Timber.i("electionId: $electionId - election: ${election}")
            val electionDetailResponse =
                CivicsApi.retrofitService.getVoterInfoForElection(electionId = electionId, address = election.division.state)
            Timber.i("Details for election $electionId: state: ${electionDetailResponse.state} polling: ${electionDetailResponse.pollingLocations}")

            return CivicApiStatus.DONE
        } catch (e: Exception) {
            Timber.e("Failure getting election details: ${e.message} - $e")
            return CivicApiStatus.ERROR
        }
    }

}