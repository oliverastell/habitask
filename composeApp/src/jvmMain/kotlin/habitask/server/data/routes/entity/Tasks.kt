package habitask.server.data.routes.entity

import habitask.server.data.ServerBackend
import habitask.server.data.routes.entityOrSelf
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.entityTasksAssigned(backend: ServerBackend) = get("/{entityId}/assigned") {
    val entity = entityOrSelf(call.parameters["entityId"]!!, backend) ?: return@get

    call.respond(backend.getAssignments(entity.id)!!)
}