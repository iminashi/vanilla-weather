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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*
import fi.tiko.vanillaweather.openweather.*
import kotlin.math.roundToInt

const val LOCATION_REQUEST_CODE = 333

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var hasLocationPermissions: Boolean = false

    private val userCities = mutableListOf("Tampere")

    private lateinit var weatherType: TextView
    private lateinit var locationText: TextView
    private lateinit var lastUpdatedText: TextView
    private lateinit var temperature: TextView
    private lateinit var windSpeedText: TextView
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

        weatherType = findViewById(R.id.weatherType)
        locationText = findViewById(R.id.locationText)
        lastUpdatedText = findViewById(R.id.lastUpdatedText)
        temperature = findViewById(R.id.temperature)
        windSpeedText = findViewById(R.id.windSpeed)
        icon = findViewById(R.id.weatherIcon)

        checkLocationPermissions()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun updateUI(weather: Weather) {
        val response = weather.response
        val date = epochToDate(response.dt!!)
        val weatherAttr = response.weather?.get(0)

        val updated = SimpleDateFormat("dd.M. HH.mm", Locale.getDefault()).format(date)
        lastUpdatedText.text = getString(R.string.last_updated, updated)
        locationText.text = response.name
        val temp = response.main?.temp?.roundToInt()
        val windSpeed = response.wind?.speed
        temperature.text = getString(R.string.temperature, temp)
        windSpeedText.text = getString(R.string.wind_speed, windSpeed)
        weatherType.text = "${weatherAttr?.main}"

        if (weather.icon != null) {
            icon.setImageBitmap(weather.icon)
        }
    }

    private fun updateForecasts(forecasts: List<ForecastWeather>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = ForecastAdapter(forecasts)
    }

    private  fun getWeatherForCity(cityName: String) {
        getWeatherAsync(this, APIQuery.Name(cityName)) { weather ->
            updateUI(weather)
            // Retrieve the forecasts using the location from the response
            if(weather.response.coord != null) {
                val query = APIQuery.Location(weather.response.coord.lat!!, weather.response.coord.lon!!)
                getForecastsAsync(this, query, ::updateForecasts)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocationWeather() {
        if (!hasLocationPermissions) return

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val query = APIQuery.Location(location.latitude, location.longitude)
                    getWeatherAsync(this, query, ::updateUI)
                    getForecastsAsync(this, query, ::updateForecasts)
                } else {
                    Log.d("MainActivity", "Location was null.")
                    getWeatherForCity(userCities[0])
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