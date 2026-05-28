package com.example.ostory.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ostory.presentation.calendar.CalendarScreen
import com.example.ostory.presentation.detail.WorkDetailScreen
import com.example.ostory.presentation.preference.PreferenceScreen
import com.example.ostory.presentation.review.ReviewDetailScreen
import com.example.ostory.presentation.review.ReviewWriteScreen
import com.example.ostory.presentation.search.SearchScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Calendar : Screen("calendar", "캘린더", Icons.Default.CalendarMonth)
    object Search : Screen("search", "검색", Icons.Default.Search)
    object Preference : Screen("preference", "취향", Icons.Default.Favorite)
    
    object WorkDetail : Screen("detail/{workId}/{type}", "작품 상세") {
        fun createRoute(workId: Int, type: String) = "detail/$workId/$type"
    }
    object ReviewWrite : Screen("reviewWrite/{workId}/{type}", "감상 기록 작성") {
        fun createRoute(workId: Int, type: String) = "reviewWrite/$workId/$type"
    }
    object ReviewDetail : Screen("reviewDetail/{recordId}", "감상 기록 상세") {
        fun createRoute(recordId: Int) = "reviewDetail/$recordId"
    }
}

@Composable
fun OSToryApp() {
    val navController = rememberNavController()
    val tabItems = listOf(Screen.Calendar, Screen.Search, Screen.Preference)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            val showBottomBar = currentDestination?.route in tabItems.map { it.route }
            if (showBottomBar) {
                NavigationBar {
                    tabItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Calendar.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Calendar.route) {
                CalendarScreen(
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    },
                    onNavigateToReviewDetail = { recordId ->
                        navController.navigate(Screen.ReviewDetail.createRoute(recordId))
                    }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onNavigateToDetail = { workId, type ->
                        navController.navigate(Screen.WorkDetail.createRoute(workId, type))
                    }
                )
            }
            composable(
                route = Screen.WorkDetail.route,
                arguments = listOf(
                    navArgument("workId") { type = NavType.IntType },
                    navArgument("type") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val workId = backStackEntry.arguments?.getInt("workId") ?: 0
                val type = backStackEntry.arguments?.getString("type") ?: "MOVIE"
                WorkDetailScreen(
                    workId = workId,
                    workType = type,
                    onNavigateToReviewWrite = { id, wType ->
                        navController.navigate(Screen.ReviewWrite.createRoute(id, wType))
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = Screen.ReviewWrite.route,
                arguments = listOf(
                    navArgument("workId") { type = NavType.IntType },
                    navArgument("type") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val workId = backStackEntry.arguments?.getInt("workId") ?: 0
                val type = backStackEntry.arguments?.getString("type") ?: "MOVIE"
                ReviewWriteScreen(
                    workId = workId,
                    workType = type,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = Screen.ReviewDetail.route,
                arguments = listOf(
                    navArgument("recordId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val recordId = backStackEntry.arguments?.getInt("recordId") ?: 0
                ReviewDetailScreen(
                    recordId = recordId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Preference.route) {
                PreferenceScreen()
            }
        }
    }
}
