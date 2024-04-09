package com.example.android.politicalpreparedness.elections.electiondetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.elections.model.Election

class ElectionDetailViewModel(private val dataSource: ElectionDao) : ViewModel() {

    private val _election = MutableLiveData<Election?>()

    val election: LiveData<Election?>
        get() = _election

    fun setElection(election: Election){
        _election.value = election
    }

    //TODO: Add live data to hold voter info

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}