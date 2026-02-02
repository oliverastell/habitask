package habitask.server.data.routes

import habitask.common.Logger
import habitask.common.data.info.EntityInfo
import habitask.server.data.ServerBackend
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.origin
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingContext

suspend fun RoutingContext.validateAccount(backend: ServerBackend): EntityInfo? {
    val authHeader = call.request.headers[HttpHeaders.AuthenticationInfo]

    if (authHeader == null) {
        call.respondText("No token", status = HttpStatusCode.Unauthorized)
        Logger.warning("User [${call.request.origin.remoteAddress}] tried accessing a restricted route without a token")
        return null
    }

    val accountInfo = backend.dbManager.getEntityByToken(authHeader)
    if (accountInfo == null) {
        call.respondText("Invalid token", status = HttpStatusCode.Forbidden)
        Logger.warning("User [${call.request.origin.remoteAddress}] tried accessing " +
                "a restricted route with an invalid token")
        Logger.info("Users connecting with invalid tokens might be connected to a " +
                "different server on the same IP and port, it is recommended you run your servers on different ports")
        return null
    }

    return accountInfo
}

suspend fun RoutingContext.entityOrSelf(id: String, backend: ServerBackend): EntityInfo? {
    if (id.lowercase() == "self") {
        val account = validateAccount(backend) ?: return null
        return account
    }

    val idInt = id.toIntOrNull() ?: run {
        call.respondText("Malformed id", status = HttpStatusCode.NotFound)
        return null
    }

    val entity = backend.dbManager.getEntityById(idInt) ?: run {
        call.respondText("Invalid entity", status = HttpStatusCode.NotFound)
        return null
    }

    return entity
}