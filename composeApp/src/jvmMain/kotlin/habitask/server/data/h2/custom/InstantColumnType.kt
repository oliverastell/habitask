package habitask.server.data.h2.custom

import kotlinx.datetime.DateTimeUnit
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IDateColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.currentDialect
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

class InstantColumnType : ColumnType<Instant>(), IDateColumnType {
    override val hasTimePart: Boolean = true

    override fun sqlType(): String = currentDialect.dataTypeProvider.timestampType()

    override fun valueFromDB(value: Any): Instant = when (value) {
        is java.sql.Timestamp -> value.toInstant().toKotlinInstant()
        is String -> Instant.parse(value)
        else -> valueFromDB(value.toString())
    }

    override fun notNullValueToDB(value: Instant): Any =
        java.sql.Timestamp.from(value.toJavaInstant())
}

fun Table.instant(name: String): Column<Instant> = registerColumn(name, InstantColumnType())
