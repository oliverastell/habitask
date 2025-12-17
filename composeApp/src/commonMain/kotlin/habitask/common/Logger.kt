package habitask.common

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun interface LogStreamBinding : AutoCloseable {
    override fun close()
}


class LogStream {
    private val protocols: MutableList<((Any?) -> Unit)?> = mutableListOf()

    operator fun invoke() = invoke("")
    operator fun invoke(message: Any?) {
        protocols.forEach { protocol ->
            protocol?.invoke(message)
        }
    }

    @OptIn(ExperimentalTime::class)
    inline fun bind(tag: String, crossinline protocol: (String?) -> Unit): LogStreamBinding {
        return bindRaw { message ->
            val time = Clock.System.now()
            val timeZone = TimeZone.currentSystemDefault()
            val localTime = time.toLocalDateTime(timeZone)

            val timeFormatted = LocalDateTime.Format {
                hour()
                char(':')
                minute()
                char(':')
                second()
            }.format(localTime)

            protocol("[$tag $timeFormatted] $message")
        }
    }

    fun bindRaw(protocol: (Any?) -> Unit): LogStreamBinding {
        val i = protocols.indexOf(null)

        if (i == -1) {
            protocols.add(protocol)
            return LogStreamBinding { protocols[protocols.lastIndex] = null }
        } else {
            protocols[i] = protocol
            return LogStreamBinding { protocols[i] = null }
        }
    }
}


object Logger {
    val debug = LogStream()
    val feedback = LogStream()
    val info = LogStream()
    val warning = LogStream()
    val error = LogStream()

    init {
        loggerStaticBindings(this)
    }
}
expect fun loggerStaticBindings(logger: Logger)