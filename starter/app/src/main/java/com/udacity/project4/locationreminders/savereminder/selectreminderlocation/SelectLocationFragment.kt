    package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragmentDirections
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.LocationRequester
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment() {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()

    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var locationRequester: LocationRequester

    private lateinit var _map: GoogleMap

    // Create only one marker
    private var _marker: Marker? = null
        set(value) {
            field = value
            _viewModel.setLatLng(field!!.position)
        }

    private val REQUEST_LOCATION_PERMISSION = 1

    private val callback = OnMapReadyCallback { googleMap ->
        _map = googleMap

        setMapLongClick(_map)

        enableMyLocation(requireContext(), requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationRequester = LocationRequester(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        _viewModel.selectedPOI.observe(viewLifecycleOwner, Observer {
            findNavController().popBackStack()
        })

        return binding.root
    }

    private fun isPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation(requireContext(), requireActivity())
            }
        }
    }

    private fun enableMyLocation(context: Context, activity: Activity) {
        if (isPermissionGranted(context)) {
            @SuppressLint("MissingPermission") // The mission permission is being dealt above
            _map.isMyLocationEnabled = true

            _map.setOnMyLocationButtonClickListener {
                locationRequester.checkDeviceLocationSettings(this, true, {
                    locationRequester.getLastLocation {
                        _map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(it.latitude, it.longitude),
                                15.0f
                            )
                        )
                    }
                }, {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.location_required_error),
                        Toast.LENGTH_LONG
                    ).show()
                })
                true
            }
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


    private fun addMarker(map: GoogleMap, latLng: LatLng, snippet: String) {
        _marker = map.addMarker(
            MarkerOptions().position(latLng)
                .title(getString(R.string.your_location))
                .snippet(snippet)
        )
    }

    private fun setMapLongClick(map: GoogleMap) {
        // Add a Poi Click listener into the map
        map.setOnPoiClickListener {
            val latLng = it.latLng

            // Create a maker if is null, update it's position if is not, this will maintain only one marker
            if (_marker == null)
                addMarker(map, latLng, it.name)
            else _marker!!.position = latLng
        }

        // Add a click listener in the map
        map.setOnMapClickListener {
            _marker?.remove()

            addMarker(map, it, getString(R.string.your_location))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            _map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            _map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            _map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            _map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}