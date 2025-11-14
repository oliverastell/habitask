package com.oliverastell.habitask.filemanagers

import kotlinx.io.files.Path
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import java.io.Closeable
import java.sql.DriverManager

data class ServerPaths(
    val database: Path
)

class ServerFileManager(
    val root: Path
) {
    val paths = ServerPaths(
        database = Path(root, "tasks.sql")
    )

    val database = Database.connect("jdbc:h2:${paths.database}", driver = "org.h2.Driver")
}