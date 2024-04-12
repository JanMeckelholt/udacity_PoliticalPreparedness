package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Constants
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.getRepresentatives
import com.example.android.politicalpreparedness.representative.model.Address
import com.example.android.politicalpreparedness.representative.model.StoreableRepresentative
import com.example.android.politicalpreparedness.representative.model.toStoreabel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

const val REPRESENTATIVES = "representatives"
const val US_STATE_SELECTION = "us_state_selection"
const val ADDRESS_LINE_1 = "address_line_1"
const val ADDRESS_LINE_2 = "address_line_2"
const val ZIP = "zip"
const val CITY = "city"

class RepresentativeViewModel(application: Application, private val savedState: SavedStateHandle) :
    AndroidViewModel(application) {

    private val _statusApi = MutableLiveData<Constants.Status?>()
    val statusApi: LiveData<Constants.Status?>
        get() = _statusApi

    private val _statusGeo = MutableLiveData<Constants.Status?>()
    val statusGeo: LiveData<Constants.Status?>
        get() = _statusGeo

    private val _representatives = savedState.getLiveData<List<StoreableRepresentative>?>(REPRESENTATIVES, null)
    //private val _representatives = MutableLiveData<List<Representative>?>()
    val representatives: LiveData<List<StoreableRepresentative>?>
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

    fun setStatusGeoLoading() {
        _statusGeo.value = Constants.Status.LOADING
    }

    fun setStatusGeoDone() {
        _statusGeo.value = Constants.Status.DONE
    }

    fun setStatusGeoError() {
        _statusGeo.value = Constants.Status.ERROR
    }

    val usStates: List<String> = Constants.usStates.values.toList()
    var selectedUsState = savedState.getLiveData(US_STATE_SELECTION, usStates[0])

    fun saveLiveData() {
        Timber.i("saving Livedata")
        savedState[US_STATE_SELECTION] = selectedUsState.value
        savedState[ADDRESS_LINE_1] = addressLine1.value
        savedState[ADDRESS_LINE_2] = addressLine2.value
        savedState[CITY] = city.value
        savedState[ZIP] = zip.value
    }


    private var _snackbarText = MutableLiveData<String?>()
    val snackbarErrText: LiveData<String?>
        get() = _snackbarText

    var addressLine1 = savedState.getLiveData(ADDRESS_LINE_1, "")
    var addressLine2 = savedState.getLiveData(ADDRESS_LINE_2, "")
    var city = savedState.getLiveData(CITY, "")
    var zip = savedState.getLiveData(ZIP, "")

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
            Timber.i("Success! Got representatives Data from Civic\n $representativesResponse")
            val rep = representativesResponse.getRepresentatives().map { it.toStoreabel() }
            _representatives.value = rep

            Timber.i("Success! Got representatives Data from Civic Representatives\n $rep")
            return Constants.Status.DONE
        } catch (e: Exception) {
            Timber.e("Failure getting representatives: ${e.message} - $e")
            return Constants.Status.ERROR
        }
    }
}
