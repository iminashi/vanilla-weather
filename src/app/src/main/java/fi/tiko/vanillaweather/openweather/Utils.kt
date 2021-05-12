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

private val iconCache = mutableMapOf<String, Bitmap>()

private fun tryDownloadIcon(iconName: String): Bitmap? {
    return try {
        val url = URL("https://openweathermap.org/img/wn/$iconName.png")
        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        iconCache[iconName] = bmp
        bmp
    } catch (e: Exception) {
        Log.d("OpenWeatherUtils", "Downloading the weather icon failed: ${e.message}.")
        null
    }
}

fun tryGetIcon(weather: List<WeatherAttributes>?, size: Int): Bitmap? {
    val weatherAttr = weather?.get(0)
    return if (weatherAttr?.icon != null) {
        val iconName = "${weatherAttr.icon}@${size}x"
        iconCache.getOrElse(iconName) { tryDownloadIcon(iconName) }
    } else {
        null
    }
}
