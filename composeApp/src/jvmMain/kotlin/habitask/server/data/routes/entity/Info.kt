package habitask.server.data.routes.entity

import habitask.server.data.ServerBackend
import habitask.server.data.routes.entityOrSelf
import habitask.server.data.routes.validateAccount
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get


fun Route.entityInfo(backend: ServerBackend) = get("/{entityId}/info") {
    val entity = entityOrSelf(call.parameters["entityId"]!!, backend) ?: return@get

    call.respond(entity)
}