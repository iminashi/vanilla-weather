# Vanilla Weather

![Emulator screenshot release 2](https://i.imgur.com/ZVNurea.png)

**vanilla** *adj*: plain, ordinary, or uninteresting

A simple Android weather app written in Kotlin. Created for a programming course in the Tampere University of Applied Sciences.

## Features

- Shows the weather information for the current location or for a user defined location.
- Forecast for the next seven days.
- Hourly forecast.

## Building and Running the App

The project can be opened and run with Andoid Studio.

Running the app requires an OpenWeatherMap API key that is not included in the Git repository. The key should be placed in the file res/values/secrets.xml in the following format:

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="openweathermap_api_key">YOUR_API_KEY_HERE</string>
</resources>
```

### Known Issues

- The user can enter a place name that does not exist, in which case an error message will be displayed when the weather is fetched.
