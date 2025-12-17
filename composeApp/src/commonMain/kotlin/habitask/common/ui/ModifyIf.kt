package habitask.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Modifier.modifyIf(condition: Boolean, modification: @Composable Modifier.() -> Modifier): Modifier =
    if (condition)
        this.modification()
    else this