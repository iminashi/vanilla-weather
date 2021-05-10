package fi.tiko.vanillaweather

import android.graphics.Bitmap
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

sealed class APILocation {
    class Location(val latitude: Double, val longitude: Double) : APILocation()
    class Name(val name: String) : APILocation()
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherAttributes(
    var main: String? = null,
    var description: String? = null,
    var icon: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WindAttributes(var speed: Double? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MainAttributes(
    var temp: Double? = null,
    var feels_like: Double? = null,
    var humidity: Double? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class APIResponse(
    var name: String? = null,
    var dt: Long? = null,
    var weather: MutableList<WeatherAttributes>? = null,
    var main: MainAttributes? = null,
    var wind: WindAttributes? = null
)

data class Weather(var response: APIResponse, var icon: Bitmap? = null)