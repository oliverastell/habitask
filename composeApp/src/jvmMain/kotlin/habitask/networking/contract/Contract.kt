package habitask.networking.contract

//import io.ktor.server.request.receiveText
//import io.ktor.server.response.respond
//import io.ktor.server.response.respondText
//import io.ktor.server.routing.Route
//import io.ktor.server.routing.RoutingCall
//import io.ktor.server.routing.RoutingContext
//import io.ktor.server.routing.RoutingHandler
//import io.ktor.server.routing.route
//import kotlin.reflect.safeCast
//
//class ContractContext<T: Contract>(
//    val contract: T,
//    private val parameters: Properties,
//    private val call: RoutingCall
//) {
//    operator fun <T: Any> get(property: Property<T>): T? {
//        val nonBodyParameter = call.parameters[property.name]
//        if (nonBodyParameter != null) {
//            val decoded = ContractCodec.decode(property.cls, nonBodyParameter)
//            return property.cls.safeCast(decoded)
//        } else {
//            return parameters[property]
//        }
//    }
//
//    suspend fun respond(properties: Properties.() -> Unit) {
//        call.respondText(ContractCodec.encode(Properties(properties)))
//    }
//
//    suspend fun respond(properties: Properties) {
//        call.respondText(ContractCodec.encode(properties))
//    }
//}
//
//fun <T: Contract> Route.handleContract(contract: T, body: suspend ContractContext<T>.() -> Unit) {
//    route(contract.contractPath.toRouteString()) {
//        handle {
//            val call = this.call
//            val parameters = call.receiveText()
//
//            ContractContext(
//                contract = contract,
//                parameters = ContractCodec.decode(Properties::class, parameters),
//                call = call
//            ).body()
//        }
//    }
//}