package habitask.server.data.h2.custom

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.CustomStringFunction
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.LowerCase
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.append
import org.jetbrains.exposed.sql.stringLiteral
import kotlin.math.exp

class Replaced(
    /** Returns the expression to convert. */
    val expr: Expression<String>,
    val old: String,
    val new: String
) : Function<String>(TextColumnType()) {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit = queryBuilder {
        append("REPLACE(")
        append(expr)
        append(",")
        append(stringLiteral(old))
        append(",")
        append(stringLiteral(new))
        append(")")
    }
}

class RegexReplaced(
    val expr: Expression<String>,
    val pattern: Regex,
    val new: String
) : Function<String>(TextColumnType()) {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit = queryBuilder {
        append("REGEXP_REPLACE(")
        append(expr)
        append(",")
        append(stringLiteral(pattern.pattern))
        append(",")
        append(stringLiteral(new))
        append(")")
    }
}

fun Expression<String>.replace(old: String, new: String): Replaced = Replaced(this, old, new)

fun Expression<String>.replace(old: Regex, new: String): RegexReplaced = RegexReplaced(this, old, new)




