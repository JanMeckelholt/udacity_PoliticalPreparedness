package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Constants
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.getRepresentatives
import com.example.android.politicalpreparedness.representative.model.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class RepresentativeViewModel(application: Application) : AndroidViewModel(application) {

    private val _statusApi = MutableLiveData<Constants.Status?>()
    val statusApi: LiveData<Constants.Status?>
        get() = _statusApi

    private val _statusGeo = MutableLiveData<Constants.Status?>()
    val statusGeo: LiveData<Constants.Status?>
        get() = _statusGeo

    private val _representatives = MutableLiveData<List<Representative>?>()
    val representatives: LiveData<List<Representative>?>
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

    fun setStatusGeoLoading(){
        _statusGeo.value = Constants.Status.LOADING
    }
    fun setStatusGeoDone(){
        _statusGeo.value = Constants.Status.DONE
    }
    fun setStatusGeoError(){
        _statusGeo.value = Constants.Status.ERROR
    }

    val usStates: List<String> = Constants.usStates.values.toList()
    var selectedUsState = MutableLiveData(usStates[0])

    private var _snackbarText = MutableLiveData<String?>()
    val snackbarErrText: LiveData<String?>
        get() = _snackbarText

    var addressLine1 = MutableLiveData<String>()
    var addressLine2 = MutableLiveData<String?>()
    var city = MutableLiveData<String?>()
    var zip = MutableLiveData<String?>()

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
            _statusApi.value = Constants.Status.LOADING
            _statusApi.value = getRepresentatives()
        }
    }

    private suspend fun getRepresentatives(): Constants.Status {
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
            _representatives.value = representativesResponse.getRepresentatives()
            Timber.i("Success! Got representatives Data from Civic\n $representativesResponse")
            Timber.i("Success! Got representatives Data from Civic Representatives\n ${representativesResponse.getRepresentatives()}")
            return Constants.Status.DONE
        } catch (e: Exception) {
            Timber.e("Failure getting representatives: ${e.message} - $e")
            return Constants.Status.ERROR
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
