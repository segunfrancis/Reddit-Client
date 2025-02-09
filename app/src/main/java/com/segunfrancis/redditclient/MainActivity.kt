package com.segunfrancis.redditclient

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.segunfrancis.redditclient.ui.app_navigation.NavDestinations
import com.segunfrancis.redditclient.ui.app_navigation.RedditClientBottomNav
import com.segunfrancis.redditclient.ui.details.DetailsScreen
import com.segunfrancis.redditclient.ui.home.HomeScreen
import com.segunfrancis.redditclient.ui.search.SearchScreen
import com.segunfrancis.redditclient.ui.theme.RedditClientTheme
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            RedditClientTheme {
                KoinAndroidContext {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
                            val currentBackStack by navController.currentBackStackEntryAsState()
                            if (currentBackStack?.destination?.hierarchy?.any {
                                    it.hasRoute(NavDestinations.Home::class) || it.hasRoute(
                                        NavDestinations.Search::class
                                    )
                                } == true) {
                                RedditClientBottomNav(navController = navController)
                            }
                        }) { paddingValues ->
                            NavHost(
                                navController = navController,
                                startDestination = NavDestinations.Home
                            ) {
                                composable<NavDestinations.Home> {
                                    HomeScreen(
                                        modifier = Modifier.padding(paddingValues),
                                        onItemClick = {
                                            navController.navigate(
                                                NavDestinations.Details(
                                                    subreddit = it.subreddit,
                                                    displayName = it.displayName
                                                )
                                            )
                                        })
                                }
                                composable<NavDestinations.Search> {
                                    SearchScreen(modifier = Modifier.padding(paddingValues))
                                }
                                composable<NavDestinations.Details> { backstackEntry ->
                                    val subreddit =
                                        backstackEntry.toRoute<NavDestinations.Details>().subreddit
                                    DetailsScreen(
                                        modifier = Modifier.padding(paddingValues),
                                        subreddit = subreddit,
                                        onNavigateUp = { navController.navigateUp() },
                                        onShowMessage = {
                                            Toast.makeText(
                                                this@MainActivity,
                                                it,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
