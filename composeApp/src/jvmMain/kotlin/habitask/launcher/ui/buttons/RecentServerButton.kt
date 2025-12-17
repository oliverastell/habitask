package habitask.launcher.ui.buttons

import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitask.common.ui.modifyIf
import habitask.launcher.ui.elements.MenuButton
import habitask.resources.Res
import habitask.resources.folder_filled
import habitask.server.data.filemanagers.DesktopFileManager
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.openFileWithDefaultApplication
import kotlinx.io.files.Path
import org.jetbrains.compose.resources.painterResource
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun RecentServerButton(
    fm: DesktopFileManager,
    path: Path,
    onOpenServer: (Path) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    MenuButton(
        onClick = {
            onOpenServer(path)
        },
        contentPadding = PaddingValues(8.dp, 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .hoverable(interactionSource)
    ) {
        Icon(
            painterResource(Res.drawable.folder_filled),
            contentDescription = null,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxHeight()
                .clickable(onClick = {
                    FileKit.openFileWithDefaultApplication(PlatformFile(path))
                })
        )

        var pathString = path.toString()
        val prefix = fm.paths.defaultServerDirectory.toString()
        if (path.toString().startsWith(prefix)) {
            pathString = "..." + pathString.substring(prefix.length)
        }

        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxSize()) {
            Text(
                pathString,
                softWrap = false,
                fontSize = 10.sp,
                modifier = Modifier
                    .modifyIf(isHovered) { basicMarquee(
                        animationMode = MarqueeAnimationMode.Immediately,
                        velocity = 50.dp
                    ) }
            )
        }
    }
}