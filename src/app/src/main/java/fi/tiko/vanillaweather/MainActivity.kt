package fi.tiko.vanillaweather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
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

    private var userCities = mutableListOf("Tampere", "Turku", "Ivalo", "London", "Kyoto", "Dallas", "New York")
    private var selectedCity = -1
    private var currentLocation: APIQuery.Location? = null

    private lateinit var weatherType: TextView
    private lateinit var locationText: TextView
    private lateinit var lastUpdatedText: TextView
    private lateinit var temperature: TextView
    private lateinit var windSpeedText: TextView
    private lateinit var icon: ImageView
    private lateinit var hourlyForecastButton: Button
    private lateinit var errorLayout: LinearLayout
    private lateinit var mainWeatherLayout: LinearLayout
    private lateinit var errorMessage: TextView
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var recyclerView: RecyclerView

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, CitiesActivity::class.java)
                intent.putExtra("cities", userCities.toTypedArray())
                intent.putExtra("selectedCity", selectedCity)
                startActivityForResult(intent, 777)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 777 && resultCode == RESULT_OK) {
            data?.extras?.getStringArray("cities")?.toMutableList()?.let {
                userCities = it
            }
            data?.extras?.getInt("selectedCity")?.let {
                Log.d("MainActivity", "Selected city: $it.")
                selectedCity = it
            }
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
        hourlyForecastButton = findViewById(R.id.buttonHourlyForecast)
        errorLayout = findViewById(R.id.errorLayout)
        mainWeatherLayout = findViewById(R.id.mainWeatherLayout)
        errorMessage = findViewById(R.id.errorMessage)
        loadingSpinner = findViewById(R.id.loadingSpinner)
        recyclerView = findViewById(R.id.recycler_view)

        checkLocationPermissions()

        savedInstanceState?.run {
            getStringArrayList("cities")?.let { userCities = it.toMutableList() }
            selectedCity = getInt("selectedCity", -1)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("selectedCity", selectedCity)
        outState.putStringArray("cities", userCities.toTypedArray())

        Log.d("MainActivity", "onSaveInstanceState")

        super.onSaveInstanceState(outState)
    }

    private fun handleError(message: String) {
        Log.d("MainActivity", "Failed: $message")
        loadingSpinner.isVisible = false
        errorLayout.isVisible = true
        mainWeatherLayout.isVisible = false
        hourlyForecastButton.isVisible = false
        recyclerView.isVisible = false
        errorMessage.text = message
    }

    private fun updateUI(weather: Weather) {
        loadingSpinner.isVisible = false
        errorLayout.isVisible = false
        mainWeatherLayout.isVisible = true
        recyclerView.isVisible = true
        hourlyForecastButton.isVisible = true

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
        recyclerView.adapter = ForecastAdapter(forecasts)
    }

    private fun weatherForCityCallback(weather: Weather) {
        updateUI(weather)

        // Retrieve the forecasts using the location from the response
        if (weather.response.coord != null) {
            val query =
                APIQuery.Location(weather.response.coord.lat!!, weather.response.coord.lon!!)
            currentLocation = query
            getForecastsAsync(this, query, ::updateForecasts, ::handleError)
        }
    }

    private fun getWeatherForCity(cityName: String) {
        getWeatherAsync(this, APIQuery.Name(cityName), ::weatherForCityCallback, ::handleError)
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocationWeather() {
        if (!hasLocationPermissions) return

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val query = APIQuery.Location(location.latitude, location.longitude)
                    currentLocation = query
                    getWeatherAsync(this, query, ::updateUI, ::handleError)
                    getForecastsAsync(this, query, ::updateForecasts, ::handleError)
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

        if (selectedCity != -1 && selectedCity < userCities.size) {
            getWeatherForCity(userCities[selectedCity])
        } else {
            getUserLocationWeather()
        }
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

    // Opens the hourly forecast activity.
    fun goToHourlyForecast(view: View) {
        if (currentLocation != null) {
            val intent = Intent(this, HourlyForecastActivity::class.java)
            intent.putExtra("lat", currentLocation!!.latitude)
            intent.putExtra("lon", currentLocation!!.longitude)
            startActivity(intent)
        }
    }

    fun retry(view: View) {
        errorLayout.isVisible = false
        loadingSpinner.isVisible = true
        getUserLocationWeather()
    }
}