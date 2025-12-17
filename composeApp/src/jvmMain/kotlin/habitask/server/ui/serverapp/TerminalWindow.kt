package habitask.server.ui.serverapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import habitask.common.ui.Section
import habitask.common.ui.modifyIf
import kotlinx.io.files.Path
import org.jetbrains.exposed.sql.Column


@Composable
fun DefaultConsoleTextStyle() = LocalTextStyle.current.copy(
    color = MaterialTheme.colorScheme.onSurface,
    fontFamily = FontFamily.Monospace,
    fontSize = 14.sp,
    lineHeight = 16.sp,
    letterSpacing = (-0.5).sp
)



@Composable
fun ConsoleWindow(
    workingDirectory: Path,
    lines: List<String>,
    modifier: Modifier = Modifier
) {
    Section(
        modifier = modifier
    ) {
        var wrapText by remember { mutableStateOf(false) }

        Column {
            Row {
                Checkbox(wrapText, onCheckedChange = { wrapText = it })
                Text("Wrap text", modifier = Modifier.align(Alignment.CenterVertically))
            }

            SelectionContainer {
                LazyColumn(
                    reverseLayout = true,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .modifyIf(!wrapText) {
                            horizontalScroll(rememberScrollState())
                        }
                        .zIndex(0f),
                ) {
                    items(lines.size) {
                        BasicText(
                            lines[lines.lastIndex - it],
                            style = DefaultConsoleTextStyle(),
                            softWrap = wrapText
                        )
                    }
                }
            }
        }
    }
}