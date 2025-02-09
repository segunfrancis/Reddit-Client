package com.segunfrancis.redditclient.ui.home

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.segunfrancis.redditclient.R
import com.segunfrancis.redditclient.ui.search.RedditItem
import com.segunfrancis.redditclient.ui.theme.RedditClientTheme
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat

@Composable
fun HomeScreen(modifier: Modifier, onItemClick: (RedditItem) -> Unit) {
    val viewModel = koinViewModel<HomeViewModel>()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.errorMessageFlow.collect {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
    val subreddits by viewModel.subredditsState.collectAsState()

    HomeContent(modifier = modifier, subreddits = subreddits, onItemClick = {
        onItemClick(it)
    })
}

@Composable
fun HomeContent(
    modifier: Modifier,
    subreddits: List<RedditItem>,
    onItemClick: (RedditItem) -> Unit
) {
    if (subreddits.isNotEmpty()) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(subreddits) { redditItem ->
                ListItem(
                    modifier = Modifier
                        .border(
                            width = Dp.Hairline,
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                        .clickable { onItemClick(redditItem) },
                    headlineContent = { Text(text = redditItem.subreddit, maxLines = 1) },
                    leadingContent = {
                        AsyncImage(
                            model = redditItem.icon,
                            placeholder = painterResource(R.drawable.ic_reddit),
                            error = painterResource(R.drawable.ic_reddit),
                            contentDescription = null,
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(50))
                        )
                    },
                    supportingContent = {
                        Text(
                            text = redditItem.description,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    overlineContent = {
                        if (redditItem.subscribersCount > 0) {
                            Text(
                                text = buildAnnotatedString {
                                    append("Subscribers count: ")
                                    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                                    append(
                                        NumberFormat.getNumberInstance()
                                            .format(redditItem.subscribersCount)
                                    )
                                },
                                modifier = Modifier.wrapContentWidth()
                            )
                        }
                    })

            }
        }
    } else {
        NoContentScreen(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun NoContentScreen(
    modifier: Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "You have not saved any subreddits yet",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Click on the Search menu \uD83D\uDD0E to search for and save subreddits",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Preview
@Composable
fun NoContentPreview() {
    NoContentScreen(modifier = Modifier)
}

@Preview
@Composable
fun HomeContentPreview() {
    RedditClientTheme {
        HomeContent(
            modifier = Modifier, subreddits = listOf(
                RedditItem(
                    subreddit = "r/mAndroidDev",
                    displayName = "mAndroidDev",
                    icon = "",
                    description = "\uD83D\uDE4F Praise \uD83D\uDE4F be \uD83D\uDE4F to \uD83D\uDE4F Wharton \uD83D\uDE4F",
                    subscribersCount = 1620,
                    bannerBgImage = ""
                )
            )
        ) {}
    }
}
