package com.segunfrancis.redditclient.ui.details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.BasicRichText
import com.halilibo.richtext.ui.RichTextStyle
import com.segunfrancis.redditclient.AppConstants.COLLAPSED_TOP_BAR_HEIGHT
import com.segunfrancis.redditclient.AppConstants.EXPANDED_TOP_BAR_HEIGHT
import com.segunfrancis.redditclient.R
import com.segunfrancis.redditclient.data.remote.Post
import com.segunfrancis.redditclient.data.remote.SubredditChildren
import com.segunfrancis.redditclient.ui.components.CollapsedTopBar
import com.segunfrancis.redditclient.ui.components.ExpandedTopBar
import com.segunfrancis.redditclient.ui.theme.RedditClientTheme
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DetailsScreen(
    modifier: Modifier,
    subreddit: String,
    onNavigateUp: () -> Unit,
    onShowMessage: (String?) -> Unit
) {
    val viewModel = koinViewModel<DetailsViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    var shouldShowDeleteDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.action.collect {
            when (it) {
                DetailsActions.NavigateUp -> {
                    onNavigateUp()
                }

                is DetailsActions.ShowMessage -> {
                    onShowMessage(it.message)
                }
            }
        }
    }
    DetailsContent(modifier, subreddit, uiState, onDeleteClick = {
        shouldShowDeleteDialog = !shouldShowDeleteDialog
    })

    if (shouldShowDeleteDialog) {
        AlertDialog(onDismissRequest = { shouldShowDeleteDialog = false },
            confirmButton = {
                ElevatedButton(
                    onClick = {
                        viewModel.deleteSubreddit()
                        shouldShowDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { shouldShowDeleteDialog = false }) {
                    Text(text = "Dismiss")
                }
            },
            title = {
                Text(text = buildAnnotatedString {
                    append("Do you want to delete ")
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(subreddit)
                }, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.bodyMedium)
            })
    }
}

@Composable
fun DetailsContent(
    modifier: Modifier,
    subreddit: String,
    state: DetailsUiState,
    onDeleteClick: () -> Unit
) {
    val listState = rememberLazyListState()
    val overlapHeightPx = with(LocalDensity.current) {
        EXPANDED_TOP_BAR_HEIGHT.toPx() - COLLAPSED_TOP_BAR_HEIGHT.toPx()
    }
    val isCollapsed: Boolean by remember {
        derivedStateOf {
            val isFirstItemHidden =
                listState.firstVisibleItemScrollOffset > overlapHeightPx
            isFirstItemHidden || listState.firstVisibleItemIndex > 0
        }
    }
    Box(modifier = modifier) {
        CollapsedTopBar(
            modifier = Modifier.zIndex(2f),
            isCollapsed = isCollapsed,
            text = subreddit,
            action = {
                Icon(
                    imageVector = Icons.TwoTone.Delete,
                    contentDescription = "Delete subreddit",
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clickable {
                            onDeleteClick()
                        })
            }
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 12.dp),
            state = listState
        ) {
            item {
                ExpandedTopBar(
                    text = subreddit,
                    image = state.subredditItem?.bannerBgImage.orEmpty(),
                    subText = state.subredditItem?.let { subItem ->
                        NumberFormat.getNumberInstance(Locale.getDefault()).format(subItem.subscribersCount)
                    }
                )
            }
            if (state.loading) {
                item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            }
            items(state.posts) { children ->
                PostItem(
                    title = children.post.title,
                    selfText = children.post.selfText,
                    ups = children.post.ups,
                    downs = children.post.downs
                )
            }
        }
    }
}

@Preview
@Composable
fun DetailsPreview() {
    RedditClientTheme {
        DetailsContent(
            modifier = Modifier,
            subreddit = "r/governmentAgencies",
            state = DetailsUiState(
                posts = listOf(
                    SubredditChildren(
                        kind = "",
                        post = Post(
                            "r/governmentAgencies",
                            selfText = "* This is not just about one woman, it's about the entire system that refuses to acknowledge the value of its citizens.\n " +
                                    "* The corruption in Nigeria has reached a point where it's no longer just about greedy politicians in their mansions, it's about the suffering of those who built this nation, " +
                                    "our elderly, our workers, our future.\n\nPensions are withheld. Salaries go unpaid. People who devoted their entire lives to this country now struggle to survive, left to die " +
                                    "in silence while the politicians who’ve bled the country dry continue to thrive.\\n\\nAnd the worst part? We sit here, scrolling on our phones, ranting about it, doing nothing. " +
                                    "How much longer will we allow this cycle of greed, cruelty, and neglect to destroy us? How many more must suffer before we stop pretending everything is fine?\\n\\nNigeria is " +
                                    "rotting from the inside out, and it's time we face the reality. We need a change. *Not just words*, not just promises, but real, tangible action. Our people deserve better. " +
                                    "The time for waiting is over. The time for real change is now. \uD83D\uDC94\n\n",
                            title = "Nigeria's' Rotten System: A National Shame We Can’t Ignore Any Longer",
                            ups = 90,
                            downs = 12
                        )
                    )
                )
            ),
            onDeleteClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostItem(title: String, selfText: String, ups: Int, downs: Int) {
    var showExtra by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        onClick = {
            showExtra = !showExtra
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(1F)
                    .padding(end = 4.dp)
            )
            AnimatedContent(
                targetState = showExtra,
                label = "Arrow Animation",
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = expandVertically(animationSpec = tween(500)) + fadeIn(),
                        initialContentExit = shrinkVertically(animationSpec = tween(500)) + fadeOut(),
                        sizeTransform = SizeTransform(clip = false)
                    )
                }
            ) {
                if (it) {
                    Image(
                        imageVector = Icons.Rounded.KeyboardArrowUp,
                        contentDescription = "Expand",
                        modifier = Modifier.wrapContentSize()
                    )
                } else {
                    Image(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        }
        AnimatedContent(showExtra, label = "extra") {
            if (it) {
                BasicRichText(
                    modifier = Modifier.padding(12.dp),
                    style = RichTextStyle()
                ) {
                    Markdown(content = selfText)
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(12.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(4.dp))
            Image(
                painter = painterResource(R.drawable.ic_ups),
                contentDescription = "upvote icon",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = ups.toString(), style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.width(6.dp))
            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .height(20.dp)
                    .background(color = MaterialTheme.colorScheme.outlineVariant)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Image(
                painter = painterResource(R.drawable.ic_downs),
                contentDescription = "down-vote icon",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = downs.toString(), style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Preview
@Composable
fun PostItemPreview() {
    PostItem(
        title = "Nigeria's' Rotten System: A National Shame We Can’t Ignore Any Longer",
        selfText = "* This is not just about one woman, it's about the entire system that refuses to acknowledge the value of its citizens.\n " +
                "* The corruption in Nigeria has reached a point where it's no longer just about greedy politicians in their mansions, it's " +
                "about the suffering of those who built this nation, our elderly, our workers, our future.\n\nPensions are withheld. Salaries " +
                "go unpaid. People who devoted their entire lives to this country now struggle to survive, left to die in silence while the " +
                "politicians who’ve bled the country dry continue to thrive.\n\nAnd the worst part? We sit here, scrolling on our phones, " +
                "ranting about it, doing nothing. How much longer will we allow this cycle of greed, cruelty, and neglect to destroy us? How " +
                "many more must suffer before we stop pretending everything is fine?\n\nNigeria is rotting from the inside out, and it's " +
                "time we face the reality. We need a change. *Not just words*, not just promises, but real, tangible action. Our people " +
                "deserve better. The time for waiting is over. The time for real change is now. \uD83D\uDC94\n\n",
        ups = 29,
        downs = 2
    )
}
