package com.example.android.politicalpreparedness.representative


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.Constants
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import com.example.android.politicalpreparedness.representative.model.Address
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.Locale
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener


class RepresentativeFragment : Fragment() {
    companion object {
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private val viewModel: RepresentativeViewModel by inject()
    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutId = R.layout.fragment_representative
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.rvRepresentatives.adapter =
            RepresentativeListAdapter(RepresentativeListener { Timber.i("representative clicked ${it.official.name}") })
        binding.btnFindMyRepresentative.setOnClickListener {
            viewModel.searchMyRepresentatives()
        }
        registerFineLocationRequestPermissionLauncher()
        binding.btnUseMyLocation.setOnClickListener {
            getLocation()
        }
        viewModel.snackbarErrText.observe(viewLifecycleOwner, Observer {
            it?.let {
                Snackbar
                    .make(
                        requireActivity().findViewById(android.R.id.content),
                        it,
                        Snackbar.LENGTH_LONG
                    )
                    .setBackgroundTint(resources.getColor(R.color.colorError))
                    .setTextColor(resources.getColor(R.color.colorBlack))
                    .show()
                viewModel.doneShowingSnackBar()
            }
        })
        viewModel.statusApi.observe(viewLifecycleOwner, Observer {
            if (it == Constants.Status.LOADING) {
                binding.statusApiLoadingWheel.visibility = View.VISIBLE
                binding.rvRepresentatives.visibility = View.GONE
            } else {
                if (it == Constants.Status.ERROR) {
                    Snackbar
                        .make(
                            requireActivity().findViewById(android.R.id.content),
                            getString(R.string.api_error),
                            Snackbar.LENGTH_LONG
                        )
                        .setBackgroundTint(resources.getColor(R.color.colorError))
                        .setTextColor(resources.getColor(R.color.colorBlack))
                        .show()
                    viewModel.doneShowingSnackBar()
                }
                binding.statusApiLoadingWheel.visibility = View.GONE
                binding.rvRepresentatives.visibility = View.VISIBLE
            }
        })
        viewModel.statusGeo.observe(viewLifecycleOwner, Observer {
            if (it == Constants.Status.LOADING) {
                binding.statusGeoLoadingWheel.visibility = View.VISIBLE
                binding.etAddressLine1.visibility = View.INVISIBLE
                binding.etAddressLine2.visibility = View.INVISIBLE
                binding.etZip.visibility = View.INVISIBLE
                binding.etCity.visibility = View.INVISIBLE
                binding.spinnerState.visibility = View.INVISIBLE
            } else {
                if (it == Constants.Status.ERROR) {
                    Snackbar
                        .make(
                            requireActivity().findViewById(android.R.id.content),
                            getString(R.string.errTxtGetLocationFailed),
                            Snackbar.LENGTH_LONG
                        )
                        .setBackgroundTint(resources.getColor(R.color.colorError))
                        .setTextColor(resources.getColor(R.color.colorBlack))
                        .show()
                    viewModel.doneShowingSnackBar()
                }
                binding.statusGeoLoadingWheel.visibility = View.GONE
                binding.etAddressLine1.visibility = View.VISIBLE
                binding.etAddressLine2.visibility = View.VISIBLE
                binding.etZip.visibility = View.VISIBLE
                binding.etCity.visibility = View.VISIBLE
                binding.spinnerState.visibility = View.VISIBLE
            }
        })
        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setLocationPermission()
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (viewModel.locationIsEnabled.value != true) {
            checkDeviceLocationEnabled()
            return
        }
        if (viewModel.locationPermissionIsGranted.value != true) {
            setLocationPermission()
            return
        }
        viewModel.setStatusGeoLoading()
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? ->
                currentLocation = location
                location?.let {
                    val address = geoCodeLocation(it)
                    viewModel.addressLine1.value = address.line1
                    viewModel.addressLine2.value = address.line2
                    viewModel.zip.value = address.zip
                    viewModel.city.value = address.city
                    viewModel.selectedUsState.value = address.state
                    viewModel.setStatusGeoDone()
                    viewModel.searchMyRepresentatives()
                    hideKeyboard()

                }
            }
            .addOnFailureListener { e ->
                Timber.e("failed getting location: ${e.message}")
                viewModel.setStatusGeoError()
            }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            ?.map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            ?.first() ?: Address("not found", null, "not found", "not found", "not found")
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }


    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private fun checkDeviceLocationEnabled(resolve: Boolean = true) {
        Timber.i("checkDeviceLocation")
        if (isLocationEnabled(requireContext())) {
            viewModel.setLocationIsEnabled(true)
        }
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(500)
            .setMaxUpdateDelayMillis(1000)
            .build()
        val locationSettingsRequest =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnFailureListener { e ->
                Timber.i("onFailure addLocationRequest ${e.message} - $e")
                when {
                    e is ResolvableApiException && resolve -> {
                        try {
                            e.startResolutionForResult(
                                requireActivity(), REQUEST_TURN_DEVICE_LOCATION_ON
                            )
                        } catch (sendEx: IntentSender.SendIntentException) {
                            Timber.e("Error getting location settings resolution: ${sendEx.message}")
                        }
                    }

                    e is ApiException && e.statusCode == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        AlertDialog.Builder(requireContext()).setTitle(R.string.settings_error)
                            .setMessage(R.string.location_required_error)
                            .setPositiveButton(R.string.settings) { _, _ ->
                                startActivity(Intent().apply {
                                    action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                })
                            }.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                                dialog.dismiss()
                            }.setIcon(android.R.drawable.ic_dialog_info).show()
                    }

                    else -> {
                        Timber.e("Location services have to be activated")
                        Snackbar.make(
                            binding.root,
                            R.string.location_required_error,
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(R.string.settings) {
                            startActivity(Intent().apply {
                                action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }.show()
                    }

                }
            }
            .addOnSuccessListener {
                Timber.i("Successfully activated location")
                viewModel.setLocationIsEnabled(true)
                getLocation()
            }
    }


    private fun registerFineLocationRequestPermissionLauncher() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                when (granted) {
                    true -> {
                        setLocationPermission()
                    }
                    false -> {
                        Snackbar.make(
                            binding.root,
                            R.string.foreground_location_permission_required,
                            Snackbar.LENGTH_INDEFINITE
                        )
                            .setAction(R.string.settings) {
                                startActivity(Intent().apply {
                                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    data =
                                        Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                })
                            }
                            .show()
                    }
                }
            }
    }

    private fun setLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            Timber.i("mylocation permission is granted")
            viewModel.setLocationPermissionIsGranted(true)
            getLocation()
        }
    }
}