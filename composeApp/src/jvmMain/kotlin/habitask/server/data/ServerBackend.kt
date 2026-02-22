package habitask.server.data

import habitask.server.data.filemanagers.ServerFileManager
import habitask.server.data.routes.entity.entityInfo
import habitask.server.data.routes.entity.registerEntity
import habitask.common.Logger
import habitask.common.data.info.EntityInfo
import habitask.common.data.info.EntityInfo.EntityType
import habitask.common.data.info.AssignmentInfo
import habitask.server.data.commandengine.Command
import habitask.server.data.commandengine.CommandContext
import habitask.server.data.commandengine.MalformedCommandException
import habitask.server.data.commandengine.parseCommand
import habitask.server.data.commands.miscCommands
import habitask.server.data.commands.helpCommand
import habitask.server.data.commands.entityCommands
import habitask.server.data.commands.metaCommands
import habitask.server.data.commands.serverCommands
import habitask.server.data.commands.taskCommand
import habitask.server.data.h2.DatabaseManager
import habitask.server.data.routes.assignment.assignmentInfo
import habitask.server.data.routes.assignment.completeAssignment
import habitask.server.data.routes.assignment.outsourceAssignment
import habitask.server.data.routes.entity.entityTasksAssigned
import habitask.server.data.routes.entity.deleteEntity
import habitask.server.data.routes.entity.isAuthenticated
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
import org.jetbrains.exposed.exceptions.ExposedSQLException
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class ServerBackend(
    val fm: ServerFileManager
) {
    val dbManager = DatabaseManager(fm)
    lateinit var server: EmbeddedServer<*, *>
    lateinit var checkCycleCoroutine: Job

    val commandDefinitions = mutableListOf<CommandContext.(ServerBackend) -> Unit>(
        CommandContext::helpCommand,
        CommandContext::entityCommands,
        CommandContext::taskCommand,
        CommandContext::miscCommands,
        CommandContext::metaCommands,
        CommandContext::serverCommands,
    )

    private var inputRequest: ((String) -> Unit)? = null

    fun requestInput(body: (String) -> Unit) {
        Logger.debug("requesting input")

        inputRequest = { text ->
            body(text)
        }
    }

    fun executeCommand(command: Command, onOutput: (Any) -> Unit) {
        try {
            command.execute(onOutput) {
                for (commandDefinition in commandDefinitions) {
                    commandDefinition(this@ServerBackend)
                }
            }
        } catch (_: MalformedCommandException) {
            Logger.error("malformed command: $command")
            onOutput("malformed command, type 'help' for a list of commands")
        } catch (e: Exception) {
            Logger.error("error occurred: ${e.message}")
            onOutput("this command ran into an error, type 'help' for a list of commands")
        }
    }

    fun executeCommand(command: String, onOutput: (Any) -> Unit) {
        executeCommand(parseCommand(command), onOutput)
    }

    var multilining: String? = null
    fun inputToConsole(text: String, onOutput: (Any) -> Unit) {
        val request = inputRequest

        val text = if (multilining != null) {
            onOutput(if (request == null) "> $text" else text)
            multilining!! + text
        } else {
            onOutput(if (request == null) "$ $text" else text)
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
            reassignEvery = DateTimeUnit.MONTH,
            nextReassignment = Clock.System.now().plus(1, DateTimeUnit.MONTH, TimeZone.currentSystemDefault())
        ))
    }

    fun welcomeMessage() = """
        %${"\n".repeat(10)}
        Welcome to the
        
          ██  ██  ▄▄▄  ▄▄▄▄  ▄▄ ▄▄▄▄▄▄ ▄▄▄   ▄▄▄▄ ▄▄ ▄▄  
          ██████ ██▀██ ██▄██ ██   ██  ██▀██ ███▄▄ ██▄█▀  
          ██  ██ ██▀██ ██▄█▀ ██   ██  ██▀██ ▄▄██▀ ██ ██  
        
        terminal!
        
        Type 'help' for a list of commands.
        %
    """.trimIndent().replace("%", "")

    private fun startServer() {
        server = embeddedServer(CIO, port = dbManager.getServerConfigs().port) {
            module()
        }.start()
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
        fm.openDatabase()

        dbManager.initializeTables()
        setupServerConfigs()

        startServer()
        startCheckCycle()

        Logger.info("Server opened on port: ${dbManager.getServerConfigs().port}")
    }
    fun onClosing() {
        Logger.info("Server closing on port: ${dbManager.getServerConfigs().port}")

        stopCheckCycle()
        stopServer()
        fm.closeDatabase()
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
        Logger.info("All tasks have been reassigned")

        dbManager.deleteAllAssignments()

        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()


        // Shuffling groups ensures the order in which people are assigned to is random
        val groups = dbManager.getEntitiesWithParent(null).shuffled()

        if (groups.isEmpty())
            return

        // Shuffling tasks ensures the tasks that are assigned are random
        val tasks = dbManager.getTasks().shuffled()

        var groupIdx = 0
        for (task in tasks) {
            groupIdx++
            groupIdx %= groups.size

            dbManager.newAssignment(
                task.id,
                groups[groupIdx].id,
                now.plus(1, task.dueAfter, timeZone),
                now.plus(1, task.cycleEvery, timeZone)
            )
        }
    }

    fun isEntityDescendantOf(entityId: Int, ancestorId: Int?): Boolean {
        if (ancestorId == null)
            return true

        if (entityId == ancestorId)
            return true

        val parent = (dbManager.getEntityById(entityId) ?: return false).parent
        return if (parent == null)
            false
        else
            isEntityDescendantOf(parent, ancestorId)
    }

    fun isAssignmentAssignedToEntity(assignmentId: Int, entityId: Int): Boolean {
        Logger.debug("get assignment $assignmentId")

        val assignment = dbManager.getAssignmentById(assignmentId) ?: return false

        val assignmentHolder = assignment.entityId

        return isEntityDescendantOf(entityId, assignmentHolder)
    }

    fun getAssignments(entityId: Int): List<AssignmentInfo>? {
        val parentId = (dbManager.getEntityById(entityId) ?: return null).parent

        if (parentId == null) {
            return dbManager.getAssignmentsByEntityId(entityId) + dbManager.getAssignmentsByEntityId(null)
        }

        return dbManager.getAssignmentsByEntityId(entityId) + getAssignments(parentId)!!
    }

    @OptIn(ExperimentalTime::class)
    fun registerUser(name: String): EntityInfo {
        val account = dbManager.newEntity(
            name,
            null,
            EntityType.User
        )
        return account
    }

    fun autoReassignTasks() {
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()

        val configs = dbManager.getServerConfigs()
        val nextReassignment = configs.nextReassignment

        if (now > nextReassignment) {
            reassignTasks()

            val updatedConfigs = configs.copy(
                nextReassignment = configs.nextReassignment.plus(1, configs.reassignEvery, timeZone)
            )

            dbManager.setServerConfigs(updatedConfigs)
        }
    }

    fun autoCycleTasks() {
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()

        for (entity in dbManager.getEntities()) {
            for (assignment in dbManager.getAssignmentsByEntityId(entity.id)) {
                val task = dbManager.getTaskById(assignment.taskId)!!

                if (now > assignment.cycleTime) {
                    dbManager.deleteAssignment(assignment.id)
                    dbManager.newAssignment(
                        assignment.id,
                        assignment.entityId,
                        assignment.dueTime.plus(1, task.dueAfter, timeZone),
                        assignment.cycleTime.plus(1, task.cycleEvery, timeZone)
                    )
                }
            }
        }
    }

    fun checkCycle() {
        autoReassignTasks()
        autoCycleTasks()

        Logger.info("Check cycle")
    }

    fun Application.module() {
        // Ktor plugin, allowing serialized data structures to be sent over network
        install(ContentNegotiation) { json() }
        // Ktor plugin, automatically responds to the client if a server runs into an unhandled exception
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                Logger.error("500: ${cause.stackTrace.joinToString("\n")}")
                cause.printStackTrace()
                call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
            }
        }

        routing {
            route("/entity") {
                entityInfo(this@ServerBackend)
                registerEntity(this@ServerBackend)
                deleteEntity(this@ServerBackend)
                entityTasksAssigned(this@ServerBackend)
                isAuthenticated(this@ServerBackend)
            }

            route("/task") {
                taskInfo(this@ServerBackend)
            }

            route("/assignment") {
                assignmentInfo(this@ServerBackend)
                completeAssignment(this@ServerBackend)
                outsourceAssignment(this@ServerBackend)
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
        onClosing()
        onOpening()
    }
}