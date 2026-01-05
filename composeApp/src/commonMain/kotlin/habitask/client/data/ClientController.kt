package habitask.client.data

import habitask.common.data.info.ServerInfo
import habitask.client.data.filemanagers.ClientFileManager
import habitask.common.data.info.AssignmentInfo
import habitask.client.data.networking.NewEntityRequest
import habitask.client.data.networking.NewEntityResponse
import habitask.common.Logger
import habitask.common.data.info.EntityInfo
import habitask.common.data.info.TaskInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.InternalAPI
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

class ClientController(
    val fm: ClientFileManager
) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                }
            )
        }
    }

    private fun HttpRequestBuilder.authorize(connection: Connection) {
        headers[HttpHeaders.Authorization] = connection.token ?: error("Weak connection, no token")
    }

    suspend fun updateConnection(connection: Connection) {
        if (isServerOffline(connection)) {
            connection.currentlyUsingBackup = true
        }
    }

    suspend fun isServerOnline(connection: Connection): Boolean {
        return try {
            val response = client.get("${connection.url}/status")
            response.status.isSuccess()
        } catch (e: IOException) {
            false
        } catch (e: Exception) {
            Logger.warning("$e")
            false
        }
    }

    suspend fun isServerOffline(connection: Connection) = !isServerOnline(connection)

    @OptIn(InternalAPI::class)
    suspend fun newEntity(connection: Connection, newAccountRequest: NewEntityRequest): NewEntityResponse {
        return client.post("${connection.url}/entity/register") {
            contentType(ContentType.Application.Json)
            setBody(newAccountRequest)
        }.body<NewEntityResponse>()
    }

    /**
     * Returns true if successful
      */
    suspend fun forgetAccount(connection: Connection): Boolean {
        return client.delete("${connection.url}/entity/forget") {
            authorize(connection)
        }.status.isSuccess()
    }

    suspend fun getEntityInfo(connection: Connection): EntityInfo {
        val response = client.get("${connection.url}/entity/self/info") {
            contentType(ContentType.Application.Json)
            authorize(connection)
        }

        return response.body<EntityInfo>()
    }

    suspend fun getServerInfo(connection: Connection): ServerInfo {
        return client.get("${connection.url}/info") {
            contentType(ContentType.Application.Json)
        }.body<ServerInfo>()
    }

    suspend fun getTaskById(connection: Connection, taskId: Int): TaskInfo {
        return client.get("${connection.url}/task/$taskId") {
            contentType(ContentType.Application.Json)
            authorize(connection)
        }.body<TaskInfo>()
    }

    suspend fun getAssignments(connection: Connection): List<AssignmentInfo> {
        return client.get("${connection.url}/entity/self/assigned") {
            contentType(ContentType.Application.Json)
            authorize(connection)
        }.body<List<AssignmentInfo>>()
    }

    suspend fun outsourceAssignment(connection: Connection, assignmentId: Int): Boolean {
        return client.post("${connection.url}/assignment/$assignmentId/outsource") {
            contentType(ContentType.Application.Json)
            authorize(connection)
        }.status.isSuccess()
    }

    suspend fun completeAssignment(connection: Connection, assignmentId: Int): Boolean {
        return client.post("${connection.url}/assignment/$assignmentId/complete") {
            contentType(ContentType.Application.Json)
            authorize(connection)
        }.status.isSuccess()
    }
}