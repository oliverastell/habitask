package habitask.server.data.h2.custom

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.CustomStringFunction
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.LowerCase
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.append
import kotlin.math.exp

class Replaced<T : String?>(
    /** Returns the expression to convert. */
    val expr: Expression<T>,
    val old: Any,
    val new: Any
) : Function<String>(TextColumnType()) {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit = queryBuilder {
        append("REPLACE(")
        append(expr)
        append(",")
        append(old)
        append(",")
        append(new)
        append(")")
    }
}

fun <T : String?> Expression<T>.replace(old: Any, new: Any): Replaced<T> = Replaced(this, old, new)



