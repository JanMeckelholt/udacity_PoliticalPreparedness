package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RepresentativeViewModel: ViewModel() {

    private var _locationIsEnabled = MutableLiveData<Boolean?>()
    val locationIsEnabled: LiveData<Boolean?>
        get() = _locationIsEnabled

    fun setLocationIsEnabled(value : Boolean){
        _locationIsEnabled.value = value
    }

    private var _locationPermissionIsGranted = MutableLiveData<Boolean?>()
    val locationPermissionIsGranted: LiveData<Boolean?>
        get() = _locationPermissionIsGranted

    fun setLocationPermissionIsGranted(value : Boolean){
        _locationPermissionIsGranted.value = value
    }

    init {
        _locationPermissionIsGranted.value = null
        _locationIsEnabled.value = null
    }

    //TODO: Establish live data for representatives and address

    //TODO: Create function to fetch representatives from API from a provided address

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields

}
