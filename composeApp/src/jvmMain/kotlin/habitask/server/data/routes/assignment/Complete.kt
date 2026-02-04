package habitask.server.data.routes.assignment

import habitask.common.Logger
import habitask.server.data.ServerBackend
import habitask.server.data.routes.getAuthenticated
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.completeAssignment(backend: ServerBackend) = post("/{assignmentId}/complete") {
    val assignmentId = call.parameters["assignmentId"]?.toInt() ?: return@post

    val accountEntity = getAuthenticated(backend) ?: return@post

    if (!backend.isAssignmentAssignedToEntity(assignmentId, accountEntity.id)) {
        call.respondText("Cannot modify task that is not assigned to you", status = HttpStatusCode.Forbidden)
        return@post
    }

    Logger.info("${accountEntity.name} completed assignment @{$assignmentId}")

    backend.dbManager.deleteAssignment(assignmentId)

    call.respondText("Task completed", status = HttpStatusCode.OK)
}