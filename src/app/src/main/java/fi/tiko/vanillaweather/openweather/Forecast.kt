package fi.tiko.vanillaweather.openweather

import android.app.Activity
import com.fasterxml.jackson.databind.ObjectMapper
import fi.tiko.vanillaweather.R
import java.lang.Exception
import java.net.URL
import kotlin.concurrent.thread

private fun createAPIURL(query: String, apiKey: String): URL =
    URL("$API_BASE_URL/onecall?$query&units=metric&appid=$apiKey&exclude=current,minutely,hourly,alerts")

private fun callApi(query: String, apiKey: String): ForecastAPIResponse {
    val url = createAPIURL(query, apiKey)
    return ObjectMapper().readValue(url, ForecastAPIResponse::class.java)
}

fun getForecastsAsync(
    context: Activity,
    apiQuery: APIQuery.Location,
    onSuccess: (List<ForecastWeather>) -> Unit,
    onFailure: (String) -> Unit
) {
    thread {
        try {
            val query = createQueryString(apiQuery)
            val apiKey = context.getString(R.string.openweathermap_api_key)
            val response = callApi(query, apiKey)
            val forecasts =
                if (response.daily != null) {
                    // Skip the current day
                    val nextDays = response.daily.subList(1, response.daily.size)
                    nextDays.map { forecast ->
                        ForecastWeather(forecast, tryGetIcon(forecast.weather, 2))
                    }
                } else {
                    null
                }

            context.runOnUiThread {
                if (forecasts != null) {
                    onSuccess(forecasts)
                } else {
                    onFailure("Daily forecast did not contain expected data.")
                }
            }
        } catch (e: Exception) {
            context.runOnUiThread {
                onFailure(e.message ?: "Fetching weather information failed.")
            }
        }
    }
}