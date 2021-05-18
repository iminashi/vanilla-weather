package fi.tiko.vanillaweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import fi.tiko.vanillaweather.openweather.APIQuery
import fi.tiko.vanillaweather.openweather.HourlyWeather
import fi.tiko.vanillaweather.openweather.getHourlyForecastsAsync

class HourlyForecastActivity : AppCompatActivity() {
    private fun updateUI(forecasts: List<HourlyWeather>) {
        val recyclerView = findViewById<RecyclerView>(R.id.hourly_recycler_view)
        recyclerView.adapter = HourlyForecastAdapter(forecasts)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hourly_forecast)

        val latitude = intent.getDoubleExtra("lat", Double.NaN)
        val longitude = intent.getDoubleExtra("lon", Double.NaN)
        if (!latitude.isNaN() && !longitude.isNaN()) {
            getHourlyForecastsAsync(this, APIQuery.Location(latitude, longitude), ::updateUI)
        }
    }
}