package com.oliverastell.habitask.menuapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.TextAutoSizeDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oliverastell.habitask.DesktopDirectory
import kotlinx.io.files.Path

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MenuOption(
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
fun MenuApp(
    onOpenServerFolder: (path: Path) -> Unit = {},
    onOpenServer: () -> Unit = {},
    onOpenClient: () -> Unit = {},
    workingDirectory: DesktopDirectory,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        Column(
            Modifier.padding(8.dp)
        ) {
            MenuOption(
                onClick = onOpenClient,
                contentPadding = PaddingValues(8.dp, 0.dp),
                modifier = Modifier.fillMaxWidth().height(32.dp)
            ) {
                Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxSize()) {
                    Text("Open Client", fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(4.dp))

            MenuOption(
                onClick = onOpenServer,
                contentPadding = PaddingValues(8.dp, 0.dp),
                modifier = Modifier.fillMaxWidth().height(44.dp)
            ) {
                Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxSize()) {
                    Column {
                        Text("Open Server Directory", fontSize = 14.sp)
                        Text("Open an empty directory to create a new server", fontSize = 10.sp)
                    }
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().height(32.dp)
            ) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }

            Box(Modifier.fillMaxWidth()) {
                LazyColumn {
                    itemsIndexed(workingDirectory.getRecentServers()) { i, v ->

                        MenuOption(
                            onClick = {
                                onOpenServerFolder(v)
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