package habitask.server.data.routes.assignment

import habitask.common.Logger
import habitask.server.data.ServerBackend
import habitask.server.data.routes.validateAccount
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.outsourceAssignment(backend: ServerBackend) = post("/{assignmentId}/outsource") {
    val assignmentId = call.parameters["assignmentId"]?.toInt() ?: return@post

    val accountEntity = validateAccount(backend) ?: return@post

    if (!backend.isAssignmentAssignedToEntity(assignmentId, accountEntity.id)) {
        call.respondText("Cannot modify task that is not assigned to you", status = HttpStatusCode.Forbidden)
        return@post
    }

    Logger.info("${accountEntity.name} outsourced assignment @{$assignmentId}")

    backend.dbManager.setAssignmentEntity(assignmentId, null)

    call.respondText("Task outsourced", status = HttpStatusCode.OK)
}