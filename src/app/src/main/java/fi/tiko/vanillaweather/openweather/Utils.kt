package fi.tiko.vanillaweather.openweather

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.lang.Exception
import java.net.URL
import java.util.*

// Filters the characters in the query string and replaces spaces with "%20".
private fun filterApiQueryString(query: String) =
    query
        .lowercase(Locale.ROOT)
        .filter { it.isLetterOrDigit() || it == ' ' }
        .replace(" ", "%20")

// Creates a string from the API query object.
fun createQueryString(apiQuery: APIQuery) =
    when (apiQuery) {
        is APIQuery.Location ->
            "lat=${apiQuery.latitude}&lon=${apiQuery.longitude}"
        is APIQuery.Name ->
            "q=${filterApiQueryString(apiQuery.name)}"
    }

// Simple cache for the downloaded weather icon bitmaps.
private val iconCache = mutableMapOf<String, Bitmap>()

// Tries to download the weather with the given filename from the server.
// Returns null in case of failure.
private fun tryDownloadIcon(iconName: String) =
    try {
        val url = URL("$ICONS_BASE_URL/$iconName.png")
        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        iconCache[iconName] = bmp
        bmp
    } catch (e: Exception) {
        Log.d("OpenWeatherUtils", "Downloading the weather icon failed: ${e.message}.")
        null
    }

// Tries to get the icon for the weather attributes for the requested size.
// Returns null if the icon is not in the cache and the download failed.
fun tryGetIcon(weather: List<WeatherAttributes>?, size: Int): Bitmap? {
    val weatherAttr = weather?.getOrNull(0)

    return if (weatherAttr?.icon != null) {
        val iconName = "${weatherAttr.icon}@${size}x"
        iconCache.getOrElse(iconName) { tryDownloadIcon(iconName) }
    } else {
        null
    }
}
