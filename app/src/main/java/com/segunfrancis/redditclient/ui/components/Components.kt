package com.segunfrancis.redditclient.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.segunfrancis.redditclient.AppConstants.COLLAPSED_TOP_BAR_HEIGHT
import com.segunfrancis.redditclient.AppConstants.EXPANDED_TOP_BAR_HEIGHT
import com.segunfrancis.redditclient.R
import com.segunfrancis.redditclient.ui.theme.onSurfaceLight

@Composable
fun CollapsedTopBar(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean,
    text: String,
    action: @Composable RowScope.() -> Unit = {}
) {
    val color: Color by animateColorAsState(
        if (isCollapsed) MaterialTheme.colorScheme.background else Color.Transparent, label = ""
    )
    Box(
        modifier = modifier
            .background(color)
            .fillMaxWidth()
            .height(COLLAPSED_TOP_BAR_HEIGHT)
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        AnimatedVisibility(visible = isCollapsed) {
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
            action()
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun CollapsedTopBarPreview() {
    CollapsedTopBar(isCollapsed = true, text = "Reddit Client")
}

@Composable
fun ExpandedTopBar(text: String, subText: String? = null, image: Any) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.inversePrimary)
            .fillMaxWidth()
            .height(EXPANDED_TOP_BAR_HEIGHT),
        contentAlignment = Alignment.BottomStart
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(EXPANDED_TOP_BAR_HEIGHT)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            onSurfaceLight.copy(alpha = 0F),
                            onSurfaceLight
                        )
                    )
                )
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall,
            )
            subText?.let {
                Text(
                    text = buildAnnotatedString {
                        append("Subscribers count: ")
                        pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                        append(it)
                    }, color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ExpandedTopBarPreview() {
    ExpandedTopBar(
        text = "r/subreddit",
        image = R.drawable.ic_reddit,
        subText = "1,200"
    )
}
