package fi.tiko.vanillaweather.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fi.tiko.vanillaweather.R
import fi.tiko.vanillaweather.capitalize
import fi.tiko.vanillaweather.epochToDate
import fi.tiko.vanillaweather.openweather.HourlyWeather
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

// RecyclerView adapter for hourly forecasts.
class HourlyForecastAdapter(private val dataSet: List<HourlyWeather>) :
    RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTime: TextView = view.findViewById(R.id.textViewHourlyTime)
        val textViewTemperature: TextView = view.findViewById(R.id.textViewTemperature)
        val textWeatherType: TextView = view.findViewById(R.id.textWeatherType)
        val textViewWind: TextView = view.findViewById(R.id.textViewWind)
        val imageViewIcon: ImageView = view.findViewById(R.id.forecastIcon)
    }

    // Create new views (invoked by the layout manager).
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item.
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.hourly_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager).
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val weather = dataSet[position]
        val date = epochToDate(weather.forecast.dt!!)

        viewHolder.textViewTime.text =
            SimpleDateFormat("d.M HH:mm", Locale.getDefault()).format(date)
        viewHolder.textViewTemperature.text =
            viewHolder.itemView.context.getString(
                R.string.temperature,
                weather.forecast.temp?.roundToInt()
            )
        viewHolder.textWeatherType.text =
            capitalize(weather.forecast.weather?.get(0)?.description)
        viewHolder.textViewWind.text =
            viewHolder.itemView.context.getString(R.string.wind_speed, weather.forecast.wind_speed)
        viewHolder.imageViewIcon.setImageBitmap(weather.icon)
    }

    // Return the size of your dataset (invoked by the layout manager).
    override fun getItemCount() = dataSet.size
}