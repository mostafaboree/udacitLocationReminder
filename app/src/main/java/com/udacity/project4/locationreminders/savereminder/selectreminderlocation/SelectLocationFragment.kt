package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

private const val TAG = "SaveReminderFragment"
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    // Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap

    private val REQUEST_LOCATION_PERMISSION = 1001

    private val DEFAULT_ZOOM = 16f
    private val defaultLocation = LatLng(34.97974325243857, 44.709856046507827)
    private lateinit var lastKnownLocation: Location
    private lateinit var NewMarker: Marker
    /*val presmissionLuncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted:Boolean ->
        if (isGranted){
            getcurrentLocation()

        }else{
            Snackbar.make(this.requireView(),"not allowed for this permission",Snackbar.LENGTH_LONG).show()

        }

    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)



        checkDeviceLocationSettings()
        val mapFragment = childFragmentManager.findFragmentById(R.id.select_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.saveButton.setOnClickListener {
            onLocationSelected()
        }

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        setMapStyle()
        mapLongClick()
        poiClick()
        getcurrentLocation()
    }

    private fun poiClick() {
        map.setOnPoiClickListener {
            map.clear()
            NewMarker = map.addMarker(
                MarkerOptions()
                    .position(it.latLng)
                    .title(it.name)
            )
            NewMarker.showInfoWindow()
        }
    }

    private fun mapLongClick() {
        map.setOnMapLongClickListener {
            map.clear()
            val snip = String.format(Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f",
                it.latitude,
                it.longitude
            )
            NewMarker = map.addMarker(
                MarkerOptions()
                    .position(it)
                    .title("Dropped_BIN")
                    .snippet(snip)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )

            NewMarker.showInfoWindow()
        }
    }

    private fun setMapStyle() {
        try {
            map.uiSettings.isMapToolbarEnabled=true
            map.uiSettings.isZoomControlsEnabled=true
            map.uiSettings.isCompassEnabled=true
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }


    private fun getcurrentLocation(){
        val foucedLocationprovider=LocationServices.getFusedLocationProviderClient(requireContext())
      try {
          if (isPermissionGreanted()){
              map.isMyLocationEnabled=true
              val currentLocation=foucedLocationprovider.lastLocation
              currentLocation.addOnCompleteListener(){task ->
              if (task.isSuccessful){
                  if(task.result!= null) {

                      lastKnownLocation= task.result!!
                      map.moveCamera(
                          CameraUpdateFactory.newLatLngZoom(
                              LatLng(
                                  lastKnownLocation!!.latitude,
                                  lastKnownLocation!!.longitude
                              ), DEFAULT_ZOOM.toFloat()
                          )
                      )
                  }else{
                      Log.d(TAG, "Current location is null. Using defaults.")
                      map.uiSettings.isMyLocationButtonEnabled=true
                      map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(defaultLocation.latitude,defaultLocation.longitude),DEFAULT_ZOOM.toFloat()))
                  }

              }else{
                  Log.d(TAG, "Current location is null. Using defaults.${task.exception}")

              }

              }

          }else{
              requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_LOCATION_PERMISSION)
          }

      }catch (e:SecurityException){
          Log.e("Exption : %s",e.message,e)
      }

    }

    private fun onLocationSelected() {
        if (this::NewMarker.isInitialized) {
            _viewModel.latitude.value = NewMarker.position.latitude
            _viewModel.longitude.value = NewMarker.position.longitude
            _viewModel.rSelectedLocationStr.value = NewMarker.title
            findNavController().popBackStack()
        } else {
            val toast = Toast.makeText(context, resources.getString(R.string.select_location), Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    private fun checkDeviceLocationSettings(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        activity,
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    this.requireView(),
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettings()
                }.show()
            }
        }
    }
// request permission

      override fun onRequestPermissionsResult(
          requestCode: Int,
          permissions: Array<out String>,
          grantResults: IntArray
      ) {
          super.onRequestPermissionsResult(requestCode, permissions, grantResults)
          if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
              getcurrentLocation()
          } else {
              Snackbar.make(
                  binding.selectLocationFragment,
                  R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
              ).setAction(android.R.string.ok) {
                  requestPermissions(
                      arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION
                  )
              }.show()
          }
      }

// menu for maps style
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }



    private fun isPermissionGreanted(): Boolean {
        return  requireContext()?.let {
            ContextCompat.checkSelfPermission(
                it,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    fun View.ShowSnakbar(mas:String,id:Int,action:Int){
        ShowSnakbar(mas,id,action)
    }




}
