package fi.tiko.vanillaweather

import java.util.*

// Converts a UNIX epoch into a Date object.
fun epochToDate(epoch: Long) = Date(epoch * 1000)

// Capitalizes the first character of a string.
fun capitalize(str: String?) = str?.replaceFirstChar { it.uppercase() }
