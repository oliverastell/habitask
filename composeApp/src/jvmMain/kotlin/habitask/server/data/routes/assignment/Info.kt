package habitask.server.data.routes.assignment

import habitask.common.data.info.AssignmentInfo
import habitask.server.data.ServerBackend
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get


fun Route.assignmentInfo(backend: ServerBackend) = get("/{assignmentId}") {
    val id = call.parameters["assignmentId"]?.toInt() ?: return@get

    val assignment = backend.dbManager.getAssignmentById(id) ?: run {
        call.respondText("Invalid task", status = HttpStatusCode.NotFound)
        return@get
    }

    call.respond(assignment)
}