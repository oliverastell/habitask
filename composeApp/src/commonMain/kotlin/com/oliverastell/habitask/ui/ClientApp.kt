package com.oliverastell.habitask.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.oliverastell.habitask.data.ClientController
import com.oliverastell.habitask.data.classes.TaskInfo
import com.oliverastell.habitask.data.filemanagers.ClientFileManager
import com.oliverastell.habitask.ui.menu.HomeMenu
import com.oliverastell.habitask.ui.menu.ServerMenu
import com.oliverastell.habitask.ui.menu.SettingsMenu
//import androidx.navigation.compose.NavHost
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

enum class CurrentMenu {
    Settings,
    Home,
    Servers
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun ClientApp() {
    var currentMenu by remember { mutableStateOf(CurrentMenu.Home) }
    val fm = ClientFileManager.defaultManager
    val clientController by remember { mutableStateOf(ClientController(fm)) }


    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentMenu == CurrentMenu.Settings,
                    icon = { Icon(Icons.Default.Settings, "Settings Menu") },
                    onClick = { currentMenu = CurrentMenu.Settings }
                )

                NavigationBarItem(
                    selected = currentMenu == CurrentMenu.Home,
                    icon = { Icon(Icons.Default.Home, "Home Menu") },
                    onClick = { currentMenu = CurrentMenu.Home }
                )

                NavigationBarItem(
                    selected = currentMenu == CurrentMenu.Servers,
                    icon = { Icon(Icons.Default.Wifi, "Servers Menu") },
                    onClick = { currentMenu = CurrentMenu.Servers }
                )
            }
        }
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            when (currentMenu) {
                CurrentMenu.Settings -> SettingsMenu()
                CurrentMenu.Home -> HomeMenu(List(4) { i ->
                    TaskInfo(
                        name = "Bogo$i",
                        dueTime = 0,
                        description = "$i"
                    )
                })
                CurrentMenu.Servers -> ServerMenu(
                    clientController = clientController,
                    fm = fm
                )
            }
        }
    }

//    Column {
//
//        Text("Hi")
//
//        var showPopup by remember { mutableIntStateOf(0) }
//
//
//        if (showPopup == 1) {
//            DialogFrame(
//                dialogTitle = "Wah",
//                onDismissRequest = { showPopup = 0 }
//            ) {
//                Text("Dialogue")
//            }
//        }
//
//        NavigationBar {
//            Button(onClick = { showPopup = 0 }) {
//
//            }
//            Button(onClick = { showPopup = 1 }) {
//
//            }
//            Button(onClick = {}) {
//
//            }
//        }
//
//        ChannelCard(modifier = Modifier.fillMaxWidth())
//        Spacer(Modifier.height(16.dp))
//        TaskCard(modifier = Modifier.fillMaxWidth())
//        Spacer(Modifier.height(16.dp))
//        ChannelCard(modifier = Modifier.fillMaxWidth())
//        Spacer(Modifier.height(16.dp))
//        TaskCard(modifier = Modifier.fillMaxWidth())
//    }
//    var ipText by remember { mutableStateOf("") }
//    var portText by remember { mutableStateOf("") }
////
//    Column {
//        Text("IP")
//        TextField(
//            ipText,
//            onValueChange = {
//                ipText = it
//            },
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(Modifier.height(8.dp))
//        Text("Port")
//        TextField(
//            portText,
//            onValueChange = {
//                portText = it
//            },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Button()
//    }



//    var showContent by remember { mutableStateOf(false) }
//    Column(
//        modifier = Modifier
//            .background(MaterialTheme.colorScheme.primaryContainer)
//            .safeContentPadding()
//            .fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        Button(onClick = { showContent = !showContent }) {
//            Text("Click me!")
//        }
//        AnimatedVisibility(showContent) {
//            val greeting = remember { Greeting().greet() }
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                Image(painterResource(Res.drawable.compose_multiplatform), null)
//                Text("Compose: $greeting")
//            }
//        }
//    }
}