package habitask.server.data.routes.task

import habitask.server.data.ServerBackend
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get


fun Route.taskInfo(backend: ServerBackend) = get("/{id}") {
    val id = call.parameters["id"]?.toInt() ?: return@get
    call.respond(backend.dbManager.getTaskById(id))
}