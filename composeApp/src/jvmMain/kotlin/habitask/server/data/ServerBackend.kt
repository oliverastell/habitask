package habitask.server.data

import habitask.server.data.filemanagers.ServerFileManager
import habitask.server.data.routes.entity.entityInfo
import habitask.server.data.routes.entity.registerAccount
import habitask.common.Logger
import habitask.common.data.info.EntityInfo
import habitask.common.data.info.EntityType
import habitask.common.data.info.TaskAssignmentInfo
import habitask.server.data.commandengine.Command
import habitask.server.data.commandengine.CommandContext
import habitask.server.data.commandengine.parseCommand
import habitask.server.data.commands.funCommands
import habitask.server.data.commands.entityCommand
import habitask.server.data.commands.helpCommand
import habitask.server.data.commands.metaCommands
import habitask.server.data.commands.moveCommand
import habitask.server.data.commands.serverCommands
import habitask.server.data.commands.taskCommand
import habitask.server.data.h2.DatabaseManager
import habitask.server.data.routes.entity.entityTasksAssigned
import habitask.server.data.routes.entity.forgetEntity
import habitask.server.data.routes.task.taskInfo
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class ServerBackend(
    val fm: ServerFileManager
) {
    val dbManager = DatabaseManager(fm.database)
    lateinit var server: EmbeddedServer<*, *>
    lateinit var checkCycleCoroutine: Job

    val commands = mutableListOf<CommandContext.(ServerBackend) -> Unit>(
        CommandContext::entityCommand,
        CommandContext::moveCommand,
        CommandContext::taskCommand,
        CommandContext::funCommands,
        CommandContext::metaCommands,
        CommandContext::serverCommands,
        CommandContext::helpCommand
    )

    private var inputRequest: ((String) -> Unit)? = null

    fun validName(name: String) {
        name.all { it != '@' && it != '"' && it != '\'' && it != '\n' }
    }

    fun requestInput(body: (String) -> Unit) {
        Logger.debug("requesting input")

        inputRequest = { text ->
            body(text)
        }
    }

    fun executeCommand(command: Command, onOutput: (Any) -> Unit) {
        command.execute(onOutput) {
            for (command in commands) {
                command(this@ServerBackend)
            }
        }
    }

    fun executeCommand(command: String, onOutput: (Any) -> Unit) {
        executeCommand(parseCommand(command), onOutput)
    }

    var multilining: String? = null
    fun inputToConsole(text: String, onOutput: (Any) -> Unit) {
        val request = inputRequest

        val text = if (multilining != null) {
            Logger.feedback(if (request == null) "> $text" else text)
            multilining!! + text
        } else {
            Logger.feedback(if (request == null) "$ $text" else text)
            text
        }

        if (text.endsWith("\\")) {
            multilining = text.dropLast(1)
            return
        } else {
            multilining = null
        }

        if (request != null) {
            inputRequest = null
            request.invoke(text)
            return
        }

        executeCommand(text, onOutput)
    }

    init {
        onOpening()
    }

    private fun setupServerConfigs() {
        if (dbManager.areServerConfigsSetup())
            return

        dbManager.setServerConfigs(ServerConfigs(
            name = fm.root.name,
            port = 8080,
            reassignEvery = DateTimeUnit.MONTH
        ))
    }

    fun welcomeMessage() = """
        %${"\n".repeat(10)}
        Welcome to the
        
          ██  ██  ▄▄▄  ▄▄▄▄  ▄▄ ▄▄▄▄▄▄ ▄▄▄   ▄▄▄▄ ▄▄ ▄▄  
          ██████ ██▀██ ██▄██ ██   ██  ██▀██ ███▄▄ ██▄█▀  
          ██  ██ ██▀██ ██▄█▀ ██   ██  ██▀██ ▄▄██▀ ██ ██  
        
        terminal!
        
        Type `help` for a list of commands.
        %
    """.trimIndent().replace("%", "")

    private fun startServer() {
        server = embeddedServer(CIO, port = dbManager.getServerConfigs().port) {
            module()
        }.start()
        Logger.feedback(welcomeMessage())
    }
    private fun stopServer() {
        server.stop(1000)
    }
    private fun startCheckCycle() {
        checkCycleCoroutine = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(60.seconds)
                checkCycle()
            }
        }
        checkCycleCoroutine.start()
    }
    private fun stopCheckCycle() {
        checkCycleCoroutine.cancel()
    }

    private fun onOpening() {
        dbManager.initializeTables()

        setupServerConfigs()
        startServer()
        startCheckCycle()
    }
    fun onClosing() {
        stopCheckCycle()
        stopServer()
    }

    private var publicIp: String? = null
    suspend fun getPublicIp(): String? {
        if (publicIp != null)
            return publicIp

        val client = HttpClient(io.ktor.client.engine.cio.CIO)

        val checkers = listOf(
            "https://checkip.amazonaws.com",
            "https://api.ipify.org",
            "https://icanhazip.com",
            "https://ifconfig.me/ip",
        )

        for (url in checkers) {
            try {
                publicIp = client.get(url).bodyAsText().trim().filter { it.isDigit() || it == '.' }
                break
            } catch (_: Exception) {}
        }

        return publicIp
    }

    @OptIn(ExperimentalTime::class)
    fun reassignTasks() {
        dbManager.deleteAllTaskAssignments()

        val groups = dbManager.getEntitiesWithParent(null)
        val tasks = dbManager.getTasks()

        val shuffledTasks = tasks.shuffled()

        for ((i, task) in shuffledTasks.withIndex()) {
            val groupIdx = groups.size * i / tasks.size
            val group = groups[groupIdx]

            dbManager.newTaskAssignment(
                task.id,
                group.id,
                Clock.System.now().plus(1, task.cycleEvery, TimeZone.currentSystemDefault())
            )
        }
    }

    fun isEntityDescendantOf(entityId: Int, ancestorId: Int): Boolean {
        if (entityId == ancestorId)
            return true

        val parent = (dbManager.getEntityById(entityId) ?: return false).parent
        return if (parent == null)
            false
        else
            isEntityDescendantOf(parent, ancestorId)
    }

    fun getAssignedTasksRecursive(entityId: Int): List<TaskAssignmentInfo>? {
        val parentId = (dbManager.getEntityById(entityId) ?: return null).parent

        return if (parentId != null) {
            dbManager.getTaskAssignmentsByEntityId(entityId) + getAssignedTasksRecursive(parentId)!!
        } else {
            dbManager.getTaskAssignmentsByEntityId(entityId)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun registerAccount(name: String): EntityInfo {
        val account = dbManager.newEntity(
            name,
            null,
            EntityType.User
        )
        return account
    }

    @OptIn(ExperimentalTime::class)
    fun forgetAccount(accountInfo: EntityInfo) {
        dbManager.deleteEntity(accountInfo.id)
    }

    fun checkCycle() {
        Logger.info("Check cycle")
    }

    fun Application.module() {
        install(ContentNegotiation) { json() }
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                Logger.error("500: ${cause.stackTrace.joinToString("\n")}")
                cause.printStackTrace()
                call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
            }
        }

        routing {
            route("/account") {
                entityInfo(this@ServerBackend)
                registerAccount(this@ServerBackend)
                forgetEntity(this@ServerBackend)
                entityTasksAssigned(this@ServerBackend)
            }

            route("/task") {
                taskInfo(this@ServerBackend)
            }

            get("/info") {
                call.respond(dbManager.getServerConfigs().toServerInfo())
            }

            get("/status") {
                call.respondText("Server is active")
            }
        }
    }

    fun restart() {
        stopServer()
        startServer()
    }
}