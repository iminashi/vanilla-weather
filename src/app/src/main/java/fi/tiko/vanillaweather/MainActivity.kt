package fi.tiko.vanillaweather

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.DateFormat
import java.util.*
import kotlin.math.roundToInt

const val LOCATION_REQUEST_CODE = 333

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var hasLocationPermissions: Boolean = false

    private val userCities = mutableListOf("Tampere")

    private lateinit var text: TextView
    private lateinit var locationText: TextView
    private lateinit var lastUpdatedText: TextView
    private lateinit var temperature: TextView
    private lateinit var icon: ImageView

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission from the user
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        } else {
            hasLocationPermissions = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text = findViewById(R.id.text)
        locationText = findViewById(R.id.locationText)
        lastUpdatedText = findViewById(R.id.lastUpdatedText)
        temperature = findViewById(R.id.temperature)
        icon = findViewById(R.id.weatherIcon)

        checkLocationPermissions()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun updateUI(weather: Weather) {
        val response = weather.response
        val date = Date(response.dt!! * 1000)
        val weatherAttr = response.weather?.get(0)

        lastUpdatedText.text = "Updated: ${DateFormat.getInstance().format(date)}"
        locationText.text = response.name
        val temp = response.main?.temp?.roundToInt()
        temperature.text = "$temp °C"
        text.text = "${weatherAttr?.main}"

        if (weather.icon != null) {
            icon.setImageBitmap(weather.icon)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocationWeather() {
        if (!hasLocationPermissions) return

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    getWeatherAsync(
                        this,
                        APILocation.Location(location.latitude, location.longitude),
                        ::updateUI
                    )
                } else {
                    Log.d("MainActivity", "Location was null.")
                    getWeatherAsync(this, APILocation.Name(userCities[0]), ::updateUI)
                }
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Getting location failed.")
            }
    }

    override fun onResume() {
        super.onResume()

        getUserLocationWeather()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                hasLocationPermissions = (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getUserLocationWeather()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}