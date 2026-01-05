package habitask.server.data.routes.entity

import habitask.server.data.ServerBackend
import habitask.common.Logger
import habitask.server.data.routes.entityOrSelf
import habitask.server.data.routes.validateAccount
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete

fun Route.deleteEntity(backend: ServerBackend) = delete("/{entityId}/delete") {
    val entity = entityOrSelf(call.parameters["entityId"]!!, backend) ?: return@delete
    val self = validateAccount(backend) ?: return@delete

    if (self.id != entity.id)
        return@delete

    backend.dbManager.deleteEntity(entity.id)

    Logger.info("Entity ${entity.name} (${entity.id}) has been deleted")

    call.respondText("Account deleted", status = HttpStatusCode.OK)
}