package fi.tiko.vanillaweather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import fi.tiko.vanillaweather.adapters.DailyForecastAdapter
import java.text.SimpleDateFormat
import java.util.*
import fi.tiko.vanillaweather.openweather.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var hasLocationPermissions: Boolean = false
    private var lastUpdated: Date? = null

    private var userCities =  mutableListOf("Tampere", "New York", "Tokyo")
    private var selectedCityIndex = -1
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
    private lateinit var retryButton: Button
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var forceUpdateIcon: ImageView

    // Checks if the app has location permissions allowed.
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

    // Responds to the location permission request.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d("MainActivity", "onRequestPermissionsResult")

        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                hasLocationPermissions = (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                if (hasLocationPermissions) {
                    getUserLocationWeather()
                } else if (selectedCityIndex == -1) {
                    showErrorMessage(
                        "Cannot use the current location. Please select a city to use from the cities list.",
                        canRetry = false
                    )
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // Creates the main menu.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Transition into the cities list activity.
            R.id.menu_cities -> {
                val intent = Intent(this, CitiesActivity::class.java)
                intent.putExtra(CITIES, userCities.toTypedArray())
                intent.putExtra(SELECTED_CITY, selectedCityIndex)
                startActivityForResult(intent, CITIES_INTENT_CODE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Update the cities list and the selected city index.
        if (requestCode == CITIES_INTENT_CODE && resultCode == RESULT_OK) {
            data?.extras?.getStringArray(CITIES)?.toMutableList()?.let {
                userCities = it
            }
            data?.extras?.getInt(SELECTED_CITY)?.let {
                // Force an update if the selected city index changed.
                if (selectedCityIndex != it) {
                    lastUpdated = null
                }
                selectedCityIndex = it
            }
        }
    }

    private fun assignViewElements() {
        weatherType = findViewById(R.id.weatherType)
        locationText = findViewById(R.id.locationText)
        lastUpdatedText = findViewById(R.id.lastUpdatedText)
        temperature = findViewById(R.id.temperature)
        windSpeedText = findViewById(R.id.windSpeed)
        icon = findViewById(R.id.weatherIcon)
        hourlyForecastButton = findViewById(R.id.buttonHourlyForecast)
        errorLayout = findViewById(R.id.errorLayout)
        errorMessage = findViewById(R.id.errorMessage)
        retryButton = findViewById(R.id.retryButton)
        mainWeatherLayout = findViewById(R.id.mainWeatherLayout)
        loadingSpinner = findViewById(R.id.loadingSpinner)
        recyclerView = findViewById(R.id.recycler_view)
        forceUpdateIcon = findViewById(R.id.forceUpdateIcon)
    }

    private fun loadPreferences() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        selectedCityIndex = sharedPref.getInt(SELECTED_CITY, -1)
        val savedCities = sharedPref.getString(CITIES, null)
        if (savedCities != null) {
            userCities = savedCities.split(",").toMutableList()
        }
    }

    private fun createLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        assignViewElements()
        checkLocationPermissions()
        loadPreferences()
        createLocationClient()
    }

    // Hides the weather information UI elements.
    private fun hideWeatherUI() {
        mainWeatherLayout.isVisible = false
        hourlyForecastButton.isVisible = false
        recyclerView.isVisible = false
        forceUpdateIcon.isVisible = false
    }

    private fun showErrorMessage(message: String, canRetry: Boolean) {
        loadingSpinner.isVisible = false
        hideWeatherUI()

        errorLayout.isVisible = true
        retryButton.isVisible = canRetry

        errorMessage.text = message
    }

    private fun handleError(message: String) =
        showErrorMessage(message, canRetry = true)

    // Updates the UI with the weather information.
    private fun updateUI(weather: Weather) {
        loadingSpinner.isVisible = false
        errorLayout.isVisible = false
        mainWeatherLayout.isVisible = true
        recyclerView.isVisible = true
        hourlyForecastButton.isVisible = true
        forceUpdateIcon.isVisible = true

        val response = weather.response
        lastUpdated = epochToDate(response.dt!!)
        val weatherAttr = response.weather?.get(0)

        val updated = SimpleDateFormat("dd.M. HH.mm", Locale.getDefault()).format(lastUpdated!!)
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
        recyclerView.adapter = DailyForecastAdapter(forecasts)
    }

    // Updates the UI for the weather for the city and fetches the daily forecasts.
    private fun weatherForCityCallback(weather: Weather) {
        updateUI(weather)

        // Retrieve the forecasts using the location information from the response
        if (weather.response.coord != null) {
            val query =
                APIQuery.Location(weather.response.coord.lat!!, weather.response.coord.lon!!)
            currentLocation = query
            getForecastsAsync(this, query, ::updateForecasts, ::handleError)
        }
    }

    private fun getWeatherForCity(cityName: String) {
        getWeatherAsync(this, APIQuery.Name(cityName), ::weatherForCityCallback) { errorMessage ->
            // Disable the retry button if the city was not found
            val canRetry = !errorMessage.contains("city")
            showErrorMessage(errorMessage, canRetry)
        }
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
                    showErrorMessage(
                        "Unable to detect the current location. Please select a city to use in the cities list.",
                        canRetry = false
                    )
                }
            }
            .addOnFailureListener {
                showErrorMessage("Getting the current location failed.", canRetry = true)
            }
    }

    // Returns true if a city has been selected.
    private fun isCitySelected() =
        selectedCityIndex != -1 && selectedCityIndex < userCities.size

    // Fetches the weather information for the selected city or the current location.
    private fun updateWeatherInfo() {
        if (isCitySelected() || hasLocationPermissions) {
            hideWeatherUI()
            errorLayout.isVisible = false
            loadingSpinner.isVisible = true
        }

        if (isCitySelected()) {
            getWeatherForCity(userCities[selectedCityIndex])
        } else if (hasLocationPermissions) {
            getUserLocationWeather()
        }
    }

    private fun shouldUpdate(): Boolean {
        if(lastUpdated != null) {
            val now = Calendar.getInstance().time
            // Check if 5 minutes have passed since the last update
            return (now.time - lastUpdated!!.time >= 300000)
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        if(shouldUpdate()) {
            updateWeatherInfo()
        }
    }

    // Opens the hourly forecast activity.
    fun goToHourlyForecast(view: View) {
        if (currentLocation != null) {
            val intent = Intent(this, HourlyForecastActivity::class.java)
            intent.putExtra(LATITUDE, currentLocation!!.latitude)
            intent.putExtra(LONGITUDE, currentLocation!!.longitude)
            startActivity(intent)
        }
    }

    fun retryClicked(view: View) = updateWeatherInfo()

    fun forceUpdate(view: View) = updateWeatherInfo()

    private fun savePreferences() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt(SELECTED_CITY, selectedCityIndex)
            putString(CITIES, userCities.joinToString(separator = ","))
            commit()
        }
    }

    override fun onDestroy() {
        savePreferences()
        super.onDestroy()
    }
}