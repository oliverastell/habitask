package com.oliverastell.habitask.ui.menu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.oliverastell.habitask.data.ClientController
import com.oliverastell.habitask.data.Connection
import com.oliverastell.habitask.data.Logger
import com.oliverastell.habitask.data.classes.ServerInfo
import com.oliverastell.habitask.data.classes.UserInfo
import com.oliverastell.habitask.data.filemanagers.ClientFileManager
import com.oliverastell.habitask.data.networking.NewAccountRequest
import com.oliverastell.habitask.ui.elements.ServerCard
import com.oliverastell.habitask.ui.menu.dialog.AddServerDialog
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ServerMenu(
    clientController: ClientController,
    fm: ClientFileManager,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    HomeAndServerMenuCommon(
        modifier = modifier.fillMaxSize(),
        columnContent = {
            items(fm.getConnections()) { connection ->
                var serverOnline by remember { mutableStateOf<Boolean?>(null) }
                var serverInfo by remember { mutableStateOf<ServerInfo?>(null) }
                var userInfo by remember { mutableStateOf<UserInfo?>(null) }

                LaunchedEffect(Unit) {
                    if (clientController.isServerOffline(connection)) {
                        Logger.debug("server offline")
                        serverOnline = false
                        return@LaunchedEffect
                    }

                    serverInfo = clientController.getServerInfo(connection)
                    userInfo = clientController.getUserInfo(connection)
                    serverOnline = true
                }

                if (serverOnline == true) {
                    ServerCard(
                        serverName = serverInfo?.name ?: "Loading",
                        serverUrl = connection.url,
                        yourName = userInfo?.name ?: "Loading",
                        yourGroupName = "Placeholder"
                    )
                } else if (serverOnline == false) {
                    ServerCard(
                        serverName = "Server Offline",
                        serverUrl = connection.url,
                        yourName = "Could not resolve",
                        yourGroupName = "Could not resolve"
                    )
                }
            }
        },
        rowContent = {
            var addingServer by remember { mutableStateOf(false) }

            TextButton(onClick = { addingServer = true }) {
                Text("Add Server")
                if (addingServer)
                    AddServerDialog(
                        onDismissRequest = { addingServer = false },
                        onServerAdded = { displayName, address, port ->
                            scope.launch {
                                val connection = Connection(
                                    address = address,
                                    port = port,
                                )

                                if (clientController.isServerOffline(connection))
                                    return@launch

                                val response = clientController.newAccount(connection, NewAccountRequest(
                                    name = displayName
                                ))

                                fm.addConnection(connection.withToken(response.accessInfo.token))
                            }
                        }
                    )
            }
        }
    )
}