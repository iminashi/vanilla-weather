package fi.tiko.vanillaweather

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import fi.tiko.vanillaweather.adapters.HourlyForecastAdapter
import fi.tiko.vanillaweather.openweather.APIQuery
import fi.tiko.vanillaweather.openweather.HourlyWeather
import fi.tiko.vanillaweather.openweather.getHourlyForecastsAsync

class HourlyForecastActivity : AppCompatActivity() {
    private lateinit var errorText: TextView

    private fun updateUI(forecasts: List<HourlyWeather>) {
        val recyclerView = findViewById<RecyclerView>(R.id.hourly_recycler_view)
        recyclerView.adapter = HourlyForecastAdapter(forecasts)
        errorText.isVisible = false
    }

    private fun handleError(message: String) {
        errorText.isVisible = true
        errorText.text = message
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hourly_forecast)

        errorText = findViewById(R.id.hourlyErrorText)

        val latitude = intent.getDoubleExtra(LATITUDE, Double.NaN)
        val longitude = intent.getDoubleExtra(LONGITUDE, Double.NaN)
        if (!latitude.isNaN() && !longitude.isNaN()) {
            getHourlyForecastsAsync(this, APIQuery.Location(latitude, longitude), ::updateUI, ::handleError)
        }

        supportActionBar?.title = getString(R.string.hourly_forecast)
    }
}