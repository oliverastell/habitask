package habitask.common.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.monthsUntil
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearsUntil
import kotlin.math.absoluteValue
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


val sameYearFormat = LocalDateTime.Format {
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    chars(" ")
    day(Padding.NONE)

    chars(" at ")

    amPmHour()
    char(':')
    minute(Padding.ZERO)
    amPmMarker("am", "pm")
}

val diffYearFormat = LocalDateTime.Format {
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    chars(" ")
    day(Padding.NONE)
    chars(", ")
    year()

    chars(" at ")

    amPmHour()
    char(':')
    minute(Padding.ZERO)
    amPmMarker("am", "pm")
}

val sameDayFormat = LocalDateTime.Format {
    amPmHour()
    char(':')
    minute(Padding.ZERO)
    amPmMarker("am", "pm")
}

fun formatTime(
    now: Instant = Clock.System.now(),
    time: Instant
): String {
    val timeZone = TimeZone.currentSystemDefault()
    val dateTime = time.toLocalDateTime(timeZone)
    val nowDateTime = now.toLocalDateTime(timeZone)

    val format = when {
        nowDateTime.day == dateTime.day &&
                nowDateTime.month == dateTime.month &&
                nowDateTime.year == dateTime.year -> sameDayFormat
        nowDateTime.year == dateTime.year -> sameYearFormat
        else -> diffYearFormat
    }

    return dateTime.format(format)
}

// A positive time value represents the future
@OptIn(ExperimentalTime::class)
fun formatTimeRelative(
    now: Instant = Clock.System.now(),
    time: Instant,
    pastPrefix: String = "",
    pastSuffix: String = " ago",
    futurePrefix: String = "in ",
    futureSuffix: String = "",
): String {
    val duration = now - time

    val prefix = if (duration.isNegative()) futurePrefix else pastPrefix
    val suffix = if (duration.isPositive()) pastSuffix else futureSuffix

    val abs = duration.absoluteValue

    val months = Clock.System.now().monthsUntil(Clock.System.now() + duration, TimeZone.currentSystemDefault())
    val years = Clock.System.now().yearsUntil(Clock.System.now() + duration, TimeZone.currentSystemDefault())

    return when {
        abs < 1.minutes -> "$prefix${duration.inWholeSeconds.absoluteValue} seconds$suffix"
        abs < 1.hours -> "$prefix${duration.inWholeMinutes.absoluteValue} minutes$suffix"
        abs < 1.days -> "$prefix${duration.inWholeHours.absoluteValue} hours$suffix"
        abs < 7.days -> "$prefix${duration.inWholeDays.absoluteValue} days$suffix"
        years > 0 -> "$prefix$years years$suffix"
        months > 0 -> "$prefix$months months$suffix"
        else -> "$prefix${duration.inWholeDays/7} weeks$suffix"
    }
}