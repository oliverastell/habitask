package habitask.server.data.routes.entity

import habitask.server.data.ServerBackend
import habitask.client.data.networking.NewAccountRequest
import habitask.client.data.networking.NewAccountResponse
import habitask.common.Logger
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.registerAccount(backend: ServerBackend) = post("/register") {
    val body = call.receive<NewAccountRequest>()
    val account = backend.registerAccount(body.name)

    val access = backend.dbManager.newAccountAccess(account.id)

    val response = NewAccountResponse(
        entityInfo = account,
        accessInfo = access
    )

    Logger.info("User registered with name ${account.name} (${account.id})")

    call.respond(response)
}