package fi.tiko.vanillaweather

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import java.lang.Exception
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

private fun createAPIURL(query: String, apiKey: String) =
    "https://api.openweathermap.org/data/2.5/weather?$query&units=metric&appid=$apiKey"

private fun callApi(urlString: String): APIResponse =
    ObjectMapper().readValue(URL(urlString), APIResponse::class.java)

private fun createQueryString(location: APILocation) =
    when (location) {
        is APILocation.Location -> "lat=${location.latitude}&lon=${location.longitude}"
        is APILocation.Name -> "q=${location.name.toLowerCase(Locale.ROOT)}"
    }

private fun tryGetIcon(response: APIResponse): Bitmap? {
    val weatherAttr = response.weather?.get(0)
    return if (weatherAttr != null) {
        try {
            val url = URL("https://openweathermap.org/img/wn/${weatherAttr.icon}@4x.png")
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: Exception) {
            Log.d("WeatherAPI", "Getting the weather icon failed: ${e.message}.")
            null
        }
    } else {
        null
    }
}

fun getWeatherAsync(context: Activity, location: APILocation, callback: (Weather) -> Unit) {
    thread {
        val query = createQueryString(location)
        val apiKey = context.getString(R.string.openweathermap_api_key)
        val response = callApi(createAPIURL(query, apiKey))
        val icon = tryGetIcon(response)

        context.runOnUiThread {
            callback(Weather(response, icon))
        }
    }
}
