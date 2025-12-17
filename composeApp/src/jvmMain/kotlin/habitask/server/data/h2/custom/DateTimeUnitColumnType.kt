package habitask.server.data.h2.custom

import habitask.common.util.toDateTimeUnit
import habitask.common.util.toFormattedString
import kotlinx.datetime.DateTimeUnit
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IDateColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.currentDialect


class DateTimeUnitColumnType : ColumnType<DateTimeUnit>(), IDateColumnType {
    override val hasTimePart: Boolean = true
    override fun sqlType(): String = currentDialect.dataTypeProvider.textType()

    override fun valueFromDB(value: Any): DateTimeUnit {
        if (value is DateTimeUnit)
            return value
        return value.toString().toDateTimeUnit()
    }

    override fun notNullValueToDB(value: DateTimeUnit): Any =
        value.toFormattedString()
}

fun Table.datetimeunit(name: String): Column<DateTimeUnit> = registerColumn(name, DateTimeUnitColumnType())
