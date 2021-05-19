package fi.tiko.vanillaweather.openweather

import android.app.Activity
import com.fasterxml.jackson.databind.ObjectMapper
import fi.tiko.vanillaweather.R
import java.io.FileNotFoundException
import java.lang.Exception
import java.net.URL
import kotlin.concurrent.thread

private fun createAPIURL(query: String, apiKey: String): URL =
    URL("$API_BASE_URL/weather?$query&units=metric&appid=$apiKey")

private fun callApi(query: String, apiKey: String): WeatherAPIResponse {
    val url = createAPIURL(query, apiKey)
    return ObjectMapper().readValue(url, WeatherAPIResponse::class.java)
}

fun getWeatherAsync(
    context: Activity,
    apiQuery: APIQuery,
    onSuccess: (Weather) -> Unit,
    onFailure: (String) -> Unit
) {
    thread {
        try {
            val query = createQueryString(apiQuery)
            val apiKey = context.getString(R.string.openweathermap_api_key)
            val response = callApi(query, apiKey)
            val icon = tryGetIcon(response.weather, 4)

            context.runOnUiThread {
                onSuccess(Weather(response, icon))
            }
        } catch (e: Exception) {
            val message =
                when (e) {
                    // 404 causes a FileNotFoundException
                    is FileNotFoundException -> {
                        "The city was not found"
                    }
                    else -> e.message ?: "Fetching weather information failed."
                }
            context.runOnUiThread {
                onFailure(message)
            }
        }
    }
}
