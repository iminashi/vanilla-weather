package fi.tiko.vanillaweather.openweather

import android.app.Activity
import com.fasterxml.jackson.databind.ObjectMapper
import fi.tiko.vanillaweather.R
import java.lang.Exception
import java.net.URL
import kotlin.concurrent.thread

private fun createAPIURL(query: String, apiKey: String): URL =
    URL("$API_BASE_URL/onecall?$query&units=metric&appid=$apiKey&exclude=current,minutely,daily,alerts")

private fun callApi(query: String, apiKey: String): HourlyAPIResponse {
    val url = createAPIURL(query, apiKey)
    return ObjectMapper().readValue(url, HourlyAPIResponse::class.java)
}

fun getHourlyForecastsAsync(
    context: Activity,
    apiQuery: APIQuery.Location,
    onSuccess: (List<HourlyWeather>) -> Unit,
    onFailure: (String) -> Unit,
) {
    thread {
        try {
            val query = createQueryString(apiQuery)
            val apiKey = context.getString(R.string.openweathermap_api_key)
            val response = callApi(query, apiKey)
            val hourly =
                response.hourly?.map { forecast ->
                    HourlyWeather(forecast, tryGetIcon(forecast.weather, 2))
                }

            context.runOnUiThread {
                if (hourly != null) {
                    onSuccess(hourly)
                } else {
                    onFailure("Hourly forecast did not contain expected data.")
                }
            }
        } catch (e: Exception) {
            context.runOnUiThread {
                onFailure(e.message ?: "Fetching weather information failed.")
            }
        }
    }
}