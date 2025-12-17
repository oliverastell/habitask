package habitask.client.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.time.Duration

@Composable
inline fun UpdatingEffect(
    interval: Duration,
    crossinline block: CoroutineScope.() -> Unit
) {
    LaunchedEffect(Unit) {
        while (true) {
            block()
            delay(interval)
        }
    }
}