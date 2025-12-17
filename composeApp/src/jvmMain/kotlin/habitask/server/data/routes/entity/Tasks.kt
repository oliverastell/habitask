package habitask.server.data.routes.entity

import habitask.server.data.ServerBackend
import habitask.server.data.routes.validateAccount
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.entityTasksAssigned(backend: ServerBackend) = get("/{id}/assigned") {
    val account = validateAccount(backend) ?: return@get

    call.respond(backend.dbManager.getAssignedTaskByEntityId(account.id))
}