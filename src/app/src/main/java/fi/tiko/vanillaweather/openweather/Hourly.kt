package fi.tiko.vanillaweather.openweather

import android.app.Activity
import com.fasterxml.jackson.databind.ObjectMapper
import fi.tiko.vanillaweather.R
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
    callback: (List<HourlyWeather>) -> Unit
) {
    thread {
        val query = createQueryString(apiQuery)
        val apiKey = context.getString(R.string.openweathermap_api_key)
        val response = callApi(query, apiKey)
        val hourly =
            response.hourly?.map { forecast ->
                HourlyWeather(forecast, tryGetIcon(forecast.weather, 2))
            }

        context.runOnUiThread {
            if (hourly != null) {
                callback(hourly)
            }
        }
    }
}