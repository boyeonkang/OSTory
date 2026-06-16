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
import com.example.ostory.presentation.calendar.CalendarScreen
import com.example.ostory.presentation.detail.WorkDetailScreen
import com.example.ostory.presentation.preference.PreferenceScreen
import com.example.ostory.presentation.review.ReviewDetailScreen
import com.example.ostory.presentation.review.ReviewWriteScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ostory.presentation.search.SearchScreenRoute
import com.example.ostory.presentation.search.SearchViewModel
import com.example.ostory.presentation.onboarding.OnboardingScreen
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Calendar : Screen("calendar", "캘린더", Icons.Default.CalendarMonth)
    object Search : Screen("search", "검색", Icons.Default.Search) {
        fun createRoute(selectedDate: String? = null) = 
            if (selectedDate != null) "search?selectedDate=$selectedDate" else "search"
    }
    object Preference : Screen("preference", "취향", Icons.Default.Favorite)
    
    object WorkDetail : Screen("detail/{workId}/{workType}", "작품 상세") {
        fun createRoute(workId: Int, workType: String) = "detail/$workId/$workType"
    }
    object ReviewWrite : Screen("reviewWrite/{workId}/{workType}?watchedDate={watchedDate}&reviewId={reviewId}", "감상 기록 작성") {
        fun createRoute(workId: Int, workType: String, watchedDate: String? = null, reviewId: Int? = null) = 
            buildString {
                append("reviewWrite/$workId/$workType")
                val params = mutableListOf<String>()
                if (watchedDate != null) params.add("watchedDate=$watchedDate")
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
    object Onboarding : Screen("onboarding", "온보딩")
}

@Composable
fun OSToryApp() {
    val navController = rememberNavController()
    val tabItems = listOf(Screen.Calendar, Screen.Search, Screen.Preference)

    val context = androidx.compose.ui.platform.LocalContext.current.applicationContext
    val prefs = remember { context.getSharedPreferences("ostory_onboarding", android.content.Context.MODE_PRIVATE) }
    val isFirstRun = remember { prefs.getBoolean("is_first_run", true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Scaffold(
            containerColor = Color.White,
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                val currentRoute = currentDestination?.route
                val showBottomBar = currentRoute != null && (
                    currentRoute == "calendar" || 
                    currentRoute == "search" || 
                    currentRoute.startsWith("search?") || 
                    currentRoute == "preference"
                )
                if (showBottomBar) {
                    NavigationBar {
                        tabItems.forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                                label = { Text(screen.title) },
                                selected = when (screen) {
                                    is Screen.Calendar -> currentRoute == "calendar"
                                    is Screen.Search -> currentRoute == "search" || currentRoute?.startsWith("search?") == true
                                    is Screen.Preference -> currentRoute == "preference"
                                    else -> false
                                },
                                onClick = {
                                    if (screen == Screen.Calendar) {
                                        navController.navigate("calendar") {
                                            launchSingleTop = true
                                        }
                                    } else {
                                        navController.navigate(screen.route) {
                                            popUpTo(Screen.Calendar.route) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
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
                startDestination = if (isFirstRun) Screen.Onboarding.route else Screen.Calendar.route,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding)
            ) {
                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        onStartClick = {
                            prefs.edit().putBoolean("is_first_run", false).apply()
                            navController.navigate(Screen.Calendar.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Screen.Calendar.route) {
                    CalendarScreen(
                        onNavigateToSearch = { selectedDate ->
                            navController.navigate(Screen.Search.createRoute(selectedDate))
                        },
                        onNavigateToReviewDetail = { recordId ->
                            navController.navigate(Screen.ReviewDetail.createRoute(recordId))
                        }
                    )
                }
                composable("search") {
                    val searchViewModel: SearchViewModel = viewModel()
                    SearchScreenRoute(
                        selectedDate = null,
                        onNavigateToDetail = { workId, type, _ ->
                            navController.navigate(Screen.WorkDetail.createRoute(workId, type))
                        },
                        onCloseClick = {
                            navController.navigate(Screen.Calendar.route) {
                                popUpTo(Screen.Calendar.route) {
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
                    route = "search?selectedDate={selectedDate}",
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
                            if (date != null) {
                                navController.navigate(Screen.ReviewWrite.createRoute(workId, type, date))
                            } else {
                                navController.navigate(Screen.WorkDetail.createRoute(workId, type))
                            }
                        },
                        onCloseClick = {
                            navController.navigate(Screen.Calendar.route) {
                                popUpTo(Screen.Calendar.route) {
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
                        navArgument("workType") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val workId = backStackEntry.arguments?.getInt("workId") ?: 0
                    val workType = backStackEntry.arguments?.getString("workType") ?: "MOVIE"
                    WorkDetailScreen(
                        workId = workId,
                        workType = workType,
                        selectedDate = null,
                        onNavigateToReviewWrite = { id, type, _ ->
                            navController.navigate(Screen.ReviewWrite.createRoute(id, type, null))
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
                        navArgument("workType") { type = NavType.StringType },
                        navArgument("watchedDate") {
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
                    val workType = backStackEntry.arguments?.getString("workType") ?: "MOVIE"
                    val rawDate = backStackEntry.arguments?.getString("watchedDate")
                    val watchedDate = if (rawDate == "{watchedDate}" || rawDate.isNullOrBlank()) null else rawDate
                    val reviewId = backStackEntry.arguments?.getInt("reviewId") ?: 0
                    val safeReviewId = if (reviewId <= 0) null else reviewId
                    ReviewWriteScreen(
                        workId = workId,
                        workType = workType,
                        selectedDate = watchedDate,
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
