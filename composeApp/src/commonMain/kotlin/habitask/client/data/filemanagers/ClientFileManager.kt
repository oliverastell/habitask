package habitask.client.data.filemanagers

import habitask.client.data.Connection
import habitask.client.data.filemanagers.json.ConnectionsFile
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.io.decodeFromSource
import kotlinx.serialization.json.io.encodeToSink
import me.sujanpoudel.utils.paths.appDataDirectory

data class ClientPaths(
    val connections: Path
)

class ClientFileManager(
    val root: Path
) {
    companion object {
        val defaultManager = ClientFileManager(appDataDirectory("habitask"))
    }

    val paths = ClientPaths(
        connections = Path(root, "connections.json")
    )

    private var cachedConnectionsFile: ConnectionsFile? = null

    init {
        if (!SystemFileSystem.exists(paths.connections))
            save(ConnectionsFile(listOf()))
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun getConnections(): List<Connection> {
        cachedConnectionsFile?.let { return it.connections }

        val connectionsFile: ConnectionsFile = try {
            val connectionsFile = SystemFileSystem.source(paths.connections).buffered().use {
                Json.Default.decodeFromSource<ConnectionsFile>(it)
            }
            cachedConnectionsFile = connectionsFile
            connectionsFile
        } catch (e: SerializationException) {
            val connectionsFile = ConnectionsFile(listOf())
            save(connectionsFile)
            connectionsFile
        }

        return connectionsFile.connections
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun save(connectionsFile: ConnectionsFile) {
        cachedConnectionsFile = connectionsFile

        SystemFileSystem.sink(paths.connections).buffered().use {
            Json.encodeToSink(connectionsFile, it)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun addConnection(connection: Connection) {
        val connectionsFile = ConnectionsFile(
            connections = getConnections() + connection
        )

        save(connectionsFile)

        cachedConnectionsFile = connectionsFile
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun removeConnection(connection: Connection) {
        val connectionsFile = ConnectionsFile(
            connections = getConnections() - connection
        )

        save(connectionsFile)

        cachedConnectionsFile = connectionsFile
    }
}