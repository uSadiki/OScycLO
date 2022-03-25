package com.example.sykkelapp.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sykkelapp.R
import com.example.sykkelapp.databinding.FragmentHomeBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import org.json.JSONObject

class HomeFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView : MapView
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = root.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync { map ->
            initWeatherForecast(homeViewModel)
            initMap(map,homeViewModel)
            initAirQuality(map,homeViewModel)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initWeatherForecast(viewModel: HomeViewModel) {
        val imageView = binding.weatherIcon
        val tempView = binding.temperature
        val windView = binding.windSpeed
        val uvView = binding.uvIcon
        val uvTextView = binding.uvText
        val windRotation = binding.windDirection
        viewModel.data.observe(viewLifecycleOwner) {
            val id = resources.getIdentifier(it.next_1_hours.summary.symbol_code,"drawable",context?.packageName)
            imageView.setImageResource(id)
            tempView.text = it.instant.details.air_temperature.toString() + "°"
            windView.text = it.instant.details.wind_speed.toString() + "m/s"
            DrawableCompat.setTint(uvView.drawable,uvColor(it.instant.details.ultraviolet_index_clear_sky))
            uvTextView.text = it.instant.details.ultraviolet_index_clear_sky.toString()
            println(it.instant.details.wind_from_direction)
            windRotation.animate().rotationBy(it.instant.details.wind_from_direction.toFloat()).start()
        }
    }

    private fun initAirQuality(mMap: GoogleMap,viewModel: HomeViewModel) {
        viewModel.air.observe(viewLifecycleOwner) { list ->
            list.forEach {
                val airqualityIcon: BitmapDescriptor by lazy {
                    val color = Color.parseColor("#"+it.color)
                    BitmapHelper.vectorToBitmap(context, R.drawable.ic_baseline_eco_24, color)
                }

                val point = LatLng(it.latitude,it.longitude)
                mMap.addMarker(MarkerOptions()
                    .position(point)
                    .title(it.station)
                    .snippet("Svevestøvnivå: "+it.value + it.unit)
                    .icon(airqualityIcon)
                )
            }
        }
    }

    private fun initMap(mMap : GoogleMap, viewModel: HomeViewModel) {
        // Add a marker in Oslo and move the camera
        val ojd = LatLng(59.94410, 10.7185)
        mMap.addMarker(MarkerOptions().position(ojd).title("Marker at OJD"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ojd))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ojd,15f))
        var layer : GeoJsonLayer
        viewModel.geo.observe(viewLifecycleOwner) {
                geo -> layer = GeoJsonLayer(mMap, JSONObject(geo))
            val layer_style = layer.defaultLineStringStyle
            layer_style.isClickable = true
            layer.setOnFeatureClickListener {
                Toast.makeText(context, it.getProperty("rute"), Toast.LENGTH_SHORT).show()
            }
            uniqueColor(layer)
            layer.addLayerToMap()
        }
    }

    private fun uniqueColor(layer: GeoJsonLayer) {
        val colors = listOf<Int>(Color.BLUE,Color.BLACK,Color.RED,Color.GREEN,
            Color.YELLOW,Color.GRAY,Color.LTGRAY,
            Color.rgb(255, 128, 0),Color.rgb(128, 0, 0))

        layer.features.forEach {
            val color : Int
            val route = it.getProperty("rute")
            val lineStringStyle = GeoJsonLineStringStyle()
            color = if (route != null) {
                val routeNum = route.toInt()
                if (routeNum < colors.size) {
                    colors[routeNum]
                }
                else {
                    Color.RED
                }
            } else {
                Color.MAGENTA
            }
            lineStringStyle.color = color
            it.lineStringStyle = lineStringStyle
        }
        layer.addLayerToMap()
    }

    private fun uvColor(uvIndex : Double) : Int {
        return if (uvIndex < 3) {
            Color.GREEN
        } else if (uvIndex >= 3 && uvIndex < 6) {
            Color.YELLOW
        } else if (uvIndex >= 6 && uvIndex < 8) {
            Color.rgb(255, 128, 0)
        } else if (uvIndex >= 8 && uvIndex < 11) {
            Color.RED
        } else {
            Color.MAGENTA
        }
    }
}