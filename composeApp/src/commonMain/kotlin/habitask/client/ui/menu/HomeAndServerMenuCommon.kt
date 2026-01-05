package habitask.client.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import habitask.common.ui.Section
import kotlinx.coroutines.launch

@Composable
fun HomeAndServerMenuCommon(
    rowContent: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Column(modifier) {
        Box(
            content = content,
            modifier = Modifier.weight(1f)
        )
        Row(horizontalArrangement = Arrangement.SpaceEvenly, content = rowContent, modifier = Modifier.fillMaxWidth())
    }
}