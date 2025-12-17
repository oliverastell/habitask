package habitask.client.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import habitask.client.data.ClientController
import habitask.client.data.filemanagers.ClientFileManager
import habitask.client.ui.menu.HomeMenu
import habitask.client.ui.menu.ServerMenu
import habitask.client.ui.menu.SettingsMenu
import habitask.resources.Res
import habitask.resources.home
import habitask.resources.servers
import habitask.resources.settings
import org.jetbrains.compose.resources.painterResource
//import androidx.navigation.compose.NavHost
import org.jetbrains.compose.ui.tooling.preview.Preview
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
            NavigationBar(
                modifier = Modifier
                    .padding(24.dp, 0.dp, 24.dp, 24.dp)
                    .clip(RoundedCornerShape(100))
            ) {
                NavigationBarItem(
                    selected = currentMenu == CurrentMenu.Settings,
                    icon = { Icon(painterResource(Res.drawable.settings), "Settings Menu") },
                    onClick = { currentMenu = CurrentMenu.Settings }
                )

                NavigationBarItem(
                    selected = currentMenu == CurrentMenu.Home,
                    icon = { Icon(painterResource(Res.drawable.home), "Home Menu") },
                    onClick = { currentMenu = CurrentMenu.Home }
                )

                NavigationBarItem(
                    selected = currentMenu == CurrentMenu.Servers,
                    icon = { Icon(painterResource(Res.drawable.servers), "Servers Menu") },
                    onClick = { currentMenu = CurrentMenu.Servers }
                )
            }
        }
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            when (currentMenu) {
                CurrentMenu.Settings -> SettingsMenu()
                CurrentMenu.Home -> HomeMenu(clientController = clientController)
                CurrentMenu.Servers -> ServerMenu(clientController)
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