package com.segunfrancis.redditclient.ui.search

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.segunfrancis.redditclient.R
import com.segunfrancis.redditclient.ui.theme.RedditClientTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(modifier: Modifier) {
    val viewModel = koinViewModel<SearchViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    SearchContent(modifier = modifier, state = uiState, onSearchTextChange = {
        viewModel.setSearchText(it)
    }, onKeyDone = {
        viewModel.search()
    }, onItemClick = {
        viewModel.updateSelectedItem(it)
        viewModel.updateDialogState(state = true)
    })

    if (uiState.dialogState) {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = { viewModel.updateDialogState(state = false) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateDialogState(state = false)
                        viewModel.saveSubReddit()
                    },
                    content = { Text(text = "Save") }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.updateDialogState(state = false) },
                    content = { Text(text = "Dismiss") }
                )
            },
            title = {
                Text(text = buildAnnotatedString {
                    append("Save ")
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(uiState.selectedItem?.subreddit)
                })
            },
            text = {
                Text(text = buildAnnotatedString {
                    append("Would you like to save ")
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(uiState.selectedItem?.subreddit)
                    pushStyle(SpanStyle(fontWeight = FontWeight.Normal))
                    append(" to your favourites?")
                })
            }
        )
    }
}

@Composable
fun SearchContent(
    modifier: Modifier,
    state: SearchUiState,
    onSearchTextChange: (String) -> Unit,
    onKeyDone: () -> Unit,
    onItemClick: (RedditItem) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        OutlinedTextField(
            value = state.searchText,
            onValueChange = { onSearchTextChange(it) },
            label = { Text(text = "Search subreddit") },
            prefix = { Text(text = "r/") },
            singleLine = true,
            keyboardActions = KeyboardActions(onSearch = { onKeyDone() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 8.dp, start = 12.dp, end = 12.dp)
        )

        when (state.content) {
            is SearchContent.Content -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.content.redditItems) { redditItem ->
                        ListItem(
                            modifier = Modifier
                                .border(
                                    width = Dp.Hairline,
                                    color = MaterialTheme.colorScheme.inverseSurface
                                )
                                .clickable { onItemClick(redditItem) },
                            headlineContent = { Text(text = redditItem.displayName, maxLines = 1) },
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
                                Text(text = redditItem.subreddit, maxLines = 1)
                            })
                    }
                }
            }

            SearchContent.Default -> {

            }

            is SearchContent.Error -> {
                NoContentScreen(
                    modifier = Modifier.weight(1F),
                    error = state.content.errorMessage,
                    onRetryClick = {
                        onKeyDone()
                    })
            }

            SearchContent.NoContent -> {
                NoContentScreen(modifier = Modifier.weight(1F), subreddit = state.searchText)
            }
        }
    }
}

@Composable
fun NoContentScreen(
    modifier: Modifier,
    subreddit: String? = null,
    error: String? = null,
    onRetryClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        subreddit?.let {
            Text(
                text = buildAnnotatedString {
                    append("No subreddit with the name ")
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(subreddit)
                },
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Refine your search and try again",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        error?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.error),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        error?.let {
            ElevatedButton(onClick = { onRetryClick() }, content = {
                Text("Retry")
            })
        }
    }
}

@Preview
@Composable
fun NoContentScreenPreview() {
    RedditClientTheme {
        NoContentScreen(modifier = Modifier, subreddit = "Croatia")
    }
}

@Preview
@Composable
fun SearchContentPreview() {
    RedditClientTheme {
        SearchContent(
            modifier = Modifier,
            state = SearchUiState(
                isLoading = true, content = SearchContent.Content(
                    listOf(
                        RedditItem(
                            subreddit = "r/mAndroidDev",
                            displayName = "mAndroidDev",
                            icon = "",
                            description = "\uD83D\uDE4F Praise \uD83D\uDE4F be \uD83D\uDE4F to \uD83D\uDE4F Wharton \uD83D\uDE4F",
                            subscribersCount = 1620,
                            bannerBgImage = ""
                        )
                    )
                )
            ),
            onSearchTextChange = {},
            onKeyDone = {},
            onItemClick = {}
        )
    }
}
