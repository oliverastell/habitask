package com.oliverastell.habitask.server

import com.oliverastell.habitask.filemanagers.ServerFileManager
import com.oliverastell.habitask.filemanagers.initializeTables
import com.oliverastell.habitask.networking.NewAccountResponse
import io.ktor.client.request.request
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.io.files.Path


class Server(val workingDirectory: Path) {
    val fileManager = ServerFileManager(workingDirectory)

    fun initialize() {
        initializeTables(fileManager.database)
    }

    suspend fun RoutingContext.newAccount() {
        val userName = call.parameters["name"] ?: return

        call.respond(NewAccountResponse(
            userToken = "hello",
            userName = userName
        ))
    }

    fun open() {
        initialize()

        embeddedServer(CIO) {
            routing {
                post("/newAccount") { newAccount() }
            }
        }
    }

}