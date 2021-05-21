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
import fi.tiko.vanillaweather.openweather.DailyWeather
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

// RecyclerView adapter for daily forecasts.
class DailyForecastAdapter(private val dataSet: List<DailyWeather>) :
    RecyclerView.Adapter<DailyForecastAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewDay: TextView = view.findViewById(R.id.textViewDay)
        val textViewTempRange: TextView = view.findViewById(R.id.textViewTempRange)
        val textWeatherType: TextView = view.findViewById(R.id.textWeatherType)
        val imageViewIcon: ImageView = view.findViewById(R.id.forecastIcon)
    }

    // Create new views (invoked by the layout manager).
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item.
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.forecast_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager).
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val weather = dataSet[position]

        // Set the weekday name.
        val date = epochToDate(weather.forecast.dt!!)
        viewHolder.textViewDay.text = SimpleDateFormat("EE", Locale.US).format(date)

        // Set the temperature range.
        val min = weather.forecast.temp?.min?.roundToInt()
        val max = weather.forecast.temp?.max?.roundToInt()
        viewHolder.textViewTempRange.text =
            viewHolder.itemView.context.getString(R.string.temperature_range, min, max)

        // Set the weather type text and the icon.
        viewHolder.textWeatherType.text = capitalize(weather.forecast.weather?.get(0)?.description)
        viewHolder.imageViewIcon.setImageBitmap(weather.icon)
    }

    // Return the size of your dataset (invoked by the layout manager).
    override fun getItemCount() = dataSet.size
}