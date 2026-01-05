package habitask.server.data.filemanagers

import kotlinx.io.files.Path
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

data class ServerPaths(
    val database: Path
)

class ServerFileManager(
    val root: Path
) {
    val paths = ServerPaths(
        database = Path(root, "tasks.sql")
    )


    var database: Database? = null

    fun openDatabase() {
        database = Database.connect("jdbc:h2:${paths.database};DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
    }

    fun closeDatabase() {
        database = null
    }

}