# Vanilla Weather

![Emulator screenshot release 2](https://i.imgur.com/ZVNurea.png)

**vanilla** *adj*: plain, ordinary, or uninteresting

A simple Android weather app written in Kotlin.

---

|             |                      |
| ----------- | -------------------- |
| Name        | Tapio Malmberg       |
| Topic       | Weather app for displaying the weather for the user's current location or for a user-defined city selected from an editable list. |
| Target      | Android/Kotlin       |
| Google Play link | N/A    |

## Building and Running the App

The project can be opened and run with Andoid Studio.

Running the app requires an OpenWeatherMap API key that is not included in the Git repository. The key should be placed in the file res/values/secrets.xml in the following format:

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="openweathermap_api_key">YOUR_API_KEY_HERE</string>
</resources>
```

## Releases

### Release 1: 2021-05-12 features

![Emulator screenshot release 1](https://i.imgur.com/3BbUVj9.png)

- User is able to see the weather information from the free OpenWeather API
- The current location of the user is used
- User is able to see the forecast for the next seven days
- Shows OpenWeather's icons for the weather type
- Location permission will be asked if not enabled

### Release 2: 2021-05-21 features

- User is able to choose between displaying the weather for the current location or for a custom city
- User is able to add or remove cities from the list of cities
- User is able to view a hourly forecast
- The city list and selected city is saved when the app is closed
- A retry button is displayed if an error occurs for an operation that may be retried

### Known Bugs

- The user can enter a place name that does not exist, in which case an error message will be displayed when the weather is fetched.

### Screencast

[![Screencast](http://img.youtube.com/vi/n7UUfvVXCWQ/0.jpg)](http://www.youtube.com/watch?v=n7UUfvVXCWQ "Screencast")
