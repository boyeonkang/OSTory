package com.example.ostory.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
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
import com.example.ostory.presentation.calendar.CalendarHomeScreen
import com.example.ostory.presentation.detail.WorkDetailScreen
import com.example.ostory.presentation.preference.PreferenceScreen
import com.example.ostory.presentation.review.ReviewDetailScreen
import com.example.ostory.presentation.review.ReviewWriteScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ostory.presentation.search.SearchScreenRoute
import com.example.ostory.presentation.search.SearchViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Calendar : Screen("calendar", "캘린더", Icons.Default.CalendarMonth)
    object Search : Screen("search?selectedDate={selectedDate}", "검색", Icons.Default.Search) {
        fun createRoute(selectedDate: String? = null) = 
            if (selectedDate != null) "search?selectedDate=$selectedDate" else "search"
    }
    object Preference : Screen("preference", "취향", Icons.Default.Favorite)
    
    object WorkDetail : Screen("detail/{workId}/{type}?selectedDate={selectedDate}", "작품 상세") {
        fun createRoute(workId: Int, type: String, selectedDate: String? = null) = 
            if (selectedDate != null) "detail/$workId/$type?selectedDate=$selectedDate" else "detail/$workId/$type"
    }
    object ReviewWrite : Screen("reviewWrite/{workId}/{type}?selectedDate={selectedDate}&reviewId={reviewId}", "감상 기록 작성") {
        fun createRoute(workId: Int, type: String, selectedDate: String? = null, reviewId: Int? = null) = 
            buildString {
                append("reviewWrite/$workId/$type")
                val params = mutableListOf<String>()
                if (selectedDate != null) params.add("selectedDate=$selectedDate")
                if (reviewId != null) params.add("reviewId=$reviewId")
                if (params.isNotEmpty()) {
                    append("?")
                    append(params.joinToString("&"))
                }
            }
    }
    object ReviewDetail : Screen("reviewDetail/{recordId}", "감상 기록 상세") {
        fun createRoute(recordId: Int) = "reviewDetail/$recordId"
    }
}

@Composable
fun OSToryApp() {
    val navController = rememberNavController()
    val tabItems = listOf(Screen.Calendar, Screen.Search, Screen.Preference)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Scaffold(
            containerColor = Color.White,
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
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            composable(Screen.Calendar.route) {
                CalendarHomeScreen(
                    onNavigateToSearch = { selectedDate ->
                        navController.navigate(Screen.Search.createRoute(selectedDate))
                    },
                    onNavigateToReviewDetail = { recordId ->
                        navController.navigate(Screen.ReviewDetail.createRoute(recordId))
                    }
                )
            }
            composable(
                route = Screen.Search.route,
                arguments = listOf(
                    navArgument("selectedDate") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val rawDate = backStackEntry.arguments?.getString("selectedDate")
                val selectedDate = if (rawDate == "{selectedDate}" || rawDate.isNullOrBlank()) null else rawDate
                val searchViewModel: SearchViewModel = viewModel()
                SearchScreenRoute(
                    selectedDate = selectedDate,
                    onNavigateToDetail = { workId, type, date ->
                        navController.navigate(Screen.WorkDetail.createRoute(workId, type, date))
                    },
                    onCloseClick = {
                        navController.navigate(Screen.Calendar.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    viewModel = searchViewModel
                )
            }
            composable(
                route = Screen.WorkDetail.route,
                arguments = listOf(
                    navArgument("workId") { type = NavType.IntType },
                    navArgument("type") { type = NavType.StringType },
                    navArgument("selectedDate") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val workId = backStackEntry.arguments?.getInt("workId") ?: 0
                val type = backStackEntry.arguments?.getString("type") ?: "MOVIE"
                val rawDate = backStackEntry.arguments?.getString("selectedDate")
                val selectedDate = if (rawDate == "{selectedDate}" || rawDate.isNullOrBlank()) null else rawDate
                WorkDetailScreen(
                    workId = workId,
                    workType = type,
                    selectedDate = selectedDate,
                    onNavigateToReviewWrite = { id, wType, date ->
                        val safeDate = if (date == "{selectedDate}" || date.isNullOrBlank()) null else date
                        navController.navigate(Screen.ReviewWrite.createRoute(id, wType, safeDate))
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
                    navArgument("type") { type = NavType.StringType },
                    navArgument("selectedDate") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("reviewId") {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { backStackEntry ->
                val workId = backStackEntry.arguments?.getInt("workId") ?: 0
                val type = backStackEntry.arguments?.getString("type") ?: "MOVIE"
                val rawDate = backStackEntry.arguments?.getString("selectedDate")
                val selectedDate = if (rawDate == "{selectedDate}" || rawDate.isNullOrBlank()) null else rawDate
                val reviewId = backStackEntry.arguments?.getInt("reviewId") ?: 0
                val safeReviewId = if (reviewId <= 0) null else reviewId
                ReviewWriteScreen(
                    workId = workId,
                    workType = type,
                    selectedDate = selectedDate,
                    reviewId = safeReviewId,
                    onNavigateToReviewSaved = {
                        if (safeReviewId != null) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(Screen.Calendar.route) {
                                popUpTo(Screen.Calendar.route) { inclusive = false }
                            }
                        }
                    },
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
                val reviewDetailViewModel: com.example.ostory.presentation.review.ReviewDetailViewModel = viewModel()
                ReviewDetailScreen(
                    recordId = recordId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToReviewWrite = { workId, type, rId ->
                        navController.navigate(Screen.ReviewWrite.createRoute(workId, type, null, rId))
                    },
                    viewModel = reviewDetailViewModel
                )
            }
            composable(Screen.Preference.route) {
                PreferenceScreen()
            }
            }
        }
    }
}
