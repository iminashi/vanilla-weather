package fi.tiko.vanillaweather

import android.graphics.Bitmap
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Coordinates(
    val lon: Double? = null,
    val lat: Double? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherAttributes(
    val main: String? = null,
    val description: String? = null,
    val icon: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WindAttributes(val speed: Double? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MainAttributes(
    val temp: Double? = null,
    val feels_like: Double? = null,
    val humidity: Double? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherAPIResponse(
    val coord: Coordinates? = null,
    val name: String? = null,
    val dt: Long? = null,
    val weather: MutableList<WeatherAttributes>? = null,
    val main: MainAttributes? = null,
    val wind: WindAttributes? = null
)

data class Weather(val response: WeatherAPIResponse, val icon: Bitmap? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DailyTemp(val min: Double? = null, val max: Double? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DailyForecast(
    val dt: Long? = null,
    val temp: DailyTemp? = null,
    val weather: MutableList<WeatherAttributes>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastAPIResponse(
    val daily: MutableList<DailyForecast>? = null
)

data class ForecastWeather(
    val forecast: DailyForecast,
    val icon: Bitmap?
)
