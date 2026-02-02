package habitask.client.ui.menu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import habitask.client.data.ClientController
import habitask.client.data.Connection
import habitask.common.Logger
import habitask.common.data.info.ServerInfo
import habitask.client.data.networking.NewEntityRequest
import habitask.client.ui.elements.ServerCard
import habitask.client.ui.menu.dialog.AddServerDialog
import habitask.client.ui.menu.dialog.DisconnectServerDialog
import habitask.common.data.info.EntityInfo
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime



@OptIn(ExperimentalTime::class)
@Composable
fun ServerMenu(
    clientController: ClientController,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    var update by remember { mutableIntStateOf(0) }

    HomeAndServerMenuCommon(
        modifier = modifier.fillMaxSize().padding(16.dp),
        rowContent = {
            var addingServer by remember { mutableStateOf(false) }

            TextButton(onClick = { addingServer = true }) {
                Text("Add Server")
            }

            if (addingServer)
                AddServerDialog(
                    onDismissRequest = { addingServer = false },
                    onServerAdded = { displayName, address, backupAddress, port -> scope.launch {
                            val connection = Connection(
                                address = address,
                                backupAddress = backupAddress,
                                port = port,
                            )

                            clientController.updateConnection(connection)
                            if (clientController.isServerOffline(connection)) {
                                Logger.error("Cannot connect to offline server")
                                return@launch
                            }

                            addingServer = false

                            val response = clientController.newEntity(connection, NewEntityRequest(
                                name = displayName
                            ))

                            clientController.fm.addConnection(connection.withToken(response.accessInfo.token))
                            update++
                    } }
                )
        }
    ) {
        key(update) {
            LazyColumn {
                items(clientController.fm.getConnections()) { connection ->
                    var serverOnline by remember { mutableStateOf<Boolean?>(null) }
                    var authenticated by remember { mutableStateOf(false) }
                    var serverInfo by remember { mutableStateOf<ServerInfo?>(null) }
                    var accountInfo by remember { mutableStateOf<EntityInfo?>(null) }
                    var parentGroup by remember { mutableStateOf<EntityInfo?>(null) }

                    LaunchedEffect(Unit) {
                        clientController.updateConnection(connection)
                        if (clientController.isServerOffline(connection)) {
                            Logger.debug("server offline")
                            serverOnline = false
                            return@LaunchedEffect
                        }

                        serverInfo = clientController.getServerInfo(connection)

                        authenticated = clientController.isAuthenticated(connection)
                        if (!authenticated) {
                            Logger.debug("not authenticated")
                            serverOnline = true
                            return@LaunchedEffect
                        }

                        accountInfo = clientController.getEntityInfo(connection)

                        if (accountInfo!!.parent != null)
                            clientController.getEntityInfo(connection, accountInfo!!.parent!!)

                        serverOnline = true
                    }

                    val serverName: String
                    val yourName: String
                    val groupName: String
                    when (serverOnline) {
                        true if authenticated -> {
                            serverName = serverInfo!!.name
                            yourName = accountInfo!!.name
                            groupName = parentGroup?.name ?: "Not in a group"
                        }
                        true -> {
                            serverName = serverInfo!!.name
                            yourName = "Could not authenticate"
                            groupName = "Could not authenticate"
                        }
                        false -> {
                            serverName = "Could not fetch (offline)"
                            yourName = "Could not fetch (offline)"
                            groupName = "Could not fetch (offline)"
                        }
                        null -> {
                            serverName = "Loading"
                            yourName = "Loading"
                            groupName = "Loading"
                        }
                    }

                    var disconnecting by remember { mutableStateOf(false) }

                    if (disconnecting) {
                        DisconnectServerDialog(
                            onDismissRequest = { disconnecting = false },
                            willLikelyReplicate = serverOnline == true && authenticated,
                            onServerDisconnected = {
                                runBlocking {
                                    clientController.fm.removeConnection(connection)
                                    update++

                                    clientController.updateConnection(connection)
                                    if (clientController.isServerOffline(connection)) {
                                        return@runBlocking
                                    }

                                    val forgotten = clientController.forgetAccount(connection)
                                    if (!forgotten) {
                                        Logger.error("Server did not forget user")
                                        return@runBlocking
                                    }
                                }
                            }
                        )
                    }


                    ServerCard(
                        serverName = serverName,
                        serverUrl = connection.url,
                        yourName = yourName,
                        yourGroupName = groupName,
                        onDisconnect = {
                            Logger.debug("onDisconnect")
                            disconnecting = true
                        }
                    )
                }
            }
        }
    }
}