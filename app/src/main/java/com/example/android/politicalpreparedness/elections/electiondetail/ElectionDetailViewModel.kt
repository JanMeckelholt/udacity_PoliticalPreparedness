package com.example.android.politicalpreparedness.elections.electiondetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Constants
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.elections.model.Election
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import timber.log.Timber
import java.lang.Exception


class ElectionDetailViewModel(private val dataSource: ElectionDao) : ViewModel() {


    private val _status = MutableLiveData<Constants.Status?>()
    val status: LiveData<Constants.Status?>
        get() = _status

    private val _election = MutableLiveData<Election?>()

    val election: LiveData<Election?>
        get() = _election

    private val _electionDetail = MutableLiveData<VoterInfoResponse?>()

    val electionDetail: LiveData<VoterInfoResponse?>
        get() = _electionDetail

    private val _isFollowed = MutableLiveData<Boolean?>()
    val isFollowed: LiveData<Boolean?>
        get() = _isFollowed

    fun setElection(election: Election) {
        _election.value = election
        getDataFromCivic(election)
    }

    fun doneShowingSnackBar() {
        _status.value = Constants.Status.DONE
    }

    fun onFollowOrUnfollowClicked() {
        if (isFollowed.value == null) {
            Timber.e("isSaved was null!")
            return
        }
        if (isFollowed.value!!) {
            viewModelScope.launch {
                unfollowElection()
            }
        } else {
            viewModelScope.launch {
                followElection()
            }
        }

    }

    private suspend fun followElection() {
        election.value?.let {
            withContext(Dispatchers.IO) {
                dataSource.insertAll(it)
            }
            _isFollowed.value = true
        }

    }

    private suspend fun unfollowElection() {
        election.value?.let {
            withContext(Dispatchers.IO) {
                dataSource.delete(it)
            }
            _isFollowed.value = false
        }
    }

    init {
        _isFollowed.value = null
        viewModelScope.launch {
            checkIfFollowed()
        }
    }

    private suspend fun checkIfFollowed() {
        Timber.i("getting Data from Civic")
        withContext(Dispatchers.IO){
            election.value?.let {
               val savedElection =  dataSource.getElection(it.id)
                _isFollowed.postValue((savedElection != null))
            }
        }
    }

    private fun getDataFromCivic(election: Election) {
        Timber.i("getting Data from Civic")
        viewModelScope.launch {
            _status.value = Constants.Status.LOADING
            _status.value = getElectionDetails(election)
        }
    }

    private suspend fun getElectionDetails(election: Election): Constants.Status {
        try {
            val electionId = election.id.toLong()
            Timber.i("electionId: $electionId - election: ${election}")
            val electionDetailResponse =
                CivicsApi.retrofitService.getVoterInfoForElection(
                    electionId = electionId,
                    address = election.division.state
                )
            _electionDetail.value = electionDetailResponse
            Timber.i("Details for election $electionId: state: ${electionDetailResponse.state} polling: ${electionDetailResponse.pollingLocations}")

            return Constants.Status.DONE
        } catch (e: Exception) {
            Timber.e("Failure getting election details: ${e.message} - $e")
            _electionDetail.value = null
            return Constants.Status.ERROR
        }
    }

}