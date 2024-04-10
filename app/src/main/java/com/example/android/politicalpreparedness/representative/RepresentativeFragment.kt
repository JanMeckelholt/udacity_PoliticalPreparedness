package com.example.android.politicalpreparedness.representative


import android.Manifest
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
import android.widget.ArrayAdapter
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
import com.example.android.politicalpreparedness.elections.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.elections.adapter.ElectionListener
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import com.example.android.politicalpreparedness.representative.model.Address
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.Locale


class RepresentativeFragment : Fragment() {

    companion object {
        //TODO: Add Constant for Location request
    }

    private val _viewModel: RepresentativeViewModel by inject()
    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layoutId = R.layout.fragment_representative
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.viewModel = _viewModel
        binding.lifecycleOwner = this


        binding.rvRepresentatives.adapter = RepresentativeListAdapter(RepresentativeListener { Timber.i("representative clicked ${it.name}") })

        binding.btnFindMyRepresentative.setOnClickListener {
            _viewModel.searchMyRepresentatives()
        }

        _viewModel.snackbarText.observe(viewLifecycleOwner, Observer {
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
                _viewModel.doneShowingSnackBar()
            }
        })

        //TODO: Define and assign Representative adapter

        //TODO: Populate Representative adapter

        //TODO: Establish button listeners for field and location search
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

    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
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
            _viewModel.setLocationIsEnabled(true)
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
                _viewModel.setLocationIsEnabled(true)
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
            requestFineLocationPermissions(requireContext(), permissionLauncher)
        } else {
            Timber.i("mylocation is enabled")
            _viewModel.setLocationPermissionIsGranted(true)
        }
    }

    private fun requestFineLocationPermissions(
        context: Context,
        requestPermissionLauncher: ActivityResultLauncher<String>
    ) {
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Timber.i("launching Permission request for Post Notification")
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

}

private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
private const val REQUEST_LOCATION_PERMISSION = 1
