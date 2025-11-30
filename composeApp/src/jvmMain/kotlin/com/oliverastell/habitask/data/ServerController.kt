package com.oliverastell.habitask.data

import com.oliverastell.habitask.data.classes.ServerInfo
import com.oliverastell.habitask.data.classes.UserInfo
import com.oliverastell.habitask.data.filemanagers.ServerFileManager
import com.oliverastell.habitask.data.filemanagers.getUserByToken
import com.oliverastell.habitask.data.filemanagers.initializeTables
import com.oliverastell.habitask.data.filemanagers.newAccess
import com.oliverastell.habitask.data.filemanagers.newGroup
import com.oliverastell.habitask.data.filemanagers.newUser
import com.oliverastell.habitask.data.networking.AccountInfoResponse
import com.oliverastell.habitask.data.networking.NewAccountRequest
import com.oliverastell.habitask.data.networking.NewAccountResponse
import com.oliverastell.habitask.data.networking.ServerInfoResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ServerController(
    val fm: ServerFileManager,
    val serverInfo: ServerInfo
) {
    init {
        initializeTables(fm.database)
    }

    suspend fun RoutingContext.validateUser(): UserInfo? {
        val authHeader = call.request.headers[HttpHeaders.Authorization]

        if (authHeader == null) {
            call.respondText("No token", status = HttpStatusCode.Unauthorized)
            return null
        }

        val user = getUserByToken(fm.database, authHeader);
        if (user == null) {
            call.respondText("Invalid token", status = HttpStatusCode.Unauthorized)
            return null
        }

        return user
    }

    @OptIn(ExperimentalTime::class)
    fun register(name: String): UserInfo {
        val groupName = "$name's Group"

        val group = newGroup(fm.database, name = groupName)

        val user = newUser(
            fm.database,
            name = name,
            groupInfo = group,
            lastOnline = Clock.System.now().epochSeconds
        )

        Logger.info("User connected ${user.name}")

        return user
    }

    fun newRuntime(port: Int = 8080) = embeddedServer(CIO, port = port) {
        install(ContentNegotiation) { json() }
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                Logger.error("500: ${cause.stackTraceToString()}")
                cause.printStackTrace()
                call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
            }
        }

        routing {
            get {
                call.respondText("Install the Habitask client to interact with this server!")
            }

            route("/account") {
                get("/info") {
                    val userInfo = validateUser() ?: return@get
                    call.respond(AccountInfoResponse(userInfo))
                }

                post("/register") {
                    val body = call.receive<NewAccountRequest>()
                    val user = register(body.name)

                    val access = newAccess(fm.database, user)

                    call.respond(NewAccountResponse(
                        userInfo = user,
                        accessInfo = access
                    ))
                }
            }

            get("/info") {
                call.respond(ServerInfoResponse(serverInfo))
            }

            get("/status") {
                call.respond("Server is active")
            }
        }
    }
}