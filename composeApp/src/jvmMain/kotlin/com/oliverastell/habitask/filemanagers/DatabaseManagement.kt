package com.oliverastell.habitask.filemanagers

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


object Groups : IntIdTable("groups") {
    val groupName = text("groupName")
}

object Users : IntIdTable("users") {
    val token = text("token")
    val name = text("name")
    val lastOnline = double("last_online")
    val group = reference("group", Groups.id)
}

fun initializeTables(db: Database) {
    transaction(db) {
        SchemaUtils.create(Users)
    }
    transaction(db) {
        SchemaUtils.create(Groups)
    }
}