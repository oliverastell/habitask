package habitask.common.windows

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import habitask.resources.Res
import habitask.resources.home
import habitask.resources.home_filled
import habitask.resources.hourglass
import io.github.numq.composedesktopwindowdecoration.decoration.WindowDecoration
import org.jetbrains.compose.resources.painterResource


@Composable
fun ApplicationScope.ClientServerWindow(
    title: String,
    homeButtonPressed: () -> Unit,
    initialWindowSize: DpSize? = null,
    content: @Composable () -> Unit
) = WindowDecoration(
    titleContent = {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clickable { homeButtonPressed() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(Res.drawable.home_filled),
                null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.width(8.dp))

        Title(title)
    },
    initialWindowSize = initialWindowSize
) {
    content()
}