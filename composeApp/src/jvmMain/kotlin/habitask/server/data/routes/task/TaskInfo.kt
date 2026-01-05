package habitask.server.data.routes.task

import habitask.server.data.ServerBackend
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get


fun Route.taskInfo(backend: ServerBackend) = get("/{taskId}") {
    val id = call.parameters["taskId"]?.toInt() ?: return@get

    val task = backend.dbManager.getTaskById(id) ?: return@get

    call.respond(task)
}