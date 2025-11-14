package com.oliverastell.habitask.menuapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oliverastell.habitask.filemanagers.DesktopFileManager
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import java.io.File
import javax.swing.JFileChooser

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MenuButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(4.dp),
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        onClick = onClick,
        color = Color.Transparent,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            content = content,
        )
    }
}

@Composable
fun DefaultMenuButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    MenuButton(
        onClick = onClick,
        contentPadding = PaddingValues(8.dp, 0.dp),
        modifier = modifier.fillMaxWidth().height(32.dp)
    ) {
        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxSize()) {
            Text(text, fontSize = 14.sp)
        }
    }
}

@Composable
fun MenuApp(
    onOpenServer: (path: Path) -> Unit = {},
    onOpenClient: () -> Unit = {},
    desktopFileManager: DesktopFileManager,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        var showPopup by remember { mutableStateOf(false) }

        val recentServers by remember { mutableStateOf(desktopFileManager.getRecentServers()) }

        Column(
            Modifier.padding(8.dp)
        ) {
            if (showPopup) {
                var name by remember { mutableStateOf("example") }
                Text("Server folder name")

                TextField(name, onValueChange = {
                    name = it
                })

                val path = Path(desktopFileManager.paths.defaultServerDirectory, name.trim())
                val pathMeta = SystemFileSystem.metadataOrNull(path)

                val validPath = run {
                    if (name.trim().isEmpty()) {
                        Text("Name is empty", color = MaterialTheme.colorScheme.onError)
                        return@run false
                    }

                    if (pathMeta != null && pathMeta.isDirectory && SystemFileSystem.list(path).isNotEmpty()) {
                        Text("Directory is not empty", color = MaterialTheme.colorScheme.onError)
                    }

                    true
                }

                Row {
                    if (validPath)
                        DefaultMenuButton("Open", modifier = Modifier.weight(0.5f)) {
                            SystemFileSystem.createDirectories(path)
                            onOpenServer(path)
                        }
                    Spacer(Modifier.width(4.dp))
                    DefaultMenuButton("Cancel", modifier = Modifier.weight(0.5f)) {
                        showPopup = false
                    }
                }
            } else {
                DefaultMenuButton("Open Client", onClick = onOpenClient)

                Spacer(Modifier.height(4.dp))

                DefaultMenuButton("New Server") {
                    showPopup = true
                }

                Spacer(Modifier.height(4.dp))

                DefaultMenuButton("Open Server Directory", onClick = {
                    val chooser = JFileChooser()
                    chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    chooser.currentDirectory = File(desktopFileManager.paths.defaultServerDirectory.toString())

                    val dialog = chooser.showOpenDialog(null)
                    if (dialog == JFileChooser.APPROVE_OPTION) {
                        onOpenServer(Path(chooser.selectedFile.path))
                    }
                })

                Spacer(Modifier.height(4.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth().height(32.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }

                Box(Modifier.fillMaxWidth()) {
                    LazyColumn {
                        itemsIndexed(recentServers) { i, v ->
                            MenuButton(
                                onClick = {
                                    onOpenServer(v)
                                },
                                contentPadding = PaddingValues(8.dp, 0.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Folder,
                                    contentDescription = null,
                                    modifier = Modifier.padding(4.dp).fillMaxHeight()
                                )
                                Column {
                                    Text("Server $i", fontSize = 14.sp)
                                    Text(v.toString(), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}