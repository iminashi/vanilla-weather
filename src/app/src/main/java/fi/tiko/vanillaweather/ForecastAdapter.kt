package fi.tiko.vanillaweather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fi.tiko.vanillaweather.openweather.ForecastWeather
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class ForecastAdapter(private val dataSet: List<ForecastWeather>) :
    RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewDay: TextView = view.findViewById(R.id.textViewDay)
        val textViewTempRange: TextView = view.findViewById(R.id.textViewTempRange)
        val textWeatherType: TextView = view.findViewById(R.id.textWeatherType)
        val imageViewIcon: ImageView = view.findViewById(R.id.forecastIcon)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.forecast_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val weather = dataSet[position]
        val date = epochToDate(weather.forecast.dt!!)

        viewHolder.textViewDay.text = SimpleDateFormat("EE", Locale.US).format(date)
        val min = weather.forecast.temp?.min?.roundToInt()
        val max = weather.forecast.temp?.max?.roundToInt()
        viewHolder.textViewTempRange.text =
            viewHolder.itemView.context.getString(R.string.temperature_range, min, max)
        viewHolder.textWeatherType.text = weather.forecast.weather?.get(0)?.main
        viewHolder.imageViewIcon.setImageBitmap(weather.icon)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}