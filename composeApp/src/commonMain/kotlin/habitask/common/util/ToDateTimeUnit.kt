package habitask.common.util

import kotlinx.datetime.DateTimeUnit
import kotlin.text.iterator

fun DateTimeUnit.toFormattedString(): String =
    when (this) {
        is DateTimeUnit.DayBased ->
            "$days days"
        is DateTimeUnit.MonthBased ->
            "$months months"
        is DateTimeUnit.DateBased ->
            this.toString()
        is DateTimeUnit.TimeBased ->
            "${this.nanoseconds} ns"
    }

fun String.toDateTimeUnit(): DateTimeUnit {
    val letters = StringBuilder(this.length)
    val digits = StringBuilder(this.length)

    for (c in this) {
        if (c.isLetter())
            letters.append(c)
        else if (c.isDigit())
            digits.append(c)
    }

    val num = digits.toString().toLongOrNull() ?: 1

    return when (letters.toString().lowercase()) {
        "nanosecond", "nanoseconds", "ns" -> DateTimeUnit.TimeBased(nanoseconds = num)
        "microsecond", "microseconds", "us", "μs" -> DateTimeUnit.TimeBased(nanoseconds = num * 1000)
        "millisecond", "milliseconds", "ms" -> DateTimeUnit.TimeBased(nanoseconds = num * 1_000_000)
        "second", "seconds", "sec", "secs", "s" -> DateTimeUnit.TimeBased(nanoseconds = num * 1_000_000_000)
        "minute", "minutes", "m", "min", "mins" -> DateTimeUnit.TimeBased(nanoseconds = num * 60_000_000_000)
        "hour", "hours", "h", "hr", "hrs" -> DateTimeUnit.TimeBased(nanoseconds = num * 3600_000_000_000)
//            "day", "days", "d" -> DateTimeUnit.DAY * num
        "week", "weeks", "wk", "wks" -> DateTimeUnit.DayBased(num.toInt() * 7)
        "month", "months", "mo", "mos" -> DateTimeUnit.MonthBased(num.toInt())
        "year", "years", "y", "yr", "yrs" -> DateTimeUnit.MonthBased(num.toInt() * 12)
        "quarter", "quarters", "q", "qr", "qrs", "season", "seasons" -> DateTimeUnit.MonthBased(num.toInt() * 3)
        else -> DateTimeUnit.DayBased(num.toInt())
    }
}