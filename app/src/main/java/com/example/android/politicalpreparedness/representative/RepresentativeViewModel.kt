package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Constants
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.representative.model.Address
import com.example.android.politicalpreparedness.representative.model.Official
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class RepresentativeViewModel(application: Application) : AndroidViewModel(application) {

    private val _status = MutableLiveData<Constants.CivicApiStatus?>()
    val status: LiveData<Constants.CivicApiStatus?>
        get() = _status

    private val _representatives = MutableLiveData<List<Official>?>()
    val representatives: LiveData<List<Official>?>
        get() = _representatives

    private var _locationIsEnabled = MutableLiveData<Boolean?>()
    val locationIsEnabled: LiveData<Boolean?>
        get() = _locationIsEnabled

    fun setLocationIsEnabled(value: Boolean) {
        _locationIsEnabled.value = value
    }

    private var _locationPermissionIsGranted = MutableLiveData<Boolean?>()
    val locationPermissionIsGranted: LiveData<Boolean?>
        get() = _locationPermissionIsGranted

    fun setLocationPermissionIsGranted(value: Boolean) {
        _locationPermissionIsGranted.value = value
    }

    val usStates: List<String> = Constants.usStates.values.toList()
    var selectedUsState = MutableLiveData(usStates[0])

    private var _snackbarText = MutableLiveData<String?>()
    val snackbarText: LiveData<String?>
        get() = _snackbarText


    var addressLine1 = MutableLiveData<String>()
    var addressLine2 = MutableLiveData<String?>()
    var city = MutableLiveData<String?>()
    var zip = MutableLiveData<String?>()

//    private var _address = MutableLiveData<Address?>()
//    val address: LiveData<Address?>
//        get() = Address(
//            _addressLine1.value,
//            addressLine2.value,
//            _city.value,
//            selectedUsState.value,
//            _zip.value
//        )


    init {
        _locationPermissionIsGranted.value = null
        _locationIsEnabled.value = null
    }

    private fun addressIsValid(): Boolean {
        Timber.i("checking 1: ${addressLine1.value}, city: ${city.value}, zip: ${zip.value}")
        if (addressLine1.value.isNullOrBlank()) return false
        if (city.value.isNullOrBlank()) return false
        if (zip.value.isNullOrBlank()) return false
        return true
    }

    fun searchMyRepresentatives() {
        if (!addressIsValid()) {
            _snackbarText.value =
                getApplication<Application>().resources.getString(R.string.errTxtAddressInvalid)
            return
        }
        getDataFromCivic()

    }

    fun doneShowingSnackBar() {
        _snackbarText.value = null
    }

    private fun getDataFromCivic() {
        Timber.i("getting Data from Civic")
        viewModelScope.launch {
            _status.value = Constants.CivicApiStatus.LOADING
            _status.value = getRepresentatives()
        }
    }

    private suspend fun getRepresentatives(): Constants.CivicApiStatus {
        try {
            val address = Address(
                addressLine1.value!!,
                addressLine2.value,
                city.value!!,
                selectedUsState.value!!,
                zip.value!!
            )
            val representativesResponse =
                CivicsApi.retrofitService.getRepresentatives(address = address.toFormattedString())
            _representatives.value = representativesResponse.officials
            Timber.i("Success! Got Data from Civic")
            return Constants.CivicApiStatus.DONE
        } catch (e: Exception) {
            Timber.e("Failure getting representatives: ${e.message} - $e")
            return Constants.CivicApiStatus.ERROR
        }
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
