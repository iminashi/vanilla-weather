package fi.tiko.vanillaweather.openweather

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.lang.Exception
import java.net.URL
import java.util.*

fun createQueryString(apiQuery: APIQuery) =
    when (apiQuery) {
        is APIQuery.Location -> "lat=${apiQuery.latitude}&lon=${apiQuery.longitude}"
        is APIQuery.Name -> "q=${apiQuery.name.lowercase(Locale.ROOT)}"
    }

fun tryGetIcon(weather: List<WeatherAttributes>?, size: String): Bitmap? {
    val weatherAttr = weather?.get(0)
    return if (weatherAttr != null) {
        try {
            val url = URL("https://openweathermap.org/img/wn/${weatherAttr.icon}@$size.png")
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: Exception) {
            Log.d("ForecastAPI", "Getting the weather icon failed: ${e.message}.")
            null
        }
    } else {
        null
    }
}