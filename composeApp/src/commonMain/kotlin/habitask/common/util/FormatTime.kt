package habitask.common.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.monthsUntil
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

    val months = Clock.System.now().monthsUntil(Clock.System.now() + duration, TimeZone.currentSystemDefault())
    val years = Clock.System.now().yearsUntil(Clock.System.now() + duration, TimeZone.currentSystemDefault())

    return when {
        duration < 1.minutes -> "$prefix${duration.inWholeSeconds.absoluteValue} seconds$suffix"
        duration < 1.hours -> "$prefix${duration.inWholeMinutes.absoluteValue} minutes$suffix"
        duration < 1.days -> "$prefix${duration.inWholeHours.absoluteValue} hours$suffix"
        duration < 7.days -> "$prefix${duration.inWholeDays.absoluteValue} days$suffix"
        years > 0 -> "$prefix$years years$suffix"
        months > 0 -> "$prefix$months months$suffix"
        else -> "$prefix${duration.inWholeDays/7} weeks$suffix"
    }
}