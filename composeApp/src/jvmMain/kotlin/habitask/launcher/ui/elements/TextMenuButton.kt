package habitask.launcher.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun TextMenuButton(
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