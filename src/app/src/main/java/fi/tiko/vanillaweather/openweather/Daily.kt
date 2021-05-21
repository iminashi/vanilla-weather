package fi.tiko.vanillaweather.openweather

import android.app.Activity
import android.graphics.Bitmap
import com.fasterxml.jackson.databind.ObjectMapper
import fi.tiko.vanillaweather.R
import java.lang.Exception
import java.net.URL
import kotlin.concurrent.thread

data class DailyWeather(
    val forecast: DailyForecast,
    val icon: Bitmap?
)

// Creates the URL object for calling the API.
private fun createAPIURL(query: String, apiKey: String): URL =
    URL("$API_BASE_URL/onecall?$query&units=metric&appid=$apiKey&exclude=current,minutely,hourly,alerts")

// Calls the API with the given query and maps the result into a DailyForecastAPIResponse object.
private fun callApi(query: String, apiKey: String): DailyForecastAPIResponse {
    val url = createAPIURL(query, apiKey)
    return ObjectMapper().readValue(url, DailyForecastAPIResponse::class.java)
}

// Fetches the daily forecasts from the API.
fun getDailyForecastsAsync(
    context: Activity,
    apiQuery: APIQuery.Location,
    onSuccess: (List<DailyWeather>) -> Unit,
    onFailure: (String) -> Unit
) {
    thread {
        try {
            val query = createQueryString(apiQuery)
            val apiKey = context.getString(R.string.openweathermap_api_key)
            val response = callApi(query, apiKey)

            // Map the response to DailyWeather objects.
            val forecasts =
                response.daily?.let { daily ->
                    // Skip the current day.
                    val nextDays = daily.subList(1, daily.size)
                    nextDays.map { forecast ->
                        DailyWeather(forecast, tryGetIcon(forecast.weather, 2))
                    }
                }

            context.runOnUiThread {
                if (forecasts != null) {
                    onSuccess(forecasts)
                } else {
                    onFailure("Daily forecast did not contain the expected data.")
                }
            }
        } catch (e: Exception) {
            context.runOnUiThread {
                onFailure("Fetching the weather information failed.")
            }
        }
    }
}
