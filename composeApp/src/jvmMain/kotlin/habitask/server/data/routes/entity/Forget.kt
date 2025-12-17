package habitask.server.data.routes.entity

import habitask.server.data.ServerBackend
import habitask.common.Logger
import habitask.server.data.routes.validateAccount
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete

fun Route.forgetEntity(backend: ServerBackend) = delete("/forget") {
    val user = validateAccount(backend) ?: return@delete

    backend.forgetAccount(user)

    Logger.info("User ${user.name} (${user.id}) has been forgotten")

    call.respondText("Account deleted", status = HttpStatusCode.OK)
}