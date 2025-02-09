package com.segunfrancis.redditclient.ui.app_navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.segunfrancis.redditclient.ui.theme.RedditClientTheme

@Composable
fun RedditClientBottomNav(navController: NavHostController) {
    val navBackStack by navController.currentBackStackEntryAsState()
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        bottomNavItems.forEach { navItem ->
            NavigationBarItem(
                selected = navBackStack?.destination?.hierarchy?.any { it.hasRoute(navItem.route::class) } == true,
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = false
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (navBackStack?.destination?.hierarchy?.any {
                                it.hasRoute(
                                    navItem.route::class
                                )
                            } == true) navItem.iconSelected else navItem.iconUnselected,
                        contentDescription = navItem.label
                    )
                },
                label = {
                    Text(
                        text = navItem.label,
                        fontWeight = if (navBackStack?.destination?.hierarchy?.any {
                                it.hasRoute(
                                    navItem.route::class
                                )
                            } == true) FontWeight.SemiBold else FontWeight.Normal)
                }
            )
        }
    }
}

@Preview
@Composable
fun RedditClientBottomNavPreview() {
    RedditClientTheme {
        RedditClientBottomNav(
            navController = rememberNavController()
        )
    }
}

data class BottomNavItem(
    val label: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector,
    val route: NavDestinations
)

val bottomNavItems = listOf(
    BottomNavItem(
        label = "Home",
        iconSelected = Icons.Filled.Home,
        iconUnselected = Icons.Outlined.Home,
        route = NavDestinations.Home
    ),
    BottomNavItem(
        label = "Search",
        iconSelected = Icons.Filled.Search,
        iconUnselected = Icons.Outlined.Search,
        route = NavDestinations.Search
    )
)
