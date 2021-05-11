package fi.tiko.vanillaweather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
        val date = Date(weather.forecast.dt!! * 1000)

        viewHolder.textViewDay.text = SimpleDateFormat("EE", Locale.US).format(date)
        val min = weather.forecast.temp?.min?.roundToInt().toString()
        val max = weather.forecast.temp?.max?.roundToInt().toString()
        viewHolder.textViewTempRange.text = "$min - $max °C"
        viewHolder.textWeatherType.text = weather.forecast.weather?.get(0)?.main
        viewHolder.imageViewIcon.setImageBitmap(weather.icon)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}