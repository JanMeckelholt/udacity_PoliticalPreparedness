package com.example.android.politicalpreparedness.elections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.elections.model.Election
import com.example.android.politicalpreparedness.network.CivicsApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

enum class CivicApiStatus { LOADING, ERROR, DONE }

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(private val dataSource: ElectionDao) : ViewModel() {

    private val _status = MutableLiveData<CivicApiStatus?>()
    val status: LiveData<CivicApiStatus?>
        get() = _status

    private val _elections = MutableLiveData<List<Election>?>()
    val elections: LiveData<List<Election>?>
        get() = _elections

    //private val _savedElections = MutableLiveData<List<Election>?>()
    var savedElections: LiveData<List<Election>?> = dataSource.getElectionsLiveData()


    private val _navigateToSelectedElection = MutableLiveData<Election?>()
    val navigateToSelectedElection: LiveData<Election?>
        get() = _navigateToSelectedElection

    init {
        _status.value = null
        //savedElections = dataSource.getElectionsLiveData()
        getDataFromCivic()
    }

    fun doneShowingSnackBar() {
        _status.value = CivicApiStatus.DONE
    }

    fun displayElectionDetails(election: Election) {
        _navigateToSelectedElection.value = election
    }

    fun navigationDone() {
        _navigateToSelectedElection.value = null
    }

    //TODO: Create live data val for upcoming elections

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

    private fun getDataFromCivic() {
        Timber.i("getting Data from Civic")
        viewModelScope.launch {
            _status.value = CivicApiStatus.LOADING
            _status.value = getElections()
        }
    }

    private suspend fun getElections(): CivicApiStatus {
        try {
            val electionResponse = CivicsApi.retrofitService.getElections()
            _elections.value = electionResponse.elections
            Timber.i("Success! Got Data from Civic")
            return CivicApiStatus.DONE
        } catch (e: Exception) {
            Timber.e("Failure getting elections: ${e.message} - $e")
            return CivicApiStatus.ERROR
        }
    }

}