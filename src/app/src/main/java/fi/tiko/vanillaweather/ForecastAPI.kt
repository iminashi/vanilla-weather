package fi.tiko.vanillaweather

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import java.lang.Exception
import java.net.URL
import kotlin.concurrent.thread

private fun createAPIURL(query: String, apiKey: String) =
    "https://api.openweathermap.org/data/2.5/onecall?$query&units=metric&appid=$apiKey&exclude=current,minutely,hourly,alerts"

private fun callApi(urlString: String): ForecastAPIResponse =
    ObjectMapper().readValue(URL(urlString), ForecastAPIResponse::class.java)

private fun createQueryString(apiQuery: APIQuery.Location) =
    "lat=${apiQuery.latitude}&lon=${apiQuery.longitude}"

private fun tryGetIcon(response: DailyForecast): Bitmap? {
    val weatherAttr = response.weather?.get(0)
    return if (weatherAttr != null) {
        try {
            val url = URL("https://openweathermap.org/img/wn/${weatherAttr.icon}@2x.png")
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: Exception) {
            Log.d("ForecastAPI", "Getting the weather icon failed: ${e.message}.")
            null
        }
    } else {
        null
    }
}

fun getForecastAsync(
    context: Activity,
    apiQuery: APIQuery.Location,
    callback: (List<ForecastWeather>) -> Unit
) {
    thread {
        val query = createQueryString(apiQuery)
        val apiKey = context.getString(R.string.openweathermap_api_key)
        val response = callApi(createAPIURL(query, apiKey))
        val forecasts =
            if (response.daily != null) {
                val nextDays = response.daily.subList(1, response.daily.size)
                val icons = nextDays.map(::tryGetIcon)
                nextDays.mapIndexed { i, forecast ->
                    ForecastWeather(forecast, icons[i])
                }
            } else {
                null
            }

        context.runOnUiThread {
            if (forecasts != null) {
                callback(forecasts)
            }
        }
    }
}
