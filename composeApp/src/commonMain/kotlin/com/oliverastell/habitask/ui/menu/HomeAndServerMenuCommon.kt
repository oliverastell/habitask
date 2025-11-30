package com.oliverastell.habitask.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeAndServerMenuCommon(
    columnContent: LazyListScope.() -> Unit,
    rowContent: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        LazyColumn(
            verticalArrangement = Arrangement.Bottom,
            reverseLayout = true,
            modifier = Modifier.weight(1f),
            content = columnContent
        )
        Row(horizontalArrangement = Arrangement.SpaceEvenly, content = rowContent, modifier = Modifier.fillMaxWidth())
    }

}