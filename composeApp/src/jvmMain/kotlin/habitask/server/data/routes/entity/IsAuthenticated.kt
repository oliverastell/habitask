package habitask.server.data.routes.entity

import habitask.server.data.ServerBackend
import habitask.server.data.routes.entityOrSelf
import habitask.server.data.routes.getAuthenticated
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.isAuthenticated(backend: ServerBackend) = get("/{entityId}/isAuthenticated") {
    val entity = entityOrSelf(call.parameters["entityId"]!!, backend) ?: return@get
    val self = getAuthenticated(backend) ?: return@get

    if (self.id != entity.id)
        return@get

    call.respondText("Account is authenticated", status = HttpStatusCode.OK)
}