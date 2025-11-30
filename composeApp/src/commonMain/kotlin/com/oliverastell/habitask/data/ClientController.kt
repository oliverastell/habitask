package com.oliverastell.habitask.data

import com.oliverastell.habitask.data.classes.ServerInfo
import com.oliverastell.habitask.data.classes.UserInfo
import com.oliverastell.habitask.data.filemanagers.ClientFileManager
import com.oliverastell.habitask.data.networking.AccountInfoResponse
import com.oliverastell.habitask.data.networking.NewAccountRequest
import com.oliverastell.habitask.data.networking.NewAccountResponse
import com.oliverastell.habitask.data.networking.ServerInfoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.typeInfo
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
    suspend fun newAccount(connection: Connection, newAccountRequest: NewAccountRequest): NewAccountResponse {
        return client.post("${connection.url}/account/register") {
            contentType(ContentType.Application.Json)
            setBody(newAccountRequest)
        }.body<NewAccountResponse>()
    }

    suspend fun getUserInfo(connection: Connection): UserInfo {
        val response = client.get("${connection.url}/account/info") {
            contentType(ContentType.Application.Json)
            headers[HttpHeaders.Authorization] = connection.token ?: error("Weak connection, no token")
        }

        Logger.debug(response.bodyAsText())
        return response.body<AccountInfoResponse>().userInfo
    }

    suspend fun getServerInfo(connection: Connection): ServerInfo {
        return client.get("${connection.url}/info") {
            contentType(ContentType.Application.Json)
        }.body<ServerInfoResponse>().serverInfo
    }
}