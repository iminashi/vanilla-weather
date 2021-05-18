package fi.tiko.vanillaweather

import java.util.*

// Converts a UNIX epoch into a Date object.
fun epochToDate(epoch: Long) = Date(epoch * 1000)
