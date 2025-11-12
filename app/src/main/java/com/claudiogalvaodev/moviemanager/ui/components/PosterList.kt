package com.claudiogalvaodev.moviemanager.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PosterList(
    imagesUrl: List<String>
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        maxItemsInEachRow = 3 // simula uma grade
    ) {
        imagesUrl.forEach { imageUrl ->
            AsyncImage(
                modifier = Modifier
                    .width(122.dp)
                    .height(200.dp),
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}