package habitask.networking.contract

import androidx.compose.ui.util.fastJoinToString
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.safeCast


//object ContractCodec {
//    inline fun <reified T: Any> encode(value: T): String {
//        return Json.encodeToString(value)
//    }
//
//    @OptIn(InternalSerializationApi::class)
//    fun <T: Any> decode(cls: KClass<T>, value: String): T {
//        return Json.decodeFromString(cls.serializer(), value)
//    }
//}
//
//
//// Actual code
//@Serializable
//data class Property<T: Any>(val name: String, val cls: KClass<T>)
//inline fun <reified T: Any> Property(name: String) = Property(name, T::class)
//
//@Serializable
//class Properties {
//    private val properties: MutableMap<Property<*>, String> = mutableMapOf()
//
//    fun remove(property: Property<*>) {
//        properties.remove(property)
//    }
//
//    operator fun set(property: Property<*>, value: Any) {
//        properties[property] = ContractCodec.encode(value)
//    }
//
//    operator fun <T: Any> get(property: Property<T>): T? {
//        val encoded = properties[property] ?: return null
//        return property.cls.safeCast(ContractCodec.decode(property.cls, encoded))
//    }
//}
//
//fun Properties(builderAction: Properties.() -> Unit): Properties {
//    val properties = Properties()
//    properties.builderAction()
//    return properties
//}
//
//class ContractRequestContext(val contract: Contract) {
//    val properties = Properties()
//
//    fun remove(property: Property<*>) {
//        properties.remove(property)
//    }
//    operator fun set(property: Property<*>, value: Any) {
//        properties[property] = value
//    }
//    operator fun <T: Any> get(property: Property<T>): T? = properties[property]
//}
//
//class ContractPath private constructor(private val segments: List<Segment>) {
//    private sealed interface Segment {
//        class TextSegment(val text: String) : Segment
//        class VariableSegment(val property: Property<*>) : Segment
//    }
//
//    constructor(vararg segment: Any) : this(
//        segment.map {
//            when (it) {
//                is String -> Segment.TextSegment(it)
//                is Property<*> -> Segment.VariableSegment(it)
//                is Segment -> it
//                else -> Segment.TextSegment(it.toString())
//            }
//        }
//    )
//
//    fun toRouteString() = segments.fastJoinToString {
//        when (it) {
//            is Segment.TextSegment -> it.text
//            is Segment.VariableSegment -> "{${it.property.name}}"
//        }
//    }
//
//    fun toRequestString(properties: Properties, consumeProperties: Boolean = false): String {
//        return buildString {
//            for (segment in segments) {
//                append(when (segment) {
//                    is Segment.TextSegment -> segment.text
//                    is Segment.VariableSegment -> {
//                        val encoded = ContractCodec.encode(properties[segment.property] ?: "")
//                        if (consumeProperties)
//                            properties.remove(segment.property)
//                        encoded
//                    }
//                })
//            }
//        }
//    }
//}
//
//data class ContractResponse(
//    val status: HttpStatusCode,
//    private val properties: Properties
//) {
//    fun onSuccess(body: (Properties) -> Unit) {
//        if (status.isSuccess()) body(properties)
//    }
//}
//
//abstract class Contract(val method: HttpMethod) {
//    abstract val contractPath: ContractPath
//
//    suspend operator fun invoke(
//        client: HttpClient,
//        url: String,
//        block: ContractRequestContext.() -> Unit
//    ): ContractResponse {
//        val ctx = ContractRequestContext(this)
//        ctx.block()
//
//        val response = client.request(url + contractPath.toRequestString(ctx.properties, true)) {
//            this.method = this@Contract.method
//            setBody(ContractCodec.encode(ctx.properties))
//        }
//
//        return ContractResponse(
//            status = response.status,
//            properties = ContractCodec.decode(Properties::class, response.bodyAsText())
//        )
//    }
//}
//
//abstract class GetContract : Contract(HttpMethod.Get)
//abstract class PostContract : Contract(HttpMethod.Post)
//abstract class PatchContract : Contract(HttpMethod.Patch)
//abstract class DeleteContract : Contract(HttpMethod.Delete)
//
//
