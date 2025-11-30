package com.oliverastell.habitask.data.filemanagers

import com.oliverastell.habitask.data.classes.AccessInfo
import com.oliverastell.habitask.data.classes.GroupInfo
import com.oliverastell.habitask.data.classes.UserInfo
import com.oliverastell.habitask.data.util.toByteArray
import io.ktor.utils.io.pool.ByteBufferPool
import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.append
import kotlinx.io.bytestring.buildByteString
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.ByteBuffer
import kotlin.io.encoding.Base64
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


object GroupsTable : IntIdTable("groups") {
    val name = text("name")
}

object UsersTable : IntIdTable("users") {
    val name = text("name")
    val group = reference("group", GroupsTable.id)
    val lastOnline = long("last_online")
}

object UserAccessTable : Table("user_access") {
    val token = text("token").uniqueIndex()
    val userId = reference("user_id", UsersTable.id)
}

fun initializeTables(db: Database) {
    transaction(db) {
        SchemaUtils.create(UsersTable)
    }
    transaction(db) {
        SchemaUtils.create(GroupsTable)
    }
    transaction(db) {
        SchemaUtils.create(UserAccessTable)
    }
}

fun getUserById(db: Database, id: Int): UserInfo? {
    val row = transaction(db) {
        UsersTable.selectAll().where { UsersTable.id eq id }.firstOrNull()
    } ?: return null

    return UserInfo(
        id = row[UsersTable.id].value,
        name = row[UsersTable.name],
        groupId = row[UsersTable.group].value,
        lastOnline = row[UsersTable.lastOnline]
    )
}

fun getUserByToken(db: Database, token: String): UserInfo? {
    val row = transaction(db) {
        (UserAccessTable leftJoin UsersTable)
            .selectAll()
            .where {
                UserAccessTable.token eq token
            }.firstOrNull()
    } ?: return null

    return UserInfo(
        id = row[UsersTable.id].value,
        name = row[UsersTable.name],
        groupId = row[UsersTable.group].value,
        lastOnline = row[UsersTable.lastOnline],
    )
}

fun getGroupById(db: Database, id: Int): GroupInfo? {
    val row = transaction(db) {
        GroupsTable.selectAll().where { GroupsTable.id eq id }.firstOrNull()
    } ?: return null
    
    return GroupInfo(
        id = row[GroupsTable.id].value,
        name = row[UsersTable.name],
    )
}

var runtimeOffset = 0
@OptIn(ExperimentalTime::class)
fun newAccess(
    db: Database,
    userInfo: UserInfo
): AccessInfo {
    val epoch = Clock.System.now().epochSeconds
    val nanos = Clock.System.now().nanosecondsOfSecond

    val tokenBytes = buildByteString {
        append(userInfo.id.toByteArray())
        append(epoch.toByteArray())
        append(runtimeOffset.toByteArray())
        append(nanos.hashCode().toByteArray())
    }

    val token = Base64.withPadding(Base64.PaddingOption.ABSENT).encode(tokenBytes.toByteArray())

    transaction(db) {
        UserAccessTable.insert {
            it[UserAccessTable.token] = token
            it[UserAccessTable.userId] = userInfo.id
        }
    }

    runtimeOffset++

    return AccessInfo(
        token = token,
        userId = userInfo.id
    )
}

fun newUser(
    db: Database,
    name: String,
    groupInfo: GroupInfo,
    lastOnline: Long,
): UserInfo {
    val id = transaction(db) {
        UsersTable.insertAndGetId {
            it[UsersTable.name] = name
            it[UsersTable.group] = groupInfo.id
            it[UsersTable.lastOnline] = lastOnline
        }
    }
    
    return UserInfo(
        id = id.value,
        name = name,
        groupId = groupInfo.id,
        lastOnline = lastOnline
    )
}

fun newGroup(
    db: Database,
    name: String
): GroupInfo {
    val id = transaction(db) {
        GroupsTable.insertAndGetId {
            it[GroupsTable.name] = name
        }
    }
    
    return GroupInfo(
        id = id.value,
        name = name
    )
}