package fi.tiko.vanillaweather.openweather

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

// Union type for calling the API either with a coordinate or a place name.
sealed class APIQuery {
    class Location(val latitude: Double, val longitude: Double) : APIQuery()
    class Name(val name: String) : APIQuery()
}

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

@JsonIgnoreProperties(ignoreUnknown = true)
data class DailyTemp(val min: Double? = null, val max: Double? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DailyForecast(
    val dt: Long? = null,
    val temp: DailyTemp? = null,
    val weather: MutableList<WeatherAttributes>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DailyForecastAPIResponse(
    val daily: MutableList<DailyForecast>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class HourlyForecast(
    val dt: Long? = null,
    val temp: Double? = null,
    val humidity: Double? = null,
    val wind_speed: Double? = null,
    val weather: MutableList<WeatherAttributes>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class HourlyForecastAPIResponse(
    val hourly: MutableList<HourlyForecast>? = null
)
