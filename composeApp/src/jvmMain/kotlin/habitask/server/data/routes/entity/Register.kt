package habitask.server.data.routes.entity

import habitask.server.data.ServerBackend
import habitask.client.data.networking.NewEntityRequest
import habitask.client.data.networking.NewEntityResponse
import habitask.common.Logger
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.registerEntity(backend: ServerBackend) = post("/register") {
    val body = call.receive<NewEntityRequest>()
    val account = backend.registerUser(body.name)

    val access = backend.dbManager.newAccess(account.id)

    val response = NewEntityResponse(
        entityInfo = account,
        accessInfo = access
    )

    Logger.info("User registered with name ${account.name} (${account.id})")

    call.respond(response)
}